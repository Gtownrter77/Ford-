package com.example.data

import android.net.Uri
import com.example.model.RetailerSource

/**
 * Builds review links from an exact replacement part number. These links are a
 * deliberate fallback when a retailer has no connected, authorized price feed.
 * Opening a link never confirms fitment, price, availability, seller identity,
 * condition, or shipping cost.
 */
object RetailerSearchLinks {
    fun searchUrl(retailer: RetailerSource, partNumber: String): String? {
        val query = Uri.encode(partNumber)
        return when (retailer) {
            RetailerSource.OREILLY_PRO -> "https://www.oreillyauto.com/search?q=$query"
            RetailerSource.ROCKAUTO -> "https://www.rockauto.com/en/partsearch/?partnum=$query"
            RetailerSource.AMAZON -> "https://www.amazon.com/s?k=$query"
            RetailerSource.EBAY -> "https://www.ebay.com/sch/i.html?_nkw=$query"
            RetailerSource.FACEBOOK_MARKETPLACE -> "https://www.facebook.com/marketplace/search/?query=$query"
            RetailerSource.OTHER_ONLINE -> null
        }
    }
}
