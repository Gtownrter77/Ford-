package com.example.data

import com.example.model.CountryOfOriginClaim
import com.example.model.PartItem
import com.example.model.PartsRankingPreference

/**
 * Customer-selected catalog presentation. This is deliberately independent of
 * retailer tracking, affiliate relationships, accounts, or payment data.
 */
object PartStoreCatalogRanking {
    private val premiumBrands = setOf(
        "Motorcraft", "Bosch", "Gates", "Moog", "Fel-Pro", "WIX", "Goodyear",
        "Pioneer", "Sylvania", "3M", "Snap-on"
    )

    private val preferredProfessionalToolBrands = listOf("Snap-on")

    fun sort(parts: List<PartItem>, preference: PartsRankingPreference): List<PartItem> =
        when (preference) {
            PartsRankingPreference.PREMIUM_CHOICES -> parts.sortedWith(
                compareByDescending<PartItem> { premiumScore(it) }
                    .thenBy { it.partName }
            )
            PartsRankingPreference.BEST_VERIFIED_FIT -> parts.sortedBy { it.partName }
            PartsRankingPreference.LOWEST_DELIVERED_TOTAL -> parts.sortedBy { it.oreillyCommercialPrice }
            PartsRankingPreference.FASTEST_AVAILABILITY -> parts.sortedByDescending { it.inStockLocalStore }
            PartsRankingPreference.BEST_WARRANTY -> parts.sortedByDescending { warrantyScore(it.warranty) }
            PartsRankingPreference.LOCAL_PICKUP -> parts.sortedByDescending { it.inStockLocalStore }
            PartsRankingPreference.MARKETPLACE_USED -> parts.sortedBy { it.partName }
            PartsRankingPreference.AMERICAN_MADE -> parts.sortedByDescending {
                it.countryOfOrigin == CountryOfOriginClaim.VERIFIED_MADE_IN_USA
            }
        }

    private fun premiumScore(part: PartItem): Int {
        val brandScore = when {
            part.brand.equals("Snap-on", ignoreCase = true) && part.category.contains("Tool", ignoreCase = true) -> 3
            part.brand in premiumBrands -> 2
            part.warranty.contains("Lifetime", ignoreCase = true) -> 1
            else -> 0
        }
        val preferredToolPosition = preferredProfessionalToolBrands.indexOfFirst {
            part.brand.equals(it, ignoreCase = true)
        }
        return brandScore + if (preferredToolPosition == 0 && part.category.contains("Tool", ignoreCase = true)) 10 else 0
    }

    private fun warrantyScore(warranty: String): Int = when {
        warranty.contains("Lifetime", ignoreCase = true) -> 3
        warranty.contains("Limited", ignoreCase = true) -> 2
        warranty.isNotBlank() -> 1
        else -> 0
    }
}
