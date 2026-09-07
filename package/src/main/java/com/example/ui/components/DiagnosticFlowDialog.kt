package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.*

@Composable
fun DiagnosticFlowDialog(
    category: DiagnosticSymptomCategory,
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentQuestion by remember { mutableStateOf(category.rootQuestion) }
    var activeDiagnosis by remember { mutableStateOf<DiagnosticResult?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("diagnostic_flow_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(category.system.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeDiagnosis == null) {
                    // Question Prompt
                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Question Options
                    currentQuestion.options.forEach { option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (option.resultDiagnosis != null) {
                                        activeDiagnosis = option.resultDiagnosis
                                    }
                                }
                                .testTag("diag_option_btn"),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = option.optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                } else {
                    // Diagnostic Result Screen
                    val diag = activeDiagnosis!!

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD700))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF7F1D1D),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${diag.urgencyLevel.uppercase()} URGENCY",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFFECACA),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "${diag.confidencePercentage}% MATCH",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFFD700)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = diag.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            if (diag.obdCode != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "OBD-II Code: ${diag.obdCode}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = diag.problemSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "PROBABLE CAUSE:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFF6F00)
                            )
                            Text(
                                text = diag.probableCause,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onDismiss()
                                    onNavigateToComponent(diag.targetComponentId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_diagnostic_repair_btn")
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open 3D Model & Repair Manual")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (activeDiagnosis != null) {
                TextButton(onClick = { activeDiagnosis = null }) {
                    Text("Re-test Symptoms", color = Color(0xFF38BDF8))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF94A3B8))
            }
        }
    )
}
