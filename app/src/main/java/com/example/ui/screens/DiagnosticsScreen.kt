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
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(DiagnosticMode.GEMINI_CHAT) }
    var activeCategoryForDialog by remember { mutableStateOf<DiagnosticSymptomCategory?>(null) }
    var showForscanDialog by remember { mutableStateOf(false) }

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
        // Mode Selector Tab Bar
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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

        when (activeMode) {
            DiagnosticMode.GEMINI_CHAT -> {
                GeminiChatView(
                    messages = chatMessages,
                    isThinking = isThinking,
                    onSendMessage = onSendMessage,
                    onClearChat = onClearChat,
                    onNavigateToComponent = onNavigateToComponent,
                    modifier = Modifier.weight(1f)
                )
            }

            DiagnosticMode.GUIDED_FLOWS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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
