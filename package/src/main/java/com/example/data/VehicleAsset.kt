package com.example.data

import android.content.Context

/**
 * Licensed complete-vehicle GLB. Not a wreck model.
 */
object VehicleAsset {
    const val GLB_PATH = "models/ford_explorer_sport_trac_2004.glb"
    const val PARTS_CATALOG_PATH = "parts_data.json"
    const val DISPLAY_NAME = "2004 Ford Explorer Sport Trac"

    fun isGlbPresent(context: Context): Boolean = runCatching {
        context.assets.open(GLB_PATH).close()
        true
    }.getOrDefault(false)
}
