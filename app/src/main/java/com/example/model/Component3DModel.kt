package com.example.model

data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float
)

data class Face3D(
    val vertexIndices: List<Int>,
    val colorHex: String? = null
)

data class TorqueSpec(
    val fastenerName: String,
    val torqueFtLbs: String,
    val torqueNm: String,
    val notes: String = ""
)

data class RepairStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val warning: String? = null,
    val tip: String? = null
)

data class Component3DModel(
    val id: String,
    val name: String,
    val system: VehicleSystem,
    val oemPartNumber: String,
    val description: String,
    val locationDescription: String,
    val difficulty: String, // e.g. "Beginner", "Intermediate", "Advanced"
    val estimatedTimeMinutes: Int,
    val vertices: List<Point3D>,
    val faces: List<Face3D>,
    val centerOffset: Point3D, // Center position in 3D space relative to engine origin
    val explodeVector: Point3D, // Vector along which component moves during exploded view
    val torqueSpecs: List<TorqueSpec>,
    val requiredTools: List<String>,
    val repairSteps: List<RepairStep>,
    val commonSymptoms: List<String>,
    val replacementIntervalMiles: Int? = null
)
