package com.example.data

import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.util.SubAssemblyMeshGenerator

/** Service-scope visual fastener stacks; not a VIN-complete OEM BOM. */
object SportTracFastenerLayer {
    enum class Kind { LUG_NUT, AXLE_NUT, BOLT, WASHER, FILL_PLUG }
    data class Joint(val id: String, val kind: Kind, val spec: String, val torque: String)

    private val hullIds = listOf(
        "scaled_frame_left", "scaled_frame_right", "scaled_cab", "scaled_bed", "scaled_engine_40l",
        "scaled_trans_5r55e", "scaled_tcase_bw4411", "scaled_rear_88", "scaled_front_diff",
        "scaled_wheel_fl", "scaled_wheel_fr", "scaled_wheel_rl", "scaled_wheel_rr", "scaled_front_shaft", "scaled_rear_shaft"
    )

    val enrichedHull: List<Component3DModel> by lazy {
        SportTracScaledHull.components.map { enrich(it) }
    }

    fun enrich(component: Component3DModel): Component3DModel {
        val inventory = VehicleHardwareCatalog.enrich(component)
        return inventory.copy(subAssemblies = inventory.subAssemblies + visualFasteners(component))
    }

    fun jointsFor(componentId: String): List<Joint> = when {
        componentId.startsWith("scaled_wheel_") -> (1..5).map { Joint("${componentId}_lug_$it", Kind.LUG_NUT, "1/2-20", "84-114 ft-lb") } +
            Joint("${componentId}_axle", Kind.AXLE_NUT, "VIN-specific hub/axle nut", "Confirm workshop manual")
        componentId == "scaled_trans_5r55e" -> (1..16).map { Joint("pan_$it", Kind.BOLT, "Transmission pan bolt", "Confirm workshop manual") }
        componentId == "scaled_rear_88" -> (1..10).map { Joint("rear88_$it", Kind.BOLT, "Differential cover bolt", "Confirm workshop manual") } +
            Joint("rear88_fill", Kind.FILL_PLUG, "Differential fill plug", "Confirm workshop manual")
        else -> (1..4).map { Joint("${componentId}_bolt_$it", Kind.BOLT, "Reference service fastener", "Confirm VIN-specific workshop manual") }
    }

    private fun visualFasteners(component: Component3DModel): List<SubAssemblyPart> {
        val joints = jointsFor(component.id)
        val out = mutableListOf<SubAssemblyPart>()
        joints.forEachIndexed { index, joint ->
            val offset = Point3D((index % 4 - 1.5f) * 0.12f, 0.08f, (index / 4) * 0.10f)
            when (joint.kind) {
                Kind.WASHER -> Unit
                Kind.FILL_PLUG -> out += SubAssemblyMeshGenerator.createHexBoltSubAssembly("${joint.id}_bolt", "${component.name} fill plug", localOffset = offset, specDetails = joint.spec)
                else -> {
                    out += SubAssemblyMeshGenerator.createHexBoltSubAssembly("${joint.id}_bolt", "${component.name} bolt", localOffset = offset, specDetails = joint.spec)
                    out += SubAssemblyMeshGenerator.createWasherSubAssembly("${joint.id}_washer", "${component.name} washer", localOffset = offset, specDetails = "VIN-specific washer; confirm manual")
                    out += SubAssemblyMeshGenerator.createHexBoltSubAssembly("${joint.id}_nut", "${component.name} nut", headRadius = 0.10f, shankLength = 0.12f, localOffset = offset, specDetails = joint.torque)
                }
            }
        }
        return out
    }
}
