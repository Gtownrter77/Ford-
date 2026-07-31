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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ViewInAr
import com.example.ui.components.ArOverlayView
import com.example.ui.components.ColorSegmentBar
import com.example.ui.components.ComponentDetailSheet
import com.example.ui.components.Interactive3DViewport

enum class ViewportMode {
    MODEL_3D,
    AR_CAMERA
}

@Composable
fun Model3DScreen(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    activeSystem: VehicleSystem,
    requestDetailSheetOpen: Boolean = false,
    onClearDetailSheetRequest: () -> Unit = {},
    onSelectSystem: (VehicleSystem) -> Unit,
    onSelectComponent: (Component3DModel) -> Unit,
    onAddToCart: ((Component3DModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var viewportMode by remember { mutableStateOf(ViewportMode.MODEL_3D) }
    var showDetailSheet by remember { mutableStateOf(false) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Mode Selector Bar (3D Model Viewport vs AR Engine Vision)
        Surface(
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewportMode = ViewportMode.MODEL_3D }
                        .testTag("mode_3d_viewport"),
                    color = if (viewportMode == ViewportMode.MODEL_3D) Color(0xFF0284C7) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (viewportMode == ViewportMode.MODEL_3D) Color(0xFF38BDF8) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = if (viewportMode == ViewportMode.MODEL_3D) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D Interactive Viewport",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (viewportMode == ViewportMode.MODEL_3D) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewportMode = ViewportMode.AR_CAMERA }
                        .testTag("mode_ar_camera"),
                    color = if (viewportMode == ViewportMode.AR_CAMERA) Color(0xFFFF6F00) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (viewportMode == ViewportMode.AR_CAMERA) Color(0xFFFFD700) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = if (viewportMode == ViewportMode.AR_CAMERA) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AR Engine Vision",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (viewportMode == ViewportMode.AR_CAMERA) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Top Color-coded System Segment Bar
        ColorSegmentBar(
            activeSystem = activeSystem,
            onSelectSystem = onSelectSystem
        )

        // Main Viewport (Either Interactive 3D Canvas or AR Overlay View)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (viewportMode) {
                ViewportMode.MODEL_3D -> {
                    Interactive3DViewport(
                        components = components,
                        selectedComponent = selectedComponent,
                        activeSystemFilter = activeSystem,
                        onComponentSelect = { comp ->
                            onSelectComponent(comp)
                        },
                        onOpenDetailManual = { comp ->
                            onSelectComponent(comp)
                            showDetailSheet = true
                        }
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
        }

        // Horizontal Component Selector Tray
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "EXPLORE COMPONENTS (${components.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(components) { comp ->
                        val isSelected = selectedComponent?.id == comp.id

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectComponent(comp)
                                }
                                .testTag("comp_card_${comp.id}"),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) comp.system.color else Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(comp.system.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = comp.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                    )
                                    Text(
                                        text = comp.system.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF64748B)
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
