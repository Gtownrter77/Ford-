package com.example.data

import com.example.data.local.ComponentFailureRiskEntity
import com.example.data.local.DiagnosticFailureDao
import com.example.data.local.MaintenanceDao
import com.example.data.local.MaintenanceEntity
import com.example.data.local.UpcomingTaskEntity
import com.example.data.local.VehicleProfileEntity
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class DiagnosticHeatmapRepository(
    private val failureDao: DiagnosticFailureDao,
    private val maintenanceDao: MaintenanceDao
) {

    val allFailureRisks: Flow<List<ComponentFailureRiskEntity>> = failureDao.getAllFailureRisks()
    val highPriorityRisks: Flow<List<ComponentFailureRiskEntity>> = failureDao.getHighPriorityRisks()

    fun getRiskForComponent(componentId: String): Flow<ComponentFailureRiskEntity?> {
        return failureDao.getFailureRiskByComponent(componentId)
    }

    /**
     * Evaluates and updates failure risks for all 3D components by combining
     * historical Cologne 4.0L SOHC engineering failure data with active Room DB
     * maintenance logs, overdue upcoming tasks, and current vehicle mileage.
     */
    suspend fun evaluateAndSyncFailureRisks(
        components: List<Component3DModel>,
        maintenanceLogs: List<MaintenanceEntity>,
        upcomingTasks: List<UpcomingTaskEntity>,
        vehicleProfile: VehicleProfileEntity?
    ) {
        val currentMileage = vehicleProfile?.currentMileage ?: 115000
        val now = System.currentTimeMillis()

        val evaluatedEntities = components.map { comp ->
            evaluateSingleComponentRisk(
                component = comp,
                currentMileage = currentMileage,
                now = now,
                maintenanceLogs = maintenanceLogs,
                upcomingTasks = upcomingTasks
            )
        }

        failureDao.insertOrUpdateRisks(evaluatedEntities)
    }

    private fun evaluateSingleComponentRisk(
        component: Component3DModel,
        currentMileage: Int,
        now: Long,
        maintenanceLogs: List<MaintenanceEntity>,
        upcomingTasks: List<UpcomingTaskEntity>
    ): ComponentFailureRiskEntity {
        // Base baseline engineering profile for 2004 Sport Trac
        val baseProfile = getBaselineRiskProfile(component.id, component.name, component.system)

        // 1. Check for upcoming tasks in Room matching this component or its system
        val matchingUpcoming = upcomingTasks.filter { task ->
            task.scheduleItemId.contains(component.id, ignoreCase = true) ||
                    component.id.contains(task.scheduleItemId, ignoreCase = true) ||
                    task.title.contains(component.name, ignoreCase = true) ||
                    task.systemName.equals(component.system.displayName, ignoreCase = true)
        }

        val hasOverdueTask = matchingUpcoming.any { task ->
            task.dueDateMillis < now || (task.targetMileage in 1..currentMileage)
        }
        val hasCriticalUpcoming = matchingUpcoming.any { it.priorityLevel.equals("CRITICAL", ignoreCase = true) }
        val hasHighUpcoming = matchingUpcoming.any { it.priorityLevel.equals("HIGH", ignoreCase = true) }

        // 2. Check for completed maintenance logs in Room
        val matchingLogs = maintenanceLogs.filter { log ->
            log.isCompleted && (
                    log.scheduleItemId.contains(component.id, ignoreCase = true) ||
                            component.id.contains(log.scheduleItemId, ignoreCase = true) ||
                            log.title.contains(component.name, ignoreCase = true) ||
                            log.componentDescription.contains(component.name, ignoreCase = true) ||
                            log.systemName.equals(component.system.displayName, ignoreCase = true)
                    )
        }.sortedByDescending { it.dateLoggedMillis }

        val mostRecentLog = matchingLogs.firstOrNull()
        val milesSinceLastService = if (mostRecentLog != null) {
            (currentMileage - mostRecentLog.mileageAtService).coerceAtLeast(0)
        } else null

        // 3. Dynamic score weighting
        var calculatedScore = baseProfile.baseProbability
        var dataSourceSummary: String
        var isOverdue = false

        val interval = component.replacementIntervalMiles ?: baseProfile.mileageInterval

        if (hasOverdueTask) {
            // Overdue in Room DB -> High escalation
            calculatedScore = (calculatedScore + 0.22f).coerceAtMost(0.98f)
            isOverdue = true
            val overdueTask = matchingUpcoming.first { it.dueDateMillis < now || it.targetMileage <= currentMileage }
            dataSourceSummary = "OVERDUE in Room DB: ${overdueTask.title} (Target: ${overdueTask.targetMileage} mi)"
        } else if (hasCriticalUpcoming) {
            calculatedScore = (calculatedScore + 0.15f).coerceAtMost(0.96f)
            dataSourceSummary = "CRITICAL task pending in Room DB: ${matchingUpcoming.first().title}"
        } else if (hasHighUpcoming) {
            calculatedScore = (calculatedScore + 0.08f).coerceAtMost(0.92f)
            dataSourceSummary = "High priority service scheduled in Room DB"
        } else if (mostRecentLog != null && milesSinceLastService != null) {
            if (milesSinceLastService < (interval * 0.4f)) {
                // Freshly serviced in Room DB -> Risk drops significantly!
                calculatedScore = (calculatedScore * 0.25f).coerceIn(0.10f, 0.28f)
                dataSourceSummary = "Verified healthy in Room DB: serviced at ${mostRecentLog.mileageAtService} mi (${milesSinceLastService} mi ago)"
            } else if (milesSinceLastService < interval) {
                // Normal operational window
                calculatedScore = (calculatedScore * 0.65f).coerceIn(0.25f, 0.58f)
                dataSourceSummary = "Service logged in Room DB: ${mostRecentLog.title} (${milesSinceLastService} mi ago)"
            } else {
                // Exceeded recommended mileage interval
                calculatedScore = (calculatedScore + 0.18f).coerceAtMost(0.92f)
                isOverdue = true
                dataSourceSummary = "Service interval exceeded: ${milesSinceLastService} mi since last Room DB record"
            }
        } else {
            // No Room DB log found for this component
            if (currentMileage > interval) {
                calculatedScore = (calculatedScore + 0.10f).coerceAtMost(0.92f)
                dataSourceSummary = "No service log found in Room DB (Odometer: $currentMileage mi)"
            } else {
                dataSourceSummary = "Baseline historical rate; no custom logs in Room DB"
            }
        }

        // Determine severity categorization
        val severity = when {
            calculatedScore >= 0.80f -> "CRITICAL"
            calculatedScore >= 0.65f -> "HIGH"
            calculatedScore >= 0.45f -> "MODERATE"
            calculatedScore >= 0.25f -> "LOW"
            else -> "OPTIMAL"
        }

        return ComponentFailureRiskEntity(
            componentId = component.id,
            componentName = component.name,
            systemName = component.system.displayName,
            baseProbability = baseProfile.baseProbability,
            dynamicRiskScore = calculatedScore,
            riskSeverity = severity,
            primaryFailureMode = baseProfile.failureMode,
            tsbReference = baseProfile.tsbRef,
            commonSymptoms = baseProfile.symptoms,
            mileageInterval = interval,
            lastServiceMileage = mostRecentLog?.mileageAtService,
            isOverdue = isOverdue,
            roomDataSourceSummary = dataSourceSummary,
            recommendedAction = baseProfile.action,
            reportedIncidentsCount = baseProfile.incidentCount,
            lastUpdatedMillis = now
        )
    }

    private data class BaselineRisk(
        val baseProbability: Float,
        val failureMode: String,
        val tsbRef: String,
        val symptoms: String,
        val mileageInterval: Int,
        val action: String,
        val incidentCount: Int
    )

    private fun getBaselineRiskProfile(
        componentId: String,
        componentName: String,
        system: VehicleSystem
    ): BaselineRisk {
        return when {
            componentId.contains("engine_block") || componentId.contains("timing") -> BaselineRisk(
                baseProbability = 0.92f,
                failureMode = "Hydraulic timing chain tensioner bleed-down & plastic cassette guide degradation",
                tsbRef = "Ford TSB 02-08-01 / TSB 04-15-04",
                symptoms = "Rattle/ticking for 3-5 seconds on cold start; plastic debris in oil pan",
                mileageInterval = 75000,
                action = "Replace front & jackshaft hydraulic tensioners; inspect oil pickup tube for plastic guide fragments",
                incidentCount = 1420
            )

            componentId.contains("thermostat") || componentId.contains("coolant") -> BaselineRisk(
                baseProbability = 0.88f,
                failureMode = "Composite plastic thermostat housing seam splitting and coolant valley leakage",
                tsbRef = "Ford TSB 01-11-06 / SSM 17822",
                symptoms = "Coolant puddle in engine V-valley; sweet antifreeze odor; temperature gauge spikes",
                mileageInterval = 30000,
                action = "Upgrade from factory composite plastic to aluminum thermostat housing assembly with Motorcraft Gold coolant",
                incidentCount = 1680
            )

            componentId.contains("intake") || componentId.contains("plenum") -> BaselineRisk(
                baseProbability = 0.84f,
                failureMode = "Hardened upper/lower intake plenum rubber O-rings drawing unmetered air",
                tsbRef = "Ford TSB 04-17-02 (Lean Codes P0171/P0174)",
                symptoms = "Rough idle in cold weather; hesitation on light throttle; CEL P0171/P0174",
                mileageInterval = 60000,
                action = "Install upgraded revised silicone intake manifold gasket kit and replace vacuum elbow",
                incidentCount = 1250
            )

            componentId.contains("trans") || componentId.contains("solenoid") -> BaselineRisk(
                baseProbability = 0.82f,
                failureMode = "5R55E valve body bonded gasket blowout & electronic pressure control (EPC) solenoid wear",
                tsbRef = "Ford TSB 03-14-08 / TSB 02-13-08",
                symptoms = "2-3 shift flare; delayed reverse engagement; flashing O/D OFF light (P0732/P0733)",
                mileageInterval = 30000,
                action = "Perform transmission fluid flush with MERCON V and install Ford upgraded valve body separator plate kit",
                incidentCount = 1190
            )

            componentId.contains("exhaust") || componentId.contains("egr") || componentId.contains("dpfe") -> BaselineRisk(
                baseProbability = 0.76f,
                failureMode = "DPFE sensor moisture corrosion and clogged EGR differential pressure sample tubes",
                tsbRef = "Ford TSB 03-19-11 (DPFE Sensor Recall 02M01)",
                symptoms = "CEL P0401 (EGR Insufficient Flow); mild spark knock/pinging on highway cruise",
                mileageInterval = 50000,
                action = "Replace aluminum DPFE sensor with updated plastic Motorcraft DPFE-15 and clean carbon from sample ports",
                incidentCount = 980
            )

            componentId.contains("transfer") || componentId.contains("4wd") || componentId.contains("4x4") -> BaselineRisk(
                baseProbability = 0.72f,
                failureMode = "ControlTrac transfer case shift motor position contact oxidation and front hub vacuum leak",
                tsbRef = "Ford TSB 01-23-06 (4x4 Flashing Lights)",
                symptoms = "4WD HIGH / 4WD LOW lights flash 6 times; transfer case fails to shift into 4x4",
                mileageInterval = 40000,
                action = "Clean shift motor rotary encoder contacts or replace shift motor actuator; service transfer case fluid with MERCON V",
                incidentCount = 840
            )

            componentId.contains("brake") || componentId.contains("suspension") || componentId.contains("ball_joint") -> BaselineRisk(
                baseProbability = 0.68f,
                failureMode = "Front upper control arm sealed ball joint boot rupture and sway bar link bushing fatigue",
                tsbRef = "Ford TSB 02-21-13 (Front Suspension Noise)",
                symptoms = "Clunking over speed bumps; uneven tire feathering; loose steering centering feel",
                mileageInterval = 45000,
                action = "Inspect ball joint vertical play with dial indicator; replace with greaseable heavy-duty control arms",
                incidentCount = 910
            )

            componentId.contains("alternator") || componentId.contains("battery") || componentId.contains("ignition") -> BaselineRisk(
                baseProbability = 0.62f,
                failureMode = "EDIS 6-tower ignition coil pack micro-fractures causing carbon tracking and secondary misfire",
                tsbRef = "Ford TSB 03-08-04",
                symptoms = "Cylinder misfire under heavy load (P0301-P0306); battery light flicker at high RPM",
                mileageInterval = 60000,
                action = "Inspect coil pack underside for hairline heat cracks; replace spark plugs with Motorcraft AGSF-22PP at 0.054 gap",
                incidentCount = 760
            )

            componentId.contains("ac") || componentId.contains("heater") || componentId.contains("hvac") -> BaselineRisk(
                baseProbability = 0.64f,
                failureMode = "HVAC blend door plastic actuator drive gear strip causing loss of temperature control",
                tsbRef = "Ford TSB 02-18-05",
                symptoms = "Repetitive clicking behind center dash; A/C blows only hot air or heater blows cold",
                mileageInterval = 60000,
                action = "Install reinforced metal-gear blend door actuator or use quick-cut plenum access repair kit",
                incidentCount = 890
            )

            componentId.contains("fuel") || componentId.contains("pump") -> BaselineRisk(
                baseProbability = 0.52f,
                failureMode = "In-tank fuel pump commutator wear and fuel tank rollover valve vapor blockage",
                tsbRef = "Ford TSB 01-14-04",
                symptoms = "Extended crank time before start; fuel pressure drops below 55 PSI under wide open throttle",
                mileageInterval = 80000,
                action = "Verify fuel rail Schrader valve pressure (65 PSI key on); replace inline fuel filter under driver frame rail",
                incidentCount = 610
            )

            componentId.contains("throttle") || componentId.contains("iac") -> BaselineRisk(
                baseProbability = 0.48f,
                failureMode = "Idle Air Control (IAC) valve solenoid varnish buildup and throttle body carbon coking",
                tsbRef = "Ford TSB 02-09-02",
                symptoms = "Engine stalls when stopping at red lights; buzzing/humming sound from intake air tube at idle",
                mileageInterval = 30000,
                action = "Clean throttle bore and IAC plunger with sensor-safe throttle body cleaner; replace IAC gasket",
                incidentCount = 540
            )

            else -> when (system) {
                VehicleSystem.ENGINE -> BaselineRisk(0.70f, "High thermal cycle stress on SOHC V6 valve train and gaskets", "TSB General Engine", "Engine vibration, slight oil weeping at valve covers", 50000, "Routine visual inspection and torque checks", 420)
                VehicleSystem.COOLING -> BaselineRisk(0.72f, "Plastic radiator end-tank crimp seal degradation over time", "TSB Cooling", "Minor coolant loss without external puddle", 40000, "Pressure test cooling system to 16 PSI", 510)
                VehicleSystem.TRANSMISSION -> BaselineRisk(0.68f, "Band servo piston rubber lip seal hardening", "TSB Transmission", "Firm 1-2 shift or mild slip into overdrive", 35000, "Check transmission fluid condition and line pressures", 490)
                VehicleSystem.ELECTRICAL -> BaselineRisk(0.50f, "Corrosion at battery ground eyelet and alternator terminal", "TSB Electrical", "Slow cranking in freezing temperatures", 45000, "Clean battery terminal clamps and apply dielectric grease", 380)
                VehicleSystem.BRAKES_CHASSIS -> BaselineRisk(0.55f, "Rear drum brake shoe self-adjuster cable seizure", "TSB Brakes", "High parking brake lever travel; spongy pedal", 30000, "Clean and lubricate rear drum adjuster star wheels", 410)
                else -> BaselineRisk(0.35f, "Standard mechanical wear within normal operational tolerances", "Standard Spec", "Normal wear and tear", 50000, "Periodic inspection during oil changes", 200)
            }
        }
    }
}
