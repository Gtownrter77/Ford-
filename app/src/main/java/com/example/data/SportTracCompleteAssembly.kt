package com.example.data

import com.example.model.Component3DModel

/**
 * Everything the 3D tab should show for this truck:
 * catalog service parts + meter-true hull + remaining systems + fastener stacks.
 *
 * Not a VIN-complete Ford CAD dump. Hidden TTY head bolts, interior screws,
 * body clips, and harness retainers stay in the workshop manual.
 *
 * Wire ExplorerViewModel.filteredComponents to [components].
 */
object SportTracCompleteAssembly {

    val components: List<Component3DModel> by lazy {
        val merged = (
            SportTracData.components +
                SportTracScaledHull.components +
                SportTracHullExtras.components
            ).distinctBy { it.id }
        merged.map { SportTracFastenerLayer.enrich(it) }
    }

    val hullOnly: List<Component3DModel>
        get() = SportTracFastenerLayer.enrichedHull +
            SportTracHullExtras.components.map { SportTracFastenerLayer.enrich(it) }

    val fastenerPieceCount: Int
        get() = components.sumOf { it.subAssemblies.size }

    val systemCoverage: Set<String>
        get() = components.map { it.system.name }.toSet()
}
