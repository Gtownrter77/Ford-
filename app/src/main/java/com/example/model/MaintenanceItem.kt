package com.example.model

data class MaintenanceScheduleItem(
    val id: String,
    val title: String,
    val system: VehicleSystem,
    val intervalMiles: Int,
    val intervalMonths: Int,
    val fluidTypeOrSpec: String,
    val description: String,
    val targetComponentId: String? = null
)
