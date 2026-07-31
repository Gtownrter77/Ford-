package com.example.model

import androidx.compose.ui.graphics.Color

enum class VehicleSystem(
    val displayName: String,
    val color: Color,
    val hexColor: String,
    val description: String,
    val iconName: String
) {
    ALL(
        displayName = "All Systems",
        color = Color(0xFF64748B),
        hexColor = "#64748B",
        description = "Complete 2004 Ford Explorer Sport Trac Vehicle Assembly",
        iconName = "DirectionsCar"
    ),
    ENGINE(
        displayName = "Engine (4.0L SOHC)",
        color = Color(0xFFFF6F00),
        hexColor = "#FF6F00",
        description = "Cologne 4.0L SOHC V6 Engine Block, Heads & Valve Train",
        iconName = "Engine"
    ),
    AIR_INTAKE(
        displayName = "Air Intake System",
        color = Color(0xFF0284C7),
        hexColor = "#0284C7",
        description = "Air Filter Box, MAF Sensor, Throttle Body & Intake Manifold",
        iconName = "Air"
    ),
    TRANSMISSION(
        displayName = "Transmission",
        color = Color(0xFF9333EA),
        hexColor = "#9333EA",
        description = "5R55E 5-Speed Automatic Transmission & Torque Converter",
        iconName = "Settings"
    ),
    COOLING(
        displayName = "Cooling System",
        color = Color(0xFF06B6D4),
        hexColor = "#06B6D4",
        description = "Radiator, Thermostat Housing, Water Pump & Fan Clutch",
        iconName = "WaterDrop"
    ),
    AIR_CONDITIONING(
        displayName = "Air Conditioning",
        color = Color(0xFF10B981),
        hexColor = "#10B981",
        description = "A/C Compressor, Condenser, Accumulator & Evaporator Core",
        iconName = "AcUnit"
    ),
    ELECTRICAL(
        displayName = "Electrical & Ignition",
        color = Color(0xFFEAB308),
        hexColor = "#EAB308",
        description = "Alternator, Battery, Spark Plugs, Ignition Coil & Wiring",
        iconName = "Bolt"
    ),
    BRAKES_CHASSIS(
        displayName = "Brakes & Suspension",
        color = Color(0xFFEF4444),
        hexColor = "#EF4444",
        description = "Front Torsion Bars, Rear Leaf Springs, Calipers & Rotors",
        iconName = "Build"
    )
}
