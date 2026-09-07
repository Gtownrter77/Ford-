package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportTracServiceManualDiagnostics
import com.example.model.ServiceManualTroubleMatch
import com.example.model.SymptomItem
import com.example.model.VehicleSystem
import com.example.util.HapticHelper

@Composable
fun SymptomTroubleshootingDialog(
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    var selectedSystemFilter by remember { mutableStateOf<VehicleSystem?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val selectedSymptomIds = remember { mutableStateListOf<String>() }
    var expandedMatchId by remember { mutableStateOf<String?>(null) }

    val filteredSymptoms = remember(selectedSystemFilter, searchQuery) {
        SportTracServiceManualDiagnostics.symptomsList.filter { symptom ->
            val matchesSystem = selectedSystemFilter == null || symptom.system == selectedSystemFilter
            val matchesQuery = searchQuery.isBlank() ||
                    symptom.title.contains(searchQuery, ignoreCase = true) ||
                    symptom.description.contains(searchQuery, ignoreCase = true)
            matchesSystem && matchesQuery
        }
    }

    val matchingCauses = remember(selectedSymptomIds.toList()) {
        SportTracServiceManualDiagnostics.findTroubleMatchesForSymptoms(selectedSymptomIds.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("symptom_troubleshooting_dialog"),
        title = {
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
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Service Manual Diagnostic Flow",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "2004 Sport Trac Factory Manual & TSBs",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search symptoms, codes (e.g., P0171, rattle)...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("symptom_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Vehicle System Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedSystemFilter == null,
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                selectedSystemFilter = null
                            },
                            label = { Text("All Systems (${SportTracServiceManualDiagnostics.symptomsList.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }

                    items(VehicleSystem.entries) { sys ->
                        FilterChip(
                            selected = selectedSystemFilter == sys,
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                selectedSystemFilter = if (selectedSystemFilter == sys) null else sys
                            },
                            label = { Text(sys.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = sys.color,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT OBSERVED SYMPTOMS (${selectedSymptomIds.size} Selected)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                        color = Color(0xFF94A3B8)
                    )

                    if (selectedSymptomIds.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                selectedSymptomIds.clear()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Clear Selection", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Symptom Selector Items
                    items(filteredSymptoms) { symptom ->
                        val isChecked = selectedSymptomIds.contains(symptom.id)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    if (isChecked) {
                                        selectedSymptomIds.remove(symptom.id)
                                    } else {
                                        selectedSymptomIds.add(symptom.id)
                                    }
                                }
                                .testTag("symptom_item_${symptom.id}"),
                            color = if (isChecked) Color(0xFF1E3A5F) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isChecked) Color(0xFF38BDF8) else Color(0xFF334155)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        if (checked) selectedSymptomIds.add(symptom.id) else selectedSymptomIds.remove(symptom.id)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF38BDF8),
                                        uncheckedColor = Color(0xFF64748B)
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = symptom.system.color,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = symptom.system.displayName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (symptom.severity == "Critical" || symptom.severity == "Severe") {
                                            Surface(
                                                color = Color(0xFF7F1D1D),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = symptom.severity.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFFFECACA),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = symptom.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )

                                    Text(
                                        text = symptom.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    // MATCHED SERVICE MANUAL CAUSES SECTION
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedSymptomIds.isEmpty()) "SELECT SYMPTOMS ABOVE TO GENERATE CAUSE MATCHES" else "MATCHED 2004 SERVICE MANUAL CAUSES (${matchingCauses.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                            color = Color(0xFFFFD700)
                        )
                    }

                    if (selectedSymptomIds.isEmpty()) {
                        item {
                            Surface(
                                color = Color(0xFF0F2238),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF0284C7))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Check one or more symptoms from the list to cross-reference potential causes from the official 2004 Ford Sport Trac Factory Service Manual & TSB database.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    items(matchingCauses) { match ->
                        val isExpanded = expandedMatchId == match.id

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFFFD700))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFF7F1D1D),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${match.urgencyLevel.uppercase()} URGENCY",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFFECACA),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = match.serviceManualSection,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF38BDF8)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = match.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )

                                if (match.tsbNumber != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Factory TSB Ref: ${match.tsbNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFFF6F00)
                                    )
                                }

                                if (match.obdCodes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        match.obdCodes.forEach { code ->
                                            Surface(
                                                color = Color(0xFF0F172A),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color(0xFF38BDF8))
                                            ) {
                                                Text(
                                                    text = code,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFF38BDF8),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = match.problemSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "PROBABLE CAUSE:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFF6F00)
                                )
                                Text(
                                    text = match.probableCause,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Toggle Verification Steps Button
                                OutlinedButton(
                                    onClick = {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        expandedMatchId = if (isExpanded) null else match.id
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Icon(
                                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isExpanded) "Hide Diagnostic Verification Steps" else "Show Diagnostic Verification Steps (${match.diagnosticVerificationSteps.size})")
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .padding(top = 10.dp)
                                            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "SERVICE MANUAL DIAGNOSTIC TEST PROCEDURE:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFFFD700)
                                        )

                                        match.diagnosticVerificationSteps.forEach { step ->
                                            Text(
                                                text = step,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFE2E8F0)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        HapticHelper.triggerComplexComponentPulse(context, view, haptic)
                                        onDismiss()
                                        onNavigateToComponent(match.targetComponentId)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("symptom_open_3d_${match.id}")
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open 3D Model & Repair Manual")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF94A3B8))
            }
        }
    )
}
