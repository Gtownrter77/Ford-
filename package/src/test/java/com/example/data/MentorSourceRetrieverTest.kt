package com.example.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class MentorSourceRetrieverTest {
    @Test fun hvacQueryReturnsOnlyFourWheelDriveVinKSourcePointers() = runTest {
        val results = CuratedMentorSourceRetriever().search("A/C compressor pressure", VehicleConfiguration())
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.configuration.contains("4WD V6-4.0L VIN K") })
        assertTrue(results.none { it.sourceUrl.orEmpty().contains("2WD") })
    }

    @Test fun incompatibleConfigurationReturnsNoSource() = runTest {
        assertEquals(emptyList<SourceExcerpt>(), CuratedMentorSourceRetriever().search("timing chain", VehicleConfiguration(drivetrain = "2WD")))
    }

    @Test fun missingMatchProducesExplicitCitationRule() {
        val context = MentorSourceContext.format(emptyList())
        assertTrue(context.contains("No configuration-safe indexed source matched"))
        assertTrue(context.contains("must be checked"))
    }
}
