package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acoustic_reference_sounds")
data class AcousticReferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val soundProfileId: String,
    val title: String,
    val matchCategory: String,
    val frequencyMinHz: Int,
    val frequencyMaxHz: Int,
    val spectralPatternSignature: String,
    val matchConfidencePercent: Int,
    val soundCharacteristics: String,
    val rootCause: String,
    val recommendedFix: String,
    val targetComponentId: String,
    val colorHex: String,
    val audioSampleUrlOrResource: String,
    val verifiedDatabaseCount: Int = 245000
)
