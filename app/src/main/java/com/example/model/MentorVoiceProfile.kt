package com.example.model

import androidx.compose.ui.graphics.Color

enum class VoicePersonality(
    val id: String,
    val title: String,
    val roleTitle: String,
    val tagline: String,
    val defaultSpeechRate: Float,
    val defaultPitch: Float,
    val toneColor: Color,
    val badgeText: String,
    val samplePhrase: String,
    val cadenceDescription: String
) {
    BIG_MIKE(
        id = "big_mike",
        title = "Big Mike",
        roleTitle = "Master Ford Field Technician (40 Yrs)",
        tagline = "Authoritative, calm, direct, and strictly focused on shop safety & precise torque specs.",
        defaultSpeechRate = 0.90f,
        defaultPitch = 0.85f,
        toneColor = Color(0xFFFF6F00),
        badgeText = "MASTER TECH",
        samplePhrase = "Alright son, torque that bolt to 85 foot-pounds in a cross-star pattern. Don't strip it.",
        cadenceDescription = "Paced, steady delivery with mandatory safety check pauses."
    ),
    DR_EVELYN(
        id = "dr_evelyn",
        title = "Dr. Evelyn Vance",
        roleTitle = "Chief Powertrain & OEM Systems Engineer",
        tagline = "Analytical, precise, and articulate. Focuses on tolerances, flow metrics, and OEM assembly guidelines.",
        defaultSpeechRate = 1.05f,
        defaultPitch = 1.15f,
        toneColor = Color(0xFF38BDF8),
        badgeText = "OEM ENGINEER",
        samplePhrase = "Verify thermal seal integrity. Atmospheric intake variance must remain below 1.2 percent.",
        cadenceDescription = "Rapid technical analysis with metric callouts."
    ),
    SGT_REED(
        id = "sgt_reed",
        title = "Sgt. Marcus Reed",
        roleTitle = "Tactical Fleet Maintenance Instructor",
        tagline = "Disciplined, crisp, zero-tolerance safety protocol coach for heavy duty field operations.",
        defaultSpeechRate = 1.00f,
        defaultPitch = 0.92f,
        toneColor = Color(0xFF10B981),
        badgeText = "FLEET INSTRUCTOR",
        samplePhrase = "Attention! Disconnect negative battery terminal immediately before servicing ignition harness.",
        cadenceDescription = "Commanding cadence with repeated warning alerts."
    ),
    ELENA_ROSTOVA(
        id = "elena_rostova",
        title = "Elena Rostova",
        roleTitle = "High-Performance Dyno & Calibration Specialist",
        tagline = "Sharp, dynamic coach specializing in high-output powertrain tuning & real-time load diagnostics.",
        defaultSpeechRate = 1.10f,
        defaultPitch = 1.22f,
        toneColor = Color(0xFFF59E0B),
        badgeText = "DYNO SPECIALIST",
        samplePhrase = "Check fuel trim response at 3,500 RPM. We want optimal air-fuel ratio during load build.",
        cadenceDescription = "Upbeat, performance-oriented diagnostic callouts."
    ),
    SYNTH_CORE_9000(
        id = "synth_core_9000",
        title = "Diagnostic Core 9000",
        roleTitle = "Synthesized Neural Telemetry Engine",
        tagline = "Monotone, ultra-precise robotic audio synthesis engineered for high-decibel shop environments.",
        defaultSpeechRate = 0.95f,
        defaultPitch = 0.70f,
        toneColor = Color(0xFFA855F7),
        badgeText = "NEURAL AI CORE",
        samplePhrase = "Scanning primary CAN-bus sensor packet. Fault code P0301 identified on cylinder one.",
        cadenceDescription = "Synthetic robotic tone optimized for garage background noise."
    )
}

data class MentorVoiceSettings(
    val activeProfile: VoicePersonality = VoicePersonality.BIG_MIKE,
    val speechRate: Float = VoicePersonality.BIG_MIKE.defaultSpeechRate,
    val pitch: Float = VoicePersonality.BIG_MIKE.defaultPitch,
    val announceWarningsFirst: Boolean = true,
    val autoReadTorqueTwice: Boolean = true,
    val handsFreeConfirmSensitivity: Float = 0.8f,
    val audioDuckingEnabled: Boolean = true,
    val hapticPulseOnSpeech: Boolean = true
)
