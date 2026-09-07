package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class AcDiagnosticPath(
    val id: String,
    val title: String,
    val symptom: String,
    val componentId: String,
    val urgency: String,
    val tools: String,
    val steps: List<String>,
    val stopCondition: String
)

private data class AcPracticeStep(
    val title: String,
    val goal: String,
    val componentId: String,
    val modelAction: String,
    val realVehicleTransfer: String,
    val safetyBoundary: String
)

private val acPracticeSteps = listOf(
    AcPracticeStep(
        title = "Map the A/C cooling path",
        goal = "Start at the front of the truck and identify the major heat-transfer and cabin-air components in the correct order.",
        componentId = "ac_condenser_lines_3d",
        modelAction = "Open the condenser/line assembly in the 3D model. Rotate it, identify the front-of-radiator location, then return to the Workbench.",
        realVehicleTransfer = "On the truck, inspect through the grille only with the engine OFF and cool. Look for debris or damaged fins; do not loosen lines or fittings.",
        safetyBoundary = "No refrigerant fitting is opened during this step. This is visual orientation practice only."
    ),
    AcPracticeStep(
        title = "Separate blower airflow from refrigerant cooling",
        goal = "Practice the first decision: can the HVAC system move cabin air before asking whether the A/C circuit can cool it?",
        componentId = "hvac_blower_motor_3d",
        modelAction = "Inspect the blower motor and resistor assembly. Practice finding the electrical connector, mounting screws, and safe tool list in the model.",
        realVehicleTransfer = "On the truck, test all fan speeds first. A failed blower or resistor is a cabin-air problem, not a reason to charge the A/C system.",
        safetyBoundary = "Disconnect battery power before unplugging or inspecting an electrical connector closely."
    ),
    AcPracticeStep(
        title = "Rehearse the protected compressor command path",
        goal = "Understand that clutch operation depends on a requested A/C command, electrical protection, and pressure-protection logic.",
        componentId = "ac_service_ports_controls_3d",
        modelAction = "Review the service-port/pressure-control model. Identify the fuse/relay reference, clutch connector, and the protected switch path without bypassing anything.",
        realVehicleTransfer = "Use the owner-manual fuse/relay diagram and safe visual observations to document whether the clutch engages. Record results rather than forcing engagement.",
        safetyBoundary = "Never jumper a pressure switch, relay, or clutch connector to force the compressor on."
    ),
    AcPracticeStep(
        title = "Practice compressor access and fastener handling",
        goal = "Learn the access order around the belt, compressor, manifold connection, and hardware before touching the real engine bay.",
        componentId = "ac_compressor",
        modelAction = "Use the exploded model and Mentor steps to identify the belt path, compressor mounting hardware, connector, manifold block, seals, and the order in which they are handled.",
        realVehicleTransfer = "With the engine OFF, use the model to confirm tool placement and access from the passenger-side area. Do not disconnect the manifold or remove the compressor until recovery is arranged.",
        safetyBoundary = "The real refrigerant circuit remains sealed during rehearsal. Recovery, evacuation, leak testing, and charging are not practice steps."
    ),
    AcPracticeStep(
        title = "Recognize the sealed-system handoff",
        goal = "Know when the practice sequence ends and qualified refrigerant-service work begins.",
        componentId = "ac_evaporator_accumulator_3d",
        modelAction = "Inspect the accumulator, evaporator, and fixed-orifice assembly. Review the Mentor warnings and the visible service hardware without starting a removal procedure.",
        realVehicleTransfer = "If diagnosis points to a leak, contamination, compressor failure, or an opened line, document symptoms and hand off recovery/evacuation/charging work to a properly equipped A/C technician.",
        safetyBoundary = "Use the under-hood label and Ford service procedure for exact charge and oil information. Do not estimate from generic pressures or can instructions."
    )
)

private val acDiagnosticPaths = listOf(
    AcDiagnosticPath(
        id = "no_cabin_airflow",
        title = "No or weak air from the vents",
        symptom = "The fan is silent, only works on some speeds, or airflow is weak in every mode.",
        componentId = "hvac_blower_motor_3d",
        urgency = "Electrical / cabin-air path first",
        tools = "Flashlight, trim tools, multimeter if qualified",
        steps = listOf(
            "With the engine running in a safe, open area, switch the HVAC fan through every speed and note which speeds work.",
            "Check the owner-manual fuse chart and the appropriate HVAC/blower fuse before removing trim. Replace only with the specified fuse rating.",
            "Listen for the blower motor behind the passenger-side dash. A working motor with no airflow points toward a blocked inlet, mode-door, or housing issue.",
            "If only one or two speeds work, inspect the blower resistor/connector area for heat damage after disconnecting the battery.",
            "Use the model link below to inspect the blower and resistor assembly before removing anything on the truck."
        ),
        stopCondition = "Stop if a fuse repeatedly opens, wiring smells hot, or a connector is melted. Electrical repair should be diagnosed before any A/C refrigerant work."
    ),
    AcDiagnosticPath(
        id = "clutch_does_not_engage",
        title = "Strong airflow, but compressor clutch never engages",
        symptom = "The dash blower works, but the center clutch plate on the A/C compressor does not pull in when A/C is requested.",
        componentId = "ac_compressor",
        urgency = "Electrical command or pressure-protection path",
        tools = "Safety glasses, flashlight, owner manual, multimeter only if experienced",
        steps = listOf(
            "Park outdoors, set the parking brake, keep loose clothing away from belts, and never reach near the compressor pulley with the engine running.",
            "Set MAX A/C, blower high, and a low temperature setting. Observe from a safe distance whether the clutch hub changes from stationary to turning with the pulley.",
            "Check the A/C-related fuse and relay locations using the exact owner-manual diagram for this truck. Do not bypass a relay or pressure switch.",
            "With the engine OFF and battery disconnected where required, inspect the clutch connector and visible wiring for corrosion, broken insulation, or oil-stained residue.",
            "If basic electrical checks are normal, stop before opening the refrigerant circuit. A shop can confirm pressure-switch logic, PCM command, and refrigerant charge with the proper equipment."
        ),
        stopCondition = "Do not jumper pressure switches, vent refrigerant, or open lines. Those shortcuts can damage the compressor, cause injury, and defeat the system’s pressure protection."
    ),
    AcDiagnosticPath(
        id = "rapid_clutch_cycling",
        title = "Compressor clutch clicks rapidly or cools only briefly",
        symptom = "The clutch turns on and off every few seconds, or cold air fades almost immediately.",
        componentId = "ac_compressor",
        urgency = "Refrigerant protection / clutch / airflow diagnosis",
        tools = "Flashlight, thermometer for vent comparison, professional A/C service equipment for pressure testing",
        steps = listOf(
            "Confirm the blower is moving a strong, steady stream of air first; a weak blower can make normal A/C feel ineffective.",
            "Look for oily dirt around the compressor manifold, hose crimps, condenser, and accumulator. Treat it only as a clue—not proof of a leak.",
            "Compare vent temperature at idle and after a short drive, without touching refrigerant fittings or moving parts.",
            "If clutch engagement changes only after the engine bay has heat-soaked, document that behavior for the A/C technician; it may help separate an electrical/clutch issue from charge protection.",
            "Have a certified shop recover, evacuate, leak-test, and charge the system by the under-hood label and Ford procedure rather than using a single low-side gauge or guessing by pressure."
        ),
        stopCondition = "Do not add refrigerant until a leak/charge diagnosis is complete. A low charge usually has a cause, and overcharge can also stop cooling or damage components."
    ),
    AcDiagnosticPath(
        id = "cold_moving_warm_idle",
        title = "Cold while driving, warm at idle or in traffic",
        symptom = "A/C improves at road speed but turns warm when stopped, especially in extreme heat.",
        componentId = "ac_condenser_lines_3d",
        urgency = "Condenser heat-rejection and airflow path",
        tools = "Flashlight, non-contact visual inspection only around a running engine",
        steps = listOf(
            "With the engine cool and OFF, inspect the condenser face through the grille for leaves, plastic bags, bent fins, or heavy debris blocking airflow.",
            "Inspect the fan shroud and visible mechanical-fan area for missing shrouds, damaged blades, or obvious obstruction. Keep hands completely clear once the engine is running.",
            "Watch the engine-temperature gauge. If the truck also runs hot at idle, treat cooling-system airflow as a priority before chasing refrigerant charge.",
            "Document whether the A/C is cooler at 40–50 mph than at a stoplight; that strongly helps a shop target condenser airflow, fan-clutch performance, or high-side pressure issues.",
            "Use the model link to review the condenser/lines and radiator/fan assemblies together."
        ),
        stopCondition = "Stop testing if engine temperature rises above normal, steam appears, or there is a coolant smell/leak. Engine cooling takes priority over cabin A/C."
    ),
    AcDiagnosticPath(
        id = "one_side_warm_or_wrong_mode",
        title = "Air is cold from some vents, warm from others, or comes from the wrong outlet",
        symptom = "The compressor may be working, but temperature or mode delivery inside the cab is inconsistent.",
        componentId = "hvac_blend_door_actuator_3d",
        urgency = "Cabin HVAC door / actuator path",
        tools = "Flashlight, trim tools, service information",
        steps = listOf(
            "Move temperature and mode controls slowly through their full range and listen for clicking, ratcheting, or a change in airflow direction behind the dash.",
            "Compare left, center, and right vent output after the system has run for several minutes; record which vents differ.",
            "If clicking occurs or the outlet does not change, inspect the blend-door/mode-door actuator procedure before removing dash trim.",
            "Check for a loose electrical connector only with the ignition OFF. Avoid forcing climate-control knobs or actuator gears.",
            "Use the model link to review the blend-door actuator and HVAC case before beginning interior disassembly."
        ),
        stopCondition = "Do not remove refrigerant components for a cabin-air distribution problem; this path is usually inside the HVAC case or its controls."
    ),
    AcDiagnosticPath(
        id = "noise_or_visible_leak",
        title = "Grinding noise, smoke, or a visible/oily leak",
        symptom = "A/C operation makes a harsh noise, the belt area smokes, or oily residue is visible near a refrigerant fitting.",
        componentId = "ac_compressor",
        urgency = "Stop and prevent secondary damage",
        tools = "Flashlight only; professional recovery equipment for refrigerant work",
        steps = listOf(
            "Turn A/C OFF immediately if there is grinding, smoke, a burning-belt smell, or a severe metal noise.",
            "With the engine OFF and cool, visually inspect the belt path and compressor area without touching fittings, hoses, or the clutch face.",
            "Photograph oil-stained areas and note the sound, time, and conditions. This is useful evidence for a professional diagnosis.",
            "If a compressor has failed internally, the Ford service procedure calls for contamination control: flushing or filtering as applicable, a new orifice element, and a new suction accumulator as directed.",
            "Arrange professional recovery and repair before any line is opened."
        ),
        stopCondition = "Do not drive with a smoking belt or a seized compressor. Do not vent or intentionally release refrigerant."
    )
)

/**
 * Dedicated A/C symptom workbench. It handles safe, non-invasive diagnosis
 * first and deliberately separates it from refrigerant recovery/evacuation/
 * charging work that requires correct equipment and professional handling.
 */
@Composable
fun AcSystemWorkbenchDialog(
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPath by remember { mutableStateOf<AcDiagnosticPath?>(null) }
    var practiceStepIndex by remember { mutableStateOf(-1) }
    var practiceRehearsed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("ac_system_workbench_dialog"),
        containerColor = Color(0xFF071B2A),
        titleContentColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF0EA5E9), shape = RoundedCornerShape(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(9.dp).size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "A/C WORKBENCH",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.8.sp),
                        color = Color.White
                    )
                    Text(
                        text = "2004 Sport Trac • Diagnose safely before replacing parts",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7DD3FC)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AcHeatSafetyBanner()
                if (practiceStepIndex >= 0) {
                    AcPracticeTrainer(
                        stepIndex = practiceStepIndex,
                        isRehearsed = practiceRehearsed,
                        onMarkRehearsed = { practiceRehearsed = true },
                        onNavigateToComponent = onNavigateToComponent
                    )
                } else if (selectedPath == null) {
                    AcPracticeEntryCard(onStartPractice = {
                        practiceStepIndex = 0
                        practiceRehearsed = false
                    })
                    AcPartsFinderGuidanceCard()
                    Text(
                        text = "START WITH WHAT THE TRUCK IS DOING",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp),
                        color = Color(0xFFBAE6FD)
                    )
                    Text(
                        text = "Choose the closest symptom. The workbench keeps airflow, electrical, compressor, and refrigerant-service questions separate so the first repair is based on evidence rather than guesswork.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0F2FE)
                    )
                    acDiagnosticPaths.forEach { path ->
                        AcPathSelectionCard(path = path, onSelect = { selectedPath = path })
                    }
                } else {
                    val path = selectedPath!!
                    Surface(
                        color = Color(0xFF0C2B3F),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(path.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(path.symptom, style = MaterialTheme.typography.bodySmall, color = Color(0xFFBAE6FD))
                            Surface(color = Color(0xFF0369A1), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = path.urgency.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text("Tools: ${path.tools}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFDE68A))
                        }
                    }

                    Text("INSPECTION PATH", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp), color = Color(0xFFBAE6FD))
                    path.steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(7.dp)) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(9.dp))
                            Text(step, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE0F2FE), modifier = Modifier.weight(1f))
                        }
                    }

                    Surface(color = Color(0xFF7F1D1D), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFEF4444))) {
                        Row(modifier = Modifier.padding(11.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(path.stopCondition, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFFFECACA))
                        }
                    }

                    Surface(color = Color(0xFF172554), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFF38BDF8))) {
                        Row(modifier = Modifier.padding(11.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Refrigerant recovery, evacuation, leak testing, and charging require correct A/C equipment and should be handled by a qualified MVAC technician. Use the under-hood label and Ford service information—not a guessed pressure target—for the exact charge specification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDBEAFE)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when {
                practiceStepIndex >= 0 -> {
                    val isLast = practiceStepIndex == acPracticeSteps.lastIndex
                    Button(
                        enabled = practiceRehearsed,
                        onClick = {
                            if (isLast) {
                                practiceStepIndex = -1
                            } else {
                                practiceStepIndex += 1
                                practiceRehearsed = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isLast) Color(0xFF10B981) else Color(0xFF0284C7)),
                        modifier = Modifier.testTag("ac_practice_next_btn")
                    ) {
                        Icon(if (isLast) Icons.Default.CheckCircle else Icons.Default.NavigateNext, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            when {
                                !practiceRehearsed -> "Mark 3D Rehearsal First"
                                isLast -> "Complete Practice Review"
                                else -> "Confirm Practice Step"
                            }
                        )
                    }
                }
                selectedPath == null -> {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.testTag("ac_workbench_close_btn")
                    ) { Text("Close") }
                }
                else -> {
                    Button(
                        onClick = { onNavigateToComponent(selectedPath!!.componentId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.testTag("ac_workbench_view_component_btn")
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("View Related 3D Part")
                    }
                }
            }
        },
        dismissButton = {
            when {
                practiceStepIndex >= 0 -> {
                    OutlinedButton(
                        onClick = {
                            practiceStepIndex = -1
                            practiceRehearsed = false
                        },
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.testTag("ac_practice_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Exit Practice")
                    }
                }
                selectedPath != null -> {
                    OutlinedButton(
                        onClick = { selectedPath = null },
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.testTag("ac_workbench_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("All Symptoms")
                    }
                }
            }
        }
    )
}

@Composable
private fun AcHeatSafetyBanner() {
    Surface(
        color = Color(0xFF4C1D0B),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFF97316))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text("EXTREME-HEAT CHECK", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFFDE68A))
                Text(
                    text = "Work in shade when possible, take water breaks, and never test the truck in an enclosed garage. Stop if the engine temperature rises, the belt smokes, or anyone becomes unwell from the heat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFEDD5)
                )
            }
        }
    }
}

@Composable
private fun AcPathSelectionCard(path: AcDiagnosticPath, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("ac_path_${path.id}"),
        color = Color(0xFF0F2A3C),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1D4ED8))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color(0xFF0369A1), shape = RoundedCornerShape(8.dp)) {
                Icon(
                    Icons.Default.AcUnit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(7.dp).size(17.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(path.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(path.symptom, style = MaterialTheme.typography.labelSmall, color = Color(0xFFBAE6FD))
            }
            Icon(Icons.Default.NavigateNext, contentDescription = "Open diagnostic path", tint = Color(0xFF7DD3FC))
        }
    }
}

@Composable
private fun AcPracticeEntryCard(onStartPractice: () -> Unit) {
    Surface(
        color = Color(0xFF064E3B),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, Color(0xFF34D399)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStartPractice)
            .testTag("ac_practice_entry_card")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF10B981), shape = RoundedCornerShape(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(7.dp).size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.size(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRACTICE ON THE MODEL FIRST",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 0.7.sp),
                        color = Color.White
                    )
                    Text(
                        text = "Five guided rehearsal steps before working on the truck",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFA7F3D0)
                    )
                }
                Icon(Icons.Default.NavigateNext, contentDescription = "Start practice", tint = Color.White)
            }
            Text(
                text = "Rehearse the cooling path, blower checks, protected clutch-command logic, compressor access, hardware sequence, and sealed-system handoff. Each step links to the matching 3D assembly so the order is familiar before any real work starts.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFDCFCE7)
            )
        }
    }
}

@Composable
private fun AcPracticeTrainer(
    stepIndex: Int,
    isRehearsed: Boolean,
    onMarkRehearsed: () -> Unit,
    onNavigateToComponent: (String) -> Unit
) {
    val step = acPracticeSteps[stepIndex.coerceIn(0, acPracticeSteps.lastIndex)]
    Surface(
        color = Color(0xFF0F2A3C),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, Color(0xFF34D399)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = Color(0xFF10B981), shape = RoundedCornerShape(7.dp)) {
                    Text(
                        text = "PRACTICE ${stepIndex + 1} / ${acPracticeSteps.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "MODEL FIRST",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.7.sp),
                    color = Color(0xFF6EE7B7)
                )
            }

            Text(step.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Text(step.goal, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD1FAE5))

            HorizontalDivider(color = Color(0xFF1D4ED8))

            PracticeSection(
                heading = "1. REHEARSE ON THE 3D MODEL",
                body = step.modelAction,
                color = Color(0xFF7DD3FC)
            )
            Button(
                onClick = { onNavigateToComponent(step.componentId) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ac_practice_view_model_${stepIndex + 1}")
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Open Matching 3D Assembly")
            }
            OutlinedButton(
                onClick = onMarkRehearsed,
                border = BorderStroke(1.dp, if (isRehearsed) Color(0xFF34D399) else Color(0xFF7DD3FC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ac_practice_mark_rehearsed_${stepIndex + 1}")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text(if (isRehearsed) "3D Rehearsal Recorded" else "Mark 3D Step Rehearsed")
            }

            PracticeSection(
                heading = "2. TRANSFER ONLY AFTER PRACTICE",
                body = step.realVehicleTransfer,
                color = Color(0xFFFDE68A)
            )

            Surface(
                color = Color(0xFF7F1D1D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444))
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(7.dp))
                    Column {
                        Text("PRACTICE BOUNDARY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black), color = Color(0xFFFCA5A5))
                        Text(step.safetyBoundary, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFECACA))
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeSection(heading: String, body: String, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = heading,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.5.sp),
            color = color
        )
        Text(body, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE0F2FE))
    }
}

@Composable
private fun AcPartsFinderGuidanceCard() {
    Surface(
        color = Color(0xFF2A1D06),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ac_parts_finder_guidance_card")
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(color = Color(0xFFF59E0B), shape = RoundedCornerShape(8.dp)) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFF1F2937),
                    modifier = Modifier.padding(7.dp).size(18.dp)
                )
            }
            Spacer(modifier = Modifier.size(9.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "CHEAP PARTS FINDER — USE AFTER CONFIRMATION",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.45.sp),
                    color = Color(0xFFFDE68A)
                )
                Text(
                    text = "The Parts Finder already compares the build’s compatible A/C catalog entries and cost snapshots. Use it after the sound comparison, A/C Workbench, and 3D rehearsal point to one component. Sort by cost, but verify VIN fitment, included seals, warranty, core charge, and current seller price before buying.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFF7ED)
                )
            }
        }
    }
}
