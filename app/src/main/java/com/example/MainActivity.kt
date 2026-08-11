package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.TeamPanelDialog
import com.example.ui.components.MentorVoiceSettingsDialog
import com.example.ui.components.VoiceControlOverlay
import androidx.compose.foundation.clickable
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
                val upcomingTasks by viewModel.upcomingTasks.collectAsStateWithLifecycle()
                val requestDetailSheetOpen by viewModel.requestDetailSheetOpen.collectAsStateWithLifecycle()
                val voiceNotice by viewModel.voiceNotice.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isGeminiThinking by viewModel.isGeminiThinking.collectAsStateWithLifecycle()
                val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
                val commercialAccount by viewModel.commercialAccount.collectAsStateWithLifecycle()
                val cartSortOption by viewModel.cartSortOption.collectAsStateWithLifecycle()
                val orderSuccessNotice by viewModel.orderSuccessNotice.collectAsStateWithLifecycle()
                val skillLevel by viewModel.skillLevel.collectAsStateWithLifecycle()
                val isTeamPanelOpen by viewModel.isTeamPanelOpen.collectAsStateWithLifecycle()
                val isVoiceSettingsOpen by viewModel.isVoiceSettingsOpen.collectAsStateWithLifecycle()
                val cached3DCount by viewModel.cached3DAssetsCount.collectAsStateWithLifecycle()
                val cachedManualsCount by viewModel.cachedManualsCount.collectAsStateWithLifecycle()
                val cachedSymptomsCount by viewModel.cachedSymptomsCount.collectAsStateWithLifecycle()
                val cacheManifest by viewModel.cacheManifest.collectAsStateWithLifecycle()

                if (isTeamPanelOpen) {
                    TeamPanelDialog(
                        currentSkillLevel = skillLevel,
                        onSkillLevelChange = { level -> viewModel.setSkillLevel(level) },
                        onDismiss = { viewModel.closeTeamPanel() }
                    )
                }

                if (isVoiceSettingsOpen) {
                    MentorVoiceSettingsDialog(
                        cached3DCount = cached3DCount,
                        cachedManualsCount = cachedManualsCount,
                        cachedSymptomsCount = cachedSymptomsCount,
                        cacheManifest = cacheManifest,
                        onForceUpdate = { viewModel.resyncOfflineCache() },
                        onUpgradeContent = { viewModel.upgradeOfflineCache() },
                        onCheckForUpgrades = { viewModel.checkForCacheUpgrades() },
                        onClearCache = { viewModel.clearOfflineCache() },
                        onDismiss = { viewModel.closeVoiceSettings() }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0F172A),
                    topBar = {
                        Column(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                        ) {
                            val voiceState by voiceCommandManager.voiceState.collectAsState()

                            Surface(
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF1E293B))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DirectionsCar,
                                            contentDescription = "Ford Explorer",
                                            tint = Color(0xFFFF6F00),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "FORD EXPLORER",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp
                                                ),
                                                color = Color(0xFFFF6F00)
                                            )
                                            Text(
                                                text = "2004 Sport Trac 4.0L",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            color = Color(0xFF1E293B),
                                            shape = CircleShape,
                                            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .clickable { viewModel.openVoiceSettings() }
                                                .testTag("btn_open_voice_settings")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.RecordVoiceOver,
                                                    contentDescription = "Mentor Voice Settings",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            color = if (voiceState is com.example.util.VoiceState.Listening) Color(0xFF0284C7) else Color(0xFF1E293B),
                                            shape = CircleShape,
                                            border = BorderStroke(1.dp, if (voiceState is com.example.util.VoiceState.Listening) Color(0xFF38BDF8) else Color(0xFF334155)),
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    if (voiceState is com.example.util.VoiceState.Listening) {
                                                        voiceCommandManager.stopListening()
                                                    } else {
                                                        voiceCommandManager.startListening()
                                                    }
                                                }
                                                .testTag("voice_mic_fab")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Mic,
                                                    contentDescription = "Voice Commands",
                                                    tint = if (voiceState is com.example.util.VoiceState.Listening) Color.White else Color(0xFF38BDF8),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            color = Color(0xFF1E293B),
                                            shape = RoundedCornerShape(20.dp),
                                            border = BorderStroke(1.dp, skillLevel.color),
                                            modifier = Modifier
                                                .clickable { viewModel.openTeamPanel() }
                                                .testTag("btn_open_team_panel")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Group, contentDescription = null, tint = skillLevel.color, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = skillLevel.label.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Voice Notice Toast Overlay (Only floats down when active)
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
                                cached3DCount = cached3DCount,
                                cachedManualsCount = cachedManualsCount,
                                cachedSymptomsCount = cachedSymptomsCount,
                                requestDetailSheetOpen = requestDetailSheetOpen,
                                onClearDetailSheetRequest = { viewModel.clearDetailSheetRequest() },
                                onSelectSystem = { sys -> viewModel.setSystemFilter(sys) },
                                onSelectComponent = { comp -> viewModel.selectComponent(comp) },
                                onAddToCart = { comp -> viewModel.addPartForComponent(comp) },
                                onReSyncOfflineCache = { viewModel.resyncOfflineCache() },
                                onClearOfflineCache = { viewModel.clearOfflineCache() }
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
                                upcomingTasks = upcomingTasks,
                                onUpdateMileage = { miles -> viewModel.updateMileage(miles) },
                                onLogService = { log -> viewModel.logMaintenance(log) },
                                onDeleteLog = { id -> viewModel.deleteLog(id) },
                                onAddUpcomingTask = { task -> viewModel.addUpcomingTask(task) },
                                onCompleteUpcomingTask = { task, miles, cost, notes -> viewModel.completeUpcomingTask(task, miles, cost, notes) },
                                onDeleteUpcomingTask = { taskId -> viewModel.deleteUpcomingTask(taskId) }
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
