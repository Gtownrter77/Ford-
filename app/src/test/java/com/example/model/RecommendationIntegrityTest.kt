package com.example.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationIntegrityTest {

    @Test
    fun fitmentEvidenceOutranksCheaperUnverifiedMarketplaceListing() {
        val verifiedCatalogQuote = quote(
            retailer = RetailerSource.ROCKAUTO,
            total = 92.00,
            evidence = FitmentEvidence.PART_NUMBER_MATCH,
            status = QuoteStatus.SAVED_CATALOG
        )
        val cheaperMarketplaceListing = quote(
            retailer = RetailerSource.EBAY,
            total = 55.00,
            evidence = FitmentEvidence.SELLER_AND_FITMENT_REVIEW,
            status = QuoteStatus.SAVED_CATALOG
        )

        val ranked = RecommendationIntegrity.sortedForReview(
            listOf(cheaperMarketplaceListing, verifiedCatalogQuote)
        )

        assertEquals(verifiedCatalogQuote, ranked.first())
    }

    @Test
    fun quoteVerificationStatusOutranksManualLinkWhenFitmentIsEqual() {
        val savedRecord = quote(
            retailer = RetailerSource.AMAZON,
            total = 85.00,
            evidence = FitmentEvidence.VIN_REQUIRED,
            status = QuoteStatus.SAVED_CATALOG
        )
        val manualRecord = quote(
            retailer = RetailerSource.FACEBOOK_MARKETPLACE,
            total = 60.00,
            evidence = FitmentEvidence.VIN_REQUIRED,
            status = QuoteStatus.MANUAL_LINK
        )

        val ranked = RecommendationIntegrity.sortedForReview(listOf(manualRecord, savedRecord))

        assertEquals(savedRecord, ranked.first())
    }

    @Test
    fun retailerListPreservesManualOtherRetailerChoice() {
        assertTrue(RetailerSource.entries.contains(RetailerSource.OTHER_ONLINE))
    }

    private fun quote(
        retailer: RetailerSource,
        total: Double,
        evidence: FitmentEvidence,
        status: QuoteStatus
    ): PriceQuoteRecord = PriceQuoteRecord(
        retailer = retailer,
        partNumber = "TEST-PART-001",
        itemPrice = total,
        shippingCost = 0.0,
        coreCharge = 0.0,
        quoteStatus = status,
        fitmentEvidence = evidence
    )
}
