package com.example.model

enum class FulfillmentType(val displayName: String, val badgeColorHex: Long) {
    LOCAL_PICKUP("Local Store Pickup (Ready in 15 mins)", 0xFF10B981),
    ONLINE_DELIVERY("Express Commercial Delivery (Same Day)", 0xFF0284C7)
}

data class CompetitorPrice(
    val storeName: String, // "AutoZone", "Advance Auto Parts", "RockAuto", "NAPA"
    val price: Double,
    val shippingCost: Double = 0.0,
    val notes: String = "Retail Price"
) {
    val totalPrice: Double get() = price + shippingCost
}

data class PartItem(
    val id: String,
    val componentId: String,
    val partName: String,
    val brand: String,
    val partNumber: String,
    val oreillyCommercialPrice: Double,
    val oreillyRetailPrice: Double,
    val inStockLocalStore: Boolean = true,
    val storeStockCount: Int = 5,
    val storeLocation: String = "O'Reilly Store #1428 - 1204 Main St (2.4 mi)",
    val competitorPrices: List<CompetitorPrice> = emptyList(),
    val category: String = "OEM Replacement",
    val warranty: String = "Limited Lifetime Warranty",
    val coreDeposit: Double = 0.0,
    val isLoanerTool: Boolean = false,
    val countryOfOrigin: CountryOfOriginClaim = CountryOfOriginClaim.NOT_DISCLOSED
) {
    val commercialDiscountPct: Int
        get() = if (oreillyRetailPrice > 0) {
            (((oreillyRetailPrice - oreillyCommercialPrice) / oreillyRetailPrice) * 100).toInt()
        } else 0

    val cheapestCompetitor: CompetitorPrice?
        get() = competitorPrices.minByOrNull { it.totalPrice }

    val savingsVersusRetail: Double
        get() = (oreillyRetailPrice - oreillyCommercialPrice).coerceAtLeast(0.0)
}

data class CartItem(
    val part: PartItem,
    var quantity: Int = 1,
    var fulfillment: FulfillmentType = FulfillmentType.LOCAL_PICKUP
) {
    val itemTotal: Double get() = part.oreillyCommercialPrice * quantity
}

enum class CartSortOption(val displayName: String) {
    CHEAPEST_FIRST("Cheapest First ($ → $$$)"),
    HIGHEST_PRICE("Highest Price ($$$ → $)"),
    NAME_AZ("Part Name (A-Z)"),
    HIGHEST_SAVINGS("Biggest Commercial Savings")
}

data class OreillyCommercialAccount(
    val companyName: String = "Commercial price reference",
    val accountNumber: String = "Private account not connected",
    val tierLevel: String = "Manual retailer verification required",
    val discountDescription: String = "Saved values are not live quotes",
    val assignedHubStore: String = "Sign in to your retailer account to confirm local fulfillment",
    val hubAddress: String = "Not stored in the app",
    val commercialHotline: String = "Use your private retailer contact information",
    val accountManager: String = "Not stored in the app",
    val creditLimit: Double = 0.0,
    val currentBalance: Double = 0.0,
    val paymentTerms: String = "No payment or credit information is stored here"
)
