package com.example.model

/**
 * Quote ranking is intentionally limited to repair-relevant evidence. It does
 * not accept affiliate, referral, commission, retailer-preference, or customer
 * payment data, so a future funding link cannot change a result.
 */
object RecommendationIntegrity {
    fun compareQuotes(first: PriceQuoteRecord, second: PriceQuoteRecord): Int {
        val firstEvidence = evidenceRank(first.fitmentEvidence)
        val secondEvidence = evidenceRank(second.fitmentEvidence)
        if (firstEvidence != secondEvidence) return firstEvidence.compareTo(secondEvidence)

        val firstStatus = quoteStatusRank(first.quoteStatus)
        val secondStatus = quoteStatusRank(second.quoteStatus)
        if (firstStatus != secondStatus) return firstStatus.compareTo(secondStatus)

        val firstTotal = first.deliveredTotal ?: Double.MAX_VALUE
        val secondTotal = second.deliveredTotal ?: Double.MAX_VALUE
        if (firstTotal != secondTotal) return firstTotal.compareTo(secondTotal)

        return first.retailer.displayName.compareTo(second.retailer.displayName)
    }

    fun sortedForReview(quotes: List<PriceQuoteRecord>): List<PriceQuoteRecord> =
        quotes.sortedWith(::compareQuotes)

    private fun evidenceRank(evidence: FitmentEvidence): Int = when (evidence) {
        FitmentEvidence.CONFIRMED_BY_USER -> 0
        FitmentEvidence.PART_NUMBER_MATCH -> 1
        FitmentEvidence.VIN_REQUIRED -> 2
        FitmentEvidence.SELLER_AND_FITMENT_REVIEW -> 3
    }

    private fun quoteStatusRank(status: QuoteStatus): Int = when (status) {
        QuoteStatus.LIVE_AUTHORIZED -> 0
        QuoteStatus.SAVED_CATALOG -> 1
        QuoteStatus.MANUAL_LINK -> 2
    }
}
