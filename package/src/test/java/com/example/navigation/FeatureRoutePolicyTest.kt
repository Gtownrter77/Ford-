package com.example.navigation

import com.example.data.SportTracData
import com.example.model.VehicleSystem
import com.example.ui.viewmodel.MainTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureRoutePolicyTest {

    @Test
    fun loungeDoesNotOpenFeatureData() {
        assertFalse(FeatureRoutePolicy.requiresFeatureData(MainTab.LOUNGE))
        assertFalse(FeatureRoutePolicy.requiresFeatureData(MainTab.VIEW_3D))
        assertFalse(FeatureRoutePolicy.requiresFeatureData(MainTab.PARTS_CART))
        assertTrue(FeatureRoutePolicy.requiresFeatureData(MainTab.MAINTENANCE))
        assertTrue(FeatureRoutePolicy.requiresFeatureData(MainTab.REPAIR_MANUAL))
    }

    @Test
    fun allFilterBoundsSceneToOneComponent() {
        val selected = SportTracData.components.first()
        val bounded = FeatureRoutePolicy.boundedSceneComponents(
            selectedComponent = selected,
            activeSystem = VehicleSystem.ALL
        )
        assertEquals(1, bounded.size)
        assertEquals(selected.id, bounded.single().id)
    }

    @Test
    fun systemFilterCapsAtEightComponents() {
        val bounded = FeatureRoutePolicy.boundedSceneComponents(
            selectedComponent = SportTracData.components.firstOrNull(),
            activeSystem = VehicleSystem.ENGINE
        )
        assertTrue(bounded.size <= FeatureRoutePolicy.SAFE_SCENE_COMPONENT_CAP)
        assertTrue(bounded.all { it.system == VehicleSystem.ENGINE })
    }
}
