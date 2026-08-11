package com.example.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AcousticDiagnosticRepository
import com.example.data.local.AppDatabase
import com.example.service.AudioAnalysisPipeline
import com.example.service.SimulatedEngineScenario
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun AcousticSoundDiagnosticDialog(
    onDismiss: () -> Unit,
    onNavigateToComponent: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize Room Database Repository & Audio Analysis Pipeline
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { AcousticDiagnosticRepository(db.acousticReferenceDao()) }
    val pipeline = remember { AudioAnalysisPipeline(context, scope) }
    val geminiRepo = remember { com.example.data.GeminiDiagnosticRepository() }

    var isAnalyzingWithGemini by remember { mutableStateOf(false) }
    var geminiReport by remember { mutableStateOf<com.example.model.ChatMessage?>(null) }

    val referenceProfiles by repository.allReferenceSounds.collectAsState(initial = emptyList())
    val pipelineState by pipeline.state.collectAsState()

    // Seed initial 4.0L V6 frequency profiles in Room DB if empty
    LaunchedEffect(Unit) {
        repository.seedInitialDatabaseIfEmpty()
    }

    // Permission launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pipeline.startRecording(referenceProfiles)
        } else {
            pipeline.startSimulatedScenario(SimulatedEngineScenario.TIMING_CHAIN_RATTLE, referenceProfiles)
        }
    }

    // Ensure audio recording is cleaned up on dismiss
    DisposableEffect(Unit) {
        onDispose {
            pipeline.stopRecording()
        }
    }

    Dialog(
        onDismissRequest = {
            pipeline.stopRecording()
            onDismiss()
        },
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
                // Header Row
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
                            color = Color(0xFF00F0FF).copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
                        ) {
                            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "4.0L V6 ACOUSTIC DIAGNOSTIC PIPELINE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.8.sp),
                                    color = Color(0xFF00F0FF)
                                )
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AudioRecord PCM + FFT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Realtime Frequency Spectrum vs Room DB Profiles",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            pipeline.stopRecording()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("btn_close_acoustic_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Realtime FFT & Waveform Analyzer Box
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (pipelineState.isRecording) Color(0xFF00F0FF) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Top Status Bar: Source, Decibels & Peak Frequency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (pipelineState.isRecording) Color(0xFF22C55E) else Color(0xFFEF4444))
                                )
                                Text(
                                    text = pipelineState.activeScenarioName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (pipelineState.isRecording) Color(0xFF22C55E) else Color(0xFF94A3B8)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // RMS dBFS Gauge Readout
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = "SPL: %.1f dBFS".format(pipelineState.rmsDecibels),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = when {
                                            pipelineState.rmsDecibels > -20f -> Color(0xFFEF4444)
                                            pipelineState.rmsDecibels > -40f -> Color(0xFFFFD700)
                                            else -> Color(0xFF38BDF8)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Peak Frequency Hz Readout
                                Surface(
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0284C7))
                                ) {
                                    Text(
                                        text = "PEAK: ${pipelineState.peakFrequencyHz} Hz",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        ),
                                        color = Color(0xFF00F0FF),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Realtime FFT Spectrogram Bar Canvas (0Hz to 10,000Hz)
                        val fftResult = pipelineState.fftResult
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                .testTag("fft_spectrum_canvas")
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            // Draw Frequency Grid Reference Lines
                            val gridFreqs = listOf("100Hz", "500Hz", "1kHz", "2.5kHz", "5kHz", "10kHz")
                            val xSteps = gridFreqs.size
                            for (g in 0 until xSteps) {
                                val xPos = (canvasWidth / xSteps) * g
                                drawLine(
                                    color = Color(0xFF334155).copy(alpha = 0.5f),
                                    start = Offset(xPos, 0f),
                                    end = Offset(xPos, canvasHeight),
                                    strokeWidth = 1f
                                )
                            }

                            if (fftResult != null && fftResult.magnitudes.isNotEmpty()) {
                                val mags = fftResult.magnitudes
                                val displayBins = 48
                                val binStep = (mags.size / displayBins).coerceAtLeast(1)
                                val barWidth = canvasWidth / displayBins

                                for (b in 0 until displayBins) {
                                    val sampleIndex = (b * binStep).coerceIn(0, mags.lastIndex)
                                    val magValue = mags[sampleIndex]
                                    val normalizedHeight = (magValue * 6f).coerceIn(0.05f, 1.0f)
                                    val barH = canvasHeight * normalizedHeight
                                    val startX = b * barWidth + 1f
                                    val startY = canvasHeight - barH

                                    val barColor = when {
                                        b < 8 -> Color(0xFF38BDF8) // Sub-bass / Idle
                                        b < 18 -> Color(0xFF00F0FF) // Low Rumble / Piston
                                        b < 30 -> Color(0xFFFFD700) // Valvetrain / Chain
                                        else -> Color(0xFFEF4444) // High Whine / Friction
                                    }

                                    drawRect(
                                        color = barColor,
                                        topLeft = Offset(startX, startY),
                                        size = Size((barWidth - 2f).coerceAtLeast(1f), barH)
                                    )
                                }
                            } else {
                                // Default static baseline waveform
                                val barCount = 48
                                val barWidth = canvasWidth / barCount
                                for (i in 0 until barCount) {
                                    val barH = canvasHeight * 0.12f
                                    drawRect(
                                        color = Color(0xFF334155),
                                        topLeft = Offset(i * barWidth + 1f, canvasHeight - barH),
                                        size = Size(barWidth - 2f, barH)
                                    )
                                }
                            }
                        }

                        // Frequency Axis Labels Below Canvas
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("20Hz", "250Hz", "800Hz", "2.0kHz", "5.0kHz", "10kHz").forEach { label ->
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls Row: Record Mic vs Preset Scenarios + Gemini AI Trigger
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!pipelineState.isRecording) {
                                Button(
                                    onClick = {
                                        if (pipelineState.permissionGranted) {
                                            pipeline.startRecording(referenceProfiles)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_start_mic_record")
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🔴 Record Engine",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { pipeline.stopRecording() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_stop_mic_record")
                                ) {
                                    Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⏸️ Pause Stream",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isAnalyzingWithGemini = true
                                    scope.launch {
                                        val topMatch = pipelineState.topMatch
                                        val fft = pipelineState.fftResult
                                        val energyBreakdown = if (fft != null) {
                                            "Valvetrain/Chain: ${(fft.valvetrainEnergyRatio * 100).toInt()}%, High Whine: ${(fft.highWhineEnergyRatio * 100).toInt()}%, Low Rumble: ${(fft.lowRumbleEnergyRatio * 100).toInt()}%"
                                        } else "N/A"

                                        val report = geminiRepo.analyzeEngineAcousticAudio(
                                            peakFrequencyHz = pipelineState.peakFrequencyHz,
                                            rmsDecibels = pipelineState.rmsDecibels,
                                            sourceName = pipelineState.activeScenarioName,
                                            topMatchTitle = topMatch?.referenceEntity?.title,
                                            topMatchConfidence = topMatch?.matchConfidencePercent,
                                            spectralBreakdown = energyBreakdown
                                        )
                                        geminiReport = report
                                        isAnalyzingWithGemini = false
                                    }
                                },
                                enabled = !isAnalyzingWithGemini,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("btn_analyze_gemini_audio")
                            ) {
                                if (isAnalyzingWithGemini) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Analyzing...",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "✨ Analyze sound",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4.0L V6 Audio Scenario Selector Row
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "4.0L V6 PRESET ENGINE RECORDINGS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scenario_selector_lazyrow")
                    ) {
                        items(SimulatedEngineScenario.values().filter { it != SimulatedEngineScenario.LIVE_MIC }) { scenario ->
                            val isSelected = pipelineState.activeScenarioName == scenario.displayName
                            Surface(
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable {
                                        pipeline.startSimulatedScenario(scenario, referenceProfiles)
                                    }
                                    .testTag("scenario_chip_${scenario.name}")
                            ) {
                                Text(
                                    text = scenario.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gemini AI Acoustic Report Card Overlay (if analyzed or analyzing)
                if (isAnalyzingWithGemini) {
                    Surface(
                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF00F0FF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00F0FF), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Column {
                                Text(
                                    text = "GEMINI AI ACOUSTIC REASONING IN PROGRESS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                                )
                                Text(
                                    text = "Evaluating peak ${pipelineState.peakFrequencyHz} Hz frequency harmonics against 2004 Sport Trac V6 failure signatures...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 10.5.sp)
                                )
                            }
                        }
                    }
                } else if (geminiReport != null) {
                    val report = geminiReport!!
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF00F0FF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "GEMINI AI ACOUSTIC REPORT",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                        color = Color(0xFF00F0FF)
                                    )
                                }

                                Surface(
                                    color = when (report.urgencyLevel) {
                                        "Immediate Attention Needed" -> Color(0xFFEF4444)
                                        "Repair Soon" -> Color(0xFFFF9800)
                                        else -> Color(0xFF22C55E)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = report.urgencyLevel ?: "AI Analyzed",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = report.text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            )

                            if (!report.suggestedComponentId.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        pipeline.stopRecording()
                                        onDismiss()
                                        onNavigateToComponent(report.suggestedComponentId)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                ) {
                                    Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Inspect 3D Component: ${report.suggestedComponentName ?: "Engine System"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Results & Room Database Spectral Matches Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPECTRAL MATCHES (ROOM DATABASE)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color(0xFF38BDF8)
                    )

                    val topMatch = pipelineState.topMatch
                    if (topMatch != null) {
                        Surface(
                            color = if (topMatch.isNormalBaseline) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (topMatch.isNormalBaseline) Color(0xFF22C55E) else Color(0xFFEF4444))
                        ) {
                            Text(
                                text = "TOP: ${topMatch.matchConfidencePercent}% ${topMatch.severityTag}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                ),
                                color = if (topMatch.isNormalBaseline) Color(0xFF22C55E) else Color(0xFFF87171),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // List of Ranked Room Database Matches
                val rankedMatches = pipelineState.rankedMatches
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ranked_matches_lazycolumn"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(rankedMatches) { index, match ->
                        val profile = match.referenceEntity
                        val isTop = index == 0

                        Surface(
                            color = if (isTop) Color(0xFA0F172A) else Color(0xEB1E293B),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (isTop) 1.5.dp else 1.dp,
                                color = when {
                                    match.isNormalBaseline -> Color(0xFF22C55E)
                                    isTop -> Color(0xFF00F0FF)
                                    else -> Color(0xFF334155)
                                }
                            ),
                            shadowElevation = if (isTop) 6.dp else 2.dp
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (match.isNormalBaseline) {
                                            Surface(
                                                color = Color(0xFF22C55E),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "NORMAL",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black
                                                    ),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                color = Color(0xFFEF4444),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "FAULT",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black
                                                    ),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = profile.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }

                                    Surface(
                                        color = if (match.isNormalBaseline) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFF00F0FF).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (match.isNormalBaseline) Color(0xFF22C55E) else Color(0xFF00F0FF))
                                    ) {
                                        Text(
                                            text = "${match.matchConfidencePercent}% Match",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            ),
                                            color = if (match.isNormalBaseline) Color(0xFF22C55E) else Color(0xFF00F0FF),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Band Frequency & Characteristics
                                Text(
                                    text = "Target Freq Range: ${profile.frequencyMinHz} Hz - ${profile.frequencyMaxHz} Hz (Δ ${match.frequencyDeltaHz} Hz)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp
                                    ),
                                    color = Color(0xFF38BDF8)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = profile.soundCharacteristics,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    ),
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Root Cause: ${profile.rootCause}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (match.isNormalBaseline) Color(0xFF4ADE80) else Color(0xFFFFD700)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Fix: ${profile.recommendedFix}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = Color(0xFF94A3B8),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (profile.targetComponentId.isNotEmpty()) {
                                        Button(
                                            onClick = {
                                                pipeline.stopRecording()
                                                onDismiss()
                                                onNavigateToComponent(profile.targetComponentId)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (match.isNormalBaseline) Color(0xFF22C55E) else Color(0xFF0284C7)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .height(28.dp)
                                                .testTag("btn_view_3d_${profile.targetComponentId}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ViewInAr,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "3D View",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Close Button
                Button(
                    onClick = {
                        pipeline.stopRecording()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("btn_close_acoustic_footer")
                ) {
                    Text(
                        text = "Close Acoustic Diagnostics",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
