package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.util.HapticHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Build
import com.example.ui.components.ArOverlayView
import com.example.ui.components.CameraMeasurementDialog
import com.example.ui.components.ColorSegmentBar
import com.example.ui.components.ComponentDetailSheet
import com.example.ui.components.Interactive3DViewport

import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.CloudOff
import com.example.ui.components.OfflineCacheDialog
import com.example.ui.components.CadStepIngestionDialog
import com.example.ui.components.ExplodedPhysicsDialog
import com.example.ui.components.LayerControllerDialog
import com.example.ui.components.LayerControllerState
import com.example.ui.components.SceneViewGltfViewport

enum class ViewportMode {
    MODEL_3D,
    SCENEVIEW_GLTF,
    AR_CAMERA
}

@Composable
fun Model3DScreen(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    activeSystem: VehicleSystem,
    cached3DCount: Int = 14,
    cachedManualsCount: Int = 11,
    cachedSymptomsCount: Int = 18,
    requestDetailSheetOpen: Boolean = false,
    onClearDetailSheetRequest: () -> Unit = {},
    onSelectSystem: (VehicleSystem) -> Unit,
    onSelectComponent: (Component3DModel) -> Unit,
    onAddToCart: ((Component3DModel) -> Unit)? = null,
    onReSyncOfflineCache: () -> Unit = {},
    onClearOfflineCache: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    var viewportMode by remember { mutableStateOf(ViewportMode.MODEL_3D) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showCameraMeasurementDialog by remember { mutableStateOf(false) }
    var showCadIngestionDialog by remember { mutableStateOf(false) }
    var showPhysicsDialog by remember { mutableStateOf(false) }
    var showLayerControllerDialog by remember { mutableStateOf(false) }
    var showOfflineCacheDialog by remember { mutableStateOf(false) }
    var topLayerControllerState by remember { mutableStateOf(LayerControllerState()) }
    var isTrayExpanded by remember { mutableStateOf(false) }
    var isFocusMode by remember { mutableStateOf(false) }

    if (showOfflineCacheDialog) {
        OfflineCacheDialog(
            cached3DCount = cached3DCount,
            cachedManualsCount = cachedManualsCount,
            cachedSymptomsCount = cachedSymptomsCount,
            onReSync = onReSyncOfflineCache,
            onClear = onClearOfflineCache,
            onDismiss = { showOfflineCacheDialog = false }
        )
    }

    if (showLayerControllerDialog) {
        LayerControllerDialog(
            components = components,
            state = topLayerControllerState,
            onStateChange = { topLayerControllerState = it },
            onDismiss = { showLayerControllerDialog = false }
        )
    }

    if (showCameraMeasurementDialog) {
        CameraMeasurementDialog(
            onDismiss = { showCameraMeasurementDialog = false }
        )
    }

    if (showPhysicsDialog) {
        ExplodedPhysicsDialog(
            components = components,
            initialSelectedComponent = selectedComponent,
            onDismiss = { showPhysicsDialog = false }
        )
    }

    if (showCadIngestionDialog) {
        CadStepIngestionDialog(
            onDismiss = { showCadIngestionDialog = false },
            onViewIn3DViewport = { targetCompId ->
                val match = components.firstOrNull { it.id == targetCompId }
                if (match != null) {
                    onSelectComponent(match)
                }
            }
        )
    }

    LaunchedEffect(requestDetailSheetOpen) {
        if (requestDetailSheetOpen) {
            showDetailSheet = true
            onClearDetailSheetRequest()
        }
    }

    if (showDetailSheet && selectedComponent != null) {
        ComponentDetailSheet(
            component = selectedComponent,
            onDismiss = { showDetailSheet = false },
            onAddToCart = onAddToCart
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. Full-bleed Main Viewport Canvas (Either Interactive 3D Canvas, SceneView GLTF Renderer, or AR Overlay View)
        when (viewportMode) {
            ViewportMode.MODEL_3D -> {
                Interactive3DViewport(
                    components = components,
                    selectedComponent = selectedComponent,
                    activeSystemFilter = activeSystem,
                    onComponentSelect = { comp -> onSelectComponent(comp) },
                    onOpenDetailManual = { comp ->
                        onSelectComponent(comp)
                        showDetailSheet = true
                    }
                )
            }

            ViewportMode.SCENEVIEW_GLTF -> {
                SceneViewGltfViewport(
                    components = components,
                    selectedComponent = selectedComponent,
                    onComponentSelect = { comp -> onSelectComponent(comp) }
                )
            }

            ViewportMode.AR_CAMERA -> {
                ArOverlayView(
                    components = components,
                    selectedComponent = selectedComponent,
                    onSelectComponent = onSelectComponent,
                    onOpenDetailSheet = { comp ->
                        onSelectComponent(comp)
                        showDetailSheet = true
                    }
                )
            }
        }

        // Floating Focus Canvas Toggle (Eye Icon in Top-Right corner)
        Surface(
            color = if (isFocusMode) Color(0xFF0284C7) else Color(0xEB0B132B),
            shape = CircleShape,
            border = BorderStroke(1.dp, if (isFocusMode) Color(0xFF38BDF8) else Color(0xFF334155)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .size(38.dp)
                .clip(CircleShape)
                .clickable { isFocusMode = !isFocusMode }
                .testTag("toggle_focus_mode_btn")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFocusMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle Focus Mode",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (!isFocusMode) {
            // 2. Floating Top Controls Overlay (System Category Filter & Viewport Mode Toggles)
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 12.dp, end = 58.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color-coded System Segment Bar (Slim Floating Scrollable Row)
                    Box(modifier = Modifier.weight(1f)) {
                        ColorSegmentBar(
                            activeSystem = activeSystem,
                            onSelectSystem = onSelectSystem
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Viewport Mode Floating Toggle Pill (3D / AR / CV Measure)
                    Surface(
                        color = Color(0xEB0B132B),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewportMode = ViewportMode.MODEL_3D },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        if (viewportMode == ViewportMode.MODEL_3D) Color(0xFF0284C7) else Color.Transparent,
                                        CircleShape
                                    )
                                    .testTag("mode_3d_viewport")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = "3D Viewport",
                                    tint = if (viewportMode == ViewportMode.MODEL_3D) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewportMode = ViewportMode.SCENEVIEW_GLTF },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        if (viewportMode == ViewportMode.SCENEVIEW_GLTF) Color(0xFFFF6F00) else Color.Transparent,
                                        CircleShape
                                    )
                                    .testTag("mode_sceneview_gltf")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "SceneView GLTF",
                                    tint = if (viewportMode == ViewportMode.SCENEVIEW_GLTF) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewportMode = ViewportMode.AR_CAMERA },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        if (viewportMode == ViewportMode.AR_CAMERA) Color(0xFFFF6F00) else Color.Transparent,
                                        CircleShape
                                    )
                                    .testTag("mode_ar_camera")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "AR Camera",
                                    tint = if (viewportMode == ViewportMode.AR_CAMERA) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { showCameraMeasurementDialog = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color.Transparent, CircleShape)
                                    .testTag("mode_camera_cv_measure")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = "CV Measure",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { showPhysicsDialog = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.35f), CircleShape)
                                    .testTag("btn_open_physics_simulation_top")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Rigid Body 3D Physics & Collisions",
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { showCadIngestionDialog = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(0xFF00F0FF).copy(alpha = 0.2f), CircleShape)
                                    .testTag("btn_open_cad_ingest_dialog")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileOpen,
                                    contentDescription = "CAD STEP Ingestion & BILT Steps",
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { showLayerControllerDialog = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.25f), CircleShape)
                                    .testTag("btn_open_layer_controller_top")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Engine Systems Layer Controller",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = { showOfflineCacheDialog = true },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(0xFF22C55E).copy(alpha = 0.25f), CircleShape)
                                    .testTag("btn_open_offline_cache_top")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Room Offline Caching Status",
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Floating Bottom Component Selector Tray (Collapsible to clear viewport clutter)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color(0xEB0B132B),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isTrayExpanded = !isTrayExpanded }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PARTS LAYER (${components.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF94A3B8)
                            )
                            if (selectedComponent != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${selectedComponent.name}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = selectedComponent.system.color,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isTrayExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isTrayExpanded) "Collapse Tray" else "Expand Tray",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isTrayExpanded) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(components) { comp ->
                                val isSelected = selectedComponent?.id == comp.id

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            HapticHelper.triggerComponentHaptic(context, view, haptic, comp)
                                            onSelectComponent(comp)
                                        }
                                        .testTag("comp_card_${comp.id}"),
                                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) comp.system.color else Color(0xFF334155)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
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
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
