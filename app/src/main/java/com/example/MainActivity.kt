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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.screens.LoungeScreen
import com.example.ui.theme.SportTracTheme
import com.example.ui.viewmodel.ExplorerViewModel
import com.example.ui.viewmodel.MainTab

// Keep this true until the merged APK has passed the physical-device protocol.
private const val SAFE_SHELL_MODE = true

class MainActivity : ComponentActivity() {

    /**
     * Track 3: Android creates this ViewModel lazily. The Lounge-first composition below does
     * not resolve it; it is reached only from the explicit 3D tab callback.
     */
    private val explorerViewModel: ExplorerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SportTracTheme {
                var selectedTab by remember { mutableStateOf(MainTab.LOUNGE) }

                SafeLaunchShell(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        // This is the only root route allowed to open Room-backed feature data.
                        if (tab == MainTab.VIEW_3D) {
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

/**
 * Track 1/3 cold-launch root. It composes LoungeScreen() with no ViewModel, Room, voice,
 * preference, navigation, or coroutine dependency. Feature routes remain isolated for device
 * testing while SAFE_SHELL_MODE is true.
 */
@Composable
private fun SafeLaunchShell(
    selectedTab: MainTab,
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
                else -> RouteUnderReviewScreen(
                    tab = selectedTab,
                    onReturnToLounge = onReturnToLounge
                )
            }
        }
    }
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

/**
 * During physical-device isolation, disabled routes state their status plainly instead of
 * constructing a feature screen that could repeat the observed crash loop.
 */
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
                    text = if (SAFE_SHELL_MODE) {
                        "This function is temporarily under physical-device review while the crash loop is isolated. It is not being presented as ready."
                    } else {
                        "This function is not yet enabled for this build."
                    },
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
