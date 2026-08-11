package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.MentorVoiceSettingsRepository
import com.example.model.MentorVoiceSettings
import com.example.model.VoicePersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class MentorTtsManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val settingsRepo = MentorVoiceSettingsRepository(context.applicationContext)
    var activeSettings: MentorVoiceSettings = settingsRepo.loadSettings()
        private set

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("MentorTtsManager", "Language US is not supported or missing data")
                } else {
                    isInitialized = true
                    applySettings(activeSettings)
                }
            } else {
                Log.e("MentorTtsManager", "TTS Initialization failed with status: $status")
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun applySettings(settings: MentorVoiceSettings) {
        activeSettings = settings
        if (isInitialized && tts != null) {
            tts?.setSpeechRate(settings.speechRate)
            tts?.setPitch(settings.pitch)
        }
    }

    fun reloadSettings() {
        val updated = settingsRepo.loadSettings()
        applySettings(updated)
    }

    fun speakStep(
        stepNumber: Int,
        totalSteps: Int,
        title: String,
        instruction: String,
        warning: String? = null,
        notes: String? = null
    ) {
        if (_isMuted.value) return
        stop()

        reloadSettings()

        val textToRead = buildString {
            append("${activeSettings.activeProfile.title} Coaching Mode. ")
            if (activeSettings.announceWarningsFirst && !warning.isNullOrBlank()) {
                append("CRITICAL SAFETY WARNING: $warning. ")
            }
            append("Step $stepNumber of $totalSteps. ")
            append("$title. ")
            append("$instruction. ")
            if (!activeSettings.announceWarningsFirst && !warning.isNullOrBlank()) {
                append("Warning: $warning. ")
            }
            if (!notes.isNullOrBlank()) {
                if (activeSettings.autoReadTorqueTwice) {
                    append("Torque specification: $notes. Repeat, torque specification: $notes. ")
                } else {
                    append("Torque specification: $notes. ")
                }
            }
            append("Say confirm step when done.")
        }

        speakText(textToRead)
    }

    fun previewProfile(profile: VoicePersonality, rate: Float, pitch: Float) {
        stop()
        if (isInitialized && tts != null) {
            tts?.setSpeechRate(rate)
            tts?.setPitch(pitch)
            speakText("${profile.title}. ${profile.samplePhrase}")
        }
    }

    fun speakText(text: String) {
        if (_isMuted.value) return
        if (isInitialized && tts != null) {
            _isSpeaking.value = true
            val utteranceId = "mentor_utterance_${System.currentTimeMillis()}"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            stop()
        }
    }

    fun stop() {
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("MentorTtsManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
    }
}

