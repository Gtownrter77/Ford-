package com.example.navigation

import com.example.data.SportTracData
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import com.example.ui.viewmodel.MainTab

/**
 * Per-route enablement for the 2026-08-26 increment.
 *
 * This replaces the all-or-nothing [SAFE_SHELL_MODE] switch. Lounge stays the
 * cold-launch surface. Text and list routes are live. The procedural 3D Canvas
 * is reachable only after an explicit safe-scene load and is capped to a
 * bounded component set.
 */
object FeatureRoutePolicy {
    const val SAFE_SCENE_COMPONENT_CAP = 8

    fun isEnabled(tab: MainTab): Boolean = when (tab) {
        MainTab.LOUNGE -> true
        MainTab.REPAIR_MANUAL -> true
        MainTab.MAINTENANCE -> true
        MainTab.DIAGNOSTICS -> true
        MainTab.PARTS_CART -> true
        MainTab.VIEW_3D -> true
    }

    fun requiresFeatureData(tab: MainTab): Boolean = when (tab) {
        MainTab.LOUNGE -> false
        MainTab.PARTS_CART -> false
        MainTab.VIEW_3D -> false
        MainTab.REPAIR_MANUAL -> true
        MainTab.MAINTENANCE -> true
        MainTab.DIAGNOSTICS -> false
    }

    /**
     * First interactive 3D frame must not receive the full 56-part catalog.
     * ALL filter: selected component only. System filter: up to eight parts.
     */
    fun boundedSceneComponents(
        allComponents: List<Component3DModel> = SportTracData.components,
        selectedComponent: Component3DModel?,
        activeSystem: VehicleSystem
    ): List<Component3DModel> {
        if (activeSystem != VehicleSystem.ALL) {
            return allComponents
                .filter { it.system == activeSystem }
                .take(SAFE_SCENE_COMPONENT_CAP)
        }
        val focused = selectedComponent ?: allComponents.firstOrNull()
        return listOfNotNull(focused)
    }
}
