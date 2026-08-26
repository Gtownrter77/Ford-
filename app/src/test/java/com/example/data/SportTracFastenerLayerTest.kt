package com.example.data

import com.example.model.FastenerCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SportTracFastenerLayerTest {

    @Test
    fun everyWheelGetsFiveLugNutsAndAnAxleNut() {
        listOf("scaled_wheel_fl", "scaled_wheel_fr", "scaled_wheel_rl", "scaled_wheel_rr").forEach { id ->
            val joints = SportTracFastenerLayer.jointsFor(id)
            assertEquals(6, joints.size)
            assertEquals(5, joints.count { it.kind == SportTracFastenerLayer.Kind.LUG_NUT })
            assertEquals(1, joints.count { it.kind == SportTracFastenerLayer.Kind.AXLE_NUT })
            joints.filter { it.kind == SportTracFastenerLayer.Kind.LUG_NUT }.forEach { joint ->
                assertTrue(joint.spec.contains("1/2-20"))
                assertTrue(joint.torque.contains("84-114"))
            }
        }
    }

    @Test
    fun transmissionPanHasSixteenBolts() {
        val pans = SportTracFastenerLayer.jointsFor("scaled_trans_5r55e").filter { it.id.startsWith("pan_") }
        assertEquals(16, pans.size)
    }

    @Test
    fun rearCoverHasTenBoltsPlusFillPlug() {
        val joints = SportTracFastenerLayer.jointsFor("scaled_rear_88")
        assertEquals(10, joints.count { it.id.startsWith("rear88_") && it.id != "rear88_fill" })
        assertTrue(joints.any { it.id == "rear88_fill" })
    }

    @Test
    fun enrichedHullExposesBoltsWashersAndNuts() {
        val hull = SportTracFastenerLayer.enrichedHull
        assertTrue(hull.size >= 15)
        val parts = hull.flatMap { it.subAssemblies }
        assertTrue(parts.count { it.id.endsWith("_bolt") } > 40)
        assertTrue(parts.count { it.id.endsWith("_washer") } > 20)
        assertTrue(parts.count { it.id.endsWith("_nut") } > 40)
        assertTrue(hull.flatMap { it.fasteners }.any { it.category == FastenerCategory.BOLT })
        hull.flatMap { it.fasteners }.forEach { item ->
            assertTrue(item.notes.contains("VIN-specific"))
        }
    }
}
