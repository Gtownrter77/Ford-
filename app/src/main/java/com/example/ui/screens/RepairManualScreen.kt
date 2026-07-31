package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.SportTracData
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.ui.components.ColorSegmentBar
import com.example.ui.components.ComponentDetailSheet
import com.example.ui.components.FourWheelDriveDiagram
import com.example.ui.components.TorqueSpecsQuickReference

enum class RepairManualTab {
    PART_GUIDES,
    FOUR_WHEEL_DRIVE_DIAGRAM,
    TORQUE_SPECS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairManualScreen(
    components: List<Component3DModel>,
    activeSystem: VehicleSystem,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectSystem: (VehicleSystem) -> Unit,
    onSelectComponent: (Component3DModel) -> Unit,
    onAddToCart: ((Component3DModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(RepairManualTab.PART_GUIDES) }
    var activeComponentForSheet by remember { mutableStateOf<Component3DModel?>(null) }

    if (activeComponentForSheet != null) {
        ComponentDetailSheet(
            component = activeComponentForSheet!!,
            onDismiss = { activeComponentForSheet = null },
            onAddToCart = onAddToCart
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Section Mode Switcher Bar
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
                        .clickable { selectedTab = RepairManualTab.PART_GUIDES }
                        .testTag("tab_repair_guides"),
                    color = if (selectedTab == RepairManualTab.PART_GUIDES) Color(0xFF0284C7) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (selectedTab == RepairManualTab.PART_GUIDES) Color(0xFF38BDF8) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (selectedTab == RepairManualTab.PART_GUIDES) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Component Manuals",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == RepairManualTab.PART_GUIDES) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedTab = RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM }
                        .testTag("tab_4wd_diagram"),
                    color = if (selectedTab == RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM) Color(0xFF10B981) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (selectedTab == RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM) Color(0xFF34D399) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TireRepair,
                            contentDescription = null,
                            tint = if (selectedTab == RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4WD Diagram",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedTab = RepairManualTab.TORQUE_SPECS }
                        .testTag("tab_torque_specs_db"),
                    color = if (selectedTab == RepairManualTab.TORQUE_SPECS) Color(0xFFFF6F00) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (selectedTab == RepairManualTab.TORQUE_SPECS) Color(0xFFFFD700) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = if (selectedTab == RepairManualTab.TORQUE_SPECS) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Torque Specs",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == RepairManualTab.TORQUE_SPECS) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        if (selectedTab == RepairManualTab.FOUR_WHEEL_DRIVE_DIAGRAM) {
            FourWheelDriveDiagram(
                components = components,
                onSelectComponent = onSelectComponent,
                modifier = Modifier.weight(1f)
            )
        } else if (selectedTab == RepairManualTab.TORQUE_SPECS) {
            TorqueSpecsQuickReference(
                components = components,
                initialSearchQuery = searchQuery,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Search Header
            Surface(
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "2004 SPORT TRAC REPAIR MANUAL",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search parts, OEM numbers, torque specs, codes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_search_input")
                )
            }
        }

        // Color Segment Bar
        ColorSegmentBar(
            activeSystem = activeSystem,
            onSelectSystem = onSelectSystem
        )

        // Vehicle Overview Specifications Quick Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("VEHICLE ENGINE SPECIFICATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                    Text("4.0L Cologne SOHC V6 • 210 HP • 254 lb-ft", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
                Surface(
                    color = Color(0xFF0284C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("12-Valve V6", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Component List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(components) { comp ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onSelectComponent(comp)
                            activeComponentForSheet = comp
                        }
                        .testTag("manual_item_${comp.id}"),
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = comp.system.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, comp.system.color),
                                shape = RoundedCornerShape(8.dp)
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
                                    Text(
                                        text = comp.system.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = comp.system.color
                                    )
                                }
                            }

                            Text(
                                text = "OEM #${comp.oemPartNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = comp.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = comp.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick info badges (Tools count, Torque specs count, Steps)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${comp.requiredTools.size} Tools", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Compress, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${comp.torqueSpecs.size} Torque Specs", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${comp.repairSteps.size} Steps", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }
        }
    }
}
}


