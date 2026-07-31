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
import com.example.data.local.MaintenanceEntity
import com.example.data.local.VehicleProfileEntity
import com.example.ui.components.AddServiceLogDialog
import com.example.ui.components.VehicleHealthDashboard
import java.text.SimpleDateFormat
import java.util.*

enum class MaintenanceTab {
    HEALTH_DASHBOARD,
    SCHEDULE_LOGS
}

@Composable
fun MaintenanceScreen(
    vehicleProfile: VehicleProfileEntity?,
    maintenanceLogs: List<MaintenanceEntity>,
    onUpdateMileage: (Int) -> Unit,
    onLogService: (MaintenanceEntity) -> Unit,
    onDeleteLog: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(MaintenanceTab.HEALTH_DASHBOARD) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isEditingMileage by remember { mutableStateOf(false) }
    var mileageInputText by remember { mutableStateOf((vehicleProfile?.currentMileage ?: 115000).toString()) }

    val currentMileage = vehicleProfile?.currentMileage ?: 115000

    if (showAddDialog) {
        AddServiceLogDialog(
            scheduleItems = SportTracData.defaultMaintenanceSchedules,
            currentMileage = currentMileage,
            onDismiss = { showAddDialog = false },
            onSaveLog = { log ->
                onLogService(log)
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Top Vehicle Mileage Counter Header
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "2004 FORD EXPLORER SPORT TRAC",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "Maintenance & Health",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("add_log_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Log Service")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Selector Bar (Health Dashboard vs Schedule Logs)
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { activeTab = MaintenanceTab.HEALTH_DASHBOARD }
                                .testTag("tab_health_dashboard"),
                            color = if (activeTab == MaintenanceTab.HEALTH_DASHBOARD) Color(0xFF0284C7) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = if (activeTab == MaintenanceTab.HEALTH_DASHBOARD) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Health Dashboard",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeTab == MaintenanceTab.HEALTH_DASHBOARD) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { activeTab = MaintenanceTab.SCHEDULE_LOGS }
                                .testTag("tab_schedule_logs"),
                            color = if (activeTab == MaintenanceTab.SCHEDULE_LOGS) Color(0xFF0284C7) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ListAlt,
                                    contentDescription = null,
                                    tint = if (activeTab == MaintenanceTab.SCHEDULE_LOGS) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Schedule & Logs",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeTab == MaintenanceTab.SCHEDULE_LOGS) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Odometer Mileage Tracker Box
                Surface(
                    color = Color(0xFF0B132B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF0284C7))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("CURRENT ODOMETER", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                if (isEditingMileage) {
                                    OutlinedTextField(
                                        value = mileageInputText,
                                        onValueChange = { mileageInputText = it },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.width(120.dp)
                                    )
                                } else {
                                    Text(
                                        text = "%,d miles".format(currentMileage),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (isEditingMileage) {
                                    val newMiles = mileageInputText.toIntOrNull()
                                    if (newMiles != null) {
                                        onUpdateMileage(newMiles)
                                    }
                                    isEditingMileage = false
                                } else {
                                    isEditingMileage = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.testTag("update_mileage_btn")
                        ) {
                            Text(if (isEditingMileage) "Save" else "Update", color = Color(0xFF38BDF8))
                        }
                    }
                }
            }
        }

        when (activeTab) {
            MaintenanceTab.HEALTH_DASHBOARD -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        VehicleHealthDashboard(
                            currentMileage = currentMileage,
                            maintenanceLogs = maintenanceLogs,
                            onLogServiceClick = { showAddDialog = true }
                        )
                    }
                }
            }

            MaintenanceTab.SCHEDULE_LOGS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

            // Maintenance Due Status Section
            item {
                Text(
                    text = "SYSTEM HEALTH & MAINTENANCE INTERVALS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = Color(0xFF38BDF8)
                )
            }

            items(SportTracData.defaultMaintenanceSchedules) { item ->
                // Calculate last logged mileage for this item
                val lastLog = maintenanceLogs.firstOrNull { it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true) }
                val lastServiceMileage = lastLog?.mileageAtService ?: (currentMileage - (item.intervalMiles * 0.6).toInt())
                val milesSinceService = (currentMileage - lastServiceMileage).coerceAtLeast(0)
                val percentRemaining = ((1.0f - (milesSinceService.toFloat() / item.intervalMiles.toFloat())) * 100).coerceIn(0f, 100f)

                val healthColor = when {
                    percentRemaining > 50f -> Color(0xFF10B981) // Green
                    percentRemaining > 20f -> Color(0xFFFFD700) // Yellow
                    else -> Color(0xFFEF4444)                  // Red
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
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
                                        .background(item.system.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "${percentRemaining.toInt()}% HEALTH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = healthColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { percentRemaining / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = healthColor,
                            trackColor = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Interval: %,d mi • %s".format(item.intervalMiles, item.fluidTypeOrSpec),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )

                            val nextDueMiles = lastServiceMileage + item.intervalMiles
                            Text(
                                text = "Next Due: %,d mi".format(nextDueMiles),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }

            // Completed Logs History Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMPLETED SERVICE HISTORY LOGS (${maintenanceLogs.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            if (maintenanceLogs.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No maintenance logs recorded yet. Tap '+' above to add your first service record.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(maintenanceLogs) { log ->
                    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
                    val dateStr = dateFormat.format(Date(log.dateLoggedMillis))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Done at %,d miles • %s • $%.2f".format(log.mileageAtService, dateStr, log.costUsd),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF38BDF8)
                                )
                                if (log.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteLog(log.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = Color(0xFFEF4444))
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




