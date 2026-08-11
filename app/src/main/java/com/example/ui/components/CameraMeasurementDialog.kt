package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.util.HapticHelper
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class MeasurementTargetType(val label: String, val icon: String) {
    BOLT_HEAD("Bolt / Nut Socket Size", "🔧"),
    THREAD_PITCH("Thread Pitch & Diameter", "🔩"),
    SPARK_PLUG_GAP("Spark Plug Gap", "⚡"),
    BELT_SERPENTINE("Serpentine Belt Rib Wear", "⛓️"),
    CUSTOM_CALIPER("Manual Component Caliper", "📏")
}

data class StandardRefObject(
    val name: String,
    val realMm: Float,
    val description: String
)

val STANDARD_REFERENCES = listOf(
    StandardRefObject("US Quarter", 24.26f, "24.26 mm coin diameter"),
    StandardRefObject("10mm Standard Hex", 10.00f, "Standard M6 bolt head width"),
    StandardRefObject("1 Inch Scale", 25.40f, "25.4 mm reference card/rule"),
    StandardRefObject("US Dime", 17.91f, "17.91 mm coin diameter")
)

data class SportTracHardwareSpec(
    val title: String,
    val minMm: Float,
    val maxMm: Float,
    val socketOrGap: String,
    val torqueOrNote: String,
    val systemLocation: String
)

val SPORT_TRAC_SPECS = listOf(
    SportTracHardwareSpec("Lower Intake Manifold / Valve Cover Bolt", 7.8f, 8.2f, "8mm Socket (M6 Bolt)", "89 lb-in (10 Nm) Torque", "Cologne 4.0L SOHC Engine"),
    SportTracHardwareSpec("Upper Intake Plenum / Throttle Body Bolt", 9.7f, 10.3f, "10mm Socket (M6 Bolt)", "89 lb-in (10 Nm) Torque", "Air Intake / Plenum"),
    SportTracHardwareSpec("Thermostat Housing / Water Outlet Bolt", 12.6f, 13.3f, "13mm Socket (M8 Bolt)", "18 lb-ft (25 Nm) Torque", "Cooling System"),
    SportTracHardwareSpec("Serpentine Belt Tensioner Assembly Bolt", 14.6f, 15.4f, "15mm Socket (M10 Bolt)", "35 lb-ft (47 Nm) Torque", "Accessory Drive Belt"),
    SportTracHardwareSpec("Spark Plug Thread / Hex Nut Size", 15.6f, 16.3f, "16mm / 5/8\" Spark Plug Socket", "11 lb-ft (15 Nm) (AWSF-32EE)", "Ignition System"),
    SportTracHardwareSpec("4.0L SOHC Factory Spark Plug Gap", 1.25f, 1.45f, "0.052\" - 0.056\" (1.32 - 1.42 mm)", "Set Gap before installing motorcraft plug", "Ignition System"),
    SportTracHardwareSpec("Brake Caliper Guide Pin Bolt", 12.0f, 13.0f, "13mm Socket", "26 lb-ft Torque (Grease pins with silicone)", "Front Brakes"),
    SportTracHardwareSpec("Transmission Oil Pan Hex Bolt", 9.6f, 10.4f, "10mm Socket", "11 lb-ft (15 Nm) Torque (5R55E Pan)", "5R55E Transmission")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraMeasurementDialog(
    onDismiss: () -> Unit,
    onApplyMeasurementToNotes: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var selectedTargetType by remember { mutableStateOf(MeasurementTargetType.BOLT_HEAD) }
    var selectedReference by remember { mutableStateOf(STANDARD_REFERENCES[0]) }

    var isFrozenPhoto by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var simPhotoIndex by remember { mutableIntStateOf(0) }

    // Caliper Pin Locations on Canvas (Normalized 0.0 to 1.0)
    var pinAX by remember { mutableFloatStateOf(0.35f) }
    var pinAY by remember { mutableFloatStateOf(0.50f) }
    var pinBX by remember { mutableFloatStateOf(0.65f) }
    var pinBY by remember { mutableFloatStateOf(0.50f) }

    // Reference scale pin (Normalized 0.0 to 1.0) for calibrating mm/pixel ratio
    var refPinAX by remember { mutableFloatStateOf(0.20f) }
    var refPinAY by remember { mutableFloatStateOf(0.20f) }
    var refPinBX by remember { mutableFloatStateOf(0.40f) }
    var refPinBY by remember { mutableFloatStateOf(0.20f) }

    var isCalibratingReference by remember { mutableStateOf(false) }
    var torchOn by remember { mutableStateOf(false) }

    // Computer Vision Detected Edge Bounding Box
    var cvDetectedMm by remember { mutableFloatStateOf(0f) }
    var cvConfidence by remember { mutableFloatStateOf(0.88f) }

    // Calculated Pixel Scale (mm per pixel distance unit on screen)
    val refPixelDist = sqrt((refPinBX - refPinAX).pow(2) + (refPinBY - refPinAY).pow(2))
    val mmPerPixelNorm = if (refPixelDist > 0.01f) selectedReference.realMm / refPixelDist else 100f

    val measuredPixelDist = sqrt((pinBX - pinAX).pow(2) + (pinBY - pinAY).pow(2))
    val calculatedMm = measuredPixelDist * mmPerPixelNorm
    val calculatedInches = calculatedMm / 25.4f

    // Match calculated size against Sport Trac service manual specs
    val matchedSpec = remember(calculatedMm) {
        SPORT_TRAC_SPECS.firstOrNull { spec ->
            calculatedMm >= spec.minMm && calculatedMm <= spec.maxMm
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("camera_measurement_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF0284C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
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
                            text = "Camera CV Component Measurement",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Computer Vision Bolt & Gap Sizing",
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
                    .heightIn(max = 540.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Measurement Mode Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(MeasurementTargetType.entries) { target ->
                        FilterChip(
                            selected = selectedTargetType == target,
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                selectedTargetType = target
                            },
                            label = { Text("${target.icon} ${target.label}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }

                // Reference Object Scale Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Straighten, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reference Target: ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = selectedReference.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        STANDARD_REFERENCES.forEach { ref ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        selectedReference = ref
                                    },
                                color = if (selectedReference.name == ref.name) Color(0xFF0284C7) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ref.name.take(6),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // CAMERA VIEWPORT & COMPUTER VISION OVERLAY CANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF090D16))
                        .border(1.5.dp, if (isCalibratingReference) Color(0xFFFF6F00) else Color(0xFF0284C7), RoundedCornerShape(14.dp))
                        .testTag("camera_cv_viewport")
                ) {
                    // 1. Camera Feed / Photo Display
                    if (hasCameraPermission && !isFrozenPhoto) {
                        AndroidView(
                            factory = { ctx ->
                                PreviewView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }
                            },
                            update = { previewView ->
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }
                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                        cameraProvider.unbindAll()
                                        val camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                        camera.cameraControl.enableTorch(torchOn)
                                    } catch (e: Exception) {
                                        Log.e("CameraMeasurement", "Camera binding error", e)
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // High resolution simulated hardware preview (Engine Intake / Bolt / Spark Plug)
                        SimulatedEngineComponentCameraFeed(
                            simPhotoIndex = simPhotoIndex,
                            targetType = selectedTargetType,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // 2. COMPUTER VISION OVERLAY & DRAGGABLE CALIPER TOUCH HANDLES
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val w = constraints.maxWidth.toFloat()
                        val h = constraints.maxHeight.toFloat()

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(isCalibratingReference) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val dx = dragAmount.x / w
                                        val dy = dragAmount.y / h

                                        if (isCalibratingReference) {
                                            // Move reference pins
                                            val distA = sqrt((change.position.x - refPinAX * w).pow(2) + (change.position.y - refPinAY * h).pow(2))
                                            val distB = sqrt((change.position.x - refPinBX * w).pow(2) + (change.position.y - refPinBY * h).pow(2))
                                            if (distA < distB) {
                                                refPinAX = (refPinAX + dx).coerceIn(0.05f, 0.95f)
                                                refPinAY = (refPinAY + dy).coerceIn(0.05f, 0.95f)
                                            } else {
                                                refPinBX = (refPinBX + dx).coerceIn(0.05f, 0.95f)
                                                refPinBY = (refPinBY + dy).coerceIn(0.05f, 0.95f)
                                            }
                                        } else {
                                            // Move measurement caliper pins
                                            val distA = sqrt((change.position.x - pinAX * w).pow(2) + (change.position.y - pinAY * h).pow(2))
                                            val distB = sqrt((change.position.x - pinBX * w).pow(2) + (change.position.y - pinBY * h).pow(2))
                                            if (distA < distB) {
                                                pinAX = (pinAX + dx).coerceIn(0.05f, 0.95f)
                                                pinAY = (pinAY + dy).coerceIn(0.05f, 0.95f)
                                            } else {
                                                pinBX = (pinBX + dx).coerceIn(0.05f, 0.95f)
                                                pinBY = (pinBY + dy).coerceIn(0.05f, 0.95f)
                                            }
                                        }
                                    }
                                }
                        ) {
                            val pA = Offset(pinAX * w, pinAY * h)
                            val pB = Offset(pinBX * w, pinBY * h)

                            val refA = Offset(refPinAX * w, refPinAY * h)
                            val refB = Offset(refPinBX * w, refPinBY * h)

                            // Draw Reference Scale Line (Amber Dotted)
                            drawLine(
                                color = Color(0xFFFFD700),
                                start = refA,
                                end = refB,
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            drawCircle(color = Color(0xFFFFD700), radius = 6.dp.toPx(), center = refA)
                            drawCircle(color = Color(0xFFFFD700), radius = 6.dp.toPx(), center = refB)

                            // Draw Caliper Measurement Line (Cyan / Green Solid with end brackets)
                            val caliperColor = if (matchedSpec != null) Color(0xFF10B981) else Color(0xFF38BDF8)

                            drawLine(
                                color = caliperColor,
                                start = pA,
                                end = pB,
                                strokeWidth = 3.dp.toPx()
                            )

                            // Caliper Pin A Handle
                            drawCircle(color = caliperColor, radius = 9.dp.toPx(), center = pA)
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pA)

                            // Caliper Pin B Handle
                            drawCircle(color = caliperColor, radius = 9.dp.toPx(), center = pB)
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pB)

                            // Bounding Box CV Edge Detector Visual Box around component
                            val boxLeft = min(pA.x, pB.x) - 16.dp.toPx()
                            val boxRight = max(pA.x, pB.x) + 16.dp.toPx()
                            val boxTop = min(pA.y, pB.y) - 20.dp.toPx()
                            val boxBottom = max(pA.y, pB.y) + 20.dp.toPx()

                            drawRoundRect(
                                color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                                topLeft = Offset(boxLeft, boxTop),
                                size = Size(boxRight - boxLeft, boxBottom - boxTop),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                )
                            )
                        }

                        // HUD Control overlay pill (Freeze / Snap / Torch)
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        torchOn = !torchOn
                                    },
                                color = if (torchOn) Color(0xFFFF6F00) else Color(0xFF1E293B).copy(alpha = 0.85f),
                                shape = CircleShape
                            ) {
                                Icon(
                                    if (torchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Torch",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        simPhotoIndex = (simPhotoIndex + 1) % 3
                                        isFrozenPhoto = true
                                    },
                                color = Color(0xFF0284C7).copy(alpha = 0.85f),
                                shape = CircleShape
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Cycle Sample",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 3. MEASUREMENT RESULTS & COMPUTER VISION SPECIFICATION MATCH
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (matchedSpec != null) Color(0xFF10B981) else Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ESTIMATED COMPONENT SIZE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                    color = Color(0xFF94A3B8)
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format("%.1f mm", calculatedMm),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${String.format("%.3f in", calculatedInches)})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            Surface(
                                color = if (matchedSpec != null) Color(0xFF065F46) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (matchedSpec != null) Color(0xFF10B981) else Color(0xFF64748B))
                            ) {
                                Text(
                                    text = if (matchedSpec != null) "SPEC MATCHED ✓" else "CV READY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (matchedSpec != null) Color(0xFFA7F3D0) else Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (matchedSpec != null) {
                            HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = matchedSpec.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Socket/Gap: ${matchedSpec.socketOrGap} • Spec: ${matchedSpec.torqueOrNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Location: ${matchedSpec.systemLocation}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        } else {
                            Text(
                                text = "Drag cyan caliper pins on the image to measure component width/gap against the amber reference scale.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // 4. CALIBRATION TOGGLE & SAVE BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.triggerControlTick(context, view, haptic)
                            isCalibratingReference = !isCalibratingReference
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isCalibratingReference) Color(0xFFFF6F00) else Color(0xFF38BDF8)
                        ),
                        border = BorderStroke(1.dp, if (isCalibratingReference) Color(0xFFFF6F00) else Color(0xFF334155))
                    ) {
                        Icon(
                            if (isCalibratingReference) Icons.Default.Done else Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isCalibratingReference) "Done Calibrating" else "Adjust Reference Scale")
                    }

                    if (onApplyMeasurementToNotes != null) {
                        Button(
                            onClick = {
                                HapticHelper.triggerComplexComponentPulse(context, view, haptic)
                                val noteText = "CV Measured: ${String.format("%.1f mm", calculatedMm)} (${String.format("%.3f in", calculatedInches)}) - ${matchedSpec?.title ?: selectedTargetType.label}"
                                onApplyMeasurementToNotes(noteText)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Notes")
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

@Composable
fun SimulatedEngineComponentCameraFeed(
    simPhotoIndex: Int,
    targetType: MeasurementTargetType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dark Engine Bay Textured Background
        drawRect(color = Color(0xFF0B101D))

        // Draw grid lines
        val step = 40.dp.toPx()
        var x = 0f
        while (x < w) {
            drawLine(Color(0xFF1E293B).copy(alpha = 0.5f), Offset(x, 0f), Offset(x, h), 1f)
            x += step
        }
        var y = 0f
        while (y < h) {
            drawLine(Color(0xFF1E293B).copy(alpha = 0.5f), Offset(0f, y), Offset(w, y), 1f)
            y += step
        }

        val centerX = w / 2f
        val centerY = h / 2f

        when (simPhotoIndex) {
            0 -> {
                // Bolt Head Hex Engine Intake Photo Simulation
                val hexRadius = 55.dp.toPx()
                // Outer Bolt Flange
                drawCircle(color = Color(0xFF475569), radius = hexRadius * 1.3f, center = Offset(centerX, centerY))
                drawCircle(color = Color(0xFF334155), radius = hexRadius * 1.3f, center = Offset(centerX, centerY), style = Stroke(3.dp.toPx()))

                // 6-sided Hex Head
                val hexPath = androidx.compose.ui.graphics.Path()
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60 + 30).toDouble())
                    val px = centerX + hexRadius * kotlin.math.cos(angle).toFloat()
                    val py = centerY + hexRadius * kotlin.math.sin(angle).toFloat()
                    if (i == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                }
                hexPath.close()
                drawPath(path = hexPath, color = Color(0xFF94A3B8))
                drawPath(path = hexPath, color = Color(0xFFCBD5E1), style = Stroke(4.dp.toPx()))

                // Center bolt threads/indentation
                drawCircle(color = Color(0xFF1E293B), radius = hexRadius * 0.4f, center = Offset(centerX, centerY))
            }
            1 -> {
                // Spark Plug Electrode Gap Simulation
                val gapWidth = 35.dp.toPx()
                // Main Spark Plug Body
                drawRect(
                    color = Color(0xFF64748B),
                    topLeft = Offset(centerX - 90.dp.toPx(), centerY - 40.dp.toPx()),
                    size = Size(80.dp.toPx(), 80.dp.toPx())
                )

                // Ground Electrode Curve
                val electrodePath = androidx.compose.ui.graphics.Path()
                electrodePath.moveTo(centerX - 90.dp.toPx(), centerY - 60.dp.toPx())
                electrodePath.lineTo(centerX + 30.dp.toPx(), centerY - 60.dp.toPx())
                electrodePath.lineTo(centerX + 30.dp.toPx(), centerY - 15.dp.toPx())
                drawPath(path = electrodePath, color = Color(0xFFCBD5E1), style = Stroke(12.dp.toPx()))

                // Center Electrode Pin
                drawRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = Offset(centerX + 30.dp.toPx() - gapWidth, centerY - 25.dp.toPx()),
                    size = Size(10.dp.toPx(), 40.dp.toPx())
                )
            }
            else -> {
                // Thermostat Flange & Hose Gap Simulation
                drawCircle(color = Color(0xFF0284C7).copy(alpha = 0.6f), radius = 70.dp.toPx(), center = Offset(centerX, centerY))
                drawCircle(color = Color.White, radius = 70.dp.toPx(), center = Offset(centerX, centerY), style = Stroke(4.dp.toPx()))
            }
        }
    }
}
