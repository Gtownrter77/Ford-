package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class DiagnosticStage(val title: String, val purpose: String, val checks: List<String>)

private val stages = listOf(
    DiagnosticStage("1. Roadside safety gate", "Prevent engine damage and protect the person before testing.", listOf(
        "If the oil-pressure warning is on, the temperature gauge is in the red, there is smoke, a major leak, or a loud metallic rattle: shut the engine off and tow it.",
        "Do not open refrigerant lines, reach near a running belt/fan, crawl under the truck roadside, or run the engine with low oil.",
        "Photograph warning lamps, leaks, belt area, and the location/conditions of the noise before moving the vehicle."
    )),
    DiagnosticStage("2. No-start / cold visual checks", "Collect evidence without guessing at the failed part.", listOf(
        "With the engine cold and off, check oil level on level ground, coolant level only at the correct safe condition, belt condition, and obvious oil/coolant/refrigerant residue.",
        "Record whether the rattle is cold-start only, warm idle, acceleration, or continuous; record duration and whether it follows engine speed.",
        "Check battery voltage, fuses, compressor-clutch connector, blower fuse, and visible harness damage without bypassing protections."
    )),
    DiagnosticStage("3. Oil-pressure and oiling gate", "Rule out a lubrication problem before timing-chain conclusions.", listOf(
        "Confirm oil level and correct viscosity/filter history. Do not infer pressure from the dash gauge alone.",
        "Install a mechanical oil-pressure gauge at the approved test port and compare cold and fully-warm readings with the exact 2004 4.0L service specification.",
        "If pressure is below specification, stop diagnosis: inspect filter, sender, drain plug/pan, pickup screen, pump, relief path, and bearing condition. Do not continue running the engine."
    )),
    DiagnosticStage("4. Timing-chain rattle gate", "Separate chain/tensioner noise from accessory or valvetrain noise.", listOf(
        "Use a mechanic’s stethoscope only as a localization aid; compare front cover, valve-cover, accessory, and oil-pan areas with the engine running only when safe.",
        "Scan for cam/crank correlation and misfire codes, then inspect live data; a code or sound alone does not prove chain failure.",
        "If oil pressure is correct but rattle persists, inspect guides, tensioners, chain slack, timing marks, and cover debris using the exact engine procedure. A jumped chain requires no-driving/tow status."
    )),
    DiagnosticStage("5. A/C and heat separation", "Identify airflow, electrical, refrigerant, and coolant-flow faults in the right order.", listOf(
        "For warm air: verify blower airflow, mode/temperature door operation, compressor-clutch command, fuse/relay, belt, condenser airflow, and visible leaks.",
        "Use approved recovery equipment and manifold gauges; do not vent refrigerant or add refrigerant by guess. Leak-test, evacuate, and charge only to the under-hood label specification.",
        "For no heat: verify coolant level/temperature, heater-hose temperature difference, thermostat behavior, heater-core restriction, blend door, and vacuum/control operation. Never open a hot cooling system."
    )),
    DiagnosticStage("6. Release decision", "Turn measurements into an actionable repair decision.", listOf(
        "Green: oil pressure meets specification, no overheating/leak, no severe rattle, and A/C/heat tests identify a contained fault—repair and retest.",
        "Yellow: symptoms remain ambiguous or measurements are missing—do not authorize parts replacement based on a confidence score; obtain the missing test.",
        "Red: low oil pressure, severe timing noise, overheating, active leak, or unsafe roadside location—tow to a qualified technician."
    ))
)

@Composable
fun CriticalDiagnosticDialog(onDismiss: () -> Unit, onNavigateToComponent: (String) -> Unit) {
    var completed by remember { mutableStateOf(setOf<Int>()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("critical_diagnostic_dialog"),
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        title = {
            Column {
                Row { Icon(Icons.Default.Warning, null, tint = Color(0xFFFFB020)); Spacer(Modifier.width(8.dp)); Text("Critical Diagnostic Protocol", fontWeight = FontWeight.Bold) }
                Text("A/C • Heat • Oil Pressure • Timing Chain", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7DD3FC))
            }
        },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(color = Color(0xFF7F1D1D), shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, Color(0xFFFCA5A5))) {
                    Text("STOP RULE: oil warning, red temperature gauge, major leak, smoke, or loud metallic rattle = engine off and tow.", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
                stages.forEachIndexed { index, stage ->
                    val done = index in completed
                    Surface(color = if (done) Color(0xFF12352A) else Color(0xFF1E293B), shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, if (done) Color(0xFF34D399) else Color(0xFF334155))) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stage.title, color = Color.White, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { completed = if (done) completed - index else completed + index }) { Text(if (done) "DONE" else "MARK DONE", fontSize = 10.sp) }
                            }
                            Text(stage.purpose, color = Color(0xFF93C5FD), style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            stage.checks.forEach { Text("• $it", color = Color(0xFFE2E8F0), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp)) }
                        }
                    }
                }
                Text("This workflow guides safe triage and technician testing. It does not certify a diagnosis, replace the factory manual, or authorize refrigerant/oil-pressure/timing repairs without measurements.", color = Color(0xFFFBBF24), style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = Color(0xFF7DD3FC)) } },
        dismissButton = { TextButton(onClick = { onNavigateToComponent("ac_compressor"); onDismiss() }) { Icon(Icons.Default.Build, null); Spacer(Modifier.width(4.dp)); Text("Open Model") } }
    )
}
