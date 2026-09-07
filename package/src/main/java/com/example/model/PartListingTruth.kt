package com.example.model

/**
 * Evidence-aware presentation summary for a Part Store quote.
 * This never authorizes ordering or claims fitment by itself.
 */
data class PartListingTruth(
    val priceLabel: String,
    val fitmentLabel: String,
    val sellerLabel: String,
    val canBePresentedAsLive: Boolean,
    val canBePresentedAsFitmentConfirmed: Boolean,
    val orderingEnabled: Boolean = false
)

fun PriceQuoteRecord.truthSummary(): PartListingTruth {
    val live = quoteStatus == QuoteStatus.LIVE_AUTHORIZED
    val fitmentConfirmed = fitmentEvidence == FitmentEvidence.CONFIRMED_BY_USER

    return PartListingTruth(
        priceLabel = when (quoteStatus) {
            QuoteStatus.SAVED_CATALOG -> "Saved catalog price — not live"
            QuoteStatus.MANUAL_LINK -> "Open retailer link to review current price"
            QuoteStatus.LIVE_AUTHORIZED -> "Live authorized quote"
        },
        fitmentLabel = when (fitmentEvidence) {
            FitmentEvidence.VIN_REQUIRED -> "Fitment requires VIN or vehicle confirmation"
            FitmentEvidence.PART_NUMBER_MATCH -> "Part-number match — verify vehicle fitment before purchase"
            FitmentEvidence.SELLER_AND_FITMENT_REVIEW -> "Seller and fitment review required before purchase"
            FitmentEvidence.CONFIRMED_BY_USER -> "Fitment confirmed by user"
        },
        sellerLabel = sellerName?.takeIf { it.isNotBlank() } ?: "Seller not verified by this app",
        canBePresentedAsLive = live,
        canBePresentedAsFitmentConfirmed = fitmentConfirmed,
        orderingEnabled = false
    )
}
