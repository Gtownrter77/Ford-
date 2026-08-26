package com.example.data

import com.example.model.Component3DModel
import com.example.model.MaintenanceScheduleItem

/**
 * Owner-guide overlay on packaged Sport Trac data.
 * Use this from UI/Mentor paths so stale 15.3 / FA-1695 / MERCON V transfer-case
 * strings in SportTracData cannot reach the user.
 */
object PublishedSportTracData {
    val vehicleOverviewSpecs: Map<String, String> =
        SportTracData.vehicleOverviewSpecs + OwnerGuideSpecs.overview

    val defaultMaintenanceSchedules: List<MaintenanceScheduleItem> =
        SportTracData.defaultMaintenanceSchedules.map { item ->
            when (item.id) {
                "air_filter" -> item.copy(fluidTypeOrSpec = "Motorcraft FA-1744 (2004 OG)")
                "coolant_flush" -> item.copy(fluidTypeOrSpec = "Motorcraft Premium Gold VC-7-A, 14.0 qt (2004 OG)")
                "spark_plugs" -> item.copy(fluidTypeOrSpec = "Motorcraft AGSF-22PP gap 0.052-0.056 in (2004 OG)")
                else -> item
            }
        }

    val components: List<Component3DModel> = SportTracData.components.map { component ->
        component.copy(
            description = component.description.replace(
                "MERCON V fluid reservoir",
                "MERCON ATF reservoir (2004 OG)"
            ),
            torqueSpecs = component.torqueSpecs.map { spec ->
                spec.copy(
                    notes = spec.notes
                        .replace("Use MERCON V ATF fluid", "2004 OG: MERCON ATF (XT-2-QDX), 1.3 qt — not MERCON V")
                        .replace("Uses 1.5 quarts MERCON V ATF", "2004 OG: 1.3 qt MERCON ATF, not MERCON V")
                )
            },
            repairSteps = component.repairSteps.map { step ->
                step.copy(
                    instruction = step.instruction
                        .replace(
                            "drain 1.5 quarts of MERCON V fluid.",
                            "drain fluid. 2004 OG refill is 1.3 quarts Motorcraft MERCON ATF (XT-2-QDX), not MERCON V."
                        )
                        .replace(
                            "drain/refill transfer case with 1.5 qts fresh MERCON V ATF.",
                            "drain/refill transfer case with 1.3 qt Motorcraft MERCON ATF (XT-2-QDX) per 2004 OG."
                        )
                )
            }
        )
    }
}
