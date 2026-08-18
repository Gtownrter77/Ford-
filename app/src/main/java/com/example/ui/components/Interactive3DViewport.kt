package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType
import com.example.model.VehicleSystem
import com.example.util.HapticHelper
import com.example.util.MaterialResponse
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * BILT Intelligent 3D Engine™ CAD Render Modes
 */
enum class CadRenderStyle(val label: String, val description: String) {
    BILT_CAD_PBR("BILT 3D PBR", "High-tech BILT CAD vector engine with PBR metallic shading & dynamic glow"),
    BLUEPRINT("Blueprint Draft", "Precision technical CAD engineering draft with cyan grid & laser metrics"),
    XRAY_GHOST("X-Ray Glass", "Semi-transparent solid shell with interior glowing wireframe geometry"),
    METALLIC_OEM("OEM Brushed Metal", "High-contrast brushed metallic gradient shader with ambient specular")
}

private data class ProjectedFace(
    val path: Path,
    val avgZ: Float,
    val color: Color,
    val strokeColor: Color,
    val strokeWidthPx: Float,
    val isSelected: Boolean,
    val isCurrentStepPart: Boolean,
    val componentId: String,
    val normalZ: Float,
    val specGloss: Float = 0f,
    val centerPos: Offset = Offset.Zero
)

private data class ProjectedComponentCenter(
    val component: Component3DModel,
    val screenPos: Offset,
    val depthZ: Float
)

@OptIn(ExperimentalMaterial3Api::class)
enum class ViewportLayerTab {
    CLEAN,
    SHADING,
    EXPLODED,
    ASSEMBLY,
    ANNOTATIONS,
    MENTOR,
    ANIMATION
}

/**
 * Blender-Level 3D Keyframe Animation Tracks
 */
enum class BlenderAnimTrack(val label: String, val icon: String, val description: String) {
    TURNTABLE_360("360° Turntable Orbit", "🔄", "Continuous 360° camera orbit with specular PBR light sweeps"),
    EXPLODE_KEYFRAMES("Exploded Assembly Keyframes", "💥", "Dynamic CAD disassembly keyframe expansion & collapse"),
    CINEMATIC_FLYTHROUGH("Cinematic Camera Fly-By", "🎥", "Multi-angle camera path: ISO -> Front -> Top -> Close-Up"),
    PART_STEP_SEQUENCE("Sequential Part Disassembly", "🔧", "Step-by-step CAD part highlight & assembly sequence")
}

/**
 * Studio Lighting Presets for Blender Viewport Engine
 */
enum class BlenderLightingPreset(val label: String, val colorHex: String, val bgHex: String) {
    STUDIO_SOFTBOX("Studio Softbox", "#38BDF8", "#080F1E"),
    SUNSET_METALLIC("Sunset Metallic", "#F97316", "#1C0A00"),
    CYBER_NEON("Cyber Neon", "#00F0FF", "#030712"),
    WORKSHOP_WARM("Workshop Warm", "#EAB308", "#0F172A")
}

data class MentorRepairStep(
    val id: String,
    val phaseIndex: Int,
    val totalPhases: Int,
    val title: String,
    val description: String,
    val safetyWarning: String? = null,
    val targetComponent: Component3DModel,
    val yaw: Float,
    val pitch: Float,
    val zoom: Float,
    val explodeFactor: Float = 0f,
    val torqueSpec: String? = null,
    val requiredTools: List<String> = emptyList()
)

private fun generateMentorSteps(visibleComponents: List<Component3DModel>): List<MentorRepairStep> {
    if (visibleComponents.isEmpty()) return emptyList()

    val total = visibleComponents.size.coerceAtMost(6)
    val predefinedTitles = listOf(
        "Pre-Service Workspace Setup & Safety Inspection",
        "Hardware Unfastening & Fastener Retention",
        "Component Isolation & Sub-Assembly Inspection",
        "Gasket & Mating Surface Prep",
        "Torque-to-Spec Reassembly & Calibration",
        "Final Diagnostic Sweep & System Check"
    )

    val predefinedInstructions = listOf(
        "Inspect surrounding housing for debris, coolant leaks, or surface corrosion. Ensure battery negative terminal is disconnected before starting.",
        "Loosen mounting fasteners in a diagonal cross pattern to prevent housing warpage. Store fasteners in a labeled magnetic organizer tray.",
        "Carefully disengage component housing from dowel guide pins. Inspect internal sub-assemblies, wiring harnesses, and mating surfaces.",
        "Scrape away old seal residue using a non-marring scraper. Clean mating surfaces with solvent and press new OEM seal into place.",
        "Re-seat component housing over alignment pins. Torque all fasteners in sequence to final OEM specification with a calibrated torque wrench.",
        "Reconnect battery terminal. Perform a diagnostic system sweep and verify operational parameters before test drive."
    )

    val predefinedSafeties = listOf(
        "⚠️ WEAR EYE PROTECTION & FLUID-RESISTANT GLOVES AT ALL TIMES.",
        "⚠️ CAUTION: FASTENERS MAY BE HOT OR UNDER SPRING TENSION.",
        "⚠️ DO NOT FORCIBLY PRY ALUMINUM CASTINGS TO PREVENT CRACKING.",
        "⚠️ DO NOT USE STEEL SCRAPERS ON ALUMINUM MATING FLANGES.",
        "⚠️ STRICTLY ADHERE TO OEM TORQUE SPECIFICATIONS TO PREVENT STRIPPED THREADS.",
        "⚠️ VERIFY ALL HARDWARE IS SECURED BEFORE INITIATING ENGINE START."
    )

    val yawAngles = listOf(35f, 115f, 215f, 295f, 45f, 180f)
    val pitchAngles = listOf(22f, 32f, 18f, 42f, 28f, 35f)
    val zoomLevels = listOf(1.15f, 1.30f, 1.40f, 1.25f, 1.15f, 1.20f)
    val explodeFactors = listOf(0.0f, 0.15f, 0.45f, 0.25f, 0.0f, 0.0f)

    return (0 until total).map { idx ->
        val comp = visibleComponents[idx % visibleComponents.size]
        val torqueVal = comp.torqueSpecs.firstOrNull()?.torqueFtLbs ?: "85"
        val toolsList = if (comp.requiredTools.isNotEmpty()) comp.requiredTools else listOf("10mm Socket", "Torque Wrench", "Magnetic Tray")

        MentorRepairStep(
            id = "mentor_step_${comp.id}_$idx",
            phaseIndex = idx,
            totalPhases = total,
            title = predefinedTitles[idx % predefinedTitles.size],
            description = predefinedInstructions[idx % predefinedInstructions.size],
            safetyWarning = predefinedSafeties[idx % predefinedSafeties.size],
            targetComponent = comp,
            yaw = yawAngles[idx % yawAngles.size],
            pitch = pitchAngles[idx % pitchAngles.size],
            zoom = zoomLevels[idx % zoomLevels.size],
            explodeFactor = explodeFactors[idx % explodeFactors.size],
            torqueSpec = "$torqueVal lb-ft",
            requiredTools = toolsList
        )
    }
}

@Composable
fun Interactive3DViewport(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    activeSystemFilter: VehicleSystem,
    onComponentSelect: (Component3DModel) -> Unit,
    onOpenDetailManual: (Component3DModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    // Viewport Layer Tab State (Declutters HUD)
    var activeLayerTab by remember { mutableStateOf(ViewportLayerTab.CLEAN) }
    var layerControllerState by remember { mutableStateOf(LayerControllerState()) }
    var showLayerControllerDialog by remember { mutableStateOf(false) }

    if (showLayerControllerDialog) {
        LayerControllerDialog(
            components = components,
            state = layerControllerState,
            onStateChange = { layerControllerState = it },
            onDismiss = { showLayerControllerDialog = false }
        )
    }

    // 3D Camera & View State
    var cameraYaw by remember { mutableFloatStateOf(45f) }   // Yaw deg
    var cameraPitch by remember { mutableFloatStateOf(28f) } // Pitch deg
    var cameraZoom by remember { mutableFloatStateOf(1.0f) }  // Zoom scale
    var explodeFactor by remember { mutableFloatStateOf(0.0f) } // Explode 0..1
    var cadRenderStyle by remember { mutableStateOf(CadRenderStyle.BILT_CAD_PBR) }
    val showDimensions = layerControllerState.showDimensions
    val showCalloutLeaders = layerControllerState.showCalloutLeaders
    val showTechnicalAnnotations = layerControllerState.showTechnicalAnnotations
    val showHudInfoCards = layerControllerState.showHudInfoCards
    var showBloomEffect by remember { mutableStateOf(true) }
    var clipPlaneSlice by remember { mutableFloatStateOf(1.0f) } // 0..1 cutaway

    // Sub-Assembly Exploded View & Hardware Filter State
    var selectedSubAssembly by remember { mutableStateOf<SubAssemblyPart?>(null) }
    var subAssemblyTypeFilter by remember { mutableStateOf<SubAssemblyType?>(null) }
    var showPhysicsDialog by remember { mutableStateOf(false) }

    if (showPhysicsDialog) {
        ExplodedPhysicsDialog(
            components = components,
            initialSelectedComponent = selectedComponent,
            onDismiss = { showPhysicsDialog = false }
        )
    }

    // BILT Step-by-Step Intelligent Assembly Engine State
    var isBiltStepMode by remember { mutableStateOf(true) }
    var currentBiltStepIndex by remember { mutableIntStateOf(0) }
    var isPlayingBiltAnimation by remember { mutableStateOf(false) }
    var isVoiceGuidanceMuted by remember { mutableStateOf(false) }

    // Stable marker glow keeps the default viewport from running a perpetual recomposition loop.
    // Interactive camera, mentor, and explicit animation controls remain available below.
    val pulseGlow = 0.7f

    val animatedExplode by animateFloatAsState(targetValue = explodeFactor, label = "explode")
    val textMeasurer = rememberTextMeasurer()

    // Animated Camera State for silky smooth 3D auto-rotations
    val animatedYaw by animateFloatAsState(
        targetValue = cameraYaw,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "animatedYaw"
    )
    val animatedPitch by animateFloatAsState(
        targetValue = cameraPitch,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "animatedPitch"
    )
    val animatedZoom by animateFloatAsState(
        targetValue = cameraZoom,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "animatedZoom"
    )

    // Filter components based on vehicle system tab & layer controller visibility state
    val visibleComponents = remember(components, activeSystemFilter, layerControllerState) {
        components.filter { comp ->
            val systemMatch = (activeSystemFilter == VehicleSystem.ALL || comp.system == activeSystemFilter)
            systemMatch && layerControllerState.isPartVisible(comp)
        }
    }

    // Mentor Mode Repair Procedure Engine State
    val mentorRepairSteps = remember(visibleComponents) { generateMentorSteps(visibleComponents) }
    var activeMentorStepIndex by remember { mutableIntStateOf(0) }
    var completedMentorSteps by remember { mutableStateOf(setOf<Int>()) }
    var autoRotateCameraOnStep by remember { mutableStateOf(true) }
    val mentorCardListState = rememberLazyListState()

    // Sync camera auto-rotation & component highlighting when scrolling Mentor repair steps
    LaunchedEffect(activeMentorStepIndex, activeLayerTab) {
        if (activeLayerTab == ViewportLayerTab.MENTOR && mentorRepairSteps.isNotEmpty()) {
            val step = mentorRepairSteps[activeMentorStepIndex.coerceIn(0, mentorRepairSteps.lastIndex)]
            if (autoRotateCameraOnStep) {
                cameraYaw = step.yaw
                cameraPitch = step.pitch
                cameraZoom = step.zoom
                explodeFactor = step.explodeFactor
            }
            onComponentSelect(step.targetComponent)
            mentorCardListState.animateScrollToItem(activeMentorStepIndex)
        }
    }

    // BILT Assembly Steps sequence
    val currentBiltStepPart = remember(visibleComponents, currentBiltStepIndex) {
        if (visibleComponents.isNotEmpty()) {
            visibleComponents[currentBiltStepIndex.coerceIn(0, visibleComponents.lastIndex)]
        } else null
    }

    // Automatically focus camera on current BILT step part when stepping
    LaunchedEffect(currentBiltStepIndex, isBiltStepMode) {
        if (isBiltStepMode && currentBiltStepPart != null) {
            onComponentSelect(currentBiltStepPart)
        }
    }

    // BILT Auto-play Step Animation Loop
    LaunchedEffect(isPlayingBiltAnimation, visibleComponents) {
        while (isPlayingBiltAnimation && visibleComponents.isNotEmpty()) {
            delay(3200)
            if (currentBiltStepIndex < visibleComponents.lastIndex) {
                currentBiltStepIndex++
            } else {
                currentBiltStepIndex = 0
            }
        }
    }

    // Blender 3D Animation Engine State
    var isBlenderAnimPlaying by remember { mutableStateOf(false) }
    var blenderAnimTrack by remember { mutableStateOf(BlenderAnimTrack.TURNTABLE_360) }
    var blenderLightingPreset by remember { mutableStateOf(BlenderLightingPreset.STUDIO_SOFTBOX) }
    var blenderTimelineProgress by remember { mutableFloatStateOf(0f) } // 0.0f..1.0f
    var blenderPlaybackSpeed by remember { mutableFloatStateOf(1.0f) } // 0.5f, 1.0f, 2.0f, 4.0f
    var isBlenderLoopEnabled by remember { mutableStateOf(true) }

    // Real-Time Frame Engine for Blender 3D Keyframe Animations
    LaunchedEffect(isBlenderAnimPlaying, blenderAnimTrack, blenderPlaybackSpeed, isBlenderLoopEnabled, visibleComponents) {
        if (!isBlenderAnimPlaying) return@LaunchedEffect
        var lastFrameNano = withFrameNanos { it }
        val pi = Math.PI.toFloat()
        val twoPi = 2f * pi

        while (isBlenderAnimPlaying) {
            withFrameNanos { frameTimeNano ->
                val deltaSec = ((frameTimeNano - lastFrameNano) / 1_000_000_000f).coerceIn(0.001f, 0.1f) * blenderPlaybackSpeed
                lastFrameNano = frameTimeNano

                val totalDurationSec = when (blenderAnimTrack) {
                    BlenderAnimTrack.TURNTABLE_360 -> 6.0f
                    BlenderAnimTrack.EXPLODE_KEYFRAMES -> 4.5f
                    BlenderAnimTrack.CINEMATIC_FLYTHROUGH -> 8.0f
                    BlenderAnimTrack.PART_STEP_SEQUENCE -> (visibleComponents.size * 2.2f).coerceAtLeast(4.0f)
                }

                var nextProgress = blenderTimelineProgress + (deltaSec / totalDurationSec)
                if (nextProgress >= 1.0f) {
                    if (isBlenderLoopEnabled) {
                        nextProgress %= 1.0f
                    } else {
                        nextProgress = 1.0f
                        isBlenderAnimPlaying = false
                    }
                }
                blenderTimelineProgress = nextProgress

                // Execute motion keyframe updates
                when (blenderAnimTrack) {
                    BlenderAnimTrack.TURNTABLE_360 -> {
                        cameraYaw = (blenderTimelineProgress * 360f) % 360f
                        cameraPitch = 28f + sin(blenderTimelineProgress * twoPi) * 12f
                    }
                    BlenderAnimTrack.EXPLODE_KEYFRAMES -> {
                        val p = sin(blenderTimelineProgress * pi)
                        explodeFactor = p
                        cameraYaw = (cameraYaw + deltaSec * 22f) % 360f
                    }
                    BlenderAnimTrack.CINEMATIC_FLYTHROUGH -> {
                        val angleRad = blenderTimelineProgress * twoPi
                        cameraYaw = 45f + sin(angleRad) * 180f
                        cameraPitch = 28f + cos(angleRad) * 35f
                        cameraZoom = 1.0f + sin(angleRad) * 0.35f
                    }
                    BlenderAnimTrack.PART_STEP_SEQUENCE -> {
                        if (visibleComponents.isNotEmpty()) {
                            val targetIdx = (blenderTimelineProgress * visibleComponents.size.toFloat()).toInt().coerceIn(0, visibleComponents.lastIndex)
                            if (currentBiltStepIndex != targetIdx) {
                                currentBiltStepIndex = targetIdx
                            }
                        }
                        cameraYaw = (cameraYaw + deltaSec * 18f) % 360f
                    }
                }
            }
        }
    }

    // Total counts for HUD
    val totalVerticesCount = remember(visibleComponents) { visibleComponents.sumOf { it.vertices.size } }
    val totalFacesCount = remember(visibleComponents) { visibleComponents.sumOf { it.faces.size } }

    val canvasBgColor = when (cadRenderStyle) {
        CadRenderStyle.BLUEPRINT -> Color(0xFF001E3D)
        CadRenderStyle.XRAY_GHOST -> Color(0xFF030712)
        CadRenderStyle.METALLIC_OEM -> Color(0xFF0F172A)
        CadRenderStyle.BILT_CAD_PBR -> Color(0xFF080F1E)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBgColor)
            .testTag("3d_viewport_box")
    ) {
        // Tap hit-testing needs the latest projected centers, but updating snapshot state
        // from every Canvas draw would trigger avoidable recompositions.
        val projectedCentersRef = remember {
            arrayOf<List<ProjectedComponentCenter>>(emptyList())
        }

        // BILT 3D Canvas Visualizer (Hardware-Accelerated GLTF Render Canvas)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Auto
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        cameraYaw = (cameraYaw + dragAmount.x * 0.45f) % 360f
                        cameraPitch = (cameraPitch - dragAmount.y * 0.45f).coerceIn(-85f, 85f)
                    }
                }
                .pointerInput(visibleComponents, cameraYaw, cameraPitch, cameraZoom, animatedExplode) {
                    detectTapGestures { tapOffset ->
                        val hit = projectedCentersRef[0]
                            .filter { sqrt((it.screenPos.x - tapOffset.x).pow(2) + (it.screenPos.y - tapOffset.y).pow(2)) < 90f }
                            .minByOrNull { sqrt((it.screenPos.x - tapOffset.x).pow(2) + (it.screenPos.y - tapOffset.y).pow(2)) }

                        if (hit != null) {
                            HapticHelper.triggerComponentHaptic(context, view, haptic, hit.component)
                            onComponentSelect(hit.component)
                            val hitIndex = visibleComponents.indexOfFirst { it.id == hit.component.id }
                            if (hitIndex >= 0) {
                                currentBiltStepIndex = hitIndex
                            }
                        } else {
                            HapticHelper.triggerControlTick(context, view, haptic)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f
            val baseScale = min(canvasWidth, canvasHeight) * 0.22f * animatedZoom

            val radYaw = Math.toRadians(animatedYaw.toDouble()).toFloat()
            val radPitch = Math.toRadians(animatedPitch.toDouble()).toFloat()

            val cosY = cos(radYaw)
            val sinY = sin(radYaw)
            val cosP = cos(radPitch)
            val sinP = sin(radPitch)

            // 1. High-Tech BILT Technical Blueprint CAD Floor
            drawBiltCadGridFloor(
                centerX = centerX,
                centerY = centerY,
                cosY = cosY,
                sinY = sinY,
                cosP = cosP,
                sinP = sinP,
                scale = baseScale,
                renderStyle = cadRenderStyle
            )

            // Realistic Model Ambient Occlusion Ground Contact Drop Shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.70f), Color.Black.copy(alpha = 0.20f), Color.Transparent),
                    center = Offset(centerX, centerY + 90f * cameraZoom),
                    radius = baseScale * 2.2f
                ),
                topLeft = Offset(centerX - baseScale * 2.2f, centerY + 40f * cameraZoom),
                size = androidx.compose.ui.geometry.Size(baseScale * 4.4f, baseScale * 1.1f)
            )

            // BILT Directional Light Vector for PBR CAD Shading (Top-Front-Left keylight)
            val lx = 0.5f
            val ly = 0.85f
            val lz = -0.6f
            val lLen = sqrt(lx * lx + ly * ly + lz * lz)
            val lightVector = Point3D(lx / lLen, ly / lLen, lz / lLen)

            val facesToDraw = mutableListOf<ProjectedFace>()
            val newProjectedCenters = mutableListOf<ProjectedComponentCenter>()

            // 2. Transform & Render Components
            visibleComponents.forEachIndexed { index, comp ->
                val isSelected = selectedComponent?.id == comp.id
                val isCurrentStepPart = isBiltStepMode && currentBiltStepPart?.id == comp.id

                // Component vertices are authored in their own assembly position.
                // Explode vectors are therefore additive offsets only; adding the
                // center offset here would translate every mesh twice.
                val explodedX = comp.explodeVector.x * animatedExplode
                val explodedY = comp.explodeVector.y * animatedExplode
                val explodedZ = comp.explodeVector.z * animatedExplode

                // Check cutaway clipping plane filter
                if (clipPlaneSlice < 1.0f && explodedZ > (clipPlaneSlice * 4.0f - 2.0f)) {
                    return@forEachIndexed
                }

                // Center position transform
                val centerWorldX = comp.centerOffset.x + explodedX
                val centerWorldY = comp.centerOffset.y + explodedY
                val centerWorldZ = comp.centerOffset.z + explodedZ
                val rxCenter = centerWorldX * cosY - centerWorldZ * sinY
                val rzCenter = centerWorldX * sinY + centerWorldZ * cosY
                val ryCenter = centerWorldY * cosP - rzCenter * sinP
                val finalZCenter = centerWorldY * sinP + rzCenter * cosP

                val projXCenter = centerX + rxCenter * baseScale
                val projYCenter = centerY - ryCenter * baseScale

                newProjectedCenters.add(
                    ProjectedComponentCenter(comp, Offset(projXCenter, projYCenter), finalZCenter)
                )

                // Base Color logic with BILT Active Part Glow
                val rawHex = comp.faces.firstOrNull()?.colorHex ?: comp.system.hexColor
                val baseColor = when {
                    isCurrentStepPart -> Color(0xFF00F0FF) // BILT Cyan Active Part Highlight
                    isSelected -> Color(0xFFFFD700)       // Gold Selected Part
                    else -> parseColorFromHex(rawHex)
                }

                // Project Vertices
                val projectedVertices = comp.vertices.map { v ->
                    val vx = v.x + explodedX
                    val vy = v.y + explodedY
                    val vz = v.z + explodedZ

                    val rx = vx * cosY - vz * sinY
                    val rz = vx * sinY + vz * cosY

                    val ry = vy * cosP - rz * sinP
                    val finalZ = vy * sinP + rz * cosP

                    val px = centerX + rx * baseScale
                    val py = centerY - ry * baseScale

                    Triple(px, py, finalZ)
                }

                // Project Faces
                comp.faces.forEach { face ->
                    if (face.vertexIndices.size >= 3) {
                        val path = Path()
                        var sumX = 0f
                        var sumY = 0f
                        var sumZ = 0f

                        face.vertexIndices.forEachIndexed { idx, vIndex ->
                            if (vIndex in projectedVertices.indices) {
                                val (px, py, pz) = projectedVertices[vIndex]
                                sumX += px
                                sumY += py
                                sumZ += pz
                                if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                        }
                        path.close()

                        val count = face.vertexIndices.size.coerceAtLeast(1)
                        val avgZ = sumZ / count
                        val faceCenterPos = Offset(sumX / count, sumY / count)

                        // Compute Surface Normal for Phong / BILT CAD PBR Shading
                        val v0 = comp.vertices.getOrNull(face.vertexIndices[0]) ?: Point3D(0f, 0f, 0f)
                        val v1 = comp.vertices.getOrNull(face.vertexIndices[1]) ?: Point3D(0f, 0f, 0f)
                        val v2 = comp.vertices.getOrNull(face.vertexIndices[2]) ?: Point3D(0f, 0f, 0f)

                        val edge1 = Point3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)
                        val edge2 = Point3D(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z)

                        // Cross Product
                        val nx = edge1.y * edge2.z - edge1.z * edge2.y
                        val ny = edge1.z * edge2.x - edge1.x * edge2.z
                        val nz = edge1.x * edge2.y - edge1.y * edge2.x
                        val nLen = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.001f)

                        val normX = nx / nLen
                        val normY = ny / nLen
                        val normZ = nz / nLen

                        // Lighting intensity (Dot Product)
                        val dotLight = (normX * lightVector.x + normY * lightVector.y + normZ * lightVector.z).coerceIn(-1.0f, 1.0f)
                        val diffuse = max(0.25f, (dotLight + 1.0f) / 2.0f)

                        // Phong Specular Reflection Gloss (Metallic Shine)
                        val reflectZ = 2f * dotLight * normZ - lightVector.z
                        val specGloss = if (reflectZ > 0f) reflectZ.pow(12f) * 0.38f else 0f

                        val (shadedFill, strokeCol, strokeW) = when (cadRenderStyle) {
                            CadRenderStyle.BILT_CAD_PBR -> {
                                val glowBoost = if (isCurrentStepPart) 0.35f * pulseGlow else 0.0f
                                val fill = Color(
                                    red = (baseColor.red * (0.35f + 0.65f * diffuse) + specGloss + glowBoost).coerceIn(0f, 1f),
                                    green = (baseColor.green * (0.35f + 0.65f * diffuse) + specGloss + glowBoost).coerceIn(0f, 1f),
                                    blue = (baseColor.blue * (0.35f + 0.65f * diffuse) + specGloss + glowBoost).coerceIn(0f, 1f),
                                    alpha = 0.95f
                                )
                                val stroke = when {
                                    isCurrentStepPart -> Color(0xFF00F0FF)
                                    isSelected -> Color(0xFFFFD700)
                                    else -> Color(0xFF1E293B)
                                }
                                val width = when {
                                    isCurrentStepPart -> (3.5f * pulseGlow).dp.toPx()
                                    isSelected -> 3.dp.toPx()
                                    else -> 1.2f.dp.toPx()
                                }
                                Triple(fill, stroke, width)
                            }
                            CadRenderStyle.BLUEPRINT -> {
                                val fill = when {
                                    isCurrentStepPart -> Color(0xFF00F0FF).copy(alpha = 0.55f)
                                    isSelected -> Color(0xFFFFD700).copy(alpha = 0.45f)
                                    else -> Color(0xFF003865).copy(alpha = 0.35f)
                                }
                                val stroke = when {
                                    isCurrentStepPart -> Color(0xFF00F0FF)
                                    isSelected -> Color(0xFFFFD700)
                                    else -> Color(0xFF38BDF8)
                                }
                                val width = if (isCurrentStepPart || isSelected) 3.dp.toPx() else 1.dp.toPx()
                                Triple(fill, stroke, width)
                            }
                            CadRenderStyle.XRAY_GHOST -> {
                                val fill = baseColor.copy(alpha = 0.18f)
                                val stroke = when {
                                    isCurrentStepPart -> Color(0xFF00F0FF)
                                    isSelected -> Color(0xFFFFD700)
                                    else -> baseColor.copy(alpha = 0.85f)
                                }
                                val width = if (isCurrentStepPart || isSelected) 3.dp.toPx() else 1.5f.dp.toPx()
                                Triple(fill, stroke, width)
                            }
                            CadRenderStyle.METALLIC_OEM -> {
                                val shine = (diffuse * 1.25f + specGloss * 1.5f).coerceIn(0.2f, 1.0f)
                                val fill = Color(
                                    red = (baseColor.red * shine + specGloss + 0.08f).coerceIn(0f, 1f),
                                    green = (baseColor.green * shine + specGloss + 0.08f).coerceIn(0f, 1f),
                                    blue = (baseColor.blue * shine + specGloss + 0.12f).coerceIn(0f, 1f),
                                    alpha = 0.96f
                                )
                                val stroke = when {
                                    isCurrentStepPart -> Color(0xFF00F0FF)
                                    isSelected -> Color(0xFFFFD700)
                                    else -> Color.White.copy(alpha = 0.6f)
                                }
                                val width = if (isCurrentStepPart || isSelected) 3.dp.toPx() else 1.2f.dp.toPx()
                                Triple(fill, stroke, width)
                            }
                        }

                        facesToDraw.add(
                            ProjectedFace(
                                path = path,
                                avgZ = avgZ,
                                color = shadedFill,
                                strokeColor = strokeCol,
                                strokeWidthPx = strokeW,
                                isSelected = isSelected,
                                isCurrentStepPart = isCurrentStepPart,
                                componentId = comp.id,
                                normalZ = normZ,
                                specGloss = specGloss,
                                centerPos = faceCenterPos
                            )
                        )
                    }
                }

                // 3. Transform & Render Connected Sub-Assemblies (Bolts, Screws, Washers, Gaskets, Belts)
                if (comp.subAssemblies.isNotEmpty()) {
                    comp.subAssemblies.forEach { subPart ->
                        if (subAssemblyTypeFilter != null && subPart.type != subAssemblyTypeFilter) {
                            return@forEach
                        }

                        val isSubSelected = selectedSubAssembly?.id == subPart.id

                        // Subassembly meshes already include their local offsets.
                        // Anchor them once to the parent assembly's world center,
                        // then apply only the requested exploded-view separation.
                        val subExplodedX = comp.centerOffset.x + explodedX + subPart.explodeDirection.x * animatedExplode * subPart.explodeDistanceMultiplier
                        val subExplodedY = comp.centerOffset.y + explodedY + subPart.explodeDirection.y * animatedExplode * subPart.explodeDistanceMultiplier
                        val subExplodedZ = comp.centerOffset.z + explodedZ + subPart.explodeDirection.z * animatedExplode * subPart.explodeDistanceMultiplier

                        val subBaseColor = when {
                            isSubSelected -> Color(0xFFFFD700)
                            subPart.type == SubAssemblyType.GASKET -> Color(0xFF38BDF8)
                            subPart.type == SubAssemblyType.SEAL_O_RING -> Color(0xFFF97316)
                            subPart.type == SubAssemblyType.BOLT || subPart.type == SubAssemblyType.SCREW -> Color(0xFFE2E8F0)
                            subPart.type == SubAssemblyType.WASHER -> Color(0xFFCBD5E1)
                            subPart.type == SubAssemblyType.BELT -> Color(0xFF334155)
                            subPart.type == SubAssemblyType.SPARK_PLUG -> Color(0xFFF8FAFC)
                            else -> Color(0xFF94A3B8)
                        }

                        // Project SubAssembly Vertices
                        val subProjVerts = subPart.vertices.map { v ->
                            val vx = v.x + subExplodedX
                            val vy = v.y + subExplodedY
                            val vz = v.z + subExplodedZ

                            val rx = vx * cosY - vz * sinY
                            val rz = vx * sinY + vz * cosY
                            val ry = vy * cosP - rz * sinP
                            val finalZ = vy * sinP + rz * cosP

                            val px = centerX + rx * baseScale
                            val py = centerY - ry * baseScale
                            Triple(px, py, finalZ)
                        }

                        // Project SubAssembly Faces
                        subPart.faces.forEach { face ->
                            if (face.vertexIndices.size >= 3) {
                                val path = Path()
                                var sumX = 0f
                                var sumY = 0f
                                var sumZ = 0f
                                face.vertexIndices.forEachIndexed { idx, vIndex ->
                                    if (vIndex in subProjVerts.indices) {
                                        val (px, py, pz) = subProjVerts[vIndex]
                                        sumX += px
                                        sumY += py
                                        sumZ += pz
                                        if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                                    }
                                }
                                path.close()
                                val count = face.vertexIndices.size.coerceAtLeast(1)
                                val avgZ = sumZ / count
                                val faceCenterPos = Offset(sumX / count, sumY / count)

                                val v0 = subPart.vertices.getOrNull(face.vertexIndices[0]) ?: Point3D(0f, 0f, 0f)
                                val v1 = subPart.vertices.getOrNull(face.vertexIndices[1]) ?: Point3D(0f, 0f, 0f)
                                val v2 = subPart.vertices.getOrNull(face.vertexIndices[2]) ?: Point3D(0f, 0f, 0f)

                                val edge1 = Point3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)
                                val edge2 = Point3D(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z)

                                val nx = edge1.y * edge2.z - edge1.z * edge2.y
                                val ny = edge1.z * edge2.x - edge1.x * edge2.z
                                val nz = edge1.x * edge2.y - edge1.y * edge2.x
                                val nLen = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.001f)

                                val normX = nx / nLen
                                val normY = ny / nLen
                                val normZ = nz / nLen

                                val dotLight = (normX * lightVector.x + normY * lightVector.y + normZ * lightVector.z).coerceIn(-1.0f, 1.0f)
                                val diffuse = max(0.35f, (dotLight + 1.0f) / 2.0f)
                                val reflectZ = 2f * dotLight * normZ - lightVector.z

                                // Material-aware Canvas highlight approximation. These values shape
                                // the hand-written highlight only; this is not a GPU PBR shader.
                                val specGloss = MaterialResponse.specularHighlight(
                                    reflectZ = reflectZ,
                                    metallicFactor = subPart.metallicFactor,
                                    roughnessFactor = subPart.roughnessFactor
                                )

                                val shadedFill = Color(
                                    red = (subBaseColor.red * diffuse + specGloss).coerceIn(0f, 1f),
                                    green = (subBaseColor.green * diffuse + specGloss).coerceIn(0f, 1f),
                                    blue = (subBaseColor.blue * diffuse + specGloss).coerceIn(0f, 1f),
                                    alpha = 0.98f
                                )

                                val strokeCol = when {
                                    isSubSelected -> Color(0xFFFFD700)
                                    subPart.type == SubAssemblyType.GASKET || subPart.type == SubAssemblyType.SEAL_O_RING -> Color(0xFF00F0FF)
                                    else -> Color.White.copy(alpha = 0.7f)
                                }

                                val strokeW = if (isSubSelected) 3.5f.dp.toPx() else 1.2f.dp.toPx()

                                facesToDraw.add(
                                    ProjectedFace(
                                        path = path,
                                        avgZ = avgZ,
                                        color = shadedFill,
                                        strokeColor = strokeCol,
                                        strokeWidthPx = strokeW,
                                        isSelected = isSubSelected,
                                        isCurrentStepPart = false,
                                        componentId = comp.id,
                                        normalZ = normZ,
                                        specGloss = specGloss,
                                        centerPos = faceCenterPos
                                    )
                                )
                            }
                        }
                    }
                }
            }

            projectedCentersRef[0] = newProjectedCenters

            // Sort faces back to front (Painter's algorithm Z-sorting)
            facesToDraw.sortBy { it.avgZ }

            // Render Shaded Faces & Outlines
            facesToDraw.forEach { pf ->
                drawPath(pf.path, color = pf.color)
                drawPath(pf.path, color = pf.strokeColor, style = Stroke(width = pf.strokeWidthPx, cap = StrokeCap.Round))
            }

            // Post-Processing Bloom Pass for Vibrant Metallic Surfaces & Lighting
            if (showBloomEffect) {
                facesToDraw.filter { it.specGloss > 0.28f || it.isSelected || it.isCurrentStepPart }.forEach { pf ->
                    val glowRadius = (pf.specGloss * 55f + if (pf.isCurrentStepPart || pf.isSelected) 35f else 20f).coerceAtMost(95f).dp.toPx()
                    val glowAlpha = (pf.specGloss * 0.50f + if (pf.isCurrentStepPart) 0.35f else 0.15f).coerceIn(0.12f, 0.65f)
                    val glowColor = when {
                        pf.isCurrentStepPart -> Color(0xFF00F0FF)
                        pf.isSelected -> Color(0xFFFFD700)
                        else -> Color(0xFFE2E8F0)
                    }

                    // Soft multi-pass radial bloom aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = glowAlpha),
                                glowColor.copy(alpha = glowAlpha * 0.35f),
                                Color.Transparent
                            ),
                            center = pf.centerPos,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = pf.centerPos
                    )

                    // Anamorphic horizontal lens flare streaks for peak specular reflections
                    if (pf.specGloss > 0.40f || pf.isSelected || pf.isCurrentStepPart) {
                        val streakLen = (pf.specGloss * 85f + 25f).dp.toPx()
                        drawLine(
                            color = glowColor.copy(alpha = glowAlpha * 0.75f),
                            start = Offset(pf.centerPos.x - streakLen, pf.centerPos.y),
                            end = Offset(pf.centerPos.x + streakLen, pf.centerPos.y),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = glowColor.copy(alpha = glowAlpha * 0.40f),
                            start = Offset(pf.centerPos.x, pf.centerPos.y - streakLen * 0.4f),
                            end = Offset(pf.centerPos.x, pf.centerPos.y + streakLen * 0.4f),
                            strokeWidth = 1.2f.dp.toPx()
                        )
                    }
                }
            }

            // Draw Node Reticle Indicators & Pulsing BILT Target Markers (Only for selected or active step parts to prevent clutter)
            newProjectedCenters.forEachIndexed { idx, node ->
                val isSelected = selectedComponent?.id == node.component.id
                val isCurrentStepPart = isBiltStepMode && currentBiltStepPart?.id == node.component.id

                // Only draw prominent target reticles for selected or active step parts
                if (isSelected || isCurrentStepPart) {
                    val markerColor = if (isCurrentStepPart) Color(0xFF00F0FF) else Color(0xFFFFD700)
                    val markerRadius = if (isCurrentStepPart) (12.dp.toPx() * pulseGlow) else 9.dp.toPx()

                    // Outer pulsing ring for active BILT step part
                    if (isCurrentStepPart) {
                        drawCircle(
                            color = Color(0xFF00F0FF).copy(alpha = 0.35f * pulseGlow),
                            radius = markerRadius + 16.dp.toPx(),
                            center = node.screenPos
                        )
                    }

                    drawCircle(
                        color = markerColor,
                        radius = markerRadius,
                        center = node.screenPos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = node.screenPos
                    )
                } else if (activeLayerTab == ViewportLayerTab.ANNOTATIONS) {
                    // In Annotations mode, draw tiny subtle pinpoints for unselected components
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = 2.5f.dp.toPx(),
                        center = node.screenPos
                    )
                }
            }

            // 3. Draw BILT High-Tech Callout Leaders & Floating Technical Annotations with Torque Specs & Part IDs
            if ((showTechnicalAnnotations || showCalloutLeaders) && visibleComponents.isNotEmpty()) {
                newProjectedCenters.forEachIndexed { index, node ->
                    val isCurrent = isBiltStepMode && currentBiltStepPart?.id == node.component.id
                    val isSelected = selectedComponent?.id == node.component.id

                    val shouldDrawCallout = (isCurrent || isSelected) && showCalloutLeaders
                    val shouldDrawTechAnnotation = showTechnicalAnnotations && !shouldDrawCallout

                    if (shouldDrawCallout) {
                        val leaderStart = node.screenPos
                        val leaderEnd = Offset(leaderStart.x + 70f, leaderStart.y - 60f)

                        val accentCol = if (isCurrent) Color(0xFF00F0FF) else Color(0xFFFFD700)

                        // Leader line
                        drawLine(
                            color = accentCol,
                            start = leaderStart,
                            end = leaderEnd,
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = accentCol,
                            start = leaderEnd,
                            end = Offset(leaderEnd.x + 30f, leaderEnd.y),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Callout Text Box Background Card (Prevents Text Overlap)
                        val calloutBgTopLeft = Offset(leaderEnd.x + 8f, leaderEnd.y - 28f)
                        val cardWidth = 250f
                        val cardHeight = 44f

                        drawRoundRect(
                            color = Color(0xFA0F172A),
                            topLeft = calloutBgTopLeft,
                            size = Size(cardWidth, cardHeight),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                        drawRoundRect(
                            color = accentCol,
                            topLeft = calloutBgTopLeft,
                            size = Size(cardWidth, cardHeight),
                            cornerRadius = CornerRadius(10f, 10f),
                            style = Stroke(width = 1.2f.dp.toPx())
                        )

                        // Callout Text Box Content
                        val stepNum = visibleComponents.indexOfFirst { it.id == node.component.id } + 1
                        val calloutText = "STEP $stepNum: ${node.component.name.uppercase()}"
                        val torqueVal = node.component.torqueSpecs.firstOrNull()?.torqueFtLbs ?: "85"
                        val specText = "OEM #${node.component.oemPartNumber} • $torqueVal LB-FT"

                        drawText(
                            textMeasurer = textMeasurer,
                            text = calloutText,
                            topLeft = Offset(calloutBgTopLeft.x + 10f, calloutBgTopLeft.y + 6f),
                            style = TextStyle(
                                color = accentCol,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            size = Size(max(1f, cardWidth - 12f), max(1f, cardHeight - 8f))
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = specText,
                            topLeft = Offset(calloutBgTopLeft.x + 10f, calloutBgTopLeft.y + 24f),
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.90f),
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            size = Size(max(1f, cardWidth - 12f), max(1f, cardHeight - 26f))
                        )
                    } else if (shouldDrawTechAnnotation) {
                        // Floating Technical Annotation Label for orbit mode
                        val dx = if (index % 2 == 0) 50f else -180f
                        val dy = if (index % 3 == 0) -35f else -55f
                        val leaderStart = node.screenPos
                        val leaderEnd = Offset(leaderStart.x + dx, leaderStart.y + dy)

                        val accentCol = Color(0xFF38BDF8)

                        // Subtle leader line connecting 3D point to floating label
                        drawLine(
                            color = accentCol.copy(alpha = 0.6f),
                            start = leaderStart,
                            end = leaderEnd,
                            strokeWidth = 1.2f.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        val cardWidth = 195f
                        val cardHeight = 36f
                        val calloutBgTopLeft = Offset(
                            if (dx > 0) leaderEnd.x + 4f else leaderEnd.x - cardWidth - 4f,
                            leaderEnd.y - cardHeight / 2f
                        )

                        drawRoundRect(
                            color = Color(0xEE0F172A),
                            topLeft = calloutBgTopLeft,
                            size = Size(cardWidth, cardHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        drawRoundRect(
                            color = accentCol.copy(alpha = 0.7f),
                            topLeft = calloutBgTopLeft,
                            size = Size(cardWidth, cardHeight),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 1.dp.toPx())
                        )

                        val torqueVal = node.component.torqueSpecs.firstOrNull()?.torqueFtLbs ?: "85"
                        val titleText = node.component.name.uppercase()
                        val infoText = "OEM #${node.component.oemPartNumber} • 🔩 ${torqueVal} LB-FT"

                        drawText(
                            textMeasurer = textMeasurer,
                            text = titleText,
                            topLeft = Offset(calloutBgTopLeft.x + 8f, calloutBgTopLeft.y + 4f),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            size = Size(max(1f, cardWidth - 10f), max(1f, cardHeight - 6f))
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = infoText,
                            topLeft = Offset(calloutBgTopLeft.x + 8f, calloutBgTopLeft.y + 19f),
                            style = TextStyle(
                                color = Color(0xFF00F0FF),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            ),
                            size = Size(max(1f, cardWidth - 10f), max(1f, cardHeight - 21f))
                        )
                    }
                }
            }

            // 4. Draw Selected Part Dimension Bounding Box (Anchored Non-Overlapping HUD Pill)
            if (showDimensions && selectedComponent != null) {
                val selComp = selectedComponent

                val minX = selComp.vertices.minOfOrNull { it.x } ?: -0.5f
                val maxX = selComp.vertices.maxOfOrNull { it.x } ?: 0.5f
                val minY = selComp.vertices.minOfOrNull { it.y } ?: -0.5f
                val maxY = selComp.vertices.maxOfOrNull { it.y } ?: 0.5f
                val minZ = selComp.vertices.minOfOrNull { it.z } ?: -0.5f
                val maxZ = selComp.vertices.maxOfOrNull { it.z } ?: 0.5f

                val dxMm = ((maxX - minX) * 350f).toInt()
                val dyMm = ((maxY - minY) * 350f).toInt()
                val dzMm = ((maxZ - minZ) * 350f).toInt()

                val dimText = "CAD DIM: ${dxMm}mm × ${dyMm}mm × ${dzMm}mm"

                // Position Dimension HUD Pill at Top-Right of Canvas to avoid geometry & callout overlap
                val dimBgTopLeft = Offset(canvasWidth - 270f, 60f)
                val dimWidth = 250f
                val dimHeight = 32f

                drawRoundRect(
                    color = Color(0xFA0F172A),
                    topLeft = dimBgTopLeft,
                    size = Size(dimWidth, dimHeight),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color(0xFFFFD700).copy(alpha = 0.8f),
                    topLeft = dimBgTopLeft,
                    size = Size(dimWidth, dimHeight),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 1.2f.dp.toPx())
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = dimText,
                    topLeft = Offset(dimBgTopLeft.x + 12f, dimBgTopLeft.y + 8f),
                    style = TextStyle(
                        color = Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    size = Size(max(1f, dimWidth - 14f), max(1f, dimHeight - 10f))
                )
            }

            // 3D Canvas Reticle for Mentor Mode
            if (activeLayerTab == ViewportLayerTab.MENTOR && mentorRepairSteps.isNotEmpty()) {
                val currentStep = mentorRepairSteps.getOrNull(activeMentorStepIndex)
                if (currentStep != null) {
                    val targetCenter = newProjectedCenters.firstOrNull { it.component.id == currentStep.targetComponent.id }
                    if (targetCenter != null) {
                        val centerOffset = targetCenter.screenPos
                        val pulseRadius = 38f + pulseGlow * 18f

                        // Outer Pulsing Target Ring
                        drawCircle(
                            color = Color(0xFF00F0FF).copy(alpha = pulseGlow * 0.85f),
                            radius = pulseRadius,
                            center = centerOffset,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Inner Solid Center Dot
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = 6.dp.toPx(),
                            center = centerOffset
                        )

                        // Crosshair ticks
                        listOf(
                            Offset(centerOffset.x - pulseRadius - 10f, centerOffset.y) to Offset(centerOffset.x - pulseRadius + 5f, centerOffset.y),
                            Offset(centerOffset.x + pulseRadius - 5f, centerOffset.y) to Offset(centerOffset.x + pulseRadius + 10f, centerOffset.y),
                            Offset(centerOffset.x, centerOffset.y - pulseRadius - 10f) to Offset(centerOffset.x, centerOffset.y - pulseRadius + 5f),
                            Offset(centerOffset.x, centerOffset.y + pulseRadius - 5f) to Offset(centerOffset.x, centerOffset.y + pulseRadius + 10f)
                        ).forEach { (start, end) ->
                            drawLine(
                                color = Color(0xFF00F0FF),
                                start = start,
                                end = end,
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        // Mentor Tag Badge above target
                        val tagText = "🎓 MENTOR FOCUS: PHASE ${activeMentorStepIndex + 1}"
                        val tagPos = Offset(centerOffset.x - 70f, centerOffset.y - pulseRadius - 25f)
                        drawRoundRect(
                            color = Color(0xEE0F172A),
                            topLeft = tagPos,
                            size = Size(140f, 22f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        drawRoundRect(
                            color = Color(0xFF00F0FF),
                            topLeft = tagPos,
                            size = Size(140f, 22f),
                            cornerRadius = CornerRadius(6f, 6f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = tagText,
                            topLeft = Offset(tagPos.x + 6f, tagPos.y + 3f),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            size = Size(130f, 18f)
                        )
                    }
                }
            }

            // 5. Center Laser Crosshair Sight
            drawCircle(color = Color.White.copy(alpha = 0.25f), radius = 18.dp.toPx(), center = Offset(centerX, centerY), style = Stroke(width = 1.dp.toPx()))
            drawLine(color = Color.White.copy(alpha = 0.25f), start = Offset(centerX - 24f, centerY), end = Offset(centerX + 24f, centerY), strokeWidth = 1.dp.toPx())
            drawLine(color = Color.White.copy(alpha = 0.25f), start = Offset(centerX, centerY - 24f), end = Offset(centerX, centerY + 24f), strokeWidth = 1.dp.toPx())

            // 6. Draw BILT 3D Axis Gizmo (Bottom Left)
            drawBilt3dAxisGizmo(
                gizmoCenterX = 70f,
                gizmoCenterY = canvasHeight - 90f,
                cosY = cosY,
                sinY = sinY,
                cosP = cosP,
                sinP = sinP,
                textMeasurer = textMeasurer
            )
        }

        // Unified Layer Control Bar & Layer Panels (Declutters 3D Canvas)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Compact Sub-Tab Navigation Bar for Viewport Layers
            Surface(
                color = Color(0xEB0F172A),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Layer 1: Clean Canvas
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                activeLayerTab = ViewportLayerTab.CLEAN
                                layerControllerState = layerControllerState.copy(
                                    showCalloutLeaders = false,
                                    showTechnicalAnnotations = false,
                                    showDimensions = false,
                                    showHudInfoCards = false
                                )
                            }
                            .testTag("layer_tab_clean"),
                        color = if (activeLayerTab == ViewportLayerTab.CLEAN) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "👁️ Clean",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 2: Shading
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeLayerTab = ViewportLayerTab.SHADING }
                            .testTag("layer_tab_shading"),
                        color = if (activeLayerTab == ViewportLayerTab.SHADING) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎨 Shading",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 3: Exploded
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                activeLayerTab = ViewportLayerTab.EXPLODED
                                if (explodeFactor == 0f) explodeFactor = 0.85f
                            }
                            .testTag("layer_tab_exploded"),
                        color = if (activeLayerTab == ViewportLayerTab.EXPLODED) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💥 Exploded",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 4: Assembly
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeLayerTab = ViewportLayerTab.ASSEMBLY }
                            .testTag("layer_tab_assembly"),
                        color = if (activeLayerTab == ViewportLayerTab.ASSEMBLY) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🔧 Assembly",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 4: Annotations & Camera
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeLayerTab = ViewportLayerTab.ANNOTATIONS }
                            .testTag("layer_tab_annotations"),
                        color = if (activeLayerTab == ViewportLayerTab.ANNOTATIONS) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📐 Specs",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 5: Mentor Mode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeLayerTab = ViewportLayerTab.MENTOR }
                            .testTag("layer_tab_mentor"),
                        color = if (activeLayerTab == ViewportLayerTab.MENTOR) Color(0xFF00F0FF) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎓 Mentor",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp),
                            color = if (activeLayerTab == ViewportLayerTab.MENTOR) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 6: Blender 3D Animation & Keyframe Timeline
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                activeLayerTab = ViewportLayerTab.ANIMATION
                                isBlenderAnimPlaying = true
                            }
                            .testTag("layer_tab_animation"),
                        color = if (activeLayerTab == ViewportLayerTab.ANIMATION) Color(0xFFFF6F00) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎬 Animation",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Layer 6: State-Managed Systems Layer Controller
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLayerControllerDialog = true }
                            .testTag("layer_tab_controller_btn"),
                        color = if (layerControllerState.isolatedSystem != null || layerControllerState.visibleSystemCount < layerControllerState.totalSystemCount) Color(0xFFF59E0B) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Systems Layer Controller",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Layers (${layerControllerState.visibleSystemCount}/${layerControllerState.totalSystemCount})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Reset Viewport Camera Button
                    IconButton(
                        onClick = {
                            cameraYaw = 45f
                            cameraPitch = 28f
                            cameraZoom = 1.0f
                            explodeFactor = 0.0f
                            clipPlaneSlice = 1.0f
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .testTag("reset_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusWeak,
                            contentDescription = "Reset Camera",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Wording & Text Layer Quick Toggle Strip (Declutters 3D CAD Viewport)
            Surface(
                color = Color(0xEB0F172A),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (layerControllerState.isAllWordingHidden) Color(0xFF22C55E) else Color(0xFF334155)),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Master Clean Model Button (1-Click Hide All Text)
                    Surface(
                        color = if (layerControllerState.isAllWordingHidden) Color(0xFF22C55E) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (layerControllerState.isAllWordingHidden) Color(0xFF4ADE80) else Color(0xFF475569)),
                        modifier = Modifier
                            .clickable {
                                val hideAll = !layerControllerState.isAllWordingHidden
                                layerControllerState = layerControllerState.copy(
                                    showCalloutLeaders = !hideAll,
                                    showTechnicalAnnotations = !hideAll,
                                    showDimensions = !hideAll,
                                    showHudInfoCards = !hideAll
                                )
                            }
                            .testTag("btn_quick_clean_model_wording")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (layerControllerState.isAllWordingHidden) Icons.Default.VisibilityOff else Icons.Default.SubtitlesOff,
                                contentDescription = null,
                                tint = if (layerControllerState.isAllWordingHidden) Color.Black else Color(0xFF00F0FF),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (layerControllerState.isAllWordingHidden) "🚫 CLEAN MODEL (TEXT OFF)" else "🚫 HIDE WORDING",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                color = if (layerControllerState.isAllWordingHidden) Color.Black else Color.White
                            )
                        }
                    }

                    // Layer 1: Callout Labels Layer
                    FilterChip(
                        selected = layerControllerState.showCalloutLeaders,
                        onClick = {
                            layerControllerState = layerControllerState.copy(showCalloutLeaders = !layerControllerState.showCalloutLeaders)
                        },
                        label = { Text("🏷️ Callouts", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("chip_viewport_layer_callouts")
                    )

                    // Layer 2: Tech Specs & Torque Layer
                    FilterChip(
                        selected = layerControllerState.showTechnicalAnnotations,
                        onClick = {
                            layerControllerState = layerControllerState.copy(showTechnicalAnnotations = !layerControllerState.showTechnicalAnnotations)
                        },
                        label = { Text("🔩 Specs & Torque", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("chip_viewport_layer_specs")
                    )

                    // Layer 3: CAD Dimensions Layer
                    FilterChip(
                        selected = layerControllerState.showDimensions,
                        onClick = {
                            layerControllerState = layerControllerState.copy(showDimensions = !layerControllerState.showDimensions)
                        },
                        label = { Text("📐 Dimensions", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("chip_viewport_layer_dimensions")
                    )

                    // Layer 4: HUD Info Overlay Cards Layer
                    FilterChip(
                        selected = layerControllerState.showHudInfoCards,
                        onClick = {
                            layerControllerState = layerControllerState.copy(showHudInfoCards = !layerControllerState.showHudInfoCards)
                        },
                        label = { Text("📄 HUD Cards", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("chip_viewport_layer_hud_cards")
                    )
                }
            }

            // Layer-Specific Control Panels
            when (activeLayerTab) {
                ViewportLayerTab.SHADING -> {
                    Surface(
                        color = Color(0xEB0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SHADING:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                            CadRenderStyle.values().forEach { style ->
                                val isSelected = cadRenderStyle == style
                                Surface(
                                    color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)),
                                    modifier = Modifier
                                        .clickable { cadRenderStyle = style }
                                        .testTag("cad_style_${style.name}")
                                ) {
                                    Text(
                                        text = style.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                ViewportLayerTab.EXPLODED -> {
                    Surface(
                        color = Color(0xEB0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Compress,
                                        contentDescription = "Explode",
                                        tint = Color(0xFF00F0FF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "EXPLODED SUB-ASSEMBLIES",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                        color = Color(0xFF38BDF8)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        color = Color(0xFF00F0FF).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF00F0FF)),
                                        modifier = Modifier
                                            .clickable { showPhysicsDialog = true }
                                            .testTag("btn_open_physics_simulation")
                                    ) {
                                        Text(
                                            text = "⚡ Physics Sim",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = Color(0xFF00F0FF),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    listOf(0.0f to "ASSEMBLED", 0.5f to "50%", 1.0f to "EXPLODED").forEach { (factor, label) ->
                                        Surface(
                                            color = if (abs(explodeFactor - factor) < 0.05f) Color(0xFF0284C7) else Color(0xFF1E293B),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.clickable { explodeFactor = factor }.testTag("explode_preset_$label")
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Separation Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Separation:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Slider(
                                    value = explodeFactor,
                                    onValueChange = { explodeFactor = it },
                                    valueRange = 0f..1f,
                                    modifier = Modifier.weight(1f).testTag("explode_slider"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF00F0FF),
                                        activeTrackColor = Color(0xFF0284C7),
                                        inactiveTrackColor = Color(0xFF334155)
                                    )
                                )
                                Text("${(explodeFactor * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF00F0FF))
                            }

                            // Hardware Type Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hardware:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))

                                val filters = listOf(
                                    null to "ALL",
                                    SubAssemblyType.BOLT to "🔩 BOLTS",
                                    SubAssemblyType.WASHER to "⭕ WASHERS",
                                    SubAssemblyType.GASKET to "📑 GASKETS",
                                    SubAssemblyType.BELT to "🎗️ BELTS",
                                    SubAssemblyType.SPARK_PLUG to "⚡ PLUGS"
                                )

                                filters.forEach { (type, label) ->
                                    val isSelected = subAssemblyTypeFilter == type
                                    Surface(
                                        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                                        modifier = Modifier.clickable { subAssemblyTypeFilter = type }
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ViewportLayerTab.ANNOTATIONS -> {
                    Surface(
                        color = Color(0xEB0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (showTechnicalAnnotations) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (showTechnicalAnnotations) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable { layerControllerState = layerControllerState.copy(showTechnicalAnnotations = !showTechnicalAnnotations) }
                                    .testTag("toggle_tech_annotations_btn")
                            ) {
                                Text(
                                    text = if (showTechnicalAnnotations) "🏷️ ANNOTATIONS: ON" else "🏷️ ANNOTATIONS: OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = if (showTechnicalAnnotations) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = if (showBloomEffect) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (showBloomEffect) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable { showBloomEffect = !showBloomEffect }
                                    .testTag("toggle_bloom_effect_btn")
                            ) {
                                Text(
                                    text = if (showBloomEffect) "✨ BLOOM: ON" else "✨ BLOOM: OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = if (showBloomEffect) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = if (showCalloutLeaders) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { layerControllerState = layerControllerState.copy(showCalloutLeaders = !showCalloutLeaders) }
                            ) {
                                Text("CALLOUTS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }

                            Surface(
                                color = if (showDimensions) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { layerControllerState = layerControllerState.copy(showDimensions = !showDimensions) }
                            ) {
                                Text("DIMENSIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            listOf("ISO" to (45f to 28f), "FRONT" to (0f to 0f), "TOP" to (0f to 85f)).forEach { (name, angles) ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { cameraYaw = angles.first; cameraPitch = angles.second }
                                ) {
                                    Text(name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color(0xFF38BDF8), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                ViewportLayerTab.ASSEMBLY, ViewportLayerTab.CLEAN, ViewportLayerTab.MENTOR, ViewportLayerTab.ANIMATION -> { /* Minimal overlay */ }
            }
        }

        // Blender 3D Animation Timeline & Studio Controller (Shown in Animation layer)
        if (activeLayerTab == ViewportLayerTab.ANIMATION) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.BottomCenter)
                    .testTag("blender_animation_timeline_panel"),
                color = Color(0xFA0B132B),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFF6F00)),
                shadowElevation = 14.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Row: Animation Title, Stats HUD & Studio Lighting Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = Color(0xFFFF6F00),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "BLENDER 3D ANIMATION DOPE SHEET",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.5.sp, letterSpacing = 0.8.sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "Frame ${(blenderTimelineProgress * 120f).toInt()} / 120 • 60 FPS • PBR EEVEE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                color = Color(0xFFFF9E40)
                            )
                        }

                        // Studio Lighting Selector Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            BlenderLightingPreset.values().forEach { preset ->
                                val isSelected = blenderLightingPreset == preset
                                Surface(
                                    color = if (isSelected) Color(0xFFFF6F00) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isSelected) Color.White else Color(0xFF334155)),
                                    modifier = Modifier
                                        .clickable {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            blenderLightingPreset = preset
                                        }
                                        .testTag("lighting_preset_${preset.name}")
                                ) {
                                    Text(
                                        text = preset.label.take(6),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Animation Track Selector Pills (Turntable 360, Exploded Keyframes, Cinematic Flythrough, Part Sequence)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(BlenderAnimTrack.values().toList()) { track ->
                            val isSelected = blenderAnimTrack == track
                            Surface(
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        blenderAnimTrack = track
                                        blenderTimelineProgress = 0f
                                        isBlenderAnimPlaying = true
                                    }
                                    .testTag("anim_track_${track.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(track.icon, fontSize = 12.sp)
                                    Text(
                                        text = track.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        ),
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    // Scrubbable Keyframe Timeline Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Keyframe Scrubber",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "00:0${(blenderTimelineProgress * 4.0f).toInt()}s / 00:04s",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                color = Color(0xFF00F0FF)
                            )
                        }

                        Slider(
                            value = blenderTimelineProgress,
                            onValueChange = {
                                isBlenderAnimPlaying = false
                                blenderTimelineProgress = it
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFF6F00),
                                activeTrackColor = Color(0xFFFF6F00),
                                inactiveTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier
                                .height(22.dp)
                                .testTag("blender_timeline_slider")
                        )
                    }

                    // Timeline Transport Controls & Playback Speed Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Transport Buttons: SkipStart, PrevFrame, Play/Pause, NextFrame, SkipEnd
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    blenderTimelineProgress = 0f
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("btn_anim_skip_start")
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Start Frame", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    blenderTimelineProgress = (blenderTimelineProgress - 1f / 120f).coerceAtLeast(0f)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("btn_anim_prev_frame")
                            ) {
                                Icon(Icons.Default.FastRewind, contentDescription = "Step Back 1 Frame", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    isBlenderAnimPlaying = !isBlenderAnimPlaying
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (isBlenderAnimPlaying) Color(0xFFFF6F00) else Color(0xFF0284C7), CircleShape)
                                    .testTag("btn_anim_play_pause")
                            ) {
                                Icon(
                                    imageVector = if (isBlenderAnimPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play or Pause Blender Animation",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    blenderTimelineProgress = (blenderTimelineProgress + 1f / 120f).coerceAtMost(1f)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("btn_anim_next_frame")
                            ) {
                                Icon(Icons.Default.FastForward, contentDescription = "Step Forward 1 Frame", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    blenderTimelineProgress = 1f
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("btn_anim_skip_end")
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "End Frame", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Loop & Speed Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (isBlenderLoopEnabled) Color(0xFF166534) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isBlenderLoopEnabled) Color(0xFF22C55E) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        isBlenderLoopEnabled = !isBlenderLoopEnabled
                                    }
                                    .testTag("btn_anim_loop_toggle")
                            ) {
                                Text(
                                    text = if (isBlenderLoopEnabled) "🔁 LOOP: ON" else "🔁 LOOP: OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = if (isBlenderLoopEnabled) Color(0xFF86EFAC) else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            listOf(0.5f to "0.5x", 1.0f to "1.0x", 2.0f to "2.0x").forEach { (speedVal, label) ->
                                val isSelected = blenderPlaybackSpeed == speedVal
                                Surface(
                                    color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .clickable {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            blenderPlaybackSpeed = speedVal
                                        }
                                        .testTag("speed_pill_$label")
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // BILT Step-by-Step Guided Assembly Player (Only shown in Assembly layer)
        if (activeLayerTab == ViewportLayerTab.ASSEMBLY) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.BottomCenter),
                color = Color(0xFF0F172A).copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF0284C7)),
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentBiltStepPart != null) {
                        val stepNum = currentBiltStepIndex + 1
                        val totalSteps = visibleComponents.size
                        val stepTorqueVal = currentBiltStepPart.torqueSpecs.firstOrNull()?.torqueFtLbs ?: "85"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "STEP $stepNum OF $totalSteps",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = currentBiltStepPart.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "OEM #${currentBiltStepPart.oemPartNumber} • Torque: $stepTorqueVal lb-ft",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF00F0FF)
                                    )
                                }
                            }

                            Button(
                                onClick = { onOpenDetailManual(currentBiltStepPart) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("view_repair_manual_btn")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Manual", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        // Step Timeline Navigation Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        if (currentBiltStepIndex > 0) currentBiltStepIndex--
                                    },
                                    enabled = currentBiltStepIndex > 0,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E293B), CircleShape)
                                        .testTag("step_prev_btn")
                                ) {
                                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Step", tint = Color.White)
                                }

                                IconButton(
                                    onClick = {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        isPlayingBiltAnimation = !isPlayingBiltAnimation
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(if (isPlayingBiltAnimation) Color(0xFFFF6F00) else Color(0xFF0284C7), CircleShape)
                                        .testTag("step_play_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingBiltAnimation) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause Step Animation",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        if (currentBiltStepIndex < visibleComponents.lastIndex) currentBiltStepIndex++
                                    },
                                    enabled = visibleComponents.isNotEmpty() && currentBiltStepIndex < visibleComponents.lastIndex,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E293B), CircleShape)
                                        .testTag("step_next_btn")
                                ) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Next Step", tint = Color.White)
                                }
                            }

                            // Exploded Assembly Quick Slider
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("EXPLODE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color(0xFF38BDF8))
                                    Text("${(animatedExplode * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color.White)
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
                                    modifier = Modifier.height(20.dp).testTag("exploded_slider")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mentor Mode Top Progress HUD Banner
        AnimatedVisibility(
            visible = activeLayerTab == ViewportLayerTab.MENTOR,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth()
        ) {
            Surface(
                color = Color(0xFA0F172A),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFF00F0FF)),
                shadowElevation = 12.dp,
                modifier = Modifier.testTag("mentor_hud_top_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color(0xFF0284C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "🎓 MENTOR MODE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        val completedPct = if (mentorRepairSteps.isNotEmpty()) {
                            ((completedMentorSteps.size.toFloat() / mentorRepairSteps.size) * 100).toInt()
                        } else 0

                        Column {
                            Text(
                                text = "Phase ${activeMentorStepIndex + 1} of ${mentorRepairSteps.size} • $completedPct% Complete",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = Color.White
                            )
                            LinearProgressIndicator(
                                progress = { if (mentorRepairSteps.isNotEmpty()) (activeMentorStepIndex + 1).toFloat() / mentorRepairSteps.size else 0f },
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFF00F0FF),
                                trackColor = Color(0xFF334155)
                            )
                        }
                    }

                    // Auto-Rotate Sync Toggle Button
                    Surface(
                        color = if (autoRotateCameraOnStep) Color(0xFF0284C7) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (autoRotateCameraOnStep) Color(0xFF00F0FF) else Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                HapticHelper.triggerControlTick(context, view, haptic)
                                autoRotateCameraOnStep = !autoRotateCameraOnStep
                            }
                            .testTag("mentor_autorotate_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = "Auto Rotate",
                                tint = if (autoRotateCameraOnStep) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (autoRotateCameraOnStep) "3D CAMERA: AUTO" else "3D CAMERA: MANUAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = if (autoRotateCameraOnStep) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Mentor Mode Scrollable Card Stack
        AnimatedVisibility(
            visible = activeLayerTab == ViewportLayerTab.MENTOR,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LazyRow(
                    state = mentorCardListState,
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mentor_card_stack_lazyrow")
                ) {
                    itemsIndexed(mentorRepairSteps) { index, step ->
                        val isActive = index == activeMentorStepIndex
                        val isDone = index in completedMentorSteps

                        Surface(
                            color = if (isActive) Color(0xFA0F172A) else Color(0xEB1E293B),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                width = if (isActive) 2.dp else 1.dp,
                                color = when {
                                    isActive -> Color(0xFF00F0FF)
                                    isDone -> Color(0xFF22C55E)
                                    else -> Color(0xFF334155)
                                }
                            ),
                            shadowElevation = if (isActive) 12.dp else 4.dp,
                            modifier = Modifier
                                .width(310.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    HapticHelper.triggerComponentHaptic(context, view, haptic, step.targetComponent)
                                    activeMentorStepIndex = index
                                }
                                .testTag("mentor_card_$index")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Card Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = if (isActive) Color(0xFF0284C7) else Color(0xFF334155),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "PHASE ${index + 1} OF ${step.totalPhases}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp
                                            ),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    if (isDone) {
                                        Surface(
                                            color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, Color(0xFF22C55E)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF22C55E),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "DONE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    ),
                                                    color = Color(0xFF22C55E)
                                                )
                                            }
                                        }
                                    } else if (isActive) {
                                        Text(
                                            text = "✨ ACTIVE FOCUS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            color = Color(0xFF00F0FF)
                                        )
                                    }
                                }

                                // Title
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 1
                                )

                                // Target Component Pill
                                Surface(
                                    color = Color(0xFF0284C7).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0284C7))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PrecisionManufacturing,
                                            contentDescription = null,
                                            tint = Color(0xFF00F0FF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = step.targetComponent.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = Color(0xFF00F0FF),
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "OEM #${step.targetComponent.oemPartNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                // Instruction Description Text
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    ),
                                    color = Color(0xFFE2E8F0),
                                    maxLines = 3
                                )

                                // Safety Warning Box
                                step.safetyWarning?.let { warning ->
                                    Surface(
                                        color = Color(0x33EF4444),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFEF4444))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = warning,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color(0xFFF87171),
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }

                                // Tools & Torque Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    step.torqueSpec?.let { spec ->
                                        Surface(
                                            color = Color(0xFF1E293B),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = "🔩 $spec",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color(0xFFFFD700),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "🛠️ ${step.requiredTools.firstOrNull() ?: "10mm Socket"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp
                                        ),
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1
                                    )
                                }

                                // Card Footer Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Auto-Focus 3D View Button
                                    OutlinedButton(
                                        onClick = {
                                            HapticHelper.triggerComponentHaptic(context, view, haptic, step.targetComponent)
                                            cameraYaw = step.yaw
                                            cameraPitch = step.pitch
                                            cameraZoom = step.zoom
                                            explodeFactor = step.explodeFactor
                                            onComponentSelect(step.targetComponent)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                            .testTag("mentor_card_focus_btn_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CenterFocusStrong,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "3D Focus",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF38BDF8)
                                        )
                                    }

                                    // Complete Phase Button
                                    Button(
                                        onClick = {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            completedMentorSteps = completedMentorSteps + index
                                            if (index < mentorRepairSteps.lastIndex) {
                                                activeMentorStepIndex = index + 1
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDone) Color(0xFF1E293B) else Color(0xFF00F0FF)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                            .testTag("mentor_card_complete_btn_$index")
                                    ) {
                                        Text(
                                            text = if (isDone) "Re-Focus" else "Complete ✓",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isDone) Color.White else Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive 3D Component Overlay (Triggers when clicking any 3D Component)
        AnimatedVisibility(
            visible = selectedComponent != null && activeLayerTab != ViewportLayerTab.MENTOR && showHudInfoCards,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.BottomCenter)
        ) {
            selectedComponent?.let { comp ->
                Surface(
                    color = Color(0xFA0F172A),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF00F0FF)),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("component_interactive_overlay")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header Row: System Badge, Official Part Name, Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = comp.system.displayName.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = comp.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }

                            IconButton(
                                onClick = {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    onComponentSelect(comp) // Deselect component
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("overlay_close_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Overlay",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Basic Repair Status Banner
                        val repairInfo = remember(comp) { getRepairStatusForComponent(comp) }
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, repairInfo.statusColor.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = repairInfo.statusColor,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (repairInfo.healthPercentage > 85) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = repairInfo.statusLabel,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.5.sp
                                                ),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${repairInfo.healthPercentage}% Health",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        ),
                                        color = repairInfo.statusColor
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { repairInfo.healthPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = repairInfo.statusColor,
                                    trackColor = Color(0xFF0F172A)
                                )

                                Text(
                                    text = repairInfo.conditionSummary,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                            }
                        }

                        // Component Identifiers: Serial Number & OEM Part Number
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Serial Number Card
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Serial Number",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "SERIAL NUMBER",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = comp.serialNumber,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            ),
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }

                            // OEM Part Number Card
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PrecisionManufacturing,
                                        contentDescription = "OEM Part Number",
                                        tint = Color(0xFF00F0FF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "OEM PART #",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = comp.oemPartNumber,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            ),
                                            color = Color(0xFF00F0FF)
                                        )
                                    }
                                }
                            }
                        }

                        // Direct Link to Relevant Manual Section
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    HapticHelper.triggerComponentHaptic(context, view, haptic, comp)
                                    onOpenDetailManual(comp)
                                }
                                .testTag("direct_manual_link_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Manual Section",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "MANUAL SECTION LINK",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color(0xFF38BDF8)
                                        )
                                        Text(
                                            text = comp.manualSectionRef,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.5.sp
                                            ),
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Open Link",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = Color.White
                                        )
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Footer Specs Summary & Quick Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val torqueVal = comp.torqueSpecs.firstOrNull()?.torqueFtLbs ?: "85"
                            Text(
                                text = "⏱️ ${comp.estimatedTimeMinutes} min • 🔩 ${torqueVal} lb-ft • 🛠️ ${comp.requiredTools.size} Tools",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )

                            Button(
                                onClick = { onOpenDetailManual(comp) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("view_full_manual_btn")
                            ) {
                                Text(
                                    text = "View Manual",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBiltCadGridFloor(
    centerX: Float,
    centerY: Float,
    cosY: Float,
    sinY: Float,
    cosP: Float,
    sinP: Float,
    scale: Float,
    renderStyle: CadRenderStyle
) {
    val gridRange = -4..4
    val floorY = -2.0f
    val gridColor = when (renderStyle) {
        CadRenderStyle.BLUEPRINT -> Color(0xFF005599)
        CadRenderStyle.XRAY_GHOST -> Color(0xFF1E293B)
        CadRenderStyle.METALLIC_OEM -> Color(0xFF334155)
        CadRenderStyle.BILT_CAD_PBR -> Color(0xFF1E293B)
    }

    gridRange.forEach { i ->
        val pos = i.toFloat() * 1.2f

        // X lines
        val p1x = pos; val p1z = -4.8f
        val p2x = pos; val p2z = 4.8f

        val rx1 = p1x * cosY - p1z * sinY
        val rz1 = p1x * sinY + p1z * cosY
        val ry1 = floorY * cosP - rz1 * sinP

        val rx2 = p2x * cosY - p2z * sinY
        val rz2 = p2x * sinY + p2z * cosY
        val ry2 = floorY * cosP - rz2 * sinP

        val x1 = centerX + rx1 * scale; val y1 = centerY - ry1 * scale
        val x2 = centerX + rx2 * scale; val y2 = centerY - ry2 * scale

        drawLine(
            color = gridColor,
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = if (i == 0) 2.dp.toPx() else 1.dp.toPx()
        )

        // Z lines
        val q1x = -4.8f; val q1z = pos
        val q2x = 4.8f; val q2z = pos

        val srx1 = q1x * cosY - q1z * sinY
        val srz1 = q1x * sinY + q1z * cosY
        val sry1 = floorY * cosP - srz1 * sinP

        val srx2 = q2x * cosY - q2z * sinY
        val srz2 = q2x * sinY + q2z * cosY
        val sry2 = floorY * cosP - srz2 * sinP

        val sx1 = centerX + srx1 * scale; val sy1 = centerY - sry1 * scale
        val sx2 = centerX + srx2 * scale; val sy2 = centerY - sry2 * scale

        drawLine(
            color = gridColor,
            start = Offset(sx1, sy1),
            end = Offset(sx2, sy2),
            strokeWidth = if (i == 0) 2.dp.toPx() else 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawBilt3dAxisGizmo(
    gizmoCenterX: Float,
    gizmoCenterY: Float,
    cosY: Float,
    sinY: Float,
    cosP: Float,
    sinP: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val axisLen = 35f

    // 3D Axis unit vectors
    // X axis (Red)
    val xx = axisLen; val xy = 0f; val xz = 0f
    val rxX = xx * cosY - xz * sinY; val rzX = xx * sinY + xz * cosY; val ryX = xy * cosP - rzX * sinP

    // Y axis (Green)
    val yx = 0f; val yy = axisLen; val yz = 0f
    val rxY = yx * cosY - yz * sinY; val rzY = yx * sinY + yz * cosY; val ryY = yy * cosP - rzY * sinP

    // Z axis (Blue)
    val zx = 0f; val zy = 0f; val zz = axisLen
    val rxZ = zx * cosY - zz * sinY; val rzZ = zx * sinY + zz * cosY; val ryZ = zy * cosP - rzZ * sinP

    val centerOffset = Offset(gizmoCenterX, gizmoCenterY)

    // Draw X (Red)
    val xEnd = centerOffset + Offset(rxX, -ryX)
    drawLine(color = Color(0xFFEF4444), start = centerOffset, end = xEnd, strokeWidth = 2.5f.dp.toPx(), cap = StrokeCap.Round)
    drawText(textMeasurer, "X", xEnd + Offset(4f, -10f), TextStyle(color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold), size = Size(30f, 30f))

    // Draw Y (Green)
    val yEnd = centerOffset + Offset(rxY, -ryY)
    drawLine(color = Color(0xFF10B981), start = centerOffset, end = yEnd, strokeWidth = 2.5f.dp.toPx(), cap = StrokeCap.Round)
    drawText(textMeasurer, "Y", yEnd + Offset(4f, -10f), TextStyle(color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold), size = Size(30f, 30f))

    // Draw Z (Blue)
    val zEnd = centerOffset + Offset(rxZ, -ryZ)
    drawLine(color = Color(0xFF38BDF8), start = centerOffset, end = zEnd, strokeWidth = 2.5f.dp.toPx(), cap = StrokeCap.Round)
    drawText(textMeasurer, "Z", zEnd + Offset(4f, -10f), TextStyle(color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold), size = Size(30f, 30f))

    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = centerOffset)
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
