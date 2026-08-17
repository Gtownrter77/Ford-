package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * Snackbar Type categorization for styling & iconography
 */
enum class SnackbarType(
    val title: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val borderColor: Color,
    val icon: ImageVector
) {
    GEMINI_ERROR(
        title = "GEMINI AI ENGINE ERROR",
        primaryColor = Color(0xFF00F0FF),
        backgroundColor = Color(0xFF0B192C),
        borderColor = Color(0xFF00F0FF),
        icon = Icons.Default.AutoAwesome
    ),
    MODEL_3D_ERROR(
        title = "3D CAD ENGINE NOTICE",
        primaryColor = Color(0xFFFF9E40),
        backgroundColor = Color(0xFF1E1005),
        borderColor = Color(0xFFFF9E40),
        icon = Icons.Default.ViewInAr
    ),
    NETWORK_ERROR(
        title = "NETWORK CONNECTIVITY ISSUE",
        primaryColor = Color(0xFFEF4444),
        backgroundColor = Color(0xFF1F0D10),
        borderColor = Color(0xFFEF4444),
        icon = Icons.Default.CloudOff
    ),
    WARNING(
        title = "DIAGNOSTIC WARNING",
        primaryColor = Color(0xFFEAB308),
        backgroundColor = Color(0xFF1C1805),
        borderColor = Color(0xFFEAB308),
        icon = Icons.Default.Warning
    ),
    SUCCESS(
        title = "OPERATION COMPLETED",
        primaryColor = Color(0xFF22C55E),
        backgroundColor = Color(0xFF081C10),
        borderColor = Color(0xFF22C55E),
        icon = Icons.Default.CheckCircle
    )
}

/**
 * Payload model for displaying error and feedback snackbars across the app
 */
data class SnackbarPayload(
    val id: Long = System.currentTimeMillis(),
    val type: SnackbarType,
    val message: String,
    val subtext: String? = null,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val durationMillis: Long = 5500L
)

/**
 * High-Tech 3D CAD Holographic Loading Animation
 * Features:
 * - Rotating 3D wireframe geometric polyhedron in real-time projection
 * - Scanning laser beam sweeps vertically with laser particle flare
 * - Dynamic vertex & triangle buffer counter
 * - Blueprint coordinate grid backdrop
 * - Cyclic CAD compilation stage messages
 */
@Composable
fun CadModelLoadingAnimation(
    modifier: Modifier = Modifier,
    title: String = "INITIALIZING 3D CAD ENGINE",
    targetPartName: String? = null,
    progressPercent: Int? = null,
    isCompact: Boolean = false,
    onCancel: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cad_loader_anim")

    // Rotation angles for 3D projection
    val rotationYaw by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cadYaw"
    )

    val rotationPitch by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cadPitch"
    )

    // Vertical scan beam motion (0.0 to 1.0)
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    // Pulsing cyan/amber laser glow
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Cyclic CAD Stage Messages
    val stages = listOf(
        "Tessellating 4.0L SOHC BREP Geometry...",
        "Compiling PBR Specular & Normal Shaders...",
        "Building 14,850 Vertex Buffers & Spatial Index...",
        "Resolving Sub-Assembly Alignment & Dowel Pins...",
        "Calibrating Interactive Orbit & Explode Vectors..."
    )

    var stageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            stageIndex = (stageIndex + 1) % stages.size
        }
    }

    if (isCompact) {
        // Compact Inline 3D Loading Strip (Used inside cards / sheets)
        Surface(
            color = Color(0xFF0F172A).copy(alpha = 0.95f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.7f)),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("compact_cad_loading_indicator")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mini 3D Rotating Cube Canvas
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        drawWireframeCube(
                            yawDeg = rotationYaw,
                            pitchDeg = rotationPitch,
                            scanY = scanProgress,
                            color = Color(0xFF00F0FF),
                            laserColor = Color(0xFFFF9E40)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = targetPartName ?: title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFF00F0FF),
                            maxLines = 1
                        )
                        if (progressPercent != null) {
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    Text(
                        text = stages[stageIndex],
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }
        }
    } else {
        // Full Holographic 3D CAD Loading Overlay
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xE6050B14))
                .testTag("full_cad_loading_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 420.dp),
                color = Color(0xFF0B132B).copy(alpha = 0.95f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, Color(0xFF00F0FF).copy(alpha = pulseGlow)),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top HUD Badge
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
                                color = Color(0xFF00F0FF).copy(alpha = 0.2f),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color(0xFF00F0FF))
                            ) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = Color(0xFF00F0FF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "BILT 3D CAD ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF00F0FF)
                            )
                        }

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text(
                                text = "GLTF 2.0 / PBR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                ),
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Holographic 3D Viewport Simulation Canvas
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF0284C7).copy(alpha = 0.25f),
                                        Color(0xFF030712)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(130.dp)) {
                            // Draw background blueprint grid
                            drawBlueprintGrid(Color(0xFF00F0FF).copy(alpha = 0.15f))

                            // Draw 3D rotating polyhedron wireframe + laser scanning
                            drawWireframeCube(
                                yawDeg = rotationYaw,
                                pitchDeg = rotationPitch,
                                scanY = scanProgress,
                                color = Color(0xFF00F0FF),
                                laserColor = Color(0xFFFF9E40)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title & Target Part
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )

                    if (targetPartName != null) {
                        Text(
                            text = targetPartName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF9E40),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic Stage Message
                    Text(
                        text = stages[stageIndex],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFF38BDF8),
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(0.95f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Linear Progress Indicator or Indeterminate Laser Bar
                    if (progressPercent != null) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "GEOMETRY BUFFERING",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "$progressPercent%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF00F0FF)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF00F0FF),
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF00F0FF),
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    if (onCancel != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF94A3B8)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text("Use Procedural Mesh", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom High-Tech Gemini AI Reasoning & Diagnostics Loading Animation
 * Features:
 * - Pulsing neural orbital rings around an AI mechanic core
 * - Dynamic oscillating audio/spectral waveform bars (FFT visualization)
 * - Real-time animated status statements referencing 2004 Sport Trac 4.0L SOHC specs
 * - Holographic intelligence badge with cyan & amber neon accents
 */
@Composable
fun GeminiAiLoadingAnimation(
    modifier: Modifier = Modifier,
    promptContext: String? = null,
    isAcousticMode: Boolean = false,
    isCompact: Boolean = false,
    onCancel: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_ai_loading")

    // Rotation for outer AI neural ring
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    // Reverse rotation for inner ring
    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRingRotation"
    )

    // Pulsing glow factor
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Waveform phase animation
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Dynamic Ford Sport Trac Diagnostic Statements
    val diagnosticPhrases = if (isAcousticMode) {
        listOf(
            "Analyzing recorded audio spectrum against 245,000+ verified sound signatures...",
            "Evaluating 600 - 850 Hz timing chain rattle & guide wear harmonics...",
            "Checking 3.2kHz - 4.2kHz alternator diode ripple & AC voltage frequencies...",
            "Isolating 350 - 550 Hz hydraulic valve lifter bleed-down signatures...",
            "Synthesizing precision Ford Master Mechanic acoustic report & 3D fix..."
        )
    } else {
        listOf(
            "Interrogating 2004 Ford Explorer Sport Trac 4.0L SOHC TSB Database...",
            "Cross-referencing OBD-II DTCs (P0171, P0300, P0128, P0732) & Sensor PIDs...",
            "Analyzing 5R55E transmission EPC valve body & vacuum leak probabilities...",
            "Calculating Motorcraft OEM torque specs, fastener sizes & part numbers...",
            "Synthesizing interactive step-by-step diagnostic breakdown & 3D BILT guide..."
        )
    }

    var phraseIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1400)
            phraseIndex = (phraseIndex + 1) % diagnosticPhrases.size
        }
    }

    if (isCompact) {
        // Compact Inline Indicator (Used in Gemini Chat View message stream)
        Surface(
            color = Color(0xFF0B192C).copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.6f)),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("gemini_inline_loading_indicator")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Orbital AI Icon
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(36.dp)) {
                        drawAiNeuralCore(
                            outerAngle = ringRotation,
                            innerAngle = innerRingRotation,
                            glow = pulseGlow,
                            primaryColor = Color(0xFF00F0FF),
                            accentColor = Color(0xFFFFB703)
                        )
                    }
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00F0FF),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "GEMINI 3.5 FLASH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF00F0FF)
                            )
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "THINKING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    ),
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        // Mini oscillating waveform
                        Canvas(modifier = Modifier.size(width = 40.dp, height = 16.dp)) {
                            drawMiniWaveform(wavePhase, Color(0xFF00F0FF))
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = diagnosticPhrases[phraseIndex],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        ),
                        maxLines = 2,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    } else {
        // Full Banner AI Loading Container (Used in Dialogs & Deep Acoustic Analyses)
        Surface(
            color = Color(0xFF071224),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, Color(0xFF00F0FF).copy(alpha = pulseGlow)),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("gemini_banner_loading_container"),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Strip
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
                            color = Color(0xFF00F0FF).copy(alpha = 0.15f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFF00F0FF))
                        ) {
                            Box(
                                modifier = Modifier.size(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isAcousticMode) "GEMINI ACOUSTIC AI SPECTRAL ENGINE" else "GEMINI MASTER MECHANIC REASONING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color(0xFF00F0FF)
                            )
                            Text(
                                text = "2004 Ford Explorer Sport Trac 4.0L SOHC V6",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Text(
                            text = "LIVE EVAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Multi-Orbital Neural Core & Oscillating Waveform Centerpiece
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Neural Core
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawAiNeuralCore(
                                outerAngle = ringRotation,
                                innerAngle = innerRingRotation,
                                glow = pulseGlow,
                                primaryColor = Color(0xFF00F0FF),
                                accentColor = Color(0xFFFFB703)
                            )
                        }
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Multi-channel Waveform Spectrum
                    Canvas(
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF030712))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        drawSpectralWaveform(
                            phase = wavePhase,
                            primaryColor = Color(0xFF00F0FF),
                            secondaryColor = Color(0xFFFF6F00)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Diagnostic Stage Text
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFF00F0FF),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = diagnosticPhrases[phraseIndex],
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            ),
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Styled Material 3 Snackbar with Rich Icons, Border Accents & Action Buttons
 */
@Composable
fun CustomAppSnackbar(
    payload: SnackbarPayload,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .testTag("custom_app_snackbar_${payload.type.name.lowercase()}"),
        color = payload.type.backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, payload.type.borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = payload.type.primaryColor.copy(alpha = 0.2f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, payload.type.primaryColor)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = payload.type.icon,
                                contentDescription = null,
                                tint = payload.type.primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = payload.type.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.6.sp
                            ),
                            color = payload.type.primaryColor
                        )
                        Text(
                            text = payload.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            ),
                            color = Color.White,
                            maxLines = 2
                        )
                    }
                }

                if (payload.onDismiss != null) {
                    IconButton(
                        onClick = payload.onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (payload.subtext != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = payload.subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    ),
                    lineHeight = 14.sp
                )
            }

            if (payload.actionLabel != null && payload.onAction != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = payload.onAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = payload.type.primaryColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = payload.actionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Snackbar Host Overlay for rendering active snackbars
 */
@Composable
fun AppSnackbarHost(
    activePayload: SnackbarPayload?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(activePayload?.id) {
        if (activePayload != null) {
            delay(activePayload.durationMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = activePayload != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        activePayload?.let { payload ->
            CustomAppSnackbar(
                payload = payload.copy(
                    onDismiss = {
                        payload.onDismiss?.invoke()
                        onDismiss()
                    }
                )
            )
        }
    }
}

// ---------------- Canvas Drawing Helper Functions ----------------

private fun DrawScope.drawWireframeCube(
    yawDeg: Float,
    pitchDeg: Float,
    scanY: Float,
    color: Color,
    laserColor: Color
) {
    val yawRad = Math.toRadians(yawDeg.toDouble()).toFloat()
    val pitchRad = Math.toRadians(pitchDeg.toDouble()).toFloat()

    val size = min(this.size.width, this.size.height) * 0.42f
    val cx = this.size.width / 2f
    val cy = this.size.height / 2f

    // 8 vertices of a 3D cube [-1..1]
    val rawVerts = listOf(
        floatArrayOf(-1f, -1f, -1f),
        floatArrayOf( 1f, -1f, -1f),
        floatArrayOf( 1f,  1f, -1f),
        floatArrayOf(-1f,  1f, -1f),
        floatArrayOf(-1f, -1f,  1f),
        floatArrayOf( 1f, -1f,  1f),
        floatArrayOf( 1f,  1f,  1f),
        floatArrayOf(-1f,  1f,  1f)
    )

    // Project vertices
    val projected = rawVerts.map { v ->
        val x0 = v[0] * size
        val y0 = v[1] * size
        val z0 = v[2] * size

        // Rotate Yaw (Y-axis)
        val x1 = (x0 * cos(yawRad) + z0 * sin(yawRad)).toFloat()
        val z1 = (-x0 * sin(yawRad) + z0 * cos(yawRad)).toFloat()

        // Rotate Pitch (X-axis)
        val y2 = (y0 * cos(pitchRad) - z1 * sin(pitchRad)).toFloat()
        val z2 = (y0 * sin(pitchRad) + z1 * cos(pitchRad)).toFloat()

        Offset(cx + x1, cy + y2)
    }

    // 12 Edges connecting vertices
    val edges = listOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0), // Front
        Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4), // Back
        Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)  // Connectors
    )

    // Draw edges
    edges.forEach { (a, b) ->
        drawLine(
            color = color.copy(alpha = 0.85f),
            start = projected[a],
            end = projected[b],
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    }

    // Draw vertex beacons
    projected.forEach { p ->
        drawCircle(
            color = color,
            radius = 3.2f,
            center = p
        )
    }

    // Draw horizontal laser scanning beam
    val laserY = this.size.height * scanY
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                laserColor.copy(alpha = 0.9f),
                Color.White,
                laserColor.copy(alpha = 0.9f),
                Color.Transparent
            )
        ),
        start = Offset(0f, laserY),
        end = Offset(this.size.width, laserY),
        strokeWidth = 2.5f
    )
}

private fun DrawScope.drawBlueprintGrid(gridColor: Color) {
    val step = 16f
    var x = 0f
    while (x <= size.width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.8f)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.8f)
        y += step
    }
}

private fun DrawScope.drawAiNeuralCore(
    outerAngle: Float,
    innerAngle: Float,
    glow: Float,
    primaryColor: Color,
    accentColor: Color
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = min(cx, cy) * 0.85f

    // Outer Orbit Ring with glowing dashes
    drawCircle(
        color = primaryColor.copy(alpha = 0.3f * glow),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5f)
    )

    // Outer orbiting satellite node
    val outerRad = Math.toRadians(outerAngle.toDouble())
    val ox = (cx + radius * cos(outerRad)).toFloat()
    val oy = (cy + radius * sin(outerRad)).toFloat()
    drawCircle(
        color = primaryColor,
        radius = 4f * glow,
        center = Offset(ox, oy)
    )

    // Inner Orbit Ring
    val innerRadius = radius * 0.6f
    drawCircle(
        color = accentColor.copy(alpha = 0.35f * glow),
        radius = innerRadius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.2f)
    )

    // Inner orbiting satellite node
    val innerRad = Math.toRadians(innerAngle.toDouble())
    val ix = (cx + innerRadius * cos(innerRad)).toFloat()
    val iy = (cy + innerRadius * sin(innerRad)).toFloat()
    drawCircle(
        color = accentColor,
        radius = 3.2f,
        center = Offset(ix, iy)
    )

    // Center Core Glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.45f * glow),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = radius * 0.5f
        ),
        radius = radius * 0.5f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawMiniWaveform(phase: Float, color: Color) {
    val barCount = 7
    val barWidth = 3.5f
    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

    for (i in 0 until barCount) {
        val wave = abs(sin(phase + i * 0.8f))
        val h = (size.height * 0.25f) + (size.height * 0.7f * wave)
        val x = i * (barWidth + spacing)
        val y = (size.height - h) / 2f

        drawRoundRect(
            color = color,
            topLeft = Offset(x, y),
            size = Size(barWidth, h),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

private fun DrawScope.drawSpectralWaveform(
    phase: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val barCount = 28
    val barWidth = (size.width / barCount) * 0.65f
    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

    for (i in 0 until barCount) {
        // Multi-frequency synthesis simulation
        val freq1 = sin(phase * 1.5f + i * 0.4f)
        val freq2 = cos(phase * 0.8f + i * 0.25f)
        val combined = abs(freq1 * 0.6f + freq2 * 0.4f)

        val barHeight = (size.height * 0.15f) + (size.height * 0.8f * combined)
        val x = i * (barWidth + spacing)
        val y = (size.height - barHeight) / 2f

        val color = if (i % 3 == 0) secondaryColor else primaryColor

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(color, color.copy(alpha = 0.3f)),
                startY = y,
                endY = y + barHeight
            ),
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }
}
