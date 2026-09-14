package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * The visible one-on-one Mentor identity. All characters share the same Mentor
 * knowledge and safety brain; only their presentation and coaching style differ.
 */
enum class MentorCharacter(
    val id: String,
    val title: String,
    val roleTitle: String,
    val tagline: String,
    val accentColor: Color,
    val badge: String,
    val greeting: String
) {
    MASTER_MECHANIC(
        id = "master_mechanic",
        title = "The Master Mechanic",
        roleTitle = "Experienced & Rugged",
        tagline = "Real problems. Real solutions. Calm under pressure with dry humor.",
        accentColor = Color(0xFF38BDF8),
        badge = "MASTER TECH",
        greeting = "Let's get your ride back on the road. I've seen worse."
    ),
    PRECISION_ENGINEER(
        id = "precision_engineer",
        title = "The Precision Engineer",
        roleTitle = "Sharp & Focused",
        tagline = "Details make the difference. Calm, caring, and relentlessly precise.",
        accentColor = Color(0xFFA855F7),
        badge = "SYSTEMS ENGINEER",
        greeting = "Small details. Big difference. Let's take a closer look."
    ),
    GEARHEAD(
        id = "gearhead",
        title = "The Gearhead",
        roleTitle = "Energetic & Enthusiastic",
        tagline = "Cars, tech, solutions. Playful, encouraging, and great for beginners.",
        accentColor = Color(0xFF22C55E),
        badge = "GEARHEAD",
        greeting = "You got this. Let's make this repair feel easier."
    )
}
