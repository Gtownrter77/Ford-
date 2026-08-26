package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.navigation.FeatureRoutePolicy
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.LoungeScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.PartsShoppingScreen
import com.example.ui.screens.RepairManualScreen
import com.example.ui.screens.SafeProcedural3DRoute
import com.example.ui.theme.SportTracTheme
import com.example.ui.viewmodel.ExplorerViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {

    /**
     * ViewModel is created lazily by the Activity. Lounge composition does not
     * read it. Feature data still stays closed until a route that needs Room
     * is selected, or until the user authorizes the safe 3D scene.
     */
    private val explorerViewModel: ExplorerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SportTracTheme {
                var selectedTab by remember { mutableStateOf(MainTab.LOUNGE) }

                FeatureLaunchShell(
                    selectedTab = selectedTab,
                    viewModel = explorerViewModel,
                    onTabSelected = { tab ->
                        if (FeatureRoutePolicy.requiresFeatureData(tab)) {
                            explorerViewModel.ensureFeatureData()
                        }
                        selectedTab = tab
                    },
                    onReturnToLounge = { selectedTab = MainTab.LOUNGE }
                )
            }
        }
    }
}

@Composable
private fun FeatureLaunchShell(
    selectedTab: MainTab,
    viewModel: ExplorerViewModel,
    onTabSelected: (MainTab) -> Unit,
    onReturnToLounge: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0F172A),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                SafeTabButton(MainTab.LOUNGE, selectedTab, "Lounge", Icons.Default.Home, "tab_lounge", onTabSelected)
                SafeTabButton(MainTab.VIEW_3D, selectedTab, "3D Model", Icons.Default.ViewInAr, "tab_3d_model", onTabSelected)
                SafeTabButton(MainTab.REPAIR_MANUAL, selectedTab, "Manual", Icons.Default.MenuBook, "tab_manual", onTabSelected)
                SafeTabButton(MainTab.DIAGNOSTICS, selectedTab, "Diagnostics", Icons.Default.Psychology, "tab_diagnostics", onTabSelected)
                SafeTabButton(MainTab.MAINTENANCE, selectedTab, "Schedule", Icons.Default.Speed, "tab_maintenance", onTabSelected)
                SafeTabButton(MainTab.PARTS_CART, selectedTab, "Parts", Icons.Default.ShoppingCart, "tab_parts_cart", onTabSelected)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.LOUNGE -> LoungeScreen()
                MainTab.VIEW_3D -> {
                    if (!FeatureRoutePolicy.isEnabled(selectedTab)) {
                        RouteUnderReviewScreen(selectedTab, onReturnToLounge)
                    } else {
                        SafeProcedural3DHost(viewModel = viewModel)
                    }
                }
                MainTab.REPAIR_MANUAL -> {
                    val components by viewModel.filteredComponents.collectAsState()
                    val activeSystem by viewModel.activeSystem.collectAsState()
                    val searchQuery by viewModel.manualSearchQuery.collectAsState()
                    RepairManualScreen(
                        components = components,
                        activeSystem = activeSystem,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::setManualSearchQuery,
                        onSelectSystem = viewModel::setSystemFilter,
                        onSelectComponent = viewModel::selectComponent,
                        onAddToCart = viewModel::addPartForComponent
                    )
                }
                MainTab.DIAGNOSTICS -> {
                    val chatMessages by viewModel.chatMessages.collectAsState()
                    val isThinking by viewModel.isGeminiThinking.collectAsState()
                    DiagnosticsScreen(
                        chatMessages = chatMessages,
                        isThinking = isThinking,
                        onSendMessage = viewModel::sendDiagnosticQuery,
                        onClearChat = viewModel::clearChatHistory,
                        onNavigateToComponent = viewModel::selectComponentById,
                        onRetryLastQuery = viewModel::retryLastGeminiQuery
                    )
                }
                MainTab.MAINTENANCE -> {
                    val profile by viewModel.vehicleProfile.collectAsState()
                    val logs by viewModel.maintenanceLogs.collectAsState()
                    val tasks by viewModel.upcomingTasks.collectAsState()
                    MaintenanceScreen(
                        vehicleProfile = profile,
                        maintenanceLogs = logs,
                        upcomingTasks = tasks,
                        onUpdateMileage = viewModel::updateMileage,
                        onLogService = viewModel::logMaintenance,
                        onDeleteLog = viewModel::deleteMaintenanceLog,
                        onAddUpcomingTask = viewModel::addUpcomingTask,
                        onCompleteUpcomingTask = viewModel::completeUpcomingTask,
                        onDeleteUpcomingTask = viewModel::deleteUpcomingTask
                    )
                }
                MainTab.PARTS_CART -> {
                    val cartItems by viewModel.cartItems.collectAsState()
                    val commercialAccount by viewModel.commercialAccount.collectAsState()
                    val cartSortOption by viewModel.cartSortOption.collectAsState()
                    val orderSuccessNotice by viewModel.orderSuccessNotice.collectAsState()
                    PartsShoppingScreen(
                        cartItems = cartItems,
                        commercialAccount = commercialAccount,
                        cartSortOption = cartSortOption,
                        orderSuccessNotice = orderSuccessNotice,
                        onSortOptionChange = viewModel::setCartSortOption,
                        onUpdateQuantity = viewModel::updateCartItemQuantity,
                        onUpdateFulfillment = viewModel::updateCartItemFulfillment,
                        onRemoveItem = viewModel::removeFromCart,
                        onAddPartToCart = { viewModel.addPartToCart(it) },
                        onDismissSuccessNotice = viewModel::dismissOrderSuccessNotice
                    )
                }
            }
        }
    }
}

@Composable
private fun SafeProcedural3DHost(viewModel: ExplorerViewModel) {
    val selectedComponent by viewModel.selectedComponent.collectAsState()
    val activeSystem by viewModel.activeSystem.collectAsState()
    val requestDetailSheetOpen by viewModel.requestDetailSheetOpen.collectAsState()
    val cached3DCount by viewModel.cached3DAssetsCount.collectAsState()
    val cachedManualsCount by viewModel.cachedManualsCount.collectAsState()
    val cachedSymptomsCount by viewModel.cachedSymptomsCount.collectAsState()

    SafeProcedural3DRoute(
        selectedComponent = selectedComponent,
        activeSystem = activeSystem,
        requestDetailSheetOpen = requestDetailSheetOpen,
        cached3DCount = cached3DCount,
        cachedManualsCount = cachedManualsCount,
        cachedSymptomsCount = cachedSymptomsCount,
        onClearDetailSheetRequest = viewModel::clearDetailSheetRequest,
        onSelectSystem = viewModel::setSystemFilter,
        onSelectComponent = viewModel::selectComponent,
        onAddToCart = viewModel::addPartForComponent,
        onReSyncOfflineCache = viewModel::resyncOfflineCache,
        onClearOfflineCache = viewModel::clearOfflineCache,
        onSafeSceneAuthorized = viewModel::ensureFeatureData
    )
}

@Composable
private fun RowScope.SafeTabButton(
    tab: MainTab,
    selectedTab: MainTab,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBarItem(
        selected = selectedTab == tab,
        onClick = { onTabSelected(tab) },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color(0xFFF59E0B),
            indicatorColor = Color(0xFF92400E),
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
        ),
        modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun RouteUnderReviewScreen(
    tab: MainTab,
    onReturnToLounge: () -> Unit
) {
    val title = when (tab) {
        MainTab.VIEW_3D -> "3D Practice Model"
        MainTab.REPAIR_MANUAL -> "Repair Manual"
        MainTab.DIAGNOSTICS -> "Diagnostics"
        MainTab.MAINTENANCE -> "Service Schedule"
        MainTab.PARTS_CART -> "Part Store"
        MainTab.LOUNGE -> "Lounge"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF182231),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.testTag("route_under_review")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFBBF24))
                Spacer(modifier = Modifier.height(12.dp))
                Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This function is not enabled in the current route policy.",
                    color = Color(0xFFCBD5E1),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Color(0xFF0284C7),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .clickable(onClick = onReturnToLounge)
                        .testTag("return_to_lounge_btn")
                ) {
                    Text(
                        "Return to Lounge",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
