package com.example.data

import com.example.model.CountryOfOriginClaim
import com.example.model.PartItem
import com.example.model.PartsRankingPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class PartStoreCatalogRankingTest {

    @Test
    fun premiumChoicesAreTheDefaultRankingPreference() {
        assertEquals(PartsRankingPreference.PREMIUM_CHOICES, PartsRankingPreference.entries.first())
    }

    @Test
    fun professionalSnapOnToolIsFirstWithinPremiumChoices() {
        val genericTool = part("Generic Tools", "Specialty Tools", "tool_generic")
        val snapOnTool = part("Snap-on", "Specialty Tools", "tool_snap_on")
        val ranked = PartStoreCatalogRanking.sort(
            listOf(genericTool, snapOnTool),
            PartsRankingPreference.PREMIUM_CHOICES
        )

        assertEquals(snapOnTool, ranked.first())
    }

    @Test
    fun verifiedAmericanMadeClaimOutranksUndisclosedOriginOnlyWhenRequested() {
        val undisclosed = part("Premium Brand", "Filters", "filter_unknown")
        val verifiedAmericanMade = part(
            brand = "Premium Brand",
            category = "Filters",
            id = "filter_usa",
            origin = CountryOfOriginClaim.VERIFIED_MADE_IN_USA
        )
        val ranked = PartStoreCatalogRanking.sort(
            listOf(undisclosed, verifiedAmericanMade),
            PartsRankingPreference.AMERICAN_MADE
        )

        assertEquals(verifiedAmericanMade, ranked.first())
    }

    private fun part(
        brand: String,
        category: String,
        id: String,
        origin: CountryOfOriginClaim = CountryOfOriginClaim.NOT_DISCLOSED
    ): PartItem = PartItem(
        id = id,
        componentId = id,
        partName = id,
        brand = brand,
        partNumber = id,
        oreillyCommercialPrice = 10.0,
        oreillyRetailPrice = 12.0,
        category = category,
        countryOfOrigin = origin
    )
}
