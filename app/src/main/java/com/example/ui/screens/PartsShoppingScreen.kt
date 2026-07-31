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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportTracPartsCatalog
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
    onCheckout: (String) -> Unit,
    onDismissSuccessNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("O'Reilly Commercial Line of Credit (Net 30)") }
    var searchQuery by remember { mutableStateOf("") }
    var showBusinessInfoDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

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
                                        text = "COMMERCIAL VERIFIED",
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
                                text = "Commercial Shopping List & Inventory",
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
                                    text = "VIN: 2004 Sport Trac ✓",
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
                                    text = "Store Info",
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
                                text = "${commercialAccount.companyName} (Acc #${commercialAccount.accountNumber})",
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
                                text = "Credit Line: \$${String.format("%.2f", commercialAccount.creditLimit - commercialAccount.currentBalance)}",
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
                    Text(
                        text = "Click part to add",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Catalog Quick Add Cards
            val catalogParts = SportTracPartsCatalog.catalog.sortedBy { it.oreillyCommercialPrice }
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = part.partName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stock Verified at Store #1428 (${part.storeStockCount} available)",
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
                        onClick = { showCheckoutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_checkout")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Checkout / Order Now", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }

    // CHECKOUT DIALOG / IN-APP PAYMENT
    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            containerColor = Color(0xFF0F172A),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("O'Reilly Commercial Checkout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Review commercial order total and select payment option:",
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
                                Text("Parts Subtotal:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text("\$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Commercial Discount Applied:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981))
                                Text("-\$${String.format("%.2f", totalSavings)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Commercial Freight / Tax:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text("\$0.00 (Exempt Line)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                            HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Final Amount Due:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("\$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                            }
                        }
                    }

                    Text("SELECT PAYMENT METHOD", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))

                    val paymentMethods = listOf(
                        "O'Reilly Commercial Line of Credit (Net 30)",
                        "Corporate Credit Card (Visa ending in 4821)",
                        "Google Pay Commercial"
                    )

                    paymentMethods.forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF38BDF8))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(method, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutDialog = false
                        onCheckout(selectedPaymentMethod)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("btn_confirm_checkout")
                ) {
                    Text("Place Order Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCheckoutDialog = false }) {
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
                    Text("O'Reilly Commercial Hub #1428", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Store Address & Business Information:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD700)
                    )

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📍 Location:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("1204 Main Street, Commercial Hub Suite B", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("📞 Commercial Desk Hotline:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("(555) 382-7400 (Ext 3 - Commercial Parts)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("👤 Account Representative:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("Marcus Vance (Sr. Commercial Specialist)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("⏰ Commercial Desk Hours:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("Mon-Fri: 7:00 AM - 6:00 PM | Sat: 8:00 AM - 4:00 PM", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("🚚 Delivery Fleet Dispatch:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("Hot Shot Commercial Delivery Vans (30-45 min local delivery)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981))
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
