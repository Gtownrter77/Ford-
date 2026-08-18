package com.example.data

import com.example.model.RetailerSource

enum class PartsReadinessTier(
    val label: String,
    val description: String
) {
    READY_NOW("Ready now", "Preconfigured for immediate comparison and weekly review."),
    HIGH_PRIORITY("High priority", "Safety, no-start, mobility, or heat-management parts worth keeping prepared."),
    PREPARED_REFERENCE("Prepared reference", "Available with part numbers and verification links when a diagnosis points here.")
}

data class PendingFitmentItem(
    val name: String,
    val verificationNeeded: String
)

data class PartsReadinessPackage(
    val id: String,
    val title: String,
    val tier: PartsReadinessTier,
    val description: String,
    val partIds: Set<String>,
    val pendingFitmentItems: List<PendingFitmentItem> = emptyList(),
    val defaultWatchRetailers: Set<RetailerSource> = setOf(
        RetailerSource.OREILLY_PRO,
        RetailerSource.ROCKAUTO,
        RetailerSource.AMAZON,
        RetailerSource.EBAY,
        RetailerSource.FACEBOOK_MARKETPLACE,
        RetailerSource.OTHER_ONLINE
    )
)

/**
 * Curated only from IDs already present in SportTracPartsCatalog. This is a
 * readiness plan, not a claim that a part has failed or that every item fits
 * every VIN/trim without a final catalog check.
 */
object SportTracPartsReadiness {
    val packages: List<PartsReadinessPackage> = listOf(
        PartsReadinessPackage(
            id = "ac_heat_readiness",
            title = "A/C and extreme-heat readiness",
            tier = PartsReadinessTier.READY_NOW,
            description = "The first watch package for no-cooling, weak-airflow, clutch-command, accessory-drive, and heat-management diagnosis. Use the A/C Workbench and fitment verification before ordering a sealed-system part.",
            partIds = setOf(
                "part_ac_compressor_murray",
                "part_heater_core_murray",
                "part_blower_motor_murray",
                "part_blower_resistor_dorman",
                "part_blend_door_actuator_dorman",
                "part_ac_accumulator_murray",
                "part_ac_condenser_murray",
                "part_thermostat_housing_dorman",
                "part_serpentine_belt_gates",
                "part_tool_digital_multimeter"
            ),
            pendingFitmentItems = listOf(
                PendingFitmentItem("A/C hose and service-port seals", "Confirm line routing and fitting style by VIN before part-number lookup."),
                PendingFitmentItem("Pressure-protection switch or sensor", "Confirm the exact control configuration before ordering."),
                PendingFitmentItem("Refrigerant and oil service", "Use the under-hood label and qualified recovery/charging procedure; do not estimate a charge from a generic listing.")
            )
        ),
        PartsReadinessPackage(
            id = "maintenance_ignition_readiness",
            title = "Maintenance and ignition readiness",
            tier = PartsReadinessTier.HIGH_PRIORITY,
            description = "Routine service and common no-start/rough-running support parts.",
            partIds = setOf(
                "part_spark_plug_motorcraft",
                "part_spark_plug_bosch",
                "part_oil_filter_motorcraft",
                "part_oil_wix",
                "part_intake_gasket_felpro",
                "part_timing_tensioner_cloyes",
                "part_tool_fuel_pressure_tester"
            ),
            pendingFitmentItems = listOf(
                PendingFitmentItem("Spark-plug wire set", "Confirm ignition and wire routing configuration by VIN before matching a replacement set."),
                PendingFitmentItem("Engine air filter", "Confirm air-cleaner housing dimensions and catalog fitment before part-number lookup."),
                PendingFitmentItem("Oil-change bundle", "Confirm the engine oil specification, capacity, drain-plug washer, and preferred filter before pricing a bundle."),
                PendingFitmentItem("Starter motor", "Confirm transmission and engine application by VIN before part-number lookup."),
                PendingFitmentItem("Fuse assortment", "Match fuse type and amp rating to the owner manual or circuit label; never substitute a higher rating.")
            )
        ),
        PartsReadinessPackage(
            id = "brake_wheel_safety_readiness",
            title = "Brake, wheel, and safety readiness",
            tier = PartsReadinessTier.HIGH_PRIORITY,
            description = "Parts that affect stopping, wheel retention, tire support, ABS-related hub service, and restraint-system diagnostics.",
            partIds = setOf(
                "part_brake_pads_ceramic",
                "part_front_rotor_vented",
                "part_rear_brake_pads_rotors_bosch",
                "part_brake_master_cylinder_dorman",
                "part_hub_assembly_moog",
                "part_lug_nuts_dorman",
                "part_tire_goodyear_adventure",
                "part_wheel_oe_aluminum",
                "part_tool_brake_bleeder",
                "part_tool_spring_compressor",
                "part_airbag_clockspring_dorman",
                "part_airbag_front_crash_sensor",
                "part_airbag_seatbelt_pretensioner"
            )
        ),
        PartsReadinessPackage(
            id = "mobility_drivetrain_readiness",
            title = "4WD, drivetrain, and road-readiness",
            tier = PartsReadinessTier.HIGH_PRIORITY,
            description = "Parts that can leave the truck without four-wheel-drive, reliable charging, or dependable exhaust/emissions operation.",
            partIds = setOf(
                "part_4x4_shift_motor",
                "part_trans_filter_wix",
                "part_alternator_ultrapower",
                "part_catback_muffler_walker",
                "part_o2_sensor_bosch",
                "part_tool_exhaust_cutter"
            ),
            pendingFitmentItems = listOf(
                PendingFitmentItem("Fuel-pump module", "Confirm fuel-system configuration and tank application by VIN before part-number lookup."),
                PendingFitmentItem("Water pump", "Confirm accessory-drive and engine application by VIN before part-number lookup."),
                PendingFitmentItem("Radiator and upper/lower hoses", "Confirm transmission-cooler and hose-routing configuration before ordering."),
                PendingFitmentItem("Engine coolant", "Confirm coolant specification and total fill capacity from the owner/workshop documentation.")
            )
        ),
        PartsReadinessPackage(
            id = "visibility_electrical_readiness",
            title = "Visibility and electrical readiness",
            tier = PartsReadinessTier.HIGH_PRIORITY,
            description = "Lighting, wipers, and electrical repair parts that can immediately affect visibility or legal road use.",
            partIds = setOf(
                "part_headlight_bulbs_motorcraft",
                "part_headlight_assembly_anzo",
                "part_wiring_harness_dorman",
                "part_front_wiper_blades_bosch",
                "part_dash_fuse_block_dorman",
                "part_dash_wiring_repair_pigtails"
            )
        ),
        PartsReadinessPackage(
            id = "body_cabin_reference",
            title = "Cabin, roof, rear-window, and audio reference",
            tier = PartsReadinessTier.PREPARED_REFERENCE,
            description = "Prebuilt part records for water management, cabin comfort, rear-window, dash, sunroof, and audio repairs when the model points to them.",
            partIds = setOf(
                "part_instrument_cluster_dorman",
                "part_cluster_led_bulbs_sylvania",
                "part_sunroof_weatherstrip_seal",
                "part_sunroof_drive_motor",
                "part_sunroof_drain_cleaner_kit",
                "part_windshield_urethane_3m",
                "part_rear_window_regulator_dorman",
                "part_radio_head_unit",
                "part_door_speakers_6x8",
                "part_radio_wiring_harness",
                "part_tool_din_keys",
                "part_subwoofer_pioneer"
            )
        )
    )

    val defaultWatchPackage: PartsReadinessPackage
        get() = packages.first { it.id == "ac_heat_readiness" }

    val allPreparedPartIds: Set<String>
        get() = packages.flatMap { it.partIds }.toSet()
}
