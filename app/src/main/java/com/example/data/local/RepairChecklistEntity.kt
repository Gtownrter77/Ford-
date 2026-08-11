package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repair_checklists")
data class RepairChecklistEntity(
    @PrimaryKey val componentId: String,
    val componentName: String,
    val currentStepIndex: Int,
    val completedStepsCsv: String,
    val totalSteps: Int,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
