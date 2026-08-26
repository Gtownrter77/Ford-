package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.SportTracData
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.navigation.FeatureRoutePolicy
import com.example.ui.components.SafeSceneLoadGate

/**
 * 3D tab entry. The full Canvas is not composed until the user taps
 * "Load safe interactive scene". The scene then receives a bounded
 * component list so the first frame cannot draw all 56 parts.
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

    Model3DScreen(
        components = visibleComponents,
        selectedComponent = selectedComponent ?: visibleComponents.firstOrNull(),
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
        modifier = modifier.testTag("safe_procedural_3d_scene")
    )
}
