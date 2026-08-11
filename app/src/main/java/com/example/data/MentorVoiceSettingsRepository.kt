package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MentorVoiceSettings
import com.example.model.VoicePersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MentorVoiceSettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("mentor_voice_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<MentorVoiceSettings> = _settingsFlow.asStateFlow()

    fun loadSettings(): MentorVoiceSettings {
        val profileId = prefs.getString(KEY_PROFILE, VoicePersonality.BIG_MIKE.id) ?: VoicePersonality.BIG_MIKE.id
        val profile = VoicePersonality.entries.find { it.id == profileId } ?: VoicePersonality.BIG_MIKE

        val speechRate = prefs.getFloat(KEY_SPEECH_RATE, profile.defaultSpeechRate)
        val pitch = prefs.getFloat(KEY_PITCH, profile.defaultPitch)
        val warningsFirst = prefs.getBoolean(KEY_WARNINGS_FIRST, true)
        val torqueTwice = prefs.getBoolean(KEY_TORQUE_TWICE, true)
        val sensitivity = prefs.getFloat(KEY_SENSITIVITY, 0.8f)
        val ducking = prefs.getBoolean(KEY_DUCKING, true)
        val haptics = prefs.getBoolean(KEY_HAPTICS, true)

        return MentorVoiceSettings(
            activeProfile = profile,
            speechRate = speechRate,
            pitch = pitch,
            announceWarningsFirst = warningsFirst,
            autoReadTorqueTwice = torqueTwice,
            handsFreeConfirmSensitivity = sensitivity,
            audioDuckingEnabled = ducking,
            hapticPulseOnSpeech = haptics
        )
    }

    fun saveSettings(settings: MentorVoiceSettings) {
        prefs.edit()
            .putString(KEY_PROFILE, settings.activeProfile.id)
            .putFloat(KEY_SPEECH_RATE, settings.speechRate)
            .putFloat(KEY_PITCH, settings.pitch)
            .putBoolean(KEY_WARNINGS_FIRST, settings.announceWarningsFirst)
            .putBoolean(KEY_TORQUE_TWICE, settings.autoReadTorqueTwice)
            .putFloat(KEY_SENSITIVITY, settings.handsFreeConfirmSensitivity)
            .putBoolean(KEY_DUCKING, settings.audioDuckingEnabled)
            .putBoolean(KEY_HAPTICS, settings.hapticPulseOnSpeech)
            .apply()

        _settingsFlow.value = settings
    }

    fun selectProfile(profile: VoicePersonality) {
        val current = _settingsFlow.value
        val updated = current.copy(
            activeProfile = profile,
            speechRate = profile.defaultSpeechRate,
            pitch = profile.defaultPitch
        )
        saveSettings(updated)
    }

    companion object {
        private const val KEY_PROFILE = "active_profile_id"
        private const val KEY_SPEECH_RATE = "speech_rate"
        private const val KEY_PITCH = "pitch"
        private const val KEY_WARNINGS_FIRST = "announce_warnings_first"
        private const val KEY_TORQUE_TWICE = "auto_read_torque_twice"
        private const val KEY_SENSITIVITY = "hands_free_sensitivity"
        private const val KEY_DUCKING = "audio_ducking"
        private const val KEY_HAPTICS = "haptic_pulse"
    }
}
