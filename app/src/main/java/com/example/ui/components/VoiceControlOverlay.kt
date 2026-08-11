package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.util.VoiceCommandManager
import com.example.util.VoiceState

@Composable
fun VoiceControlOverlay(
    voiceCommandManager: VoiceCommandManager,
    voiceNotice: String?,
    onExecuteCommand: (String) -> String,
    onDismissNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showCheatSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            voiceCommandManager.startListening()
        }
    }

    val voiceState by voiceCommandManager.voiceState.collectAsState()
    val lastRecognizedText by voiceCommandManager.lastRecognizedText.collectAsState()

    // Process recognized voice speech whenever VoiceState transitions to Processing
    LaunchedEffect(voiceState) {
        if (voiceState is VoiceState.Processing) {
            val text = (voiceState as VoiceState.Processing).text
            if (text.isNotBlank()) {
                val feedback = onExecuteCommand(text)
                voiceCommandManager.setCommandExecuted(text, feedback)
            }
        }
    }

    // Infinite pulse transition for mic listening ring
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    if (showCheatSheet) {
        VoiceCheatSheetDialog(
            onDismiss = { showCheatSheet = false },
            onSelectSampleCommand = { cmd ->
                showCheatSheet = false
                onExecuteCommand(cmd)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("voice_control_overlay")
    ) {
        // Voice Feedback Bar Notice (Only shown when active or feedback present)
        AnimatedVisibility(
            visible = voiceNotice != null || voiceState is VoiceState.Listening || voiceState is VoiceState.Error || voiceState is VoiceState.Processing,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Surface(
                color = when (voiceState) {
                    is VoiceState.Listening -> Color(0xFF0F2D4A)
                    is VoiceState.Error -> Color(0xFF451A1A)
                    else -> Color(0xFF1E293B)
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    when (voiceState) {
                        is VoiceState.Listening -> Color(0xFF38BDF8)
                        is VoiceState.Error -> Color(0xFFEF4444)
                        else -> Color(0xFF10B981)
                    }
                ),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = when (voiceState) {
                                is VoiceState.Listening -> Icons.Default.GraphicEq
                                is VoiceState.Error -> Icons.Default.Warning
                                else -> Icons.Default.RecordVoiceOver
                            },
                            contentDescription = null,
                            tint = when (voiceState) {
                                is VoiceState.Listening -> Color(0xFF38BDF8)
                                is VoiceState.Error -> Color(0xFFEF4444)
                                else -> Color(0xFF10B981)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (voiceState) {
                                is VoiceState.Listening -> "Listening... \"$lastRecognizedText\""
                                is VoiceState.Error -> (voiceState as VoiceState.Error).message
                                else -> voiceNotice ?: "Voice Command Processed"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            voiceCommandManager.resetState()
                            onDismissNotice()
                        },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceCheatSheetDialog(
    onDismiss: () -> Unit,
    onSelectSampleCommand: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0B132B),
        titleContentColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Hands-Free Voice Commands",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                Text(
                    text = "Speak these commands while working under the hood without touching your phone:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(10.dp))

                val categories = listOf(
                    "Mentor Mode TTS" to listOf("Confirm Step", "Repeat Step", "Previous Step", "Read Torque Specs", "Mute Voice"),
                    "3D Systems" to listOf("Engine", "Intake", "Brakes", "Transmission", "Cooling", "Electrical", "Show All"),
                    "Part Selection" to listOf("PCV Valve", "Intake Manifold", "Coil Pack", "Thermostat", "MAF Sensor", "EGR Valve"),
                    "3D Navigation" to listOf("Next Part", "Previous Part", "Open Detail Sheet"),
                    "Screen Tabs" to listOf("3D Model", "Repair Manual", "Diagnostics", "Maintenance Schedule")
                )

                categories.forEach { (catTitle, cmds) ->
                    Text(
                        text = catTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cmds.take(3).forEach { cmd ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectSampleCommand(cmd) },
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF0284C7))
                            ) {
                                Text(
                                    text = "\"$cmd\"",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Got It")
            }
        }
    )
}
