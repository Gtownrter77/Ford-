package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.util.HapticHelper
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArOverlayView(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    onSelectComponent: (Component3DModel) -> Unit,
    onOpenDetailSheet: (Component3DModel) -> Unit,
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

    // AR HUD Overlay Settings
    var showWireframes by remember { mutableStateOf(true) }
    var showDigitalLabels by remember { mutableStateOf(true) }
    var showGuidedSteps by remember { mutableStateOf(false) }
    var showCameraMeasurementDialog by remember { mutableStateOf(false) }
    var showMentorModeDialog by remember { mutableStateOf(false) }
    var activeStepIndex by remember { mutableIntStateOf(0) }
    var torchEnabled by remember { mutableStateOf(false) }
    var simFeedMode by remember { mutableStateOf(!hasCameraPermission) }

    // AR System Filter & Calibration Nudge Offsets
    var labelFilterSystem by remember { mutableStateOf<VehicleSystem?>(null) }
    var isArCalibrating by remember { mutableStateOf(false) }
    var arOffsetDx by remember { mutableFloatStateOf(0f) }
    var arOffsetDy by remember { mutableFloatStateOf(0f) }

    val activeComp = selectedComponent ?: components.firstOrNull()

    if (showMentorModeDialog && activeComp != null) {
        MentorModeDialog(
            component = activeComp,
            onDismiss = { showMentorModeDialog = false }
        )
    }

    if (showCameraMeasurementDialog) {
        CameraMeasurementDialog(
            onDismiss = { showCameraMeasurementDialog = false }
        )
    }

    // Pulsing animation for AR tracking crosshairs
    val infiniteTransition = rememberInfiniteTransition(label = "ar_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val reticleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reticle_rot"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("ar_camera_view_container")
    ) {
        val density = LocalDensity.current
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        val centerX = screenWidthPx / 2f + arOffsetDx
        val centerY = screenHeightPx / 2f + arOffsetDy

        // 1. CAMERA PREVIEW FEED OR ENGINE BAY SIMULATION
        if (hasCameraPermission && !simFeedMode) {
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
                            camera.cameraControl.enableTorch(torchEnabled)
                        } catch (e: Exception) {
                            Log.e("ArOverlayView", "Camera binding failed", e)
                            simFeedMode = true
                        }
                    }, ContextCompat.getMainExecutor(context))
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // High-Tech Simulated Engine Bay Background for Emulator / Offline
            SimulatedEngineBayFeed(
                activeComponent = activeComp,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. AR HUD WIREFRAME & TARGETING OVERLAY CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Grid Lines for AR Spatial Tracking Effect
            if (showWireframes) {
                val gridStep = 80.dp.toPx()
                var x = 0f
                while (x < canvasWidth) {
                    drawLine(
                        color = Color(0xFF0284C7).copy(alpha = 0.12f),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 1f
                    )
                    x += gridStep
                }
                var y = 0f
                while (y < canvasHeight) {
                    drawLine(
                        color = Color(0xFF0284C7).copy(alpha = 0.12f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                    y += gridStep
                }
            }

            // Center AR HUD Reticle
            val reticleRadius = 60.dp.toPx()
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.3f * pulseAlpha),
                radius = reticleRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating HUD Brackets
            val bracketLen = 20.dp.toPx()
            for (i in 0 until 4) {
                val angleRad = Math.toRadians((reticleRotation + i * 90).toDouble())
                val bx = centerX + (reticleRadius + 10.dp.toPx()) * cos(angleRad).toFloat()
                val by = centerY + (reticleRadius + 10.dp.toPx()) * sin(angleRad).toFloat()

                drawCircle(
                    color = Color(0xFFFF6F00),
                    radius = 3.dp.toPx(),
                    center = Offset(bx, by)
                )
            }

            // Draw Wireframe Highlights for discovered engine components
            components.forEach { comp ->
                if (labelFilterSystem == null || comp.system == labelFilterSystem) {
                    val isSelected = comp.id == activeComp?.id

                    // Calculate screen position from 3D model center offset
                    val screenX = centerX + (comp.centerOffset.x * 220.dp.toPx())
                    val screenY = centerY + (-comp.centerOffset.y * 180.dp.toPx()) - 40.dp.toPx()

                    val boxWidth = if (isSelected) 140.dp.toPx() else 90.dp.toPx()
                    val boxHeight = if (isSelected) 100.dp.toPx() else 70.dp.toPx()

                    val rectLeft = screenX - boxWidth / 2f
                    val rectTop = screenY - boxHeight / 2f

                    if (showWireframes) {
                        val compColor = if (isSelected) comp.system.color else Color(0xFF38BDF8).copy(alpha = 0.5f)

                        // Bounding Box Frame
                        drawRoundRect(
                            color = compColor.copy(alpha = if (isSelected) 0.8f * pulseAlpha else 0.3f),
                            topLeft = Offset(rectLeft, rectTop),
                            size = Size(boxWidth, boxHeight),
                            cornerRadius = CornerRadius(12.dp.toPx()),
                            style = Stroke(
                                width = if (isSelected) 2.5.dp.toPx() else 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            )
                        )

                        // Corner Brackets
                        val cornerLen = 16.dp.toPx()
                        // Top-Left
                        drawLine(compColor, Offset(rectLeft, rectTop), Offset(rectLeft + cornerLen, rectTop), 3.dp.toPx())
                        drawLine(compColor, Offset(rectLeft, rectTop), Offset(rectLeft, rectTop + cornerLen), 3.dp.toPx())

                        // Top-Right
                        drawLine(compColor, Offset(rectLeft + boxWidth, rectTop), Offset(rectLeft + boxWidth - cornerLen, rectTop), 3.dp.toPx())
                        drawLine(compColor, Offset(rectLeft + boxWidth, rectTop), Offset(rectLeft + boxWidth, rectTop + cornerLen), 3.dp.toPx())

                        // Bottom-Left
                        drawLine(compColor, Offset(rectLeft, rectTop + boxHeight), Offset(rectLeft + cornerLen, rectTop + boxHeight), 3.dp.toPx())
                        drawLine(compColor, Offset(rectLeft, rectTop + boxHeight), Offset(rectLeft, rectTop + boxHeight - cornerLen), 3.dp.toPx())

                        // Bottom-Right
                        drawLine(compColor, Offset(rectLeft + boxWidth, rectTop + boxHeight), Offset(rectLeft + boxWidth - cornerLen, rectTop + boxHeight), 3.dp.toPx())
                        drawLine(compColor, Offset(rectLeft + boxWidth, rectTop + boxHeight), Offset(rectLeft + boxWidth, rectTop + boxHeight - cornerLen), 3.dp.toPx())
                    }

                    // Leader Line to Label Card for selected component
                    if (isSelected) {
                        val cardX = (screenX + 160.dp.toPx()).coerceAtMost(canvasWidth - 180.dp.toPx())
                        val cardY = (screenY - 120.dp.toPx()).coerceAtLeast(80.dp.toPx())

                        val leaderPath = Path().apply {
                            moveTo(screenX, screenY)
                            lineTo(screenX + 40.dp.toPx(), cardY + 30.dp.toPx())
                            lineTo(cardX - 10.dp.toPx(), cardY + 30.dp.toPx())
                        }

                        drawPath(
                            path = leaderPath,
                            color = Color(0xFFFF6F00),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                            )
                        )

                        drawCircle(
                            color = Color(0xFFFF6F00),
                            radius = 6.dp.toPx(),
                            center = Offset(screenX, screenY)
                        )
                    }
                }
            }
        }

        // 2b. DIGITAL FLOATING LABELS OVERLAID ON PHYSICAL ENGINE COMPONENTS
        if (showDigitalLabels) {
            val scaleX = with(density) { 220.dp.toPx() }
            val scaleY = with(density) { 180.dp.toPx() }
            val yPad = with(density) { 40.dp.toPx() }
            val maxXMargin = with(density) { (screenWidthPx - 140.dp.toPx()).toDp() }
            val maxYMargin = with(density) { (screenHeightPx - 160.dp.toPx()).toDp() }

            components.forEach { comp ->
                if (labelFilterSystem == null || comp.system == labelFilterSystem) {
                    val isSelected = comp.id == activeComp?.id

                    val compScreenX = centerX + (comp.centerOffset.x * scaleX)
                    val compScreenY = centerY + (-comp.centerOffset.y * scaleY) - yPad

                    val compXDp = with(density) { compScreenX.toDp() }
                    val compYDp = with(density) { compScreenY.toDp() }

                    Surface(
                        modifier = Modifier
                            .offset(
                                x = (compXDp - 55.dp).coerceIn(8.dp, maxXMargin),
                                y = (compYDp - 42.dp).coerceIn(120.dp, maxYMargin)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                HapticHelper.triggerComponentHaptic(context, view, haptic, comp)
                                onSelectComponent(comp)
                            }
                            .testTag("ar_digital_label_${comp.id}"),
                        color = if (isSelected) Color(0xFF0F172A).copy(alpha = 0.95f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color(0xFFFF6F00) else comp.system.color
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(comp.system.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = comp.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = Color.White
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFFFF6F00),
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "OEM: ${comp.oemPartNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. TOP AR HUD HEADER, SYSTEM FILTER & TOOLBAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A).copy(alpha = 0.88f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AR DIGITAL COMPONENT LABELS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = if (simFeedMode) "3D Model Physical Placement" else "Live Camera Real-World HUD",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Digital Label Toggle
                        IconButton(
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                showDigitalLabels = !showDigitalLabels
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (showDigitalLabels) Color(0xFF0284C7) else Color(0xFF1E293B),
                                    CircleShape
                                )
                                .testTag("ar_toggle_digital_labels")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Digital Labels",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // AR Calibration Nudge Toggle
                        IconButton(
                            onClick = {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                isArCalibrating = !isArCalibrating
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isArCalibrating) Color(0xFFFF6F00) else Color(0xFF1E293B),
                                    CircleShape
                                )
                                .testTag("ar_toggle_calibrate")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Calibrate AR Grid",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Camera vs Simulation Toggle
                        IconButton(
                            onClick = {
                                if (!hasCameraPermission) {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                } else {
                                    simFeedMode = !simFeedMode
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (simFeedMode) Color(0xFF1E293B) else Color(0xFF0284C7),
                                    CircleShape
                                )
                                .testTag("ar_toggle_sim_camera")
                        ) {
                            Icon(
                                imageVector = if (simFeedMode) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Toggle Camera",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Wireframe Mesh Toggle
                        IconButton(
                            onClick = { showWireframes = !showWireframes },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (showWireframes) Color(0xFF0284C7) else Color(0xFF1E293B),
                                    CircleShape
                                )
                                .testTag("ar_toggle_wireframe")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = "Toggle Wireframe",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Guided Step-by-Step Toggle
                        IconButton(
                            onClick = { showGuidedSteps = !showGuidedSteps },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (showGuidedSteps) Color(0xFFFF6F00) else Color(0xFF1E293B),
                                    CircleShape
                                )
                                .testTag("ar_toggle_guided_steps")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Guided Steps",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Computer Vision Bolt & Gap Measurement Button
                        IconButton(
                            onClick = { showCameraMeasurementDialog = true },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    Color(0xFF0284C7),
                                    CircleShape
                                )
                                .testTag("ar_toggle_camera_measurement")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = "CV Bolt & Gap Measure",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // System Filter Row for Digital Labels
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = labelFilterSystem == null,
                        onClick = { labelFilterSystem = null },
                        label = { Text("All Labels (${components.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0F172A).copy(alpha = 0.85f),
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                items(VehicleSystem.entries) { sys ->
                    val count = components.count { it.system == sys }
                    if (count > 0) {
                        FilterChip(
                            selected = labelFilterSystem == sys,
                            onClick = { labelFilterSystem = sys },
                            label = { Text("${sys.displayName} ($count)", fontSize = 11.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(sys.color)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = sys.color,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF0F172A).copy(alpha = 0.85f),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }

            // AR Alignment Calibration Offset Panel when active
            if (isArCalibrating) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF6F00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Align AR Grid to Engine Frame",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF6F00)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { arOffsetDx -= 15f },
                                modifier = Modifier.size(28.dp).background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Left", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { arOffsetDx += 15f },
                                modifier = Modifier.size(28.dp).background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Right", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { arOffsetDy -= 15f },
                                modifier = Modifier.size(28.dp).background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { arOffsetDy += 15f },
                                modifier = Modifier.size(28.dp).background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            TextButton(
                                onClick = { arOffsetDx = 0f; arOffsetDy = 0f },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text("Reset", fontSize = 10.sp, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            }
        }

        // 4. FLOATING AR COMPONENT CALLOUT CARD
        activeComp?.let { comp ->
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .width(220.dp)
                    .testTag("ar_component_callout"),
                color = Color(0xFF0F172A).copy(alpha = 0.92f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, comp.system.color)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = comp.system.color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = comp.system.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = comp.system.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Icon(
                            Icons.Default.Adjust,
                            contentDescription = null,
                            tint = Color(0xFFFF6F00),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = comp.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = "OEM Part: ${comp.oemPartNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Floating Torque Spec Bar
                    comp.torqueSpecs.firstOrNull()?.let { spec ->
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Handyman, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("TORQUE SPEC", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    Text("${spec.torqueFtLbs} ft-lbs (${spec.torqueNm} Nm)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { onOpenDetailSheet(comp) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guide", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                        }

                        Button(
                            onClick = { showMentorModeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(34.dp)
                                .testTag("ar_callout_mentor_mode_btn"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Voice Mentor", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // 5. GUIDED STEP-BY-STEP HANDS-ON AR OVERLAY BAR
        if (showGuidedSteps && activeComp != null && activeComp.repairSteps.isNotEmpty()) {
            val steps = activeComp.repairSteps
            val currentStep = steps.getOrNull(activeStepIndex) ?: steps.first()

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .testTag("ar_guided_steps_panel"),
                color = Color(0xFF0B132B).copy(alpha = 0.95f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFF6F00))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6F00))
                            ) {
                                Text(
                                    text = "${currentStep.stepNumber}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AR STEP ${currentStep.stepNumber} OF ${steps.size}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFFFF6F00)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showMentorModeDialog = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("ar_hud_mentor_mode_btn")
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "Launch Voice Mentor Mode", tint = Color(0xFF10B981))
                            }
                            IconButton(
                                onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                                enabled = activeStepIndex > 0,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Step", tint = Color.White)
                            }
                            IconButton(
                                onClick = { if (activeStepIndex < steps.size - 1) activeStepIndex++ },
                                enabled = activeStepIndex < steps.size - 1,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Step", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentStep.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = currentStep.instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1)
                    )

                    currentStep.warning?.let { warn ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(warn, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFCA5A5))
                        }
                    }
                }
            }
        }

        // 6. BOTTOM COMPONENT SELECTOR STRIP FOR QUICK TARGETING
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFF0F172A).copy(alpha = 0.9f),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(components) { comp ->
                    val isSelected = activeComp?.id == comp.id

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectComponent(comp) }
                            .testTag("ar_comp_strip_${comp.id}"),
                        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF090D16),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) comp.system.color else Color(0xFF334155)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(comp.system.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = comp.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedEngineBayFeed(
    activeComponent: Component3DModel?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Dark Metallic Engine Bay Floor
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0B132B), Color(0xFF030712))
            )
        )

        // Engine Block Outline (4.0L SOHC V6 Cylinder Bank Canvas Art)
        val engineX = width * 0.25f
        val engineY = height * 0.22f
        val engineW = width * 0.5f
        val engineH = height * 0.45f

        // Cast Iron Engine Block
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(engineX, engineY),
            size = Size(engineW, engineH),
            cornerRadius = CornerRadius(20.dp.toPx())
        )

        // Aluminum Intake Manifold Runners
        for (i in 0..5) {
            val runnerY = engineY + (i * engineH / 6) + 15.dp.toPx()
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(engineX + 20.dp.toPx(), runnerY),
                end = Offset(engineX + engineW - 20.dp.toPx(), runnerY),
                strokeWidth = 8.dp.toPx()
            )
        }

        // Valve Covers & Spark Plug Cables
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(engineX - 30.dp.toPx(), engineY + 20.dp.toPx()),
            size = Size(30.dp.toPx(), engineH - 40.dp.toPx()),
            cornerRadius = CornerRadius(8.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(engineX + engineW, engineY + 20.dp.toPx()),
            size = Size(30.dp.toPx(), engineH - 40.dp.toPx()),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Radiator & Cooling Fan Shroud Front
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(engineX - 20.dp.toPx(), engineY - 50.dp.toPx()),
            size = Size(engineW + 40.dp.toPx(), 35.dp.toPx())
        )

        // Battery Box & Fuse Relays Side
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(width * 0.08f, height * 0.3f),
            size = Size(70.dp.toPx(), 90.dp.toPx()),
            cornerRadius = CornerRadius(10.dp.toPx())
        )

        // Highlight Active Component Spot
        activeComponent?.let { comp ->
            val screenX = (width / 2f) + (comp.centerOffset.x * 220.dp.toPx())
            val screenY = (height / 2f) + (-comp.centerOffset.y * 180.dp.toPx()) - 40.dp.toPx()

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(comp.system.color.copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(screenX, screenY),
                    radius = 90.dp.toPx()
                ),
                center = Offset(screenX, screenY),
                radius = 90.dp.toPx()
            )
        }
    }
}
