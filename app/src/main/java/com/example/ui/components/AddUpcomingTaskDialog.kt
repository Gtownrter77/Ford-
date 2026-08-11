package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UpcomingTaskEntity
import com.example.model.VehicleSystem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddUpcomingTaskDialog(
    currentMileage: Int,
    onDismiss: () -> Unit,
    onSaveTask: (UpcomingTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var titleText by remember { mutableStateOf("") }
    var selectedSystem by remember { mutableStateOf(VehicleSystem.ENGINE) }
    var targetMileageText by remember { mutableStateOf((currentMileage + 3000).toString()) }
    var daysUntilDue by remember { mutableStateOf(30) } // Default 30 days
    var fluidSpecText by remember { mutableStateOf("") }
    var priorityLevel by remember { mutableStateOf("NORMAL") }
    var estimatedCostText by remember { mutableStateOf("50.00") }
    var notesText by remember { mutableStateOf("") }

    val computedDueDateMillis = remember(daysUntilDue) {
        System.currentTimeMillis() + (daysUntilDue * 86400000L)
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("add_upcoming_task_dialog"),
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
                        imageVector = Icons.Default.EventRepeat,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Schedule Service Due Task",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
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
                // Task Title Input
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Task Title (e.g. Front Brake Pads Replacement)", color = Color(0xFF94A3B8)) },
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
                        .testTag("input_task_title")
                )

                // Vehicle System Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "VEHICLE SYSTEM",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF38BDF8)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            VehicleSystem.ENGINE to "Engine",
                            VehicleSystem.TRANSMISSION to "Trans",
                            VehicleSystem.COOLING to "Cooling",
                            VehicleSystem.BRAKES_CHASSIS to "Brakes",
                            VehicleSystem.DRIVETRAIN_4WD to "4WD"
                        ).forEach { (sys, label) ->
                            val isSelected = selectedSystem == sys
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedSystem = sys },
                                color = if (isSelected) sys.color else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) sys.color else Color(0xFF334155))
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Target Mileage & Priority Level Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = targetMileageText,
                        onValueChange = { targetMileageText = it },
                        label = { Text("Target Odometer (mi)", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD700)) },
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
                            .weight(1.2f)
                            .testTag("input_target_mileage")
                    )

                    OutlinedTextField(
                        value = estimatedCostText,
                        onValueChange = { estimatedCostText = it },
                        label = { Text("Cost ($)", color = Color(0xFF94A3B8)) },
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
                            .weight(0.8f)
                            .testTag("input_cost")
                    )
                }

                // Due Date Presets Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ESTIMATED DUE DATE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = dateFormatter.format(Date(computedDueDateMillis)),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF10B981)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            14 to "14 Days",
                            30 to "1 Month",
                            90 to "3 Months",
                            180 to "6 Months"
                        ).forEach { (days, label) ->
                            val isSelected = daysUntilDue == days
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { daysUntilDue = days },
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Priority Badge Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PRIORITY URGENCY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF38BDF8)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "CRITICAL" to Color(0xFFEF4444),
                            "HIGH" to Color(0xFFFFD700),
                            "NORMAL" to Color(0xFF10B981)
                        ).forEach { (prio, color) ->
                            val isSelected = priorityLevel == prio
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { priorityLevel = prio },
                                color = if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) color else Color(0xFF334155))
                            ) {
                                Text(
                                    text = prio,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isSelected) color else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Parts / Fluid Specs Input
                OutlinedTextField(
                    value = fluidSpecText,
                    onValueChange = { fluidSpecText = it },
                    label = { Text("Part Number / Spec (e.g. Motorcraft MERCON V)", color = Color(0xFF94A3B8)) },
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
                        .testTag("input_fluid_spec")
                )

                // Notes / Torque / Instructions
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Torque Specs or Workshop Notes", color = Color(0xFF94A3B8)) },
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
                        .testTag("input_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleText.isNotBlank()) {
                        val targetMiles = targetMileageText.toIntOrNull() ?: (currentMileage + 3000)
                        val cost = estimatedCostText.toDoubleOrNull() ?: 0.0

                        val newEntity = UpcomingTaskEntity(
                            scheduleItemId = "custom_${System.currentTimeMillis()}",
                            title = titleText.trim(),
                            systemName = selectedSystem.displayName,
                            targetMileage = targetMiles,
                            dueDateMillis = computedDueDateMillis,
                            intervalMiles = 5000,
                            fluidSpecOrPart = fluidSpecText.trim(),
                            priorityLevel = priorityLevel,
                            estimatedCostUsd = cost,
                            isCompleted = false,
                            notes = notesText.trim()
                        )
                        onSaveTask(newEntity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("btn_save_upcoming_task")
            ) {
                Text("Save Task to Room DB", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
