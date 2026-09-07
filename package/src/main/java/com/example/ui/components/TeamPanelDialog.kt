package com.example.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class SkillLevel(val label: String, val subtitle: String, val icon: ImageVector, val color: Color) {
    ROOKIE("Rookie", "Looking for a ratchet • Basic tool photos & extra guidance", Icons.Default.HelpOutline, Color(0xFF10B981)),
    SHADE_TREE("Shade Tree", "Intermediate • Concise torque specs & tool lists", Icons.Default.Build, Color(0xFF38BDF8)),
    MASTER("Master Mechanic", "Installing Nitrous • Engineering tolerances & custom specs", Icons.Default.Speed, Color(0xFFFF6F00))
}

data class TeamMember(
    val name: String,
    val role: String,
    val avatarBg: Color,
    val quote: String,
    val focusArea: String
)

@Composable
fun TeamPanelDialog(
    currentSkillLevel: SkillLevel,
    onSkillLevelChange: (SkillLevel) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Team Perspectives, 1: Suggestions in Priority Order, 2: Skill Level Setting

    val teamMembers = listOf(
        TeamMember(
            name = "Marcus Vance",
            role = "Lead Systems & 3D BILT Engine Architect",
            avatarBg = Color(0xFF0284C7),
            quote = "Our 3D vector CAD engine delivers full 60 FPS interactive exploded views across Engine, Dash, Drivetrain, HVAC, Sunroof, and Chassis without bloating app size.",
            focusArea = "Full 3D Vehicle Domain Mapping & Interactive Explosion Vectors"
        ),
        TeamMember(
            name = "Big Mike 'The Wrench'",
            role = "Master AI Mentor & Lead Mechanic",
            avatarBg = Color(0xFFFF6F00),
            quote = "I'm right here in your ear! Whether you're trying to figure out which end of the wrench to hold or degreeing a camshaft for 250 shot of nitrous, I keep it calm, clear, and encouraging.",
            focusArea = "Voice-Guided Coaching, Friendly Mentorship & Under-Truck Live Chat"
        ),
        TeamMember(
            name = "Dr. Elena Rostova",
            role = "Educational Pedagogy & VR Sim Director",
            avatarBg = Color(0xFF10B981),
            quote = "Practice in 3D first! By requiring users to complete the virtual tool setup and assembly practice in 3D before climbing under the truck, we ensure safety and complete confidence.",
            focusArea = "Virtual Practice Certification -> Real Truck Repair Workflow"
        ),
        TeamMember(
            name = "Jax Rivera",
            role = "3D UI / UX & BILT Interactive Designer",
            avatarBg = Color(0xFFEC4899),
            quote = "Every nut, bolt, washer, gasket, and weatherpack wiring connector is visually mapped with interactive callout pins and instant torque spec HUD overlays.",
            focusArea = "BILT-Style Step Timeline, Callouts & Fastener Inventory HUD"
        ),
        TeamMember(
            name = "Samira Patel",
            role = "Vehicle Diagnostics & Technical Data Lead",
            avatarBg = Color(0xFF8B5CF6),
            quote = "We compiled factory OEM specs for 52+ assemblies with exact ft-lb/Nm torque values, FORScan OBD-II diagnostic flows, and maintenance intervals for the 2004 Sport Trac 4.0L SOHC.",
            focusArea = "OEM Specifications, Torque DB & FORScan OBD-II Integration"
        )
    )

    val priorities = listOf(
        "1. Interactive 3D BILT Mapping (Highest Importance)" to "Every single system—Engine, Dash, Interior, Drivetrain 4WD, Transmission, Suspension, Lighting, Heating/Air, and Sunroof—mapped in full 3D with explode sliders, step timelines, and BILT voice guidance.",
        "2. Virtual Practice First, Real Truck Second" to "Mandatory virtual 3D repair practice mode before unlocking real-time under-truck mode, preventing dropped bolts and repair mistakes.",
        "3. Personalized Skill Level Adaptability" to "Dynamic instructions tailored for everyone: Rookie ('Looking for a ratchet'), Shade Tree Mechanic, or Master Mechanic ('Installing Nitrous').",
        "4. AI Virtual Mechanic & Mentor Coach" to "A warm, funny, caring, well-spoken AI mentor available in live voice/chat to walk you through troubleshooting, tools, and step-by-step repairs.",
        "5. Complete Hardware & Wiring Fastener HUD" to "Detailed tracking of every bolt, screw, washer, seal gasket, and weatherpack wiring harness connector with exact socket tools required."
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF38BDF8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFF38BDF8))
                        ) {
                            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("5-MEMBER DEV & MENTOR TEAM", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = Color(0xFF38BDF8))
                            Text("Collective Thoughts & Roadmap", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Team Perspectives", "Suggestions (Priority)", "Skill Level Setting").forEachIndexed { index, title ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { activeTab = index }
                                .testTag("team_tab_$index"),
                            color = if (activeTab == index) Color(0xFF0284C7) else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeTab == index) Color.White else Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content View
                when (activeTab) {
                    0 -> {
                        // Team Perspectives
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(teamMembers) { member ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = member.avatarBg,
                                                shape = CircleShape
                                            ) {
                                                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = member.name.take(1),
                                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(member.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                                Text(member.role, style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Surface(
                                            color = Color(0xFF0F172A),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                                        ) {
                                            Text(
                                                text = "\"${member.quote}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFE2E8F0),
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Focus: ${member.focusArea}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF94A3B8))
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Priority Suggestions
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(priorities) { (title, desc) ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0284C7))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF00F0FF))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(desc, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Skill Level Setting
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CHOOSE YOUR MECHANIC EXPERIENCE LEVEL",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Big Mike will adapt all voice coaching, repair descriptions, tool guides, and torque specs to match your background.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SkillLevel.values().forEach { level ->
                                val isSelected = currentSkillLevel == level
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSkillLevelChange(level) }
                                        .testTag("skill_option_${level.name}"),
                                    color = if (isSelected) level.color.copy(alpha = 0.15f) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, if (isSelected) level.color else Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = level.color.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            border = BorderStroke(1.dp, level.color)
                                        ) {
                                            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                                                Icon(level.icon, contentDescription = null, tint = level.color, modifier = Modifier.size(20.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(level.label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                            Text(level.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                                        }

                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = level.color, modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Close
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply & Return to 3D Virtual Truck", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
