package com.example.data

import com.example.data.local.AcousticReferenceDao
import com.example.data.local.AcousticReferenceEntity
import kotlinx.coroutines.flow.Flow

class AcousticDiagnosticRepository(private val dao: AcousticReferenceDao) {

    val allReferenceSounds: Flow<List<AcousticReferenceEntity>> = dao.getAllReferenceSounds()
    val totalSoundCount: Flow<Int> = dao.getSoundCount()

    fun getSoundsByCategory(category: String): Flow<List<AcousticReferenceEntity>> {
        return dao.getSoundsByCategory(category)
    }

    fun searchSounds(query: String): Flow<List<AcousticReferenceEntity>> {
        return dao.searchSounds(query)
    }

    suspend fun getSoundById(id: String): AcousticReferenceEntity? {
        return dao.getSoundById(id)
    }

    suspend fun seedInitialDatabaseIfEmpty() {
        val initialSounds = listOf(
            AcousticReferenceEntity(
                soundProfileId = "v6_40l_normal_idle_000",
                title = "Ford 4.0L SOHC V6 Normal Idle Baseline (720 RPM)",
                matchCategory = "NORMAL_ENGINE",
                frequencyMinHz = 120,
                frequencyMaxHz = 280,
                spectralPatternSignature = "SMOOTH_6_CYLINDER_FIRING_PULSE_720RPM",
                matchConfidencePercent = 99,
                soundCharacteristics = "Smooth 120Hz-280Hz combustion rhythm, balanced sub-bass exhaust tone, zero metallic clatter",
                rootCause = "Normal healthy Ford 4.0L V6 engine operation at factory idle specification",
                recommendedFix = "No action required. Engine acoustic signature aligns with factory reference baseline.",
                targetComponentId = "cylinder_heads_valvetrain_3d",
                colorHex = "#22C55E",
                audioSampleUrlOrResource = "acoustic_db/ref_40l_normal_idle.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "v6_40l_normal_cruise_001",
                title = "Ford 4.0L SOHC V6 Normal Cruise Baseline (2000 RPM)",
                matchCategory = "NORMAL_ENGINE",
                frequencyMinHz = 380,
                frequencyMaxHz = 580,
                spectralPatternSignature = "BALANCED_HIGHWAY_CRUISE_33HZ_PRIMARY_ORDER",
                matchConfidencePercent = 98,
                soundCharacteristics = "Consistent 380Hz-580Hz smooth induction and exhaust tone during steady cruise",
                rootCause = "Normal factory combustion acoustics under 2000 RPM light road load",
                recommendedFix = "No repair necessary. Normal operating acoustic telemetry.",
                targetComponentId = "air_cleaner_maf_3d",
                colorHex = "#10B981",
                audioSampleUrlOrResource = "acoustic_db/ref_40l_normal_cruise.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "sohc_timing_chain_rattle_002",
                title = "4.0L SOHC Timing Chain Cassette & Tensioner Rattle",
                matchCategory = "ENGINE_TIMING",
                frequencyMinHz = 550,
                frequencyMaxHz = 950,
                spectralPatternSignature = "METALLIC_SOHC_TIMING_COVER_RATTLE_650HZ",
                matchConfidencePercent = 97,
                soundCharacteristics = "Metallic clatter/rattle at front/rear timing covers, pronounced during cold startup and 2000-3000 RPM overrun",
                rootCause = "Worn plastic timing chain guide cassette or hydraulic chain tensioner bleed-down on 4.0L SOHC engine",
                recommendedFix = "Replace front/rear hydraulic chain tensioners and timing chain cassette guides.",
                targetComponentId = "cylinder_heads_valvetrain_3d",
                colorHex = "#EF4444",
                audioSampleUrlOrResource = "acoustic_db/ref_sohc_chain_rattle.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "radio_alternator_whistle_001",
                title = "Radio High-Pitch Whistle (Faulty Alternator Ground)",
                matchCategory = "ELECTRICAL",
                frequencyMinHz = 2800,
                frequencyMaxHz = 4500,
                spectralPatternSignature = "RPM_TRACKING_DIODE_RIPPLE_WHISTLE",
                matchConfidencePercent = 98,
                soundCharacteristics = "Whining frequency 2.8kHz - 4.5kHz tracking directly with engine RPM pitch",
                rootCause = "Loose firewall engine ground strap or bad ground connection on stereo head unit",
                recommendedFix = "Clean ignition coil ground lug, tighten battery block strap, install inline noise filter.",
                targetComponentId = "alternator_ignition_3d",
                colorHex = "#00F0FF",
                audioSampleUrlOrResource = "acoustic_db/ref_radio_whistle.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "water_pump_bearing_squeal_003",
                title = "Water Pump Impeller Bearing Wear & Seal Screech",
                matchCategory = "COOLING",
                frequencyMinHz = 2400,
                frequencyMaxHz = 3600,
                spectralPatternSignature = "ACCESSORY_DRIVE_2.8KHZ_BEARING_SCREECH",
                matchConfidencePercent = 95,
                soundCharacteristics = "High pitch metallic screeching/scraping at front water pump pulley at idle and load",
                rootCause = "Water pump shaft bearing wear or mechanical coolant seal breakdown",
                recommendedFix = "Replace water pump assembly, thermostat housing, and flush cooling system.",
                targetComponentId = "water_pump_thermostat_3d",
                colorHex = "#F59E0B",
                audioSampleUrlOrResource = "acoustic_db/ref_water_pump_squeal.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "engine_lifter_sohc_tick_005",
                title = "Cold Engine Lifter / Hydraulic Lash Adjuster Tick",
                matchCategory = "ENGINE_VALVETRAIN",
                frequencyMinHz = 320,
                frequencyMaxHz = 620,
                spectralPatternSignature = "METALLIC_CYLINDER_HEAD_TAPPING_8HZ",
                matchConfidencePercent = 94,
                soundCharacteristics = "Metallic tapping noise at cylinder head front cover (8Hz at idle)",
                rootCause = "Hydraulic timing chain tensioner oil pressure bleed-down or low motor oil level",
                recommendedFix = "Check oil level immediately, inspect hydraulic tensioner assembly.",
                targetComponentId = "cylinder_heads_valvetrain_3d",
                colorHex = "#FF6F00",
                audioSampleUrlOrResource = "acoustic_db/ref_lifter_tick.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "fuel_pump_hum_003",
                title = "In-Tank Fuel Pump High-Pitch Hum",
                matchCategory = "FUEL_SYSTEM",
                frequencyMinHz = 1600,
                frequencyMaxHz = 2100,
                spectralPatternSignature = "REAR_BED_1.8KHZ_ELECTRIC_MOTOR_HUM",
                matchConfidencePercent = 95,
                soundCharacteristics = "Constant high-frequency electric humming behind rear bed (1.8kHz)",
                rootCause = "Electric fuel pump motor brush wear or clogged fuel filter causing high amperage draw",
                recommendedFix = "Replace fuel filter under frame, inspect fuel pump delivery pressure.",
                targetComponentId = "air_cleaner_maf_3d",
                colorHex = "#FFD700",
                audioSampleUrlOrResource = "acoustic_db/ref_fuel_pump_hum.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "power_steering_cavitation_006",
                title = "Power Steering Pump Cavitation & Fluid Aeration Groan",
                matchCategory = "STEERING_AUX",
                frequencyMinHz = 750,
                frequencyMaxHz = 1600,
                spectralPatternSignature = "HYDRAULIC_PUMP_1.2KHZ_CAVITATION_GROAN",
                matchConfidencePercent = 93,
                soundCharacteristics = "Heavy hydraulic groan/whine when turning steering wheel lock-to-lock",
                rootCause = "Low Mercon ATF power steering fluid or air aeration at pump inlet reservoir hose",
                recommendedFix = "Check power steering fluid level, bleed steering rack, replace reservoir O-ring seal.",
                targetComponentId = "brakes_suspension_3d",
                colorHex = "#3B82F6",
                audioSampleUrlOrResource = "acoustic_db/ref_power_steering_groan.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "exhaust_manifold_gasket_leak_007",
                title = "Exhaust Manifold Flange Gasket Blow-By Leak (Hiss-Puff)",
                matchCategory = "EXHAUST_EMISSIONS",
                frequencyMinHz = 140,
                frequencyMaxHz = 420,
                spectralPatternSignature = "CYLINDER_EXHAUST_PULSE_PUFF_200HZ",
                matchConfidencePercent = 92,
                soundCharacteristics = "Rhythmic sharp exhaust puff/hiss under acceleration, diminishes as manifold heats up",
                rootCause = "Cracked cast iron exhaust manifold or blown multi-layer steel manifold gasket on passenger side bank",
                recommendedFix = "Replace exhaust manifold gasket set, inspect casting for thermal stress cracks.",
                targetComponentId = "exhaust_manifolds_3d",
                colorHex = "#EC4899",
                audioSampleUrlOrResource = "acoustic_db/ref_exhaust_leak.wav"
            ),
            AcousticReferenceEntity(
                soundProfileId = "ac_compressor_pulley_growl_training_008",
                title = "A/C Compressor or Pulley Bearing Growl — Compare and Verify",
                matchCategory = "AC_ACCESSORY_DRIVE",
                frequencyMinHz = 800,
                frequencyMaxHz = 1700,
                spectralPatternSignature = "AC_ACCESSORY_DRIVE_LOW_MECHANICAL_GROWL",
                matchConfidencePercent = 70,
                soundCharacteristics = "Low mechanical growl or rough rotating sound near the passenger-side accessory drive. The same broad sound can also come from an idler, tensioner, belt, or another pulley.",
                rootCause = "Possible compressor/pulley-bearing or accessory-drive wear; audio alone cannot isolate the failed part.",
                recommendedFix = "Use the model to inspect the belt and compressor access path first. With the engine OFF, inspect the belt/pulleys visually; if the sound is severe, stop A/C use and obtain a qualified accessory-drive or A/C diagnosis before buying parts.",
                targetComponentId = "ac_compressor",
                colorHex = "#F59E0B",
                audioSampleUrlOrResource = "training_profile/ac_compressor_pulley_growl"
            ),
            AcousticReferenceEntity(
                soundProfileId = "ac_clutch_cycling_context_009",
                title = "A/C Clutch Cycling Clicks — Context Required",
                matchCategory = "AC_CLUTCH_ELECTRICAL",
                frequencyMinHz = 1800,
                frequencyMaxHz = 3600,
                spectralPatternSignature = "INTERMITTENT_AC_CLUTCH_TRANSIENT_CLICK_CONTEXT",
                matchConfidencePercent = 55,
                soundCharacteristics = "Intermittent engagement clicks may be heard as the clutch cycles. A simple frequency match cannot determine whether cycling is normal, charge-related, electrical, thermal, or commanded by the vehicle controls.",
                rootCause = "Possible A/C clutch cycling pattern requiring supporting airflow, electrical-command, and professionally measured refrigerant-system checks.",
                recommendedFix = "Do not jumper the clutch or pressure controls. Record whether cooling changes, whether the clutch cycles, and whether airflow is strong; then use the A/C Workbench before deciding on service.",
                targetComponentId = "ac_service_ports_controls_3d",
                colorHex = "#38BDF8",
                audioSampleUrlOrResource = "training_profile/ac_clutch_cycling_context"
            ),
            AcousticReferenceEntity(
                soundProfileId = "ac_belt_squeal_training_010",
                title = "A/C-On Belt or Clutch Squeal — Compare and Verify",
                matchCategory = "AC_ACCESSORY_DRIVE",
                frequencyMinHz = 2600,
                frequencyMaxHz = 4400,
                spectralPatternSignature = "AC_ACCESSORY_HIGH_FRICTION_SQUEAL",
                matchConfidencePercent = 68,
                soundCharacteristics = "A high-pitch friction squeal that may change when A/C load changes. Belt condition, tensioner, pulley, clutch, and compressor load can overlap acoustically.",
                rootCause = "Possible accessory-drive belt slip, tensioner/pulley wear, clutch issue, or compressor load issue; further inspection is required.",
                recommendedFix = "Turn A/C OFF if a strong squeal or smoke occurs. With the engine cool and OFF, inspect the belt path. Do not spray belt dressing, reach into a running engine bay, or open the refrigerant circuit based on sound alone.",
                targetComponentId = "ac_compressor",
                colorHex = "#EF4444",
                audioSampleUrlOrResource = "training_profile/ac_belt_squeal"
            )
        )

        dao.insertAll(initialSounds)
    }
}
