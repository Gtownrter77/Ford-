package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.VehicleSystem

enum class LayerPreset(val label: String, val description: String) {
    ALL("All Systems", "Show all vehicle system layers"),
    POWER_TRAIN("Powertrain", "Engine, Air Intake, Transmission & Drivetrain"),
    THERMAL("Thermal & Cooling", "Cooling System & Air Conditioning"),
    ELECTRICAL("Electrical & Ignition", "Battery, Alternator, Ignition & Dash"),
    CHASSIS("Chassis & Brakes", "Suspension, Brakes, Torsion Bars & Steering")
}

data class LayerControllerState(
    val systemVisibility: Map<VehicleSystem, Boolean> = VehicleSystem.values()
        .filter { it != VehicleSystem.ALL }
        .associateWith { true },
    val partVisibility: Map<String, Boolean> = emptyMap(),
    val isolatedSystem: VehicleSystem? = null,
    val searchQuery: String = "",
    // Wording & Annotation Layer Toggles
    val showCalloutLeaders: Boolean = true,
    val showTechnicalAnnotations: Boolean = true,
    val showDimensions: Boolean = true,
    val showHudInfoCards: Boolean = true
) {
    val isAllWordingHidden: Boolean
        get() = !showCalloutLeaders && !showTechnicalAnnotations && !showDimensions && !showHudInfoCards

    val activeWordingLayerCount: Int
        get() = (if (showCalloutLeaders) 1 else 0) +
                (if (showTechnicalAnnotations) 1 else 0) +
                (if (showDimensions) 1 else 0) +
                (if (showHudInfoCards) 1 else 0)

    fun isSystemVisible(system: VehicleSystem): Boolean {
        if (isolatedSystem != null) {
            return system == isolatedSystem
        }
        return systemVisibility[system] ?: true
    }

    fun isPartVisible(component: Component3DModel): Boolean {
        if (!isSystemVisible(component.system)) return false
        val customPartVis = partVisibility[component.id]
        if (customPartVis != null && !customPartVis) return false
        if (searchQuery.isNotBlank()) {
            return component.name.contains(searchQuery, ignoreCase = true) ||
                   component.system.displayName.contains(searchQuery, ignoreCase = true) ||
                   component.oemPartNumber.contains(searchQuery, ignoreCase = true)
        }
        return true
    }

    val visibleSystemCount: Int
        get() = if (isolatedSystem != null) 1 else systemVisibility.count { it.key != VehicleSystem.ALL && it.value }

    val totalSystemCount: Int
        get() = VehicleSystem.values().count { it != VehicleSystem.ALL }
}

enum class ControllerTab {
    SYSTEMS, WORDING_LAYERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerControllerDialog(
    components: List<Component3DModel>,
    state: LayerControllerState,
    onStateChange: (LayerControllerState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ControllerTab.SYSTEMS) }
    var expandedSystem by remember { mutableStateOf<VehicleSystem?>(null) }

    val systemPartMap = remember(components) {
        components.groupBy { it.system }
    }

    val availableSystems = remember {
        VehicleSystem.values().filter { it != VehicleSystem.ALL }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("close_layer_controller_btn")
            ) {
                Text("Done")
            }
        },
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier.testTag("layer_controller_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "3D Layer Controller",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (activeTab == ControllerTab.SYSTEMS)
                                "${state.visibleSystemCount}/${state.totalSystemCount} Systems Active"
                            else
                                if (state.isAllWordingHidden) "🚫 Clean Model Mode (No Text)" else "${state.activeWordingLayerCount}/4 Text Layers Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.isAllWordingHidden && activeTab == ControllerTab.WORDING_LAYERS) Color(0xFF22C55E) else Color(0xFF94A3B8)
                        )
                    }
                }

                if (state.isolatedSystem != null) {
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SOLO: ${state.isolatedSystem.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tab Switcher: 3D Systems vs Wording & Annotations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B132B), RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = if (activeTab == ControllerTab.SYSTEMS) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = ControllerTab.SYSTEMS }
                            .testTag("tab_layer_systems")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "3D Systems",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = if (activeTab == ControllerTab.WORDING_LAYERS) Color(0xFF0284C7) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = ControllerTab.WORDING_LAYERS }
                            .testTag("tab_layer_wording")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.isAllWordingHidden) Icons.Default.SubtitlesOff else Icons.Default.Subtitles,
                                contentDescription = null,
                                tint = if (state.isAllWordingHidden) Color(0xFF22C55E) else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Wording & Text",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                if (activeTab == ControllerTab.WORDING_LAYERS) {
                    // WORDING & ANNOTATION LAYERS TAB
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Master Clean Model Toggle Switch
                        Surface(
                            color = if (state.isAllWordingHidden) Color(0xFF22C55E).copy(alpha = 0.15f) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, if (state.isAllWordingHidden) Color(0xFF22C55E) else Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (state.isAllWordingHidden) Icons.Default.VisibilityOff else Icons.Default.SubtitlesOff,
                                        contentDescription = null,
                                        tint = if (state.isAllWordingHidden) Color(0xFF22C55E) else Color(0xFF00F0FF),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Clean Model (Hide All Wording)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Declutter 3D view: hides all labels, callouts & HUD text cards",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Switch(
                                    checked = state.isAllWordingHidden,
                                    onCheckedChange = { hideAll ->
                                        onStateChange(
                                            state.copy(
                                                showCalloutLeaders = !hideAll,
                                                showTechnicalAnnotations = !hideAll,
                                                showDimensions = !hideAll,
                                                showHudInfoCards = !hideAll
                                            )
                                        )
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF22C55E),
                                        uncheckedThumbColor = Color(0xFF64748B),
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.testTag("switch_master_clean_wording")
                                )
                            }
                        }

                        Divider(color = Color(0xFF334155))

                        Text(
                            text = "INDIVIDUAL WORDING LAYERS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = Color(0xFF38BDF8)
                        )

                        // 1. Part Callout Labels Layer
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (state.showCalloutLeaders) Color(0xFF0284C7) else Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🏷️", fontSize = 16.sp)
                                    Column {
                                        Text("Part Callouts & Leaders", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Text("STEP # & Component Name callout boxes on 3D geometry", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    }
                                }
                                Switch(
                                    checked = state.showCalloutLeaders,
                                    onCheckedChange = { checked ->
                                        onStateChange(state.copy(showCalloutLeaders = checked))
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7)),
                                    modifier = Modifier.testTag("switch_layer_callouts")
                                )
                            }
                        }

                        // 2. Technical Specs & Torque Wording Layer
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (state.showTechnicalAnnotations) Color(0xFF0284C7) else Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🔩", fontSize = 16.sp)
                                    Column {
                                        Text("Technical Specs & Torque Values", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Text("OEM Part Numbers & Ft-Lb torque spec text labels", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    }
                                }
                                Switch(
                                    checked = state.showTechnicalAnnotations,
                                    onCheckedChange = { checked ->
                                        onStateChange(state.copy(showTechnicalAnnotations = checked))
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7)),
                                    modifier = Modifier.testTag("switch_layer_specs")
                                )
                            }
                        }

                        // 3. CAD Dimensions Layer
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (state.showDimensions) Color(0xFF0284C7) else Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("📐", fontSize = 16.sp)
                                    Column {
                                        Text("CAD Dimensions HUD", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Text("Part width × height × depth millimeter measurement pill", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    }
                                }
                                Switch(
                                    checked = state.showDimensions,
                                    onCheckedChange = { checked ->
                                        onStateChange(state.copy(showDimensions = checked))
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7)),
                                    modifier = Modifier.testTag("switch_layer_dimensions")
                                )
                            }
                        }

                        // 4. HUD Info Overlay Cards Layer
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (state.showHudInfoCards) Color(0xFF0284C7) else Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("📄", fontSize = 16.sp)
                                    Column {
                                        Text("Floating HUD Cards & BILT Steps", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Text("Bottom interactive component card & repair step text overlays", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    }
                                }
                                Switch(
                                    checked = state.showHudInfoCards,
                                    onCheckedChange = { checked ->
                                        onStateChange(state.copy(showHudInfoCards = checked))
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7)),
                                    modifier = Modifier.testTag("switch_layer_hud_cards")
                                )
                            }
                        }
                    }
                } else {
                    // 3D VEHICLE SYSTEMS TAB
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Search Bar
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { newQuery ->
                                onStateChange(state.copy(searchQuery = newQuery))
                            },
                            placeholder = { Text("Filter system or part name...", color = Color(0xFF64748B)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onStateChange(state.copy(searchQuery = "")) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0B132B),
                                unfocusedContainerColor = Color(0xFF0B132B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("layer_search_input")
                        )

                // Presets Quick Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LayerPreset.values()) { preset ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val newMap = state.systemVisibility.toMutableMap()
                                    when (preset) {
                                        LayerPreset.ALL -> {
                                            availableSystems.forEach { newMap[it] = true }
                                        }
                                        LayerPreset.POWER_TRAIN -> {
                                            availableSystems.forEach {
                                                newMap[it] = (it == VehicleSystem.ENGINE || it == VehicleSystem.AIR_INTAKE || it == VehicleSystem.TRANSMISSION || it == VehicleSystem.DRIVETRAIN_4WD)
                                            }
                                        }
                                        LayerPreset.THERMAL -> {
                                            availableSystems.forEach {
                                                newMap[it] = (it == VehicleSystem.COOLING || it == VehicleSystem.AIR_CONDITIONING)
                                            }
                                        }
                                        LayerPreset.ELECTRICAL -> {
                                            availableSystems.forEach {
                                                newMap[it] = (it == VehicleSystem.ELECTRICAL || it == VehicleSystem.INTERIOR_DASH)
                                            }
                                        }
                                        LayerPreset.CHASSIS -> {
                                            availableSystems.forEach {
                                                newMap[it] = (it == VehicleSystem.BRAKES_CHASSIS || it == VehicleSystem.DRIVETRAIN_4WD)
                                            }
                                        }
                                    }
                                    onStateChange(state.copy(systemVisibility = newMap, isolatedSystem = null))
                                }
                                .testTag("preset_${preset.name}")
                        ) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Global Action Buttons (Show All / Hide All / Clear Solo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val newMap = availableSystems.associateWith { true }
                            onStateChange(state.copy(systemVisibility = newMap, isolatedSystem = null))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_show_all_layers")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Show All", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            val newMap = availableSystems.associateWith { false }
                            onStateChange(state.copy(systemVisibility = newMap, isolatedSystem = null))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_hide_all_layers")
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hide All", style = MaterialTheme.typography.labelSmall)
                    }

                    if (state.isolatedSystem != null) {
                        Button(
                            onClick = {
                                onStateChange(state.copy(isolatedSystem = null))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier.testTag("btn_clear_solo")
                        ) {
                            Text("Reset Solo", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                        }
                    }
                }

                // System Layers List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableSystems) { system ->
                        val partsInSystem = systemPartMap[system] ?: emptyList()
                        val isVisible = state.isSystemVisible(system)
                        val isSoloed = state.isolatedSystem == system
                        val isExpanded = expandedSystem == system

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isSoloed) Color(0xFF272010) else if (isVisible) Color(0xFF1E293B) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSoloed) Color(0xFFF59E0B) else if (isVisible) system.color else Color(0xFF334155)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                expandedSystem = if (isExpanded) null else system
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(system.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = system.displayName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isVisible) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isVisible) Color.White else Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "${partsInSystem.size} 3D components",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Solo Button
                                        IconButton(
                                            onClick = {
                                                if (isSoloed) {
                                                    onStateChange(state.copy(isolatedSystem = null))
                                                } else {
                                                    onStateChange(state.copy(isolatedSystem = system))
                                                }
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("solo_btn_${system.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CenterFocusStrong,
                                                contentDescription = "Solo System Layer",
                                                tint = if (isSoloed) Color(0xFFF59E0B) else Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Toggle Visibility Button
                                        Switch(
                                            checked = isVisible,
                                            onCheckedChange = { checked ->
                                                val newMap = state.systemVisibility.toMutableMap()
                                                newMap[system] = checked
                                                onStateChange(state.copy(systemVisibility = newMap, isolatedSystem = null))
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = system.color,
                                                uncheckedThumbColor = Color(0xFF64748B),
                                                uncheckedTrackColor = Color(0xFF1E293B)
                                            ),
                                            modifier = Modifier.testTag("switch_layer_${system.name}")
                                        )
                                    }
                                }

                                // Expanded Part-level Visibility Toggles
                                if (isExpanded && partsInSystem.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFF334155))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    partsInSystem.forEach { part ->
                                        val isPartVis = state.isPartVisible(part)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp, horizontal = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = part.name,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                    color = if (isPartVis) Color.White else Color(0xFF64748B)
                                                )
                                                Text(
                                                    text = "OEM: ${part.oemPartNumber}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF64748B)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    val newPartMap = state.partVisibility.toMutableMap()
                                                    newPartMap[part.id] = !isPartVis
                                                    onStateChange(state.copy(partVisibility = newPartMap))
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("toggle_part_${part.id}")
                                            ) {
                                                Icon(
                                                    imageVector = if (isPartVis) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle Part Visibility",
                                                    tint = if (isPartVis) Color(0xFF38BDF8) else Color(0xFF64748B),
                                                    modifier = Modifier.size(16.dp)
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
    }
}
)
}
