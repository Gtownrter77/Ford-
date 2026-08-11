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
import com.example.data.local.UpcomingTaskEntity
import com.example.data.local.VehicleProfileEntity
import com.example.ui.components.AddServiceLogDialog
import com.example.ui.components.AddUpcomingTaskDialog
import com.example.ui.components.MaintenanceNotificationDialog
import com.example.ui.components.UpcomingTasksListView
import com.example.ui.components.VehicleHealthDashboard
import java.text.SimpleDateFormat
import java.util.*

enum class MaintenanceTab {
    UPCOMING_TASKS,
    HEALTH_DASHBOARD,
    SCHEDULE_LOGS
}

@Composable
fun MaintenanceScreen(
    vehicleProfile: VehicleProfileEntity?,
    maintenanceLogs: List<MaintenanceEntity>,
    upcomingTasks: List<UpcomingTaskEntity> = emptyList(),
    onUpdateMileage: (Int) -> Unit,
    onLogService: (MaintenanceEntity) -> Unit,
    onDeleteLog: (Long) -> Unit,
    onAddUpcomingTask: (UpcomingTaskEntity) -> Unit = {},
    onCompleteUpcomingTask: (UpcomingTaskEntity, Int, Double, String) -> Unit = { _, _, _, _ -> },
    onDeleteUpcomingTask: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(MaintenanceTab.UPCOMING_TASKS) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddUpcomingDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var isEditingMileage by remember { mutableStateOf(false) }
    var mileageInputText by remember { mutableStateOf((vehicleProfile?.currentMileage ?: 115000).toString()) }
    var searchQuery by remember { mutableStateOf("") }

    val currentMileage = vehicleProfile?.currentMileage ?: 115000

    val filteredLogs = remember(maintenanceLogs, searchQuery) {
        if (searchQuery.isBlank()) {
            maintenanceLogs
        } else {
            maintenanceLogs.filter { log ->
                log.title.contains(searchQuery, ignoreCase = true) ||
                log.componentDescription.contains(searchQuery, ignoreCase = true) ||
                log.systemName.contains(searchQuery, ignoreCase = true) ||
                log.notes.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val totalInvestment = remember(maintenanceLogs) {
        maintenanceLogs.sumOf { it.costUsd }
    }

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

    if (showAddUpcomingDialog) {
        AddUpcomingTaskDialog(
            currentMileage = currentMileage,
            onDismiss = { showAddUpcomingDialog = false },
            onSaveTask = { task ->
                onAddUpcomingTask(task)
                showAddUpcomingDialog = false
            }
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val schedulerService = remember { com.example.service.MaintenanceSchedulerService(context) }

    val urgentAlertsCount = remember(upcomingTasks, currentMileage) {
        val nowMillis = System.currentTimeMillis()
        upcomingTasks.count { task ->
            !task.isCompleted && (
                task.targetMileage <= currentMileage ||
                (task.dueDateMillis > 0 && task.dueDateMillis <= nowMillis) ||
                (task.targetMileage - currentMileage in 1..1000)
            )
        }
    }

    // Automatically check and trigger local system notifications for overdue or due soon tasks
    LaunchedEffect(upcomingTasks, currentMileage) {
        if (upcomingTasks.isNotEmpty()) {
            schedulerService.checkAndPostUrgentNotifications(upcomingTasks, currentMileage)
        }
    }

    if (showNotificationDialog) {
        MaintenanceNotificationDialog(
            maintenanceLogs = maintenanceLogs,
            upcomingTasks = upcomingTasks,
            currentMileage = currentMileage,
            onDismiss = { showNotificationDialog = false }
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2004 FORD EXPLORER SPORT TRAC",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "ROOM DB ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Maintenance Records & History",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            IconButton(
                                onClick = { showNotificationDialog = true },
                                modifier = Modifier.testTag("notification_reminders_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notification Reminders",
                                    tint = Color(0xFF38BDF8)
                                )
                            }
                            if (urgentAlertsCount > 0) {
                                Surface(
                                    color = Color(0xFFEF4444),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 2.dp, end = 2.dp)
                                ) {
                                    Text(
                                        text = urgentAlertsCount.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        FloatingActionButton(
                            onClick = {
                                if (activeTab == MaintenanceTab.UPCOMING_TASKS) {
                                    showAddUpcomingDialog = true
                                } else {
                                    showAddDialog = true
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("add_log_fab")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Task or Log")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Selector Bar (Upcoming Tasks vs Health Dashboard vs Schedule Logs)
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
                                .clickable { activeTab = MaintenanceTab.UPCOMING_TASKS }
                                .testTag("tab_upcoming_tasks"),
                            color = if (activeTab == MaintenanceTab.UPCOMING_TASKS) Color(0xFF0284C7) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    tint = if (activeTab == MaintenanceTab.UPCOMING_TASKS) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Upcoming Tasks",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeTab == MaintenanceTab.UPCOMING_TASKS) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }

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
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Health",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Logs",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeTab == MaintenanceTab.SCHEDULE_LOGS) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Odometer Mileage & Investment Summary Box
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

                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL INVESTED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(
                                text = "$%.2f".format(totalInvestment),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981)
                            )
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
            MaintenanceTab.UPCOMING_TASKS -> {
                UpcomingTasksListView(
                    upcomingTasks = upcomingTasks,
                    currentMileage = currentMileage,
                    onCompleteTask = onCompleteUpcomingTask,
                    onDeleteTask = onDeleteUpcomingTask,
                    onAddNewTaskClick = { showAddUpcomingDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }

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

                    // Room Database Search & History Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ROOM DATABASE LOGS (${filteredLogs.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search logs by part, title, or system...", color = Color(0xFF64748B)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_room_logs_input")
                        )
                    }

                    if (filteredLogs.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) "No maintenance logs recorded yet. Tap '+' above to add your first service record." else "No maintenance logs found matching '$searchQuery'.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(filteredLogs) { log ->
                            val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
                            val dateStr = dateFormat.format(Date(log.dateLoggedMillis))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = log.systemName.uppercase(),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                        color = Color(0xFF38BDF8),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = dateStr,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF94A3B8)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = log.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )

                                            if (log.componentDescription.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Component: ${log.componentDescription}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = Color(0xFFFFD700)
                                                )
                                            }

                                            Text(
                                                text = "Done at %,d miles • Cost: $%.2f".format(log.mileageAtService, log.costUsd),
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
}





