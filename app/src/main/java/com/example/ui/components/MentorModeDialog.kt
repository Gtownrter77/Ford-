package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.model.Component3DModel
import com.example.model.RepairStep
import com.example.model.TorqueSpec
import com.example.util.HapticHelper
import com.example.util.MentorTtsManager
import com.example.util.VoiceCommandManager
import com.example.util.VoiceState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorModeDialog(
    component: Component3DModel,
    onDismiss: () -> Unit,
    voiceCommandManager: VoiceCommandManager? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val ttsManager = remember { MentorTtsManager(context) }
    val vcm = remember { voiceCommandManager ?: VoiceCommandManager(context) }

    var currentStepIndex by remember { mutableStateOf(0) }
    val completedStepIndices = remember { mutableStateListOf<Int>() }
    var voiceFeedbackNotice by remember { mutableStateOf<String?>(null) }
    var hasResumedFromRoom by remember { mutableStateOf(false) }
    var isVoiceListeningEnabled by remember { mutableStateOf(false) }
    var showVoiceSettingsDialog by remember { mutableStateOf(false) }

    if (showVoiceSettingsDialog) {
        MentorVoiceSettingsDialog(
            onDismiss = {
                showVoiceSettingsDialog = false
                ttsManager.reloadSettings()
            }
        )
    }

    val db = remember { com.example.data.local.AppDatabase.getDatabase(context) }
    val checklistDao = remember { db.repairChecklistDao() }
    var isLoadedFromDb by remember { mutableStateOf(false) }

    val steps = component.repairSteps
    val currentStep = steps.getOrNull(currentStepIndex)

    // Load saved checklist progress from Room Database on startup
    LaunchedEffect(component.id) {
        val savedProgress = checklistDao.getChecklistForComponentDirect(component.id)
        if (savedProgress != null) {
            currentStepIndex = savedProgress.currentStepIndex.coerceIn(0, (steps.size - 1).coerceAtLeast(0))
            completedStepIndices.clear()
            if (savedProgress.completedStepsCsv.isNotBlank()) {
                val parsed = savedProgress.completedStepsCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
                completedStepIndices.addAll(parsed)
            }
            hasResumedFromRoom = true
            voiceFeedbackNotice = "Resumed progress from saved repair session (Step ${currentStepIndex + 1} of ${steps.size})"
        }
        isLoadedFromDb = true
    }

    // Automatically persist updated repair checklist state to Room Database
    LaunchedEffect(currentStepIndex, completedStepIndices.toList()) {
        if (isLoadedFromDb && steps.isNotEmpty()) {
            val completedCsv = completedStepIndices.sorted().joinToString(",")
            val isDone = completedStepIndices.size >= steps.size
            checklistDao.saveChecklist(
                com.example.data.local.RepairChecklistEntity(
                    componentId = component.id,
                    componentName = component.name,
                    currentStepIndex = currentStepIndex,
                    completedStepsCsv = completedCsv,
                    totalSteps = steps.size,
                    lastUpdated = System.currentTimeMillis(),
                    isCompleted = isDone
                )
            )
        }
    }

    val isSpeaking by ttsManager.isSpeaking.collectAsState()
    val isMuted by ttsManager.isMuted.collectAsState()

    val voiceState by vcm.voiceState.collectAsState()

    // Automatic TTS read out when currentStep changes
    LaunchedEffect(currentStepIndex, isMuted) {
        if (!isMuted && currentStep != null) {
            val matchingTorque = component.torqueSpecs.firstOrNull {
                it.fastenerName.contains(currentStep.title, ignoreCase = true)
            }?.notes
            ttsManager.speakStep(
                stepNumber = currentStep.stepNumber,
                totalSteps = steps.size,
                title = currentStep.title,
                instruction = currentStep.instruction,
                warning = currentStep.warning,
                notes = matchingTorque
            )
        }
    }

    // Process spoken voice commands for hands-free repair work
    LaunchedEffect(voiceState) {
        if (voiceState is VoiceState.Processing) {
            val spokenText = (voiceState as VoiceState.Processing).text.lowercase()
            if (spokenText.isNotBlank()) {
                val actionResult = handleVoiceCommand(
                    spokenText = spokenText,
                    currentStepIndex = currentStepIndex,
                    totalSteps = steps.size,
                    currentStep = currentStep,
                    torqueSpecs = component.torqueSpecs,
                    onAdvance = { newIndex ->
                        if (!completedStepIndices.contains(currentStepIndex)) {
                            completedStepIndices.add(currentStepIndex)
                        }
                        currentStepIndex = newIndex
                    },
                    onPrevious = { prevIndex ->
                        currentStepIndex = prevIndex
                    },
                    onRepeat = {
                        if (currentStep != null) {
                            ttsManager.speakStep(
                                stepNumber = currentStep.stepNumber,
                                totalSteps = steps.size,
                                title = currentStep.title,
                                instruction = currentStep.instruction,
                                warning = currentStep.warning
                            )
                        }
                    },
                    onReadTorque = {
                        val torqueText = if (component.torqueSpecs.isNotEmpty()) {
                            component.torqueSpecs.joinToString(". ") {
                                "${it.fastenerName}: ${it.torqueFtLbs} foot pounds, ${it.torqueNm} Newton meters"
                            }
                        } else {
                            "No specific torque values declared for this step. Use standard OEM hand tightness."
                        }
                        ttsManager.speakText("Torque Specifications: $torqueText")
                    },
                    onToggleMute = {
                        ttsManager.toggleMute()
                    }
                )

                voiceFeedbackNotice = actionResult
                vcm.setCommandExecuted(spokenText, actionResult)
            }
        }
    }

    // Voice listening is opt-in so opening Mentor does not activate the microphone silently.
    LaunchedEffect(isVoiceListeningEnabled) {
        if (isVoiceListeningEnabled) {
            vcm.startListening()
        } else {
            vcm.stopListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            vcm.stopListening()
            ttsManager.shutdown()
            vcm.destroy()
        }
    }

    // Pulse animation for active voice speech indicator
    val infiniteTransition = rememberInfiniteTransition(label = "tts_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tts_scale"
    )

    AlertDialog(
        onDismissRequest = {
            ttsManager.stop()
            onDismiss()
        },
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("mentor_mode_dialog"),
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF10B981),
                            shape = CircleShape
                        ) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MENTOR MODE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF064E3B),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Text(
                                        text = "HANDS-FREE TTS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${component.name} • OEM Manual Procedure",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showVoiceSettingsDialog = true }) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Mentor Voice Settings",
                                tint = Color(0xFF38BDF8)
                            )
                        }
                        IconButton(onClick = { ttsManager.toggleMute() }) {
                            Icon(
                                if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Toggle Audio Voice",
                                tint = if (isMuted) Color(0xFFEF4444) else Color(0xFFFFD700)
                            )
                        }
                        IconButton(onClick = {
                            isVoiceListeningEnabled = false
                            ttsManager.stop()
                            onDismiss()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                        }
                    }
                }

                // Voice status bar
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSpeaking) Color(0xFFFFD700) else Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) Color(0xFFFFD700) else Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isMuted && !isVoiceListeningEnabled -> "TTS muted • Microphone off"
                                    isMuted -> "TTS muted • Microphone listening"
                                    isSpeaking && isVoiceListeningEnabled -> "Speaking step out loud... Listening enabled"
                                    isVoiceListeningEnabled -> "Microphone listening • Say 'Confirm Step', 'Next', or 'Repeat'"
                                    else -> "Microphone off • Tap the mic to enable voice commands"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSpeaking) Color(0xFFFFD700) else Color(0xFFE2E8F0)
                            )
                        }

                        IconButton(
                            onClick = { isVoiceListeningEnabled = !isVoiceListeningEnabled },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("mentor_voice_toggle_btn")
                        ) {
                            Icon(
                                if (isVoiceListeningEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = if (isVoiceListeningEnabled) "Stop listening" else "Start listening",
                                tint = if (isVoiceListeningEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
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
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Voice Recognition Feedback Notification Toast
                if (!voiceFeedbackNotice.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFF0284C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = voiceFeedbackNotice!!,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                // Overall Progress Indicator & Room Persistence Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PROGRESS: STEP ${currentStepIndex + 1} OF ${steps.size} (AUTO-SAVED)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF94A3B8)
                            )
                        }
                        
                        TextButton(
                            onClick = {
                                currentStepIndex = 0
                                completedStepIndices.clear()
                                coroutineScope.launch {
                                    checklistDao.deleteChecklist(component.id)
                                }
                                voiceFeedbackNotice = "Checklist progress reset to Step 1"
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp).testTag("mentor_reset_progress_btn")
                        ) {
                            Text(
                                text = "Reset Progress",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentStepIndex + 1).toFloat() / steps.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF334155),
                    )
                }

                // Current Active Step Card (Prominent display for hands-free vehicle work)
                if (currentStep != null) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF10B981),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "CURRENT STEP #${currentStep.stepNumber}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            ttsManager.speakStep(
                                                stepNumber = currentStep.stepNumber,
                                                totalSteps = steps.size,
                                                title = currentStep.title,
                                                instruction = currentStep.instruction,
                                                warning = currentStep.warning
                                            )
                                        },
                                        modifier = Modifier.testTag("mentor_repeat_tts_btn")
                                    ) {
                                        Icon(
                                            Icons.Default.Campaign,
                                            contentDescription = "Read Step Aloud",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentStep.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentStep.instruction,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                                color = Color(0xFFE2E8F0)
                            )

                            if (!currentStep.warning.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = Color(0xFF7F1D1D),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = currentStep.warning,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color(0xFFFECACA)
                                        )
                                    }
                                }
                            }

                            if (!currentStep.tip.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFF064E3B),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = currentStep.tip,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFA7F3D0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Voice Command Hints Cheat Card
                Surface(
                    color = Color(0xFF0B132B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SPOKEN COMMANDS ACCEPTED:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                color = Color(0xFFFFD700)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• \"Confirm Step\" / \"Next\"", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                            Text("• \"Repeat Step\"", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• \"Previous Step\" / \"Back\"", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                            Text("• \"Read Torque Specs\"", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Large Primary Confirm Step Button for Hands-Free or Quick Touch
                Button(
                    onClick = {
                        HapticHelper.vibrateSuccess(context)
                        if (!completedStepIndices.contains(currentStepIndex)) {
                            completedStepIndices.add(currentStepIndex)
                        }
                        if (currentStepIndex < steps.size - 1) {
                            currentStepIndex++
                        } else {
                            ttsManager.speakText("Congratulations! All repair steps completed for ${component.name}.")
                            voiceFeedbackNotice = "All Repair Steps Completed!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("mentor_confirm_step_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentStepIndex < steps.size - 1) "Confirm Step & Read Next (Voice: \"Confirm\")" else "Finish Repair Procedure",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentStepIndex > 0) {
                                currentStepIndex--
                            }
                        },
                        enabled = currentStepIndex > 0,
                        modifier = Modifier.testTag("mentor_prev_step_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    OutlinedButton(
                        onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                            }
                        },
                        enabled = currentStepIndex < steps.size - 1,
                        modifier = Modifier.testTag("mentor_next_step_btn")
                    ) {
                        Text("Skip Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        dismissButton = {}
    )
}

private fun handleVoiceCommand(
    spokenText: String,
    currentStepIndex: Int,
    totalSteps: Int,
    currentStep: RepairStep?,
    torqueSpecs: List<TorqueSpec>,
    onAdvance: (Int) -> Unit,
    onPrevious: (Int) -> Unit,
    onRepeat: () -> Unit,
    onReadTorque: () -> Unit,
    onToggleMute: () -> Unit
): String {
    return when {
        spokenText.contains("confirm") || spokenText.contains("done") || spokenText.contains("next") || spokenText.contains("check") || spokenText.contains("finished") || spokenText.contains("completed") -> {
            if (currentStepIndex < totalSteps - 1) {
                onAdvance(currentStepIndex + 1)
                "Voice Command: Step Confirmed! Advancing to Step ${currentStepIndex + 2}."
            } else {
                "Voice Command: Final Step Confirmed! Procedure Complete."
            }
        }
        spokenText.contains("previous") || spokenText.contains("back") || spokenText.contains("last") -> {
            if (currentStepIndex > 0) {
                onPrevious(currentStepIndex - 1)
                "Voice Command: Returning to Step ${currentStepIndex}."
            } else {
                "Voice Command: Already at Step 1."
            }
        }
        spokenText.contains("repeat") || spokenText.contains("read") || spokenText.contains("again") || spokenText.contains("say") -> {
            onRepeat()
            "Voice Command: Re-reading Step ${currentStepIndex + 1}."
        }
        spokenText.contains("torque") || spokenText.contains("spec") || spokenText.contains("tight") -> {
            onReadTorque()
            "Voice Command: Reading Torque Specifications out loud."
        }
        spokenText.contains("mute") || spokenText.contains("quiet") || spokenText.contains("stop") || spokenText.contains("pause") -> {
            onToggleMute()
            "Voice Command: Voice Muted."
        }
        else -> {
            "Voice Heard: \"$spokenText\" (Try saying 'Confirm Step' or 'Repeat')"
        }
    }
}
