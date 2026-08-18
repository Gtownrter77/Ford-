package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaintenanceEntity
import com.example.data.local.UpcomingTaskEntity
import com.example.data.local.VehicleProfileEntity
import com.example.ui.viewmodel.MainTab

/**
 * Lounge / Entrance Shell
 *
 * Style reminder: midnight navy, charcoal, burnished steel, raw wood and amber work-light
 * accents. This is the front door to a personal garage, not a generic dashboard. Keep the
 * truck, the Mentor, and the next practical decision visually central.
 */
@Composable
fun LoungeScreen(
    vehicleProfile: VehicleProfileEntity?,
    maintenanceLogs: List<MaintenanceEntity>,
    upcomingTasks: List<UpcomingTaskEntity>,
    skillLabel: String,
    onOpenSkillSettings: () -> Unit,
    onOpenVoiceSettings: () -> Unit,
    onNavigate: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = vehicleProfile ?: VehicleProfileEntity()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .testTag("lounge_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "THE LOUNGE",
                    color = Color(0xFFF59E0B),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pull up a chair, Ry.",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your Sport Trac is waiting. What do you want to do with it today?",
                    color = Color(0xFFCBD5E1),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            VehicleWelcomeCard(profile = profile, onOpenSkillSettings = onOpenSkillSettings)
        }

        item {
            SectionLabel(title = "OPEN A DOOR")
        }

        items(
            items = listOf(
                Doorway(
                    title = "Get to Work",
                    subtitle = "Practice on the to-scale truck before the real repair.",
                    icon = Icons.Default.Build,
                    accent = Color(0xFFF59E0B),
                    tab = MainTab.VIEW_3D,
                    tag = "door_shop"
                ),
                Doorway(
                    title = "Figure It Out",
                    subtitle = "Symptoms, A/C, sound clues, and evidence before buying.",
                    icon = Icons.Default.Psychology,
                    accent = Color(0xFF38BDF8),
                    tab = MainTab.DIAGNOSTICS,
                    tag = "door_diagnostics"
                ),
                Doorway(
                    title = "Plan the Parts",
                    subtitle = "Readiness packages, choices, fitment, and price watch.",
                    icon = Icons.Default.ShoppingCart,
                    accent = Color(0xFF34D399),
                    tab = MainTab.PARTS_CART,
                    tag = "door_part_store"
                ),
                Doorway(
                    title = "See the Story",
                    subtitle = "Service history and what is coming up next.",
                    icon = Icons.Default.Speed,
                    accent = Color(0xFFA78BFA),
                    tab = MainTab.MAINTENANCE,
                    tag = "door_maintenance"
                ),
                Doorway(
                    title = "The Body Shop",
                    subtitle = "Body kits, stance, wheels, paint, and interior concepts.",
                    icon = Icons.Default.Tune,
                    accent = Color(0xFFFB7185),
                    tab = null,
                    tag = "door_body_shop",
                    roadmapOnly = true
                )
            ),
            key = { it.title }
        ) { doorway ->
            DoorwayCard(
                doorway = doorway,
                onClick = { doorway.tab?.let(onNavigate) }
            )
        }

        item {
            MentorWorkbenchCard(
                skillLabel = skillLabel,
                maintenanceCount = maintenanceLogs.size,
                upcomingCount = upcomingTasks.size,
                onOpenSkillSettings = onOpenSkillSettings,
                onOpenVoiceSettings = onOpenVoiceSettings
            )
        }
    }
}

private data class Doorway(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val tab: MainTab?,
    val tag: String,
    val roadmapOnly: Boolean = false
)

@Composable
private fun VehicleWelcomeCard(
    profile: VehicleProfileEntity,
    onOpenSkillSettings: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        border = BorderStroke(1.dp, Color(0xFF374151)),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().testTag("lounge_vehicle_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                modifier = Modifier.size(58.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${profile.modelYear} Ford Explorer Sport Trac",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = profile.trimName,
                    color = Color(0xFFCBD5E1),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${profile.currentMileage.toString().reversed().chunked(3).joinToString(",").reversed()} miles logged",
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Your truck",
                tint = Color(0xFFFB7185),
                modifier = Modifier.size(20.dp).clickable(onClick = onOpenSkillSettings)
            )
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        color = Color(0xFF94A3B8),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp),
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun DoorwayCard(doorway: Doorway, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182231)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = doorway.tab != null, onClick = onClick)
            .testTag(doorway.tag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = doorway.accent.copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(doorway.icon, contentDescription = null, tint = doorway.accent, modifier = Modifier.size(25.dp))
                }
            }
            Spacer(modifier = Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = doorway.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (doorway.roadmapOnly) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFF334155), shape = RoundedCornerShape(50)) {
                            Text(
                                text = "NEXT",
                                color = Color(0xFFFBBF24),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = doorway.subtitle, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MentorWorkbenchCard(
    skillLabel: String,
    maintenanceCount: Int,
    upcomingCount: Int,
    onOpenSkillSettings: () -> Unit,
    onOpenVoiceSettings: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF202B3A)),
        border = BorderStroke(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.55f)),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().testTag("lounge_mentor_workbench")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF0EA5E9).copy(alpha = 0.16f), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(42.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mentor workbench", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Tell me the destination. I’ll help plan the route.", color = Color(0xFFBAE6FD), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Learning level: $skillLabel  •  $maintenanceCount service logs  •  $upcomingCount upcoming tasks",
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Color(0xFF0EA5E9),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable(onClick = onOpenSkillSettings).testTag("lounge_btn_skill_settings")
                ) {
                    Text("Tune my guidance", modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
                Surface(
                    color = Color.Transparent,
                    contentColor = Color(0xFFBAE6FD),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable(onClick = onOpenVoiceSettings).testTag("lounge_btn_voice_settings")
                ) {
                    Text("Voice & privacy", modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
