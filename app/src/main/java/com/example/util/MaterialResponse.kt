package com.example.util

import kotlin.math.pow

/**
 * Deterministic material-aware highlight approximation used by the procedural
 * Compose Canvas renderer. This is not a physically based BRDF or GPU shader.
 */
object MaterialResponse {
    fun specularHighlight(
        reflectZ: Float,
        metallicFactor: Float,
        roughnessFactor: Float
    ): Float {
        val metallic = metallicFactor.coerceIn(0f, 1f)
        val roughness = roughnessFactor.coerceIn(0f, 1f)
        val highlightPower = (20f - roughness * 14f).coerceIn(6f, 20f)
        val highlightStrength = (0.14f + metallic * 0.52f) * (1f - roughness * 0.35f)
        return if (reflectZ > 0f) {
            reflectZ.coerceAtMost(1.5f).pow(highlightPower) * highlightStrength
        } else {
            0f
        }
    }
}

private fun Float.pow(exponent: Float): Float =
    this.toDouble().pow(exponent.toDouble()).toFloat()
