package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportTracPartsCatalog
import com.example.data.PartStoreCatalogRanking
import com.example.data.PartsReadinessPackage
import com.example.data.PartsReadinessTier
import com.example.data.SportTracPartsReadiness
import com.example.ui.components.PriceWatchDialog
import com.example.ui.components.ReadinessDashboardDialog
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartsShoppingScreen(
    cartItems: List<CartItem>,
    commercialAccount: OreillyCommercialAccount,
    cartSortOption: CartSortOption,
    orderSuccessNotice: String?,
    onSortOptionChange: (CartSortOption) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onUpdateFulfillment: (String, FulfillmentType) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddPartToCart: (PartItem) -> Unit,
    onDismissSuccessNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPartsListReviewDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showBusinessInfoDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var comparisonPart by remember { mutableStateOf<PartItem?>(null) }
    var showReadinessDashboard by remember { mutableStateOf(false) }
    var catalogRanking by remember { mutableStateOf(PartsRankingPreference.PREMIUM_CHOICES) }
    var catalogRankingMenuOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val readinessPreferences = remember { context.getSharedPreferences("weekly_price_watch", android.content.Context.MODE_PRIVATE) }
    val readinessPackages = remember { SportTracPartsReadiness.packages }
    val acReadinessPackage = remember { SportTracPartsReadiness.defaultWatchPackage }
    val allReadinessPartIds = remember { SportTracPartsReadiness.allPreparedPartIds }
    val pendingReadinessCount = remember { readinessPackages.sumOf { it.pendingFitmentItems.size } }
    var acReadinessEnabled by remember {
        mutableStateOf(acReadinessPackage.partIds.all { readinessPreferences.getBoolean("enabled_$it", false) })
    }
    var allReadinessEnabled by remember {
        mutableStateOf(allReadinessPartIds.all { readinessPreferences.getBoolean("enabled_$it", false) })
    }
    var readinessNotice by remember { mutableStateOf<String?>(null) }

    // Sort Cart Items according to current sort option (DEFAULT: CHEAPEST FIRST)
    val sortedCartItems = remember(cartItems, cartSortOption) {
        when (cartSortOption) {
            CartSortOption.CHEAPEST_FIRST -> cartItems.sortedBy { it.part.oreillyCommercialPrice }
            CartSortOption.HIGHEST_PRICE -> cartItems.sortedByDescending { it.part.oreillyCommercialPrice }
            CartSortOption.NAME_AZ -> cartItems.sortedBy { it.part.partName }
            CartSortOption.HIGHEST_SAVINGS -> cartItems.sortedByDescending { it.part.savingsVersusRetail }
        }
    }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.itemTotal } }
    val totalSavings = remember(cartItems) {
        cartItems.sumOf { it.part.savingsVersusRetail * it.quantity }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. TOP HEADER: O'REILLY COMMERCIAL ACCOUNT & STORE VERIFICATION
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF008000).copy(alpha = 0.2f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Box(modifier = Modifier.padding(8.dp)) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "O'REILLY AUTO PARTS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF10B981),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PRIVATE PART STORE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Private Parts Store & Readiness",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // VIN Decoder & Fitment Check Button
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier
                                .clickable { showBusinessInfoDialog = true }
                                .testTag("btn_vin_fitment")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Fitment: verify VIN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        // Business Info Button
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier
                                .clickable { showBusinessInfoDialog = true }
                                .testTag("btn_business_info")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Privacy & Fitment",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Commercial Account Bar Details
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = commercialAccount.companyName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${commercialAccount.tierLevel} • ${commercialAccount.discountDescription}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Private in-app planning only",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = commercialAccount.assignedHubStore,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Order Receipt Alert Banner
        if (orderSuccessNotice != null) {
            Surface(
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = orderSuccessNotice,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismissSuccessNotice) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                    }
                }
            }
        }

        // 2. SORTING & FULFILLMENT CONTROL BAR (DEFAULT: CHEAPEST FIRST)
        Surface(
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sort, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sort:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CartSortOption.values().forEach { option ->
                        val isSelected = cartSortOption == option
                        Surface(
                            color = if (isSelected) Color(0xFFFF6F00) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) Color.White else Color(0xFF334155)),
                            modifier = Modifier
                                .clickable { onSortOptionChange(option) }
                                .testTag("sort_${option.name}")
                        ) {
                            Text(
                                text = when (option) {
                                    CartSortOption.CHEAPEST_FIRST -> "Cheapest ⚡"
                                    CartSortOption.HIGHEST_PRICE -> "Highest $"
                                    CartSortOption.NAME_AZ -> "A-Z"
                                    CartSortOption.HIGHEST_SAVINGS -> "Max Savings"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. MAIN CONTENT: CART ITEMS & QUICK ADD PARTS CATALOG
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ReadinessLibraryCard(
                    packages = readinessPackages,
                    acPackage = acReadinessPackage,
                    acEnabled = acReadinessEnabled,
                    allEnabled = allReadinessEnabled,
                    allPartCount = allReadinessPartIds.size,
                    pendingCount = pendingReadinessCount,
                    notice = readinessNotice,
                    onOpenDashboard = { showReadinessDashboard = true },
                    onEnableAcWatch = {
                        val now = System.currentTimeMillis()
                        val editor = readinessPreferences.edit()
                        acReadinessPackage.partIds.forEach { partId ->
                            editor.putBoolean("enabled_$partId", true)
                            editor.putStringSet("retailers_$partId", acReadinessPackage.defaultWatchRetailers.map { it.name }.toSet())
                            editor.putLong("configured_$partId", now)
                        }
                        editor.apply()
                        acReadinessEnabled = true
                        readinessNotice = "Weekly review enabled for all ${acReadinessPackage.partIds.size} A/C and heat-readiness entries."
                    },
                    onEnableAllWatch = {
                        val now = System.currentTimeMillis()
                        val editor = readinessPreferences.edit()
                        readinessPackages.forEach { readinessPackage ->
                            readinessPackage.partIds.forEach { partId ->
                                editor.putBoolean("enabled_$partId", true)
                                editor.putStringSet("retailers_$partId", readinessPackage.defaultWatchRetailers.map { it.name }.toSet())
                                editor.putLong("configured_$partId", now)
                            }
                        }
                        editor.apply()
                        acReadinessEnabled = true
                        allReadinessEnabled = true
                        readinessNotice = "Weekly review enabled for all ${allReadinessPartIds.size} prepared parts across every system."
                    },
                    onReviewPackage = { readinessPackage ->
                        comparisonPart = SportTracPartsCatalog.catalog.firstOrNull { it.id in readinessPackage.partIds }
                    }
                )
            }

            // Cart Items Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR SHOPPING LIST (${cartItems.sumOf { it.quantity }} ITEMS)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFF38BDF8)
                    )

                    if (totalSavings > 0) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = "Commercial Savings: \$${String.format("%.2f", totalSavings)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            if (cartItems.isEmpty()) {
                item {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Shopping List is Empty",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Click on any 2004 Sport Trac part below or in the 3D Manual to automatically add it to your list!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            } else {
                items(sortedCartItems, key = { it.part.id }) { item ->
                    PartCartItemCard(
                        item = item,
                        onUpdateQuantity = { q -> onUpdateQuantity(item.part.id, q) },
                        onUpdateFulfillment = { ful -> onUpdateFulfillment(item.part.id, ful) },
                        onRemove = { onRemoveItem(item.part.id) }
                    )
                }
            }

            // SECTION 4: O'REILLY PARTS CATALOG (CLICK TO AUTO-ADD)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FAST ADD: 2004 SPORT TRAC PARTS CATALOG",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFFFFD700)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = Color(0xFF0C2B3F),
                            shape = RoundedCornerShape(7.dp),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier
                                .clickable { catalogRankingMenuOpen = true }
                                .testTag("catalog_ranking_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Sort, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(catalogRanking.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFBAE6FD))
                            }
                        }
                        DropdownMenu(
                            expanded = catalogRankingMenuOpen,
                            onDismissRequest = { catalogRankingMenuOpen = false }
                        ) {
                            PartsRankingPreference.entries.forEach { preference ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(preference.label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                            Text(preference.detail, style = MaterialTheme.typography.labelSmall)
                                        }
                                    },
                                    onClick = {
                                        catalogRanking = preference
                                        catalogRankingMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Catalog Quick Add Cards
            val catalogParts = PartStoreCatalogRanking.sort(SportTracPartsCatalog.catalog, catalogRanking)
            items(catalogParts, key = { "cat_${it.id}" }) { part ->
                val isInCart = cartItems.any { it.part.id == part.id }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isInCart) Color(0xFF10B981) else Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddPartToCart(part) }
                        .testTag("catalog_part_${part.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = part.brand,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PN: ${part.partNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Origin: ${part.countryOfOrigin.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (part.countryOfOrigin.isVerifiedAmericanMade) Color(0xFF6EE7B7) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = { comparisonPart = part },
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(26.dp)
                                    .testTag("catalog_compare_watch_${part.id}")
                            ) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compare / Watch", style = MaterialTheme.typography.labelSmall, color = Color(0xFFBAE6FD))
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = part.partName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Saved catalog reference — confirm stock and fitment at retailer",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "\$${String.format("%.2f", part.oreillyCommercialPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "\$${String.format("%.2f", part.oreillyRetailPrice)} Retail",
                                style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.LineThrough),
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { onAddPartToCart(part) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isInCart) Color(0xFF059669) else Color(0xFFFF6F00)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(
                                    if (isInCart) Icons.Default.Check else Icons.Default.AddShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isInCart) "In List (+1)" else "Add", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // 4. BOTTOM CHECKOUT BAR
        if (cartItems.isNotEmpty()) {
            Surface(
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMMERCIAL TOTAL (${cartItems.sumOf { it.quantity }} items)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "\$${String.format("%.2f", subtotal)}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF10B981)
                        )
                    }

                    Button(
                        onClick = { showPartsListReviewDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_checkout")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Review Parts List", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }

    comparisonPart?.let { part ->
        PriceWatchDialog(
            part = part,
            onDismiss = { comparisonPart = null }
        )
    }

    if (showReadinessDashboard) {
        ReadinessDashboardDialog(
            onDismiss = { showReadinessDashboard = false },
            onReviewPackage = { readinessPackage ->
                showReadinessDashboard = false
                comparisonPart = SportTracPartsCatalog.catalog.firstOrNull { it.id in readinessPackage.partIds }
            }
        )
    }

    // PRIVATE PARTS-LIST REVIEW — NO CHECKOUT OR ORDER SUBMISSION
    if (showPartsListReviewDialog) {
        AlertDialog(
            onDismissRequest = { showPartsListReviewDialog = false },
            containerColor = Color(0xFF0F172A),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Private Parts List Review", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Review your saved list before opening any retailer manually. This app does not submit orders, collect payment, or charge an account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Saved-list reference total:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text("\$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Price-source status:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981))
                                Text("Saved catalog values — verify", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Shipping, core charge, and tax:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text("Confirm at retailer", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Planning total only:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("\$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                            }
                        }
                    }

                    Surface(
                        color = Color(0xFF0C2B3F),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Text(
                            text = "To purchase, open a retailer comparison from the individual part record, verify fitment and the final delivered cost, then complete checkout directly with that retailer. No payment method, account balance, or order details are stored here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE0F2FE),
                            modifier = Modifier.padding(11.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPartsListReviewDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("btn_confirm_list_review")
                ) {
                    Text("Keep List Ready")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPartsListReviewDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // BUSINESS INFORMATION DIALOG
    if (showBusinessInfoDialog) {
        AlertDialog(
            onDismissRequest = { showBusinessInfoDialog = false },
            containerColor = Color(0xFF0F172A),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Private Retailer Handoff", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "FITMENT AND PRIVACY GUIDANCE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD700)
                    )

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Your retailer account, account number, payment information, location, and contact list are not stored in this app.", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Before opening any retailer link, confirm the vehicle VIN, exact part number, condition, shipping, core charge, and return policy.", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("The Part Store prepares the comparison and practice path. Any final checkout happens only on the retailer’s own page after the customer chooses to proceed.", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBAE6FD))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showBusinessInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PartCartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onUpdateFulfillment: (FulfillmentType) -> Unit,
    onRemove: () -> Unit
) {
    var showCompetitors by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.part.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Brand & Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.part.brand,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PN: ${item.part.partNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.part.partName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Savings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\$${String.format("%.2f", item.part.oreillyCommercialPrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "\$${String.format("%.2f", item.part.oreillyRetailPrice)}",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${item.part.commercialDiscountPct}% OFF",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock & Fulfillment Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FulfillmentType.values().forEach { ful ->
                    val isSelected = item.fulfillment == ful
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onUpdateFulfillment(ful) },
                        color = if (isSelected) Color(ful.badgeColorHex).copy(alpha = 0.2f) else Color(0xFF0F172A),
                        border = BorderStroke(1.dp, if (isSelected) Color(ful.badgeColorHex) else Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (ful == FulfillmentType.LOCAL_PICKUP) Icons.Default.Store else Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = if (isSelected) Color(ful.badgeColorHex) else Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (ful == FulfillmentType.LOCAL_PICKUP) "Local Pickup" else "Express Delivery",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // COMPETITOR PRICES COMPARISON EXPANDER
            if (item.part.competitorPrices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCompetitors = !showCompetitors },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Compare Competitor Prices (AutoZone, RockAuto...)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFFD700)
                                )
                            }
                            Icon(
                                if (showCompetitors) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (showCompetitors) {
                            Spacer(modifier = Modifier.height(8.dp))
                            item.part.competitorPrices.forEach { comp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${comp.storeName} (${comp.notes})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                    Text(
                                        text = "\$${String.format("%.2f", comp.totalPrice)}" +
                                                if (comp.shippingCost > 0) " (+\$${comp.shippingCost} ship)" else "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadinessLibraryCard(
    packages: List<PartsReadinessPackage>,
    acPackage: PartsReadinessPackage,
    acEnabled: Boolean,
    allEnabled: Boolean,
    allPartCount: Int,
    pendingCount: Int,
    notice: String?,
    onOpenDashboard: () -> Unit,
    onEnableAcWatch: () -> Unit,
    onEnableAllWatch: () -> Unit,
    onReviewPackage: (PartsReadinessPackage) -> Unit
) {
    Surface(
        color = Color(0xFF0B2440),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("parts_readiness_library")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(9.dp)) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PREBUILT PARTS READINESS LIBRARY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 0.6.sp),
                        color = Color(0xFFBAE6FD)
                    )
                    Text(
                        text = "$allPartCount price-watch-ready parts plus $pendingCount VIN/capacity lookup items across ${packages.size} system packages",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0F2FE)
                    )
                }
                if (allEnabled) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "All system watches enabled", tint = Color(0xFF6EE7B7))
                }
            }

            Text(
                text = "Each package is built from an existing part-number record. Enabling a watch saves a seven-day review schedule with O'Reilly Pro, RockAuto, Amazon, eBay, Facebook Marketplace, and other-source verification options. Live price retrieval remains separately labeled until authorized data access is connected.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )
            OutlinedButton(
                onClick = onOpenDashboard,
                border = BorderStroke(1.dp, Color(0xFF7DD3FC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_readiness_dashboard")
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open All-Inclusive Dashboard", color = Color(0xFFBAE6FD))
            }

            packages.forEach { readinessPackage ->
                val isAc = readinessPackage.id == acPackage.id
                val tierColor = when (readinessPackage.tier) {
                    PartsReadinessTier.READY_NOW -> Color(0xFF38BDF8)
                    PartsReadinessTier.HIGH_PRIORITY -> Color(0xFFF59E0B)
                    PartsReadinessTier.PREPARED_REFERENCE -> Color(0xFF94A3B8)
                }
                Surface(
                    color = if (isAc) Color(0xFF064E3B) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(11.dp),
                    border = BorderStroke(1.dp, if (isAc) Color(0xFF34D399) else Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReviewPackage(readinessPackage) }
                        .testTag("readiness_package_${readinessPackage.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = tierColor.copy(alpha = 0.18f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = readinessPackage.tier.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 8.sp),
                                color = tierColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(readinessPackage.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(
                                text = buildString {
                                    append("${readinessPackage.partIds.size} price-watch records")
                                    if (readinessPackage.pendingFitmentItems.isNotEmpty()) {
                                        append(" · ${readinessPackage.pendingFitmentItems.size} VIN lookup items")
                                    }
                                    append(" · Tap to review first part")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        if (isAc && acEnabled) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "A/C watch enabled", tint = Color(0xFF6EE7B7), modifier = Modifier.size(18.dp))
                        } else {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Review package", tint = Color(0xFF7DD3FC), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onEnableAcWatch,
                    colors = ButtonDefaults.buttonColors(containerColor = if (acEnabled) Color(0xFF047857) else Color(0xFF0284C7)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("enable_ac_readiness_watch")
                ) {
                    Icon(Icons.Default.AcUnit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(if (acEnabled) "A/C Watch Ready" else "Watch A/C First", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onEnableAllWatch,
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("enable_all_readiness_watch")
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFFDE68A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(if (allEnabled) "All Watches Ready" else "Watch Every System", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFDE68A))
                }
            }

            notice?.let { message ->
                Surface(color = Color(0xFF064E3B), shape = RoundedCornerShape(9.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.padding(9.dp)
                    )
                }
            }
        }
    }
}
