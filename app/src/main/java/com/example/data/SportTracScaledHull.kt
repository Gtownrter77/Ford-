package com.example.data

import com.example.model.Component3DModel
import com.example.model.Face3D
import com.example.model.Point3D
import com.example.model.VehicleSystem

/** Meter-scale reference hull. Geometry is explicitly a teaching envelope, not OEM CAD. */
object SportTracScaledHull {
    private val s get() = SportTracVehicleScale

    val components: List<Component3DModel> = listOf(
        component("scaled_frame_left", "Left frame rail", VehicleSystem.BRAKES_CHASSIS, Point3D(-0.62f, 0.38f, 0f), 1.8f, 0.12f, 3.0f, 0.12f),
        component("scaled_frame_right", "Right frame rail", VehicleSystem.BRAKES_CHASSIS, Point3D(0.62f, 0.38f, 0f), 1.8f, 0.12f, 3.0f, 0.12f),
        component("scaled_cab", "Cab shell envelope", VehicleSystem.LIGHTING_BODY, Point3D(0f, 1.10f, 0.55f), 1.58f, 1.20f, 1.75f, 0.10f),
        component("scaled_bed", "Bed shell envelope", VehicleSystem.LIGHTING_BODY, Point3D(0f, 0.98f, -1.30f), 1.55f, 0.82f, 1.30f, 0.12f),
        component("scaled_engine_40l", "4.0L SOHC engine envelope", VehicleSystem.ENGINE, Point3D(0f, 0.82f, 1.02f), 0.88f, 0.78f, 0.92f, 0.18f),
        component("scaled_trans_5r55e", "5R55E transmission envelope", VehicleSystem.TRANSMISSION, Point3D(0f, 0.48f, 0.20f), 0.62f, 0.55f, 1.05f, 0.20f),
        component("scaled_tcase_bw4411", "BW4411 transfer case envelope", VehicleSystem.DRIVETRAIN_4WD, Point3D(0f, 0.42f, -0.52f), 0.52f, 0.52f, 0.60f, 0.20f),
        component("scaled_rear_88", "Ford 8.8 rear axle envelope", VehicleSystem.DRIVETRAIN_4WD, Point3D(0f, 0.36f, s.rearAxleZ), 1.50f, 0.50f, 0.34f, 0.18f),
        component("scaled_front_diff", "Front differential envelope", VehicleSystem.DRIVETRAIN_4WD, Point3D(0f, 0.34f, s.frontAxleZ), 0.85f, 0.48f, 0.38f, 0.18f),
        component("scaled_wheel_fl", "Front wheel LH", VehicleSystem.BRAKES_CHASSIS, s.wheelCenter(true, false), 0.76f, 0.28f, 0.76f, 0.30f),
        component("scaled_wheel_fr", "Front wheel RH", VehicleSystem.BRAKES_CHASSIS, s.wheelCenter(true, true), 0.76f, 0.28f, 0.76f, 0.30f),
        component("scaled_wheel_rl", "Rear wheel LH", VehicleSystem.BRAKES_CHASSIS, s.wheelCenter(false, false), 0.76f, 0.28f, 0.76f, 0.30f),
        component("scaled_wheel_rr", "Rear wheel RH", VehicleSystem.BRAKES_CHASSIS, s.wheelCenter(false, true), 0.76f, 0.28f, 0.76f, 0.30f),
        component("scaled_front_shaft", "Front propeller shaft", VehicleSystem.DRIVETRAIN_4WD, Point3D(0f, 0.30f, 0.62f), 0.14f, 0.14f, 1.35f, 0.25f),
        component("scaled_rear_shaft", "Rear propeller shaft", VehicleSystem.DRIVETRAIN_4WD, Point3D(0f, 0.30f, -0.44f), 0.14f, 0.14f, 1.30f, 0.25f)
    )

    fun box(width: Float, height: Float, depth: Float, center: Point3D, color: String): Pair<List<Point3D>, List<Face3D>> {
        val x = width / 2f; val y = height / 2f; val z = depth / 2f
        val v = listOf(
            Point3D(center.x-x, center.y-y, center.z-z), Point3D(center.x+x, center.y-y, center.z-z),
            Point3D(center.x+x, center.y+y, center.z-z), Point3D(center.x-x, center.y+y, center.z-z),
            Point3D(center.x-x, center.y-y, center.z+z), Point3D(center.x+x, center.y-y, center.z+z),
            Point3D(center.x+x, center.y+y, center.z+z), Point3D(center.x-x, center.y+y, center.z+z)
        )
        val f = listOf(listOf(0,1,2,3), listOf(4,7,6,5), listOf(0,4,5,1), listOf(1,5,6,2), listOf(2,6,7,3), listOf(4,0,3,7)).map { Face3D(it, color) }
        return v to f
    }

    fun cylinder(radius: Float, length: Float, segments: Int, center: Point3D, color: String, axis: Char): Pair<List<Point3D>, List<Face3D>> {
        val vertices = mutableListOf<Point3D>()
        repeat(2) { end -> repeat(segments) { i ->
            val a = (2.0 * Math.PI * i / segments).toFloat(); val r = radius
            val axial = if (end == 0) -length/2f else length/2f
            vertices += when (axis) {
                'X' -> Point3D(center.x + axial, center.y + r*kotlin.math.cos(a), center.z + r*kotlin.math.sin(a))
                'Y' -> Point3D(center.x + r*kotlin.math.cos(a), center.y + axial, center.z + r*kotlin.math.sin(a))
                else -> Point3D(center.x + r*kotlin.math.cos(a), center.y + r*kotlin.math.sin(a), center.z + axial)
            }
        } }
        val faces = mutableListOf<Face3D>(); repeat(segments) { i -> val n=(i+1)%segments; faces += Face3D(listOf(i,n,segments+n,segments+i), color) }
        faces += Face3D((0 until segments).toList(), color); faces += Face3D((segments until segments*2).toList().reversed(), color)
        return vertices to faces
    }

    private fun component(id: String, name: String, system: VehicleSystem, center: Point3D, width: Float, height: Float, depth: Float, explode: Float): Component3DModel {
        val mesh = box(width, height, depth, center, system.hexColor)
        return Component3DModel(id, name, system, "REFERENCE-$id", "Meter-scale teaching envelope; not OEM CAD.", "Vehicle reference placement", "Reference model", 0, mesh.first, mesh.second, center, Point3D(0f, explode, 0f), emptyList(), emptyList(), emptyList(), emptyList())
    }
}
