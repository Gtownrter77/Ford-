package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_repair_manuals")
data class CachedRepairManualEntity(
    @PrimaryKey val id: String,
    val title: String,
    val serviceManualSection: String,
    val tsbNumber: String?,
    val problemSummary: String,
    val probableCause: String,
    val obdCodesCsv: String,
    val targetComponentId: String,
    val urgencyLevel: String,
    val matchingSymptomIdsCsv: String,
    val diagnosticStepsJson: String,
    val difficulty: String,
    val assetVersion: String = "2.4.0",
    val lastCachedMillis: Long = System.currentTimeMillis(),
    val isOfflineAvailable: Boolean = true
)

@Entity(tableName = "cached_symptoms")
data class CachedSymptomEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val systemName: String,
    val severity: String,
    val assetVersion: String = "2.4.0",
    val lastCachedMillis: Long = System.currentTimeMillis()
)
