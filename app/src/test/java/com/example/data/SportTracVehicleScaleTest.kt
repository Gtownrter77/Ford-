package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class SportTracVehicleScaleTest {

    @Test
    fun ownerGuideMillimetersMatchInches() {
        assertEquals(5230, SportTracVehicleScale.LENGTH_MM)
        assertEquals(1823, SportTracVehicleScale.WIDTH_MM)
        assertEquals(1776, SportTracVehicleScale.HEIGHT_MM)
        assertEquals(1794, SportTracVehicleScale.HEIGHT_4X4_16_MM)
        assertEquals(3198, SportTracVehicleScale.WHEELBASE_MM)
        assertEquals(1486, SportTracVehicleScale.TRACK_FRONT_MM)
        assertEquals(1480, SportTracVehicleScale.TRACK_REAR_MM)
    }

    @Test
    fun metersAreInchTimes0254() {
        assertEquals(205.9f * 0.0254f, SportTracVehicleScale.lengthM, 0.0001f)
        assertEquals(125.9f * 0.0254f, SportTracVehicleScale.wheelbaseM, 0.0001f)
    }

    @Test
    fun overhangsReconstructOverallLength() {
        assertTrue(SportTracVehicleScale.overallFitsOgEnvelope())
        val span = SportTracVehicleScale.frontBumperZ - SportTracVehicleScale.rearBumperZ
        assertEquals(SportTracVehicleScale.lengthM, span, 0.002f)
    }

    @Test
    fun axlesSitOnOfficialWheelbase() {
        val span = SportTracVehicleScale.frontAxleZ - SportTracVehicleScale.rearAxleZ
        assertEquals(SportTracVehicleScale.wheelbaseM, span, 0.0001f)
    }

    @Test
    fun wheelsSitOnOfficialTracksAndTireRadius() {
        val fl = SportTracVehicleScale.wheelCenter(front = true, passenger = false)
        val fr = SportTracVehicleScale.wheelCenter(front = true, passenger = true)
        val rl = SportTracVehicleScale.wheelCenter(front = false, passenger = false)
        val rr = SportTracVehicleScale.wheelCenter(front = false, passenger = true)
        assertEquals(SportTracVehicleScale.trackFrontM, fr.x - fl.x, 0.0001f)
        assertEquals(SportTracVehicleScale.trackRearM, rr.x - rl.x, 0.0001f)
        assertEquals(SportTracVehicleScale.tireRadiusM, fl.y, 0.0001f)
        assertEquals(SportTracVehicleScale.frontAxleZ, fl.z, 0.0001f)
        assertEquals(SportTracVehicleScale.rearAxleZ, rl.z, 0.0001f)
    }

    @Test
    fun hullContainsMeterTrueShellParts() {
        val ids = SportTracScaledHull.components.map { it.id }.toSet()
        assertTrue(ids.containsAll(listOf(
            "scaled_frame_left", "scaled_cab", "scaled_bed",
            "scaled_engine_40l", "scaled_trans_5r55e", "scaled_tcase_bw4411",
            "scaled_rear_88", "scaled_wheel_fl", "scaled_wheel_rr"
        )))
        val xs = SportTracScaledHull.components.flatMap { it.vertices.map { v -> v.x } }
        val zs = SportTracScaledHull.components.flatMap { it.vertices.map { v -> v.z } }
        assertTrue(xs.maxOrNull()!! <= SportTracVehicleScale.widthM / 2f + 0.20f)
        assertTrue(abs(zs.maxOrNull()!! - zs.minOrNull()!!) <= SportTracVehicleScale.lengthM + 0.25f)
    }
}
