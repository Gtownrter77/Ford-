package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_3d_assets")
data class Cached3DAssetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val systemName: String,
    val oemPartNumber: String,
    val description: String,
    val locationDescription: String,
    val difficulty: String,
    val estimatedTimeMinutes: Int,
    val verticesCount: Int,
    val facesCount: Int,
    val subAssembliesCount: Int,
    val serialNumber: String,
    val manualSectionRef: String,
    val repairStepsJson: String,
    val torqueSpecsJson: String,
    val requiredToolsCsv: String,
    val commonSymptomsCsv: String,
    val fastenersJson: String,
    val assetVersion: String = "2.4.0",
    val lastCachedMillis: Long = System.currentTimeMillis(),
    val isOfflineAvailable: Boolean = true
)
