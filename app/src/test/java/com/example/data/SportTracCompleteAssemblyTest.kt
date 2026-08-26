package com.example.data

import com.example.model.VehicleSystem
import org.junit.Assert.assertTrue
import org.junit.Test

class SportTracCompleteAssemblyTest {

    @Test
    fun extrasAndHullArePresent() {
        val ids = SportTracCompleteAssembly.hullOnly.map { it.id }.toSet()
        listOf(
            "scaled_engine_40l", "scaled_trans_5r55e", "scaled_tcase_bw4411",
            "scaled_exhaust", "scaled_fuel_tank", "scaled_battery",
            "scaled_caliper_fl", "scaled_shaft_rear", "scaled_airbox",
            "scaled_condenser", "scaled_headlamp_l", "scaled_spare"
        ).forEach { id ->
            assertTrue("$id missing", ids.contains(id))
        }
    }

    @Test
    fun fastenersExistOnCompleteHull() {
        assertTrue(SportTracCompleteAssembly.hullOnly.sumOf { it.subAssemblies.size } > 80)
    }

    @Test
    fun catalogSystemsStayRepresented() {
        val systems = SportTracCompleteAssembly.systemCoverage
        listOf(
            VehicleSystem.ENGINE,
            VehicleSystem.TRANSMISSION,
            VehicleSystem.DRIVETRAIN_4WD,
            VehicleSystem.COOLING,
            VehicleSystem.AIR_INTAKE,
            VehicleSystem.ELECTRICAL,
            VehicleSystem.BRAKES_CHASSIS,
            VehicleSystem.AIR_CONDITIONING,
            VehicleSystem.LIGHTING_BODY
        ).forEach { system ->
            assertTrue("$system missing", systems.contains(system.name))
        }
    }
}
