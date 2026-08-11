package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddServiceLogDialog(
    scheduleItems: List<MaintenanceScheduleItem>,
    currentMileage: Int,
    onDismiss: () -> Unit,
    onSaveLog: (MaintenanceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScheduleItem by remember { mutableStateOf(scheduleItems.firstOrNull()) }
    var serviceTitle by remember { mutableStateOf(selectedScheduleItem?.title ?: "Custom Maintenance Service") }
    var componentDescription by remember { mutableStateOf(selectedScheduleItem?.description ?: "4.0L SOHC V6 Replacement Component") }
    var mileageText by remember { mutableStateOf(currentMileage.toString()) }
    
    val todayFormatted = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var dateString by remember { mutableStateOf(todayFormatted) }
    
    var costText by remember { mutableStateOf("45.00") }
    var notesText by remember { mutableStateOf("") }
    var systemName by remember { mutableStateOf(selectedScheduleItem?.system?.displayName ?: VehicleSystem.ENGINE.displayName) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("add_service_log_dialog"),
        title = {
            Text(
                text = "Log Sport Trac Service Record",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    value = componentDescription,
                    onValueChange = { componentDescription = it },
                    label = { Text("Component Description / Part Name") },
                    placeholder = { Text("e.g. Thermostat Housing, Motorcraft FL-820S") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("component_description_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = mileageText,
                        onValueChange = { mileageText = it },
                        label = { Text("Mileage (mi)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mileage_input")
                    )

                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("service_date_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            .weight(1f)
                            .testTag("cost_input")
                    )

                    OutlinedTextField(
                        value = systemName,
                        onValueChange = { systemName = it },
                        label = { Text("Vehicle System") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("system_input")
                    )
                }

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

                    val dateMillis = try {
                        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString)
                        parsedDate?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }

                    val entity = MaintenanceEntity(
                        scheduleItemId = selectedScheduleItem?.id ?: "custom",
                        title = serviceTitle,
                        systemName = systemName,
                        mileageAtService = miles,
                        dateLoggedMillis = dateMillis,
                        componentDescription = componentDescription,
                        costUsd = cost,
                        notes = notesText,
                        isCompleted = true
                    )
                    onSaveLog(entity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_service_log_btn")
            ) {
                Text("Save to Room DB")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

