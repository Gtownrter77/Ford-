package com.example.data

import android.content.Context

/**
 * Single source of truth for the licensed wreck GLB.
 * Accuracy and Blender-level graphics are blocked until this file exists.
 */
object VehicleAsset {
    const val WRECK_GLB_PATH = "models/ford_explorer_sport_trac_2004_wreck.glb"
    const val PARTS_CATALOG_PATH = "parts_data.json"
    const val DISPLAY_NAME = "2004 Ford Explorer Sport Trac"

    fun isWreckGlbPresent(context: Context): Boolean = runCatching {
        context.assets.open(WRECK_GLB_PATH).close()
        true
    }.getOrDefault(false)
}
