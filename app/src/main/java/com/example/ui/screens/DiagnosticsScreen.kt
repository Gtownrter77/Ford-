package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportTracData
import com.example.model.ChatMessage
import com.example.model.DiagnosticSymptomCategory
import com.example.ui.components.DiagnosticFlowDialog
import com.example.ui.components.ForscanDialog
import com.example.ui.components.GeminiChatView
import com.example.ui.components.SymptomTroubleshootingDialog

enum class DiagnosticMode {
    GEMINI_CHAT,
    GUIDED_FLOWS
}

@Composable
fun DiagnosticsScreen(
    chatMessages: List<ChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    onRetryLastQuery: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(DiagnosticMode.GEMINI_CHAT) }
    var activeCategoryForDialog by remember { mutableStateOf<DiagnosticSymptomCategory?>(null) }
    var showForscanDialog by remember { mutableStateOf(false) }
    var showAcousticDialog by remember { mutableStateOf(false) }
    var showSymptomTroubleshootingDialog by remember { mutableStateOf(false) }
    var showDiagnosticWizardDialog by remember { mutableStateOf(false) }
    var showAcSystemWorkbench by remember { mutableStateOf(false) }

    if (showAcSystemWorkbench) {
        com.example.ui.components.AcSystemWorkbenchDialog(
            onDismiss = { showAcSystemWorkbench = false },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    if (showDiagnosticWizardDialog) {
        com.example.ui.components.DiagnosticWizardDialog(
            onDismiss = { showDiagnosticWizardDialog = false },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    if (showSymptomTroubleshootingDialog) {
        SymptomTroubleshootingDialog(
            onDismiss = { showSymptomTroubleshootingDialog = false },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    if (showAcousticDialog) {
        com.example.ui.components.AcousticSoundDiagnosticDialog(
            onDismiss = { showAcousticDialog = false },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    if (activeCategoryForDialog != null) {
        DiagnosticFlowDialog(
            category = activeCategoryForDialog!!,
            onDismiss = { activeCategoryForDialog = null },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    if (showForscanDialog) {
        ForscanDialog(
            onDismiss = { showForscanDialog = false },
            onNavigateToComponent = onNavigateToComponent
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Mode Selector Tab Bar & Listen To Engine Action Banner
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column {
                // Listen to Engine AI Feature Banner
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clickable { showAcousticDialog = true }
                        .testTag("btn_listen_to_engine_banner"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF00F0FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color(0xFFEF4444))
                            ) {
                                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "LISTEN TO ENGINE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.8.sp),
                                        color = Color(0xFF00F0FF)
                                    )
                                    Surface(
                                        color = Color(0xFF0284C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "GEMINI AI",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Record mic audio & analyze 4.0L V6 failure signatures",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = { showAcousticDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Listen",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gemini Chat Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeMode = DiagnosticMode.GEMINI_CHAT }
                            .testTag("mode_gemini_chat"),
                        color = if (activeMode == DiagnosticMode.GEMINI_CHAT) Color(0xFF0284C7) else Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (activeMode == DiagnosticMode.GEMINI_CHAT) Color.White else Color(0xFF38BDF8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini AI Mechanic",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (activeMode == DiagnosticMode.GEMINI_CHAT) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Guided Flows & OBD Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeMode = DiagnosticMode.GUIDED_FLOWS }
                            .testTag("mode_guided_flows"),
                        color = if (activeMode == DiagnosticMode.GUIDED_FLOWS) Color(0xFF0284C7) else Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (activeMode == DiagnosticMode.GUIDED_FLOWS) Color.White else Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guided Flows & OBD",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (activeMode == DiagnosticMode.GUIDED_FLOWS) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        when (activeMode) {
            DiagnosticMode.GEMINI_CHAT -> {
                GeminiChatView(
                    messages = chatMessages,
                    isThinking = isThinking,
                    onSendMessage = onSendMessage,
                    onClearChat = onClearChat,
                    onNavigateToComponent = onNavigateToComponent,
                    onRetryLastQuery = onRetryLastQuery,
                    modifier = Modifier.weight(1f)
                )
            }

            DiagnosticMode.GUIDED_FLOWS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // A/C Workbench Hero Card — targeted, safety-first climate-control diagnosis.
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showAcSystemWorkbench = true }
                                .testTag("ac_workbench_hero_card"),
                            color = Color(0xFF0B2840),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(8.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.AcUnit,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(8.dp).size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "A/C Workbench",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Start here for warm air, weak airflow, clutch, or idle-cooling issues",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF7DD3FC)
                                            )
                                        }
                                    }
                                    Surface(color = Color(0xFF0C4A6E), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "PRIORITY",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                            color = Color(0xFFBAE6FD),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "A focused, safety-first diagnostic path for the Sport Trac A/C system. It separates cabin airflow, electrical control, compressor operation, condenser airflow, and refrigerant service so no one has to guess which repair comes first.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE0F2FE)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showAcSystemWorkbench = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.fillMaxWidth().testTag("open_ac_workbench_btn")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open A/C Workbench", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    // Diagnostic Decision Tree Wizard Hero Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showDiagnosticWizardDialog = true }
                                .testTag("diagnostic_wizard_hero_card"),
                            color = Color(0xFF0F2D4A),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF0284C7))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFF0284C7),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "INTERACTIVE WIZARD",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Diagnostic Decision Tree Wizard",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Guided Symptom & Cause Resolution",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                    }

                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Step-by-step decision trees for engine misfires (P0300, PCV elbow), suspension clunks & wheel bearings, 5R55E 2-3 shift flares, 4x4 flashing lights, and thermostat leaks with confidence scoring & OEM parts.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { showDiagnosticWizardDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Launch Diagnostic Wizard", color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    // Service Manual Symptom Troubleshooting Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showSymptomTroubleshootingDialog = true }
                                .testTag("service_manual_symptom_card"),
                            color = Color(0xFF1E1B18),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFFF6F00))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFFF6F00),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "FSM & TSB",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Symptom Troubleshooting Flow",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "2004 Sport Trac Service Manual Matches",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFFF6F00)
                                            )
                                        }
                                    }

                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFFF6F00))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Select one or multiple symptoms from an extensive list across Engine, 5R55E Transmission, ControlTrac 4WD, Cooling, and Brakes to pinpoint exact potential causes, OBD codes, and step-by-step diagnostic procedures.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { showSymptomTroubleshootingDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Select Symptoms & Analyze Causes", color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    // FORScan Banner Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showForscanDialog = true }
                                .testTag("forscan_open_card"),
                            color = Color(0xFF0F2238),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF0284C7))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFF0284C7),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "FORScan",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "FORScan & OBD-II Scanner",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Live PID Telemetry • DTC Log Match",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                    }

                                    Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF38BDF8))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Connect ELM327 / OBDLink Bluetooth, monitor 4.0L SOHC live PIDs (RPM, Coolant ECT, Fuel Trims STFT/LTFT), or parse FORScan DTC logs directly to target parts in 3D model.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { showForscanDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Usb, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open FORScan OBD Analyzer")
                                }
                            }
                        }
                    }

                    // Acoustic Vehicle Sound Scanner Banner Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showAcousticDialog = true }
                                .testTag("acoustic_open_card"),
                            color = Color(0xFF1E1035),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFF00F0FF),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "ACOUSTIC AI",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = Color(0xFF0F172A),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Acoustic Sound & Noise Analyzer",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "245,000+ Verified Vehicle Sound DB • 95%+ Match",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF00F0FF)
                                            )
                                        }
                                    }

                                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF00F0FF))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Record any strange sound from your truck. Our AI spectral frequency analyzer compares your audio to 245,000+ verified recordings—from flat tire thumps, blown turn signal relays, and fuel pump hums to radio alternator ground whistles with a 95%+ confidence match guarantee!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { showAcousticDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Record & Compare Vehicle Sound", color = Color(0xFF0F172A), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    // Section Title
                    item {
                        Text(
                            text = "OBSERVED SYMPTOM CATEGORIES",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color(0xFF38BDF8)
                        )
                    }

                    items(SportTracData.diagnosticCategories) { cat ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { activeCategoryForDialog = cat }
                                .testTag("diag_cat_${cat.id}"),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(cat.system.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat.system.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = cat.system.color
                                        )
                                    }

                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = cat.categoryName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Common Symptoms Tags
                                cat.commonSymptoms.forEach { sym ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(sym, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { activeCategoryForDialog = cat },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("start_diag_btn")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Guided Diagnosis")
                                }
                            }
                        }
                    }

                    // Classic 4.0L SOHC Common Faults Reference Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF0F2238),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF0284C7))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "CLASSIC 2004 SPORT TRAC KNOWN ISSUES",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "1. P0171 / P0174 Lean Codes: Caused by shrinking intake manifold gaskets or PCV vacuum elbow leak.\n" +
                                            "2. Cold Startup Engine Rattle: Caused by worn 4.0L SOHC timing chain hydraulic tensioner seals.\n" +
                                            "3. Coolant Valley Leak: Caused by cracked seam on factory composite plastic thermostat housing.\n" +
                                            "4. 2-3 Shift Flare (5R55E Trans): Caused by blown valve body separator plate gasket near EPC solenoid.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
