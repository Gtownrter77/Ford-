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

data class TorqueEntry(
    val id: String,
    val boltLocation: String,
    val componentName: String,
    val system: VehicleSystem,
    val torqueFtLbs: Float,
    val threadSpec: String,
    val socketSize: String,
    val tighteningPattern: String = "Standard Star/Cross Pattern",
    val isTty: Boolean = false, // Torque-To-Yield (Must replace bolts)
    val threadLocker: String? = null,
    val overtighteningWarning: String
)

enum class TorqueUnit {
    FT_LBS,
    NM,
    IN_LBS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorqueSpecsQuickReference(
    components: List<Component3DModel>,
    initialSearchQuery: String = "",
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    var selectedSystemFilter by remember { mutableStateOf<VehicleSystem?>(null) }
    var selectedUnit by remember { mutableStateOf(TorqueUnit.FT_LBS) }
    var showTtyOnly by remember { mutableStateOf(false) }
    var activeTorqueSimulatorEntry by remember { mutableStateOf<TorqueEntry?>(null) }

    activeTorqueSimulatorEntry?.let { entry ->
        TorqueWrenchSimulatorDialog(
            fastenerName = entry.boltLocation,
            componentName = entry.componentName,
            targetTorqueFtLbs = entry.torqueFtLbs,
            socketSize = entry.socketSize,
            isTty = entry.isTty,
            targetAngleDegrees = if (entry.isTty) 90f else 0f,
            onDismiss = { activeTorqueSimulatorEntry = null }
        )
    }

    // Master database of 2004 Ford Explorer Sport Trac Torque Specs compiled from OEM service manuals & component models
    val allTorqueEntries = remember(components) {
        val list = mutableListOf<TorqueEntry>()

        // Extracted from components
        components.forEach { comp ->
            comp.torqueSpecs.forEachIndexed { idx, spec ->
                val ftLbsVal = spec.torqueFtLbs.toFloatOrNull() ?: 20f
                list.add(
                    TorqueEntry(
                        id = "${comp.id}_$idx",
                        boltLocation = spec.fastenerName,
                        componentName = comp.name,
                        system = comp.system,
                        torqueFtLbs = ftLbsVal,
                        threadSpec = spec.notes.ifBlank { "Standard Metric/SAE Fastener" },
                        socketSize = when {
                            spec.fastenerName.contains("Spark Plug", ignoreCase = true) -> "5/8 in Spark Plug Socket"
                            spec.fastenerName.contains("Caliper", ignoreCase = true) -> "13mm / 15mm Socket"
                            spec.fastenerName.contains("Wheel", ignoreCase = true) || spec.fastenerName.contains("Lug", ignoreCase = true) -> "19mm / 3/4 in Deep Socket"
                            spec.fastenerName.contains("Bed", ignoreCase = true) -> "T55 Torx Bit"
                            spec.fastenerName.contains("Head", ignoreCase = true) -> "13mm 12-Point Socket"
                            spec.fastenerName.contains("Thermostat", ignoreCase = true) -> "8mm / 10mm Socket"
                            spec.fastenerName.contains("Oil Drain", ignoreCase = true) -> "13mm Socket"
                            spec.fastenerName.contains("Tie Rod", ignoreCase = true) -> "18mm Castle Nut Socket"
                            spec.fastenerName.contains("Driveshaft", ignoreCase = true) -> "12mm 12-Point Socket"
                            else -> "Standard 10mm-18mm Socket"
                        },
                        tighteningPattern = when {
                            spec.fastenerName.contains("Head", ignoreCase = true) || spec.fastenerName.contains("Intake", ignoreCase = true) -> "Spiral Outward 3-Step Sequence"
                            spec.fastenerName.contains("Wheel", ignoreCase = true) || spec.fastenerName.contains("Pan", ignoreCase = true) -> "Criss-Cross Star Pattern"
                            else -> "Even Alternating Sequence"
                        },
                        isTty = spec.notes.contains("stretch", ignoreCase = true) || spec.fastenerName.contains("Cylinder Head", ignoreCase = true) || spec.notes.contains("replace", ignoreCase = true),
                        threadLocker = when {
                            spec.fastenerName.contains("Driveshaft", ignoreCase = true) || spec.fastenerName.contains("Flywheel", ignoreCase = true) -> "Blue Threadlocker 242"
                            spec.fastenerName.contains("Exhaust", ignoreCase = true) || spec.fastenerName.contains("Spark Plug", ignoreCase = true) -> "High-Temp Anti-Seize Compound"
                            else -> null
                        },
                        overtighteningWarning = when {
                            spec.fastenerName.contains("Spark Plug", ignoreCase = true) -> "DO NOT overtighten in aluminum heads! Stripping threads requires Timesert thread repair."
                            spec.fastenerName.contains("Oil Pan", ignoreCase = true) || spec.fastenerName.contains("Valve Cover", ignoreCase = true) -> "Crushing rubber gasket causes permanent oil leaks. Tighten in inch-pounds only."
                            spec.fastenerName.contains("Cylinder Head", ignoreCase = true) -> "Torque-To-Yield stretch bolts! Angle gauge required. Always replace with new bolts."
                            spec.fastenerName.contains("Wheel", ignoreCase = true) -> "Uneven lug nut torque warps brake rotors and causes pedal pulsation."
                            else -> "Excessive torque can snap Grade 8 studs or strip aluminum casting threads."
                        }
                    )
                )
            }
        }

        // Additional critical 2004 Sport Trac chassis & powertrain torque specs
        list.addAll(
            listOf(
                TorqueEntry("extra_1", "4.0L SOHC Spark Plugs", "Engine Ignition", VehicleSystem.ENGINE, 13f, "AWSF-32PM Platinum", "5/8 in Spark Plug Socket", "Hand-tighten then 1/16 turn", false, "Anti-Seize", "Aluminum head threads strip easily if over-torqued. Cold engine only!"),
                TorqueEntry("extra_2", "Lower Control Arm Ball Joint Pinch Bolt", "Front Suspension", VehicleSystem.BRAKES_CHASSIS, 83f, "Grade 10.9 Bolt", "18mm Deep Socket", "Single pass torque", false, "Blue Loctite 242", "Critical safety bolt. Ensure pinch bolt seats in ball joint groove."),
                TorqueEntry("extra_3", "Front Wheel Lug Nuts (1/2-20 Studs)", "Wheels & Hubs", VehicleSystem.BRAKES_CHASSIS, 100f, "1/2 in - 20 Thread", "19mm / 3/4 in Deep Impact", "5-Lug Star Pattern in 2 Passes", false, null, "Torque in star pattern to avoid warping brake rotors."),
                TorqueEntry("extra_4", "5R55E Transmission Oil Pan Bolts", "Automatic Transmission", VehicleSystem.TRANSMISSION, 10f, "M6 x 1.0 Flange Bolts", "10mm Socket", "Criss-Cross Outer Edge", false, null, "Measured in INCH-POUNDS (120 in-lbs). Over-torquing crushes pan gasket."),
                TorqueEntry("extra_5", "Front Axle Shaft Hub Nut", "Front 4WD Hub Assembly", VehicleSystem.TRANSMISSION, 184f, "M24 Large Spindle Nut", "32mm Deep Axle Socket", "Single Continuous Heavy Pass", true, "Retaining Cotter Pin", "Extreme high torque required to set front wheel bearing preload."),
                TorqueEntry("extra_6", "4.0L SOHC Timing Chain Tensioner Retaining Plug", "Camshaft Timing System", VehicleSystem.ENGINE, 32f, "Hydraulic Oil Pressure Plug", "19mm Socket", "Torque & Verify O-ring Seal", false, "New O-ring Washer", "DO NOT over-tighten into aluminum block timing cassette housing."),
                TorqueEntry("extra_7", "Front Brake Caliper Anchor Bracket to Knuckle", "Front Disc Brakes", VehicleSystem.BRAKES_CHASSIS, 85f, "15mm Heavy Hex Bolt", "15mm Impact Socket", "Heavy Torque Pass", false, "Blue Loctite 242", "Must use threadlocker to prevent brake bracket loosening under heavy stopping.")
            )
        )

        list.distinctBy { "${it.boltLocation}_${it.componentName}" }
    }

    // Filtered entries
    val filteredEntries = remember(searchQuery, selectedSystemFilter, showTtyOnly, allTorqueEntries) {
        allTorqueEntries.filter { entry ->
            val matchesQuery = searchQuery.isBlank() ||
                    entry.boltLocation.contains(searchQuery, ignoreCase = true) ||
                    entry.componentName.contains(searchQuery, ignoreCase = true) ||
                    entry.socketSize.contains(searchQuery, ignoreCase = true) ||
                    entry.threadSpec.contains(searchQuery, ignoreCase = true)

            val matchesSystem = selectedSystemFilter == null || entry.system == selectedSystemFilter
            val matchesTty = !showTtyOnly || entry.isTty

            matchesQuery && matchesSystem && matchesTty
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. TOP HEADER & SEARCH BAR
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF6F00).copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TORQUE SPECIFICATIONS DATABASE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFFFF6F00)
                            )
                            Text(
                                text = "2004 Ford Explorer Sport Trac",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // Unit Converter Selector Switch
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            TorqueUnit.values().forEach { unit ->
                                val isSelected = selectedUnit == unit
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF0284C7) else Color.Transparent)
                                        .clickable { selectedUnit = unit }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("unit_toggle_${unit.name}")
                                ) {
                                    Text(
                                        text = when (unit) {
                                            TorqueUnit.FT_LBS -> "ft-lbs"
                                            TorqueUnit.NM -> "Nm"
                                            TorqueUnit.IN_LBS -> "in-lbs"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by bolt location, plug, wheel, head...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFF6F00)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6F00),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("torque_search_input")
                )
            }
        }

        // 2. SYSTEM FILTER CHIPS & STRETCH BOLT FILTER
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedSystemFilter == null,
                    onClick = { selectedSystemFilter = null },
                    label = { Text("All Systems (${allTorqueEntries.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }

            items(VehicleSystem.values()) { sys ->
                FilterChip(
                    selected = selectedSystemFilter == sys,
                    onClick = {
                        selectedSystemFilter = if (selectedSystemFilter == sys) null else sys
                    },
                    label = { Text(sys.displayName) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(sys.color)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = sys.color,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )
            }

            item {
                FilterChip(
                    selected = showTtyOnly,
                    onClick = { showTtyOnly = !showTtyOnly },
                    label = { Text("TTY Stretch Bolts Only") },
                    leadingIcon = {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEF4444),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFEF4444)
                    )
                )
            }
        }

        // 3. RESULTS COUNTER & TIGHTENING TIP BANNER
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color(0xFF0B132B),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${filteredEntries.size} Specs Found",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Text(
                    text = "Always lubricate threads as specified before torquing",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. TORQUE SPECIFICATION CARDS LIST
        if (filteredEntries.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No torque specifications match '$searchQuery'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedSystemFilter = null
                            showTtyOnly = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Reset Search Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEntries, key = { it.id }) { entry ->
                    TorqueSpecCard(
                        entry = entry,
                        unit = selectedUnit,
                        onCalibrateTorque = { activeTorqueSimulatorEntry = entry }
                    )
                }
            }
        }
    }
}

@Composable
fun TorqueSpecCard(
    entry: TorqueEntry,
    unit: TorqueUnit,
    onCalibrateTorque: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Formatted Value Conversion
    val displayedValue = when (unit) {
        TorqueUnit.FT_LBS -> "%.0f ft-lbs".format(entry.torqueFtLbs)
        TorqueUnit.NM -> "%.0f Nm".format(entry.torqueFtLbs * 1.35582f)
        TorqueUnit.IN_LBS -> "%.0f in-lbs".format(entry.torqueFtLbs * 12f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("torque_card_${entry.id}"),
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (entry.isTty) Color(0xFFEF4444).copy(alpha = 0.6f) else Color(0xFF334155)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = entry.system.color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, entry.system.color)
                ) {
                    Text(
                        text = entry.system.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = entry.system.color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (entry.isTty) {
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "TTY STRETCH BOLT (REPLACE)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.boltLocation,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Component: ${entry.componentName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                // High-Visibility Torque Value Box
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFF6F00))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayedValue,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "TARGET SPEC",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF6F00)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tool & Thread Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("REQUIRED TOOL / SOCKET", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(entry.socketSize, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }

                entry.threadLocker?.let { locker ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("SEALANT / LOCKER", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Text(locker, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tightening Pattern & Over-tightening Warning Box
            Surface(
                color = Color(0xFF0B132B),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Grid4x4, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sequence: ${entry.tighteningPattern}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = entry.overtighteningWarning,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Torque Wrench Simulator Action Button
            Button(
                onClick = onCalibrateTorque,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_calibrate_torque_${entry.id}")
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("⚡ OPEN DIGITAL TORQUE WRENCH SIMULATOR", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
