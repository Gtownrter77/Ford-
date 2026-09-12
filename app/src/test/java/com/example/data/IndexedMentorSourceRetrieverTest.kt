package com.example.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IndexedMentorSourceRetrieverTest {
    private val pages = listOf(
        IndexedSourcePage(
            sourceId = "charm-4wd-vin-k-hvac",
            title = "A/C compressor pressure — Climate Control",
            section = "CHARM 4WD VIN K Flex Fuel",
            pageLabel = "1770",
            configuration = "2004 Sport Trac 4WD V6-4.0L VIN K Flex Fuel",
            excerpt = "4WD VIN K A/C high and low side pressures. Recover, evacuate, recharge. Do not use 2WD HVAC pages.",
            sourceUrl = "zip://pages/1770.html",
            evidenceType = "diagnosis",
            eligibility = "preferred",
            checksum = "abc"
        ),
        IndexedSourcePage(
            sourceId = "charm-4wd-vin-k-timing",
            title = "Timing chain and tensioner — Engine",
            section = "CHARM 4WD VIN K Flex Fuel",
            pageLabel = "7934",
            configuration = "2004 Sport Trac 4WD V6-4.0L VIN K Flex Fuel",
            excerpt = "Cologne 4.0L SOHC VIN K timing chain, guides, and tensioner. 4WD VIN K only.",
            sourceUrl = "zip://pages/7934.html",
            evidenceType = "procedure",
            eligibility = "preferred",
            checksum = "def"
        ),
        IndexedSourcePage(
            sourceId = "blocked-2wd",
            title = "2WD HVAC compressor",
            section = "wrong tree",
            pageLabel = "1",
            configuration = "2WD",
            excerpt = "2WD only compressor R&R",
            sourceUrl = "https://example/2WD/hvac",
            evidenceType = "procedure",
            eligibility = "blocked",
            checksum = "ghi"
        )
    )
    private val retriever = IndexedMentorSourceRetriever { pages }

    @Test
    fun hvacQueryReturnsFourWheelDriveVinKEvidence() = runTest {
        val results = retriever.search("A/C compressor pressure", VehicleConfiguration())
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.configuration.contains("4WD") && it.configuration.contains("VIN K") })
        assertTrue(results.none { it.sourceUrl.orEmpty().contains("2WD") })
        assertTrue(results.any { it.excerpt.contains("A/C") || it.title.contains("A/C") })
    }

    @Test
    fun timingQueryDoesNotSelectTwoWheelDrivePage() = runTest {
        val results = retriever.search("timing chain", VehicleConfiguration())
        assertTrue(results.any { it.title.contains("Timing", ignoreCase = true) })
        assertTrue(results.none { it.title.contains("2WD") })
    }

    @Test
    fun twoWheelDriveConfigurationReturnsNothing() = runTest {
        assertEquals(
            emptyList<SourceExcerpt>(),
            retriever.search("timing chain", VehicleConfiguration(drivetrain = "2WD"))
        )
    }

    @Test
    fun missingTorqueSourceIsExplicit() = runTest {
        val results = retriever.search("left-hand thread widget torque", VehicleConfiguration())
        assertTrue(results.isEmpty())
        val context = MentorSourceContext.format(results)
        assertTrue(context.contains("No configuration-safe indexed source matched"))
    }
}
