package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity storing component failure risk statistics and diagnostic heatmap metrics
 * for the 2004 Ford Explorer Sport Trac 4.0L SOHC V6.
 *
 * Combines historical Cologne 4.0L TSB failure frequencies with live Room database
 * maintenance logs and upcoming service task schedules.
 */
@Entity(tableName = "component_failure_risks")
data class ComponentFailureRiskEntity(
    @PrimaryKey val componentId: String,
    val componentName: String,
    val systemName: String,
    val baseProbability: Float, // 0.0f .. 1.0f base historical failure probability
    val dynamicRiskScore: Float, // 0.0f .. 1.0f weighted dynamically by Room DB records
    val riskSeverity: String, // "CRITICAL", "HIGH", "MODERATE", "LOW", "OPTIMAL"
    val primaryFailureMode: String,
    val tsbReference: String,
    val commonSymptoms: String,
    val mileageInterval: Int,
    val lastServiceMileage: Int? = null,
    val isOverdue: Boolean = false,
    val roomDataSourceSummary: String = "",
    val recommendedAction: String = "",
    val reportedIncidentsCount: Int = 0,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
