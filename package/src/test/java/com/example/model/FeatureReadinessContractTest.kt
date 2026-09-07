package com.example.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureReadinessContractTest {
    @Test
    fun foundationCatalog_hasUniqueDomainIds() {
        val ids = FoundationTrackCatalog.domains.map { it.domainId }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun foundationCatalog_doesNotRepresentDevicePendingAsVerified() {
        val devicePending = FoundationTrackCatalog.domains.filter {
            it.readiness == FeatureReadiness.DEVICE_PENDING
        }

        assertTrue(devicePending.isNotEmpty())
        assertTrue(devicePending.none { it.readiness == FeatureReadiness.SANDBOX_VERIFIED })
    }

    @Test
    fun foundationCatalog_containsTheMajorRemainingDomains() {
        val ids = FoundationTrackCatalog.domains.map { it.domainId }.toSet()

        assertTrue(ids.contains("procedural_3d"))
        assertTrue(ids.contains("ac_workbench"))
        assertTrue(ids.contains("mentor"))
        assertTrue(ids.contains("audio_diagnosis"))
        assertTrue(ids.contains("part_store"))
        assertTrue(ids.contains("body_shop"))
    }
}
