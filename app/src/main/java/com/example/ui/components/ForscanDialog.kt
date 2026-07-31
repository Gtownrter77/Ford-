package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.ForscanData
import com.example.model.FordModule
import com.example.model.ForscanDtcCode
import com.example.model.ForscanPidData
import kotlinx.coroutines.delay

enum class ForscanSubTab(val label: String) {
    LIVE_PIDS("Live PIDs & Gauges"),
    DTC_CODES("DTC Fault Scanner"),
    MODULES("Ford Modules")
}

@Composable
fun ForscanDialog(
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(ForscanSubTab.LIVE_PIDS) }
    var isLiveStreaming by remember { mutableStateOf(true) }
    var pidsList by remember { mutableStateOf(ForscanData.defaultPids) }
    var pasteLogText by remember { mutableStateOf("") }
    var selectedDtc by remember { mutableStateOf<ForscanDtcCode?>(ForscanData.knownSportTracDtcs[0]) }
    var activeDtcsList by remember { mutableStateOf(ForscanData.knownSportTracDtcs) }
    var isConnectedToObd by remember { mutableStateOf(true) }

    // Live PID value simulation loop
    LaunchedEffect(isLiveStreaming) {
        while (isLiveStreaming) {
            delay(800)
            pidsList = pidsList.map { pid ->
                val jitter = (Math.random() - 0.5) * (if (pid.shortName == "RPM") 30.0 else if (pid.shortName == "ECT") 0.5 else 0.2)
                val newDouble = (pid.currentValue + jitter).coerceIn(pid.minVal, pid.maxVal)
                pid.copy(currentValue = Math.round(newDouble * 10.0) / 10.0)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0B132B),
        titleContentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .testTag("forscan_integration_dialog"),
        title = {
            Column {
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
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OBD-II & FORScan Integration",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "2004 Sport Trac • 4.0L SOHC V6",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Connection Status Strip
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isConnectedToObd) Color(0xFF10B981) else Color(0xFFEF4444))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnectedToObd) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isConnectedToObd) "ELM327 Bluetooth (OBDLink MX+ v2.2)" else "Disconnected",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        Text(
                            text = "14.1 V",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-tabs row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ForscanSubTab.entries.forEach { tab ->
                        val isSel = activeSubTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { activeSubTab = tab },
                            color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                when (activeSubTab) {
                    ForscanSubTab.LIVE_PIDS -> {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PCM LIVE SENSOR PIDs",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = Color(0xFF38BDF8)
                                )

                                Button(
                                    onClick = { isLiveStreaming = !isLiveStreaming },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isLiveStreaming) Color(0xFF7F1D1D) else Color(0xFF064E3B)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        if (isLiveStreaming) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isLiveStreaming) "Pause Stream" else "Start Stream", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(pidsList) { pid ->
                                    val isNormal = pid.currentValue in pid.normalMin..pid.normalMax
                                    val pidColor = if (isNormal) Color(0xFF10B981) else Color(0xFFFF6F00)

                                    Surface(
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(pid.shortName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                                Text(pid.module.codeName, style = MaterialTheme.typography.labelSmall, color = pid.module.color)
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "${pid.currentValue} ${pid.unit}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                                color = pidColor
                                            )

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = "Norm: ${pid.normalMin.toInt()}-${pid.normalMax.toInt()} ${pid.unit}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ForscanSubTab.DTC_CODES -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "IMPORT FORScan LOG OR DTC CODES",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                            color = Color(0xFFFFD700)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = pasteLogText,
                                            onValueChange = { pasteLogText = it },
                                            placeholder = { Text("Paste FORScan DTC log text (e.g. PCM: P0171, P0174)...") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Button(
                                            onClick = {
                                                if (pasteLogText.isNotBlank()) {
                                                    activeDtcsList = ForscanData.parseForscanLogText(pasteLogText)
                                                    selectedDtc = activeDtcsList.firstOrNull()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Parse Log & Match 3D Parts")
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "DETECTED FORD DTC CODES (${activeDtcsList.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            items(activeDtcsList) { dtc ->
                                val isSelected = selectedDtc?.code == dtc.code

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedDtc = dtc },
                                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = dtc.module.color.copy(alpha = 0.2f),
                                                    border = BorderStroke(1.dp, dtc.module.color),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "${dtc.module.codeName}: ${dtc.code}",
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = dtc.module.color,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = dtc.title,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                            }

                                            Surface(
                                                color = Color(0xFF7F1D1D),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = dtc.status.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFFFECACA),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = dtc.fordSpecificDetails,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFCBD5E1)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                onDismiss()
                                                onNavigateToComponent(dtc.targetComponentId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("forscan_dtc_highlight_3d_btn")
                                        ) {
                                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Highlight Faulty Part in 3D Model")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ForscanSubTab.MODULES -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(FordModule.entries) { mod ->
                                val dtcCount = activeDtcsList.count { it.module == mod }

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(mod.color)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(mod.codeName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                                Text(mod.fullName, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                            }
                                        }

                                        Surface(
                                            color = if (dtcCount > 0) Color(0xFF7F1D1D) else Color(0xFF064E3B),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (dtcCount > 0) "$dtcCount DTC FAULTS" else "HEALTHY",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (dtcCount > 0) Color(0xFFFECACA) else Color(0xA1D1D80),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    activeDtcsList = emptyList()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
            ) {
                Text("Clear DTC Memory", color = Color(0xFFCBD5E1))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF94A3B8))
            }
        }
    )
}
