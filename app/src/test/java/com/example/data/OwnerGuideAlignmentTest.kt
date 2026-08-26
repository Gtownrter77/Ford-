package com.example.data

import com.example.mentor.MentorKnowledge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnerGuideAlignmentTest {

    @Test
    fun overviewUsesOwnerGuideCoolantAndAirFilter() {
        assertTrue(PublishedSportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("14.0"))
        assertFalse(PublishedSportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("15.3"))
        assertEquals(OwnerGuideSpecs.AIR_FILTER, PublishedSportTracData.vehicleOverviewSpecs.getValue("Air Filter"))
        assertTrue(PublishedSportTracData.vehicleOverviewSpecs.getValue("Transfer Case Fluid").contains("1.3"))
        assertTrue(PublishedSportTracData.vehicleOverviewSpecs.getValue("Transfer Case Fluid").contains("not MERCON V"))
    }

    @Test
    fun maintenanceScheduleUsesOwnerGuideAirAndCoolant() {
        val air = PublishedSportTracData.defaultMaintenanceSchedules.first { it.id == "air_filter" }
        val coolant = PublishedSportTracData.defaultMaintenanceSchedules.first { it.id == "coolant_flush" }
        assertTrue(air.fluidTypeOrSpec.contains("FA-1744"))
        assertFalse(air.fluidTypeOrSpec.contains("FA-1695"))
        assertTrue(coolant.fluidTypeOrSpec.contains("14.0"))
        assertFalse(coolant.fluidTypeOrSpec.contains("15.3"))
    }

    @Test
    fun transferCasePackagedStepsCiteMerconAtfNotMerconV() {
        val transfer = PublishedSportTracData.components.filter {
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

    @Test
    fun publishedCatalogNoLongerShowsStaleOwnerGuideNumbers() {
        assertTrue(PublishedSportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("14.0"))
        assertFalse(PublishedSportTracData.vehicleOverviewSpecs.getValue("Cooling System Capacity").contains("15.3"))
        assertEquals(OwnerGuideSpecs.AIR_FILTER, PublishedSportTracData.vehicleOverviewSpecs.getValue("Air Filter"))
        val air = PublishedSportTracData.defaultMaintenanceSchedules.first { it.id == "air_filter" }
        val coolant = PublishedSportTracData.defaultMaintenanceSchedules.first { it.id == "coolant_flush" }
        assertTrue(air.fluidTypeOrSpec.contains("FA-1744"))
        assertFalse(air.fluidTypeOrSpec.contains("FA-1695"))
        assertTrue(coolant.fluidTypeOrSpec.contains("14.0"))
        assertFalse(coolant.fluidTypeOrSpec.contains("15.3"))
        val transferBlob = PublishedSportTracData.components
            .filter { it.name.contains("Transfer", ignoreCase = true) }
            .joinToString { component ->
                component.repairSteps.joinToString { it.instruction } +
                    component.torqueSpecs.joinToString { it.notes } +
                    component.requiredTools.joinToString()
            }
        assertFalse(transferBlob.contains("1.5 quarts of MERCON V"))
        assertFalse(transferBlob.contains("1.5 qts fresh MERCON V"))
        assertTrue(transferBlob.contains("1.3"))
        assertTrue(transferBlob.contains("MERCON ATF"))
    }

    @Test
    fun overviewPublishesBrakeWasherAxlesAndWheelbase() {
        val specs = PublishedSportTracData.vehicleOverviewSpecs
        assertTrue(specs.getValue("Brake Fluid").contains("DOT 3"))
        assertTrue(specs.getValue("Washer Fluid").contains("2.7"))
        assertTrue(specs.getValue("Battery").contains("BXT-65-650"))
        assertTrue(specs.getValue("Fuel Filter").contains("FG-1036"))
        assertTrue(specs.getValue("Rear Axle").contains("75W-90"))
        assertTrue(specs.getValue("Firing Order").contains("1-4-2-5-3-6"))
        assertTrue(specs.getValue("Wheelbase").contains("125.9"))
    }

    @Test
    fun maintenanceScheduleIncludesOgServiceIds() {
        val ids = PublishedSportTracData.defaultMaintenanceSchedules.map { it.id }.toSet()
        assertTrue(ids.containsAll(listOf("fuel_filter", "transfer_case", "front_axle", "rear_axle", "battery", "pcv")))
    }

    @Test
    fun publishedRearAxleForbidsOldSeventyFiveWOneFortyRefill() {
        val blob = PublishedSportTracData.components
            .filter { it.name.contains("Diff", ignoreCase = true) || it.name.contains("Axle", ignoreCase = true) }
            .joinToString { component ->
                component.repairSteps.joinToString { it.instruction } +
                    component.torqueSpecs.joinToString { it.notes } +
                    component.requiredTools.joinToString()
            }
        assertFalse(blob.contains("2.5 qts 75W-140"))
        assertFalse(blob.contains("75W-140"))
        assertTrue(blob.contains("75W-90"))
    }
}
