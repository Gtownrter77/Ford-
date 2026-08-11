package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_tasks")
data class UpcomingTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleItemId: String,
    val title: String,
    val systemName: String,
    val targetMileage: Int,
    val dueDateMillis: Long,
    val intervalMiles: Int = 5000,
    val fluidSpecOrPart: String = "",
    val priorityLevel: String = "NORMAL", // "CRITICAL", "HIGH", "NORMAL"
    val estimatedCostUsd: Double = 0.0,
    val isCompleted: Boolean = false,
    val notes: String = ""
)
