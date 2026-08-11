package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UpcomingTaskEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UpcomingTasksListView(
    upcomingTasks: List<UpcomingTaskEntity>,
    currentMileage: Int,
    onCompleteTask: (UpcomingTaskEntity, Int, Double, String) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onAddNewTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("ALL") }

    // Dialog state for completing a task
    var taskToComplete by remember { mutableStateOf<UpcomingTaskEntity?>(null) }
    var actualMileageText by remember { mutableStateOf("") }
    var actualCostText by remember { mutableStateOf("") }
    var completionNotesText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val nowMillis = remember { System.currentTimeMillis() }

    val overdueCount = remember(upcomingTasks, currentMileage, nowMillis) {
        upcomingTasks.count { task ->
            task.targetMileage <= currentMileage || task.dueDateMillis <= nowMillis
        }
    }

    val dueSoonCount = remember(upcomingTasks, currentMileage, nowMillis) {
        upcomingTasks.count { task ->
            val milesRem = task.targetMileage - currentMileage
            val daysRem = (task.dueDateMillis - nowMillis) / 86400000L
            (milesRem in 1..3000) || (daysRem in 1..30)
        }
    }

    val filteredTasks = remember(upcomingTasks, searchQuery, selectedFilterChip, currentMileage, nowMillis) {
        upcomingTasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.systemName.contains(searchQuery, ignoreCase = true) ||
                task.fluidSpecOrPart.contains(searchQuery, ignoreCase = true) ||
                task.notes.contains(searchQuery, ignoreCase = true)

            val isOverdue = task.targetMileage <= currentMileage || task.dueDateMillis <= nowMillis

            val matchesFilter = when (selectedFilterChip) {
                "OVERDUE" -> isOverdue
                "CRITICAL" -> task.priorityLevel == "CRITICAL" || task.priorityLevel == "HIGH"
                "ENGINE" -> task.systemName.contains("Engine", ignoreCase = true)
                "TRANS" -> task.systemName.contains("Trans", ignoreCase = true)
                "BRAKES" -> task.systemName.contains("Brake", ignoreCase = true)
                "4WD" -> task.systemName.contains("4WD", ignoreCase = true) || task.systemName.contains("Drivetrain", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    if (taskToComplete != null) {
        val task = taskToComplete!!
        AlertDialog(
            onDismissRequest = { taskToComplete = null },
            containerColor = Color(0xFF0F172A),
            titleContentColor = Color.White,
            modifier = Modifier.testTag("complete_task_dialog"),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Log Service Completion",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Task: ${task.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Marking complete will log this service into Room DB history and automatically schedule the next interval task.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    OutlinedTextField(
                        value = actualMileageText,
                        onValueChange = { actualMileageText = it },
                        label = { Text("Actual Mileage at Service", color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_actual_mileage")
                    )

                    OutlinedTextField(
                        value = actualCostText,
                        onValueChange = { actualCostText = it },
                        label = { Text("Actual Cost ($)", color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_actual_cost")
                    )

                    OutlinedTextField(
                        value = completionNotesText,
                        onValueChange = { completionNotesText = it },
                        label = { Text("Service Notes / Technician Signoff", color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_completion_notes")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val miles = actualMileageText.toIntOrNull() ?: currentMileage
                        val cost = actualCostText.toDoubleOrNull() ?: task.estimatedCostUsd
                        onCompleteTask(task, miles, cost, completionNotesText)
                        taskToComplete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("btn_confirm_complete_task")
                ) {
                    Text("Confirm & Record in Room DB", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToComplete = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header Metric Cards
        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROOM DB SCHEDULED TASKS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Text(
                        text = "${upcomingTasks.size} Tasks Monitored",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (overdueCount > 0) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Text(
                                text = "$overdueCount OVERDUE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (dueSoonCount > 0) {
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD700))
                        ) {
                            Text(
                                text = "$dueSoonCount DUE SOON",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                color = Color(0xFFFFD700),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onAddNewTaskClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_add_task_header")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter upcoming tasks by title, spec, or system...", color = Color(0xFF64748B)) },
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
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_upcoming_tasks_input")
            )

            // Category Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "OVERDUE", "CRITICAL", "ENGINE", "TRANS", "BRAKES", "4WD").forEach { filterKey ->
                    val isSelected = selectedFilterChip == filterKey
                    val chipColor = when (filterKey) {
                        "OVERDUE" -> Color(0xFFEF4444)
                        "CRITICAL" -> Color(0xFFFFD700)
                        else -> Color(0xFF0284C7)
                    }

                    Surface(
                        modifier = Modifier
                            .clickable { selectedFilterChip = filterKey }
                            .testTag("chip_filter_$filterKey"),
                        color = if (isSelected) chipColor else Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isSelected) chipColor else Color(0xFF334155))
                    ) {
                        Text(
                            text = filterKey,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // List View of Upcoming Tasks
        if (filteredTasks.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isBlank()) "No upcoming maintenance tasks found in Room DB." else "No upcoming tasks matching '$searchQuery'.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Tap 'Add Task' above to add custom maintenance service due targets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    val milesRemaining = task.targetMileage - currentMileage
                    val daysRemaining = (task.dueDateMillis - nowMillis) / 86400000L

                    val isOverdueByMiles = milesRemaining <= 0
                    val isOverdueByDate = task.dueDateMillis <= nowMillis
                    val isOverdue = isOverdueByMiles || isOverdueByDate

                    val statusColor = when {
                        isOverdue -> Color(0xFFEF4444)
                        milesRemaining in 1..3000 || daysRemaining in 1..30 -> Color(0xFFFFD700)
                        else -> Color(0xFF10B981)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upcoming_task_card_${task.id}"),
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = if (isOverdue) 1.5.dp else 1.dp,
                            color = if (isOverdue) Color(0xFFEF4444) else Color(0xFF334155)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Row: System Badge + Priority + Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                                    ) {
                                        Text(
                                            text = task.systemName.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        color = statusColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, statusColor)
                                    ) {
                                        Text(
                                            text = when {
                                                isOverdue -> "SERVICE OVERDUE"
                                                milesRemaining <= 3000 -> "DUE SOON"
                                                else -> task.priorityLevel
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val scheduler = com.example.service.MaintenanceSchedulerService(context)
                                            val notificationId = ("task_" + task.id).hashCode()
                                            val milesRem = task.targetMileage - currentMileage
                                            val statusTitle = if (milesRem <= 0) "⚠️ OVERDUE: ${task.title}" else "🔔 REMINDER: ${task.title}"
                                            val statusMsg = "Due at %,d miles (Current: %,d miles) | Spec: %s".format(
                                                task.targetMileage,
                                                currentMileage,
                                                if (task.fluidSpecOrPart.isNotBlank()) task.fluidSpecOrPart else "Ford OEM Spec"
                                            )
                                            scheduler.triggerLocalNotification(notificationId, statusTitle, statusMsg)
                                            android.widget.Toast.makeText(context, "Notification posted for ${task.title}!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "Trigger Reminder Notification",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = { onDeleteTask(task.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Task",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Task Title
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            if (task.fluidSpecOrPart.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Spec / Part: ${task.fluidSpecOrPart}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFFFFD700)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Due Date & Target Mileage Metrics Box
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Due Date: ${dateFormat.format(Date(task.dueDateMillis))}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Speed,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Target Odometer: %,d mi".format(task.targetMileage),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (isOverdueByMiles) "%,d mi Overdue".format(-milesRemaining)
                                            else "In %,d mi".format(milesRemaining),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                            color = statusColor
                                        )
                                        if (task.estimatedCostUsd > 0.0) {
                                            Text(
                                                text = "Est. $%.2f".format(task.estimatedCostUsd),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }
                            }

                            if (task.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Notes: ${task.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom Complete & Log Action Button
                            Button(
                                onClick = {
                                    actualMileageText = currentMileage.toString()
                                    actualCostText = if (task.estimatedCostUsd > 0.0) String.format("%.2f", task.estimatedCostUsd) else "45.00"
                                    completionNotesText = "Completed at %,d miles".format(currentMileage)
                                    taskToComplete = task
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("btn_complete_task_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mark Complete & Save Record in Room DB",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
