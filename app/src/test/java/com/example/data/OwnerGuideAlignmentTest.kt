package com.example.data

import com.example.mentor.MentorKnowledge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnerGuideAlignmentTest {

    @Test
    fun overviewUsesOwnerGuideCoolantAndAirFilter() {
        assertTrue(SportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("14.0"))
        assertFalse(SportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("15.3"))
        assertEquals(OwnerGuideSpecs.AIR_FILTER, SportTracData.vehicleOverviewSpecs.getValue("Air Filter"))
        assertTrue(SportTracData.vehicleOverviewSpecs.getValue("Transfer Case Fluid").contains("1.3"))
        assertTrue(SportTracData.vehicleOverviewSpecs.getValue("Transfer Case Fluid").contains("not MERCON V"))
    }

    @Test
    fun maintenanceScheduleUsesOwnerGuideAirAndCoolant() {
        val air = SportTracData.defaultMaintenanceSchedules.first { it.id == "air_filter" }
        val coolant = SportTracData.defaultMaintenanceSchedules.first { it.id == "coolant_flush" }
        assertTrue(air.fluidTypeOrSpec.contains("FA-1744"))
        assertFalse(air.fluidTypeOrSpec.contains("FA-1695"))
        assertTrue(coolant.fluidTypeOrSpec.contains("14.0"))
        assertFalse(coolant.fluidTypeOrSpec.contains("15.3"))
    }

    @Test
    fun transferCasePackagedStepsCiteMerconAtfNotMerconV() {
        val transfer = SportTracData.components.filter {
            it.name.contains("Transfer", ignoreCase = true)
        }
        assertTrue(transfer.isNotEmpty())
        transfer.forEach { component ->
            val blob = (component.description + component.repairSteps.joinToString { it.instruction } +
                component.torqueSpecs.joinToString { it.notes })
            assertFalse(blob.contains("1.5 quarts of MERCON V"))
            assertFalse(blob.contains("1.5 qts fresh MERCON V"))
        }
    }

    @Test
    fun mentorFluidsAnswerUsesOwnerGuideCoolant() {
        val component = SportTracData.components.first()
        val answer = MentorKnowledge.answer(component, "what is the coolant capacity")
        assertTrue(answer.contains("14.0"))
        assertFalse(answer.contains("15.3"))
    }
}
