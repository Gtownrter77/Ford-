package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_logs")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleItemId: String,
    val title: String,
    val systemName: String,
    val mileageAtService: Int,
    val dateLoggedMillis: Long = System.currentTimeMillis(),
    val componentDescription: String = "",
    val costUsd: Double = 0.0,
    val notes: String = "",
    val isCompleted: Boolean = true
)

@Entity(tableName = "vehicle_profile")
data class VehicleProfileEntity(
    @PrimaryKey val id: Int = 1,
    val currentMileage: Int = 115000,
    val vinNumber: String = "1FMZU72E44U******",
    val modelYear: Int = 2004,
    val trimName: String = "Sport Trac XLT 4.0L SOHC V6",
    val transmissionType: String = "5R55E 5-Speed Automatic"
)

