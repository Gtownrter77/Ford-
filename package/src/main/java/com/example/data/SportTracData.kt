package com.example.data

import com.example.data.local.MaintenanceEntity
import com.example.model.*
import com.example.util.SubAssemblyMeshGenerator

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

    // Design reminder: the workshop model uses restrained technical colors, asymmetric exploded vectors,
    // and explicit source labeling so geometry supports inspection rather than decorative illustration.
    private fun createRearShockMesh(): Pair<List<Point3D>, List<Face3D>> {
        val (bodyVertices, bodyFaces) = createBoxMesh(
            width = 0.34f, height = 1.45f, depth = 0.34f,
            center = Point3D(0f, -0.45f, 0f), colorHex = "#334155"
        )
        val (rodVertices, rodFaces) = createCylinderMesh(
            radius = 0.075f, height = 0.95f, segments = 12,
            center = Point3D(0f, 0.75f, 0f), colorHex = "#CBD5E1"
        )
        val (upperEyeVertices, upperEyeFaces) = createBoxMesh(
            width = 0.46f, height = 0.18f, depth = 0.38f,
            center = Point3D(0f, 1.28f, 0f), colorHex = "#64748B"
        )
        val (lowerEyeVertices, lowerEyeFaces) = createBoxMesh(
            width = 0.46f, height = 0.18f, depth = 0.38f,
            center = Point3D(0f, -1.23f, 0f), colorHex = "#64748B"
        )
        val vertices = bodyVertices + rodVertices + upperEyeVertices + lowerEyeVertices
        fun offsetFaces(faces: List<Face3D>, offset: Int) = faces.map { face ->
            face.copy(vertexIndices = face.vertexIndices.map { it + offset })
        }
        val faces = bodyFaces +
            offsetFaces(rodFaces, bodyVertices.size) +
            offsetFaces(upperEyeFaces, bodyVertices.size + rodVertices.size) +
            offsetFaces(lowerEyeFaces, bodyVertices.size + rodVertices.size + upperEyeVertices.size)
        return Pair(vertices, faces)
    }

    private fun generateSubAssemblies(id: String): List<SubAssemblyPart> {
        return when (id) {
            "engine_block" -> listOf(
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "head_gasket_left",
                    name = "Left Cylinder Head Multi-Layer Steel Gasket",
                    width = 1.8f, depth = 1.0f, thickness = 0.03f,
                    localOffset = Point3D(-0.4f, 0.4f, 0.2f),
                    explodeDir = Point3D(-0.5f, 0.8f, 0.2f),
                    explodeMultiplier = 1.8f,
                    colorHex = "#38BDF8",
                    specDetails = "MLS 3-Ply Cylinder Head Gasket • OEM #1L2Z-6051-BA"
                ),
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "head_gasket_right",
                    name = "Right Cylinder Head Multi-Layer Steel Gasket",
                    width = 1.8f, depth = 1.0f, thickness = 0.03f,
                    localOffset = Point3D(0.4f, 0.4f, 0.2f),
                    explodeDir = Point3D(0.5f, 0.8f, 0.2f),
                    explodeMultiplier = 1.8f,
                    colorHex = "#38BDF8",
                    specDetails = "MLS 3-Ply Cylinder Head Gasket • OEM #1L2Z-6051-AA"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "head_bolt_1",
                    name = "Cylinder Head TTY Flange Bolt M11",
                    headRadius = 0.12f, shankRadius = 0.06f, shankLength = 0.5f,
                    localOffset = Point3D(-0.6f, 0.7f, 0.5f),
                    explodeDir = Point3D(-0.6f, 1.2f, 0.5f),
                    explodeMultiplier = 3.6f,
                    colorHex = "#CBD5E1",
                    specDetails = "M11x1.50 TTY Head Bolt • Torque: 26 lb-ft + 90 deg"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "head_bolt_2",
                    name = "Cylinder Head TTY Flange Bolt M11",
                    headRadius = 0.12f, shankRadius = 0.06f, shankLength = 0.5f,
                    localOffset = Point3D(0.6f, 0.7f, 0.5f),
                    explodeDir = Point3D(0.6f, 1.2f, 0.5f),
                    explodeMultiplier = 3.6f,
                    colorHex = "#CBD5E1",
                    specDetails = "M11x1.50 TTY Head Bolt • Torque: 26 lb-ft + 90 deg"
                ),
                SubAssemblyMeshGenerator.createWasherSubAssembly(
                    id = "head_washer_1",
                    name = "Hardened Steel Head Bolt Washer",
                    innerRadius = 0.07f, outerRadius = 0.16f,
                    localOffset = Point3D(-0.6f, 0.55f, 0.5f),
                    explodeDir = Point3D(-0.6f, 1.0f, 0.5f),
                    explodeMultiplier = 2.7f,
                    colorHex = "#E2E8F0",
                    specDetails = "M11 Hardened Steel Washer"
                ),
                SubAssemblyMeshGenerator.createWasherSubAssembly(
                    id = "head_washer_2",
                    name = "Hardened Steel Head Bolt Washer",
                    innerRadius = 0.07f, outerRadius = 0.16f,
                    localOffset = Point3D(0.6f, 0.55f, 0.5f),
                    explodeDir = Point3D(0.6f, 1.0f, 0.5f),
                    explodeMultiplier = 2.7f,
                    colorHex = "#E2E8F0",
                    specDetails = "M11 Hardened Steel Washer"
                )
            )
            "intake_manifold" -> listOf(
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "intake_port_gasket_1",
                    name = "Molded Silicone Intake Runner Port Seal",
                    width = 1.2f, depth = 0.6f, thickness = 0.04f,
                    localOffset = Point3D(0f, 0.1f, 0f),
                    explodeDir = Point3D(0f, 1.2f, 0f),
                    explodeMultiplier = 1.8f,
                    colorHex = "#00F0FF",
                    specDetails = "Press-In Silicone Rubber Port Seal • OEM #2L2Z-9441-AA"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "intake_bolt_1",
                    name = "Lower Intake Mounting Hex Flange Bolt",
                    headRadius = 0.10f, shankRadius = 0.04f, shankLength = 0.35f,
                    localOffset = Point3D(-0.5f, 0.4f, 0.3f),
                    explodeDir = Point3D(-0.5f, 1.5f, 0.3f),
                    explodeMultiplier = 3.5f,
                    colorHex = "#CBD5E1",
                    specDetails = "M6x1.00 Flange Bolt • Torque: 89 in-lbs"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "intake_bolt_2",
                    name = "Lower Intake Mounting Hex Flange Bolt",
                    headRadius = 0.10f, shankRadius = 0.04f, shankLength = 0.35f,
                    localOffset = Point3D(0.5f, 0.4f, 0.3f),
                    explodeDir = Point3D(0.5f, 1.5f, 0.3f),
                    explodeMultiplier = 3.5f,
                    colorHex = "#CBD5E1",
                    specDetails = "M6x1.00 Flange Bolt • Torque: 89 in-lbs"
                ),
                SubAssemblyMeshGenerator.createWasherSubAssembly(
                    id = "intake_washer_1",
                    name = "Belleville Intake Lock Washer",
                    innerRadius = 0.05f, outerRadius = 0.12f,
                    localOffset = Point3D(-0.5f, 0.28f, 0.3f),
                    explodeDir = Point3D(-0.5f, 1.1f, 0.3f),
                    explodeMultiplier = 2.5f,
                    colorHex = "#E2E8F0",
                    specDetails = "M6 Belleville Lock Washer"
                )
            )
            "thermostat_housing" -> listOf(
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "stat_o_ring",
                    name = "Thermostat Housing Rubber O-Ring Seal",
                    width = 0.5f, depth = 0.5f, thickness = 0.03f,
                    localOffset = Point3D(0f, -0.1f, 0f),
                    explodeDir = Point3D(0f, 1.2f, 0f),
                    explodeMultiplier = 1.7f,
                    colorHex = "#F97316",
                    specDetails = "Viton Thermostat O-Ring Seal • OEM #F87Z-8255-AA"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "stat_bolt_1",
                    name = "Thermostat Housing Mounting Bolt",
                    headRadius = 0.09f, shankRadius = 0.04f, shankLength = 0.30f,
                    localOffset = Point3D(-0.25f, 0.25f, 0.2f),
                    explodeDir = Point3D(-0.3f, 1.4f, 0.2f),
                    explodeMultiplier = 3.4f,
                    colorHex = "#CBD5E1",
                    specDetails = "M6x1.0 Flange Bolt • Torque: 89 in-lbs"
                ),
                SubAssemblyMeshGenerator.createWasherSubAssembly(
                    id = "stat_washer_1",
                    name = "Sealing Flat Washer",
                    innerRadius = 0.045f, outerRadius = 0.11f,
                    localOffset = Point3D(-0.25f, 0.15f, 0.2f),
                    explodeDir = Point3D(-0.3f, 1.0f, 0.2f),
                    explodeMultiplier = 2.4f,
                    colorHex = "#E2E8F0",
                    specDetails = "M6 Stainless Flat Washer"
                )
            )
            "water_pump", "radiator_assembly" -> listOf(
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "wp_gasket",
                    name = "Water Pump Flange Paper/Silicone Gasket",
                    width = 0.7f, depth = 0.6f, thickness = 0.02f,
                    localOffset = Point3D(0f, 0f, -0.1f),
                    explodeDir = Point3D(0f, 0f, 1.4f),
                    explodeMultiplier = 1.8f,
                    colorHex = "#38BDF8",
                    specDetails = "Molded Paper Gasket with Silicone Bead"
                ),
                SubAssemblyMeshGenerator.createSerpentineBeltSubAssembly(
                    id = "serpentine_belt",
                    name = "6-Rib EPDM Serpentine Accessory Drive Belt",
                    pulleyCenters = listOf(
                        Point3D(-0.6f, 0.5f, 0.1f),
                        Point3D(0.6f, 0.5f, 0.1f),
                        Point3D(0.5f, -0.4f, 0.1f),
                        Point3D(-0.5f, -0.4f, 0.1f)
                    ),
                    localOffset = Point3D(0f, 0f, 0.3f),
                    explodeDir = Point3D(0f, 0f, 1f),
                    explodeMultiplier = 2.4f,
                    colorHex = "#1E293B",
                    specDetails = "Motorcraft JK6-825 6-Rib Belt"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "wp_bolt_1",
                    name = "Water Pump Flange Bolt",
                    headRadius = 0.09f, shankRadius = 0.04f, shankLength = 0.32f,
                    localOffset = Point3D(-0.25f, 0.2f, 0.25f),
                    explodeDir = Point3D(-0.25f, 0.2f, 1.5f),
                    explodeMultiplier = 3.5f,
                    colorHex = "#CBD5E1",
                    specDetails = "M6x1.00 Flange Bolt • Torque: 89 in-lbs"
                )
            )
            else -> listOf(
                SubAssemblyMeshGenerator.createGasketSubAssembly(
                    id = "${id}_gasket",
                    name = "Component Flange Seal Gasket",
                    width = 0.8f, depth = 0.5f, thickness = 0.03f,
                    localOffset = Point3D(0f, -0.05f, 0f),
                    explodeDir = Point3D(0f, 1.2f, 0f),
                    explodeMultiplier = 1.8f,
                    colorHex = "#38BDF8",
                    specDetails = "OEM Rubber Flange Seal"
                ),
                SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                    id = "${id}_bolt_1",
                    name = "OEM Mounting Hex Flange Bolt",
                    headRadius = 0.09f, shankRadius = 0.04f, shankLength = 0.30f,
                    localOffset = Point3D(-0.3f, 0.2f, 0.2f),
                    explodeDir = Point3D(-0.3f, 1.4f, 0.2f),
                    explodeMultiplier = 3.4f,
                    colorHex = "#CBD5E1",
                    specDetails = "M8 Flange Bolt • Torque: 18 lb-ft"
                ),
                SubAssemblyMeshGenerator.createWasherSubAssembly(
                    id = "${id}_washer_1",
                    name = "Belleville Lock Washer",
                    innerRadius = 0.045f, outerRadius = 0.11f,
                    localOffset = Point3D(-0.3f, 0.1f, 0.2f),
                    explodeDir = Point3D(-0.3f, 1.0f, 0.2f),
                    explodeMultiplier = 2.4f,
                    colorHex = "#E2E8F0",
                    specDetails = "M8 Lock Washer"
                )
            )
        }
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
                replacementIntervalMiles = 200000,
                subAssemblies = generateSubAssemblies("engine_block")
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
        },

        // 17. BRAKE MASTER CYLINDER, VACUUM BOOSTER & ABS MODULE
        run {
            val (v, f) = createCylinderMesh(0.6f, 1.4f, 8, Point3D(-0.7f, 0.7f, 0.4f), "#EF4444")
            Component3DModel(
                id = "abs_master_cylinder_3d",
                name = "Brake Master Cylinder, Power Vacuum Booster & 4-Wheel ABS Pump",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "1L2Z-2140-AB",
                description = "Cast aluminum dual-reservoir brake master cylinder, 10-inch vacuum power booster diaphragm, and 4-channel hydraulic anti-lock brake pump control module.",
                locationDescription = "Driver side engine firewall directly behind brake pedal arm.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.7f, 0.7f, 0.4f),
                explodeVector = Point3D(-1.4f, 1.2f, 0.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Master Cylinder Mounting Nuts to Booster", "18", "25", "Replace paper seal gasket"),
                    TorqueSpec("Brake Fluid Line Fitting Nuts", "14", "19", "Use 3/8-in & 7/16-in flare nut wrenches"),
                    TorqueSpec("ABS Module Hydraulic Unit Bolts", "89 in-lbs", "10 Nm", "")
                ),
                requiredTools = listOf("Flare Nut Wrench Set", "Brake Bleeder Kit", "DOT 3 / DOT 4 Brake Fluid", "FORScan / OBD scanner for ABS pump bleed"),
                repairSteps = listOf(
                    RepairStep(1, "Syphon Old Brake Fluid", "Extract dark degraded fluid from master cylinder translucent reservoir with turkey baster or syringe."),
                    RepairStep(2, "Disconnect Brake Lines", "Unscrew two hydraulic flare line fittings on master cylinder body using flare nut wrench to prevent rounding nuts.", warning = "Brake fluid damages automotive body paint on contact. Wipe up any spills immediately with soapy water."),
                    RepairStep(3, "Unbolt Booster Nuts", "Unscrew two 13mm nuts securing master cylinder to vacuum booster shell."),
                    RepairStep(4, "Bench Bleed New Master Cylinder", "Bench bleed new master cylinder with clear tubing loops before installing onto truck to purge air pockets."),
                    RepairStep(5, "Bleed 4 Wheel Calipers", "Bleed brakes in order: Right Rear, Left Rear, Right Front, Left Front until pedal feels rock solid.")
                ),
                commonSymptoms = listOf("Brake pedal slowly sinks to floorboard at stoplights", "ABS warning light illuminated on dashboard", "Spongy brake pedal feel after replacing brake pads"),
                replacementIntervalMiles = 60000
            )
        },

        // 18. REAR DISC / DRUM BRAKES & PARKING BRAKE SHOES
        run {
            val (v, f) = createBoxMesh(2.6f, 0.8f, 1.0f, Point3D(0f, -0.9f, -3.6f), "#EF4444")
            Component3DModel(
                id = "rear_brakes_3d",
                name = "Rear Disc Brakes & Internal Drum Parking Brake Shoes",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "2L2Z-2200-AA",
                description = "Solid rear brake rotors with integrated hat drum for parking brake shoes, single-piston calipers, and mechanical emergency brake tension cables.",
                locationDescription = "Rear axle ends behind 16-inch alloy wheels.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0f, -0.9f, -3.6f),
                explodeVector = Point3D(0f, -1.5f, -4.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Rear Caliper Mounting Pins", "26", "35", "Apply silicone brake lubricant"),
                    TorqueSpec("Rear Caliper Bracket Bolts", "70", "95", "Apply threadlocker"),
                    TorqueSpec("Wheel Lug Nuts", "100", "135", "Torque to 100 lb-ft")
                ),
                requiredTools = listOf("12mm & 15mm Sockets", "Brake Caliper Piston Press", "Parking Brake Spring Pliers", "Brake Cleaner Spray"),
                repairSteps = listOf(
                    RepairStep(1, "Release Parking Brake Cable", "Ensure emergency handbrake lever inside cab is completely released."),
                    RepairStep(2, "Remove Rear Caliper & Bracket", "Unbolt two 12mm guide pins and two 15mm anchor bracket bolts to detach rear caliper assembly."),
                    RepairStep(3, "Slide Off Rotor Hat", "Pull rotor straight off axle studs. If stuck, thread M8 bolts into threaded rotor removal holes to push off hub."),
                    RepairStep(4, "Inspect Parking Brake Shoes", "Check internal drum shoe lining thickness and adjust star-wheel star gear tensioner."),
                    RepairStep(5, "Compress Piston & Reassemble", "Push single caliper piston back into bore and assemble with fresh ceramic brake pads.")
                ),
                commonSymptoms = listOf("Grinding noise from rear axle when stopping", "Parking brake pedal goes all the way to floor without holding truck on hill", "Rear wheel hot to touch from dragging caliper"),
                replacementIntervalMiles = 45000
            )
        },

        // 19. STAINLESS STEEL CAT-BACK EXHAUST MUFFLER & TAILPIPE
        run {
            val (v, f) = createCylinderMesh(0.7f, 3.4f, 8, Point3D(0.5f, -0.7f, -2.2f), "#FF6F00")
            Component3DModel(
                id = "catback_exhaust_muffler_3d",
                name = "Stainless Steel Cat-Back Exhaust Muffler & Mandrel Tailpipe",
                system = VehicleSystem.ENGINE,
                oemPartNumber = "2L2Z-5230-AA",
                description = "Full 2.5-inch aluminized / stainless steel replacement exhaust system including acoustic chamber muffler, tailpipe over rear axle, and heavy-duty rubber isolator hangers.",
                locationDescription = "Under vehicle center body extending from Y-pipe catalytic converter outlet to passenger side rear bumper.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.5f, -0.7f, -2.2f),
                explodeVector = Point3D(1.8f, -0.7f, -2.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Exhaust Flange Clamp Bolts", "35", "47", "Use heavy duty U-bolt clamps"),
                    TorqueSpec("Cat-Back Inlet Flange Nuts", "30", "41", "Apply high temp anti-seize")
                ),
                requiredTools = listOf("Exhaust Hanger Removal Pliers", "Sawzall / Reciprocating Saw (if replacing rusted stock pipe)", "15mm & 16mm Sockets", "Penetrating Fluid"),
                repairSteps = listOf(
                    RepairStep(1, "Spray Exhaust Hangers", "Soak rubber isolation hangers in soapy water or silicone spray for easy sliding."),
                    RepairStep(2, "Unbolt Y-Pipe Inlet Flange", "Remove two 15mm flange nuts connecting cat-back inlet to catalytic converter Y-pipe."),
                    RepairStep(3, "Pry Hangers & Remove Muffler", "Use exhaust hanger pliers to pop mounting prongs out of rubber isolators and slide muffler rearward over axle."),
                    RepairStep(4, "Install New Stainless System", "Slide replacement muffler over axle tube, align polished tailpipe tip with rear bumper cut-out, and torque exhaust clamps.")
                ),
                commonSymptoms = listOf("Loud rumbling or buzzing exhaust drone while driving", "Rattling noise under cargo bed floor when idling", "Visible rust hole or dark soot marks on muffler casing"),
                replacementIntervalMiles = 100000
            )
        },

        // 20. HEATED OXYGEN SENSORS (HO2S UPSTREAM & DOWNSTREAM)
        run {
            val (v, f) = createCylinderMesh(0.35f, 0.8f, 6, Point3D(-0.3f, -0.3f, -0.4f), "#EAB308")
            Component3DModel(
                id = "oxygen_sensors_3d",
                name = "Upstream & Downstream Heated Oxygen Sensors (HO2S)",
                system = VehicleSystem.ENGINE,
                oemPartNumber = "1L2Z-9F472-AA",
                description = "Zirconia 4-wire heated oxygen sensors (Bank 1 & Bank 2 Sensor 1 upstream for fuel trim feedback, and Sensor 2 downstream for catalytic converter monitor).",
                locationDescription = "Threaded directly into exhaust manifolds and Y-pipe before and after catalytic converters.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.3f, -0.3f, -0.4f),
                explodeVector = Point3D(-0.8f, 0.5f, -0.4f),
                torqueSpecs = listOf(
                    TorqueSpec("O2 Sensor Thread Torque", "30", "41", "Apply high-temp nickel anti-seize to threads (avoid sensor tip)")
                ),
                requiredTools = listOf("22mm (7/8-in) Slotted O2 Sensor Socket", "Penetrating Fluid (PB Blaster)", "Propane Torch (if seized)", "Wire Harness Cleaner"),
                repairSteps = listOf(
                    RepairStep(1, "Soak Threads in Penetrating Fluid", "Spray PB Blaster on threaded boss 15 minutes before attempting removal."),
                    RepairStep(2, "Unplug 4-Pin Harness Connector", "Press locking tab on oxygen sensor pigtail harness connector and pull apart."),
                    RepairStep(3, "Unscrew O2 Sensor", "Slip slotted 22mm O2 sensor socket over wire harness and break sensor loose counter-clockwise."),
                    RepairStep(4, "Apply Anti-Seize & Thread New Sensor", "Coat threads with nickel anti-seize paste, hand thread into boss, and torque to 30 lb-ft.")
                ),
                commonSymptoms = listOf("Check Engine Light P0133, P0135, P0141 or P0153 (O2 Sensor Circuit / Slow Response)", "Poor fuel economy (12-14 MPG)", "Rich exhaust smell"),
                replacementIntervalMiles = 75000
            )
        },

        // 21. ALL-TERRAIN TIRES & 16-INCH CAST ALUMINUM WHEELS
        run {
            val (v, f) = createCylinderMesh(1.4f, 0.9f, 10, Point3D(1.1f, -0.8f, 2.0f), "#64748B")
            Component3DModel(
                id = "tires_wheels_3d",
                name = "P265/70R16 All-Terrain Tires & 16x7-inch Aluminum Wheels",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "1L2Z-1007-BA",
                description = "265/70R16 112T All-Terrain tires mounted on 16x7-inch 5-spoke machined cast aluminum wheels with 5x114.3mm (5x4.5) bolt pattern and 1/2-in-20 lug studs.",
                locationDescription = "Four wheel corners supporting truck suspension and payload weight.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 30,
                vertices = v,
                faces = f,
                centerOffset = Point3D(1.1f, -0.8f, 2.0f),
                explodeVector = Point3D(2.2f, -0.8f, 2.5f),
                torqueSpecs = listOf(
                    TorqueSpec("Wheel Lug Nuts (1/2-in-20 Thread)", "100", "135", "Tighten in 5-lug star criss-cross pattern"),
                    TorqueSpec("Tire Cold Inflation Pressure", "35 PSI Front / 35 PSI Rear", "35 PSI", "Check pressure when tires are cold")
                ),
                requiredTools = listOf("19mm (3/4-in) Deep Socket", "Calibrated Torque Wrench (100 lb-ft)", "Hydraulic Floor Jack & Heavy Jack Stands", "Digital Tire Pressure Gauge"),
                repairSteps = listOf(
                    RepairStep(1, "Loosen Lug Nuts on Ground", "Crack loose 5 lug nuts 1/2 turn counter-clockwise while tires are resting on the ground."),
                    RepairStep(2, "Jack Up Frame Corner", "Place hydraulic jack under front lower control arm or rear axle tube and raise vehicle onto safety jack stands."),
                    RepairStep(3, "Remove Wheel Assembly", "Unscrew lug nuts completely and pull wheel straight off hub studs."),
                    RepairStep(4, "Mount Wheel & Torque in Star Pattern", "Hand-thread lug nuts, lower truck to ground, and torque each lug to 100 lb-ft in a 5-point star pattern.")
                ),
                commonSymptoms = listOf("Vibration in steering wheel at 55-65 MPH (unbalanced tire or bent wheel rim)", "Uneven tire tread wear on inside edge (front alignment toe/camber out)", "Slow air leak around tire valve stem or rim bead"),
                replacementIntervalMiles = 50000
            )
        },

        // 22. FRONT WHEEL HUB BEARING ASSEMBLY & LUG STUDS
        run {
            val (v, f) = createCylinderMesh(0.7f, 1.1f, 8, Point3D(0.9f, -0.8f, 1.8f), "#EF4444")
            Component3DModel(
                id = "wheel_bearings_hubs_3d",
                name = "Front Wheel Hub & Sealed Bearing Assembly with ABS Sensor",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "2L2Z-1104-AB",
                description = "Complete unitized front wheel hub assembly with pre-greased sealed double-row roller bearings, 5 pressed wheel studs, and integrated ABS wheel speed sensor wiring harness.",
                locationDescription = "Bolted inside front steering knuckle behind brake rotor.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.9f, -0.8f, 1.8f),
                explodeVector = Point3D(1.8f, -0.8f, 2.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Front Axle Shaft Nut (4WD Models)", "184", "250", "Must use 32mm socket & replace cotter pin"),
                    TorqueSpec("Hub Bearing Assembly to Knuckle Bolts", "83", "112", "Torque three 15mm mounting bolts"),
                    TorqueSpec("Brake Caliper Anchor Bracket Bolts", "85", "115", "Apply blue threadlocker")
                ),
                requiredTools = listOf("32mm Axle Nut Socket", "15mm 6-Point Socket", "Slide Hammer / Hub Puller", "Torque Wrench (up to 200 lb-ft)"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Axle Nut & Brake Rotor", "Remove 32mm front CV axle nut, caliper, caliper anchor bracket, and brake rotor."),
                    RepairStep(2, "Disconnect ABS Harness Connector", "Unplug ABS wheel speed sensor harness connector pinned behind plastic inner fender liner."),
                    RepairStep(3, "Unbolt Hub Assembly", "Remove three 15mm bolts from back side of steering knuckle and pull hub off CV axle spline."),
                    RepairStep(4, "Install Fresh Hub & Torque Axle Nut", "Clean knuckle bore, slide new hub onto axle shaft, tighten 3 knuckle bolts to 83 lb-ft, and torque axle nut to 184 lb-ft.")
                ),
                commonSymptoms = listOf("Loud humming, growling or roaring wheel noise that increases with road speed", "ABS indicator light illuminated with speed sensor code", "Steering wheel looseness or wheel play when wiggled at 12 and 6 o'clock"),
                replacementIntervalMiles = 90000
            )
        },

        // 23. DASHBOARD INSTRUMENT CLUSTER & GAUGE BEZEL
        run {
            val (v, f) = createBoxMesh(2.2f, 0.9f, 0.8f, Point3D(-0.4f, 0.6f, 0.8f), "#38BDF8")
            Component3DModel(
                id = "dash_dashboard_cluster_3d",
                name = "Instrument Cluster Gauges, White Face Overlay & Dash Bezel",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-10849-BA",
                description = "Sport Trac white-face gauge cluster including 120 MPH speedometer, tachometer, fuel level, engine coolant temp, oil pressure, battery voltage gauges, and backlighting bulb circuit board.",
                locationDescription = "Inside driver cab dashboard directly behind steering wheel.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.4f, 0.6f, 0.8f),
                explodeVector = Point3D(-0.4f, 1.2f, 1.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Dash Trim Bezel Screws (7mm Head)", "18 in-lbs", "2 Nm", "Do not overtighten into plastic clips"),
                    TorqueSpec("Instrument Cluster Mounting Screws", "22 in-lbs", "2.5 Nm", "Hand tighten securely")
                ),
                requiredTools = listOf("7mm Socket & Nut Driver", "Plastic Trim Removal Pry Tool", "T15 Torx Driver", "Replacement #194 / #37 Incandescent or LED Bulbs"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Negative Battery Cable", "Disconnect battery negative terminal to prevent shorting dash electrical circuits."),
                    RepairStep(2, "Remove Lower Steering Column Cover", "Unscrew two 7mm screws and unclip plastic lower dash knee bolster trim panel."),
                    RepairStep(3, "Unscrew Instrument Bezel", "Remove four 7mm screws securing dash cluster surround bezel and unclip headlight switch electrical harness."),
                    RepairStep(4, "Pull Cluster & Unplug Wiring Connectors", "Remove four 7mm cluster screws, tilt unit forward, and depress release tabs on two main wire harness connectors."),
                    RepairStep(5, "Replace Backlight Bulbs or Solder Joints", "Twist bulb sockets 1/4 turn to replace burnt gauge illumination bulbs or repair odometer display cold solder joints.")
                ),
                commonSymptoms = listOf("Odometer & gear indicator display flickering or turning completely black (cracked PCB solder joints)", "Instrument gauge backlights burnt out or dark at night", "Speedometer or tachometer needle sticking or jumping erratically"),
                replacementIntervalMiles = 100000
            )
        },

        // 24. MAIN DASHBOARD WIRING HARNESS & FUSE BOX (CJB)
        run {
            val (v, f) = createBoxMesh(2.4f, 0.6f, 1.2f, Point3D(0.0f, 0.4f, 0.6f), "#EAB308")
            Component3DModel(
                id = "dash_wiring_harness_3d",
                name = "Main Dash Wiring Harness & Central Junction Box (Inside Fuse Panel)",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "2L2Z-14401-AA",
                description = "Complete under-dash main wiring harness interfacing Central Junction Box (CJB interior fuse panel), GEM (Generic Electronic Module), ignition switch, HVAC controls, and radio audio connections.",
                locationDescription = "Runs across entire firewall beneath dashboard pad from driver kick panel fuse box to glovebox.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 180,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.4f, 0.6f),
                explodeVector = Point3D(0.0f, 0.9f, 1.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Central Junction Box Mounting Bolts", "89 in-lbs", "10 Nm", "Hand start bolts"),
                    TorqueSpec("Dash Skeleton Reinforcement Bracket Bolts", "18", "25", "10mm socket")
                ),
                requiredTools = listOf("10mm & 8mm Sockets", "Digital Multimeter & Wire Strippers", "Electrical Contact Cleaner Spray", "Automotive Fuse Tester & Puller"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery & Disarm Airbags", "Disconnect battery terminals and wait 10 minutes to safely discharge airbag backup capacitors before touching dash wiring."),
                    RepairStep(2, "Access Driver Kick Panel Fuse Box", "Remove left lower trim panel to expose Central Junction Box fuse block and GEM module connectors."),
                    RepairStep(3, "Inspect Relay & Fuse Contacts", "Use multimeter or continuity tester to check mini-fuses (10A, 15A, 20A) and ISO relays for corrosion or melting."),
                    RepairStep(4, "Trace & Solder Damaged Wire Leads", "Repair melted wires or broken ground rings using heat-shrink tubing and rosin-core solder.")
                ),
                commonSymptoms = listOf("Intermittent power window, door lock, or wiper failure (GEM module circuit glitch)", "Blows interior fuses repeatedly upon turning key to RUN", "Melted wire insulation or burnt electrical plastic odor behind dashboard"),
                replacementIntervalMiles = 150000
            )
        },

        // 25. POWER MOONROOF GLASS PANEL & WEATHERSTRIP SEALS
        run {
            val (v, f) = createBoxMesh(2.2f, 0.2f, 1.8f, Point3D(0.0f, 1.4f, -0.4f), "#0284C7")
            Component3DModel(
                id = "sunroof_glass_frame_3d",
                name = "Power Moonroof Tempered Glass Panel & Weatherstrip Perimeter Seal",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-78502A00-AA",
                description = "Dark tint solar glass tempered moonroof panel with metal perimeter carrier frame, adjustable height tilt brackets, and rubber perimeter weatherstrip seal.",
                locationDescription = "Roof panel above driver and front passenger seats.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 60,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.4f, -0.4f),
                explodeVector = Point3D(0.0f, 2.5f, -0.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Glass Panel Corner Fastener Screws (T25 Torx)", "27 in-lbs", "3 Nm", "Hand tighten carefully to avoid cracking glass frame"),
                    TorqueSpec("Sunroof Frame Assembly Reinforcement Bolts", "89 in-lbs", "10 Nm", "")
                ),
                requiredTools = listOf("T25 Torx Screwdriver", "Plastic Trim Removal Wedges", "Silicone Weatherstrip Lubricant", "Denatured Alcohol Cleaner"),
                repairSteps = listOf(
                    RepairStep(1, "Tilt Sunroof Glass Open", "Press overhead console switch to place moonroof in full TILT-UP position."),
                    RepairStep(2, "Remove Side Accordion Blind Covers", "Unclip plastic rubber side accordion bellows covers to expose four T25 Torx mounting screws."),
                    RepairStep(3, "Unscrew Glass Corner Torx Screws", "Remove four T25 Torx screws securing glass panel brackets to sliding track mechanism."),
                    RepairStep(4, "Lift Out Glass Panel & Inspect Rubber Seal", "Lift tempered glass panel straight up off roof opening and check bulb seal for cracks or deterioration."),
                    RepairStep(5, "Adjust Flush Glass Height", "Reinstall glass and adjust corner height so top glass edge rests 1mm below roof line to prevent wind noise.")
                ),
                commonSymptoms = listOf("Water leaking into interior headliner or front A-pillar grab handles during heavy rain", "Excessive wind whistle or air rush noise at highway speeds (55+ MPH)", "Cracked, chipped or shattered top glass panel"),
                replacementIntervalMiles = 100000
            )
        },

        // 26. SUNROOF DRIVE MOTOR, HELICAL CABLES & GUIDE TRACK RAILS
        run {
            val (v, f) = createCylinderMesh(0.5f, 1.8f, 8, Point3D(0.0f, 1.3f, -0.9f), "#EAB308")
            Component3DModel(
                id = "sunroof_motor_tracks_3d",
                name = "Sunroof Electric Drive Motor, Helical Drive Cables & Track Rails",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-15790-AA",
                description = "High-torque 12V electric drive motor with worm gear gearbox, twin flexible spiral/helical push-pull cables, aluminum guide track channels, and internal limit switches.",
                locationDescription = "Mounted inside roof ceiling structure behind overhead console and beneath front headliner.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 150,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.3f, -0.9f),
                explodeVector = Point3D(0.0f, 2.2f, -1.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Sunroof Drive Motor Mounting Screws (T20 Torx)", "35 in-lbs", "4 Nm", "Apply blue threadlocker"),
                    TorqueSpec("Roof Headliner Grab Handle Screw Bolts", "30 in-lbs", "3.5 Nm", "")
                ),
                requiredTools = listOf("T20 & T25 Torx Drivers", "White Lithium Grease", "4mm Allen Wrench (for manual emergency crank)", "Multimeter / Wire Probe"),
                repairSteps = listOf(
                    RepairStep(1, "Lower Front Roof Headliner", "Remove sun visors, center overhead console, and front A-pillar trim panels to drop headliner down 6 inches."),
                    RepairStep(2, "Unplug Motor Wire Harness", "Disconnect 3-pin power & ground electrical connector on drive motor attached to front frame crossmember."),
                    RepairStep(3, "Unscrew Drive Motor", "Remove three T20 Torx screws securing motor gearbox to cable housing assembly."),
                    RepairStep(4, "Clean & Grease Helical Cables", "Clean accumulated dirt/debris out of aluminum track grooves with brake cleaner and apply fresh white lithium grease."),
                    RepairStep(5, "Calibrate & Synchronize Track Positions", "Run manual re-initialization sequence (hold TILT switch 10 seconds) until motor resets home position stops.")
                ),
                commonSymptoms = listOf("Sunroof gets stuck halfway open or tilts unevenly to one side", "Loud popping, grinding or clicking gear noise when pressing open/close switch", "Motor hums but glass does not slide back or move"),
                replacementIntervalMiles = 120000
            )
        },

        // 27. SUNROOF DRAIN TUBES, SLIDING SUNSHADE & SWITCH
        run {
            val (v, f) = createCylinderMesh(0.25f, 2.8f, 6, Point3D(-0.8f, 1.1f, -0.4f), "#10B981")
            Component3DModel(
                id = "sunroof_drain_tubes_shade_3d",
                name = "Sunroof Water Drain Hoses, Interior Sunshade & Overhead Switch",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-78502C52-AA",
                description = "Four vinyl corner water drainage tubes (routing through A-pillars and C-pillars), vinyl vinyl-wrapped sliding interior fabric sunshade panel, and overhead rocker control switch.",
                locationDescription = "Four corners of sunroof tray routing down front A-pillars to fender wells and rear C-pillars.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.8f, 1.1f, -0.4f),
                explodeVector = Point3D(-1.6f, 1.8f, -0.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Overhead Console Mounting Screws", "18 in-lbs", "2 Nm", "Snug tight")
                ),
                requiredTools = listOf("Weed Trimmer Nylon Line (0.080-in) or Air Blow Gun", "7mm Socket", "Trim Removal Tools", "Rubbing Alcohol"),
                repairSteps = listOf(
                    RepairStep(1, "Locate Tray Corner Drain Hole", "Open sunroof fully and inspect front left and right corners of aluminum water trough tray."),
                    RepairStep(2, "Clear Clogged Drain Lines", "Snake flexible nylon string trimmer line down corner drain grommets to push out dirt, pine needles, and leaves."),
                    RepairStep(3, "Flush Lines with Warm Water", "Pour a small cup of warm water into tray channel to verify water drains freely beneath front tires onto ground."),
                    RepairStep(4, "Replace Loose Drain Hose Fittings", "If leaking onto floorboards, remove A-pillar trim and reconnect loose drain hose elbow fitting to body exit firewall grommet.")
                ),
                commonSymptoms = listOf("Water dripping onto driver or passenger lap from overhead console after car wash", "Wet damp carpet or standing water in front footwells", "Musty mildew odor inside cab"),
                replacementIntervalMiles = 50000
            )
        },

        // 28. FRONT LAMINATED SAFETY WINDSHIELD, WIPERS & COWL GRID
        run {
            val (v, f) = createBoxMesh(2.4f, 0.9f, 1.2f, Point3D(0.0f, 1.0f, 0.4f), "#38BDF8")
            Component3DModel(
                id = "front_windshield_3d",
                name = "Front Acoustic Safety Laminated Windshield, Wipers & Cowl Grille",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-7803100-BA",
                description = "Solar-control laminated safety glass front windshield with acoustic layer, integrated rearview mirror bracket, dual wiper arms with 22-inch blades, washer jet nozzles, and plastic wiper cowl intake grille.",
                locationDescription = "Front cabin windshield frame resting above hood line and dashboard.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 60,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.0f, 0.4f),
                explodeVector = Point3D(0.0f, 1.9f, 1.0f),
                torqueSpecs = listOf(
                    TorqueSpec("Wiper Arm Mounting Pivot Nuts", "14", "19", "Pop plastic nut cover cap off first"),
                    TorqueSpec("Wiper Linkage Motor Mounting Bolts", "89 in-lbs", "10 Nm", "Apply blue threadlocker")
                ),
                requiredTools = listOf("Wiper Arm Puller / Small Battery Terminal Puller", "15mm Socket & Extension", "Urethane Windshield Cutout Wire / Knife", "Glass Cleaner & Microfiber"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Plastic Wiper Arm Cap & Nut", "Pop 15mm nut cap off wiper pivot arms and unscrew nut."),
                    RepairStep(2, "Use Puller to Pop Wiper Arms", "Attach wiper arm puller tool to press wiper arms off tapered splined shafts."),
                    RepairStep(3, "Remove Plastic Cowl Retaining Pins", "Unclip plastic push-pins securing driver and passenger wiper cowl grille halves."),
                    RepairStep(4, "Slice Polyurethane Bead Seal", "When replacing glass, cut perimeter automotive urethane bead using cold knife or cut wire, clean pinchweld, and apply primer/urethane bead.")
                ),
                commonSymptoms = listOf("Spiderweb cracks, bullseye stone chips or stress cracks obscuring driver vision", "Streaking or chattering wiper blades across glass during rain", "Water leaking under dashboard onto carpet after heavy downpour"),
                replacementIntervalMiles = 100000
            )
        },

        // 29. POWER DROP-DOWN REAR SLIDING GLASS WINDOW & MOTOR
        run {
            val (v, f) = createBoxMesh(2.2f, 0.8f, 0.2f, Point3D(0.0f, 1.0f, -2.4f), "#0284C7")
            Component3DModel(
                id = "rear_window_power_slide_3d",
                name = "Power Drop-Down Rear Glass Window, Regulator & Weatherstrip Channel",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-7842006-AA",
                description = "Signature Sport Trac full-width power drop-down rear window assembly with solar tinted tempered glass, electric cable-driven window regulator motor, defroster heating grid, and lower trough drainage seals.",
                locationDescription = "Rear cab wall separating passenger cabin from truck bed cargo box.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.0f, -2.4f),
                explodeVector = Point3D(0.0f, 1.8f, -3.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Rear Window Glass Clamp Bolts", "44 in-lbs", "5 Nm", "Tighten carefully into rubber glass channel"),
                    TorqueSpec("Rear Regulator Assembly Nut Screws", "89 in-lbs", "10 Nm", "")
                ),
                requiredTools = listOf("10mm & 8mm Sockets", "Torx T20 Screwdriver", "Silicone Spray Lubricant", "Trim Removal Pry Tool"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Rear Seat Backrest & Back Wall Carpet", "Fold down rear seats and unclip rear carpet insulation panel to access lower cab wall."),
                    RepairStep(2, "Unbolt Lower Access Cover Panel", "Remove 10mm bolts securing steel access cover plate exposing window regulator tracks."),
                    RepairStep(3, "Disconnect Power Window Motor Harness", "Unplug 2-pin electrical connector for rear power sliding window motor."),
                    RepairStep(4, "Unbolt Glass Clamp Brackets & Lift Glass", "Loosen glass clamp bolts and carefully slide rear glass panel up out of lower channel guide track.")
                ),
                commonSymptoms = listOf("Rear glass gets stuck open or refuses to drop down when pressing dash switch", "Grinding, clicking or popping sound coming from behind rear seats when lowering window", "Water leaking into rear carpet under rear seat bench during rain"),
                replacementIntervalMiles = 120000
            )
        },

        // 30. DRIVER AIRBAG MODULE & STEERING COLUMN CLOCK SPRING
        run {
            val (v, f) = createCylinderMesh(0.6f, 0.5f, 8, Point3D(-0.4f, 0.5f, 0.6f), "#EF4444")
            Component3DModel(
                id = "airbag_driver_clockspring_3d",
                name = "Driver Steering Wheel Airbag Module & Spiral Clock Spring Harness",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-14A664-AB",
                description = "Dual-stage pyrotechnic driver frontal airbag inflator module mounted behind steering wheel emblem cover, paired with multi-channel spiral cable clock spring harness supplying continuous electrical contact for horn, cruise control switches, and airbag ignition circuits.",
                locationDescription = "Mounted in center hub of steering wheel column.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.4f, 0.5f, 0.6f),
                explodeVector = Point3D(-0.4f, 1.2f, 1.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Airbag Steering Wheel Side Screws (8mm Head)", "84 in-lbs", "9.5 Nm", "Two side cover screws"),
                    TorqueSpec("Steering Wheel Center Hub Bolt", "33", "45", "Apply blue threadlocker")
                ),
                requiredTools = listOf("8mm Socket & Extension", "Steering Wheel Puller Kit", "Plastic Trim Removal Wedges", "T30 Torx Driver"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery & Wait 15 Minutes", "Disconnect negative battery cable and wait 15 minutes for airbag backup power storage capacitors to fully discharge.", warning = "ALWAYS disconnect negative battery terminal and wait 15 minutes before servicing any SRS airbag wire or component to avoid accidental deployment!"),
                    RepairStep(2, "Remove Steering Column Side Bolt Covers", "Pop plastic side caps off steering wheel body and unscrew two 8mm airbag module retaining screws."),
                    RepairStep(3, "Disconnect Yellow SRS Wire Connectors", "Carefully lift driver airbag module off wheel center and depress locking tabs on yellow SRS electrical wire harness connectors."),
                    RepairStep(4, "Unbolt Steering Wheel & Replace Clock Spring", "Remove center wheel bolt, pull steering wheel off splined shaft, and unclip plastic clock spring assembly from column housing.")
                ),
                commonSymptoms = listOf("Airbag warning light flashing on instrument cluster (Airbag Lamp Fault Code 19 or Code 32 - Driver Airbag Circuit Resistance High)", "Horn stops working or functions intermittently", "Cruise control steering wheel buttons non-responsive"),
                replacementIntervalMiles = 150000
            )
        },

        // 31. RESTRAINT CONTROL MODULE (RCM) & FRONT CRASH IMPACT SENSORS
        run {
            val (v, f) = createBoxMesh(0.8f, 0.4f, 0.8f, Point3D(0.0f, 0.1f, 0.2f), "#DC2626")
            Component3DModel(
                id = "airbag_rcm_sensors_3d",
                name = "Restraint Control Module (RCM) & Front Crash Impact Sensors",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-14B056-AA",
                description = "Microprocessor-controlled SRS Restraint Control Module (RCM) with internal solid-state accelerometers and rollover sensors, plus dual front radiator core support crash impact sensors.",
                locationDescription = "RCM mounted under center console on floor tunnel; crash sensors mounted on front radiator core support crossmember.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.1f, 0.2f),
                explodeVector = Point3D(0.0f, 0.8f, 0.8f),
                torqueSpecs = listOf(
                    TorqueSpec("RCM Ground Fastener Nuts to Floor Pan", "106 in-lbs", "12 Nm", "Must maintain clean metal-to-metal ground seal"),
                    TorqueSpec("Front Crash Sensor Core Support Bolts", "89 in-lbs", "10 Nm", "Torque to prevent sensor housing vibration")
                ),
                requiredTools = listOf("10mm Socket & Ratchet", "FORScan / High-End OBD2 Scanner with SRS diagnostics", "Dielectric Grease"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery & Disarm Airbag System", "Remove negative battery terminal and wait 15 minutes before unbolting module."),
                    RepairStep(2, "Remove Center Console Assembly", "Unbolt interior floor console cover to expose aluminum RCM module grounded to floor pan."),
                    RepairStep(3, "Unplug RCM Harness Connectors", "Slide red secondary locking wedges back on dual 24-pin yellow airbag connectors."),
                    RepairStep(4, "Replace Front Radiator Core Support Sensors", "Unbolt rusted front impact sensors from behind grille mesh and install new gold-plated contacts.")
                ),
                commonSymptoms = listOf("Airbag indicator light illuminated continuously on dash without turning off", "OBD code B1318 (Low Battery Voltage to RCM) or B1231 (Crash Data Stored)", "Front crash sensor corrosion causing SRS flash code 42"),
                replacementIntervalMiles = 150000
            )
        },

        // 32. SEATBELT PRETENSIONERS & OCCUPANT SENSORS
        run {
            val (v, f) = createCylinderMesh(0.3f, 1.2f, 6, Point3D(-0.6f, 0.2f, -0.2f), "#EF4444")
            Component3DModel(
                id = "airbag_seatbelt_pretensioners_3d",
                name = "Pyrotechnic Seatbelt Buckle Pretensioners & Buckle Switch Harness",
                system = VehicleSystem.ELECTRICAL,
                oemPartNumber = "1L2Z-7861203-AB",
                description = "Driver and front passenger pyrotechnic cable-pull seatbelt buckle pretensioner anchors, equipped with integrated buckle latch switches that signal the RCM module whether occupants are buckled in.",
                locationDescription = "Mounted to inner side of front driver and passenger bucket seat frames.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.6f, 0.2f, -0.2f),
                explodeVector = Point3D(-1.2f, 0.6f, -0.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Seatbelt Anchor Bolt to Seat Frame", "30", "40", "T50 Torx head bolt - heavy threadlocker"),
                    TorqueSpec("Seat Frame Track Floor Mounting Bolts", "39", "53", "15mm socket")
                ),
                requiredTools = listOf("T50 Torx Socket & Breaker Bar", "15mm Socket", "Wire Harness Contact Cleaner"),
                repairSteps = listOf(
                    RepairStep(1, "Disarm Airbag System", "Disconnect battery negative terminal and wait 15 minutes."),
                    RepairStep(2, "Unbolt Seat Track & Tilt Seat Back", "Remove four 15mm floor bolts securing front bucket seat to access inner buckle mounting bracket."),
                    RepairStep(3, "Unplug Under-Seat Airbag Harness", "Disconnect yellow seatbelt pretensioner wire pigtail located under seat cushion."),
                    RepairStep(4, "Unbolt Buckle Anchor", "Use T50 Torx socket to remove pretensioner assembly bolt from lower seat frame rail.")
                ),
                commonSymptoms = listOf("Airbag light flashing Code 46 or 47 (Driver/Passenger Seatbelt Pretensioner Circuit Open)", "Buckle chime continues chiming even when seatbelt is latched", "Pretensioner cable crumpled or contracted after minor fender bender"),
                replacementIntervalMiles = 150000
            )
        },

        // 33. A/C COMPRESSOR, MAGNETIC CLUTCH & PRESSURE SWITCHES
        run {
            val (v, f) = createCylinderMesh(0.65f, 1.4f, 8, Point3D(0.6f, 0.4f, 1.2f), "#06B6D4")
            Component3DModel(
                id = "ac_compressor_pressure_controls",
                name = "A/C Compressor, Electromagnetic Clutch & High/Low Pressure Cut-off Switches",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-19703-AA",
                description = "FS10 10-piston aluminum A/C compressor with electromagnetic clutch coil, 6-groove serpentine-belt pulley, air-gap shims, pressure-protection controls, and a sealed R-134a refrigerant circuit. Refrigerant amount and oil balance must be confirmed from the under-hood label and Ford service procedure for the exact vehicle.",
                locationDescription = "Lower passenger side front engine bay driven directly by serpentine belt.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.6f, 0.4f, 1.2f),
                explodeVector = Point3D(1.4f, 0.8f, 1.6f),
                torqueSpecs = listOf(
                    TorqueSpec("A/C Compressor Mounting Bolts (3 Bolts)", "18", "25", "Tighten in 2 stages"),
                    TorqueSpec("A/C Manifold Hose Block Bolt", "15", "20", "Must replace green HNBR O-rings"),
                    TorqueSpec("Clutch Hub Front Center Nut", "Verify", "Verify", "Measure clutch air gap and use the Ford service specification for this exact compressor/clutch.")
                ),
                requiredTools = listOf("A/C Manifold Gauge Set & Vacuum Pump", "R134a Refrigerant Recovery Machine", "8mm & 10mm Sockets", "HNBR Green A/C O-Ring Seal Kit", "A/C Clutch Hub Holding Tool"),
                repairSteps = listOf(
                    RepairStep(1, "Evacuate R134a Refrigerant", "Connect EPA-certified A/C recovery machine to high and low service ports to evacuate R134a gas safely.", warning = "Never vent R134a refrigerant into atmosphere! Pressurized refrigerant causes severe frostbite on skin contact."),
                    RepairStep(2, "Remove Serpentine Belt & Skid Plate", "Release tensioner pulley with 3/8-inch ratchet and slip serpentine belt off compressor pulley."),
                    RepairStep(3, "Disconnect A/C Hose Block & Switches", "Remove single 10mm bolt securing manifold hose assembly block to compressor back and unplug 2-pin clutch wire connector."),
                    RepairStep(4, "Unbolt & Replace Compressor", "Remove the compressor fasteners, install approved new O-rings lubricated with the specified PAG oil, and have the system recovered, evacuated, leak-tested, and charged to the exact under-hood-label specification by qualified A/C service equipment.", warning = "Do not guess refrigerant charge or compressor-oil quantity. An incorrect charge or oil balance can damage the replacement compressor.")
                ),
                commonSymptoms = listOf("A/C blows hot air or fails to cool cabin", "Loud squealing or metallic grinding noise from compressor pulley bearing when A/C is turned ON", "A/C clutch plate fails to engage or rapid-cycles ON and OFF every 3 seconds due to low refrigerant level"),
                replacementIntervalMiles = 90000
            )
        },

        // 34. HEATER CORE, VACUUM VALVE & DUAL HEATER HOSES
        run {
            val (v, f) = createBoxMesh(1.2f, 0.9f, 0.4f, Point3D(0.4f, 0.5f, -0.2f), "#EF4444")
            Component3DModel(
                id = "heater_core_hvac_3d",
                name = "Aluminum Heater Core, Vacuum Control Valve & Heater Hoses",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-18476-AA",
                description = "Heavy-duty aluminum heat-exchanger core housed inside dash HVAC housing, vacuum-actuated 4-port heater coolant control shut-off valve, and dual 5/8-inch reinforced rubber heater supply/return hoses.",
                locationDescription = "Mounted inside lower passenger dash HVAC plenum box; heater control valve located on engine firewall.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 210,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.4f, 0.5f, -0.2f),
                explodeVector = Point3D(1.0f, 1.2f, -0.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Heater Hose Clamp Tensioners", "22 in-lbs", "2.5 Nm", "Inspect rubber hoses for swelling"),
                    TorqueSpec("HVAC Case Plenum Mounting Stud Nuts", "89 in-lbs", "10 Nm", "Engine firewall studs")
                ),
                requiredTools = listOf("Hose Disconnect Pliers", "7mm, 8mm & 10mm Sockets", "Coolant Drain Pan", "Heater Core Flush Kit", "50/50 Premium Gold Coolant"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Engine Coolant", "Open radiator petcock valve to drain 1.5 gallons of engine coolant below heater hose level."),
                    RepairStep(2, "Disconnect Firewall Heater Hoses", "Squeeze constant-tension clamps on firewall 5/8-inch heater hoses and disconnect from core nipples.", warning = "Engine coolant is toxic to pets. Catch all fluid in clean sealed containers."),
                    RepairStep(3, "Unbolt Lower Passenger Dashboard", "Remove passenger dash trim, glovebox, and right HVAC ducting to expose heater core access door."),
                    RepairStep(4, "Slide Out Old Core & Flush System", "Unscrew 4 cover door screws, pull leaking heater core out, install fresh aluminum core with foam insulation tape, and refill coolant system.")
                ),
                commonSymptoms = listOf("Sweet antifreeze coolant smell inside cabin with greasy fog film coating inside windshield", "Passenger side front carpet damp or soaked with green/gold coolant", "No heat from dashboard vents even with engine at full operating temperature (clogged core passages)"),
                replacementIntervalMiles = 100000
            )
        },

        // 35. HVAC BLOWER MOTOR & MULTI-SPEED RESISTOR PACK
        run {
            val (v, f) = createCylinderMesh(0.55f, 0.8f, 8, Point3D(0.7f, 0.6f, 0.3f), "#06B6D4")
            Component3DModel(
                id = "hvac_blower_motor_3d",
                name = "HVAC Blower Motor Fan Assembly & 4-Speed Resistor Block",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "2L2Z-19805-AA",
                description = "High-output 12V permanent magnet blower motor with balanced plastic squirrel cage fan wheel, paired with a ceramic-coated 4-position blower motor resistor block and thermal cutoff fuse.",
                locationDescription = "Mounted on right passenger engine firewall under hood; resistor block mounted directly in HVAC air duct stream.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 40,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.7f, 0.6f, 0.3f),
                explodeVector = Point3D(1.4f, 1.0f, 0.3f),
                torqueSpecs = listOf(
                    TorqueSpec("Blower Motor Mounting Screws (4 Screws)", "25 in-lbs", "2.8 Nm", "Hand tighten carefully into plastic housing"),
                    TorqueSpec("Blower Resistor Retaining Screws", "18 in-lbs", "2 Nm", "")
                ),
                requiredTools = listOf("8mm Socket & Nut Driver", "T20 Torx Driver", "Electrical Contact Cleaner", "Wire Strippers / Heat Shrink Terminal Kit (if connector melted)"),
                repairSteps = listOf(
                    RepairStep(1, "Unplug Blower Motor Wire Harness", "Press release tab on 2-pin electrical connector at blower motor on engine firewall."),
                    RepairStep(2, "Remove Blower Motor Screws", "Unscrew four 8mm screws securing blower flange to plastic HVAC casing."),
                    RepairStep(3, "Extract Blower Wheel & Motor", "Tilt motor assembly forward and pull out of engine bay firewall opening."),
                    RepairStep(4, "Replace Blower Motor Resistor Pack", "Unbolt two 8mm screws on passenger firewall HVAC case to replace burnt resistor block and inspect wire harness connector for melted pin terminals.")
                ),
                commonSymptoms = listOf("Blower fan works ONLY on HIGH speed (#4) but stays dead on low speeds #1, #2, and #3 (burnt resistor pack)", "Loud chirping, squeaking or vibrating thumping noise when heater fan is running", "Blower motor fails to blow any air through dash vents on any speed setting"),
                replacementIntervalMiles = 80000
            )
        },

        // 36. HVAC ELECTRIC BLEND DOOR ACTUATOR & TEMPERATURE FLAP
        run {
            val (v, f) = createBoxMesh(0.5f, 0.4f, 0.5f, Point3D(0.1f, 0.5f, 0.1f), "#EAB308")
            Component3DModel(
                id = "hvac_blend_door_actuator_3d",
                name = "HVAC Electric Temperature Blend Door Actuator Motor",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-19E616-BA",
                description = "Micro-stepper electric motor actuator with internal nylon gear reduction drive and feedback potentiometer, controlling the HVAC plenum chamber blend door flap to blend hot heater core air with cold A/C evaporator air.",
                locationDescription = "Mounted on top of HVAC plenum box behind center dashboard radio / climate control panel.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 75,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.1f, 0.5f, 0.1f),
                explodeVector = Point3D(0.1f, 1.2f, 0.1f),
                torqueSpecs = listOf(
                    TorqueSpec("Blend Door Actuator Screws (8mm Head)", "18 in-lbs", "2 Nm", "Do not overtighten screws into plastic plenum")
                ),
                requiredTools = listOf("8mm Socket & Mini Ratchet / Flex Extension", "Plastic Pry Tools", "Heavy-Duty Aluminum Replacement Blend Door Flap (if axle shaft broken)"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Center Dash Bezel & Glovebox", "Unscrew 7mm radio bezel screws and lower glovebox door to gain access behind center dash."),
                    RepairStep(2, "Locate Actuator Motor on Plenum Top", "Reach through glovebox opening to locate black rectangular actuator motor seated on top of HVAC plenum chamber."),
                    RepairStep(3, "Unscrew Mounting Screws & Unplug Wire Harness", "Remove three 8mm screws using mini ratchet or flex driver and unplug 5-pin wire connector."),
                    RepairStep(4, "Inspect Blend Door Axle D-Shaft & Replace Actuator", "Check plastic blend door axle socket for cracks. If cracked, install reinforced metal blend door axle before securing new motor actuator.")
                ),
                commonSymptoms = listOf("Repetitive loud rhythmic clicking or popping noise behind center dashboard when turning key ON or adjusting temperature", "A/C blows cold on driver side but hot on passenger side, or stuck blowing ice cold air with no heat available", "Temperature selector dial has no effect on air temperature"),
                replacementIntervalMiles = 75000
            )
        },

        // 37. A/C EVAPORATOR CORE, ACCUMULATOR DRIER & ORIFICE TUBE
        run {
            val (v, f) = createCylinderMesh(0.5f, 1.6f, 8, Point3D(0.6f, 0.5f, -0.1f), "#06B6D4")
            Component3DModel(
                id = "ac_evaporator_accumulator_3d",
                name = "A/C Evaporator Core, Accumulator / Drier Bottle & Fixed Orifice Tube",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-19860-AA",
                description = "High-efficiency aluminum evaporator core, accumulator/drier bottle with internal desiccant and pressure-switch port, and fixed-orifice metering device. The exact replacement configuration must match the VIN-specific parts catalogue and service procedure.",
                locationDescription = "Evaporator housed inside passenger firewall case; accumulator bottle mounted directly on passenger firewall.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 150,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.6f, 0.5f, -0.1f),
                explodeVector = Point3D(1.4f, 1.2f, -0.1f),
                torqueSpecs = listOf(
                    TorqueSpec("A/C Accumulator Nut Coupling Spring Lock", "15", "20", "Use spring lock tool #4 & #5"),
                    TorqueSpec("Low Pressure Cycling Switch to Accumulator Port", "89 in-lbs", "10 Nm", "Includes Schrader valve seal")
                ),
                requiredTools = listOf("Correct spring-lock quick-disconnect tools", "Approved orifice-tube extraction tool", "A/C recovery, evacuation, and leak-test equipment", "Ford-specified PAG oil and new compatible O-rings"),
                repairSteps = listOf(
                    RepairStep(1, "Recover R134a Refrigerant", "Evacuate refrigerant system using A/C recovery machine."),
                    RepairStep(2, "Disconnect Spring-Lock Line Fittings", "Use spring lock release tool to separate aluminum suction and liquid lines from accumulator bottle and evaporator inlet.", warning = "Always replace desiccant accumulator bottle whenever A/C system is opened to atmosphere to prevent moisture contamination."),
                    RepairStep(3, "Extract Fixed Orifice Tube", "Use needle-nose pliers or specialized orifice tube puller tool to extract plastic orifice tube from liquid line pipe.", tip = "Inspect orifice tube mesh screen for metal debris ('black death') from compressor failure."),
                    RepairStep(4, "Replace Evaporator & Accumulator", "Install the correct accumulator/drier, fixed orifice, and compatible O-rings. Before charging, have the system evacuated, leak-tested, and charged to the exact under-hood-label specification.", warning = "Opening the refrigerant circuit requires correct recovery equipment; do not release refrigerant to atmosphere.")
                ),
                commonSymptoms = listOf("Musty vinegar smell or damp mold odor coming from dashboard air vents when A/C is turned ON", "A/C air starts cold then gradually fades to warm as evaporator core freezes into a solid block of ice", "Accumulator bottle covered in thick white frost or ice build-up"),
                replacementIntervalMiles = 100000
            )
        },

        // 38. A/C CONDENSER CORE & HIGH-PRESSURE SUCTION / LIQUID HOSES
        run {
            val (v, f) = createBoxMesh(2.6f, 1.8f, 0.3f, Point3D(0.0f, 0.5f, 2.5f), "#06B6D4")
            Component3DModel(
                id = "ac_condenser_lines_3d",
                name = "A/C Condenser Parallel-Flow Radiator Core & High-Pressure Hose Assembly",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "1L2Z-19712-AA",
                description = "Heavy-duty aluminum parallel-flow A/C condenser heat exchanger with integrated sub-cooler, high-pressure aluminum discharge lines, flexible barrier rubber hoses, and dual high/low Schrader service valve ports.",
                locationDescription = "Mounted at very front of vehicle in front of main radiator behind front grille mesh.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 110,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.5f, 2.5f),
                explodeVector = Point3D(0.0f, 0.5f, 3.2f),
                torqueSpecs = listOf(
                    TorqueSpec("A/C Condenser Bracket Retaining Bolts", "89 in-lbs", "10 Nm", ""),
                    TorqueSpec("High Pressure Hose Fitting Block Bolt", "15", "20", "Replace green HNBR O-ring seals")
                ),
                requiredTools = listOf("A/C Spring-Lock Disconnect Tool Set", "10mm Socket & Ratchet", "Fin Straightener Comb", "Electronic A/C Leak Detector / UV Dye Flashlight"),
                repairSteps = listOf(
                    RepairStep(1, "Evacuate Refrigerant", "Recover all R134a gas using certified A/C machine."),
                    RepairStep(2, "Remove Front Grille & Upper Header Panel", "Unclip front grille mesh and header panel clips to expose aluminum A/C condenser core."),
                    RepairStep(3, "Disconnect High-Pressure Lines", "Unbolt 10mm line block fittings from condenser manifolds and inspect for UV dye leaks."),
                    RepairStep(4, "Replace Condenser Core", "Install the correct condenser and compatible new O-rings, then have the system evacuated, leak-tested, and charged to the exact under-hood-label specification with approved A/C equipment.", warning = "Do not charge by a guessed pressure target or a generic can label; use the vehicle-specific label and Ford procedure.")
                ),
                commonSymptoms = listOf("A/C cools moderately while driving at 50 MPH but blows hot air when idling at stoplights (poor heat dissipation)", "Oily green UV dye residue visible on condenser aluminum cooling fins from rock chip puncture", "A/C high side pressure exceeds 350 PSI due to restricted condenser fins or bent tubes"),
                replacementIntervalMiles = 100000
            )
        },

        // 39. A/C SERVICE PORTS, PRESSURE PROTECTION & CLUTCH COMMAND PATH
        run {
            val (v, f) = createBoxMesh(1.15f, 0.55f, 0.45f, Point3D(0.95f, 0.72f, 0.72f), "#0EA5E9")
            Component3DModel(
                id = "ac_service_ports_controls_3d",
                name = "A/C Service Ports, Pressure-Protection Controls & Clutch Command Path",
                system = VehicleSystem.AIR_CONDITIONING,
                oemPartNumber = "AC-SVC-2004-REFERENCE",
                description = "Diagnostic-reference assembly representing the high- and low-side service ports, pressure-protection controls, A/C relay/fuse command path, compressor clutch connector, and associated harness routing. It is intended to guide safe observation and electrical inspection without bypassing safety controls or opening the sealed refrigerant circuit.",
                locationDescription = "Service ports and pressure-control fittings are located along the refrigerant lines; relay/fuse locations must be confirmed from the exact owner-manual diagram; compressor clutch connector is at the lower passenger-side compressor.",
                difficulty = "Beginner diagnostic / Professional refrigerant service",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.95f, 0.72f, 0.72f),
                explodeVector = Point3D(1.6f, 1.1f, 1.15f),
                torqueSpecs = listOf(
                    TorqueSpec("Service-port cap and valve handling", "N/A", "N/A", "Do not remove Schrader valves or loosen fittings for a basic diagnostic inspection."),
                    TorqueSpec("Pressure-control and clutch-circuit verification", "N/A", "N/A", "Use the Ford wiring information and approved test methods; do not jumper protective switches.")
                ),
                requiredTools = listOf("Owner-manual fuse/relay diagram", "Flashlight", "Safety glasses", "Digital multimeter only if experienced", "Professional MVAC recovery/diagnostic equipment for any refrigerant-circuit service"),
                repairSteps = listOf(
                    RepairStep(1, "Heat and engine-safety preflight", "Park outdoors or in a fully ventilated area, work in shade where possible, set the parking brake, and keep hands and clothing clear of belts and fans."),
                    RepairStep(2, "Confirm cabin-air and A/C request", "Verify the blower moves air and that MAX A/C / low-temperature control settings are actually requesting cooling before testing the compressor command path."),
                    RepairStep(3, "Inspect without opening the system", "With the engine OFF and cool, inspect service-port caps, visible line routing, the compressor-clutch connector, and nearby harnesses for damage, oil-stained dirt, or corrosion."),
                    RepairStep(4, "Check protected electrical path", "Use the exact owner-manual diagram to inspect the specified fuse and relay. Do not bypass the relay, pressure switches, or clutch connector to force compressor operation.", warning = "Pressure switches protect the system. Bypassing them can damage components or create a hazardous condition."),
                    RepairStep(5, "Escalate sealed-system diagnosis", "If airflow and basic electrical checks are normal but cooling is still poor, have a qualified A/C technician recover, measure, leak-test, evacuate, and charge the system according to the under-hood label and Ford procedure.")
                ),
                commonSymptoms = listOf("Compressor clutch does not engage", "Compressor clutch rapidly cycles", "A/C works while driving but is warm at idle", "Strong blower airflow but warm vent air", "Visible oily residue at a fitting or service-port area"),
                replacementIntervalMiles = null
            )
        },

        // 40. DASHBOARD INSTRUMENT CLUSTER & GAUGE PACK
        run {
            val (v, f) = createBoxMesh(1.8f, 0.8f, 0.4f, Point3D(-0.4f, 0.9f, 0.5f), "#F59E0B")
            Component3DModel(
                id = "dash_instrument_cluster_3d",
                name = "Dashboard Instrument Cluster & White-Face Gauge Pack",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2Z-10849-BA",
                description = "Sport Trac factory white-faced gauge instrument cluster featuring analog speedometer, tachometer, engine temp, fuel gauge, oil pressure, battery voltage gauge, illuminated gear position display, and dual 16-pin micro-lock electrical connectors.",
                locationDescription = "Mounted inside dashboard instrument bezel directly behind steering wheel.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.4f, 0.9f, 0.5f),
                explodeVector = Point3D(-0.4f, 1.3f, 1.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Instrument Cluster Retaining Screws (4 Screws)", "22 in-lbs", "2.5 Nm", "Hand tighten into plastic dash framing"),
                    TorqueSpec("Dash Bezel Screws (7mm Head)", "18 in-lbs", "2 Nm", "Two screws located above gauge lens hood")
                ),
                requiredTools = listOf("7mm Socket & Nut Driver", "Plastic Trim Removal Tool", "T15 Torx Screwdriver", "Replacement #194 / #37 Mini Wedge Bulbs or LED Conversion Kit"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Dash Bezel Trim", "Unscrew two 7mm screws from upper instrument hood and gently pull plastic bezel away from dash clips."),
                    RepairStep(2, "Unscrew Instrument Cluster Frame", "Remove four 7mm gold screws holding instrument cluster housing to dashboard dashboard structure."),
                    RepairStep(3, "Disconnect Rear Electrical Plugs & Speedo Cable", "Tilt cluster top forward, press release tabs on black and grey 16-pin wiring harness plugs, and disconnect PRNDL gear selector string clip."),
                    RepairStep(4, "Replace Backlight Bulbs / Install Cluster", "Twist ¼-turn bulb sockets on cluster rear circuit board to replace dead gauge backlights, reconnect wiring, and test all gauge needles.")
                ),
                commonSymptoms = listOf("Speedometer needle bounces erratically, stays stuck at zero, or digital odometer display goes completely blank", "Backlight bulbs burned out causing dark spots on tachometer or fuel gauge at night", "ABS, Battery, or 4x4 High indicator lights stay continuously illuminated on gauge face"),
                replacementIntervalMiles = 120000
            )
        },

        // 40. DASH CENTER STACK CLIMATE CONTROLS, RADIO & 4WD AUTO SWITCH
        run {
            val (v, f) = createBoxMesh(1.2f, 1.4f, 0.5f, Point3D(0.0f, 0.7f, 0.5f), "#F59E0B")
            Component3DModel(
                id = "dash_hvac_radio_center_stack_3d",
                name = "Dash Center Stack Radio, Electronic HVAC Panel & 4WD Switch",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2Z-19B888-AB",
                description = "Center dash control console housing factory Mach 500 double-DIN radio unit, rotary HVAC vacuum selector switch module, temperature control potentiometer, rear power window toggle switch, and 3-position rotary 4WD Auto / 4x4 High / 4x4 Low transfer case selector switch.",
                locationDescription = "Located in center dashboard stack directly above center floor console.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 35,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.7f, 0.5f),
                explodeVector = Point3D(0.0f, 1.0f, 1.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Center Radio Bezel Screws (7mm Head)", "18 in-lbs", "2 Nm", "Two screws above HVAC panel"),
                    TorqueSpec("HVAC Control Module Screws", "15 in-lbs", "1.7 Nm", "Four mini screws into plastic housing")
                ),
                requiredTools = listOf("7mm Socket & Driver", "Ford DIN Radio Removal Keys (if removing head unit)", "Plastic Pry Tools", "Electrical Contact Cleaner"),
                repairSteps = listOf(
                    RepairStep(1, "Unscrew Bezel Retaining Bolts", "Unscrew two 7mm screws located in lip above climate control knobs."),
                    RepairStep(2, "Unclip Bezel Assembly", "Gently pry around perimeter with plastic trim tool to release spring steel retaining clips."),
                    RepairStep(3, "Disconnect Switch Wire Connectors & Vacuum Harness", "Unplug 4WD switch connector, rear power window switch plug, cigarette lighter socket, and 5-tube HVAC vacuum line harness block."),
                    RepairStep(4, "Replace Control Switch / Module", "Unbolt four 7mm screws on rear of bezel to replace damaged 4WD rotary switch or climate control module.")
                ),
                commonSymptoms = listOf("Turning 4WD selector knob does not engage 4x4 or cause transfer case shift motor to click", "Air blows ONLY out of defrost vents on windshield regardless of HVAC knob position (vacuum leak)", "Rear power window toggle switch fails to lower back glass window"),
                replacementIntervalMiles = 100000
            )
        },

        // 41. STEERING COLUMN, CRUISE CONTROL & DRIVER SRS AIRBAG MODULE
        run {
            val (v, f) = createCylinderMesh(0.6f, 1.2f, 8, Point3D(-0.4f, 0.7f, 0.8f), "#F59E0B")
            Component3DModel(
                id = "steering_wheel_airbag_column_3d",
                name = "Steering Column, Cruise Control Switches & Driver SRS Airbag Module",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2Z-3600-BA",
                description = "Collapsible tilt steering column assembly complete with leather-wrapped steering wheel, steering column clockspring wiring coil, multifunction turn signal wiper lever, cruise control ON/OFF/SET thumb switches, ignition lock cylinder, and driver SRS airbag module.",
                locationDescription = "Mounted through driver side dash bulkhead to steering gear rack shaft.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.4f, 0.7f, 0.8f),
                explodeVector = Point3D(-0.4f, 1.2f, 1.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Steering Wheel Center Hub Retaining Bolt", "33", "45", "Apply blue threadlocker to M12 bolt"),
                    TorqueSpec("Driver Airbag Module Retaining Screws (8mm)", "89 in-lbs", "10 Nm", "Two screws behind steering wheel spokes"),
                    TorqueSpec("Steering Column Pinch Bolt to Shaft", "26", "35", "")
                ),
                requiredTools = listOf("8mm Socket", "15mm Socket & Torque Wrench", "2-Jaw Steering Wheel Puller", "T25 Torx Driver", "Battery Disconnect Wrench"),
                repairSteps = listOf(
                    RepairStep(1, "Disconnect Battery & Wait 15 Minutes", "Disconnect negative battery cable and wait 15 minutes to fully discharge SRS airbag backup capacitor.", warning = "Failure to disconnect battery before working on SRS airbag can trigger accidental deployment."),
                    RepairStep(2, "Remove Airbag Module", "Remove two 8mm plastic access covers on back of steering wheel, unscrew 8mm bolts, and unplug yellow SRS connector."),
                    RepairStep(3, "Unbolt Center Steering Hub Bolt", "Remove center 15mm bolt and use steering wheel puller tool to lift wheel off column splines."),
                    RepairStep(4, "Replace Clockspring / Multifunction Switch", "Unclip steering column plastic shrouds and unscrew clockspring assembly or multifunction turn signal switch lever.")
                ),
                commonSymptoms = listOf("Airbag SRS warning light stays continuously illuminated or flashes 19/32 flash codes on dashboard", "Horn fails to sound and cruise control buttons stop functioning (broken clockspring ribbon cable)", "Turn signal switch lever fails to click into detent or windshield wipers run continuously"),
                replacementIntervalMiles = 150000
            )
        },

        // 42. OVERHEAD ROOF CONSOLE WITH DIGITAL COMPASS & TEMP DISPLAY
        run {
            val (v, f) = createBoxMesh(0.8f, 1.6f, 0.3f, Point3D(0.0f, 1.6f, 0.6f), "#F59E0B")
            Component3DModel(
                id = "overhead_console_compass_3d",
                name = "Overhead Roof Console with Digital Compass, Temp Display & Sunroof Switch",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2Z-51519A58-AA",
                description = "Factory overhead roof console containing green VFD digital display for exterior temperature and heading compass, twin dome map lights, garage door opener storage compartment, sunglass holder bay, and power sunroof toggle control switch.",
                locationDescription = "Mounted to center roof headliner directly above rearview mirror.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 30,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.6f, 0.6f),
                explodeVector = Point3D(0.0f, 2.1f, 0.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Overhead Console Front Screw (Ph2)", "15 in-lbs", "1.7 Nm", "Single Phillips screw inside sunglass compartment")
                ),
                requiredTools = listOf("Phillips #2 Screwdriver", "Soldering Iron & 470-Ohm Resistors (for fixing dim display)", "Plastic Pry Bar"),
                repairSteps = listOf(
                    RepairStep(1, "Open Sunglass Compartment & Remove Screw", "Press latch to drop sunglass holder door and remove single Phillips screw inside upper roof bracket."),
                    RepairStep(2, "Pull Console Assembly Down", "Gently pull rear of console downward to disengage spring clips from roof headliner frame."),
                    RepairStep(3, "Unplug Electrical Connector", "Disconnect 8-pin wiring harness plug powering map lights, compass circuit board, and sunroof switch."),
                    RepairStep(4, "Repair / Replace Compass Circuit Board", "Unscrew circuit board from console housing. Resolder surface-mount SMD 470-ohm chip resistors (R51 & R47) if display was completely dark.")
                ),
                commonSymptoms = listOf("Digital compass and ambient temperature readout screen went completely dark or flickers randomly", "Exterior temperature reads '-40°F' or '122°F' constantly (failed front bumper temp sensor)", "Overhead map reading lights fail to turn on when doors open"),
                replacementIntervalMiles = 100000
            )
        },

        // 43. FACTORY POWER GLASS SUNROOF ASSEMBLY & TRACK RAILS
        run {
            val (v, f) = createBoxMesh(2.2f, 0.2f, 2.0f, Point3D(0.0f, 1.8f, -0.2f), "#14B8A6")
            Component3DModel(
                id = "power_sunroof_assembly_3d",
                name = "Factory Power Glass Sunroof Assembly, Dual-Track Rails & Water Drain Hoses",
                system = VehicleSystem.SUNROOF_ROOF,
                oemPartNumber = "1L2Z-78502C52-AA",
                description = "Complete power glass sunroof assembly featuring tinted tempered glass panel with perimeter rubber weatherseal gasket, dual extruded aluminum guide track rails, tilt/slide mechanism lifter arms, sliding interior fabric sunshade, and 4-corner rubber water drain hoses routed down A/C roof pillars.",
                locationDescription = "Integrated into roof panel structure above front driver and passenger seats.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.8f, -0.2f),
                explodeVector = Point3D(0.0f, 2.5f, -0.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Glass Panel Side Screws (Torx T25 - 4 Screws)", "35 in-lbs", "4 Nm", "Adjust glass panel flush with outer roof line"),
                    TorqueSpec("Sunroof Frame Retaining Bolts (10mm)", "89 in-lbs", "10 Nm", "Eight bolts to roof internal crossmembers")
                ),
                requiredTools = listOf("T25 Torx Driver", "10mm Socket & Flex Extension", "Silicone Spray Lubricant", "Weed Trimmer Line / Compressed Air (for clearing drain tubes)"),
                repairSteps = listOf(
                    RepairStep(1, "Retract Accordion Side Rubber Seals", "Slide sunroof glass to tilt position and unclip rubber side bellows accordion covers to expose glass adjustment Torx screws."),
                    RepairStep(2, "Remove Sunroof Glass Panel", "Remove four T25 Torx screws (2 per side) and lift tempered glass panel up and off roof frame."),
                    RepairStep(3, "Clean Track Channels & Drain Holes", "Inspect aluminum track grooves for broken plastic guide feet or dried grease. Insert trimmer line into 4 corner drain holes to clear leaves and dirt."),
                    RepairStep(4, "Reinstall & Align Glass Panel", "Set glass panel onto brackets, tighten T25 screws finger tight, align glass flush with roof surface (+1mm higher at rear edge), and torque screws to 35 in-lbs.")
                ),
                commonSymptoms = listOf("Water leaking from overhead console, A-pillar trim, or wet front carpet after heavy rain (clogged sunroof drain hoses)", "Sunroof glass gets stuck halfway when closing or makes loud grinding crunching noise", "Wind noise or water drips entering cab around glass perimeter weatherseal"),
                replacementIntervalMiles = 120000
            )
        },

        // 44. SUNROOF ELECTRIC DRIVE MOTOR & CABLE MECHANISM
        run {
            val (v, f) = createCylinderMesh(0.4f, 0.8f, 8, Point3D(0.0f, 1.7f, 0.4f), "#14B8A6")
            Component3DModel(
                id = "sunroof_motor_drive_gear_3d",
                name = "Sunroof Electric Drive Motor, Helical Drive Cables & Limit Module",
                system = VehicleSystem.SUNROOF_ROOF,
                oemPartNumber = "1L2Z-15790-AA",
                description = "High-torque 12V reversible electric gear motor with internal brass pinion drive gear, meshing with dual flexible steel helical cables to slide and tilt the sunroof glass, equipped with electronic overload limit sensing and manual hex key emergency closure socket.",
                locationDescription = "Mounted behind overhead roof console attached to forward sunroof crossmember.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 60,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.7f, 0.4f),
                explodeVector = Point3D(0.0f, 2.2f, 0.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Sunroof Motor Mounting Screws (T20 - 3 Screws)", "40 in-lbs", "4.5 Nm", "Do not cross-thread into metal bracket")
                ),
                requiredTools = listOf("T20 Torx Driver", "4mm Allen Wrench (for emergency manual cranking)", "White Lithium Grease", "10mm Socket"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Overhead Roof Console", "Unscrew and remove overhead console to gain access to sunroof drive motor assembly."),
                    RepairStep(2, "Emergency Manual Closure (if stuck open)", "Insert 4mm Allen wrench into center socket of motor gear shaft and manually turn clockwise to crank glass fully closed."),
                    RepairStep(3, "Unplug Motor Connector & Unbolt Screws", "Disconnect 6-pin electrical plug and unscrew three T20 Torx screws securing motor to roof frame."),
                    RepairStep(4, "Replace Motor & Calibrate Auto-Stop", "Install new drive motor, hold sunroof control switch forward for 10 seconds until motor clicks to reset closed position memory.")
                ),
                commonSymptoms = listOf("Pressing sunroof switch results in clicking sound from roof but glass panel does not move", "Sunroof drive motor spins freely but glass remains stationary (stripped motor pinion drive gear)", "Sunroof glass reverses direction automatically when trying to close"),
                replacementIntervalMiles = 100000
            )
        },

        // 45. SPORT TRAC POWER DROP-DOWN REAR SLIDING BACK GLASS WINDOW
        run {
            val (v, f) = createBoxMesh(2.4f, 1.0f, 0.2f, Point3D(0.0f, 1.1f, -2.2f), "#14B8A6")
            Component3DModel(
                id = "power_rear_window_assembly_3d",
                name = "Sport Trac Power Drop-Down Back Glass Window & Cable Regulator Motor",
                system = VehicleSystem.SUNROOF_ROOF,
                oemPartNumber = "1L2Z-3542006-AA",
                description = "Signature Sport Trac full-width power drop-down rear window assembly featuring heated tempered back glass with defrost grid, dual-cable scissor regulator track, high-torque electric motor, bottom drain trough, and rubber weatherstripping seal.",
                locationDescription = "Mounted inside rear cab back wall structure behind rear passenger seatback.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 90,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.1f, -2.2f),
                explodeVector = Point3D(0.0f, 1.1f, -2.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Rear Window Regulator Bracket Bolts (10mm)", "89 in-lbs", "10 Nm", ""),
                    TorqueSpec("Glass Mounting Channel Clamp Screws", "35 in-lbs", "4 Nm", "Ensure rubber clamp isolator pads in place")
                ),
                requiredTools = listOf("10mm Socket & Ratchet", "12mm Socket for Seat Bolts", "Plastic Trim Removal Tool", "Glass Cleaner & Silicone Spray"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Rear Seat Bench & Back Trim", "Unbolt rear 60/40 folding seat brackets and unscrew back cab wall carpet insulation panel."),
                    RepairStep(2, "Expose Power Rear Window Regulator", "Locate steel window regulator channels and 12V motor mounted inside double-walled rear cab metal frame."),
                    RepairStep(3, "Unbolt Window Clamps & Cable Pulley", "Lower glass slightly, unbolt two 10mm glass clamp brackets, and carefully support back glass with suction cups."),
                    RepairStep(4, "Replace Regulator Motor / Cable Unit", "Unbolt regulator motor from cab wall, replace snapped cable assembly, reattach glass clamps, and test rear window switch operation.")
                ),
                commonSymptoms = listOf("Rear power back window drops down into cab wall suddenly and won't roll back up (snapped regulator cable)", "Motor hums when pressing dash switch but back glass does not slide smoothly", "Water puddling under carpet behind rear seats after washing vehicle"),
                replacementIntervalMiles = 120000
            )
        },

        // 46. DUAL BEAM HALOGEN HEADLIGHT ASSEMBLIES, CORNER MARKERS & FOG LAMPS
        run {
            val (v, f) = createBoxMesh(2.6f, 0.6f, 0.6f, Point3D(0.0f, 0.6f, 2.7f), "#EC4899")
            Component3DModel(
                id = "headlight_foglight_assemblies_3d",
                name = "Dual-Beam Halogen Headlight Assemblies, Corner Markers & Fog Lamps",
                system = VehicleSystem.LIGHTING_BODY,
                oemPartNumber = "1L2Z-13008-AA",
                description = "Clear poly-carbonate front lighting pack consisting of dual composite headlight housings with 9007 HB5 dual-filament halogen bulbs, amber turn signal corner marker lenses with 3157NA bulbs, and lower bumper round fog lamp housings with H10 42W halogen bulbs.",
                locationDescription = "Mounted in front radiator header panel and front bumper cover.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 25,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.6f, 2.7f),
                explodeVector = Point3D(0.0f, 0.6f, 3.4f),
                torqueSpecs = listOf(
                    TorqueSpec("Headlight Retaining Retraction Pins", "Hand Latch", "N/A", "Pull two vertical steel slide retainer pins straight up"),
                    TorqueSpec("Fog Lamp Mounting Bracket Bolts (8mm)", "45 in-lbs", "5 Nm", "Aiming adjustment bolt on rear bracket")
                ),
                requiredTools = listOf("Needle Nose Pliers / Hands", "8mm Socket", "Clean Cotton Gloves (do not touch glass halogen bulbs with bare skin)"),
                repairSteps = listOf(
                    RepairStep(1, "Lift Hood & Locate Headlight Retainer Pins", "Open hood and locate two vertical steel L-shaped locking retainer pins behind top of headlight housing."),
                    RepairStep(2, "Slide Retainer Pins Straight Up", "Pull both steel locking pins upward until they disengage from headlight mounting tabs."),
                    RepairStep(3, "Pull Headlight Housing Forward", "Pull complete headlight lens assembly straight forward away from radiator header panel."),
                    RepairStep(4, "Twist Bulb Collar & Replace Bulb", "Rotate 9007 bulb retaining ring ¼-turn counter-clockwise, unplug electrical harness connector, and insert fresh 9007 halogen bulb.")
                ),
                commonSymptoms = listOf("Headlight lens heavily yellowed, oxidized, or hazy restricting night visibility", "High beam / low beam headlight bulb burned out on one side", "Fog lamp lens cracked from highway stone impact"),
                replacementIntervalMiles = 50000
            )
        },

        // 47. REAR TAIL LIGHT LENSES, BRAKE LIGHT SOCKETS & REVERSE HARNESS
        run {
            val (v, f) = createBoxMesh(2.6f, 0.8f, 0.4f, Point3D(0.0f, 0.8f, -2.6f), "#EC4899")
            Component3DModel(
                id = "tail_light_reverse_assemblies_3d",
                name = "Rear Tail Light Lenses, Brake Light Sockets & Reverse Wire Harness",
                system = VehicleSystem.LIGHTING_BODY,
                oemPartNumber = "1L2Z-13404-AA",
                description = "Rear bed corner tail lamp assemblies featuring red brake/tail light section (3157 dual-filament bulb), clear reverse light section (3156 bulb), amber turn signal section, rubber weatherproof bulb socket seals, and bed wiring harness plug.",
                locationDescription = "Mounted on rear outer bed corners adjacent to tailgate.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 20,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.8f, -2.6f),
                explodeVector = Point3D(0.0f, 0.8f, -3.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Tail Light Housing Screws (Ph2 - 2 Screws)", "18 in-lbs", "2 Nm", "Tighten into plastic bed grommets")
                ),
                requiredTools = listOf("Phillips #2 Screwdriver", "Dielectric Silicone Grease", "3157 & 3156 Replacement Bulbs"),
                repairSteps = listOf(
                    RepairStep(1, "Lower Tailgate", "Drop tailgate to reveal two Phillips screws on inner bed flange of tail light housing."),
                    RepairStep(2, "Unscrew Housing Screws", "Remove two Phillips screws securing tail light lens assembly to sheet metal bed pillar."),
                    RepairStep(3, "Pull Housing Outward", "Gently pull outer edge of housing straight out to release two plastic alignment alignment pins from bed rubber sockets."),
                    RepairStep(4, "Twist Sockets ¼-Turn to Change Bulbs", "Twist bulb socket ¼-turn counter-clockwise, pull out old 3157 bulb, coat contacts with dielectric grease, and snap new bulb in.")
                ),
                commonSymptoms = listOf("Brake lights do not illuminate when pressing brake pedal or turn signal blinks rapidly (hyper-flash due to burnt bulb)", "Water or condensation visible inside tail light lens", "Reverse lights stay dark when shifting into Reverse gear"),
                replacementIntervalMiles = 40000
            )
        },

        // 48. BORGWARNER 4411 TRANSFER CASE & ELECTRONIC 4WD SHIFT MOTOR
        run {
            val (v, f) = createBoxMesh(1.4f, 1.2f, 1.4f, Point3D(-0.2f, -0.3f, -0.6f), "#8B5CF6")
            Component3DModel(
                id = "transfer_case_shift_motor_3d",
                name = "BorgWarner 4411 Transfer Case & Electronic 4WD Shift Control Motor",
                system = VehicleSystem.DRIVETRAIN_4WD,
                oemPartNumber = "1L2Z-7A195-AA",
                description = "Cast aluminum BorgWarner 4411 electronic shift-on-the-fly transfer case complete with planetary gear reduction set, electromagnetic clutch assembly for 4x4 Auto torque splitting, internal oil pump, drive chain, and rear-mounted 12V DC electric shift encoder motor.",
                locationDescription = "Bolted directly behind 5R55E automatic transmission tailshaft housing under center chassis frame.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(-0.2f, -0.3f, -0.6f),
                explodeVector = Point3D(-0.6f, -0.8f, -0.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Transfer Case to Transmission Adapter Bolts (13mm)", "30", "41", ""),
                    TorqueSpec("4WD Shift Motor Mounting Bolts (T30 / 10mm)", "89 in-lbs", "10 Nm", "Three bolts attaching motor to shift shaft"),
                    TorqueSpec("Transfer Case Fill & Drain Plugs (3/8 In Drive)", "22", "30", "Uses 1.5 quarts MERCON V ATF")
                ),
                requiredTools = listOf("10mm & 13mm Sockets", "3/8-Inch Square Drive Ratchet", "MERCON V ATF (1.5 Qts) & Fluid Pump", "RTV Silicone Gasket Maker"),
                repairSteps = listOf(
                    RepairStep(1, "Unplug 4WD Shift Motor Wire Connector", "Press locking tab on 7-pin round wiring harness plug attached to rear of transfer case."),
                    RepairStep(2, "Unbolt 4WD Shift Motor", "Remove three 10mm bolts securing shift motor assembly to transfer case aluminum rear housing."),
                    RepairStep(3, "Inspect Triangular Shift Shaft Rotary Pin", "Use pliers to verify manual rotation of triangular shift shaft pin on transfer case."),
                    RepairStep(4, "Install New Shift Motor / Change ATF Fluid", "Align new motor socket onto triangular shaft pin, torque 10mm bolts, and drain/refill transfer case with 1.5 qts fresh MERCON V ATF.")
                ),
                commonSymptoms = listOf("4x4 High and 4x4 Low dash lights flash 6 times periodically while driving", "Turning 4WD dash switch produces no sound or engagement under truck", "Grinding noise when 4WD auto engages during rear wheel slip"),
                replacementIntervalMiles = 100000
            )
        },

        // 49. REAR ALUMINUM DRIVESHAFT, SLIP YOKE & HEAVY-DUTY U-JOINTS
        run {
            val (v, f) = createCylinderMesh(0.4f, 3.2f, 8, Point3D(0.0f, -0.4f, -1.6f), "#8B5CF6")
            Component3DModel(
                id = "rear_driveshaft_slip_yoke_3d",
                name = "Rear Aluminum Driveshaft Assembly, Slip Yoke & Heavy-Duty U-Joints",
                system = VehicleSystem.DRIVETRAIN_4WD,
                oemPartNumber = "1L2Z-4602-AA",
                description = "Balanced 4-inch diameter lightweight aluminum rear driveshaft tube featuring splined transmission slip yoke, rear pinion companion flange, and dual greaseable Spicer 1330 series universal joints.",
                locationDescription = "Extends from transfer case / transmission rear slip seal to rear differential pinion flange.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 60,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, -0.4f, -1.6f),
                explodeVector = Point3D(0.0f, -1.0f, -1.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Rear Pinion Flange 12-Point Bolts (12mm Head)", "83", "112", "Apply red threadlocker to 4 flange bolts"),
                    TorqueSpec("U-Joint Strap Bolts (8mm)", "15", "20", "")
                ),
                requiredTools = listOf("12mm 12-Point Socket & Breaker Bar", "Red Threadlocker 271", "U-Joint Press Tool / C-Clamp", "PTFE High-Temp Slip Yoke Grease"),
                repairSteps = listOf(
                    RepairStep(1, "Mark Pinion Flange Alignment", "Use paint pen to mark indexing alignment mark on driveshaft flange and differential pinion flange."),
                    RepairStep(2, "Remove Four 12-Point Flange Bolts", "Unscrew four 12mm 12-point bolts securing driveshaft flange to rear differential."),
                    RepairStep(3, "Slide Slip Yoke Out of Transfer Case", "Lower rear of driveshaft and slide front slip yoke out of transfer case rear extension housing seal."),
                    RepairStep(4, "Replace U-Joints & Grease Slip Yoke Splines", "Press out old dry needle bearing U-joints, install greaseable Spicer 1330 U-joints, apply PTFE grease to slip yoke splines, and torque flange bolts to 83 lb-ft with red threadlocker.")
                ),
                commonSymptoms = listOf("Squeaking or chirping noise from under truck that speeds up with vehicle speed (dry U-joint needle bearings)", "Loud clunking thump felt when shifting from Park into Drive or Reverse (worn U-joint play)", "High-speed driveline vibration felt through floorboards above 55 MPH"),
                replacementIntervalMiles = 90000
            )
        },

        // 50. FORD 8.8-INCH REAR DIFFERENTIAL AXLE WITH TRACTION-LOK
        run {
            val (v, f) = createBoxMesh(2.2f, 1.2f, 1.2f, Point3D(0.0f, -0.5f, -2.6f), "#8B5CF6")
            Component3DModel(
                id = "rear_differential_88_3d",
                name = "Ford 8.8-Inch Rear Differential Axle with Limited Slip Traction-Lok",
                system = VehicleSystem.DRIVETRAIN_4WD,
                oemPartNumber = "1L2Z-4000-AA",
                description = "Cast iron Ford 8.8-inch rear axle housing with 3.73 or 4.10 ring and pinion gear set, Traction-Lok multi-disc clutch pack limited-slip differential carrier, 31-spline axle shafts, steel rear cover pan, and ABS wheel speed sensor port.",
                locationDescription = "Center rear axle assembly supporting rear suspension leaf springs.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 150,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, -0.5f, -2.6f),
                explodeVector = Point3D(0.0f, -1.2f, -2.6f),
                torqueSpecs = listOf(
                    TorqueSpec("Differential Cover Pan Bolts (10 Bolts - 1/2 In Head)", "33", "45", "Tighten in star pattern with RTV sealant"),
                    TorqueSpec("Pinion Nut", "160", "217", "Sets pinion bearing preload"),
                    TorqueSpec("Differential Fill Plug (3/8 In Drive)", "22", "30", "Use 80W-90 / 75W-140 Synthetic + 4oz Friction Modifier")
                ),
                requiredTools = listOf("1/2-Inch & 9/16-Inch Sockets", "3/8-Inch Square Ratchet", "Permatex Ultra Grey RTV Gasket Maker", "75W-140 Synthetic Gear Oil (2.5 Qts)", "Ford XL-3 Friction Modifier Additive (4 oz)"),
                repairSteps = listOf(
                    RepairStep(1, "Place Drain Pan & Remove Cover Pan Bolts", "Unbolt 10 differential cover bolts, pry bottom of steel cover pan to drain old dark gear oil."),
                    RepairStep(2, "Clean Differential Housing & Cover Flanges", "Scrape old RTV gasket material clean off housing flange using razor scraper and brake cleaner."),
                    RepairStep(3, "Inspect Ring & Pinion Gear Teeth", "Inspect ring gear teeth for chipping, pitting, or excess backlash metal shavings on magnet."),
                    RepairStep(4, "Apply RTV Sealant & Refill Gear Oil", "Apply continuous 1/8-in bead of RTV grey silicone around cover pan, torque 10 bolts to 33 lb-ft, wait 1 hour, and fill with 2.5 qts 75W-140 synthetic gear oil + 4oz XL-3 friction modifier additive.")
                ),
                commonSymptoms = listOf("Whining or howling noise from rear end that changes when accelerating vs coasting (worn pinion/carrier bearings)", "Rear tires chatter, hop or bind during tight parking lot turns (worn Traction-Lok clutch packs needing friction modifier additive)", "Fluid leaking from bottom edge of differential cover pan or rear pinion seal"),
                replacementIntervalMiles = 100000
            )
        },

        // 51. CYLINDER HEADS, VALVES & HYDRAULIC ROLLER FOLLOWERS (4.0L SOHC)
        run {
            val (v, f) = createBoxMesh(2.0f, 1.0f, 1.2f, Point3D(0.0f, 1.1f, 0.2f), "#FF6F00")
            Component3DModel(
                id = "cylinder_heads_valvetrain_3d",
                name = "Aluminum Cylinder Heads, Valves, Hydraulic Roller Followers & Head Bolts",
                system = VehicleSystem.ENGINE,
                oemPartNumber = "1L2Z-6049-BA",
                description = "Cast aluminum cylinder heads for Cologne 4.0L SOHC V6 featuring 12 overhead valves (6 intake / 6 exhaust), single overhead camshaft per bank, hydraulic roller rocker arm followers, beehive valve springs, MLS multi-layer steel head gaskets, and torque-to-yield head bolts.",
                locationDescription = "Bolted to top left and right cylinder banks of engine block.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 360,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 1.1f, 0.2f),
                explodeVector = Point3D(0.0f, 1.8f, 0.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Head Bolts - Pass 1", "26", "35", "Use NEW TTY Bolts"),
                    TorqueSpec("Head Bolts - Pass 2", "59", "80", "Tighten in specified sequence"),
                    TorqueSpec("Head Bolts - Pass 3", "Rotate +90°", "Angle Torque", "Final angle torque turn")
                ),
                requiredTools = listOf("Torque Wrench & Angle Torque Gauge", "T55 Torx Socket", "Valve Spring Compressor", "MLS Head Gasket Set", "Cam Alignment Holding Tools"),
                repairSteps = listOf(
                    RepairStep(1, "Drain Engine Coolant & Remove Intakes", "Drain radiator coolant, remove intake manifold plenum, valve covers, and timing chain cassettes."),
                    RepairStep(2, "Unbolt Torque-to-Yield Cylinder Head Bolts", "Unbolt 8 head bolts per side using T55 Torx in reverse sequence.", warning = "Cologne 4.0L SOHC head bolts are Torque-to-Yield (TTY) and MUST be discarded and replaced with new bolts upon reassembly."),
                    RepairStep(3, "Lift Off Cylinder Head & Clean Deck Surface", "Lift off aluminum head, inspect combustion chambers and valves, scrape block deck surface perfectly flat."),
                    RepairStep(4, "Install MLS Head Gaskets & Torque Head Bolts", "Place fresh MLS steel head gaskets dry, position head, install NEW TTY bolts lubricated with engine oil, and follow 3-pass torque-to-yield sequence (26 ft-lb -> 59 ft-lb -> +90° turn).")
                ),
                commonSymptoms = listOf("Engine overheating with white sweet-smelling exhaust smoke and loss of coolant", "Milky chocolate-colored foam on engine oil dipstick (coolant mixing with motor oil due to blown head gasket)", "Low compression on adjacent cylinders causing misfire flash code P0300/P0301"),
                replacementIntervalMiles = 150000
            )
        },

        // 52. FRONT UPPER & LOWER CONTROL ARMS WITH HEAVY-DUTY BALL JOINTS
        run {
            val (v, f) = createBoxMesh(2.4f, 0.8f, 1.4f, Point3D(0.0f, -0.6f, 1.2f), "#EF4444")
            Component3DModel(
                id = "front_control_arms_balljoints_3d",
                name = "Front Upper & Lower Control Arms with Heavy-Duty Press-In Ball Joints",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "1L2Z-3078-AA",
                description = "Forged steel upper and lower front A-arm control suspension arms complete with natural rubber frame pivot bushings, greaseable heavy-duty press-in upper/lower ball joints, torsion bar mounting socket, and sway bar end link mounts.",
                locationDescription = "Connects front wheel steering knuckles to vehicle chassis frame.",
                difficulty = "Advanced",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, -0.6f, 1.2f),
                explodeVector = Point3D(0.0f, -1.2f, 1.8f),
                torqueSpecs = listOf(
                    TorqueSpec("Lower Ball Joint Pinch Nut", "83", "112", "Install fresh cotter pin"),
                    TorqueSpec("Upper Control Arm Camber Pinch Bolts (2 Bolts)", "98", "133", "Requires 4-wheel alignment after replacement"),
                    TorqueSpec("Lower Control Arm Frame Pivot Bolts", "111", "150", "Torque while vehicle weight is resting on suspension")
                ),
                requiredTools = listOf("C-Frame Ball Joint Press Tool Kit", "Pickle Fork / Ball Joint Separator", "18mm & 21mm Deep Sockets", "Torque Wrench (up to 150 lb-ft)", "Grease Gun with NLGI #2 EP Grease"),
                repairSteps = listOf(
                    RepairStep(1, "Jack Up Front Frame & Remove Front Wheel", "Secure vehicle on heavy jack stands, remove front 19mm wheel lug nuts and tire."),
                    RepairStep(2, "Unbolt Ball Joint Pinch Nuts & Separate Knuckle", "Unbolt upper and lower ball joint nuts, use ball joint separator tool to pop tapered ball joint studs free from steering knuckle."),
                    RepairStep(3, "Press Out Old Ball Joints", "Use C-frame ball joint press tool with receiver cups to press worn ball joint out of lower control arm bore."),
                    RepairStep(4, "Press In Greaseable Ball Joint / Align Alignment", "Press in new greaseable ball joint, install snap ring, torque ball joint nuts, grease Zerk fitting, and perform 4-wheel alignment.")
                ),
                commonSymptoms = listOf("Loud clunking thumping noise when driving over speed bumps or potholed roads", "Front tires showing severe uneven edge wear (feathering / cupping)", "Squeaking creaking sound when turning steering wheel at low speeds"),
                replacementIntervalMiles = 80000
            )
        },

        // 53. REAR SHOCK ABSORBER — 4WD VIN K
        run {
            val (v, f) = createRearShockMesh()
            Component3DModel(
                id = "rear_shock_absorbers_4wd_3d",
                name = "4WD Rear Shock Absorber Pair & Mounting Eyes",
                system = VehicleSystem.BRAKES_CHASSIS,
                oemPartNumber = "AFTERMARKET DIRECT-FIT — VERIFY VIN / AXLE CODE",
                description = "Pair of rear telescopic shock absorbers for the 2004 Explorer Sport Trac 4WD chassis, shown with steel damper bodies, polished piston rods, upper and lower mounting eyes, and service fastener interfaces. Geometry is a teaching aid; confirm replacement fitment against the VIN and axle code before purchase.",
                locationDescription = "One shock is mounted at each side of the rear axle, between the frame rail and rear axle/spring assembly.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 120,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, -0.8f, -1.9f),
                explodeVector = Point3D(0.0f, -1.4f, -1.0f),
                torqueSpecs = listOf(
                    TorqueSpec("Shock absorber-to-frame nuts", "17", "23", "4WD VIN K CHARM rear specification"),
                    TorqueSpec("Shock absorber lower bolt", "46", "63", "4WD VIN K CHARM rear specification"),
                    TorqueSpec("Wheel nuts", "100", "135", "Reinstall wheel and torque in star pattern")
                ),
                requiredTools = listOf("Floor jack and rated jack stands", "Wheel chocks", "18mm/19mm socket set", "Torque wrench (17–100 lb-ft range)", "Rust penetrant and wire brush", "Safety glasses and mechanic gloves"),
                repairSteps = listOf(
                    RepairStep(1, "Verify fitment and prepare the work area", "Confirm the replacement pair is listed for 2004 Explorer Sport Trac 4WD, compare the mounting-eye orientation, park on a level surface, apply the parking brake, chock the front wheels, and disconnect no electrical components.", warning = "Do not work beneath a vehicle supported only by a floor jack. Support the frame with rated jack stands before removing a shock fastener."),
                    RepairStep(2, "Lift and support the rear of the truck", "Loosen the rear wheel nuts slightly, lift the rear frame with a floor jack, place rated jack stands under the frame, and keep the axle supported separately so it cannot drop when the shock is removed.", warning = "Never place a stand under a thin body panel or an unstable suspension arm. Confirm the vehicle is stable before entering the work zone."),
                    RepairStep(3, "Remove the upper shock mounting nuts", "Brush the exposed threads, apply penetrant, and remove the upper shock absorber-to-frame nuts. If the stud turns with the nut, use the correct counter-hold method rather than applying heat near rubber components or fuel-system parts."),
                    RepairStep(4, "Remove the lower bolt and the old shock", "Support the axle, remove the lower shock absorber bolt, then withdraw the shock. Compare the old and new units for length, bushing stack, eye orientation, and physical damage before installation."),
                    RepairStep(5, "Install the replacement shock pair", "Install both shocks with the same orientation and hardware stack as removed. Start all threads by hand; do not fully tighten the bushings while the axle is hanging if the mount design requires ride-height loading."),
                    RepairStep(6, "Torque, lower, and inspect", "Torque the upper frame nuts to 17 lb-ft (23 N·m) and the lower bolts to 46 lb-ft (63 N·m) using the 4WD VIN K rear specification. Reinstall wheels and torque wheel nuts to 100 lb-ft (135 N·m), lower the vehicle, and perform a bounce/visual inspection for contact, loose hardware, or asymmetric ride height.", tip = "After a short, low-speed test, recheck for oil leakage, loose hardware, abnormal noise, and correct shock clearance. Stop immediately if the vehicle wanders or a mount shifts.")
                ),
                commonSymptoms = listOf("Rear-end float or repeated bouncing after a bump", "Damped oil leakage from a shock body", "Uneven rear tire cupping or scalloped tread", "Clunking from a loose shock mount"),
                replacementIntervalMiles = null,
                fasteners = listOf(
                    FastenerInventoryItem("Upper shock-to-frame nut", FastenerCategory.BOLT, 2, "Factory rear shock mount", "Torque wrench", "Torque 23 N·m / 17 lb-ft per 4WD VIN K rear specification"),
                    FastenerInventoryItem("Lower shock mounting bolt", FastenerCategory.BOLT, 2, "Factory rear shock lower mount", "Torque wrench", "Torque 63 N·m / 46 lb-ft per 4WD VIN K rear specification"),
                    FastenerInventoryItem("Wheel nuts", FastenerCategory.BOLT, 10, "Wheel stud nuts", "Torque wrench", "Torque 135 N·m / 100 lb-ft after wheel installation")
                ),
                subAssemblies = listOf(
                    SubAssemblyPart("rear_shock_damper_body", "Twin-tube damper body", SubAssemblyType.MAIN_BODY, v, f, specDetails = "Teaching geometry; verify actual replacement dimensions"),
                    SubAssemblyPart("rear_shock_upper_eye", "Upper frame mounting eye", SubAssemblyType.BOLT, emptyList(), emptyList(), localOffset = Point3D(0f, 1.28f, 0f), specDetails = "Upper nuts: 23 N·m / 17 lb-ft"),
                    SubAssemblyPart("rear_shock_lower_eye", "Lower axle mounting eye", SubAssemblyType.BOLT, emptyList(), emptyList(), localOffset = Point3D(0f, -1.23f, 0f), specDetails = "Lower bolt: 63 N·m / 46 lb-ft")
                ),
                manualSectionRef = "Operation CHARM 4WD VIN K: Suspension > Specifications > Rear"
            )
        },

        // 54. MACH 500 / PIONEER PREMIUM DOUBLE-DIN RADIO HEAD UNIT & CD CHANGER
        run {
            val (v, f) = createBoxMesh(1.1f, 0.8f, 0.9f, Point3D(0.0f, 0.85f, 0.65f), "#F59E0B")
            Component3DModel(
                id = "radio_mach500_head_unit_3d",
                name = "Mach 500 / Pioneer Premium Double-DIN AM/FM Radio & CD Changer Head Unit",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2F-18C815-AA",
                description = "Factory premium double-DIN stereo receiver head unit featuring integrated 6-disc in-dash CD changer mechanism, RDS digital radio tuner module, dot-matrix green vacuum fluorescent display screen, dual volume/tuner rotary encoder dials, speed-compensated volume control processor, rear subwoofer preamp output plug, and dual 16-pin Ford factory wiring harness sockets.",
                locationDescription = "Mounted in center dash stack bezel directly above HVAC climate control knobs.",
                difficulty = "Beginner",
                estimatedTimeMinutes = 20,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.0f, 0.85f, 0.65f),
                explodeVector = Point3D(0.0f, 1.2f, 1.5f),
                torqueSpecs = listOf(
                    TorqueSpec("Center Dash Bezel Screws (7mm Head)", "18 in-lbs", "2 Nm", "Two 7mm screws located above climate control knobs"),
                    TorqueSpec("Radio Side Mounting Bracket Screws", "15 in-lbs", "1.7 Nm", "Four mini T15 Torx screws into side chassis")
                ),
                requiredTools = listOf("7mm Socket & Nut Driver", "Ford U-Shaped DIN Radio Removal Key Tool Set (Ford # T83P-19061-A)", "Plastic Trim Removal Tool", "Electrical Contact Cleaner Spray"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Dash Bezel Screws", "Unscrew two 7mm hex screws located in the lip directly above climate control rotary knobs."),
                    RepairStep(2, "Unclip Center Dash Bezel", "Gently insert plastic pry bar around radio bezel edge and pop four spring steel retention clips free from dashboard."),
                    RepairStep(3, "Insert Ford DIN Removal Tool Keys", "Insert pair of U-shaped Ford DIN removal keys into four holes on radio faceplate until internal spring clips click open."),
                    RepairStep(4, "Slide Radio Chassis Out & Unplug Harnesses", "Spread DIN keys outward slightly and pull head unit straight forward. Unplug main 16-pin wiring harness, subwoofer RCA/remote plug, and motorola antenna coax lead."),
                    RepairStep(5, "Install Replacement Head Unit / Aftermarket Adapter", "Connect Ford wiring harness adapter plug, snap antenna coax lead in place, and slide chassis into center stack frame until latches lock.")
                ),
                commonSymptoms = listOf(
                    "High-pitch alternator whistle/whine pitch tracking engine RPM through speakers due to bad stereo ground or alternator diode ripple",
                    "CD Changer mechanism jammed with 'CD ERROR' or 'INITIALIZING' displayed on VFD screen",
                    "Radio volume knob skips erratically or fails to respond when rotated",
                    "Head unit screen goes completely blank or internal display backlights burn out"
                ),
                replacementIntervalMiles = 100000
            )
        },

        // 54. FACTORY 6x8 CUSTOM DOOR SPEAKERS & PIONEER POWERED SUBWOOFER SYSTEM
        run {
            val (v, f) = createCylinderMesh(0.7f, 0.5f, 8, Point3D(0.8f, 0.4f, -0.2f), "#F59E0B")
            Component3DModel(
                id = "audio_door_speakers_subwoofer_3d",
                name = "Factory 6x8 Custom Door Speakers, Tweeters & Pioneer 8\" Powered Subwoofer",
                system = VehicleSystem.INTERIOR_DASH,
                oemPartNumber = "1L2Z-18808-BA",
                description = "High-fidelity vehicle audio transducer system comprising four 6x8-inch polypropylene full-range coaxial door speakers with treated cloth surrounds, A-pillar silk dome tweeters, and the factory Pioneer rear cabin trim-integrated 8-inch powered subwoofer enclosure with dedicated 290-watt peak audio amplifier module.",
                locationDescription = "Door speaker units mounted inside all four interior door trim panels; Pioneer subwoofer mounted behind rear passenger trim panel.",
                difficulty = "Intermediate",
                estimatedTimeMinutes = 45,
                vertices = v,
                faces = f,
                centerOffset = Point3D(0.8f, 0.4f, -0.2f),
                explodeVector = Point3D(1.6f, 0.6f, -0.2f),
                torqueSpecs = listOf(
                    TorqueSpec("Door Speaker Mounting Screws (4 Screws per Door)", "18 in-lbs", "2 Nm", "7mm screws into plastic door sheet metal inserts"),
                    TorqueSpec("Door Interior Handle Screws (7mm Head)", "25 in-lbs", "2.8 Nm", "Two screws behind armrest access cover"),
                    TorqueSpec("Pioneer Subwoofer Enclosure Bracket Bolts", "89 in-lbs", "10 Nm", "Three 10mm bolts to rear cab wall")
                ),
                requiredTools = listOf("7mm & 8mm Sockets & Driver", "T20 Torx Bit", "Plastic Interior Door Trim Removal Pry Tool", "Wire Stripper & Crimp Connectors"),
                repairSteps = listOf(
                    RepairStep(1, "Remove Interior Door Trim Panel", "Remove lower door reflector lens, unscrew two 7mm screws under armrest handle, and unclip plastic window switch bezel."),
                    RepairStep(2, "Pry Trim Panel From Door Frame", "Use plastic trim pry bar to release perimeter christmas tree push-pin fasteners and lift door panel upward off window ledge."),
                    RepairStep(3, "Unscrew 6x8 Speaker & Disconnect Plug", "Unscrew four 7mm hex screws securing speaker frame to door frame and disconnect 2-pin Ford speaker wire plug."),
                    RepairStep(4, "Install 6x8 Speaker & Harness Adapter", "Snap plug-and-play Ford speaker wire harness adapter onto speaker terminals, secure frame with 7mm screws, and reattach door trim panel."),
                    RepairStep(5, "Inspect Pioneer Subwoofer Fuse & Ground", "If rear subwoofer produces no bass, verify 15A audio amplifier fuse #1.03 in engine junction box and clean rear cab floor ground eyelet.")
                ),
                commonSymptoms = listOf(
                    "Loud crackling, static scratching, or muffled sound when playing music at high volume (dry rotted speaker cone surround or torn voice coil)",
                    "Complete silence from individual door speaker channel (broken wire in door hinge rubber boot or burnt speaker coil)",
                    "Pioneer rear subwoofer produces zero low-frequency bass or rattles loudly inside rear trim panel"
                ),
                replacementIntervalMiles = 80000
            )
        }
    ).map(VehicleHardwareCatalog::enrich)

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
            componentDescription = "Motorcraft FL-820S Oil Filter & 5.0 Qts 5W-30 Motor Oil",
            costUsd = 45.0,
            notes = "Used 5.0 qts Motorcraft 5W-30 synthetic blend and FL-820S filter. Oil pressure normal.",
            isCompleted = true
        ),
        MaintenanceEntity(
            id = 2,
            scheduleItemId = "coolant_flush",
            title = "Cooling System Flush & Thermostat Housing",
            systemName = VehicleSystem.COOLING.displayName,
            mileageAtService = 110000,
            dateLoggedMillis = System.currentTimeMillis() - (120L * 24 * 3600 * 1000),
            componentDescription = "Upgraded Aluminum Thermostat Housing Assembly & Motorcraft Premium Gold Coolant",
            costUsd = 120.0,
            notes = "Replaced factory plastic thermostat housing with upgraded metal assembly. Refilled with Motorcraft Gold coolant.",
            isCompleted = true
        ),
        MaintenanceEntity(
            id = 3,
            scheduleItemId = "trans_fluid",
            title = "5R55E Transmission Fluid & Solenoid Filter Service",
            systemName = VehicleSystem.TRANSMISSION.displayName,
            mileageAtService = 105000,
            dateLoggedMillis = System.currentTimeMillis() - (180L * 24 * 3600 * 1000),
            componentDescription = "5R55E Valve Body Solenoid Pack, Filter & MERCON V ATF Fluid",
            costUsd = 185.0,
            notes = "Replaced transmission filter and pan gasket. Refilled with MERCON V ATF. Solenoid pressure confirmed.",
            isCompleted = true
        ),
        MaintenanceEntity(
            id = 4,
            scheduleItemId = "spark_plugs",
            title = "Spark Plugs & EDIS Ignition Coil Pack Replacement",
            systemName = VehicleSystem.ENGINE.displayName,
            mileageAtService = 98000,
            dateLoggedMillis = System.currentTimeMillis() - (300L * 24 * 3600 * 1000),
            componentDescription = "Motorcraft AGSF-22PP Platinum Plugs & 6-Tower EDIS Coil Pack",
            costUsd = 95.0,
            notes = "Gapped spark plugs to 0.054 inches. Replaced original EDIS ignition coil pack to fix cylinder misfire.",
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
        ),

        DiagnosticSymptomCategory(
            id = "cat_audio_radio",
            categoryName = "Radio, Audio & Speakers Diagnostics",
            system = VehicleSystem.INTERIOR_DASH,
            commonSymptoms = listOf("High pitch engine RPM whistle through speakers", "Door speaker static or muffled crackle", "CD changer jammed with error", "Pioneer subwoofer no bass"),
            rootQuestion = DiagnosticQuestion(
                id = "q_audio_symptom",
                questionText = "What specific audio or radio issue are you experiencing in your Sport Trac?",
                options = listOf(
                    DiagnosticOption(
                        optionText = "High-pitched whining or whistling sound from speakers that changes pitch when stepping on the gas pedal",
                        resultDiagnosis = DiagnosticResult(
                            title = "Alternator Ground Noise / Diode Ripple Interference in Audio System",
                            problemSummary = "Alternator AC diode ripple or a degraded engine block/firewall ground strap induces electrical noise directly into the radio power feed or unshielded speaker wires.",
                            probableCause = "Loose firewall ground strap or faulty alternator diode bridge leaking AC voltage into stereo 12V line.",
                            obdCode = "None",
                            targetComponentId = "radio_mach500_head_unit_3d",
                            confidencePercentage = 96,
                            urgencyLevel = "Low",
                            recommendedAction = "Clean engine firewall ground lug, test alternator diode ripple with multimeter AC scale, or install an inline 12V audio power noise choke filter."
                        )
                    ),
                    DiagnosticOption(
                        optionText = "Door speaker produces loud static, scratching crackle, or dead sound when volume is turned up",
                        resultDiagnosis = DiagnosticResult(
                            title = "Door Speaker Dry Rotted Cone Surround or Burnt Voice Coil",
                            problemSummary = "Exposure to humidity and heat inside door panels degrades the factory foam or cloth surround of the 6x8 speakers over time, causing the voice coil to rub against the magnet.",
                            probableCause = "Dry rotted speaker surround or broken door hinge wire harness flex leads.",
                            obdCode = "None",
                            targetComponentId = "audio_door_speakers_subwoofer_3d",
                            confidencePercentage = 94,
                            urgencyLevel = "Low",
                            recommendedAction = "Remove interior door trim panel, test 6x8 speaker terminals for 4-ohm resistance with multimeter, and replace with fresh 6x8 coaxial speakers."
                        )
                    ),
                    DiagnosticOption(
                        optionText = "Mach 500 radio display shows 'CD ERROR' or CD changer is physically jammed and won't eject discs",
                        resultDiagnosis = DiagnosticResult(
                            title = "In-Dash 6-Disc CD Changer Mechanical Ejection Jam",
                            problemSummary = "Internal plastic drive gears and disc loading trays in the factory Mach 500 head unit jam or slip off guide tracks after years of thermal expansion.",
                            probableCause = "Broken internal CD tray slider gear or misaligned optical pickup laser.",
                            obdCode = "None",
                            targetComponentId = "radio_mach500_head_unit_3d",
                            confidencePercentage = 91,
                            urgencyLevel = "Low",
                            recommendedAction = "Pull radio head unit using U-shaped Ford DIN removal keys. Disassemble top cover to manually clear stuck disc or upgrade to modern double-DIN Bluetooth head unit."
                        )
                    )
                )
            )
        )
    )
}
