package com.example.data

import com.example.model.Component3DModel
import com.example.model.MaintenanceScheduleItem

/**
 * Owner-guide overlay on packaged Sport Trac data.
 * Live UI/Mentor/cache paths must read this object, not raw SportTracData.
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
                "fuel_filter" -> item.copy(fluidTypeOrSpec = "Motorcraft FG-1036 (2004 OG)")
                "transfer_case" -> item.copy(fluidTypeOrSpec = "1.3 qt Motorcraft MERCON ATF (XT-2-QDX) per 2004 OG")
                "front_axle" -> item.copy(fluidTypeOrSpec = "1.8 qt Motorcraft SAE 80W-90 (4x4 OG)")
                "rear_axle" -> item.copy(fluidTypeOrSpec = "75W-90 FE 5.5-5.8 pt + XL-7; lifetime unless leak/service/water")
                "battery" -> item.copy(fluidTypeOrSpec = "Motorcraft BXT-65-650 (2004 OG)")
                "pcv" -> item.copy(fluidTypeOrSpec = "Motorcraft EV-243 (2004 OG)")
                else -> item
            }
        }

    val components: List<Component3DModel> = SportTracData.components.map { component ->
        component.copy(
            description = component.description
                .replace("MERCON V fluid reservoir", "MERCON ATF reservoir (2004 OG)")
                .replace("75W-140", "75W-90 FE (2004 OG)"),
            torqueSpecs = component.torqueSpecs.map { spec ->
                spec.copy(
                    notes = spec.notes
                        .replace("Use MERCON V ATF fluid", "2004 OG: MERCON ATF (XT-2-QDX), 1.3 qt — not MERCON V")
                        .replace("Uses 1.5 quarts MERCON V ATF", "2004 OG: 1.3 qt MERCON ATF, not MERCON V")
                        .replace("75W-140", "75W-90 FE")
                )
            },
            requiredTools = component.requiredTools.map { tool ->
                tool.replace("MERCON V ATF (1.5 Qts)", "MERCON ATF 1.3 qt (2004 OG)")
                    .replace("75W-140 Synthetic Gear Oil (2.5 Qts)", "75W-90 FE synthetic 5.5-5.8 pints")
                    .replace("Ford XL-3 Friction Modifier Additive (4 oz)", "Ford XL-7 Friction Modifier Additive (4 oz)")
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
                        .replace("2.5 qts 75W-140 synthetic gear oil + 4oz XL-3",
                            "5.5-5.8 pints 75W-90 FE synthetic + 4 oz XL-7")
                        .replace("75W-140", "75W-90 FE")
                )
            }
        )
    }
}
