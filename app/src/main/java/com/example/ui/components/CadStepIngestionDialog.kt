package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.VehicleSystem
import com.example.service.BiltAssemblyStep
import com.example.service.CadStepIngestionService
import com.example.service.StepIngestionStatus
import kotlinx.coroutines.launch

/**
 * Interactive Dialog for Ingesting Raw CAD STEP (.step/.stp) files into optimized GLTF format
 * with BILT-style interactive assembly steps, tool requirements, and torque specs.
 */
@Composable
fun CadStepIngestionDialog(
    onDismiss: () -> Unit,
    onViewIn3DViewport: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val ingestionService = remember { CadStepIngestionService() }
    val jobState by ingestionService.jobState.collectAsState()

    val sampleStepFiles = listOf(
        "4.0L_V6_Timing_Cassette_Assembly.step",
        "Thermostat_Housing_Billet.stp",
        "Front_Brake_Caliper_2Piston.step",
        "Control_Arm_Balljoint_Subassembly.step"
    )

    var selectedSampleIndex by remember { mutableStateOf(0) }
    var activeBiltStepIndex by remember { mutableStateOf(0) }

    // Auto-ingest first sample on launch if idle
    LaunchedEffect(Unit) {
        if (jobState.status == StepIngestionStatus.IDLE) {
            ingestionService.ingestStepFile(sampleStepFiles[0], VehicleSystem.ENGINE)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
                        ) {
                            Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "CAD STEP INGESTION & BILT PIPELINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color(0xFF00F0FF)
                                )
                                Surface(
                                    color = Color(0xFF22C55E),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "STEP -> GLTF 2.0",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "B-Rep Tessellation, Draco LOD & Assembly Steps",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_cad_ingest_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Raw STEP File Selection Row
                Text(
                    text = "SELECT RAW CAD STEP FILE TO INGEST",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("step_file_selector_lazyrow")
                ) {
                    itemsIndexed(sampleStepFiles) { index, fileName ->
                        val isSelected = selectedSampleIndex == index
                        Surface(
                            color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                            modifier = Modifier
                                .clickable {
                                    selectedSampleIndex = index
                                    activeBiltStepIndex = 0
                                    scope.launch {
                                        ingestionService.ingestStepFile(fileName, VehicleSystem.ENGINE)
                                    }
                                }
                                .testTag("chip_step_file_$index")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = fileName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ingestion Pipeline Progress Card
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        when (jobState.status) {
                            StepIngestionStatus.COMPLETED -> Color(0xFF22C55E)
                            StepIngestionStatus.FAILED -> Color(0xFFEF4444)
                            else -> Color(0xFF00F0FF)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { jobState.progressPercent / 100f },
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF00F0FF),
                                    trackColor = Color(0xFF334155),
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = jobState.status.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color(0xFF00F0FF)
                                )
                            }

                            Text(
                                text = "${jobState.progressPercent}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = jobState.currentStageDescription,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar Canvas
                        LinearProgressIndicator(
                            progress = { jobState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00F0FF),
                            trackColor = Color(0xFF0F172A)
                        )

                        // Conversion Metrics Summary Bar
                        if (jobState.status == StepIngestionStatus.COMPLETED) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val m = jobState.metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MetricBadge("Raw STEP", "%.1f MB".format(m.rawStepFileSizeBytes / 1_000_000f), Color(0xFF94A3B8))
                                MetricBadge("GLTF 2.0", "%.1f MB".format(m.convertedGltfSizeBytes / 1_000_000f), Color(0xFF22C55E))
                                MetricBadge("Comp Ratio", "%.1f%%".format(m.compressionRatioPercent), Color(0xFF00F0FF))
                                MetricBadge("Triangles", "${m.outputTriangleCount}", Color(0xFF38BDF8))
                                MetricBadge("BILT Steps", "${m.biltStepsGenerated}", Color(0xFFFFD700))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BILT Interactive Step Sequence Section
                if (jobState.biltSteps.isNotEmpty()) {
                    val biltSteps = jobState.biltSteps
                    val currentStep = biltSteps.getOrNull(activeBiltStepIndex) ?: biltSteps.first()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BILT INTERACTIVE ASSEMBLY GUIDE (${activeBiltStepIndex + 1} of ${biltSteps.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color(0xFF38BDF8)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { if (activeBiltStepIndex > 0) activeBiltStepIndex-- },
                                enabled = activeBiltStepIndex > 0,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("btn_bilt_prev_step")
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Prev Step", tint = if (activeBiltStepIndex > 0) Color.White else Color(0xFF475569))
                            }
                            IconButton(
                                onClick = { if (activeBiltStepIndex < biltSteps.size - 1) activeBiltStepIndex++ },
                                enabled = activeBiltStepIndex < biltSteps.size - 1,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("btn_bilt_next_step")
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next Step", tint = if (activeBiltStepIndex < biltSteps.size - 1) Color.White else Color(0xFF475569))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // BILT Active Step Detail Card
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, if (currentStep.isCriticalSafetyStep) Color(0xFFEF4444) else Color(0xFF00F0FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFF00F0FF),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = "${currentStep.stepIndex}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp
                                            ),
                                            color = Color(0xFF0F172A),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = currentStep.stepTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = Color.White
                                    )
                                }

                                if (currentStep.isCriticalSafetyStep) {
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "CRITICAL TORQUE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Part: ${currentStep.partName} (OEM #${currentStep.oemPartNumber})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF38BDF8)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentStep.stepInstructionText,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                color = Color(0xFFCBD5E1)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Required Tools & Torque Specs Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentStep.torqueSpecification != null) {
                                    Surface(
                                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFD700))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                            Text(
                                                text = "Torque: ${currentStep.torqueSpecification}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFFFD700)
                                            )
                                        }
                                    }
                                }

                                currentStep.requiredTools.forEach { tool ->
                                    Surface(
                                        color = Color(0xFF0F172A),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Text(
                                            text = "🛠️ $tool",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Color(0xFF94A3B8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Audio/TTS Cue Text Box
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "\"${currentStep.audioCueText}\"",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.5.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // All Step Thumbnails List
                    Text(
                        text = "ASSEMBLY SEQUENCE STEPS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bilt_steps_lazycolumn"),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(biltSteps) { idx, step ->
                            val isCurrent = idx == activeBiltStepIndex
                            Surface(
                                color = if (isCurrent) Color(0xFF0284C7).copy(alpha = 0.3f) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isCurrent) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable { activeBiltStepIndex = idx }
                                    .testTag("item_bilt_step_$idx")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Step ${step.stepIndex}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isCurrent) Color(0xFF00F0FF) else Color(0xFF64748B)
                                        )

                                        Text(
                                            text = step.stepTitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }

                                    if (step.torqueSpecification != null) {
                                        Text(
                                            text = step.torqueSpecification,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text("Close Pipeline", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onViewIn3DViewport("engine_block")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                            .testTag("btn_view_gltf_in_3d")
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Interactive 3D Viewport", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBadge(title: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color(0xFF64748B))
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp), color = valueColor)
    }
}
