package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.MaintenanceEntity
import com.example.model.MaintenanceScheduleItem
import com.example.model.VehicleSystem

@Composable
fun AddServiceLogDialog(
    scheduleItems: List<MaintenanceScheduleItem>,
    currentMileage: Int,
    onDismiss: () -> Unit,
    onSaveLog: (MaintenanceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScheduleItem by remember { mutableStateOf(scheduleItems.firstOrNull()) }
    var serviceTitle by remember { mutableStateOf(selectedScheduleItem?.title ?: "Custom Maintenance") }
    var mileageText by remember { mutableStateOf(currentMileage.toString()) }
    var costText by remember { mutableStateOf("45.00") }
    var notesText by remember { mutableStateOf("") }
    var systemName by remember { mutableStateOf(selectedScheduleItem?.system?.displayName ?: VehicleSystem.ENGINE.displayName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("add_service_log_dialog"),
        title = {
            Text(
                text = "Log Maintenance Service",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = serviceTitle,
                    onValueChange = { serviceTitle = it },
                    label = { Text("Service Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_title_input")
                )

                OutlinedTextField(
                    value = mileageText,
                    onValueChange = { mileageText = it },
                    label = { Text("Mileage at Service (mi)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mileage_input")
                )

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Cost ($ USD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cost_input")
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Service Notes & Parts Used") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val miles = mileageText.toIntOrNull() ?: currentMileage
                    val cost = costText.toDoubleOrNull() ?: 0.0

                    val entity = MaintenanceEntity(
                        scheduleItemId = selectedScheduleItem?.id ?: "custom",
                        title = serviceTitle,
                        systemName = systemName,
                        mileageAtService = miles,
                        dateLoggedMillis = System.currentTimeMillis(),
                        costUsd = cost,
                        notes = notesText,
                        isCompleted = true
                    )
                    onSaveLog(entity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_service_log_btn")
            ) {
                Text("Save Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
