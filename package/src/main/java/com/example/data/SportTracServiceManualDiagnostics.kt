package com.example.data

import com.example.model.ServiceManualTroubleMatch
import com.example.model.SymptomItem
import com.example.model.VehicleSystem

object SportTracServiceManualDiagnostics {

    val symptomsList = listOf(
        SymptomItem(
            id = "sym_cold_rattle",
            title = "Cold Startup Engine Metallic Rattle / Ticking",
            description = "Loud metallic rattling or ticking noise from front/rear of engine for 3-5 seconds immediately after cold start.",
            system = VehicleSystem.ENGINE,
            severity = "Severe"
        ),
        SymptomItem(
            id = "sym_lean_codes",
            title = "Check Engine Light P0171 / P0174 (Lean Bank 1 & 2)",
            description = "Diagnostic trouble codes indicating engine is running too lean at idle, often with soft vacuum hissed noise.",
            system = VehicleSystem.ENGINE,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_rough_idle",
            title = "Engine Rough Idle or RPM Surging at Stop",
            description = "Unstable idle RPM fluctuating between 500-900 RPM when truck is in Gear at stoplights.",
            system = VehicleSystem.ENGINE,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_misfire_acceleration",
            title = "Hesitation or Engine Misfire under Heavy Load",
            description = "Stuttering or bucking when accelerating uphill or under heavy throttle with flashing or steady Check Engine Light.",
            system = VehicleSystem.ENGINE,
            severity = "Severe"
        ),
        SymptomItem(
            id = "sym_coolant_valley_leak",
            title = "Coolant Puddle in Engine Valley behind Belt",
            description = "Bright green or gold coolant pooling on top of lower intake manifold plenum behind thermostat bypass hose.",
            system = VehicleSystem.COOLING,
            severity = "Critical"
        ),
        SymptomItem(
            id = "sym_overheat_traffic",
            title = "Temp Gauge Spikes to Red in Stop & Go Traffic",
            description = "Engine coolant temperature rises rapidly when idling in traffic, but returns to middle position when cruising at 45+ MPH.",
            system = VehicleSystem.COOLING,
            severity = "Severe"
        ),
        SymptomItem(
            id = "sym_no_heat_idle",
            title = "Heater Blows Cold Air at Idle, Warm when Driving",
            description = "Cabin HVAC heater output cools off completely when truck stops at red lights and only warms up when engine RPM increases.",
            system = VehicleSystem.COOLING,
            severity = "Minor"
        ),
        SymptomItem(
            id = "sym_ac_blowing_warm",
            title = "A/C Blows Warm Air / Clutch Clicks Rapidly",
            description = "Air conditioner compressor clutch clicks on and off every 2-3 seconds with lukewarm air from dash vents.",
            system = VehicleSystem.AIR_CONDITIONING,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_shift_flare",
            title = "5R55E 2nd to 3rd Gear Shift Flare (RPM Spike)",
            description = "Transmission momentarily neutralizes and engine RPM spikes upward during the 2-3 upshift under normal acceleration.",
            system = VehicleSystem.TRANSMISSION,
            severity = "Severe"
        ),
        SymptomItem(
            id = "sym_od_light_flashing",
            title = "O/D OFF Dashboard Indicator Light Flashing",
            description = "The Overdrive Off light on the gear shift lever flashes continuously while driving, signaling transmission DTC storage.",
            system = VehicleSystem.TRANSMISSION,
            severity = "Severe"
        ),
        SymptomItem(
            id = "sym_hard_drive_engage",
            title = "Delayed or Harsh Engagement in Drive / Reverse",
            description = "Noticeable 2-3 second pause before transmission clunks into gear when moving shifter from Park into Drive or Reverse.",
            system = VehicleSystem.TRANSMISSION,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_4x4_flash",
            title = "4x4 HIGH & 4x4 LOW Dashboard Lights Flashing 6 Times",
            description = "Both 4WD indicator lights flash 6 times in sequence on instrument cluster upon key turn or switch activation.",
            system = VehicleSystem.DRIVETRAIN_4WD,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_4x4_clunk_turn",
            title = "Loud Popping / Binding in Front End during 4WD Turns",
            description = "Heavy knocking sound and steering resistance when turning steering wheel on loose surfaces in 4WD HIGH.",
            system = VehicleSystem.DRIVETRAIN_4WD,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_clunk_speedbumps",
            title = "Clunking Thump over Speed Bumps & Potholes",
            description = "Heavy metallic clunk or rattle noise coming from front wheels when suspension compresses over rough roads.",
            system = VehicleSystem.BRAKES_CHASSIS,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_brake_shudder",
            title = "Steering Wheel Shudder when Braking from Highway Speed",
            description = "Vibration felt through steering wheel and brake pedal when applying brakes at 50+ MPH.",
            system = VehicleSystem.BRAKES_CHASSIS,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_power_steering_whine",
            title = "Power Steering Whine / Groan when Cold",
            description = "High pitched hydraulic whining sound from engine bay when turning steering wheel, especially on cold mornings.",
            system = VehicleSystem.BRAKES_CHASSIS,
            severity = "Minor"
        ),
        SymptomItem(
            id = "sym_battery_drain",
            title = "Parasitic Battery Drain Overnight",
            description = "Truck battery completely discharged and unable to start engine after sitting unused for 24-48 hours.",
            system = VehicleSystem.ELECTRICAL,
            severity = "Moderate"
        ),
        SymptomItem(
            id = "sym_long_crank_start",
            title = "Long Engine Crank Time (5+ Sec) after Sitting",
            description = "Engine turns over rapidly for several seconds before catching and firing up, particularly after sitting overnight.",
            system = VehicleSystem.AIR_INTAKE,
            severity = "Moderate"
        )
    )

    val manualTroubleMatches = listOf(
        ServiceManualTroubleMatch(
            id = "match_timing_tensioners",
            title = "Cologne 4.0L SOHC Hydraulic Timing Chain Tensioner Failure",
            serviceManualSection = "Ford FSM Sec. 303-01B / TSB 02-21-13",
            tsbNumber = "TSB 02-21-13",
            problemSummary = "The Cologne 4.0L SOHC engine features primary, front, and rear timing chain assemblies. The hydraulic tensioners rely on engine oil pressure and internal check valves. Over time, internal seals degrade, causing the chain to slap against plastic cassette guides during oil pressure buildup on cold start.",
            probableCause = "Weakened spring check valve in hydraulic primary/jackshaft timing chain tensioner (OEM Part # 7U2Z-6K254-A & 7U2Z-6K254-B). Plastic guide cassette fragments may enter oil pan.",
            obdCodes = listOf("P0340", "P0344"),
            targetComponentId = "engine_block",
            urgencyLevel = "High",
            matchingSymptomIds = listOf("sym_cold_rattle"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Check engine oil dipstick level and condition. Ensure 5W-30 synthetic blend oil is used.",
                "Step 2: Connect mechanical mechanical oil pressure gauge to oil pressure sender port near filter adapter (Spec: 40-60 PSI hot @ 2000 RPM).",
                "Step 3: Remove passenger side wheel well liner to inspect front hydraulic tensioner body for oil seepage.",
                "Step 4: If cold startup rattle lasts longer than 5 seconds, replace both front and jackshaft tensioners with updated OEM Ford oil-retaining tensioners."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_intake_gaskets",
            title = "Upper Intake Manifold Press-In Gaskets & PCV Vacuum Leak",
            serviceManualSection = "Ford FSM Sec. 303-01B / Fuel & Emissions",
            tsbNumber = "TSB 04-12-03",
            problemSummary = "Factory press-in rubber seals between upper plastic plenum and lower intake manifold shrink due to thermal cycling. Unmetered air bypasses the MAF sensor, creating a lean fuel condition and erratic idle.",
            probableCause = "Hardened or flattened upper/lower intake manifold plenum gaskets or cracked PCV elbow rubber boot located behind upper intake plenum.",
            obdCodes = listOf("P0171", "P0174", "P0505"),
            targetComponentId = "intake_manifold",
            urgencyLevel = "Medium",
            matchingSymptomIds = listOf("sym_lean_codes", "sym_rough_idle"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Inspect Long Term Fuel Trims (LTFT) on OBD-II scanner. LTFT > +18% confirms unmetered air vacuum leak.",
                "Step 2: Spray carb cleaner or unlit propane torch around upper plenum mating seams while observing idle RPM.",
                "Step 3: Inspect rubber elbow tube at back of intake leading to PCV valve for collapse, softening, or dry-rot tears.",
                "Step 4: Torque intake manifold bolts to 89 lb-in (10 Nm) in factory cross-pattern, or install fresh green silicone gasket kit."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_thermostat_housing",
            title = "2-Piece Composite Plastic Thermostat Housing Seam Split",
            serviceManualSection = "Ford FSM Sec. 303-07A / Cooling System",
            tsbNumber = "TSB 01-11-06",
            problemSummary = "The 2004 4.0L SOHC original composite thermostat housing is a 2-piece design ultrasonically welded together. Hot coolant pressure and engine heat degrade the weld seam, causing green/gold coolant to pool in the lower engine valley.",
            probableCause = "Structural seam separation of OEM plastic thermostat housing (2L2Z-8592-BA) or failed thermostat bypass O-ring.",
            obdCodes = listOf("P0128"),
            targetComponentId = "thermostat_housing",
            urgencyLevel = "Critical",
            matchingSymptomIds = listOf("sym_coolant_valley_leak", "sym_no_heat_idle"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Inspect valley of engine directly behind alternator pulley for pooled coolant using bright flashlight.",
                "Step 2: Perform cooling system pressure test at 16 PSI. Observe thermostat housing lower body neck seam for micro-beads of coolant.",
                "Step 3: Check coolant level in reservoir; low fluid level causes air pocket in cabin heater core circuit.",
                "Step 4: Replace with upgraded heavy-duty solid cast aluminum thermostat housing assembly."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_fan_clutch",
            title = "Viscous Thermal Fan Clutch Fluid Loss / Slippage",
            serviceManualSection = "Ford FSM Sec. 303-07A",
            tsbNumber = null,
            problemSummary = "The radiator cooling fan is driven by a viscous silicone fluid clutch. When internal fluid leaks out or the bimetallic thermal spring fails, fan speed remains at low idle lockup, failing to pull air through radiator core when truck is stationary.",
            probableCause = "Silicone fluid depletion inside viscous fan clutch hub assembly.",
            obdCodes = emptyList(),
            targetComponentId = "radiator_assembly",
            urgencyLevel = "High",
            matchingSymptomIds = listOf("sym_overheat_traffic"),
            diagnosticVerificationSteps = listOf(
                "Step 1: With engine cold and OFF, turn fan blades by hand. There should be moderate smooth drag.",
                "Step 2: Start engine, let reach normal operating temperature (195°F). Shut OFF engine and attempt to spin fan blade; if blade spins freely more than 1 revolution, fan clutch is spent.",
                "Step 3: Inspect front center face of fan clutch hub for dark oily dirt residue indicating silicone leakage."
            ),
            difficulty = "Beginner"
        ),
        ServiceManualTroubleMatch(
            id = "match_5r55e_valvebody",
            title = "5R55E Transmission Valve Body Gasket Blowout & EPC Solenoid",
            serviceManualSection = "Ford FSM Sec. 307-01 / Automatic Transmission",
            tsbNumber = "TSB 03-20-05",
            problemSummary = "The 5R55E 5-speed automatic transmission valve body utilizes a paper separator plate gasket. High oil pressure pulses cause the gasket to blow out near the Electronic Pressure Control (EPC) solenoid channel, losing hydraulic clamp pressure on the 2-3 shift band.",
            probableCause = "Blown bonded valve body separator plate gasket or failed EPC solenoid internal pressure spool valve.",
            obdCodes = listOf("P0732", "P0733", "P0741", "P1751"),
            targetComponentId = "transmission_solenoids",
            urgencyLevel = "High",
            matchingSymptomIds = listOf("sym_shift_flare", "sym_od_light_flashing", "sym_hard_drive_engage"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Retrieve stored transmission DTCs with FORScan scanner. P0732 indicates 2nd gear incorrect ratio.",
                "Step 2: Drain Mercon V ATF and drop transmission pan. Inspect pan magnet for clutch material vs metal filings.",
                "Step 3: Remove valve body and inspect separator plate gasket for blown rubber channel around EPC solenoid circuit.",
                "Step 4: Install upgraded Ford bonded separator plate gasket kit (1L2Z-7Z490-A) and new Ford EPC Solenoid."
            ),
            difficulty = "Advanced"
        ),
        ServiceManualTroubleMatch(
            id = "match_4x4_shift_motor",
            title = "ControlTrac 4WD Transfer Case Shift Motor / Relay Contact Fault",
            serviceManualSection = "Ford FSM Sec. 308-07A / Four-Wheel Drive",
            tsbNumber = "TSB 03-19-02",
            problemSummary = "The 2004 Sport Trac uses an electric motor mounted to the BorgWarner 4411 transfer case to shift between 2WD, 4WD High, and 4WD Low. Internal rotary position switch contacts oxidize due to infrequent use, causing 4x4 module to lose encoder position.",
            probableCause = "Corroded encoder wheel contacts inside shift motor or sticking 4x4 control module relay.",
            obdCodes = listOf("P1867", "P1836", "P1854"),
            targetComponentId = "transfer_case",
            urgencyLevel = "Medium",
            matchingSymptomIds = listOf("sym_4x4_flash", "sym_4x4_clunk_turn"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Check fuse #10 (15A) and fuse #18 (20A) in central junction box under driver dash.",
                "Step 2: Tap transfer case shift motor housing lightly with rubber mallet while an assistant toggles 4x4 dash switch.",
                "Step 3: Measure 12V supply voltage at shift motor 7-pin harness plug during switch activation.",
                "Step 4: Unbolt 3 10mm bolts securing shift motor to transfer case rear housing and test motor cycle operation."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_ball_joints",
            title = "Front Lower & Upper Ball Joint Socket Play",
            serviceManualSection = "Ford FSM Sec. 204-01 / Front Suspension",
            tsbNumber = null,
            problemSummary = "Factory sealed ball joints lose internal grease lube over 70,000+ miles. Water intrusion rusts the hardened steel ball and socket, creating vertical and radial play that clunks over bumps and causes tire cupping.",
            probableCause = "Dry worn ball joint socket or torn rubber dust boot.",
            obdCodes = emptyList(),
            targetComponentId = "ball_joints_suspension",
            urgencyLevel = "High",
            matchingSymptomIds = listOf("sym_clunk_speedbumps"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Raise front frame on jack stands so front tires hang freely.",
                "Step 2: Place pry bar under tire bottom edge and lift upward while watching lower ball joint rubber boot for vertical play (Max spec: 0.030 in).",
                "Step 3: Grab tire at top (12 o'clock) and bottom (6 o'clock) and rock back and forth to check upper ball joint play.",
                "Step 4: Replace with press-in greaseable ball joints (Torque lower pinch nut: 83 lb-ft)."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_brake_rotors",
            title = "Front Brake Rotor Disc Thickness Variation (DTV) & Slide Pins",
            serviceManualSection = "Ford FSM Sec. 206-03 / Brakes",
            tsbNumber = null,
            problemSummary = "Thermal hotspots or ungreased caliper slide pins cause uneven friction material transfer onto front brake rotor surfaces. Uneven rotor thickness creates torque pulsing through caliper during braking.",
            probableCause = "Warped front vented brake rotors or seized dual-piston brake caliper guide slide pins.",
            obdCodes = emptyList(),
            targetComponentId = "brakes_suspension",
            urgencyLevel = "Medium",
            matchingSymptomIds = listOf("sym_brake_shudder"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Remove front brake caliper and slide pins. Verify pins slide smoothly in rubber boots with high-temp silicone grease.",
                "Step 2: Measure brake rotor lateral runout with dial indicator mounted to steering knuckle (Max spec: 0.0015 in).",
                "Step 3: Measure rotor thickness at 8 points around circumference with micrometer; DTV > 0.0005 in requires turning or replacing rotors.",
                "Step 4: Torque lug nuts to 100 lb-ft in star pattern to avoid distortion."
            ),
            difficulty = "Beginner"
        ),
        ServiceManualTroubleMatch(
            id = "match_ac_clutch_shim",
            title = "A/C Compressor Clutch Air Gap Wear / Shim Adjustment",
            serviceManualSection = "Ford FSM Sec. 412-00 / Climate Control",
            tsbNumber = null,
            problemSummary = "As the magnetic clutch friction face wears down, the air gap between drive pulley and front clutch plate widens past 0.035 in. The electromagnetic coil cannot pull the clutch plate closed when hot, causing rapid cycling or loss of A/C.",
            probableCause = "Excessive clutch plate air gap clearance due to normal shim friction wear.",
            obdCodes = emptyList(),
            targetComponentId = "ac_compressor",
            urgencyLevel = "Medium",
            matchingSymptomIds = listOf("sym_ac_blowing_warm"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Measure clutch air gap using feeler gauge between pulley and armature plate (Spec: 0.018 - 0.030 in).",
                "Step 2: If gap exceeds 0.035 in, unbolt center 8mm clutch bolt while holding armature plate with spanner tool.",
                "Step 3: Remove armature plate and take out one small spacer shim from center shaft sleeve.",
                "Step 4: Reinstall armature plate and verify gap is within 0.020 - 0.025 in spec."
            ),
            difficulty = "Beginner"
        ),
        ServiceManualTroubleMatch(
            id = "match_fuel_pump_checkvalve",
            title = "Fuel Pump Module Internal Check Valve Pressure Bleeddown",
            serviceManualSection = "Ford FSM Sec. 303-04C / Fuel Injection",
            tsbNumber = null,
            problemSummary = "The fuel pump assembly inside the 22.5 gallon tank incorporates a anti-bleeddown check valve. When the internal rubber valve seal warps, fuel pressure drains out of the fuel rail back into tank, requiring prolonged cranking to prime rail to 65 PSI.",
            probableCause = "Leaking internal fuel check valve on tank fuel pump reservoir module.",
            obdCodes = emptyList(),
            targetComponentId = "fuel_filter_pump",
            urgencyLevel = "Medium",
            matchingSymptomIds = listOf("sym_long_crank_start", "sym_misfire_acceleration"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Connect mechanical fuel pressure gauge to Schrader valve test port on fuel rail (Spec: 60-65 PSI).",
                "Step 2: Turn ignition key ON (engine OFF) for 2 seconds. Pressure should instantly rise to 65 PSI.",
                "Step 3: Turn key OFF and monitor gauge for 10 minutes. If pressure drops below 40 PSI, check valve or fuel injector is leaking.",
                "Step 4: Cycle ignition key ON-OFF 3 times before starting; if engine fires immediately, fuel pump check valve is leaking."
            ),
            difficulty = "Intermediate"
        ),
        ServiceManualTroubleMatch(
            id = "match_power_steering_aeration",
            title = "Power Steering Fluid Aeration & Reservoir Screen Clog",
            serviceManualSection = "Ford FSM Sec. 211-00 / Power Steering",
            tsbNumber = null,
            problemSummary = "The power steering plastic fluid reservoir contains a fine mesh screen at the bottom return port. Debris clogs this screen, restricting fluid feed to the pump inlet and causing cavitation, foaming, and cold whining noise.",
            probableCause = "Clogged mesh filter screen inside plastic power steering reservoir or trapped air in fluid lines.",
            obdCodes = emptyList(),
            targetComponentId = "power_steering_pump",
            urgencyLevel = "Low",
            matchingSymptomIds = listOf("sym_power_steering_whine"),
            diagnosticVerificationSteps = listOf(
                "Step 1: Inspect fluid inside reservoir with engine running. Micro-bubbles or pink foam indicate air suction or restricted return screen.",
                "Step 2: Remove fluid with turkey baster and inspect bottom mesh screen for black metal/rubber sludge.",
                "Step 3: Flush power steering system with fresh MERCON V ATF fluid.",
                "Step 4: Bleed air by turning steering wheel lock-to-lock 10 times with front wheels elevated off ground."
            ),
            difficulty = "Beginner"
        )
    )

    fun filterSymptomsBySystem(system: VehicleSystem?): List<SymptomItem> {
        if (system == null) return symptomsList
        return symptomsList.filter { it.system == system }
    }

    fun findTroubleMatchesForSymptoms(selectedSymptomIds: Set<String>): List<ServiceManualTroubleMatch> {
        if (selectedSymptomIds.isEmpty()) return emptyList()

        return manualTroubleMatches
            .map { match ->
                val overlap = match.matchingSymptomIds.count { selectedSymptomIds.contains(it) }
                Pair(match, overlap)
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }
}
