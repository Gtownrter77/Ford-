package com.example.model

/**
 * Retailer sources supported by the app-side comparison workflow. A source can
 * be opened for exact part-number verification even when live API access is not
 * configured.
 */
enum class RetailerSource(
    val displayName: String,
    val sourceType: String
) {
    OREILLY_PRO("O'Reilly Pro", "Commercial account / local fulfillment"),
    ROCKAUTO("RockAuto", "Online catalog"),
    AMAZON("Amazon", "Marketplace / retailer"),
    EBAY("eBay Motors", "Marketplace"),
    FACEBOOK_MARKETPLACE("Facebook Marketplace", "Local marketplace"),
    OTHER_ONLINE("Other online source", "Manual comparison")
}

enum class FitmentEvidence(
    val label: String,
    val description: String
) {
    VIN_REQUIRED(
        "VIN verification required",
        "The listing has not been confirmed against the vehicle VIN or an approved catalog interchange."
    ),
    PART_NUMBER_MATCH(
        "Part-number match",
        "The listing uses the same manufacturer or interchange part number; vehicle fitment still needs confirmation."
    ),
    SELLER_AND_FITMENT_REVIEW(
        "Seller and fitment review required",
        "Marketplace listings require seller, condition, return-policy, and fitment review."
    ),
    CONFIRMED_BY_USER(
        "Confirmed by user",
        "The user recorded that fitment was verified outside this app."
    )
}

enum class QuoteStatus(val label: String) {
    SAVED_CATALOG("Saved catalog price — not live"),
    MANUAL_LINK("Manual retailer link"),
    LIVE_AUTHORIZED("Live authorized quote")
}

data class PriceQuoteRecord(
    val retailer: RetailerSource,
    val partNumber: String,
    val itemPrice: Double? = null,
    val shippingCost: Double? = null,
    val coreCharge: Double? = null,
    val sellerName: String? = null,
    val capturedAtMillis: Long? = null,
    val quoteStatus: QuoteStatus = QuoteStatus.MANUAL_LINK,
    val fitmentEvidence: FitmentEvidence = FitmentEvidence.VIN_REQUIRED,
    val listingUrl: String? = null
) {
    val deliveredTotal: Double?
        get() = itemPrice?.plus(shippingCost ?: 0.0)?.plus(coreCharge ?: 0.0)
}

data class WeeklyPriceWatch(
    val partId: String,
    val partNumber: String,
    val enabledRetailers: Set<RetailerSource>,
    val intervalDays: Int = 7,
    val configuredAtMillis: Long = System.currentTimeMillis(),
    val lastReviewMillis: Long? = null,
    val enabled: Boolean = true
) {
    val nextReviewMillis: Long
        get() = (lastReviewMillis ?: configuredAtMillis) + intervalDays * 24L * 60L * 60L * 1000L
}
