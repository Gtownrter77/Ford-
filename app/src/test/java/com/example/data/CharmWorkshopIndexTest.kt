package com.example.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharmWorkshopIndexTest {

    @Test
    fun publishedLeavesStayOnFourWheelDriveTree() {
        assertTrue(CharmWorkshopIndex.leaves.isNotEmpty())
        CharmWorkshopIndex.leaves.forEach { leaf ->
            assertTrue(leaf.url.contains("4WD"))
            assertFalse(CharmWorkshopIndex.isRejectedTwoWheelDriveUrl(leaf.url))
            assertFalse(leaf.url.contains("2WD"))
        }
    }

    @Test
    fun twoWheelDrivePackUrlsAreRejected() {
        CharmWorkshopIndex.rejectedTwoWheelDriveUrls.forEach { url ->
            assertTrue(CharmWorkshopIndex.isRejectedTwoWheelDriveUrl(url))
        }
        val formatted = CharmWorkshopIndex.format(
            CharmWorkshopIndex.rejectedTwoWheelDriveUrls.map {
                CharmWorkshopLeaf("bad", it, listOf("x"))
            }
        )
        assertFalse(formatted.contains("2WD"))
        assertTrue(formatted.contains("4WD VIN K"))
    }

    @Test
    fun compressorQueryUsesFourWheelDriveLeaf() {
        val hits = CharmWorkshopIndex.matching("a/c compressor service")
        assertTrue(hits.any { it.url.contains("Compressor") && it.url.contains("4WD") })
        assertFalse(hits.any { CharmWorkshopIndex.isRejectedTwoWheelDriveUrl(it.url) })
    }

}
