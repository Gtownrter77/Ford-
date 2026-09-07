package com.example.data

import com.example.model.Component3DModel
import com.example.model.MaintenanceScheduleItem
import com.example.model.VehicleSystem

/**
 * Owner-guide overlay on packaged Sport Trac data.
 * Live UI/Mentor/cache paths must read this object, not raw SportTracData.
 */
object PublishedSportTracData {
    val vehicleOverviewSpecs: Map<String, String> =
        SportTracData.vehicleOverviewSpecs + OwnerGuideSpecs.overview

    private val extraOgSchedules: List<MaintenanceScheduleItem> = listOf(
        MaintenanceScheduleItem("fuel_filter", "Fuel Filter Replacement", VehicleSystem.ENGINE, 30000, 24, "Motorcraft FG-1036 (2004 OG)", "Owner Guide fuel-filter number. Confirm under-hood VECI if it differs.", "engine_block"),
        MaintenanceScheduleItem("transfer_case", "Transfer Case Fluid Service", VehicleSystem.TRANSMISSION, 60000, 48, "1.3 qt Motorcraft MERCON ATF (XT-2-QDX) per 2004 OG", "Official OG is MERCON ATF, not MERCON V. Fill to filler-hole bottom. Shop substitute MERCON V only when XT-2 cannot be sourced.", "transfer_case"),
        MaintenanceScheduleItem("front_axle", "Front Axle Fluid Inspection", VehicleSystem.BRAKES_CHASSIS, 60000, 48, "1.8 qt Motorcraft SAE 80W-90 (4x4 OG)", "4x4 front axle OG fill. Inspect for leaks before treating as a routine drain.", "front_diff"),
        MaintenanceScheduleItem("rear_axle", "Rear Axle Inspection", VehicleSystem.BRAKES_CHASSIS, 0, 12, "75W-90 FE 5.5-5.8 pt; lifetime unless leak/service/water", "2004 OG: lubricated for life unless leak, service, or water submersion. Add XL-7 4 oz on Traction-Lok refill. Fill 6-14 mm below hole.", "rear_diff"),
        MaintenanceScheduleItem("battery", "Battery Inspection", VehicleSystem.ELECTRICAL, 0, 12, "Motorcraft BXT-65-650 (2004 OG)", "Owner Guide battery group. Load-test before replacing on age alone.", "alternator_ignition"),
        MaintenanceScheduleItem("pcv", "PCV Valve Inspection", VehicleSystem.ENGINE, 60000, 48, "Motorcraft EV-243 (2004 OG)", "Owner Guide PCV number for the 4.0L SOHC.", "engine_block")
    )

    val defaultMaintenanceSchedules: List<MaintenanceScheduleItem> =
        SportTracData.defaultMaintenanceSchedules.map { item ->
            when (item.id) {
                "air_filter" -> item.copy(fluidTypeOrSpec = "Motorcraft FA-1744 (2004 OG)")
                "coolant_flush" -> item.copy(fluidTypeOrSpec = "Motorcraft Premium Gold VC-7-A, 14.0 qt (2004 OG)")
                "spark_plugs" -> item.copy(fluidTypeOrSpec = "Motorcraft AGSF-22PP gap 0.052-0.056 in (2004 OG)")
                else -> item
            }
        }.let { mapped ->
            val existing = mapped.map { it.id }.toSet()
            mapped + extraOgSchedules.filter { it.id !in existing }
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
