package com.example.data

import com.example.data.local.MaintenanceDao
import com.example.data.local.MaintenanceEntity
import com.example.data.local.UpcomingTaskEntity
import com.example.data.local.VehicleProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {

    val allLogs: Flow<List<MaintenanceEntity>> = maintenanceDao.getAllMaintenanceLogs()
    val vehicleProfile: Flow<VehicleProfileEntity?> = maintenanceDao.getVehicleProfile()
    val upcomingTasks: Flow<List<UpcomingTaskEntity>> = maintenanceDao.getAllPendingUpcomingTasks()

    fun searchLogs(query: String): Flow<List<MaintenanceEntity>> {
        return maintenanceDao.searchMaintenanceLogs(query)
    }

    suspend fun logMaintenance(log: MaintenanceEntity): Long {
        return maintenanceDao.insertLog(log)
    }

    suspend fun updateLog(log: MaintenanceEntity) {
        maintenanceDao.updateLog(log)
    }

    suspend fun deleteLog(id: Long) {
        maintenanceDao.deleteLogById(id)
    }

    suspend fun addUpcomingTask(task: UpcomingTaskEntity): Long {
        return maintenanceDao.insertUpcomingTask(task)
    }

    suspend fun updateUpcomingTask(task: UpcomingTaskEntity) {
        maintenanceDao.updateUpcomingTask(task)
    }

    suspend fun deleteUpcomingTask(taskId: Long) {
        maintenanceDao.deleteUpcomingTaskById(taskId)
    }

    suspend fun completeUpcomingTask(
        task: UpcomingTaskEntity,
        actualMileage: Int,
        costUsd: Double,
        notes: String
    ) {
        maintenanceDao.markUpcomingTaskCompleted(task.id)

        val logEntity = MaintenanceEntity(
            scheduleItemId = task.scheduleItemId,
            title = task.title,
            systemName = task.systemName,
            mileageAtService = actualMileage,
            dateLoggedMillis = System.currentTimeMillis(),
            componentDescription = task.fluidSpecOrPart,
            costUsd = costUsd,
            notes = if (notes.isBlank()) "Completed via Upcoming Tasks scheduler" else notes,
            isCompleted = true
        )
        maintenanceDao.insertLog(logEntity)

        val nextTargetMileage = actualMileage + task.intervalMiles
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val estimatedIntervalDays = ((task.intervalMiles.toDouble() / 12000.0) * 365.0).toLong()
        val nextDueDateMillis = System.currentTimeMillis() + (estimatedIntervalDays * 24 * 60 * 60 * 1000L).coerceAtLeast(thirtyDaysMillis)

        val nextTask = task.copy(
            id = 0,
            targetMileage = nextTargetMileage,
            dueDateMillis = nextDueDateMillis,
            isCompleted = false,
            notes = ""
        )
        maintenanceDao.insertUpcomingTask(nextTask)
    }

    suspend fun updateVehicleMileage(newMileage: Int) {
        val existing = vehicleProfile.firstOrNull() ?: VehicleProfileEntity()
        maintenanceDao.saveVehicleProfile(existing.copy(currentMileage = newMileage))
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existingProfile = vehicleProfile.firstOrNull()
        if (existingProfile == null) {
            maintenanceDao.saveVehicleProfile(VehicleProfileEntity())
        }
        val existingLogs = allLogs.firstOrNull()
        if (existingLogs.isNullOrEmpty()) {
            for (sampleLog in SportTracData.initialSampleLogs) {
                maintenanceDao.insertLog(sampleLog)
            }
        }
        val existingUpcoming = upcomingTasks.firstOrNull()
        if (existingUpcoming.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L
            val defaultTasks = listOf(
                UpcomingTaskEntity(
                    scheduleItemId = "sched_cooling_1",
                    title = "Cooling System & Thermostat Housing",
                    systemName = "Cooling",
                    targetMileage = 115200,
                    dueDateMillis = now - (2 * dayMillis),
                    intervalMiles = 40000,
                    fluidSpecOrPart = "Motorcraft Gold Premium Antifreeze & Thermostat Assembly",
                    priorityLevel = "CRITICAL",
                    estimatedCostUsd = 85.00,
                    notes = "Inspect plastic housing seams for weeping leaks"
                ),
                UpcomingTaskEntity(
                    scheduleItemId = "sched_oil_1",
                    title = "Engine Oil & Filter Service",
                    systemName = "Engine",
                    targetMileage = 118000,
                    dueDateMillis = now + (14 * dayMillis),
                    intervalMiles = 5000,
                    fluidSpecOrPart = "5W-30 Synthetic Blend + Motorcraft FL-820S Filter",
                    priorityLevel = "HIGH",
                    estimatedCostUsd = 42.50,
                    notes = "5.0 Quarts 5W-30 API SP Certified"
                ),
                UpcomingTaskEntity(
                    scheduleItemId = "sched_trans_1",
                    title = "5R55E Transmission Fluid & Filter",
                    systemName = "Transmission",
                    targetMileage = 120000,
                    dueDateMillis = now + (35 * dayMillis),
                    intervalMiles = 30000,
                    fluidSpecOrPart = "Motorcraft MERCON V ATF & Pan Gasket",
                    priorityLevel = "HIGH",
                    estimatedCostUsd = 95.00,
                    notes = "Drop pan, clean magnet, replace internal filter"
                ),
                UpcomingTaskEntity(
                    scheduleItemId = "sched_transfer_1",
                    title = "4x4 Transfer Case Fluid Service",
                    systemName = "Drivetrain",
                    targetMileage = 120000,
                    dueDateMillis = now + (45 * dayMillis),
                    intervalMiles = 30000,
                    fluidSpecOrPart = "1.3 Quarts Motorcraft MERCON ATF (XT-2-QDX) \u2014 2004 OG",
                    priorityLevel = "NORMAL",
                    estimatedCostUsd = 28.00,
                    notes = "OG: transfer case uses MERCON ATF, not MERCON V. Fill to bottom of filler plug."
                ),
                UpcomingTaskEntity(
                    scheduleItemId = "sched_sparks_1",
                    title = "4.0L SOHC Platinum Spark Plugs & Wires",
                    systemName = "Engine",
                    targetMileage = 125000,
                    dueDateMillis = now + (90 * dayMillis),
                    intervalMiles = 60000,
                    fluidSpecOrPart = "Motorcraft AGSF-22PP & Wire Set",
                    priorityLevel = "NORMAL",
                    estimatedCostUsd = 110.00,
                    notes = "OG gap 0.052-0.056 in (AGSF-22PP). Apply dielectric grease to boots"
                ),
                UpcomingTaskEntity(
                    scheduleItemId = "sched_diff_1",
                    title = "Rear Differential Fluid Service (8.8-inch)",
                    systemName = "Drivetrain",
                    targetMileage = 130000,
                    dueDateMillis = now + (150 * dayMillis),
                    intervalMiles = 50000,
                    fluidSpecOrPart = "Motorcraft SAE 75W-90 FE Synthetic + XL-7 if Traction-Lok \u2014 2004 OG",
                    priorityLevel = "NORMAL",
                    estimatedCostUsd = 65.00,
                    notes = "OG rear axle: 5.5-5.8 pints 75W-90 FE synthetic. Add 4 oz XL-7 only for Traction-Lok complete refill."
                )
            )
            maintenanceDao.insertUpcomingTasks(defaultTasks)
        }
    }
}
