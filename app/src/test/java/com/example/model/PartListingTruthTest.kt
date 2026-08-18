package com.example.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartListingTruthTest {
    @Test
    fun savedCatalogQuote_isNotPresentedAsLiveOrFitmentConfirmed() {
        val summary = PriceQuoteRecord(
            retailer = RetailerSource.OREILLY_PRO,
            partNumber = "TEST-123",
            itemPrice = 12.0,
            quoteStatus = QuoteStatus.SAVED_CATALOG,
            fitmentEvidence = FitmentEvidence.VIN_REQUIRED
        ).truthSummary()

        assertFalse(summary.canBePresentedAsLive)
        assertFalse(summary.canBePresentedAsFitmentConfirmed)
        assertFalse(summary.orderingEnabled)
        assertTrue(summary.priceLabel.contains("not live"))
    }

    @Test
    fun authorizedQuoteStillDoesNotEnableOrdering() {
        val summary = PriceQuoteRecord(
            retailer = RetailerSource.OREILLY_PRO,
            partNumber = "LIVE-123",
            quoteStatus = QuoteStatus.LIVE_AUTHORIZED,
            fitmentEvidence = FitmentEvidence.CONFIRMED_BY_USER,
            sellerName = "Authorized example seller"
        ).truthSummary()

        assertTrue(summary.canBePresentedAsLive)
        assertTrue(summary.canBePresentedAsFitmentConfirmed)
        assertFalse(summary.orderingEnabled)
        assertTrue(summary.sellerLabel.contains("Authorized example seller"))
    }
}
