package com.example.model

enum class PartsRankingPreference(
    val label: String,
    val detail: String
) {
    PREMIUM_CHOICES("Built for life", "Professional-grade, durable choices with the strongest recorded warranty first; premium brands follow as a tie-breaker."),
    BEST_VERIFIED_FIT("Best verified fit", "Fitment evidence and quote verification first, then delivered total."),
    LOWEST_DELIVERED_TOTAL("Lowest delivered total", "Saved item price, shipping, and core charge first; fitment warnings stay visible."),
    FASTEST_AVAILABILITY("Fastest availability", "Verified local or availability fields first when a source provides them."),
    BEST_WARRANTY("Best warranty", "Recorded warranty coverage first; verify final terms with the seller before purchase."),
    LOCAL_PICKUP("Local pickup", "Known local pickup availability first."),
    MARKETPLACE_USED("Marketplace / used", "Marketplace listings first; seller, condition, and fitment review remain required."),
    AMERICAN_MADE("American Made", "Verified U.S.-origin claim first; “assembled” and unknown claims remain distinct.")
}

enum class CountryOfOriginClaim(
    val label: String,
    val isVerifiedAmericanMade: Boolean
) {
    VERIFIED_MADE_IN_USA("Made in USA — verified", true),
    VERIFIED_ASSEMBLED_IN_USA("Assembled in USA — verified", false),
    U_S_CONTENT_CLAIM("U.S. content claim — verify", false),
    NOT_DISCLOSED("Origin not disclosed", false)
}
