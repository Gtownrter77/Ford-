package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.SportTracData
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.navigation.FeatureRoutePolicy
import com.example.ui.components.MentorDock
import com.example.ui.components.SafeSceneLoadGate

/**
 * 3D hub. Canvas stays gated. After Load, Mentor sits on the selected part.
 */
@Composable
fun SafeProcedural3DRoute(
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
    var sceneAuthorized by rememberSaveable { mutableStateOf(false) }
    val catalogSize = SportTracData.components.size

    if (!sceneAuthorized) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("safe_procedural_3d_gate"),
            contentAlignment = Alignment.Center
        ) {
            SafeSceneLoadGate(
                availableComponentCount = catalogSize,
                onLoadSafeScene = {
                    sceneAuthorized = true
                    onSafeSceneAuthorized()
                }
            )
        }
        return
    }

    val visibleComponents = FeatureRoutePolicy.boundedSceneComponents(
        selectedComponent = selectedComponent,
        activeSystem = activeSystem
    )
    val focused = selectedComponent ?: visibleComponents.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("safe_procedural_3d_scene")
    ) {
        Model3DScreen(
            components = visibleComponents,
            selectedComponent = focused,
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
            component = focused,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp, start = 12.dp, end = 58.dp)
                .fillMaxWidth()
        )
    }
}
