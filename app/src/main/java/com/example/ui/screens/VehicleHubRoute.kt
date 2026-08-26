package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.SportTracData
import com.example.data.VehicleAsset
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.navigation.FeatureRoutePolicy
import com.example.ui.components.InteractiveRepairViewer
import com.example.ui.components.MentorDock
import com.example.ui.components.OnModelInstructionOverlay

@Composable
fun VehicleHubRoute(
    selectedComponent: Component3DModel?,
    activeSystem: VehicleSystem,
    requestDetailSheetOpen: Boolean,
    cached3DCount: Int,
    cachedManualsCount: Int,
    cachedSymptomsCount: Int,
    onClearDetailSheetRequest: () -> Unit,
    onSelectSystem: (VehicleSystem) -> Unit,
    onSelectComponent: (Component3DModel) -> Unit,
    onAddToCart: ((Component3DModel) -> Unit)?,
    onReSyncOfflineCache: () -> Unit,
    onClearOfflineCache: () -> Unit,
    onSafeSceneAuthorized: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val glbReady = remember { VehicleAsset.isWreckGlbPresent(context) }
    var showSchematicPractice by rememberSaveable { mutableStateOf(false) }
    val focused = selectedComponent ?: SportTracData.components.firstOrNull()

    if (glbReady) {
        Box(modifier = modifier.fillMaxSize().testTag("vehicle_hub_filament")) {
            InteractiveRepairViewer(
                catalogAssetPath = VehicleAsset.PARTS_CATALOG_PATH,
                onExit = {},
                onPartTap = { partId ->
                    SportTracData.components.firstOrNull { it.id == partId }?.let(onSelectComponent)
                },
                modifier = Modifier.fillMaxSize()
            )
            MentorDock(
                component = focused,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 88.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth()
            )
            OnModelInstructionOverlay(
                component = focused,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
        return
    }

    if (!showSchematicPractice) {
        Box(modifier = modifier.fillMaxSize()) {
            VehicleAccuracyBlockedScreen(
                onOpenSchematicPractice = {
                    onSafeSceneAuthorized()
                    showSchematicPractice = true
                },
                modifier = Modifier.fillMaxSize()
            )
            OnModelInstructionOverlay(
                component = focused,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
        return
    }

    val visibleComponents = FeatureRoutePolicy.boundedSceneComponents(
        selectedComponent = selectedComponent,
        activeSystem = activeSystem
    )
    val schematicFocus = selectedComponent ?: visibleComponents.firstOrNull()

    Box(modifier = modifier.fillMaxSize().testTag("vehicle_hub_schematic_practice")) {
        Model3DScreen(
            components = visibleComponents,
            selectedComponent = schematicFocus,
            activeSystem = activeSystem,
            cached3DCount = cached3DCount,
            cachedManualsCount = cachedManualsCount,
            cachedSymptomsCount = cachedSymptomsCount,
            requestDetailSheetOpen = requestDetailSheetOpen,
            onClearDetailSheetRequest = onClearDetailSheetRequest,
            onSelectSystem = onSelectSystem,
            onSelectComponent = onSelectComponent,
            onAddToCart = onAddToCart,
            onReSyncOfflineCache = onReSyncOfflineCache,
            onClearOfflineCache = onClearOfflineCache,
            modifier = Modifier.fillMaxSize()
        )
        MentorDock(
            component = schematicFocus,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp, start = 12.dp, end = 58.dp)
                .fillMaxWidth()
        )
        OnModelInstructionOverlay(
            component = schematicFocus,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        )
    }
}
