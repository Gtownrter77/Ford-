package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.SubAssemblyType
import com.example.physics.*
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * Interactive Dialog showcasing 3D Physics Simulation for Exploded Assembly Views,
 * AABB Collisions, Fastener Ejection, and Prismatic/Revolute Assembly Constraints.
 */
@Composable
fun ExplodedPhysicsDialog(
    components: List<Component3DModel>,
    initialSelectedComponent: Component3DModel? = null,
    onDismiss: () -> Unit
) {
    val activeComponent = remember {
        mutableStateOf(initialSelectedComponent ?: components.firstOrNull() ?: generateDefaultPhysicsComponent())
    }

    val physicsSimulator = remember { Physics3DSimulator() }
    var physicsState by remember { mutableStateOf(PhysicsSimulationState()) }

    var cameraYaw by remember { mutableFloatStateOf(45f) }
    var cameraPitch by remember { mutableFloatStateOf(25f) }
    var cameraZoom by remember { mutableFloatStateOf(1.2f) }

    var explodeFactor by remember { mutableFloatStateOf(0.8f) }
    var showBoundingBoxes by remember { mutableStateOf(true) }
    var showConstraintVectors by remember { mutableStateOf(true) }
    var showContactPoints by remember { mutableStateOf(true) }

    var selectedPreset by remember { mutableStateOf(PhysicsPreset.EXPLODED_PHYSICS) }

    // Re-initialize physics simulator whenever active component or preset changes
    LaunchedEffect(activeComponent.value, selectedPreset) {
        val (bodies, constraints) = PhysicsAssemblyFactory.createPhysicsRigidBodiesAndConstraints(
            component = activeComponent.value,
            preset = selectedPreset
        )
        physicsSimulator.setupSimulation(bodies, constraints, selectedPreset)
        physicsSimulator.setExplodeFactor(explodeFactor)
        physicsSimulator.startSimulation()
    }

    // Physics Simulation Loop (~60 FPS update tick)
    LaunchedEffect(Unit) {
        while (true) {
            physicsSimulator.setExplodeFactor(explodeFactor)
            physicsState = physicsSimulator.step(0.016f)
            delay(16)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f),
            color = Color(0xFF070D18),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
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
                            color = Color(0xFF00F0FF).copy(alpha = 0.15f),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Color(0xFF00F0FF))
                        ) {
                            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "RIGID BODY 3D PHYSICS ENGINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color(0xFF00F0FF)
                                )
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AABB & CONSTRAINTS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Exploded View Assembly Collisions & Fastener Physics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_physics_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Physics Presets Selector Row
                Text(
                    text = "SIMULATION PRESETS & ASSEMBLY CONSTRAINTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("physics_preset_lazyrow")
                ) {
                    items(PhysicsPreset.values()) { preset ->
                        val isSelected = selectedPreset == preset
                        Surface(
                            color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                            modifier = Modifier
                                .clickable { selectedPreset = preset }
                                .testTag("chip_preset_${preset.name}")
                        ) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Main 3D Physics Canvas & Telemetry Overlay Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF030712))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .testTag("physics_3d_canvas_box")
                ) {
                    // 3D Physics Interactive Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    cameraYaw = (cameraYaw + dragAmount.x * 0.45f) % 360f
                                    cameraPitch = (cameraPitch - dragAmount.y * 0.45f).coerceIn(-85f, 85f)
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val centerX = canvasWidth / 2f
                        val centerY = canvasHeight / 2f
                        val scale = min(canvasWidth, canvasHeight) * 0.28f * cameraZoom

                        val radYaw = Math.toRadians(cameraYaw.toDouble()).toFloat()
                        val radPitch = Math.toRadians(cameraPitch.toDouble()).toFloat()

                        val cosY = cos(radYaw)
                        val sinY = sin(radYaw)
                        val cosP = cos(radPitch)
                        val sinP = sin(radPitch)

                        // 1. Draw Physics Blueprint Grid Floor
                        val gridSize = 10
                        val step = 0.35f
                        for (i in -gridSize..gridSize) {
                            val p1 = project3DPoint(Point3D(i * step, -0.6f, -gridSize * step), centerX, centerY, scale, cosY, sinY, cosP, sinP)
                            val p2 = project3DPoint(Point3D(i * step, -0.6f, gridSize * step), centerX, centerY, scale, cosY, sinY, cosP, sinP)
                            drawLine(
                                color = Color(0xFF0284C7).copy(alpha = if (i == 0) 0.5f else 0.15f),
                                start = p1,
                                end = p2,
                                strokeWidth = if (i == 0) 1.8f else 1.0f
                            )

                            val p3 = project3DPoint(Point3D(-gridSize * step, -0.6f, i * step), centerX, centerY, scale, cosY, sinY, cosP, sinP)
                            val p4 = project3DPoint(Point3D(gridSize * step, -0.6f, i * step), centerX, centerY, scale, cosY, sinY, cosP, sinP)
                            drawLine(
                                color = Color(0xFF0284C7).copy(alpha = if (i == 0) 0.5f else 0.15f),
                                start = p3,
                                end = p4,
                                strokeWidth = if (i == 0) 1.8f else 1.0f
                            )
                        }

                        // 2. Draw Rigid Bodies & Bounding Boxes
                        physicsState.bodies.forEach { body ->
                            val bodyPos = body.position
                            val baseColor = parseColorFromHex(body.colorHex)

                            // Project vertices or fallback center geometry
                            if (body.vertices.isNotEmpty()) {
                                body.faces.forEach { face ->
                                    if (face.vertexIndices.size >= 3) {
                                        val path = Path()
                                        face.vertexIndices.forEachIndexed { idx, vIndex ->
                                            if (vIndex in body.vertices.indices) {
                                                val v = body.vertices[vIndex]
                                                val worldPos = Point3D(v.x + bodyPos.x, v.y + bodyPos.y, v.z + bodyPos.z)
                                                val proj = project3DPoint(worldPos, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                                if (idx == 0) path.moveTo(proj.x, proj.y) else path.lineTo(proj.x, proj.y)
                                            }
                                        }
                                        path.close()

                                        drawPath(
                                            path = path,
                                            color = baseColor.copy(alpha = 0.70f)
                                        )
                                        drawPath(
                                            path = path,
                                            color = if (body.isFastener) Color(0xFFFFD700) else Color(0xFF00F0FF),
                                            style = Stroke(width = if (body.isFastener) 2.dp.toPx() else 1.dp.toPx())
                                        )
                                    }
                                }
                            } else {
                                // Fallback Box geometry representation
                                val projCenter = project3DPoint(bodyPos, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                drawCircle(
                                    color = baseColor,
                                    radius = if (body.isFastener) 12f else 22f,
                                    center = projCenter
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = if (body.isFastener) 12f else 22f,
                                    center = projCenter,
                                    style = Stroke(width = 1.5f)
                                )
                            }

                            // Render AABB Bounding Boxes
                            if (showBoundingBoxes) {
                                val aabb = body.getCurrentAABB()
                                drawAABBWireframe(aabb, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                            }
                        }

                        // 3. Draw Constraint Vector Lines (Prismatic Sliders & Spring Dampers)
                        if (showConstraintVectors) {
                            physicsState.constraints.forEach { constraint ->
                                when (constraint) {
                                    is AssemblyConstraint3D.PrismaticSlider -> {
                                        val body = physicsState.bodies.firstOrNull { it.id == constraint.bodyId }
                                        if (body != null) {
                                            val startProj = project3DPoint(body.initialPosition, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                            val axisEnd = Point3D(
                                                body.initialPosition.x + constraint.slideAxisVector.x * constraint.maxTravelDistance,
                                                body.initialPosition.y + constraint.slideAxisVector.y * constraint.maxTravelDistance,
                                                body.initialPosition.z + constraint.slideAxisVector.z * constraint.maxTravelDistance
                                            )
                                            val endProj = project3DPoint(axisEnd, centerX, centerY, scale, cosY, sinY, cosP, sinP)

                                            drawLine(
                                                color = Color(0xFFFFD700),
                                                start = startProj,
                                                end = endProj,
                                                strokeWidth = 2.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }

                                    is AssemblyConstraint3D.SpringDamper -> {
                                        val bodyA = physicsState.bodies.firstOrNull { it.id == constraint.bodyAId }
                                        val bodyB = physicsState.bodies.firstOrNull { it.id == constraint.bodyBId }
                                        if (bodyA != null && bodyB != null) {
                                            val pA = project3DPoint(bodyA.position, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                            val pB = project3DPoint(bodyB.position, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                            drawLine(
                                                color = Color(0xFF00F0FF).copy(alpha = 0.8f),
                                                start = pA,
                                                end = pB,
                                                strokeWidth = 1.8.dp.toPx()
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }

                        // 4. Render Collision Contact Impulse Markers
                        if (showContactPoints) {
                            physicsState.activeCollisions.forEach { contact ->
                                val proj = project3DPoint(contact.contactPoint, centerX, centerY, scale, cosY, sinY, cosP, sinP)
                                drawCircle(
                                    color = Color(0xFFEF4444),
                                    radius = 8f,
                                    center = proj
                                )
                                drawCircle(
                                    color = Color.Yellow,
                                    radius = 14f,
                                    center = proj,
                                    style = Stroke(width = 2f)
                                )
                            }
                        }
                    }

                    // Live Telemetry HUD Overlay Top Right
                    Surface(
                        color = Color(0xFF0F172A).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "PHYSICS HUD TELEMETRY",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                color = Color(0xFF00F0FF)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                TelemetryItem("Kinetic Energy", "%.2f J".format(physicsState.totalKineticEnergyJoules), Color(0xFF22C55E))
                                TelemetryItem("Collisions", "${physicsState.activeCollisions.size}", if (physicsState.activeCollisions.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF94A3B8))
                                TelemetryItem("Constraints", "${physicsState.constraints.size}", Color(0xFFFFD700))
                                TelemetryItem("Active Bodies", "${physicsState.bodies.size}", Color(0xFF38BDF8))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Physics Explode Separation Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "EXPLODE FORCE:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00F0FF)
                    )

                    Slider(
                        value = explodeFactor,
                        onValueChange = { explodeFactor = it },
                        valueRange = 0.0f..2.5f,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("slider_physics_explode"),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00F0FF),
                            activeTrackColor = Color(0xFF0284C7),
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )

                    Text(
                        text = "%.1fx".format(explodeFactor),
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Controls & Toggles Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = physicsState.gravityEnabled,
                            onClick = { physicsSimulator.toggleGravity(!physicsState.gravityEnabled) },
                            label = { Text("Gravity (9.8m/s²)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.South, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_toggle_gravity")
                        )

                        FilterChip(
                            selected = showBoundingBoxes,
                            onClick = { showBoundingBoxes = !showBoundingBoxes },
                            label = { Text("AABB Boxes", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.CropFree, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_toggle_aabb")
                        )

                        FilterChip(
                            selected = showConstraintVectors,
                            onClick = { showConstraintVectors = !showConstraintVectors },
                            label = { Text("Sliders & Springs", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_toggle_constraints")
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { physicsSimulator.resetSimulation() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF334155), CircleShape)
                                .testTag("btn_reset_physics")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                if (physicsState.isSimulating) physicsSimulator.pauseSimulation() else physicsSimulator.startSimulation()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF00F0FF), CircleShape)
                                .testTag("btn_play_pause_physics")
                        ) {
                            Icon(
                                imageVector = if (physicsState.isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryItem(title: String, value: String, color: Color) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color(0xFF94A3B8))
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = color)
    }
}

private fun project3DPoint(
    v: Point3D,
    centerX: Float,
    centerY: Float,
    scale: Float,
    cosY: Float,
    sinY: Float,
    cosP: Float,
    sinP: Float
): Offset {
    val rx = v.x * cosY - v.z * sinY
    val rz = v.x * sinY + v.z * cosY
    val ry = v.y * cosP - rz * sinP

    return Offset(
        x = centerX + rx * scale,
        y = centerY - ry * scale
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAABBWireframe(
    aabb: AABB3D,
    centerX: Float,
    centerY: Float,
    scale: Float,
    cosY: Float,
    sinY: Float,
    cosP: Float,
    sinP: Float
) {
    val min = aabb.min
    val max = aabb.max

    val corners = listOf(
        Point3D(min.x, min.y, min.z),
        Point3D(max.x, min.y, min.z),
        Point3D(max.x, max.y, min.z),
        Point3D(min.x, max.y, min.z),
        Point3D(min.x, min.y, max.z),
        Point3D(max.x, min.y, max.z),
        Point3D(max.x, max.y, max.z),
        Point3D(min.x, max.y, max.z)
    ).map { project3DPoint(it, centerX, centerY, scale, cosY, sinY, cosP, sinP) }

    val edges = listOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0),
        Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4),
        Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)
    )

    edges.forEach { (i1, i2) ->
        drawLine(
            color = Color(0xFF00F0FF).copy(alpha = 0.35f),
            start = corners[i1],
            end = corners[i2],
            strokeWidth = 1f
        )
    }
}

private fun parseColorFromHex(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(colorInt or 0xFF000000)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        Color(0xFF0284C7)
    }
}

private fun generateDefaultPhysicsComponent(): Component3DModel {
    return Component3DModel(
        id = "default_physics_assembly",
        name = "4.0L V6 Water Pump & Fasteners",
        system = com.example.model.VehicleSystem.COOLING,
        oemPartNumber = "PW-481-40L",
        description = "Water pump assembly with 4x M8 flange bolts and silicone gasket seal.",
        locationDescription = "Front timing cover cavity",
        difficulty = "Intermediate",
        estimatedTimeMinutes = 45,
        vertices = listOf(Point3D(-0.3f, 0f, -0.3f), Point3D(0.3f, 0f, -0.3f), Point3D(0f, 0.5f, 0.3f)),
        faces = emptyList(),
        centerOffset = Point3D(0f, 0f, 0f),
        explodeVector = Point3D(0f, 1f, 0f),
        torqueSpecs = emptyList(),
        requiredTools = listOf("10mm Socket", "Torque Wrench"),
        repairSteps = emptyList(),
        commonSymptoms = emptyList()
    )
}
