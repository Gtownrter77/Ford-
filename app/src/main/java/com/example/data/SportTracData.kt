package com.example.data

import com.example.data.local.MaintenanceEntity
import com.example.model.*

object SportTracData {

    val vehicleOverviewSpecs = mapOf(
        "Engine" to "4.0L Cologne SOHC V6 (12-Valve)",
        "Horsepower" to "210 hp @ 5250 RPM",
        "Torque" to "254 lb-ft @ 3700 RPM",
        "Transmission" to "5R55E 5-Speed Automatic / M5OD 5-Speed Manual",
        "Drivetrain" to "ControlTrac 4WD / Rear-Wheel Drive",
        "Cooling System Capacity" to "15.3 Quarts (50/50 Premium Gold Coolant)",
        "Oil Capacity" to "5.0 Quarts 5W-30 Motor Oil with Motorcraft FL-820S Filter",
        "Transmission Fluid" to "MERCON V ATF (Approx. 10.3 qts total refill)",
        "Fuel Tank Capacity" to "22.5 Gallons (85.2 Liters)",
        "Spark Plug Gap" to "0.052 - 0.056 in (Motorcraft AGSF-22PP)"
    )

    // Helper functions to generate 3D mesh geometry for components
    private fun createBoxMesh(
        width: Float, height: Float, depth: Float,
        center: Point3D, colorHex: String
    ): Pair<List<Point3D>, List<Face3D>> {
        val w = width / 2f
        val h = height / 2f
        val d = depth / 2f

        val vertices = listOf(
            Point3D(center.x - w, center.y - h, center.z - d), // 0: LBB
            Point3D(center.x + w, center.y - h, center.z - d), // 1: RBB
            Point3D(center.x + w, center.y + h, center.z - d), // 2: RTB
            Point3D(center.x - w, center.y + h, center.z - d), // 3: LTB
            Point3D(center.x - w, center.y - h, center.z + d), // 4: LBF
            Point3D(center.x + w, center.y - h, center.z + d), // 5: RBF
            Point3D(center.x + w, center.y + h, center.z + d), // 6: RTF
            Point3D(center.x - w, center.y + h, center.z + d)  // 7: LTF
        )

        val faces = listOf(
            Face3D(listOf(0, 1, 2, 3), colorHex), // Back
            Face3D(listOf(4, 5, 6, 7), colorHex), // Front
            Face3D(listOf(0, 4, 7, 3), colorHex), // Left
            Face3D(listOf(1, 5, 6, 2), colorHex), // Right
            Face3D(listOf(3, 2, 6, 7), colorHex), // Top
            Face3D(listOf(0, 1, 5, 4), colorHex)  // Bottom
        )

        return Pair(vertices, faces)
    }

    private fun createCylinderMesh(
        radius: Float, height: Float, segments: Int,
        center: Point3D, colorHex: String
    ): Pair<List<Point3D>, List<Face3D>> {
        val vertices = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()

        val halfH = height / 2f
        // Bottom center
        val botCenterIdx = 0
        vertices.add(Point3D(center.x, center.y - halfH, center.z))
        // Top center
        val topCenterIdx = 1
        vertices.add(Point3D(center.x, center.y + halfH, center.z))

        // Ring vertices
        for (i in 0 until segments) {
            val angle = (2.0 * Math.PI * i / segments).toFloat()
            val cosA = Math.cos(angle.toDouble()).toFloat()
            val sinA = Math.sin(angle.toDouble()).toFloat()

            // Bottom ring vertex
            vertices.add(Point3D(center.x + radius * cosA, center.y - halfH, center.z + radius * sinA))
            // Top ring vertex
            vertices.add(Point3D(center.x + radius * cosA, center.y + halfH, center.z + radius * sinA))
        }

        for (i in 0 until segments) {
            val nextI = (i + 1) % segments
            val b1 = 2 + i * 2
            val t1 = 2 + i * 2 + 1
            val b2 = 2 + nextI * 2
            val t2 = 2 + nextI * 2 + 1

            // Side quad
            faces.add(Face3D(listOf(b1, b2, t2, t1), colorHex))
            // Bottom fan
            faces.add(Face3D(listOf(botCenterIdx, b2, b1), colorHex))
            // Top fan
            faces.add(Face3D(listOf(topCenterIdx, t1, t2), colorHex))
        }

        return Pair(vertices, faces)
    }

    val components = listOf(
        // 1. ENGINE BLOCK & CYLINDER HEADS
        run {
            val (v, f) = createBoxMesh(2.2f, 1.8f, 2.5f, Point3D(0f, 0f, 0f), "#FF6F00")
            Component3DModel(
                id = "engine_block",
                name = "4.0L SOHC V6 Engine Block & Heads",
                system = VehicleSystem.ENGINE,
                oemPartNumber = "1L2Z-6010-AA",
                description = "Cast-iron engine block with aluminum 60-degree V6 cylinder heads and single overhead camshafts.",
                locationDescription = "Center engine bay, mounted longitudinally on hydraulic motor mounts.",
                difficulty = "Advanced (Specialist)",
                estimatedTimeMinutes = 360,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 0f, 0f),
                explodeVector = Point3D(0f, 0f, 0f),
                torqueSpecs = listOf(
                    TorqueSpec("Cylinder Head Bolts (Step 1)", "26", "35", "Must replace TTY bolts each time"),
                    TorqueSpec("Cylinder Head Bolts (Step 2)", "90 deg", "90 deg", "Angle rotation"),
                    TorqueSpec("Oil Pan Bolts", "15", "20", "Criss-cross pattern"),
                    TorqueSpec("Engine Mount Nuts", "65", "88", "To frame crossmember")
                ),
                requiredTools = listOf("1/2-inch Drive Torque Wrench", "Metric Socket Set (8mm-19mm)", "Engine Hoist (if removing)", "Angle Gauge"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery", "Disconnect negative battery terminal and discharge remaining electrical current.", warning = "Always disconnect battery before working on engine block."),
                    RepairStep(2, "Drain Engine Fluids", "Drain motor oil into a drain pan and discharge cooling system completely."),
                    RepairStep(3, "Remove Accessories", "Remove upper intake plenum, serpentine belt, alternator, and exhaust manifolds."),
                    RepairStep(4, "Head Bolt Extraction", "Loosen head bolts in reverse order of tightening sequence.")
                ),
                commonSymptoms = listOf("Engine timing chain tensioner rattle on startup", "Oil leaks from valve cover gaskets", "Compression loss across cylinders"),
                replacementIntervalMiles = 200000
            )
        },

        // 2. INTAKE MANIFOLD & PLENUM
        run {
            val (v, f) = createBoxMesh(1.8f, 0.8f, 2.0f, Point3D(0f, 1.2f, 0.2f), "#0284C7")
            Component3DModel(
                id = "intake_manifold",
                name = "Upper & Lower Intake Manifold",
                system = VehicleSystem.AIR_INTAKE,
                oemPartNumber = "2L2Z-9424-AA",
                description = "Composite plastic intake manifold with integrated runner controls and fuel rail mountings.",
                locationDescription = "Top center of engine, directly above cylinder valley.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 1.2f, 0.2f),
                explodeVector = Point3D(0f, 1.5f, 0f),
                torqueSpecs = listOf(
                    TorqueSpec("Lower Intake Manifold Bolts", "89 in-lbs", "10 Nm", "Tighten in 2 stages"),
                    TorqueSpec("Upper Intake Plenum Bolts", "89 in-lbs", "10 Nm", "Stage 1: 53 in-lbs, Stage 2: 89 in-lbs"),
                    TorqueSpec("Fuel Rail Retaining Bolts", "80 in-lbs", "9 Nm", "")
                ),
                requiredTools = listOf("8mm & 10mm Deep Sockets", "In-Lb Torque Wrench", "Vacuum Line Removal Tool", "Gasket Scraper"),
                repairSteps = listOf(
                    RepairStep(1, "Depressurize Fuel System", "Remove fuel pump relay and crank engine for 5 seconds to drop fuel rail line pressure.", warning = "Fuel is under 65 PSI. Wear safety goggles."),
                    RepairStep(2, "Disconnect Vacuum Lines & Harness", "Label and remove PCV tube, brake booster vacuum hose, and fuel injector connectors."),
                    RepairStep(3, "Unbolt Upper Plenum", "Remove six 10mm bolts securing upper intake to lower intake."),
                    RepairStep(4, "Replace Gaskets & Torque", "Install new silicone rubber press-in gaskets and torque in proper sequence.")
                ),
                commonSymptoms = listOf("Vacuum leaks causing P0171 / P0174 Lean System codes", "Rough idling at low RPM", "Whistling intake noise under acceleration"),
                replacementIntervalMiles = 100000
            )
        },

        // 3. THROTTLE BODY & MAF SENSOR
        run {
            val (v, f) = createCylinderMesh(0.45f, 0.9f, 8, Point3D(0.8f, 1.3f, 0.8f), "#0284C7")
            Component3DModel(
                id = "throttle_body",
                name = "Throttle Body & Mass Air Flow (MAF) Sensor",
                system = VehicleSystem.AIR_INTAKE,
                oemPartNumber = "1L2Z-9E926-AB",
                description = "Aluminum throttle body housing with throttle position sensor (TPS) and Idle Air Control (IAC) valve.",
                locationDescription = "Front right side of intake manifold plenum connected to air filter tube.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.8f, 1.3f, 0.8f),
                explodeVector = Point3D(0.8f, 0.8f, 0.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Throttle Body Bolts", "89 in-lbs", "10 Nm", "Do not overtighten brass inserts"),
                    TorqueSpec("IAC Valve Screws", "71 in-lbs", "8 Nm", "")
                ),
                requiredTools = listOf("10mm Socket", "Torx T20 Bit for MAF", "Throttle Body Cleaner Spray", "MAF Cleaner Spray"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Air Intake Tube", "Loosen flathead hose clamps on flexible intake tube and pull away from throttle body."),
                    RepairStep(2, "Disconnect Throttle Cables", "Unclip throttle cable and cruise control cable linkage from butterfly lever."),
                    RepairStep(3, "Remove 4 Mounting Bolts", "Unscrew four 10mm bolts and detach throttle body."),
                    RepairStep(4, "Clean Bore & Carbon Deposits", "Spray throttle body cleaner on butterfly plate and wipe clean with microfiber cloth.", tip = "Never use carb cleaner on MAF sensor wire; use specialized zero-residue MAF cleaner.")
                ),
                commonSymptoms = listOf("Sticky throttle pedal feel", "Engine stalling when coming to a stop", "Dirty MAF sensor causing hesitations"),
                replacementIntervalMiles = 60000
            )
        },

        // 4. THERMOSTAT HOUSING ASSEMBLY
        run {
            val (v, f) = createCylinderMesh(0.5f, 0.7f, 6, Point3D(0f, 0.9f, 1.2f), "#06B6D4")
            Component3DModel(
                id = "thermostat_housing",
                name = "Coolant Thermostat Housing Assembly",
                system = VehicleSystem.COOLING,
                oemPartNumber = "2L2Z-8592-BA",
                description = "2-piece composite thermostat housing outlet with dual coolant temperature sensors and 192°F thermostat.",
                locationDescription = "Top front center of engine valley directly behind serpentine belt pulley.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 0.9f, 1.2f),
                explodeVector = Point3D(0f, 1.2f, 1.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Thermostat Housing Bolts", "89 in-lbs", "10 Nm", "Plastic housing cracks if over-torqued"),
                    TorqueSpec("Coolant Temperature Sensor", "12", "16", "Use Teflon tape thread sealant")
                ),
                requiredTools = listOf("8mm & 10mm Deep Sockets", "Hose Clamp Pliers", "Drain Pan", "Ford Gold Coolant 50/50 Mix"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Radiator Coolant", "Open petcock drain valve at bottom left of radiator and collect 2 gallons of coolant."),
                    RepairStep(2, "Disconnect Upper Radiator Hose", "Squeeze constant-tension clamp and slide hose off thermostat neck."),
                    RepairStep(3, "Unplug Sensors", "Unclip wiring connectors for ECT sensor and gauge sender."),
                    RepairStep(4, "Unbolt Lower Housing", "Remove three 8mm bolts securing housing to engine block.", warning = "The factory plastic housing is prone to seam cracking; consider aluminum upgrade replacement assembly."),
                    RepairStep(5, "Refill & Bleed System", "Refill radiator, start engine with heater on HIGH, and purge air bubbles from coolant reservoir.")
                ),
                commonSymptoms = listOf("Coolant puddle pooling in engine valley", "Overheating at idle or highway speeds", "Sweet antifreeze smell inside cabin"),
                replacementIntervalMiles = 50000
            )
        },

        // 5. RADIATOR & FAN CLUTCH
        run {
            val (v, f) = createBoxMesh(2.8f, 2.0f, 0.6f, Point3D(0f, 0.5f, 2.3f), "#06B6D4")
            Component3DModel(
                id = "radiator_assembly",
                name = "Radiator & Mechanical Fan Clutch",
                system = VehicleSystem.COOLING,
                oemPartNumber = "1L2Z-8005-AB",
                description = "Aluminum cross-flow radiator core with plastic end tanks and viscous thermal fan clutch assembly.",
                locationDescription = "Very front of engine bay behind grille, enclosed in plastic cooling shroud.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 0.5f, 2.3f),
                explodeVector = Point3D(0f, 0f, 1.5f),
                torqueSpecs = listOf(
                    TorqueSpec("Fan Clutch Nut to Water Pump", "38", "52", "Left-Hand thread on some models (check arrow)"),
                    TorqueSpec("Radiator Mount Brackets", "80 in-lbs", "9 Nm", "")
                ),
                requiredTools = listOf("Fan Clutch Wrench Set (36mm)", "10mm Socket & Extension", "Drain Pan", "Coolant Funnel Kit"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Cooling System", "Drain radiator via lower drain valve into clean container."),
                    RepairStep(2, "Remove Upper Fan Shroud", "Unbolt top shroud clips and disconnect coolant overflow hose."),
                    RepairStep(3, "Remove Fan Clutch", "Use 36mm fan clutch wrench holding tool on water pump pulley bolts and turn clutch nut counter-clockwise."),
                    RepairStep(4, "Disconnect Transmission Cooler Lines", "Unscrew upper and lower quick-connect fittings for transmission oil cooler using flare wrench."),
                    RepairStep(5, "Lift Radiator Out", "Lift old radiator straight up out of lower rubber mounting bushings.")
                ),
                commonSymptoms = listOf("Coolant leaks along plastic tank side seams", "Engine overheating during summer stop-and-go driving", "Roaring fan noise at all speeds"),
                replacementIntervalMiles = 100000
            )
        },

        // 6. WATER PUMP
        run {
            val (v, f) = createCylinderMesh(0.6f, 0.5f, 8, Point3D(0f, 0.2f, 1.4f), "#06B6D4")
            Component3DModel(
                id = "water_pump",
                name = "Engine Coolant Water Pump",
                system = VehicleSystem.COOLING,
                oemPartNumber = "1L2Z-8501-AA",
                description = "Cast aluminum mechanical impeller pump driven directly by serpentine belt.",
                locationDescription = "Front center of engine block directly behind fan clutch pulley.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 150,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 0.2f, 1.4f),
                explodeVector = Point3D(0f, 0f, 1.0f),
                torqueSpecs = listOf(
                    TorqueSpec("Water Pump Mounting Bolts", "89 in-lbs", "10 Nm", "Tighten evenly in star pattern"),
                    TorqueSpec("Water Pump Pulley Bolts", "18", "25", "")
                ),
                requiredTools = listOf("3/8-inch Drive Socket Set", "Gasket Scraper", "RTV Silicone Gasket Sealant", "Serpentine Belt Tool"),
                repairSteps = listOf(
                    RepairStep(1, "Release Belt Tension", "Rotate belt tensioner arm counter-clockwise to slip serpentine belt off water pump pulley."),
                    RepairStep(2, "Unbolt Pulley", "Remove four 10mm bolts holding pulley to water pump flange."),
                    RepairStep(3, "Unbolt Water Pump", "Remove eleven perimeter mounting bolts securing water pump body to front timing cover."),
                    RepairStep(4, "Clean Mounting Surface", "Scrape off old gasket material completely and clean mating face with brake cleaner.", warning = "Do not gouge aluminum engine timing cover."),
                    RepairStep(5, "Install New Pump", "Apply thin layer of RTV silicone sealant and torque bolts to 89 in-lbs.")
                ),
                commonSymptoms = listOf("Squealing or grinding bearing noise from front of engine", "Coolant weeping out of front weep hole", "Engine temperature gauge spiking into red zone"),
                replacementIntervalMiles = 90000
            )
        },

        // 7. A/C COMPRESSOR & CLUTCH
        run {
            val (v, f) = createCylinderMesh(0.55f, 1.0f, 8, Point3D(-0.9f, 0.1f, 1.1f), "#10B981")
            Component3DModel(
                id = "ac_compressor",
                name = "A/C Scroll Compressor & Magnetic Clutch",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-19703-AC",
                description = "FS10 scroll refrigerant compressor with electromagnetic pulley clutch assembly.",
                locationDescription = "Lower left (driver side) of engine block, belt-driven.",
                difficulty = "Advanced (EPA Certification Recommended)",
                estimatedTimeMinutes = 180,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.9f, 0.1f, 1.1f),
                explodeVector = Point3D(-1.2f, 0f, 1.1f),
                torqueSpecs = listOf(
                    TorqueSpec("A/C Compressor Mounting Bolts", "18", "25", "3 long bolts through engine bracket"),
                    TorqueSpec("Manifold Block Fitting Bolt", "15", "20", "Use new HNBR green O-rings")
                ),
                requiredTools = listOf("A/C Manifold Gauge Set", "Vacuum Pump", "R134a Refrigerant Scale", "Snap Ring Pliers", "Feeler Gauge for Clutch Gap"),
                repairSteps = listOf(
                    RepairStep(1, "Evacuate R134a System", "Recover refrigerant legally using certified A/C recovery machine.", warning = "Venting R134a into atmosphere violates EPA Section 609 regulations."),
                    RepairStep(2, "Disconnect Electrical Clutch Connector", "Unplug single-wire magnetic coil lead."),
                    RepairStep(3, "Unbolt Manifold Line Block", "Remove single 13mm bolt holding high and low pressure suction/discharge lines."),
                    RepairStep(4, "Unbolt Compressor", "Unscrew three 10mm bolts and maneuver compressor out bottom of engine bay."),
                    RepairStep(5, "Set Clutch Air Gap", "Check new clutch gap with feeler gauge (0.014 - 0.026 in) using shims before installation.")
                ),
                commonSymptoms = listOf("A/C blows warm air", "Loud clack or screeching noise when A/C button pressed", "Refrigerant oil visible around compressor shaft seal"),
                replacementIntervalMiles = 120000
            )
        },

        // 8. TRANSMISSION ASSEMBLY (5R55E)
        run {
            val (v, f) = createBoxMesh(2.0f, 1.6f, 3.2f, Point3D(0f, -1.0f, -1.8f), "#9333EA")
            Component3DModel(
                id = "transmission_5r55e",
                name = "5R55E 5-Speed Automatic Transmission",
                system = VehicleSystem.TRANSMISSION,
                oemPartNumber = "1L2Z-7000-EA",
                description = "Electronically controlled 5-speed automatic transmission with torque converter lockup clutch and overdrive.",
                locationDescription = "Mounted directly behind engine block along center vehicle tunnel.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 300,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -1.0f, -1.8f),
                explodeVector = Point3D(0f, -1.5f, -1.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Transmission Fluid Pan Bolts", "120 in-lbs", "14 Nm", "Do not over-compress rubber gasket"),
                    TorqueSpec("Torque Converter Drive Plate Bolts", "34", "46", "Access through inspection cover"),
                    TorqueSpec("Bellhousing to Engine Bolts", "35", "47", "")
                ),
                requiredTools = listOf("Transmission Jack", "3/8-inch Drive Extension Set", "MERCON V ATF (10+ Quarts)", "Torque Wrench"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Transmission Fluid", "Remove fluid drain bolt or drop rear of pan carefully into wide catch basin."),
                    RepairStep(2, "Disconnect Driveshafts", "Unbolt front and rear driveshaft flange yokes and support with wire."),
                    RepairStep(3, "Unbolt Torque Converter", "Remove flywheel dust cover and unbolt torque converter nuts through access port."),
                    RepairStep(4, "Support & Lower Transmission", "Position transmission jack under pan, remove crossmember, unbolt bellhousing bolts, and slide rearward.")
                ),
                commonSymptoms = listOf("Flashing O/D OFF dashboard light", "Delayed 2-3 gear shift engagement or flare", "Shudder during torque converter lockup"),
                replacementIntervalMiles = 150000
            )
        },

        // 9. TRANSMISSION SOLENOID PACK & PAN
        run {
            val (v, f) = createBoxMesh(1.6f, 0.4f, 2.0f, Point3D(0f, -1.6f, -1.8f), "#9333EA")
            Component3DModel(
                id = "transmission_solenoids",
                name = "Valve Body Solenoid Pack & Filter",
                system = VehicleSystem.TRANSMISSION,
                oemPartNumber = "1L2Z-7G391-AA",
                description = "Integrated valve body solenoid block controlling EPC pressure, shift solenoids A/B/C/D, and TCC lockup.",
                locationDescription = "Inside transmission oil pan at bottom of casing.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -1.6f, -1.8f),
                explodeVector = Point3D(0f, -2.2f, -1.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Solenoid Pack Retaining Bolts", "80 in-lbs", "9 Nm", "Critical torque to prevent valve body gasket blowouts"),
                    TorqueSpec("Filter Retaining Bolt", "80 in-lbs", "9 Nm", "")
                ),
                requiredTools = listOf("T25 Torx Bit", "8mm Socket", "Fluid Transfer Pump", "MERCON V ATF", "Filter & Gasket Kit"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Transmission Pan", "Remove sixteen 10mm pan bolts and clean magnet in bottom of pan."),
                    RepairStep(2, "Remove Oil Filter", "Pull down old filter and replace O-ring seal on filter neck."),
                    RepairStep(3, "Disconnect Wiring Harness", "Unplug main 16-pin harness connector from top of solenoid pack."),
                    RepairStep(4, "Unbolt Solenoid Block", "Remove T25 Torx screws and swap solenoid pack with new gasket bond plate.", tip = "Replacing the separator plate gasket at the same time fixes common 2-3 shift flares in 5R55E transmissions.")
                ),
                commonSymptoms = listOf("Harsh gear engagement when shifting into Drive or Reverse", "Transmission slipping under heavy load", "Error codes P0731, P0732, P0750"),
                replacementIntervalMiles = 60000
            )
        },

        // 10. ALTERNATOR & IGNITION COIL
        run {
            val (v, f) = createCylinderMesh(0.5f, 0.6f, 8, Point3D(0.9f, 0.8f, 1.1f), "#EAB308")
            Component3DModel(
                id = "alternator_ignition",
                name = "130-Amp Alternator & EDIS Coil Pack",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-10346-A",
                description = "130-Amp heavy duty alternator with internal voltage regulator and 6-tower distributorless coil pack.",
                locationDescription = "Top right (passenger side) front engine accessory bracket.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 30,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.9f, 0.8f, 1.1f),
                explodeVector = Point3D(1.4f, 1.2f, 1.1f),
                torqueSpecs = listOf(
                    TorqueSpec("Alternator Mounting Bolts", "35", "47", ""),
                    TorqueSpec("B+ Terminal Nut", "80 in-lbs", "9 Nm", "Do not over-torque terminal post")
                ),
                requiredTools = listOf("13mm & 15mm Sockets", "10mm Wrench", "Voltmeter / Multimeter"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery", "Remove negative battery cable from post.", warning = "Shorting alternator wire will blow main 175A MegaFuse."),
                    RepairStep(2, "Remove Belt", "Release serpentine belt tension."),
                    RepairStep(3, "Unplug Wiring", "Unplug regulator harness and unbolt red B+ power cable."),
                    RepairStep(4, "Unbolt & Swap", "Remove three 13mm bolts and replace unit.")
                ),
                commonSymptoms = listOf("Battery indicator light on dash", "Dim headlights at idle", "Clicking starter or dead battery in morning"),
                replacementIntervalMiles = 100000
            )
        },

        // 10B. WIRING HARNESS, HEADLIGHTS & FUSE BOX 3D
        run {
            val (v, f) = createBoxMesh(2.8f, 0.9f, 4.2f, Point3D(0f, 0.4f, 0.8f), "#F59E0B")
            Component3DModel(
                id = "wiring_lighting_3d",
                name = "Engine Wiring Harness, Headlights & Central Junction Fuse Box",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-14290-AA",
                description = "Complete 12V body wiring loom, engine distribution harness, high-intensity dual beam halogen headlight assemblies, fog lamps, and battery junction box fuses.",
                locationDescription = "Engine bay perimeter, front grille header panel, and battery junction box on driver fender liner.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, 0.4f, 0.8f),
                explodeVector = Point3D(0f, 1.2f, 1.5f),
                torqueSpecs = listOf(
                    TorqueSpec("Battery Terminal Clamp Nuts", "80 in-lbs", "9 Nm", "Clean posts with wire brush"),
                    TorqueSpec("Headlight Adjuster Assembly Screws", "25 in-lbs", "2.8 Nm", "Align light beam height"),
                    TorqueSpec("Fuse Junction Box Ground Bolt", "89 in-lbs", "10 Nm", "Ensure bare metal contact for body grounds")
                ),
                requiredTools = listOf("Multimeter / Test Light", "Wire Stripper & Crimper", "Torx T20 Driver", "10mm Socket", "Dielectric Grease"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery Ground", "Remove 10mm negative battery terminal cable before touching any wiring harness or fuse box."),
                    RepairStep(2, "Unclip Headlight Retaining Pins", "Pull up the two vertical black locking tabs behind header panel to slide headlight housing forward."),
                    RepairStep(3, "Replace 9007 Headlight Bulbs", "Rotate locking collar 1/4 turn counterclockwise and swap 9007 halogen/LED bulb. Do not touch glass with bare hands.", warning = "Skin oils on quartz bulb glass cause hotspots and premature bulb burnout."),
                    RepairStep(4, "Inspect Central Fuse Junction Box", "Check 175A MegaFuse and PCM relay in battery junction box for burnt pins or green corrosion.")
                ),
                commonSymptoms = listOf("Headlights flickering or dim on driver side", "Turn signal hyperflashing due to blown bulb or relay", "Corroded ground wire causing instrument cluster electrical glitches"),
                replacementIntervalMiles = 80000
            )
        },

        // 11. FRONT BRAKES & TORSION SUSPENSION
        run {
            val (v, f) = createBoxMesh(2.6f, 0.8f, 1.0f, Point3D(0f, -0.8f, 2.0f), "#EF4444")
            Component3DModel(
                id = "brakes_suspension",
                name = "Front Disc Brakes & Torsion Bar Suspension",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "2L2Z-2001-AA",
                description = "Dual-piston floating brake calipers, vented rotors, and heavy-duty front torsion bar suspension arms.",
                locationDescription = "Front wheel wells and lower frame rails.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 75,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -0.8f, 2.0f),
                explodeVector = Point3D(0f, -1.2f, 2.5f),
                torqueSpecs = listOf(
                    TorqueSpec("Caliper Guide Pin Bolts", "26", "35", "Lubricate with silicone brake grease"),
                    TorqueSpec("Caliper Anchor Bracket Bolts", "85", "115", "Apply blue Threadlocker"),
                    TorqueSpec("Wheel Lug Nuts", "100", "135", "Torque in star pattern")
                ),
                requiredTools = listOf("13mm & 15mm Sockets", "C-Clamp / Caliper Press", "Brake Cleaner", "High-Temp Brake Grease"),
                repairSteps = listOf(
                    RepairStep(1, "Jack Up Vehicle", "Raise front of Sport Trac on rated jack stands and remove front wheels."),
                    RepairStep(2, "Unbolt Caliper", "Remove two 13mm guide pin bolts and suspend caliper with bungee cord."),
                    RepairStep(3, "Replace Rotor & Pads", "Remove anchor bracket, slide off vented rotor, and insert ceramic brake pads with anti-squeal shim paste."),
                    RepairStep(4, "Compress Pistons", "Compress dual pistons back into caliper housing using C-clamp and old brake pad.")
                ),
                commonSymptoms = listOf("Squealing or grinding noise when stopping", "Steering wheel vibration during braking", "Spongy brake pedal feel"),
                replacementIntervalMiles = 40000
            )
        },

        // 12. FULL TRUCK FRAME & COMPOSITE CARGO BED
        run {
            val (v, f) = createBoxMesh(3.4f, 1.2f, 5.2f, Point3D(0f, -0.6f, -0.5f), "#64748B")
            Component3DModel(
                id = "truck_frame_body",
                name = "Sport Trac Frame & Composite Cargo Bed",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "1L2Z-5005-CA",
                description = "Full box-section steel frame rails, 4-door cab shell structure, and dent-resistant composite cargo bed with tie-down cleats.",
                locationDescription = "Main structural chassis platform supporting engine, drivetrain, cab, and cargo bed.",
                difficulty = "Advanced (Collision / Frame)",
                estimatedTimeMinutes = 240,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -0.6f, -0.5f),
                explodeVector = Point3D(0f, -2.5f, 0f),
                torqueSpecs = listOf(
                    TorqueSpec("Cargo Bed Mounting Bolts (6 Bolts)", "59", "80", "Torx T55 head bolts"),
                    TorqueSpec("Cab Cushion Body Mount Bolts", "60", "81", "Check rubber isolator bushings"),
                    TorqueSpec("Trailer Hitch Receiver Bolts", "80", "108", "Grade 8 hardware")
                ),
                requiredTools = listOf("Torx T55 Bit", "18mm & 21mm Impact Sockets", "Breaker Bar", "Jack Stands", "Frame Rust Treatment Spray"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Tailgate Wiring", "Unplug rear taillight harness and tailgate lock wire connector before bed removal."),
                    RepairStep(2, "Unbolt Bed Floor Bolts", "Use T55 Torx socket with long breaker bar to remove 6 bed floor mounting bolts."),
                    RepairStep(3, "Lift Composite Bed", "With 2 assistants, lift lightweight composite bed straight up off steel frame rails."),
                    RepairStep(4, "Inspect Rubber Body Mounts", "Examine body mount rubber cushions for dry rot or frame rust perforation.")
                ),
                commonSymptoms = listOf("Squeaking or thumping noise over bumps from worn body mounts", "Bed misaligned with rear cab window line", "Frame surface rust near rear leaf spring shackles"),
                replacementIntervalMiles = 200000
            )
        },

        // 13. CONTROL TRAC 4WD TRANSFER CASE & DRIVESHAFTS
        run {
            val (v, f) = createBoxMesh(1.8f, 1.0f, 3.8f, Point3D(0f, -1.2f, -2.6f), "#9333EA")
            Component3DModel(
                id = "driveshaft_4x4",
                name = "Control Trac 4WD Transfer Case & Driveshafts",
                system = VehicleSystem.TRANSMISSION,
                oemPartNumber = "1L2Z-7A195-AB",
                description = "BorgWarner 44-11 electric shift-on-the-fly transfer case with front and rear aluminum driveshafts and Ford 8.8 rear differential.",
                locationDescription = "Mounted behind 5R55E transmission with driveshafts running to front and rear axles.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -1.2f, -2.6f),
                explodeVector = Point3D(0f, -1.8f, -2.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Driveshaft Flange Bolts", "83", "112", "12-point 12mm bolts with blue threadlocker"),
                    TorqueSpec("Transfer Case Drain & Fill Plugs", "22", "30", "Use MERCON V ATF fluid"),
                    TorqueSpec("Rear Differential Cover Bolts", "33", "45", "Use 75W-140 Synthetic Gear Oil with Friction Modifier")
                ),
                requiredTools = listOf("12mm 12-Point Socket", "3/8-inch Square Drive for Fill Plug", "Torque Wrench", "Fluid Transfer Pump"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Transfer Case", "Remove 3/8-inch square drive lower drain plug and drain 1.5 quarts of MERCON V fluid."),
                    RepairStep(2, "Unbolt Driveshaft Flanges", "Remove four 12-point 12mm bolts at rear differential pinion flange."),
                    RepairStep(3, "Replace U-Joints", "Press out old needle bearing U-joint caps using ball joint press or vise."),
                    RepairStep(4, "Inspect 4WD Shift Motor", "Check 4x4 electric encoder motor mounted on transfer case rear housing if 4x4 High/Low lights flash on dashboard.")
                ),
                commonSymptoms = listOf("4x4 HIGH / LOW dashboard lights flashing 6 times", "Clunking sound when engaging 4WD or shifting into Reverse", "High-speed driveline vibration from dry U-joints"),
                replacementIntervalMiles = 60000
            )
        },

        // 14. EXHAUST MANIFOLDS & CATALYTIC CONVERTER Y-PIPE
        run {
            val (v, f) = createCylinderMesh(0.5f, 2.8f, 8, Point3D(-0.4f, -0.5f, -0.8f), "#FF6F00")
            Component3DModel(
                id = "exhaust_system",
                name = "Exhaust Manifolds & Catalytic Converter Y-Pipe",
                system = VehicleSystem.ENGINE,
                oemPartNumber = "1L2Z-5E212-CB",
                description = "Cast iron exhaust manifolds, dual 3-way catalytic converters Y-pipe assembly, four heated oxygen sensors (HO2S), and stainless steel muffler.",
                locationDescription = "Bolted directly to 4.0L SOHC cylinder heads, running under vehicle belly to rear tailpipe.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 150,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.4f, -0.5f, -0.8f),
                explodeVector = Point3D(-1.8f, -0.8f, -0.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Exhaust Manifold Nuts to Cylinder Head", "18", "25", "Apply high-temp anti-seize"),
                    TorqueSpec("Y-Pipe Exhaust Flange Studs", "30", "41", "Must replace rusted flange nuts"),
                    TorqueSpec("Heated O2 Sensors (HO2S)", "30", "41", "22mm oxygen sensor socket")
                ),
                requiredTools = listOf("22mm O2 Sensor Socket", "13mm & 15mm Deep Sockets", "Penetrating Oil (PB Blaster)", "Exhaust Hanger Pliers"),
                repairSteps = listOf(
                    RepairStep(1, "Apply Penetrating Oil", "Soak all rusted exhaust flange nuts and O2 sensors in penetrating fluid 30 minutes before starting.", warning = "Exhaust components get extremely hot. Allow engine to cool completely before touching."),
                    RepairStep(2, "Disconnect Oxygen Sensors", "Unplug electrical connectors for upstream (Bank 1/2 Sensor 1) and downstream O2 sensors."),
                    RepairStep(3, "Unbolt Flange Connections", "Remove exhaust Y-pipe flange nuts at manifold outlets."),
                    RepairStep(4, "Replace Gaskets & Hangers", "Install new metal donut gaskets and lubricate rubber isolation hangers.")
                ),
                commonSymptoms = listOf("Loud ticking exhaust leak under acceleration", "Check Engine Light codes P0420 / P0430 (Catalyst Efficiency Below Threshold)", "Sulfur or rotten egg odor from tailpipe"),
                replacementIntervalMiles = 120000
            )
        },

        // 15. FUEL TANK & HIGH PRESSURE PUMP MODULE
        run {
            val (v, f) = createBoxMesh(2.2f, 1.2f, 2.4f, Point3D(-0.8f, -1.1f, -3.2f), "#0284C7")
            Component3DModel(
                id = "fuel_tank_pump",
                name = "22.5 Gallon Fuel Tank & High-Pressure Pump Module",
                system = VehicleSystem.AIR_INTAKE,
                oemPartNumber = "1L2Z-9H307-CB",
                description = "Molded 22.5-gallon polyethylene fuel tank, steel tank straps, in-tank electric turbine fuel pump module with fuel level sender, and fuel rail pressure sensor.",
                locationDescription = "Driver side frame rail under cab bed floor.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.8f, -1.1f, -3.2f),
                explodeVector = Point3D(-2.2f, -1.1f, -3.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Fuel Tank Strap Bolts", "38", "52", "Inspect straps for rust rot"),
                    TorqueSpec("Fuel Pump Retaining Lock Ring", "55", "75", "Use brass punch or spanner tool"),
                    TorqueSpec("Fuel Filter Bracket Bolt", "89 in-lbs", "10 Nm", "")
                ),
                requiredTools = listOf("Fuel Tank Lock Ring Tool", "Quick Disconnect Fuel Line Tools (3/8 & 5/16 in)", "Transmission Jack to Support Tank", "Safety Glasses"),
                repairSteps = listOf(
                    RepairStep(1, "Relieve Fuel Pressure", "Pull fuel pump inertia switch plug under passenger kick panel and crank engine 5 seconds.", warning = "Fuel is highly flammable. Work in well-ventilated area with no open sparks or flames."),
                    RepairStep(2, "Disconnect Filler Neck & Lines", "Loosen hose clamp on rubber filler neck and unclip EVAP vapor lines."),
                    RepairStep(3, "Support & Lower Tank", "Support tank with jack, unbolt 2 steel tank straps, and lower tank 6 inches to disconnect top electrical plug."),
                    RepairStep(4, "Swap Pump Module", "Unscrew fuel pump lock ring, lift old pump module out, and replace thick rubber O-ring seal.")
                ),
                commonSymptoms = listOf("Engine cranks but will not start (no fuel pressure)", "Loud whining noise from rear fuel tank area", "Fuel gauge reads empty or fluctuates erratically"),
                replacementIntervalMiles = 100000
            )
        },

        // 16. POWER STEERING RACK & PINION ASSEMBLY
        run {
            val (v, f) = createCylinderMesh(0.4f, 2.6f, 8, Point3D(0f, -0.4f, 1.6f), "#EF4444")
            Component3DModel(
                id = "steering_rack",
                name = "Power Steering Rack & Pinion Assembly",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "1L2Z-3504-BA",
                description = "Hydraulic power-assisted rack and pinion steering gear with inner/outer tie rod ends, power steering pump, and MERCON V fluid reservoir.",
                locationDescription = "Mounted across front lower engine crossmember behind front differential.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 180,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -0.4f, 1.6f),
                explodeVector = Point3D(0f, -1.0f, 2.0f),
                torqueSpecs = listOf(
                    TorqueSpec("Steering Rack Mounting Bolts", "85", "115", "Re-torque to frame crossmember"),
                    TorqueSpec("Outer Tie Rod End Castle Nuts", "41", "56", "Install new cotter pin"),
                    TorqueSpec("Power Steering Line Pressure Fittings", "20", "27", "Teflon O-ring seal")
                ),
                requiredTools = listOf("Tie Rod End Puller / Pickle Fork", "18mm & 21mm Deep Sockets", "Flare Nut Wrenches", "Torque Wrench", "Mercon V Fluid"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Outer Tie Rods", "Remove cotter pins, unscrew castle nuts, and pop outer tie rods from steering knuckles using puller tool."),
                    RepairStep(2, "Drain Steering Fluid", "Disconnect low pressure return hose and drain power steering fluid into drain pan."),
                    RepairStep(3, "Unbolt Steering Shaft Pinch Bolt", "Remove 13mm pinch bolt coupling steering column shaft to rack pinion input shaft."),
                    RepairStep(4, "Unbolt Rack & Remove", "Remove two large 21mm crossmember mounting bolts and slide rack assembly out driver wheel well."),
                    RepairStep(5, "Perform Wheel Alignment", "Perform front-end toe alignment immediately after installation.")
                ),
                commonSymptoms = listOf("Power steering fluid leaking from bellows boots onto lower control arms", "Whining or groaning noise when turning steering wheel", "Excessive play or wandering steering on highway"),
                replacementIntervalMiles = 120000
            )
        }
    )

    val defaultMaintenanceSchedules = listOf(
        MaintenanceScheduleItem("oil_change", "Engine Oil & Filter Service", VehicleSystem.ENGINE, 4000, 4, "5W-30 Motorcraft Blend (5.0 qts) & FL-820S Filter", "Essential for Cologne 4.0L SOHC timing chain tensioner hydraulic pressure.", "engine_block"),
        MaintenanceScheduleItem("air_filter", "Engine Air Filter Replacement", VehicleSystem.AIR_INTAKE, 15000, 12, "Motorcraft FA-1695 / OEM Spec", "Prevents dirt buildup on MAF sensor wire and maintains fuel economy.", "throttle_body"),
        MaintenanceScheduleItem("coolant_flush", "Cooling System Flush & Thermostat Inspection", VehicleSystem.COOLING, 30000, 24, "Motorcraft Premium Gold 50/50 Antifreeze (15.3 qts)", "Protects composite thermostat housing and aluminum cylinder heads from corrosion.", "thermostat_housing"),
        MaintenanceScheduleItem("trans_fluid", "5R55E Transmission Fluid & Filter Service", VehicleSystem.TRANSMISSION, 30000, 24, "MERCON V ATF & Premium Filter Gasket Set", "Critical to prevent valve body gasket blowout and solenoid wear in 5R55E.", "transmission_solenoids"),
        MaintenanceScheduleItem("spark_plugs", "Spark Plugs & Ignition Wires", VehicleSystem.ELECTRICAL, 60000, 48, "Motorcraft Platinum Plugs (0.054 in gap)", "Prevents engine misfires and catalyst damage.", "alternator_ignition"),
        MaintenanceScheduleItem("brake_pads", "Front Disc Brake Pad & Rotor Inspection", VehicleSystem.BRAKES_CHASSIS, 25000, 18, "Heavy Duty Ceramic Pads & Vented Rotors", "Ensures maximum stopping power for truck bed payload.", "brakes_suspension"),
        MaintenanceScheduleItem("ac_service", "A/C Pressure Inspection & System Check", VehicleSystem.AIR_CONDITIONING, 30000, 24, "R134a Refrigerant & PAG 46 Oil", "Measures high/low port pressures and checks compressor magnetic clutch air gap.", "ac_compressor")
    )

    val initialSampleLogs = listOf(
        MaintenanceEntity(
            id = 1,
            scheduleItemId = "oil_change",
            title = "Engine Oil & Filter Service",
            systemName = VehicleSystem.ENGINE.displayName,
            mileageAtService = 112000,
            dateLoggedMillis = System.currentTimeMillis() - (60L * 24 * 3600 * 1000),
            costUsd = 45.0,
            notes = "Used 5.0 qts Motorcraft 5W-30 synthetic blend and FL-820S filter. Oil pressure normal.",
            isCompleted = true
        ),
        MaintenanceEntity(
            id = 2,
            scheduleItemId = "coolant_flush",
            title = "Cooling System Flush & Thermostat Housing Upgrade",
            systemName = VehicleSystem.COOLING.displayName,
            mileageAtService = 110000,
            dateLoggedMillis = System.currentTimeMillis() - (120L * 24 * 3600 * 1000),
            costUsd = 120.0,
            notes = "Replaced factory plastic thermostat housing with upgraded metal assembly. Refilled with Motorcraft Gold coolant.",
            isCompleted = true
        )
    )

    val diagnosticCategories = listOf(
        DiagnosticSymptomCategory(
            id = "cat_engine_noise",
            categoryName = "Engine Rattle, Ticking or Misfire",
            system = VehicleSystem.ENGINE,
            commonSymptoms = listOf("Engine ticking/rattle on cold startup", "Rough idle with CEL flashing", "P0171/P0174 Lean codes"),
            rootQuestion = DiagnosticQuestion(
                id = "q_engine_noise_type",
                questionText = "When does the engine noise or symptom primarily occur?",
                options = listOf(
                    DiagnosticOption(
                        optionText = "Loud ticking/rattle from front or rear of engine for 3-5 seconds on cold startup",
                        resultDiagnosis = DiagnosticResult(
                            title = "4.0L SOHC Timing Chain Tensioner & Cassette Wear",
                            problemSummary = "The Cologne 4.0L SOHC engine uses hydraulic timing chain tensioners. When internal tensioner seals weaken or plastic guide cassettes wear out, chains rattle against the metal housing on initial oil pressure build.",
                            probableCause = "Worn front or jackshaft timing chain hydraulic tensioners (OEM # 7U2Z-6K254-A / 7U2Z-6K254-B).",
                            obdCode = "None or P0340 (Camshaft Position Sensor Circuit)",
                            targetComponentId = "engine_block",
                            confidencePercentage = 92,
                            urgencyLevel = "High",
                            recommendedAction = "Inspect oil pressure and replace hydraulic tensioner assemblies promptly before cassette plastic guide breaks into oil pan."
                        )
                    ),
                    DiagnosticOption(
                        optionText = "Engine runs rough at idle with lean codes P0171 and P0174",
                        resultDiagnosis = DiagnosticResult(
                            title = "Upper/Lower Intake Manifold Gasket Vacuum Leak",
                            problemSummary = "The composite upper intake plenum seals shrink over time due to engine heat cycles, drawing unmetered air into the combustion chambers at idle.",
                            probableCause = "Hardened or cracked intake manifold press-in rubber gaskets or cracked PCV elbow rubber boot.",
                            obdCode = "P0171 / P0174 (System Too Lean Bank 1 & Bank 2)",
                            targetComponentId = "intake_manifold",
                            confidencePercentage = 88,
                            urgencyLevel = "Medium",
                            recommendedAction = "Perform smoke test or spray brake cleaner around intake plenum seams at idle to confirm RPM drop, then replace upper and lower intake gasket kit."
                        )
                    )
                )
            )
        ),

        DiagnosticSymptomCategory(
            id = "cat_cooling_overheat",
            categoryName = "Coolant Leak or Engine Overheating",
            system = VehicleSystem.COOLING,
            commonSymptoms = listOf("Coolant puddling in engine valley", "Temperature needle in red zone", "Sweet antifreeze smell in cabin"),
            rootQuestion = DiagnosticQuestion(
                id = "q_coolant_symptom",
                questionText = "Where is coolant leaking or when does overheating happen?",
                options = listOf(
                    DiagnosticOption(
                        optionText = "Green/Gold coolant pooling on top of engine valley behind belt pulley",
                        resultDiagnosis = DiagnosticResult(
                            title = "Plastic Thermostat Housing Seam Crack",
                            problemSummary = "The original two-piece plastic thermostat housing on 2004 4.0L engines splits along its ultrasonic welded center seam after exposure to hot coolant pressure.",
                            probableCause = "Thermal fatigue and plastic degradation of OEM Thermostat Housing (2L2Z-8592-BA).",
                            obdCode = "P0128 (Coolant Thermostat Temperature Below Regulating Threshold)",
                            targetComponentId = "thermostat_housing",
                            confidencePercentage = 95,
                            urgencyLevel = "Critical",
                            recommendedAction = "Do not drive while overheating. Replace housing with upgraded cast aluminum assembly or OEM housing."
                        )
                    ),
                    DiagnosticOption(
                        optionText = "Engine temp spikes only when sitting stopped in traffic, then drops when driving fast",
                        resultDiagnosis = DiagnosticResult(
                            title = "Viscous Fan Clutch Failure",
                            problemSummary = "The mechanical viscous fan clutch fluid has leaked or thermal spring failed, preventing the fan from engaging full speed at idle.",
                            probableCause = "Worn cooling fan clutch mechanism or missing fan shroud air seals.",
                            obdCode = "None",
                            targetComponentId = "radiator_assembly",
                            confidencePercentage = 85,
                            urgencyLevel = "High",
                            recommendedAction = "With engine OFF, spin fan blade by hand; if it spins freely without resistance, replace fan clutch."
                        )
                    )
                )
            )
        ),

        DiagnosticSymptomCategory(
            id = "cat_transmission",
            categoryName = "Transmission Slipping or Flashing O/D Light",
            system = VehicleSystem.TRANSMISSION,
            commonSymptoms = listOf("Flashing O/D OFF light on dash", "Harsh 2-3 shift flare", "Transmission delays engaging Drive"),
            rootQuestion = DiagnosticQuestion(
                id = "q_transmission_symptom",
                questionText = "What behavior does the 5R55E automatic transmission exhibit?",
                options = listOf(
                    DiagnosticOption(
                        optionText = "Dashboard O/D OFF light flashes and engine revs up during 2-3 gear shift (shift flare)",
                        resultDiagnosis = DiagnosticResult(
                            title = "5R55E Valve Body Separator Plate Gasket Blowout",
                            problemSummary = "In 5R55E transmissions, the paper separator plate gasket routinely blows out near the EPC pressure solenoid channel, causing fluid pressure loss during the 2nd to 3rd gear shift.",
                            probableCause = "Blown valve body gasket plate or worn EPC solenoid pressure regulator.",
                            obdCode = "P0732 (Gear 2 Incorrect Ratio) or P0733",
                            targetComponentId = "transmission_solenoids",
                            confidencePercentage = 90,
                            urgencyLevel = "High",
                            recommendedAction = "Drop transmission fluid pan, inspect magnet for metal shavings, and replace valve body bond plate gasket and EPC solenoid."
                        )
                    )
                )
            )
        ),

        DiagnosticSymptomCategory(
            id = "cat_air_conditioning",
            categoryName = "A/C Blowing Warm Air",
            system = VehicleSystem.AIR_CONDITIONING,
            commonSymptoms = listOf("A/C blows warm air", "Compressor clutch clicks continuously", "Refrigerant pressure low"),
            rootQuestion = DiagnosticQuestion(
                id = "q_ac_symptom",
                questionText = "Does the A/C compressor clutch hub spin when A/C is turned ON?",
                options = listOf(
                    DiagnosticOption(
                        optionText = "Compressor pulley spins, but the front center clutch plate never engages or clicks rapidly every 2 seconds",
                        resultDiagnosis = DiagnosticResult(
                            title = "A/C Clutch Air Gap Too Wide or Low R134a Pressure",
                            problemSummary = "As the magnetic clutch shim wears down, the air gap widens past 0.035 inches, preventing the electromagnet from locking the clutch plate when hot. Alternatively, low pressure cutoff switch triggered.",
                            probableCause = "Excessive clutch air gap shim distance or minor R134a seal leakage.",
                            obdCode = "None",
                            targetComponentId = "ac_compressor",
                            confidencePercentage = 87,
                            urgencyLevel = "Medium",
                            recommendedAction = "Hook up manifold pressure gauges. If pressure is good, remove single clutch bolt and remove one shim spacer to adjust air gap to 0.020 inches."
                        )
                    )
                )
            )
        )
    )
}
