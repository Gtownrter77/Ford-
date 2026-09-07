package com.example.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfficialPublicationsAndCommunityTest {

    @Test
    fun ownerPublicationsStayOnFordHostedUrls() {
        assertTrue(OfficialOwnerPublications.catalog.size >= 10)
        OfficialOwnerPublications.catalog.forEach { pub ->
            assertTrue(
                pub.url.startsWith("https://www.ford.com/") ||
                    pub.url.startsWith("https://www.fordservicecontent.com/")
            )
        }
    }

    @Test
    fun ownerGuideQueryReturnsPrintingPdf() {
        val hits = OfficialOwnerPublications.matching("owner guide pdf fluids")
        assertTrue(hits.any { it.url.contains("04p27og") })
    }

    @Test
    fun communityVideosStayLabeledUnverified() {
        assertTrue(CommunityRepairVideos.catalog.size >= 17)
        CommunityRepairVideos.catalog.forEach { video ->
            assertTrue(video.url.startsWith("https://www.youtube.com/watch"))
        }
        assertTrue(CommunityRepairVideos.DISCLAIMER.contains("Not the Ford workshop manual"))
    }

    @Test
    fun communityForumDoesNotIncludeSecondGenBoardAsDefault() {
        CommunityForumThreads.catalog.forEach { thread ->
            assertFalse(thread.url.contains("2007-2010-explorer-sport-trac.124"))
        }
        val formatted = CommunityForumThreads.format(CommunityForumThreads.matching("timing chain rattle"))
        assertTrue(formatted.contains("201407"))
        assertTrue(formatted.contains("Community thread only"))
    }

    @Test
    fun charmIndexIncludesPartsLaborAndRejectsTwoWheelDrive() {
        assertTrue(CharmWorkshopIndex.leaves.any { it.title.contains("Parts and Labor") })
        assertTrue(CharmWorkshopIndex.leaves.any { it.url.contains("bundle") })
        CharmWorkshopIndex.leaves.forEach { leaf ->
            assertTrue(leaf.url.contains("4WD"))
            assertFalse(leaf.url.contains("2WD"))
        }
    }
}
