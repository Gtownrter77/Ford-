package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// --- Data Models for Diagnostic Wizard Decision Tree ---

data class WizardOption(
    val title: String,
    val subtitle: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.ChevronRight,
    val nextStepId: String? = null, // Null if this leads directly to a final diagnosis
    val finalDiagnosis: WizardDiagnosis? = null
)

data class WizardStep(
    val id: String,
    val questionNumber: Int,
    val totalEstimatedSteps: Int,
    val questionTitle: String,
    val questionSubtitle: String? = null,
    val techTip: String? = null,
    val options: List<WizardOption>
)

enum class DiagnosticSeverity(val label: String, val color: Color) {
    CRITICAL("CRITICAL - IMMEDIATE ATTENTION", Color(0xFFEF4444)),
    HIGH("HIGH - REPAIR SOON", Color(0xFFFF6F00)),
    MODERATE("MODERATE - MONITOR & SCHEDULE", Color(0xFFEAB308)),
    LOW("LOW / ROUTINE MAINTENANCE", Color(0xFF10B981))
}

data class WizardDiagnosis(
    val issueTitle: String,
    val componentId: String,
    val componentName: String,
    val confidencePercent: Int,
    val severity: DiagnosticSeverity,
    val obdCodes: List<String> = emptyList(),
    val summary: String,
    val verificationProcedure: String,
    val recommendedParts: List<String>,
    val fordPartNumber: String? = null,
    val estimatedRepairTime: String,
    val difficultyLevel: String
)

data class WizardCategory(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val rootStepId: String
)

// --- Preset Decision Trees for Sport Trac 4.0L SOHC ---

object SportTracDiagnosticTrees {

    val categories = listOf(
        WizardCategory(
            id = "engine",
            title = "Engine & Idle Stumble",
            description = "Misfires, vacuum leaks, rough idle, PCV elbow, coil pack, IAC",
            icon = Icons.Default.Speed,
            color = Color(0xFFFF6F00),
            rootStepId = "eng_1"
        ),
        WizardCategory(
            id = "suspension",
            title = "Suspension & Clunks",
            description = "Front-end clunks, wheel bearing hum, ball joints, sway bar links",
            icon = Icons.Default.DirectionsCar,
            color = Color(0xFF38BDF8),
            rootStepId = "susp_1"
        ),
        WizardCategory(
            id = "transmission",
            title = "5R55E Transmission",
            description = "2-3 shift flare, O/D light flashing, delayed reverse, servo wear",
            icon = Icons.Default.Settings,
            color = Color(0xFFA855F7),
            rootStepId = "trans_1"
        ),
        WizardCategory(
            id = "4x4",
            title = "ControlTrac 4WD",
            description = "4x4 High/Low lights flashing 6 times, transfer case click, shift motor",
            icon = Icons.Default.Explore,
            color = Color(0xFF10B981),
            rootStepId = "4x4_1"
        ),
        WizardCategory(
            id = "cooling",
            title = "Cooling & Heating",
            description = "Thermostat housing leak, overheating at stoplights, heater core",
            icon = Icons.Default.Thermostat,
            color = Color(0xFFEF4444),
            rootStepId = "cool_1"
        ),
        WizardCategory(
            id = "audio",
            title = "Radio, Audio & Speakers",
            description = "Engine alternator whistle, dead speakers, CD changer error, Pioneer sub",
            icon = Icons.Default.VolumeUp,
            color = Color(0xFFF59E0B),
            rootStepId = "audio_1"
        )
    )

    val stepsMap = mapOf(
        // --- ENGINE DECISION TREE ---
        "eng_1" to WizardStep(
            id = "eng_1",
            questionNumber = 1,
            totalEstimatedSteps = 3,
            questionTitle = "When does the engine issue occur?",
            questionSubtitle = "Select the primary symptom driving behavior on your Sport Trac 4.0L",
            techTip = "Check Engine Light (CEL) state provides key clues: A flashing CEL indicates active catalyst-damaging misfire.",
            options = listOf(
                WizardOption(
                    title = "Rough idle at stoplights, smooths out when accelerating",
                    subtitle = "Accompanied by high idle or lean codes P0171 / P0174",
                    icon = Icons.Default.Air,
                    nextStepId = "eng_vacuum"
                ),
                WizardOption(
                    title = "Engine misfire & hesitation under heavy load / acceleration",
                    subtitle = "Bucking or jerking when climbing hills or passing",
                    icon = Icons.Default.FlashOn,
                    nextStepId = "eng_misfire"
                ),
                WizardOption(
                    title = "Engine cranks continuously but won't start",
                    subtitle = "No firing or immediate stall after starting",
                    icon = Icons.Default.PowerSettingsNew,
                    nextStepId = "eng_nostart"
                )
            )
        ),

        "eng_vacuum" to WizardStep(
            id = "eng_vacuum",
            questionNumber = 2,
            totalEstimatedSteps = 3,
            questionTitle = "Do you hear a whistling or soft hiss near the back of the engine?",
            questionSubtitle = "Listen behind the upper intake plenum while engine is idling in Park",
            techTip = "The factory rubber PCV vacuum elbow behind the upper intake on 2001-2005 4.0L SOHC engines dry-rots and collapses under vacuum, creating a massive lean vacuum leak.",
            options = listOf(
                WizardOption(
                    title = "Yes, loud hiss / whistle audible behind intake plenum",
                    subtitle = "Rough idle stumbles around 500-600 RPM",
                    icon = Icons.Default.VolumeUp,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "PCV Rubber Hose Elbow Collapse / Vacuum Leak",
                        componentId = "pcv_valve",
                        componentName = "PCV Valve & Vacuum Hose Assembly",
                        confidencePercent = 95,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P0171", "P0174", "P0300"),
                        summary = "The L-shaped rubber elbow connecting the PCV tube to the rear of the upper intake plenum has dry-rotted, softened, and collapsed inward. This allows unmetered air directly into the combustion chamber, causing lean codes on both Bank 1 & Bank 2.",
                        verificationProcedure = "While engine idles, spray a brief mist of carb cleaner or throttle body spray onto the rubber PCV elbow behind the upper intake. If engine RPM instantly changes or smooths out, the vacuum leak is 100% verified.",
                        recommendedParts = listOf("Motorcraft PCV Valve Hose Elbow", "Motorcraft EV-243 PCV Valve"),
                        fordPartNumber = "2L2Z-6C324-AA / KCV-190",
                        estimatedRepairTime = "25 minutes",
                        difficultyLevel = "Beginner (No special tools needed)"
                    )
                ),
                WizardOption(
                    title = "No hiss audible, but RPM bounces continuously (500 to 1100 RPM)",
                    subtitle = "Stalls occasionally when coming to a complete stop",
                    icon = Icons.Default.SyncProblem,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Idle Air Control (IAC) Valve Carbon Fouling / Stickiness",
                        componentId = "iac_valve",
                        componentName = "Idle Air Control (IAC) Valve",
                        confidencePercent = 88,
                        severity = DiagnosticSeverity.MODERATE,
                        obdCodes = listOf("P0505", "P1504"),
                        summary = "Carbon deposits from crankcase vapors coat the internal plunger of the Idle Air Control (IAC) valve, preventing smooth air metering during idle.",
                        verificationProcedure = "Tap the aluminum body of the IAC valve with the handle of a screwdriver while the engine is stumbling at idle. If idle instantly stabilizes, the valve plunger is sticking due to carbon buildup.",
                        recommendedParts = listOf("Motorcraft CX-1863 IAC Valve", "IAC Mounting Gasket"),
                        fordPartNumber = "1F1Z-9F715-AA",
                        estimatedRepairTime = "20 minutes",
                        difficultyLevel = "Beginner (2x 10mm bolts)"
                    )
                )
            )
        ),

        "eng_misfire" to WizardStep(
            id = "eng_misfire",
            questionNumber = 2,
            totalEstimatedSteps = 3,
            questionTitle = "Is the misfire isolated to a specific cylinder OBD code?",
            questionSubtitle = "Check OBD codes like P0301 (Cyl 1), P0302 (Cyl 2), P0303, P0304, P0305, or P0306",
            techTip = "The Ford 4.0L SOHC waste-spark coil pack fires cylinders in pairs (1/5, 2/6, 3/4). Cracks in the coil plastic housing allow high voltage to arc directly to the engine block.",
            options = listOf(
                WizardOption(
                    title = "Single cylinder code (e.g. P0302 or P0303)",
                    subtitle = "Hesitation worsens in wet / humid weather",
                    icon = Icons.Default.ElectricBolt,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Ignition Coil Pack Housing Crack / Spark Plug Wire Breakdown",
                        componentId = "ignition_coil",
                        componentName = "Ignition Coil Pack & Wires",
                        confidencePercent = 90,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P0301", "P0302", "P0303", "P0304", "P0305", "P0306"),
                        summary = "Micro-fractures in the epoxy underside of the coil pack or carbon tracking inside the silicone spark plug boot allow 30,000V spark energy to arc directly to surrounding ground.",
                        verificationProcedure = "Mist water from a spray bottle over the coil pack and plug wires in low ambient light while idling. Observe for visible blue spark arcing or ticking sounds.",
                        recommendedParts = listOf("Motorcraft DG-532 Coil Pack", "Motorcraft WR-5935 Silicone Wire Set", "Motorcraft AGSF-22PP Spark Plugs"),
                        fordPartNumber = "1L2Z-12029-AA",
                        estimatedRepairTime = "45 minutes",
                        difficultyLevel = "Intermediate"
                    )
                ),
                WizardOption(
                    title = "Multiple random misfires (P0300) with poor acceleration",
                    subtitle = "Engine feels starved for fuel above 3,000 RPM",
                    icon = Icons.Default.LocalGasStation,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Clogged Fuel Filter / Low Fuel Pressure",
                        componentId = "fuel_filter",
                        componentName = "In-Line Fuel Filter",
                        confidencePercent = 85,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P0300", "P0171", "P0174"),
                        summary = "Debris build-up inside the frame-rail fuel filter restricts volumetric fuel flow under load, dropping fuel rail pressure below the required 65 PSI.",
                        verificationProcedure = "Connect a Schrader valve fuel pressure gauge to the fuel rail diagnostic port on the passenger upper engine bay. Pressure must hold steady at 60-65 PSI during snap-throttle.",
                        recommendedParts = listOf("Motorcraft FG-1083 Fuel Filter", "Quick Disconnect Fuel Line Tool (5/16 and 3/8)"),
                        fordPartNumber = "2L2Z-9155-AA",
                        estimatedRepairTime = "30 minutes",
                        difficultyLevel = "Beginner / Intermediate"
                    )
                )
            )
        ),

        "eng_nostart" to WizardStep(
            id = "eng_nostart",
            questionNumber = 2,
            totalEstimatedSteps = 3,
            questionTitle = "When you turn key to 'ON', do you hear the fuel pump hum for 2 seconds?",
            questionSubtitle = "Listen near the fuel tank fill neck under the truck bed",
            techTip = "The Sport Trac inertia fuel shutoff switch is located behind the passenger side kick panel under the glovebox and trips during hard bumps or impacts.",
            options = listOf(
                WizardOption(
                    title = "No hum audible at all from fuel pump",
                    subtitle = "Inertia switch button popped or fuel pump relay dead",
                    icon = Icons.Default.VolumeOff,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Tripped Inertia Shutoff Switch or Fuel Pump Relay",
                        componentId = "fuel_filter",
                        componentName = "Fuel Delivery System & Inertia Switch",
                        confidencePercent = 92,
                        severity = DiagnosticSeverity.CRITICAL,
                        obdCodes = listOf("P0230", "P0231"),
                        summary = "The safety inertia switch opened its circuit to disable fuel pump power, or the high-current relay in the battery junction box failed.",
                        verificationProcedure = "Press down firmly on the red button atop the inertia switch located behind the passenger front footwell kick panel. Swap the Fuel Pump Relay with the Horn Relay in the underhood box.",
                        recommendedParts = listOf("Bosch Micro 12V 40A Relay", "Inertia Safety Switch Assembly"),
                        fordPartNumber = "F88Z-9341-AA",
                        estimatedRepairTime = "15 minutes",
                        difficultyLevel = "Beginner"
                    )
                )
            )
        ),

        // --- SUSPENSION DECISION TREE ---
        "susp_1" to WizardStep(
            id = "susp_1",
            questionNumber = 1,
            totalEstimatedSteps = 3,
            questionTitle = "What type of noise or handling behavior are you experiencing?",
            questionSubtitle = "Identify front-end noise characteristics on 2001-2005 Sport Trac 4WD/RWD",
            techTip = "Sport Tracs use plastic/metal sway bar end links that snap easily, creating a distinct metallic clunk when turning into driveways.",
            options = listOf(
                WizardOption(
                    title = "Heavy clunking / popping sound over speed bumps or driveways",
                    subtitle = "Sounds like metal hitting frame under front floorboards",
                    icon = Icons.Default.Build,
                    nextStepId = "susp_clunk"
                ),
                WizardOption(
                    title = "Loud humming / roaring noise that increases with vehicle speed",
                    subtitle = "Sounds like a propeller plane, changes pitch when turning left/right",
                    icon = Icons.Default.GraphicEq,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Front Wheel Hub & Bearing Assembly Wear",
                        componentId = "front_wheel_bearing",
                        componentName = "Front Wheel Hub & Bearing Assembly",
                        confidencePercent = 94,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = emptyList(),
                        summary = "Internal steel ball bearings and races inside the sealed unitized wheel hub have pitted due to moisture intrusion past the grease seal.",
                        verificationProcedure = "Safely jack up the front wheel off the ground. Place hands at 12 o'clock and 6 o'clock and shake wheel vigorously. Any play or rough grinding rotation confirms bearing wear.",
                        recommendedParts = listOf("Motorcraft HUB-143 Wheel Hub", "Timken SP470200 Hub Assembly"),
                        fordPartNumber = "1L2Z-1104-AA",
                        estimatedRepairTime = "1 hour 15 minutes",
                        difficultyLevel = "Intermediate (Requires 32mm Axle Nut Socket)"
                    )
                )
            )
        ),

        "susp_clunk" to WizardStep(
            id = "susp_clunk",
            questionNumber = 2,
            totalEstimatedSteps = 3,
            questionTitle = "Inspect the front sway bar end links through the wheel well",
            questionSubtitle = "Look behind front tires for vertical bolt connecting sway bar to lower control arm",
            techTip = "Stock Ford sway bar link plastic sleeve spacers shatter after 60,000 miles, leaving loose bolts that rattle in control arm mounting holes.",
            options = listOf(
                WizardOption(
                    title = "Sway bar link plastic sleeve broken or bolt snapped in half",
                    subtitle = "Sway bar end is hanging loose",
                    icon = Icons.Default.Warning,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Broken Front Sway Bar Link Assembly",
                        componentId = "sway_bar_links",
                        componentName = "Front Sway Bar End Link Kit",
                        confidencePercent = 98,
                        severity = DiagnosticSeverity.MODERATE,
                        obdCodes = emptyList(),
                        summary = "The vertical sway bar end link bolt or factory plastic sleeve snapped, allowing the heavy anti-roll bar to bang directly against the lower control arm.",
                        verificationProcedure = "Visual inspection through front wheel well instantly confirms broken link or missing bushings.",
                        recommendedParts = listOf("Moog K80066 Heavy Duty Thermoplastic Sway Bar Link Kit", "Motorcraft MEF-63"),
                        fordPartNumber = "2L2Z-5K483-AA",
                        estimatedRepairTime = "30 minutes",
                        difficultyLevel = "Beginner (15mm socket)"
                    )
                ),
                WizardOption(
                    title = "Sway bar links intact, but lower ball joint rubber boot is torn/greaseless",
                    subtitle = "Squeaking sound when steering back and forth while stationary",
                    icon = Icons.Default.PrecisionManufacturing,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Worn Upper / Lower Ball Joints",
                        componentId = "upper_lower_ball_joints",
                        componentName = "Front Ball Joints & Control Arms",
                        confidencePercent = 91,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = emptyList(),
                        summary = "Greaseless ball-and-socket joint inside the control arm has developed severe play, risking joint separation and wheel detachment.",
                        verificationProcedure = "Insert pry bar between lower control arm and steering knuckle. Pry upward. Any vertical movement in ball joint indicates total replacement required.",
                        recommendedParts = listOf("Moog K80008 Lower Ball Joint", "Moog K8708T Upper Control Arm & Ball Joint Assembly"),
                        fordPartNumber = "1L2Z-3050-AB",
                        estimatedRepairTime = "2 hours",
                        difficultyLevel = "Advanced (Ball Joint Press Required)"
                    )
                )
            )
        ),

        // --- TRANSMISSION DECISION TREE ---
        "trans_1" to WizardStep(
            id = "trans_1",
            questionNumber = 1,
            totalEstimatedSteps = 3,
            questionTitle = "Describe the 5R55E automatic transmission symptom",
            questionSubtitle = "5R55E 5-speed automatic transmission issue diagnostic path",
            techTip = "The aluminum transmission case servo bores wear into an oval shape over time due to steel servo pin contact, causing hydraulic pressure loss.",
            options = listOf(
                WizardOption(
                    title = "RPM flares up (slips) between 2nd and 3rd gear shift",
                    subtitle = "Delay in engagement followed by sudden harsh kick/clunk",
                    icon = Icons.Default.Speed,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Intermediate / Overdrive Servo Bore Wear & O/D Band Slip",
                        componentId = "transmission_5r55e",
                        componentName = "5R55E Transmission & Servo Bore",
                        confidencePercent = 93,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P0732", "P0733", "P0741"),
                        summary = "The steel shaft of the Intermediate Servo piston wears away the soft cast-aluminum case bore, venting hydraulic fluid away from applying the band.",
                        verificationProcedure = "Check Mercon V fluid level and color. Scan TCM for pending P0732/P0733 ratio error codes. Install brass sleeve repair kit or AJ1 Servo Sleeve.",
                        recommendedParts = listOf("Ford Mercon V ATF", "AJ1 Brass Servo Bore Sleeve Kit", "5R55E Filter & Gasket Kit"),
                        fordPartNumber = "XT-5-QM (Mercon V)",
                        estimatedRepairTime = "2 hours (Sleeve installation in-vehicle)",
                        difficultyLevel = "Advanced"
                    )
                ),
                WizardOption(
                    title = "O/D OFF light on dashboard is flashing continuously",
                    subtitle = "Transmission locked in Limp-Home Mode (3rd Gear Start)",
                    icon = Icons.Default.Emergency,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "EPC / Shift Solenoid Electronic Fault or Valve Body Gasket Blowout",
                        componentId = "transmission_5r55e",
                        componentName = "5R55E Solenoid Block & Valve Body Gasket",
                        confidencePercent = 89,
                        severity = DiagnosticSeverity.CRITICAL,
                        obdCodes = listOf("P0705", "P0743", "P0750", "P0755", "P1747"),
                        summary = "The TCM detected electronic pressure control (EPC) out of specification or a blown paper valve body bonded gasket leaking internal hydraulic circuit pressure.",
                        verificationProcedure = "Read Ford OEM transmission trouble codes using FORScan software with ELM327 adapter to identify exact solenoid circuit fault.",
                        recommendedParts = listOf("5R55E Solenoid Block Pack", "Bonded Heavy Duty Valve Body Gaskets"),
                        fordPartNumber = "1L2Z-7G391-AA",
                        estimatedRepairTime = "1 hour 30 minutes",
                        difficultyLevel = "Intermediate (Pan removal)"
                    )
                )
            )
        ),

        // --- 4x4 DECISION TREE ---
        "4x4_1" to WizardStep(
            id = "4x4_1",
            questionNumber = 1,
            totalEstimatedSteps = 2,
            questionTitle = "What happens when you turn dashboard 4x4 selector switch?",
            questionSubtitle = "ControlTrac 4WD System Diagnostic Flow",
            techTip = "When 4WD module detects circuit failure on key-on, it flashes 4x4 HIGH and 4x4 LOW lights 6 times on instrument cluster.",
            options = listOf(
                WizardOption(
                    title = "4x4 High and Low lights flash 6 times on start-up & 4WD won't engage",
                    subtitle = "No relay click heard from behind glovebox",
                    icon = Icons.Default.SyncDisabled,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "4WD Control Module (GEM) Failure / Power Loss",
                        componentId = "transfer_case_4wd",
                        componentName = "4WD Control Module (GEM / 4x4 Module)",
                        confidencePercent = 90,
                        severity = DiagnosticSeverity.MODERATE,
                        obdCodes = listOf("P1812", "P1834"),
                        summary = "The 4x4 Control Module mounted behind passenger kick panel lost communications or experienced internal relay contact oxidation.",
                        verificationProcedure = "Verify fuse #14 (15A) in battery junction box and fuse #20 in cabin box. Inspect module connector for corrosion pin damage.",
                        recommendedParts = listOf("Motorcraft 4WD Control Module", "Dorman 600-551 4x4 Control Module"),
                        fordPartNumber = "1L2Z-7E453-AA",
                        estimatedRepairTime = "25 minutes",
                        difficultyLevel = "Beginner (Plug-and-play under glovebox)"
                    )
                ),
                WizardOption(
                    title = "Click is heard from dash, but transfer case motor won't physically shift",
                    subtitle = "Motor hums briefly or stops midway between 2WD and 4x4 High",
                    icon = Icons.Default.ReportProblem,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Transfer Case Shift Motor Encoder Contact Plate Wear",
                        componentId = "transfer_case_4wd",
                        componentName = "Transfer Case Shift Motor",
                        confidencePercent = 95,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P1867", "P1891"),
                        summary = "The electric encoder gear motor mounted under transfer case has accumulated disintegrated rubber stop debris or corroded contact wipers.",
                        verificationProcedure = "Lightly tap aluminum housing of transfer case shift motor with hammer while partner switches 4x4 knob. If motor engages, internal contacts are sticking.",
                        recommendedParts = listOf("Dorman 600-802 Transfer Case Shift Motor", "Motorcraft YL2Z-7G360-A"),
                        fordPartNumber = "1L2Z-7G360-AA",
                        estimatedRepairTime = "40 minutes",
                        difficultyLevel = "Intermediate (3 bolts under truck)"
                    )
                )
            )
        ),

        // --- COOLING DECISION TREE ---
        "cool_1" to WizardStep(
            id = "cool_1",
            questionNumber = 1,
            totalEstimatedSteps = 2,
            questionTitle = "Where is coolant leaking or overheating observed?",
            questionSubtitle = "4.0L SOHC Cooling System Diagnostic Path",
            techTip = "The factory two-piece plastic thermostat housing seams fracture over time under 16 PSI cooling pressure, pooling green/orange coolant in engine valley.",
            options = listOf(
                WizardOption(
                    title = "Coolant pooling in engine valley under intake / thermostat housing",
                    subtitle = "Sweet coolant smell in cabin with slow fluid loss",
                    icon = Icons.Default.WaterDrop,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Thermostat Housing Seam Crack / Leak",
                        componentId = "thermostat_housing",
                        componentName = "Thermostat Housing & Temp Sensors",
                        confidencePercent = 97,
                        severity = DiagnosticSeverity.HIGH,
                        obdCodes = listOf("P0128", "P0117"),
                        summary = "The sonic-welded plastic seam of the factory upper/lower thermostat housing fractured due to heat cycling.",
                        verificationProcedure = "Inspect engine valley directly behind alternator using flashlight. Puddle of coolant around temp sensors confirms housing seam fracture.",
                        recommendedParts = listOf("Dorman Aluminum Upgraded Thermostat Housing Kit", "Motorcraft RT-1167 Thermostat"),
                        fordPartNumber = "2L2Z-8592-AA / RH-148",
                        estimatedRepairTime = "1 hour",
                        difficultyLevel = "Intermediate"
                    )
                )
            )
        ),

        // --- RADIO, AUDIO & SPEAKERS DECISION TREE ---
        "audio_1" to WizardStep(
            id = "audio_1",
            questionNumber = 1,
            totalEstimatedSteps = 2,
            questionTitle = "What audio, radio, or speaker issue are you experiencing?",
            questionSubtitle = "Mach 500 / Pioneer Premium Audio Diagnostic Path",
            techTip = "Alternator AC diode ripple creates a high-pitch whine that directly matches engine RPM pitch through speaker wires when ground straps loosen.",
            options = listOf(
                WizardOption(
                    title = "High-pitched whining or whistling sound through speakers when accelerating",
                    subtitle = "Pitch rises and falls directly with engine RPM",
                    icon = Icons.Default.GraphicEq,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Alternator Ground Noise / Diode Ripple in Audio System",
                        componentId = "radio_mach500_head_unit_3d",
                        componentName = "Mach 500 Radio Head Unit & Ground Straps",
                        confidencePercent = 96,
                        severity = DiagnosticSeverity.MODERATE,
                        obdCodes = emptyList(),
                        summary = "Alternator AC diode ripple or a degraded engine firewall ground strap induces high-frequency electrical noise directly into radio 12V power feed and speaker leads.",
                        verificationProcedure = "Rev engine in Park. If high-pitched whistle frequency tracks RPM and disappears when radio is switched OFF, ground noise choke or firewall ground strap repair is needed.",
                        recommendedParts = listOf("Metra 12V 40A Inline Audio Power Noise Choke Filter", "Motorcraft Engine-to-Firewall Ground Strap"),
                        fordPartNumber = "1L2F-18C815-AA / Ground Strap",
                        estimatedRepairTime = "25 minutes",
                        difficultyLevel = "Beginner"
                    )
                ),
                WizardOption(
                    title = "Door speaker static, loud crackling, or dead audio channel",
                    subtitle = "Distortion or scratching noise when volume is raised",
                    icon = Icons.Default.Speaker,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Door Speaker Dry Rotted Cone Surround or Torn Voice Coil",
                        componentId = "audio_door_speakers_subwoofer_3d",
                        componentName = "Factory 6x8 Custom Door Speakers",
                        confidencePercent = 94,
                        severity = DiagnosticSeverity.LOW,
                        obdCodes = emptyList(),
                        summary = "Humidity and thermal cycling inside the door panel degraded the factory cloth/foam surround on the 6x8 speakers, causing coil rub or paper cone tear.",
                        verificationProcedure = "Pop interior door trim panel, inspect 6x8 speaker cone surround, and test terminals with multimeter for 4-ohm resistance.",
                        recommendedParts = listOf("Pioneer TS-A683FH 6x8 3-Way Coaxial Speakers (Pair)", "Metra 72-5600 Ford Speaker Wire Adapters"),
                        fordPartNumber = "1L2Z-18808-BA",
                        estimatedRepairTime = "45 minutes",
                        difficultyLevel = "Beginner (2 screws per door)"
                    )
                ),
                WizardOption(
                    title = "Radio display shows 'CD ERROR' or CD changer is physically jammed",
                    subtitle = "Mach 500 in-dash 6-disc CD changer will not eject or load discs",
                    icon = Icons.Default.DiscFull,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "In-Dash 6-Disc CD Changer Ejection Gear Mechanism Jam",
                        componentId = "radio_mach500_head_unit_3d",
                        componentName = "Mach 500 Double-DIN Stereo Receiver",
                        confidencePercent = 92,
                        severity = DiagnosticSeverity.LOW,
                        obdCodes = emptyList(),
                        summary = "Internal plastic drive gears and disc carrier trays in the factory Mach 500 head unit bind or slip off alignment tracks.",
                        verificationProcedure = "Pull head unit out of dash using U-shaped Ford DIN removal keys. Unbolt top shield cover to clear stuck CD disc manually.",
                        recommendedParts = listOf("Ford U-Shaped DIN Radio Removal Key Tool Set", "Aftermarket Double-DIN Touchscreen Bluetooth Radio Harness Kit"),
                        fordPartNumber = "1L2F-18C815-AA",
                        estimatedRepairTime = "30 minutes",
                        difficultyLevel = "Beginner"
                    )
                ),
                WizardOption(
                    title = "Pioneer rear subwoofer produces zero bass or muffled low end",
                    subtitle = "Rear passenger quarter panel 8-inch subwoofer enclosure unresponsive",
                    icon = Icons.Default.Headphones,
                    finalDiagnosis = WizardDiagnosis(
                        issueTitle = "Pioneer Powered Subwoofer Amp Fuse or Ground Fault",
                        componentId = "audio_door_speakers_subwoofer_3d",
                        componentName = "Pioneer 8\" Powered Subwoofer & 290W Amp",
                        confidencePercent = 89,
                        severity = DiagnosticSeverity.LOW,
                        obdCodes = emptyList(),
                        summary = "Dedicated 15A audio amplifier fuse #1.03 in engine junction box blown or rear cab floor ground eyelet corroded.",
                        verificationProcedure = "Check 15A fuse #1.03 under hood. Test 12V remote turn-on wire lead at subwoofer amplifier harness plug behind rear interior quarter trim panel.",
                        recommendedParts = listOf("Bussmann 15A Mini Blade Fuse", "Kicker 48CWRT82 8-Inch Dual 2-Ohm Subwoofer Driver"),
                        fordPartNumber = "1L2Z-18808-CA",
                        estimatedRepairTime = "40 minutes",
                        difficultyLevel = "Intermediate"
                    )
                )
            )
        )
    )
}

// --- Main Diagnostic Wizard Dialog Composable ---

@Composable
fun DiagnosticWizardDialog(
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<WizardCategory?>(SportTracDiagnosticTrees.categories.first()) }
    var currentStepId by remember { mutableStateOf(selectedCategory?.rootStepId ?: "eng_1") }
    var stepHistory by remember { mutableStateOf(listOf<String>()) }
    var activeDiagnosis by remember { mutableStateOf<WizardDiagnosis?>(null) }

    val currentStep = SportTracDiagnosticTrees.stepsMap[currentStepId]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("diagnostic_wizard_dialog"),
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, Color(0xFF0284C7)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF0284C7),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SPORT TRAC DIAGNOSTIC WIZARD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Guided Decision Tree Analysis",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("wizard_close_button")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Divider(color = Color(0xFF1E293B), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Category Selection Carousel
                Text(
                    text = "SELECT DIAGNOSTIC DOMAIN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SportTracDiagnosticTrees.categories) { cat ->
                        val isSelected = selectedCategory?.id == cat.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedCategory = cat
                                    currentStepId = cat.rootStepId
                                    stepHistory = emptyList()
                                    activeDiagnosis = null
                                }
                                .testTag("wizard_cat_${cat.id}"),
                            color = if (isSelected) cat.color.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) cat.color else Color(0xFF334155)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) cat.color else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content View: Interactive Decision Step vs. Final Diagnosis Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (activeDiagnosis != null) {
                        // --- FINAL DIAGNOSIS RESULT UI ---
                        DiagnosisResultView(
                            diagnosis = activeDiagnosis!!,
                            onRestart = {
                                activeDiagnosis = null
                                currentStepId = selectedCategory?.rootStepId ?: "eng_1"
                                stepHistory = emptyList()
                            },
                            onNavigateToComponent = { compId ->
                                onDismiss()
                                onNavigateToComponent(compId)
                            }
                        )
                    } else if (currentStep != null) {
                        // --- INTERACTIVE DECISION TREE STEP ---
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Step Progress Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "QUESTION ${currentStep.questionNumber} OF ${currentStep.totalEstimatedSteps}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF0284C7)
                                )

                                if (stepHistory.isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            val prevStep = stepHistory.last()
                                            stepHistory = stepHistory.dropLast(1)
                                            currentStepId = prevStep
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Previous Question", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { currentStep.questionNumber.toFloat() / currentStep.totalEstimatedSteps.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFF0284C7),
                                trackColor = Color(0xFF1E293B)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Question Title & Subtitle
                            Text(
                                text = currentStep.questionTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            if (currentStep.questionSubtitle != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentStep.questionSubtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            // Tech Tip Box
                            if (currentStep.techTip != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "MECHANIC TECH TIP",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFFFD700)
                                            )
                                            Text(
                                                text = currentStep.techTip,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Question Answer Options
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(currentStep.options) { option ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable {
                                                if (option.finalDiagnosis != null) {
                                                    activeDiagnosis = option.finalDiagnosis
                                                } else if (option.nextStepId != null) {
                                                    stepHistory = stepHistory + currentStepId
                                                    currentStepId = option.nextStepId
                                                }
                                            }
                                            .testTag("wizard_option_${option.title.take(15)}"),
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = Color(0xFF0F172A),
                                                shape = CircleShape,
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = option.icon,
                                                        contentDescription = null,
                                                        tint = Color(0xFF38BDF8),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = option.title,
                                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                                if (option.subtitle != null) {
                                                    Text(
                                                        text = option.subtitle,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }
                                            }

                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Diagnosis Result Detail View Composable ---

@Composable
private fun DiagnosisResultView(
    diagnosis: WizardDiagnosis,
    onRestart: () -> Unit,
    onNavigateToComponent: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Confidence Banner Header
            Surface(
                color = Color(0xFF0F2942),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, diagnosis.severity.color),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = diagnosis.severity.color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, diagnosis.severity.color)
                        ) {
                            Text(
                                text = diagnosis.severity.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = diagnosis.severity.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFF0284C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${diagnosis.confidencePercent}% MATCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = diagnosis.issueTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Target Component: ${diagnosis.componentName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF38BDF8)
                    )

                    if (diagnosis.obdCodes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Associated OBD Codes:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                            diagnosis.obdCodes.forEach { code ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                                ) {
                                    Text(
                                        text = code,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diagnostic Summary
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DIAGNOSTIC SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = diagnosis.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        // Verification Procedure
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOW TO VERIFY / CONFIRM ISSUE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFFD700)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = diagnosis.verificationProcedure,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        // Parts & Part Numbers
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RECOMMENDED REPLACEMENT PARTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    diagnosis.recommendedParts.forEach { part ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = part, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }

                    if (diagnosis.fordPartNumber != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ford OEM Part #: ${diagnosis.fordPartNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }

        // Time & Difficulty Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "ESTIMATED TIME", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text(text = diagnosis.estimatedRepairTime, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "DIFFICULTY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text(text = diagnosis.difficultyLevel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Button(
                    onClick = { onNavigateToComponent(diagnosis.componentId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wizard_locate_3d_btn")
                ) {
                    Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Locate in Interactive 3D Model", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onRestart,
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restart Diagnostic Decision Tree")
                }
            }
        }
    }
}
