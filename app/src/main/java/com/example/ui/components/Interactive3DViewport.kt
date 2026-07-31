package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.VehicleSystem
import kotlin.math.*

private data class ProjectedFace(
    val path: Path,
    val avgZ: Float,
    val color: Color,
    val isSelected: Boolean,
    val componentId: String,
    val componentName: String,
    val system: VehicleSystem
)

private data class ProjectedComponentCenter(
    val component: Component3DModel,
    val screenPos: Offset,
    val depthZ: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Interactive3DViewport(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    activeSystemFilter: VehicleSystem,
    onComponentSelect: (Component3DModel) -> Unit,
    onOpenDetailManual: (Component3DModel) -> Unit,
    modifier: Modifier = Modifier
) {
    // 3D Camera Controls State
    var cameraYaw by remember { mutableFloatStateOf(40f) }   // Horizontal rotation deg
    var cameraPitch by remember { mutableFloatStateOf(25f) } // Vertical rotation deg
    var cameraZoom by remember { mutableFloatStateOf(1.0f) }  // Zoom multiplier (0.5 to 2.5)
    var explodeFactor by remember { mutableFloatStateOf(0.0f) } // 0.0 (assembled) to 1.0 (fully exploded)
    var isWireframeMode by remember { mutableStateOf(false) }

    val animatedExplode by animateFloatAsState(targetValue = explodeFactor, label = "explode")

    // Filter components based on system tab
    val visibleComponents = remember(components, activeSystemFilter) {
        if (activeSystemFilter == VehicleSystem.ALL) {
            components
        } else {
            components.filter { it.system == activeSystemFilter }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
            .testTag("3d_viewport_box")
    ) {
        // Main 3D Canvas
        var projectedCenters by remember { mutableStateOf<List<ProjectedComponentCenter>>(emptyList()) }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        cameraYaw = (cameraYaw + dragAmount.x * 0.5f) % 360f
                        cameraPitch = (cameraPitch - dragAmount.y * 0.5f).coerceIn(-80f, 80f)
                    }
                }
                .pointerInput(visibleComponents, cameraYaw, cameraPitch, cameraZoom, animatedExplode) {
                    detectTapGestures { tapOffset ->
                        // Hit test on nearest component center
                        val hit = projectedCenters
                            .filter { sqrt((it.screenPos.x - tapOffset.x).pow(2) + (it.screenPos.y - tapOffset.y).pow(2)) < 90f }
                            .minByOrNull { sqrt((it.screenPos.x - tapOffset.x).pow(2) + (it.screenPos.y - tapOffset.y).pow(2)) }

                        if (hit != null) {
                            onComponentSelect(hit.component)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f
            val scale = min(canvasWidth, canvasHeight) * 0.22f * cameraZoom

            // Draw technical 3D grid floor
            drawGridFloor(centerX, centerY, cameraYaw, cameraPitch, cameraZoom)

            val radYaw = Math.toRadians(cameraYaw.toDouble()).toFloat()
            val radPitch = Math.toRadians(cameraPitch.toDouble()).toFloat()

            val cosY = cos(radYaw)
            val sinY = sin(radYaw)
            val cosP = cos(radPitch)
            val sinP = sin(radPitch)

            val facesToDraw = mutableListOf<ProjectedFace>()
            val newProjectedCenters = mutableListOf<ProjectedComponentCenter>()

            // Transform & Project every component
            visibleComponents.forEach { comp ->
                val isSelected = selectedComponent?.id == comp.id

                // Calculate 3D position with exploded offset vector
                val explodedX = comp.centerOffset.x + comp.explodeVector.x * animatedExplode
                val explodedY = comp.centerOffset.y + comp.explodeVector.y * animatedExplode
                val explodedZ = comp.centerOffset.z + comp.explodeVector.z * animatedExplode

                // 3D Rotation transform for component center
                val rxCenter = explodedX * cosY - explodedZ * sinY
                val rzCenter = explodedX * sinY + explodedZ * cosY
                val ryCenter = explodedY * cosP - rzCenter * sinP
                val finalZCenter = explodedY * sinP + rzCenter * cosP

                val projXCenter = centerX + rxCenter * scale
                val projYCenter = centerY - ryCenter * scale

                newProjectedCenters.add(
                    ProjectedComponentCenter(comp, Offset(projXCenter, projYCenter), finalZCenter)
                )

                // Base color from component system or highlight
                val baseColorHex = comp.faces.firstOrNull()?.colorHex ?: comp.system.hexColor
                val parseColor = parseColorFromHex(baseColorHex)
                val systemColor = if (isSelected) Color(0xFFFFD700) else parseColor

                // Transform vertices
                val projectedVertices = comp.vertices.map { v ->
                    val vx = v.x + explodedX
                    val vy = v.y + explodedY
                    val vz = v.z + explodedZ

                    // Rotate Y (Yaw)
                    val rx = vx * cosY - vz * sinY
                    val rz = vx * sinY + vz * cosY

                    // Rotate X (Pitch)
                    val ry = vy * cosP - rz * sinP
                    val finalZ = vy * sinP + rz * cosP

                    val projX = centerX + rx * scale
                    val projY = centerY - ry * scale

                    Triple(projX, projY, finalZ)
                }

                // Project Faces
                comp.faces.forEach { face ->
                    if (face.vertexIndices.size >= 3) {
                        val path = Path()
                        var sumZ = 0f

                        face.vertexIndices.forEachIndexed { idx, vIndex ->
                            if (vIndex in projectedVertices.indices) {
                                val (px, py, pz) = projectedVertices[vIndex]
                                sumZ += pz
                                if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                        }
                        path.close()

                        val avgZ = sumZ / face.vertexIndices.size

                        // Fake directional lighting based on face orientation
                        val lightShade = (1.0f - (avgZ * 0.15f)).coerceIn(0.4f, 1.2f)
                        val shadedColor = systemColor.copy(
                            red = (systemColor.red * lightShade).coerceIn(0f, 1f),
                            green = (systemColor.green * lightShade).coerceIn(0f, 1f),
                            blue = (systemColor.blue * lightShade).coerceIn(0f, 1f),
                            alpha = if (isWireframeMode) 0.35f else 0.88f
                        )

                        facesToDraw.add(
                            ProjectedFace(
                                path = path,
                                avgZ = avgZ,
                                color = shadedColor,
                                isSelected = isSelected,
                                componentId = comp.id,
                                componentName = comp.name,
                                system = comp.system
                            )
                        )
                    }
                }
            }

            projectedCenters = newProjectedCenters

            // Painter's Algorithm: Sort faces back-to-front by Z depth
            facesToDraw.sortBy { it.avgZ }

            // Render Faces & Outlines
            facesToDraw.forEach { pf ->
                if (!isWireframeMode) {
                    drawPath(pf.path, color = pf.color)
                }

                // Outline stroke
                val strokeColor = if (pf.isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.45f)
                val strokeWidth = if (pf.isSelected) 3.dp.toPx() else 1.dp.toPx()
                drawPath(pf.path, color = strokeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            }

            // Draw interactive 3D node indicators and tags for selected or hovered
            newProjectedCenters.forEach { node ->
                val isSelected = selectedComponent?.id == node.component.id

                // Circle marker
                drawCircle(
                    color = if (isSelected) Color(0xFFFFD700) else node.component.system.color,
                    radius = if (isSelected) 10.dp.toPx() else 6.dp.toPx(),
                    center = node.screenPos
                )
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 4.dp.toPx() else 2.dp.toPx(),
                    center = node.screenPos
                )
            }
        }

        // Top Overlay Bar: Active System & HUD info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF1E293B).copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(activeSystemFilter.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3D VIEW: ${activeSystemFilter.displayName.uppercase()}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Reset Camera & View Controls
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = { isWireframeMode = !isWireframeMode },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f), CircleShape)
                        .testTag("wireframe_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isWireframeMode) Icons.Default.GridOn else Icons.Default.GridOff,
                        contentDescription = "Toggle Wireframe Mode",
                        tint = if (isWireframeMode) Color(0xFFFFD700) else Color.White
                    )
                }

                IconButton(
                    onClick = {
                        cameraYaw = 40f
                        cameraPitch = 25f
                        cameraZoom = 1.0f
                        explodeFactor = 0.0f
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f), CircleShape)
                        .testTag("reset_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusWeak,
                        contentDescription = "Reset 3D Camera",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Controls: Exploded View Slider & Camera Zoom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            color = Color(0xFF0F172A).copy(alpha = 0.92f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selected component quick banner
                if (selectedComponent != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(selectedComponent.system.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = selectedComponent.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "OEM #${selectedComponent.oemPartNumber} • ${selectedComponent.system.displayName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Button(
                            onClick = { onOpenDetailManual(selectedComponent) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("view_repair_manual_btn")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Repair Manual", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    Text(
                        text = "💡 Tap any 3D component or node to view details & repair steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Exploded View & Zoom Sliders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Exploded slider
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EXPLODED VIEW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "${(animatedExplode * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                        Slider(
                            value = explodeFactor,
                            onValueChange = { explodeFactor = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF0284C7),
                                inactiveTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("exploded_slider")
                        )
                    }

                    // Zoom slider
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CAMERA ZOOM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFD700)
                            )
                            Text(
                                text = "${(cameraZoom * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                        Slider(
                            value = cameraZoom,
                            onValueChange = { cameraZoom = it },
                            valueRange = 0.5f..2.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFF6F00),
                                inactiveTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("zoom_slider")
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGridFloor(
    centerX: Float,
    centerY: Float,
    yaw: Float,
    pitch: Float,
    zoom: Float
) {
    val scale = min(size.width, size.height) * 0.22f * zoom
    val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
    val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

    val cosY = cos(radYaw)
    val sinY = sin(radYaw)
    val cosP = cos(radPitch)
    val sinP = sin(radPitch)

    val gridRange = -3..3
    val floorY = -1.8f

    gridRange.forEach { i ->
        val pos = i.toFloat() * 1.2f

        // Line along X
        val p1x = pos
        val p1z = -3.6f
        val p2x = pos
        val p2z = 3.6f

        val rx1 = p1x * cosY - p1z * sinY
        val rz1 = p1x * sinY + p1z * cosY
        val ry1 = floorY * cosP - rz1 * sinP

        val rx2 = p2x * cosY - p2z * sinY
        val rz2 = p2x * sinY + p2z * cosY
        val ry2 = floorY * cosP - rz2 * sinP

        val x1 = centerX + rx1 * scale
        val y1 = centerY - ry1 * scale
        val x2 = centerX + rx2 * scale
        val y2 = centerY - ry2 * scale

        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 1.dp.toPx()
        )

        // Line along Z
        val q1x = -3.6f
        val q1z = pos
        val q2x = 3.6f
        val q2z = pos

        val srx1 = q1x * cosY - q1z * sinY
        val srz1 = q1x * sinY + q1z * cosY
        val sry1 = floorY * cosP - srz1 * sinP

        val srx2 = q2x * cosY - q2z * sinY
        val srz2 = q2x * sinY + q2z * cosY
        val sry2 = floorY * cosP - srz2 * sinP

        val sx1 = centerX + srx1 * scale
        val sy1 = centerY - sry1 * scale
        val sx2 = centerX + srx2 * scale
        val sy2 = centerY - sry2 * scale

        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(sx1, sy1),
            end = Offset(sx2, sy2),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun parseColorFromHex(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(0xFF000000 or colorInt)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        Color(0xFF0284C7)
    }
}
