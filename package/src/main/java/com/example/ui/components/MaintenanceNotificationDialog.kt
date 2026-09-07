package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.SportTracData
import com.example.data.local.MaintenanceEntity
import com.example.data.local.UpcomingTaskEntity
import com.example.service.MaintenanceSchedulerService
import com.example.service.ScheduledReminderAlert

@Composable
fun MaintenanceNotificationDialog(
    maintenanceLogs: List<MaintenanceEntity>,
    upcomingTasks: List<UpcomingTaskEntity> = emptyList(),
    currentMileage: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schedulerService = remember { MaintenanceSchedulerService(context) }

    // Permission state check for POST_NOTIFICATIONS
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "System notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    val reminderAlerts = remember(maintenanceLogs, upcomingTasks, currentMileage) {
        schedulerService.scheduleMaintenanceRemindersFromRoomLogs(
            logs = maintenanceLogs,
            schedules = SportTracData.defaultMaintenanceSchedules,
            upcomingTasks = upcomingTasks,
            currentMileage = currentMileage
        )
    }

    var selectedFilterTab by remember { mutableStateOf("ALL") }
    var autoPushEnabled by remember { mutableStateOf(true) }
    var alertForCustomAlarm by remember { mutableStateOf<ScheduledReminderAlert?>(null) }

    val filteredAlerts = remember(reminderAlerts, selectedFilterTab) {
        when (selectedFilterTab) {
            "URGENT" -> reminderAlerts.filter { it.isOverdue || it.isDueSoon }
            "SCHEDULED" -> reminderAlerts.filter { !it.isOverdue && !it.isDueSoon }
            else -> reminderAlerts
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("maintenance_notification_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Local Notification Center",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Permission Card (If on Android 13+ and not granted)
                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Surface(
                        color = Color(0xFF7C2D12),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFF97316))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFDBA74))
                                Column {
                                    Text(
                                        text = "Notification Permission Needed",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Allow Android system tray alerts for vehicle service",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFFED7AA)
                                    )
                                }
                            }
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Grant", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                // Control Header Box
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Automated System Reminders",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Background AlarmManager + Channel ${MaintenanceSchedulerService.CHANNEL_ID}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = autoPushEnabled,
                                onCheckedChange = { autoPushEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val count = schedulerService.checkAndPostUrgentNotifications(upcomingTasks, currentMileage)
                                    if (count > 0) {
                                        Toast.makeText(context, "Fired $count urgent service notifications!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val first = reminderAlerts.firstOrNull()
                                        if (first != null) {
                                            schedulerService.triggerLocalNotification(first.id, first.title, first.message)
                                            Toast.makeText(context, "Test push notification sent!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("test_push_notification_btn")
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Push", style = MaterialTheme.typography.labelMedium)
                            }

                            Button(
                                onClick = {
                                    var scheduled = 0
                                    reminderAlerts.forEach { alert ->
                                        val delay = if (alert.isOverdue) 5_000L else if (alert.isDueSoon) 30_000L else 300_000L
                                        schedulerService.scheduleFutureReminder(
                                            id = alert.id,
                                            title = alert.title,
                                            message = alert.message,
                                            triggerAtMillis = System.currentTimeMillis() + delay
                                        )
                                        scheduled++
                                    }
                                    Toast.makeText(context, "Synced $scheduled alarm schedules with Android system!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("schedule_all_alarms_btn")
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync All Alarms", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Filter Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf(
                        "ALL" to "All Reminders (${reminderAlerts.size})",
                        "URGENT" to "Urgent / Overdue (${reminderAlerts.count { it.isOverdue || it.isDueSoon }})",
                        "SCHEDULED" to "Standard (${reminderAlerts.count { !it.isOverdue && !it.isDueSoon }})"
                    )

                    items(tabs) { (code, label) ->
                        val isSelected = selectedFilterTab == code
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterTab = code },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAlerts) { alert ->
                        val badgeColor = when {
                            alert.isOverdue -> Color(0xFFEF4444)
                            alert.isDueSoon -> Color(0xFFFFD700)
                            else -> Color(0xFF10B981)
                        }

                        val badgeText = when {
                            alert.isOverdue -> "OVERDUE"
                            alert.isDueSoon -> "DUE SOON"
                            else -> "OK"
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            color = badgeColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, badgeColor)
                                        ) {
                                            Text(
                                                text = badgeText,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = badgeColor,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = alert.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFF334155),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = alert.priorityLevel,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = alert.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { alertForCustomAlarm = alert },
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Alarm, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Set Alarm", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF38BDF8))
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = {
                                            schedulerService.triggerLocalNotification(
                                                id = alert.id,
                                                title = alert.title,
                                                message = alert.message
                                            )
                                            Toast.makeText(context, "Notification posted to system tray!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Push Now", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
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
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Close")
            }
        }
    )

    // Sub-dialog for picking custom alarm delay
    if (alertForCustomAlarm != null) {
        val alert = alertForCustomAlarm!!
        AlertDialog(
            onDismissRequest = { alertForCustomAlarm = null },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            title = {
                Text("Set Alarm Timer for Task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Task: ${alert.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "Select when Android AlarmManager should fire the local background push notification:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )

                    val delayOptions = listOf(
                        "Test (10 Seconds)" to 10_000L,
                        "1 Hour" to 3_600_000L,
                        "24 Hours (1 Day)" to 86_400_000L,
                        "3 Days" to 3 * 86_400_000L,
                        "1 Week (7 Days)" to 7 * 86_400_000L
                    )

                    delayOptions.forEach { (label, millis) ->
                        Button(
                            onClick = {
                                schedulerService.scheduleFutureReminder(
                                    id = alert.id,
                                    title = alert.title,
                                    message = alert.message,
                                    triggerAtMillis = System.currentTimeMillis() + millis
                                )
                                Toast.makeText(context, "Alarm set for $label!", Toast.LENGTH_SHORT).show()
                                alertForCustomAlarm = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White)
                                Icon(Icons.Default.Alarm, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { alertForCustomAlarm = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

