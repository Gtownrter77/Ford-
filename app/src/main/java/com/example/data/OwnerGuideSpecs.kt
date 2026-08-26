package com.example.data

/**
 * Official 2004 Explorer Sport Trac Owner Guide numbers only.
 * Source: 2004 P207 Owners Guide USA English (04p27og1e/2e/3e.pdf).
 * Hub: https://www.ford.com/support/owner-manuals-details/explorer-sport-trac/2004
 * Not a workshop manual. Do not treat these as repair torque sequences.
 * Shop substitutes are labeled separately from printed OG specs.
 */
object OwnerGuideSpecs {
    const val SOURCE = "2004 P207 Explorer Sport Trac Owners Guide (post-2002-fmt) USA English"

    const val OIL_CAPACITY = "5.0 quarts (4.7 L) including filter"
    const val OIL_SPEC = "Motorcraft SAE 5W-30 Super Premium (XO-5W30-QSP)"
    const val OIL_FILTER = "FL-820S"
    const val AIR_FILTER = "FA-1744"
    const val FUEL_FILTER = "FG-1036"
    const val BATTERY = "BXT-65-650"
    const val PCV_VALVE = "EV-243"
    const val SPARK_PLUG = "AGSF-22PP"
    const val SPARK_PLUG_GAP = "1.3-1.4 mm (0.052-0.056 in)"
    const val COOLANT_CAPACITY = "14.0 quarts (13.2 L)"
    const val COOLANT_SPEC = "Motorcraft Premium Gold Engine Coolant (yellow), VC-7-A"
    const val FUEL_TANK = "22.5 gallons (85.2 L)"
    const val TRANS_4X4_DRY_FILL = "10.3 quarts (9.8 L) approximate dry-fill; set by dipstick"
    const val TRANS_FLUID = "Motorcraft MERCON V ATF (XT-5-QM)"
    const val TRANSFER_CASE_CAPACITY = "1.3 quarts (1.2 L)"
    const val TRANSFER_CASE_FLUID =
        "Official 2004 OG: Motorcraft MERCON ATF (XT-2-QDX), not MERCON V. XT-2 is discontinued. Shop substitute when XT-2 cannot be sourced: MERCON V (XT-5)."
    const val POWER_STEERING_FLUID =
        "Official 2004 OG: Motorcraft MERCON ATF; fill to reservoir line. Shop substitute: MERCON V."
    const val FLUID_COMMON_SENSE =
        "5R55E takes MERCON V. Transfer case and power steering officially take original MERCON (XT-2-QDX). Ford printed do-not-mix. XT-2 is gone, so shops commonly use MERCON V there. Label official vs substitute."
    const val FRONT_AXLE = "1.8 quarts Motorcraft SAE 80W-90 (4x4)"
    const val REAR_AXLE = "5.5-5.8 pints Motorcraft SAE 75W-90 Fuel Efficient High Performance Synthetic"
    const val TRACTION_LOK_ADDITIVE = "118 ml (4 oz) XL-7 / EST-M2C118-A on complete Traction-Lok refill"
    const val LUG_NUT = "1/2-20: 84-114 lb-ft (113-153 Nm); retighten at 500 miles"
    const val FIRING_ORDER = "1-4-2-5-3-6"
    const val COMPRESSION_RATIO = "9.7:1"
    const val REQUIRED_FUEL = "87 octane unleaded or E85 max"

    val overview: Map<String, String> = mapOf(
        "Oil Capacity" to "$OIL_CAPACITY $OIL_SPEC + $OIL_FILTER",
        "Cooling System Capacity" to "$COOLANT_CAPACITY $COOLANT_SPEC",
        "Transmission Fluid" to "$TRANS_FLUID $TRANS_4X4_DRY_FILL",
        "Transfer Case Fluid" to "$TRANSFER_CASE_CAPACITY $TRANSFER_CASE_FLUID",
        "Power Steering Fluid" to POWER_STEERING_FLUID,
        "Fuel Tank Capacity" to FUEL_TANK,
        "Spark Plug Gap" to "$SPARK_PLUG $SPARK_PLUG_GAP",
        "Air Filter" to AIR_FILTER,
        "Wheel Lug Nut Torque" to LUG_NUT
    )
}
