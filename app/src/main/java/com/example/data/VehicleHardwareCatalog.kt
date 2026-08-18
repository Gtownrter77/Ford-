package com.example.data

import com.example.model.Component3DModel
import com.example.model.FastenerCategory
import com.example.model.FastenerInventoryItem
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.VehicleSystem
import com.example.util.SubAssemblyMeshGenerator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creates a consistent, individually addressable service-hardware layer for
 * the reference model. This catalogue is intentionally labelled as
 * service-scope visual data: exact production hardware, finish, and quantity
 * must be verified against the VIN-specific Ford parts catalogue and workshop
 * manual before it is used as a repair or restoration authority.
 */
object VehicleHardwareCatalog {

    private data class HardwareProfile(
        val boltCount: Int,
        val screwCount: Int,
        val washerCount: Int,
        val includeSeal: Boolean,
        val boltThread: String,
        val boltTool: String,
        val boltName: String,
        val screwName: String = "Torx trim / cover screw",
        val boltRadius: Float = 0.08f,
        val boltLength: Float = 0.30f
    )

    fun enrich(component: Component3DModel): Component3DModel {
        val profile = profileFor(component.system)
        val addedParts = buildRenderableHardware(component, profile)
        return component.copy(
            fasteners = buildInventory(component, profile),
            subAssemblies = component.subAssemblies + addedParts
        )
    }

    private fun profileFor(system: VehicleSystem): HardwareProfile = when (system) {
        VehicleSystem.ENGINE -> HardwareProfile(
            boltCount = 8,
            screwCount = 0,
            washerCount = 4,
            includeSeal = true,
            boltThread = "M8 × 1.25 service fastener (reference geometry)",
            boltTool = "13 mm socket / calibrated torque wrench",
            boltName = "Engine / accessory flange bolt set",
            boltRadius = 0.095f,
            boltLength = 0.38f
        )
        VehicleSystem.TRANSMISSION, VehicleSystem.DRIVETRAIN_4WD -> HardwareProfile(
            boltCount = 8,
            screwCount = 0,
            washerCount = 4,
            includeSeal = true,
            boltThread = "M10 × 1.50 drivetrain fastener (reference geometry)",
            boltTool = "15 mm socket / calibrated torque wrench",
            boltName = "Drivetrain mounting flange bolt set",
            boltRadius = 0.105f,
            boltLength = 0.42f
        )
        VehicleSystem.BRAKES_CHASSIS -> HardwareProfile(
            boltCount = 6,
            screwCount = 0,
            washerCount = 4,
            includeSeal = false,
            boltThread = "M10 chassis fastener (reference geometry)",
            boltTool = "15 mm socket / breaker bar / torque wrench",
            boltName = "Chassis, brake, or suspension mounting bolt set",
            boltRadius = 0.10f,
            boltLength = 0.40f
        )
        VehicleSystem.COOLING, VehicleSystem.AIR_CONDITIONING, VehicleSystem.AIR_INTAKE -> HardwareProfile(
            boltCount = 4,
            screwCount = 2,
            washerCount = 4,
            includeSeal = true,
            boltThread = "M6 × 1.00 service fastener (reference geometry)",
            boltTool = "8 mm / 10 mm socket and inch-pound torque wrench",
            boltName = "Accessory / housing flange bolt set",
            boltRadius = 0.075f,
            boltLength = 0.28f
        )
        VehicleSystem.ELECTRICAL -> HardwareProfile(
            boltCount = 3,
            screwCount = 4,
            washerCount = 2,
            includeSeal = false,
            boltThread = "M6 electrical bracket fastener (reference geometry)",
            boltTool = "7 mm / 8 mm socket and Torx driver",
            boltName = "Electrical module bracket bolt set",
            boltRadius = 0.07f,
            boltLength = 0.24f
        )
        VehicleSystem.INTERIOR_DASH, VehicleSystem.LIGHTING_BODY, VehicleSystem.SUNROOF_ROOF -> HardwareProfile(
            boltCount = 2,
            screwCount = 6,
            washerCount = 2,
            includeSeal = false,
            boltThread = "M6 trim bracket fastener (reference geometry)",
            boltTool = "7 mm socket / T20 Torx driver",
            boltName = "Body or trim mounting bolt set",
            boltRadius = 0.065f,
            boltLength = 0.20f
        )
        VehicleSystem.ALL -> HardwareProfile(
            boltCount = 4,
            screwCount = 2,
            washerCount = 2,
            includeSeal = true,
            boltThread = "Reference fastener profile",
            boltTool = "Vehicle-specific tool selection required",
            boltName = "Reference assembly fastener set"
        )
    }

    private fun buildInventory(
        component: Component3DModel,
        profile: HardwareProfile
    ): List<FastenerInventoryItem> {
        val torque = component.torqueSpecs.firstOrNull()?.let { "Torque reference: ${it.torqueFtLbs} ft-lb (${it.torqueNm}). ${it.notes}" }
            ?: "Torque is not inferred; consult the Ford workshop manual."
        val validationNote = "Visual service-scope model only; verify VIN-specific quantity, finish, and specification before removal or installation."
        return buildList {
            if (profile.boltCount > 0) {
                add(
                    FastenerInventoryItem(
                        name = profile.boltName,
                        category = FastenerCategory.BOLT,
                        quantity = profile.boltCount,
                        specOrThread = profile.boltThread,
                        toolRequired = profile.boltTool,
                        notes = "$torque $validationNote"
                    )
                )
            }
            if (profile.screwCount > 0) {
                add(
                    FastenerInventoryItem(
                        name = profile.screwName,
                        category = FastenerCategory.SCREW,
                        quantity = profile.screwCount,
                        specOrThread = "Torx recessed-drive reference geometry",
                        toolRequired = "T20 Torx driver",
                        notes = validationNote
                    )
                )
            }
            if (profile.washerCount > 0) {
                add(
                    FastenerInventoryItem(
                        name = "Hardened flat / lock washer set",
                        category = FastenerCategory.WASHER_SEAL,
                        quantity = profile.washerCount,
                        specOrThread = "Sized to the displayed service fastener set",
                        toolRequired = "Hand-positioned during reassembly",
                        notes = validationNote
                    )
                )
            }
            if (profile.includeSeal) {
                add(
                    FastenerInventoryItem(
                        name = "Mating-surface gasket or O-ring",
                        category = FastenerCategory.WASHER_SEAL,
                        quantity = 1,
                        specOrThread = "Component-specific elastomer / composite seal",
                        toolRequired = "Non-marring seal pick and alignment guides",
                        notes = "Replace only with the part number specified for the exact VIN and service operation."
                    )
                )
            }
            add(
                FastenerInventoryItem(
                    name = "Harness / connector retention point",
                    category = FastenerCategory.WIRING_HARNESS,
                    quantity = 1,
                    specOrThread = "Weather-sealed connector reference",
                    toolRequired = "Release tab; never pull on conductors",
                    notes = "Connector keying, pin count, and routing are vehicle-configuration dependent."
                )
            )
        }
    }

    private fun buildRenderableHardware(
        component: Component3DModel,
        profile: HardwareProfile
    ): List<SubAssemblyPart> {
        val parts = mutableListOf<SubAssemblyPart>()
        val baseRadius = when (component.system) {
            VehicleSystem.ENGINE, VehicleSystem.TRANSMISSION, VehicleSystem.DRIVETRAIN_4WD -> 0.72f
            VehicleSystem.BRAKES_CHASSIS -> 0.68f
            VehicleSystem.INTERIOR_DASH, VehicleSystem.LIGHTING_BODY, VehicleSystem.SUNROOF_ROOF -> 0.46f
            else -> 0.54f
        }
        val torqueText = component.torqueSpecs.firstOrNull()?.let { "Torque reference: ${it.torqueFtLbs} ft-lb / ${it.torqueNm}." }
            ?: "Torque reference required from Ford workshop manual."

        repeat(profile.boltCount) { index ->
            val offset = circularPosition(index, profile.boltCount, baseRadius, 0.12f)
            parts += SubAssemblyMeshGenerator.createThreadedHexBoltSubAssembly(
                id = "${component.id}_service_bolt_${index + 1}",
                name = "${component.name} service bolt ${index + 1}",
                headRadius = profile.boltRadius,
                shankRadius = profile.boltRadius * 0.48f,
                shankLength = profile.boltLength,
                localOffset = offset,
                explodeDir = radialExplodeDirection(offset),
                explodeMultiplier = 3.0f + (index % 3) * 0.18f,
                specDetails = "${profile.boltThread} • $torqueText Verify against VIN-specific Ford data."
            )
        }

        repeat(profile.washerCount) { index ->
            val matchingBolt = circularPosition(index, profile.boltCount.coerceAtLeast(1), baseRadius, -0.02f)
            parts += SubAssemblyMeshGenerator.createWasherSubAssembly(
                id = "${component.id}_service_washer_${index + 1}",
                name = "${component.name} service washer ${index + 1}",
                innerRadius = profile.boltRadius * 0.50f,
                outerRadius = profile.boltRadius * 1.25f,
                thickness = 0.024f,
                localOffset = matchingBolt,
                explodeDir = radialExplodeDirection(matchingBolt),
                explodeMultiplier = 2.3f + (index % 2) * 0.2f,
                specDetails = "Reference washer paired to displayed fastener; confirm material and quantity from parts catalogue."
            )
        }

        repeat(profile.screwCount) { index ->
            val offset = circularPosition(index, profile.screwCount, baseRadius * 0.86f, -0.06f)
            parts += SubAssemblyMeshGenerator.createTorxScrewSubAssembly(
                id = "${component.id}_trim_screw_${index + 1}",
                name = "${component.name} Torx screw ${index + 1}",
                localOffset = offset,
                explodeDir = radialExplodeDirection(offset),
                explodeMultiplier = 2.7f + (index % 2) * 0.22f,
                specDetails = "T20-style recessed-drive reference geometry; verify screw type and length before service."
            )
        }

        if (profile.includeSeal) {
            parts += SubAssemblyMeshGenerator.createGasketSubAssembly(
                id = "${component.id}_service_seal",
                name = "${component.name} mating-surface seal",
                width = baseRadius * 1.55f,
                depth = baseRadius * 0.95f,
                thickness = 0.028f,
                localOffset = Point3D(0f, -0.13f, 0f),
                explodeDir = Point3D(0f, 1f, 0f),
                explodeMultiplier = 1.85f,
                specDetails = "Service-scope gasket representation; use exact OEM part number from VIN-specific catalogue."
            )
        }
        return parts
    }

    private fun circularPosition(index: Int, total: Int, radius: Float, y: Float): Point3D {
        val angle = 2.0 * PI * index.toDouble() / total.coerceAtLeast(1)
        return Point3D(
            x = radius * cos(angle).toFloat(),
            y = y,
            z = radius * 0.64f * sin(angle).toFloat()
        )
    }

    private fun radialExplodeDirection(position: Point3D): Point3D {
        val x = if (position.x == 0f) 0.2f else position.x / 0.75f
        val z = if (position.z == 0f) 0.2f else position.z / 0.75f
        return Point3D(x, 0.8f, z)
    }
}
