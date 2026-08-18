package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.RetailerSearchLinks
import com.example.model.FitmentEvidence
import com.example.model.PartItem
import com.example.model.PriceQuoteRecord
import com.example.model.QuoteStatus
import com.example.model.RetailerSource
import com.example.model.RecommendationIntegrity

private val watchRetailers = listOf(
    RetailerSource.OREILLY_PRO,
    RetailerSource.ROCKAUTO,
    RetailerSource.AMAZON,
    RetailerSource.EBAY,
    RetailerSource.FACEBOOK_MARKETPLACE,
    RetailerSource.OTHER_ONLINE
)

@Composable
fun PriceWatchDialog(
    part: PartItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("weekly_price_watch", Context.MODE_PRIVATE)
    }
    val savedRetailers = remember(part.id) {
        preferences.getStringSet("retailers_${part.id}", emptySet())
            ?.mapNotNull { name -> RetailerSource.entries.firstOrNull { it.name == name } }
            ?.toSet()
            ?: emptySet()
    }
    var enabledRetailers by remember(part.id) {
        mutableStateOf(if (savedRetailers.isEmpty()) watchRetailers.toSet() else savedRetailers)
    }
    var watchEnabled by remember(part.id) {
        mutableStateOf(preferences.getBoolean("enabled_${part.id}", false))
    }
    var savedNotice by remember { mutableStateOf<String?>(null) }
    val quotes = remember(part) {
        RecommendationIntegrity.sortedForReview(savedQuoteRecords(part))
    }
    val recommendedQuote = quotes.firstOrNull { it.deliveredTotal != null }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFE2E8F0),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Weekly Price Watch", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "${part.brand} • ${part.partNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFF0C2B3F),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(11.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "This saves a seven-day review plan for this exact part number. It does not scrape a site, place an order, or confirm fitment. Retailer links open a manual verification search until an authorized live data source is connected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE0F2FE)
                        )
                    }
                }

                Text(
                    text = "RETAILERS TO REVIEW EACH WEEK",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFFBAE6FD)
                )
                watchRetailers.forEach { retailer ->
                    RetailerWatchRow(
                        retailer = retailer,
                        isSelected = retailer in enabledRetailers,
                        onToggle = {
                            enabledRetailers = if (retailer in enabledRetailers) enabledRetailers - retailer else enabledRetailers + retailer
                        },
                        onOpenSearch = {
                            RetailerSearchLinks.searchUrl(retailer, part.partNumber)?.let { url ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                    )
                }

                Text(
                    text = "SAVED COMPARISON EVIDENCE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFFFDE68A)
                )
                Text(
                    text = "These are saved catalog records from the app, not a current online quote. Delivered totals include the saved shipping/core values when present.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )
                quotes.forEach { quote ->
                    QuoteEvidenceRow(
                        quote = quote,
                        isRecommendedQuote = quote == recommendedQuote
                    )
                }

                Surface(
                    color = Color(0xFF2A1D06),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "Comparison order is determined by fitment evidence, quote verification status, delivered total, and retailer name. Commission, referral, and payment data are not part of the ranking. Verify the exact listing, condition, shipping, core charge, and return policy before relying on any saved record.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFF7ED)
                        )
                    }
                }

                savedNotice?.let { notice ->
                    Surface(color = Color(0xFF064E3B), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(notice, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    preferences.edit()
                        .putBoolean("enabled_${part.id}", watchEnabled.not())
                        .putStringSet("retailers_${part.id}", enabledRetailers.map { it.name }.toSet())
                        .putLong("configured_${part.id}", System.currentTimeMillis())
                        .apply()
                    watchEnabled = !watchEnabled
                    savedNotice = if (watchEnabled) {
                        "Weekly review enabled for ${enabledRetailers.size} retailer sources."
                    } else {
                        "Weekly review paused. Your retailer selections were kept."
                    }
                },
                enabled = enabledRetailers.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = if (watchEnabled) Color(0xFFB45309) else Color(0xFF0284C7)),
                modifier = Modifier.testTag("weekly_price_watch_toggle")
            ) {
                Icon(if (watchEnabled) Icons.Default.Close else Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (watchEnabled) "Pause Weekly Watch" else "Enable Weekly Watch")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color(0xFF38BDF8))
            ) { Text("Done") }
        }
    )
}

@Composable
private fun RetailerWatchRow(
    retailer: RetailerSource,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onOpenSearch: () -> Unit
) {
    val hasLink = RetailerSearchLinks.searchUrl(retailer, "part") != null
    Surface(
        color = if (isSelected) Color(0xFF1E3A5F) else Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(retailer.displayName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(retailer.sourceType, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            }
            if (hasLink) {
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = "Open exact part-number search",
                    tint = Color(0xFF7DD3FC),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onOpenSearch() }
                        .testTag("price_watch_open_${retailer.name}")
                )
            }
        }
    }
}

@Composable
private fun QuoteEvidenceRow(
    quote: PriceQuoteRecord,
    isRecommendedQuote: Boolean
) {
    Surface(
        color = if (isRecommendedQuote) Color(0xFF064E3B) else Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isRecommendedQuote) Color(0xFF10B981) else Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(quote.retailer.displayName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(
                    text = quote.deliveredTotal?.let { "$${"%.2f".format(it)} saved total" } ?: "Manual check needed",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isRecommendedQuote) Color(0xFF6EE7B7) else Color(0xFFFDE68A)
                )
            }
            Text(quote.quoteStatus.label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            Text(quote.fitmentEvidence.label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFBAE6FD))
        }
    }
}

private fun savedQuoteRecords(part: PartItem): List<PriceQuoteRecord> {
    fun savedCompetitor(retailer: RetailerSource, catalogName: String, evidence: FitmentEvidence): PriceQuoteRecord {
        val quote = part.competitorPrices.firstOrNull { it.storeName.contains(catalogName, ignoreCase = true) }
        return PriceQuoteRecord(
            retailer = retailer,
            partNumber = part.partNumber,
            itemPrice = quote?.price,
            shippingCost = quote?.shippingCost,
            coreCharge = if (retailer == RetailerSource.OREILLY_PRO) part.coreDeposit else null,
            sellerName = quote?.notes,
            quoteStatus = if (quote == null) QuoteStatus.MANUAL_LINK else QuoteStatus.SAVED_CATALOG,
            fitmentEvidence = evidence,
            listingUrl = RetailerSearchLinks.searchUrl(retailer, part.partNumber)
        )
    }

    return listOf(
        PriceQuoteRecord(
            retailer = RetailerSource.OREILLY_PRO,
            partNumber = part.partNumber,
            itemPrice = part.oreillyCommercialPrice,
            coreCharge = part.coreDeposit,
            quoteStatus = QuoteStatus.SAVED_CATALOG,
            fitmentEvidence = FitmentEvidence.VIN_REQUIRED,
            listingUrl = RetailerSearchLinks.searchUrl(RetailerSource.OREILLY_PRO, part.partNumber)
        ),
        savedCompetitor(RetailerSource.ROCKAUTO, "RockAuto", FitmentEvidence.PART_NUMBER_MATCH),
        savedCompetitor(RetailerSource.AMAZON, "Amazon", FitmentEvidence.SELLER_AND_FITMENT_REVIEW),
        savedCompetitor(RetailerSource.EBAY, "eBay", FitmentEvidence.SELLER_AND_FITMENT_REVIEW),
        PriceQuoteRecord(
            retailer = RetailerSource.FACEBOOK_MARKETPLACE,
            partNumber = part.partNumber,
            quoteStatus = QuoteStatus.MANUAL_LINK,
            fitmentEvidence = FitmentEvidence.SELLER_AND_FITMENT_REVIEW,
            listingUrl = RetailerSearchLinks.searchUrl(RetailerSource.FACEBOOK_MARKETPLACE, part.partNumber)
        ),
        PriceQuoteRecord(
            retailer = RetailerSource.OTHER_ONLINE,
            partNumber = part.partNumber,
            quoteStatus = QuoteStatus.MANUAL_LINK,
            fitmentEvidence = FitmentEvidence.VIN_REQUIRED
        )
    )
}
