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

enum class FastenerCategory(val label: String) {
    BOLT("Bolts & Studs"),
    SCREW("Screws & Fasteners"),
    WASHER_SEAL("Washers, Gaskets & Seals"),
    WIRING_HARNESS("Wiring Harness & Connectors")
}

enum class SubAssemblyType(val displayName: String, val defaultHexColor: String) {
    MAIN_BODY("Component Housing", "#0284C7"),
    BOLT("OEM Hex Bolt", "#CBD5E1"),
    SCREW("Torx / Fastener Screw", "#94A3B8"),
    WASHER("Belleville / Lock Washer", "#E2E8F0"),
    GASKET("Silicone / Flange Gasket", "#38BDF8"),
    BELT("Ribbed Serpentine Belt", "#1E293B"),
    SEAL_O_RING("Rubber O-Ring / Seal", "#F97316"),
    SPARK_PLUG("Spark Plug Assembly", "#E0E7FF")
}

data class SubAssemblyPart(
    val id: String,
    val name: String,
    val type: SubAssemblyType,
    val vertices: List<Point3D>,
    val faces: List<Face3D>,
    val localOffset: Point3D = Point3D(0f, 0f, 0f), // Relative offset in component local space
    val explodeDirection: Point3D = Point3D(0f, 1f, 0f), // Multi-tier separation direction
    val explodeDistanceMultiplier: Float = 1.0f, // Distance multiplier when exploding
    val specDetails: String = "", // Thread spec, torque rating, or material description
    val metallicFactor: Float = 0.85f, // PBR Metallic property (0.0 = dielectric, 1.0 = metal)
    val roughnessFactor: Float = 0.25f // PBR Roughness property (0.0 = mirror, 1.0 = matte)
)

data class FastenerInventoryItem(
    val name: String,
    val category: FastenerCategory,
    val quantity: Int,
    val specOrThread: String,
    val toolRequired: String,
    val notes: String = ""
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
    val replacementIntervalMiles: Int? = null,
    val fasteners: List<FastenerInventoryItem> = emptyList(),
    val subAssemblies: List<SubAssemblyPart> = emptyList(),
    val metallicFactor: Float = 0.85f, // PBR Metallic property
    val roughnessFactor: Float = 0.30f, // PBR Roughness property
    val serialNumber: String = "SN-2004-ST-" + oemPartNumber.replace("-", ""),
    val manualSectionRef: String = "Section 303-01A: " + name + " Service"
)
