package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.Model3DScreen
import com.example.ui.screens.PartsShoppingScreen
import com.example.ui.screens.RepairManualScreen
import com.example.ui.components.VoiceControlOverlay
import com.example.ui.theme.SportTracTheme
import com.example.ui.viewmodel.ExplorerViewModel
import com.example.ui.viewmodel.MainTab
import com.example.util.VoiceCommandManager

class MainActivity : ComponentActivity() {

    private val viewModel: ExplorerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SportTracTheme {
                val voiceCommandManager = remember { VoiceCommandManager(applicationContext) }

                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val activeSystem by viewModel.activeSystem.collectAsStateWithLifecycle()
                val selectedComponent by viewModel.selectedComponent.collectAsStateWithLifecycle()
                val filteredComponents by viewModel.filteredComponents.collectAsStateWithLifecycle()
                val searchQuery by viewModel.manualSearchQuery.collectAsStateWithLifecycle()
                val vehicleProfile by viewModel.vehicleProfile.collectAsStateWithLifecycle()
                val maintenanceLogs by viewModel.maintenanceLogs.collectAsStateWithLifecycle()
                val requestDetailSheetOpen by viewModel.requestDetailSheetOpen.collectAsStateWithLifecycle()
                val voiceNotice by viewModel.voiceNotice.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isGeminiThinking by viewModel.isGeminiThinking.collectAsStateWithLifecycle()
                val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
                val commercialAccount by viewModel.commercialAccount.collectAsStateWithLifecycle()
                val cartSortOption by viewModel.cartSortOption.collectAsStateWithLifecycle()
                val orderSuccessNotice by viewModel.orderSuccessNotice.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0F172A),
                    topBar = {
                        Column(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                        ) {
                            Surface(
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF1E293B))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DirectionsCar,
                                            contentDescription = "Ford Explorer",
                                            tint = Color(0xFFFF6F00),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "FORD EXPLORER",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = Color(0xFFFF6F00)
                                            )
                                            Text(
                                                text = "2004 Sport Trac 4.0L SOHC",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF0284C7))
                                    ) {
                                        Text(
                                            text = "3D MANUAL",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Voice Command Bar Overlay
                            VoiceControlOverlay(
                                voiceCommandManager = voiceCommandManager,
                                voiceNotice = voiceNotice,
                                onExecuteCommand = { spokenText ->
                                    viewModel.processVoiceCommand(spokenText)
                                },
                                onDismissNotice = {
                                    viewModel.dismissVoiceNotice()
                                }
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("main_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == MainTab.VIEW_3D,
                                onClick = { viewModel.setTab(MainTab.VIEW_3D) },
                                icon = { Icon(Icons.Default.ViewInAr, contentDescription = "3D Model") },
                                label = { Text("3D Model") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF38BDF8),
                                    indicatorColor = Color(0xFF0284C7),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("tab_3d_model")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.REPAIR_MANUAL,
                                onClick = { viewModel.setTab(MainTab.REPAIR_MANUAL) },
                                icon = { Icon(Icons.Default.MenuBook, contentDescription = "Manual") },
                                label = { Text("Manual") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF38BDF8),
                                    indicatorColor = Color(0xFF0284C7),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("tab_manual")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.DIAGNOSTICS,
                                onClick = { viewModel.setTab(MainTab.DIAGNOSTICS) },
                                icon = { Icon(Icons.Default.Psychology, contentDescription = "Diagnostics") },
                                label = { Text("Diagnostics") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF38BDF8),
                                    indicatorColor = Color(0xFF0284C7),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("tab_diagnostics")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.MAINTENANCE,
                                onClick = { viewModel.setTab(MainTab.MAINTENANCE) },
                                icon = { Icon(Icons.Default.Speed, contentDescription = "Maintenance") },
                                label = { Text("Schedule") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF38BDF8),
                                    indicatorColor = Color(0xFF0284C7),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("tab_maintenance")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.PARTS_CART,
                                onClick = { viewModel.setTab(MainTab.PARTS_CART) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (cartItems.isNotEmpty()) {
                                                Badge(
                                                    containerColor = Color(0xFF10B981),
                                                    contentColor = Color.White
                                                ) {
                                                    Text("${cartItems.sumOf { it.quantity }}")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = "Parts Cart")
                                    }
                                },
                                label = { Text("Parts Cart") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF10B981),
                                    indicatorColor = Color(0xFF059669),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("tab_parts_cart")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            MainTab.VIEW_3D -> Model3DScreen(
                                components = filteredComponents,
                                selectedComponent = selectedComponent,
                                activeSystem = activeSystem,
                                requestDetailSheetOpen = requestDetailSheetOpen,
                                onClearDetailSheetRequest = { viewModel.clearDetailSheetRequest() },
                                onSelectSystem = { sys -> viewModel.setSystemFilter(sys) },
                                onSelectComponent = { comp -> viewModel.selectComponent(comp) },
                                onAddToCart = { comp -> viewModel.addPartForComponent(comp) }
                            )

                            MainTab.REPAIR_MANUAL -> RepairManualScreen(
                                components = filteredComponents,
                                activeSystem = activeSystem,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { q -> viewModel.setManualSearchQuery(q) },
                                onSelectSystem = { sys -> viewModel.setSystemFilter(sys) },
                                onSelectComponent = { comp -> viewModel.selectComponent(comp) },
                                onAddToCart = { comp -> viewModel.addPartForComponent(comp) }
                            )

                            MainTab.DIAGNOSTICS -> DiagnosticsScreen(
                                chatMessages = chatMessages,
                                isThinking = isGeminiThinking,
                                onSendMessage = { text -> viewModel.sendDiagnosticQuery(text) },
                                onClearChat = { viewModel.clearChatHistory() },
                                onNavigateToComponent = { compId -> viewModel.selectComponentById(compId) }
                            )

                            MainTab.MAINTENANCE -> MaintenanceScreen(
                                vehicleProfile = vehicleProfile,
                                maintenanceLogs = maintenanceLogs,
                                onUpdateMileage = { miles -> viewModel.updateMileage(miles) },
                                onLogService = { log -> viewModel.logMaintenance(log) },
                                onDeleteLog = { id -> viewModel.deleteLog(id) }
                            )

                            MainTab.PARTS_CART -> PartsShoppingScreen(
                                cartItems = cartItems,
                                commercialAccount = commercialAccount,
                                cartSortOption = cartSortOption,
                                orderSuccessNotice = orderSuccessNotice,
                                onSortOptionChange = { sort -> viewModel.setCartSortOption(sort) },
                                onUpdateQuantity = { id, q -> viewModel.updateCartItemQuantity(id, q) },
                                onUpdateFulfillment = { id, ful -> viewModel.updateCartItemFulfillment(id, ful) },
                                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                onAddPartToCart = { part -> viewModel.addPartToCart(part) },
                                onCheckout = { method -> viewModel.checkoutOrder(method) },
                                onDismissSuccessNotice = { viewModel.dismissOrderSuccessNotice() }
                            )
                        }
                    }
                }
            }
        }
    }
}
