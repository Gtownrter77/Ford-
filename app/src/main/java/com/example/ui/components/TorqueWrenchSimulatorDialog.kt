package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.MentorTtsManager
import kotlin.math.*

/**
 * Interactive Digital Torque Wrench & Angle Gauge Simulator with haptic "CLICK" feedback,
 * live torque curve display, TTY angle monitoring, and OEM pass/fail validation.
 */
@Composable
fun TorqueWrenchSimulatorDialog(
    fastenerName: String = "Spark Plug AWSF-32PM",
    componentName: String = "4.0L SOHC Engine Head",
    targetTorqueFtLbs: Float = 13.0f,
    socketSize: String = "5/8 in Spark Plug Socket",
    isTty: Boolean = false,
    targetAngleDegrees: Float = 90.0f,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val ttsManager = remember { MentorTtsManager(context) }

    var setpointTorque by remember { mutableFloatStateOf(targetTorqueFtLbs) }
    var currentTorque by remember { mutableFloatStateOf(0.0f) }
    var currentAngleDeg by remember { mutableFloatStateOf(0.0f) }
    var wrenchAngleDeg by remember { mutableFloatStateOf(-45.0f) }

    var isTorqueReached by remember { mutableStateOf(false) }
    var isOverTorqued by remember { mutableStateOf(false) }
    var isClickAudibleTriggered by remember { mutableStateOf(false) }

    // Pulsing animation for Torque Click
    val infiniteTransition = rememberInfiniteTransition(label = "torque_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Evaluate Torque Pass / Fail
    LaunchedEffect(currentTorque) {
        if (currentTorque >= setpointTorque && !isClickAudibleTriggered) {
            isTorqueReached = true
            isClickAudibleTriggered = true
            ttsManager.speakText("CLICK! Target torque of %.0f foot-pounds reached!".format(setpointTorque))
        }

        if (currentTorque > setpointTorque * 1.25f) {
            isOverTorqued = true
        } else {
            isOverTorqued = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
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
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, Color(0xFFFF6F00))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = Color(0xFFFF6F00).copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Color(0xFFFF6F00))
                        ) {
                            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(22.dp))
                            }
                        }

                        Column {
                            Text(
                                text = "DIGITAL TORQUE & ANGLE WRENCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                color = Color(0xFFFF6F00)
                            )
                            Text(
                                text = fastenerName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_torque_wrench_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fastener Spec & Socket Info Banner
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "COMPONENT / LOCATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(text = componentName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "SOCKET DRIVE SIZE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(text = socketSize, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Wrench Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF030712))
                        .border(
                            1.5.dp,
                            when {
                                isOverTorqued -> Color(0xFFEF4444)
                                isTorqueReached -> Color(0xFF10B981)
                                else -> Color(0xFF1E293B)
                            },
                            RoundedCornerShape(18.dp)
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val delta = dragAmount.x * 0.4f + dragAmount.y * 0.2f
                                wrenchAngleDeg = (wrenchAngleDeg + delta).coerceIn(-120f, 180f)

                                // Calculate simulated torque build-up based on angle tightening
                                val netTurnDeg = (wrenchAngleDeg + 45f).coerceAtLeast(0f)
                                currentAngleDeg = netTurnDeg
                                currentTorque = (netTurnDeg * 0.28f * (setpointTorque / 15f)).coerceIn(0f, setpointTorque * 1.5f)

                                if (currentTorque < setpointTorque) {
                                    isClickAudibleTriggered = false
                                    isTorqueReached = false
                                }
                            }
                        }
                        .testTag("torque_wrench_canvas_box")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val center = Offset(w * 0.5f, h * 0.60f)

                        // 1. Draw Target Angle Dial & Ticks
                        val radius = min(w, h) * 0.35f
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 4.dp.toPx())
                        )

                        // Angle Sweep Arc
                        drawArc(
                            color = if (isTorqueReached) Color(0xFF10B981) else Color(0xFFFF6F00).copy(alpha = 0.5f),
                            startAngle = -135f,
                            sweepAngle = currentAngleDeg,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 2. Draw Fastener Hex Head at Center
                        rotate(degrees = currentAngleDeg, pivot = center) {
                            val hexRadius = 40f
                            val hexPath = Path()
                            for (i in 0 until 6) {
                                val rad = Math.toRadians((i * 60).toDouble()).toFloat()
                                val px = center.x + hexRadius * cos(rad)
                                val py = center.y + hexRadius * sin(rad)
                                if (i == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                            }
                            hexPath.close()

                            drawPath(
                                path = hexPath,
                                color = Color(0xFF475569)
                            )
                            drawPath(
                                path = hexPath,
                                color = Color(0xFFFFD700),
                                style = Stroke(width = 3f)
                            )
                        }

                        // 3. Draw Digital Torque Wrench Handle rotating around Fastener Center
                        rotate(degrees = wrenchAngleDeg, pivot = center) {
                            // Socket Attachment
                            drawCircle(
                                color = Color(0xFF94A3B8),
                                radius = 48f,
                                center = center
                            )

                            // Wrench Beam Handle
                            val handleLength = radius * 1.3f
                            drawLine(
                                color = Color(0xFFCBD5E1),
                                start = center,
                                end = Offset(center.x + handleLength, center.y),
                                strokeWidth = 24f,
                                cap = StrokeCap.Round
                            )

                            // Grip handle end
                            drawRoundRect(
                                color = Color(0xFFFF6F00),
                                topLeft = Offset(center.x + handleLength - 80f, center.y - 18f),
                                size = Size(90f, 36f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                            )
                        }
                    }

                    // Digital Readout Screen Box (Top Center)
                    Surface(
                        color = Color(0xFF0F172A).copy(alpha = 0.90f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            2.dp,
                            when {
                                isOverTorqued -> Color(0xFFEF4444)
                                isTorqueReached -> Color(0xFF10B981)
                                else -> Color(0xFFFF6F00)
                            }
                        ),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LIVE TORQUE READOUT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                color = Color(0xFF94A3B8)
                            )

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "%.1f".format(currentTorque),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = when {
                                        isOverTorqued -> Color(0xFFEF4444)
                                        isTorqueReached -> Color(0xFF10B981)
                                        else -> Color(0xFFFFD700)
                                    }
                                )
                                Text(
                                    text = "ft-lbs",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFF6F00),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            if (isTty) {
                                Text(
                                    text = "ANGLE: %.0f° / %.0f° TARGET".format(currentAngleDeg, targetAngleDegrees),
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            // Click Status Indicator
                            AnimatedVisibility(visible = isTorqueReached) {
                                Surface(
                                    color = if (isOverTorqued) Color(0xFFEF4444) else Color(0xFF10B981),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text(
                                        text = if (isOverTorqued) "⚠️ OVERTORQUED!" else "⚡ AUDIBLE CLICK! SPEC SET",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Drag Instruction Prompt
                    Text(
                        text = "👆 Drag wrench handle to rotate & apply torque",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF94A3B8),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Calibration Slider Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SETPOINT:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFF6F00)
                    )

                    Slider(
                        value = setpointTorque,
                        onValueChange = {
                            setpointTorque = it
                            isTorqueReached = false
                            isClickAudibleTriggered = false
                        },
                        valueRange = 5.0f..150.0f,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("slider_setpoint_torque"),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF6F00),
                            activeTrackColor = Color(0xFFFF6F00),
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )

                    Text(
                        text = "%.0f ft-lbs".format(setpointTorque),
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reset & Done Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            currentTorque = 0f
                            currentAngleDeg = 0f
                            wrenchAngleDeg = -45f
                            isTorqueReached = false
                            isOverTorqued = false
                            isClickAudibleTriggered = false
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Wrench")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTorqueReached && !isOverTorqued) Color(0xFF10B981) else Color(0xFFFF6F00)
                        )
                    ) {
                        Text(if (isTorqueReached && !isOverTorqued) "Torque Pass (Done)" else "Close Simulator")
                    }
                }
            }
        }
    }
}
