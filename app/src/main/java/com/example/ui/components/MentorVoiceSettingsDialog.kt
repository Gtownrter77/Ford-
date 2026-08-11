package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MentorVoiceSettingsRepository
import com.example.model.MentorVoiceSettings
import com.example.model.VoicePersonality
import com.example.util.HapticHelper
import com.example.util.MentorTtsManager

@Composable
fun MentorVoiceSettingsDialog(
    cached3DCount: Int = 14,
    cachedManualsCount: Int = 11,
    cachedSymptomsCount: Int = 18,
    cacheManifest: com.example.data.local.CacheManifestEntity? = null,
    onForceUpdate: () -> Unit = {},
    onUpgradeContent: () -> Unit = {},
    onCheckForUpgrades: () -> Unit = {},
    onClearCache: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    val repository = remember { MentorVoiceSettingsRepository(context) }
    val ttsManager = remember { MentorTtsManager(context) }

    var settings by remember { mutableStateOf(repository.loadSettings()) }
    val isSpeaking by ttsManager.isSpeaking.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, settings.activeProfile.toneColor),
            shadowElevation = 24.dp,
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("voice_settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Navigation Header
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
                            color = settings.activeProfile.toneColor.copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, settings.activeProfile.toneColor)
                        ) {
                            Box(
                                modifier = Modifier.padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = settings.activeProfile.toneColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "MENTOR VOICE SYNTHESIS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontSize = 11.sp
                                ),
                                color = settings.activeProfile.toneColor
                            )
                            Text(
                                text = "Coaching Voice & Speech Engine",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            HapticHelper.triggerControlTick(context, view, haptic)
                            onDismiss()
                        },
                        modifier = Modifier
                            .background(Color(0xFF1E293B), CircleShape)
                            .testTag("btn_close_voice_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Voice Settings",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section 1: Voice Profiles
                    item {
                        Text(
                            text = "CHOOSE MENTOR VOICE PROFILE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                    }

                    items(VoicePersonality.entries) { profile ->
                        val isSelected = settings.activeProfile == profile

                        Surface(
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF182232),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) profile.toneColor else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticHelper.triggerControlTick(context, view, haptic)
                                    val updated = settings.copy(
                                        activeProfile = profile,
                                        speechRate = profile.defaultSpeechRate,
                                        pitch = profile.defaultPitch
                                    )
                                    settings = updated
                                    repository.saveSettings(updated)
                                    ttsManager.applySettings(updated)
                                }
                                .testTag("voice_profile_card_${profile.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                HapticHelper.triggerControlTick(context, view, haptic)
                                                val updated = settings.copy(
                                                    activeProfile = profile,
                                                    speechRate = profile.defaultSpeechRate,
                                                    pitch = profile.defaultPitch
                                                )
                                                settings = updated
                                                repository.saveSettings(updated)
                                                ttsManager.applySettings(updated)
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = profile.toneColor,
                                                unselectedColor = Color(0xFF64748B)
                                            )
                                        )

                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = profile.title,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    ),
                                                    color = Color.White
                                                )

                                                Surface(
                                                    color = profile.toneColor.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, profile.toneColor)
                                                ) {
                                                    Text(
                                                        text = profile.badgeText,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 9.sp
                                                        ),
                                                        color = profile.toneColor,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = profile.roleTitle,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                    }

                                    // Preview Voice Audio Button
                                    OutlinedButton(
                                        onClick = {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            ttsManager.previewProfile(
                                                profile = profile,
                                                rate = if (isSelected) settings.speechRate else profile.defaultSpeechRate,
                                                pitch = if (isSelected) settings.pitch else profile.defaultPitch
                                            )
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = profile.toneColor
                                        ),
                                        border = BorderStroke(1.dp, profile.toneColor),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("btn_preview_${profile.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Sample",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = profile.tagline,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = Color(0xFF94A3B8)
                                )

                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = profile.toneColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Cadence: ${profile.cadenceDescription}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = Color(0xFFCBD5E1)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 2: Audio Engine Parameters (Rate & Pitch)
                    item {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SPEECH RATE & PITCH FREQUENCY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        ),
                                        color = settings.activeProfile.toneColor
                                    )

                                    TextButton(
                                        onClick = {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            val updated = settings.copy(
                                                speechRate = settings.activeProfile.defaultSpeechRate,
                                                pitch = settings.activeProfile.defaultPitch
                                            )
                                            settings = updated
                                            repository.saveSettings(updated)
                                            ttsManager.applySettings(updated)
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = "Reset Profile Defaults",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }

                                // Speech Rate Slider
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Speech Rate (Speed)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${String.format("%.2f", settings.speechRate)}x",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = settings.activeProfile.toneColor
                                        )
                                    }

                                    Slider(
                                        value = settings.speechRate,
                                        onValueChange = { value ->
                                            val updated = settings.copy(speechRate = value)
                                            settings = updated
                                            repository.saveSettings(updated)
                                            ttsManager.applySettings(updated)
                                        },
                                        valueRange = 0.70f..1.40f,
                                        steps = 14,
                                        colors = SliderDefaults.colors(
                                            thumbColor = settings.activeProfile.toneColor,
                                            activeTrackColor = settings.activeProfile.toneColor,
                                            inactiveTrackColor = Color(0xFF0F172A)
                                        ),
                                        modifier = Modifier.testTag("slider_speech_rate")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("0.70x (Deliberate)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                        Text("1.00x (Standard)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                        Text("1.40x (Rapid)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                    }
                                }

                                HorizontalDivider(color = Color(0xFF334155))

                                // Pitch Frequency Slider
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Voice Pitch (Frequency)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${String.format("%.2f", settings.pitch)} Hz",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = settings.activeProfile.toneColor
                                        )
                                    }

                                    Slider(
                                        value = settings.pitch,
                                        onValueChange = { value ->
                                            val updated = settings.copy(pitch = value)
                                            settings = updated
                                            repository.saveSettings(updated)
                                            ttsManager.applySettings(updated)
                                        },
                                        valueRange = 0.60f..1.40f,
                                        steps = 16,
                                        colors = SliderDefaults.colors(
                                            thumbColor = settings.activeProfile.toneColor,
                                            activeTrackColor = settings.activeProfile.toneColor,
                                            inactiveTrackColor = Color(0xFF0F172A)
                                        ),
                                        modifier = Modifier.testTag("slider_pitch")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Deep Baritone", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                        Text("Neutral", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                        Text("High Tenor / Female", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }

                    // Section 3: Professional Coaching Rules
                    item {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "COACHING RULES & AUDIO PROTOCOLS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = settings.activeProfile.toneColor
                                )

                                ToggleSettingRow(
                                    title = "Announce Critical Warnings First",
                                    description = "Speaks shop safety notes prior to step instructions.",
                                    checked = settings.announceWarningsFirst,
                                    onCheckedChange = { checked ->
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        val updated = settings.copy(announceWarningsFirst = checked)
                                        settings = updated
                                        repository.saveSettings(updated)
                                    },
                                    testTag = "switch_announce_warnings"
                                )

                                HorizontalDivider(color = Color(0xFF334155))

                                ToggleSettingRow(
                                    title = "Repeat Torque Specs Twice",
                                    description = "Ensures critical bolt torque limits are verified hands-free.",
                                    checked = settings.autoReadTorqueTwice,
                                    onCheckedChange = { checked ->
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        val updated = settings.copy(autoReadTorqueTwice = checked)
                                        settings = updated
                                        repository.saveSettings(updated)
                                    },
                                    testTag = "switch_repeat_torque"
                                )

                                HorizontalDivider(color = Color(0xFF334155))

                                ToggleSettingRow(
                                    title = "Garage Audio Ducking",
                                    description = "Reduces music/media playback when mentor is speaking.",
                                    checked = settings.audioDuckingEnabled,
                                    onCheckedChange = { checked ->
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        val updated = settings.copy(audioDuckingEnabled = checked)
                                        settings = updated
                                        repository.saveSettings(updated)
                                    },
                                    testTag = "switch_audio_ducking"
                                )

                                HorizontalDivider(color = Color(0xFF334155))

                                ToggleSettingRow(
                                    title = "Haptic Vibration Pulse",
                                    description = "Triggers subtle tactile haptics when mentor initiates speech.",
                                    checked = settings.hapticPulseOnSpeech,
                                    onCheckedChange = { checked ->
                                        HapticHelper.triggerControlTick(context, view, haptic)
                                        val updated = settings.copy(hapticPulseOnSpeech = checked)
                                        settings = updated
                                        repository.saveSettings(updated)
                                    },
                                    testTag = "switch_haptic_pulse"
                                )
                            }
                        }
                    }

                    // Section 4: Local Storage & Sync Status Indicator
                    item {
                        val isFullyCached = cached3DCount > 0 && cachedManualsCount > 0
                        val contentVersion = cacheManifest?.contentVersion ?: "2.4.0"
                        val latestVersion = cacheManifest?.latestAvailableVersion ?: "2.5.0"
                        val isUpgradeAvailable = contentVersion != latestVersion || cacheManifest?.hasPendingUpgrade == true

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, if (isUpgradeAvailable) Color(0xFFA855F7) else if (isFullyCached) Color(0xFF22C55E) else Color(0xFFEAB308)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_sync_status")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "OFFLINE SYNC & ROOM CACHE STATUS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = if (isUpgradeAvailable) Color(0xFFC084FC) else if (isFullyCached) Color(0xFF4ADE80) else Color(0xFFFACC15)
                                        )
                                        Text(
                                            text = "Content v$contentVersion • Schema v${cacheManifest?.dbSchemaVersion ?: 6}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Color(0xFF94A3B8)
                                        )
                                    }

                                    Surface(
                                        color = if (isUpgradeAvailable) Color(0xFF581C87).copy(alpha = 0.5f) else if (isFullyCached) Color(0xFF166534).copy(alpha = 0.4f) else Color(0xFF854D0E).copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isUpgradeAvailable) Color(0xFFA855F7) else if (isFullyCached) Color(0xFF22C55E) else Color(0xFFEAB308))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isUpgradeAvailable) Icons.Default.SystemUpdate else if (isFullyCached) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                                                contentDescription = null,
                                                tint = if (isUpgradeAvailable) Color(0xFFE9D5FF) else if (isFullyCached) Color(0xFF4ADE80) else Color(0xFFFACC15),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isUpgradeAvailable) "Upgrade Ready" else if (isFullyCached) "Cached Locally" else "Download Needed",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isUpgradeAvailable) Color(0xFFF3E8FF) else if (isFullyCached) Color(0xFF86EFAC) else Color(0xFFFEF08A)
                                            )
                                        }
                                    }
                                }

                                // Upgrade Available Alert Banner (if upgrade pending)
                                if (isUpgradeAvailable) {
                                    Surface(
                                        color = Color(0xFF3B0764),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFFA855F7))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFA855F7).copy(alpha = 0.3f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SystemUpdate,
                                                    contentDescription = null,
                                                    tint = Color(0xFFE9D5FF),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "New Content Pack v$latestVersion Available",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Includes upgraded 3D CAD mesh detail, refreshed factory service procedures, and updated torque specs.",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = Color(0xFFE9D5FF)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Standard Descriptive Banner
                                    Surface(
                                        color = Color(0xFF0F172A),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isFullyCached) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFFEAB308).copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isFullyCached) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = if (isFullyCached) Color(0xFF4ADE80) else Color(0xFFFACC15),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (isFullyCached) "Upgradable Database & CAD Manual Cache" else "Cache Missing or Incomplete",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = if (isFullyCached)
                                                        "All CAD models and service manual procedures are cached in Room SQLite database v${cacheManifest?.dbSchemaVersion ?: 6}."
                                                    else
                                                        "Some 3D models or service manuals are not yet stored locally. Tap Force Update to cache now.",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = Color(0xFF94A3B8)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Breakdown Rows
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.ViewInAr,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "3D CAD Asset Models",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = if (cached3DCount > 0) "$cached3DCount Cached (v$contentVersion)" else "Not Cached",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (cached3DCount > 0) Color(0xFF38BDF8) else Color(0xFFEF4444)
                                        )
                                    }

                                    HorizontalDivider(color = Color(0xFF1E293B))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Repair Manual Sections",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = if (cachedManualsCount > 0) "$cachedManualsCount Cached (v$contentVersion)" else "Not Cached",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (cachedManualsCount > 0) Color(0xFF38BDF8) else Color(0xFFEF4444)
                                        )
                                    }

                                    HorizontalDivider(color = Color(0xFF1E293B))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Build,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Diagnostic Symptoms Database",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = if (cachedSymptomsCount > 0) "$cachedSymptomsCount Cached (v$contentVersion)" else "Not Cached",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (cachedSymptomsCount > 0) Color(0xFF38BDF8) else Color(0xFFEF4444)
                                        )
                                    }
                                }

                                // Upgrade Main Action Button if update is ready
                                if (isUpgradeAvailable) {
                                    Button(
                                        onClick = {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            onUpgradeContent()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_upgrade_content_pack")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SystemUpdate,
                                            contentDescription = "Upgrade Content",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "UPGRADE TO CONTENT PACK v$latestVersion",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }

                                // Secondary Action Buttons: Check Upgrades, Force Update & Clear Cache
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                HapticHelper.triggerControlTick(context, view, haptic)
                                                onCheckForUpgrades()
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_check_for_upgrades")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SystemUpdateAlt,
                                                contentDescription = "Check for Upgrades",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Check Upgrades",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                HapticHelper.triggerControlTick(context, view, haptic)
                                                onForceUpdate()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_force_update_sync_status")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Force Update",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Force Re-Sync",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            HapticHelper.triggerControlTick(context, view, haptic)
                                            onClearCache()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                        border = BorderStroke(1.dp, Color(0xFF991B1B)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_clear_cache_sync_status")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Clear Cache",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Clear Room Cache",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons: Test Full Live Coaching & Save/Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            HapticHelper.triggerControlTick(context, view, haptic)
                            ttsManager.speakStep(
                                stepNumber = 1,
                                totalSteps = 4,
                                title = "Upper Intake Plenum Removal",
                                instruction = "Loosen the six 10mm bolts securing the plenum to the intake lower runner",
                                warning = "Fuel rail pressure is active. Wear splash goggles.",
                                notes = "10 ft-lbs torque specification"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = settings.activeProfile.toneColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_test_live_coaching")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSpeaking) "Speaking Step..." else "Test Live Speech",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            HapticHelper.triggerControlTick(context, view, haptic)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_save_voice_settings")
                    ) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF94A3B8)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF0F172A)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
