package com.example.model

import com.example.data.SportTracData
import com.example.util.MaterialResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProceduralModelRegressionTest {
    @Test
    fun materialHighlight_isZeroWhenSurfaceReflectsAwayFromLight() {
        assertEquals(
            0f,
            MaterialResponse.specularHighlight(
                reflectZ = -0.25f,
                metallicFactor = 1f,
                roughnessFactor = 0f
            ),
            0.000001f
        )
    }

    @Test
    fun materialHighlight_increasesWithMetallicFactor() {
        val nonMetal = MaterialResponse.specularHighlight(0.92f, 0f, 0.25f)
        val metal = MaterialResponse.specularHighlight(0.92f, 1f, 0.25f)

        assertTrue("metallic material should produce a stronger highlight", metal > nonMetal)
    }

    @Test
    fun materialHighlight_isBroaderForRougherMaterialAtModerateReflection() {
        val smooth = MaterialResponse.specularHighlight(0.72f, 0.8f, 0.1f)
        val rough = MaterialResponse.specularHighlight(0.72f, 0.8f, 0.9f)

        assertTrue("roughness should broaden the response at moderate reflection", rough > smooth)
    }

    @Test
    fun sourceComponentRegistry_hasUniqueIds() {
        val ids = SportTracData.components.map { it.id }
        assertEquals("component IDs must be unique", ids.size, ids.toSet().size)
    }

    @Test
    fun correctedAcPressureControlsRecord_hasDistinctId() {
        val pressureControls = SportTracData.components.first {
            it.name.contains("pressure", ignoreCase = true) &&
                it.name.contains("compressor", ignoreCase = true)
        }

        assertEquals("ac_compressor_pressure_controls", pressureControls.id)
    }
}
