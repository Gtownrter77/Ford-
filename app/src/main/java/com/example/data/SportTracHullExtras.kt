package com.example.data

import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.VehicleSystem

/**
 * Remaining service systems that sit on the meter-true hull.
 * Box / cylinder envelopes only. Confirm workshop geometry before wrenching.
 */
object SportTracHullExtras {

    val components: List<Component3DModel> by lazy { build() }

    private fun build(): List<Component3DModel> {
        val s = SportTracVehicleScale
        val h = SportTracScaledHull
        val yAxle = s.tireRadiusM
        return listOf(
            extra("scaled_exhaust", "Exhaust / muffler envelope", VehicleSystem.ENGINE, "SCALE-EXHAUST",
                "Single pipe + muffler under the bed. Not a VIN-scanned pipe route.",
                h.box(0.18f, 0.16f, 1.80f, Point3D(-0.22f, 0.22f, s.rearAxleZ + 0.35f), "#78716C"),
                Point3D(-0.22f, 0.22f, s.rearAxleZ + 0.35f), Point3D(0f, -0.25f, -0.2f)),
            extra("scaled_fuel_tank", "Fuel tank envelope (22.5 gal OG)", VehicleSystem.ENGINE, "SCALE-FUEL",
                "Tank under the bed ahead of the rear axle. OG capacity ${OwnerGuideSpecs.FUEL_TANK}.",
                h.box(0.72f, 0.28f, 0.70f, Point3D(0f, 0.28f, s.rearAxleZ + 0.55f), "#CA8A04"),
                Point3D(0f, 0.28f, s.rearAxleZ + 0.55f), Point3D(0f, -0.3f, 0f)),
            extra("scaled_battery", "Battery tray (driver fender)", VehicleSystem.ELECTRICAL, "SCALE-BATT",
                "Group-size envelope in the driver-front corner. Motorcraft test / charge per OG.",
                h.box(0.18f, 0.18f, 0.28f, Point3D(-0.48f, 0.78f, s.frontAxleZ + 0.42f), "#EAB308"),
                Point3D(-0.48f, 0.78f, s.frontAxleZ + 0.42f), Point3D(-0.25f, 0.2f, 0.15f)),
            extra("scaled_alternator", "Alternator envelope", VehicleSystem.ELECTRICAL, "SCALE-ALT",
                "Passenger-front of the 4.0L accessory drive.",
                h.box(0.16f, 0.16f, 0.14f, Point3D(0.32f, 0.86f, s.frontAxleZ + 0.28f), "#FACC15"),
                Point3D(0.32f, 0.86f, s.frontAxleZ + 0.28f), Point3D(0.25f, 0.15f, 0.1f)),
            extra("scaled_steering", "Steering gear envelope", VehicleSystem.BRAKES_CHASSIS, "SCALE-STEER",
                "Recirculating-ball box on the driver frame rail.",
                h.box(0.16f, 0.14f, 0.22f, Point3D(-0.38f, 0.42f, s.frontAxleZ + 0.18f), "#EF4444"),
                Point3D(-0.38f, 0.42f, s.frontAxleZ + 0.18f), Point3D(-0.25f, 0f, 0.1f)),
            extra("scaled_caliper_fl", "Front caliper LH", VehicleSystem.BRAKES_CHASSIS, "SCALE-CAL-FL",
                "Twin-piston envelope inboard of the front-left wheel.",
                h.box(0.08f, 0.14f, 0.16f, Point3D(-s.trackFrontM / 2f + 0.08f, yAxle, s.frontAxleZ), "#F87171"),
                Point3D(-s.trackFrontM / 2f + 0.08f, yAxle, s.frontAxleZ), Point3D(-0.2f, 0f, 0f)),
            extra("scaled_caliper_fr", "Front caliper RH", VehicleSystem.BRAKES_CHASSIS, "SCALE-CAL-FR",
                "Twin-piston envelope inboard of the front-right wheel.",
                h.box(0.08f, 0.14f, 0.16f, Point3D(s.trackFrontM / 2f - 0.08f, yAxle, s.frontAxleZ), "#F87171"),
                Point3D(s.trackFrontM / 2f - 0.08f, yAxle, s.frontAxleZ), Point3D(0.2f, 0f, 0f)),
            extra("scaled_shaft_front", "Front driveshaft", VehicleSystem.DRIVETRAIN_4WD, "SCALE-DS-F",
                "T-case to front axle. U-joint caps are service fasteners.",
                h.cylinder(0.04f, 0.95f, 10, Point3D(0f, 0.36f, s.frontAxleZ - 0.55f), "#A78BFA", 'Z'),
                Point3D(0f, 0.36f, s.frontAxleZ - 0.55f), Point3D(0f, -0.2f, 0.15f)),
            extra("scaled_shaft_rear", "Rear driveshaft", VehicleSystem.DRIVETRAIN_4WD, "SCALE-DS-R",
                "T-case to 8.8. Flange bolts are the service stack.",
                h.cylinder(0.045f, 1.55f, 10, Point3D(0f, 0.34f, (s.frontAxleZ - 1.22f + s.rearAxleZ) / 2f), "#C4B5FD", 'Z'),
                Point3D(0f, 0.34f, (s.frontAxleZ - 1.22f + s.rearAxleZ) / 2f), Point3D(0f, -0.2f, -0.15f)),
            extra("scaled_airbox", "Air-box / FA-1744 envelope", VehicleSystem.AIR_INTAKE, "FA-1744",
                "Air filter housing, passenger inner fender. Filter ${OwnerGuideSpecs.AIR_FILTER}.",
                h.box(0.28f, 0.22f, 0.32f, Point3D(0.42f, 0.92f, s.frontAxleZ + 0.35f), "#0284C7"),
                Point3D(0.42f, 0.92f, s.frontAxleZ + 0.35f), Point3D(0.25f, 0.2f, 0.1f)),
            extra("scaled_condenser", "A/C condenser envelope", VehicleSystem.AIR_CONDITIONING, "SCALE-COND",
                "Ahead of the radiator. Spring-lock fittings are not modeled as bolts.",
                h.box(s.widthM * 0.58f, 0.40f, 0.04f, Point3D(0f, 0.70f, s.hoodFrontZ - 0.02f), "#10B981"),
                Point3D(0f, 0.70f, s.hoodFrontZ - 0.02f), Point3D(0f, 0f, 0.28f)),
            extra("scaled_headlamp_l", "Headlamp LH", VehicleSystem.LIGHTING_BODY, "SCALE-HL-L",
                "Composite lamp envelope. Aim screws are the service fasteners.",
                h.box(0.22f, 0.16f, 0.10f, Point3D(-0.52f, 0.72f, s.hoodFrontZ + 0.02f), "#EC4899"),
                Point3D(-0.52f, 0.72f, s.hoodFrontZ + 0.02f), Point3D(-0.15f, 0f, 0.2f)),
            extra("scaled_headlamp_r", "Headlamp RH", VehicleSystem.LIGHTING_BODY, "SCALE-HL-R",
                "Composite lamp envelope.",
                h.box(0.22f, 0.16f, 0.10f, Point3D(0.52f, 0.72f, s.hoodFrontZ + 0.02f), "#EC4899"),
                Point3D(0.52f, 0.72f, s.hoodFrontZ + 0.02f), Point3D(0.15f, 0f, 0.2f)),
            extra("scaled_taillamp_l", "Taillamp LH", VehicleSystem.LIGHTING_BODY, "SCALE-TL-L",
                "Bed-side lamp. Two nuts behind the lens.",
                h.box(0.16f, 0.22f, 0.08f, Point3D(-0.70f, 0.78f, s.rearBumperZ + 0.06f), "#DB2777"),
                Point3D(-0.70f, 0.78f, s.rearBumperZ + 0.06f), Point3D(-0.15f, 0f, -0.15f)),
            extra("scaled_taillamp_r", "Taillamp RH", VehicleSystem.LIGHTING_BODY, "SCALE-TL-R",
                "Bed-side lamp.",
                h.box(0.16f, 0.22f, 0.08f, Point3D(0.70f, 0.78f, s.rearBumperZ + 0.06f), "#DB2777"),
                Point3D(0.70f, 0.78f, s.rearBumperZ + 0.06f), Point3D(0.15f, 0f, -0.15f)),
            extra("scaled_spare", "Spare tire under bed", VehicleSystem.BRAKES_CHASSIS, "SCALE-SPARE",
                "Full-size spare winch envelope under the bed floor.",
                h.cylinder(s.tireRadiusM * 0.92f, s.TIRE_SECTION_M, 14, Point3D(0f, 0.22f, s.bedRearZ + 0.10f), "#1F2937", 'Y'),
                Point3D(0f, 0.22f, s.bedRearZ + 0.10f), Point3D(0f, -0.35f, 0f))
        )
    }

    private fun extra(
        id: String,
        name: String,
        system: VehicleSystem,
        oem: String,
        description: String,
        mesh: Pair<List<Point3D>, List<com.example.model.Face3D>>,
        center: Point3D,
        explode: Point3D
    ): Component3DModel = Component3DModel(
        id = id,
        name = name,
        system = system,
        oemPartNumber = oem,
        description = description,
        locationDescription = "Meter-true hull extra. Confirm workshop manual.",
        difficulty = "Reference model",
        estimatedTimeMinutes = 0,
        vertices = mesh.first,
        faces = mesh.second,
        centerOffset = center,
        explodeVector = explode,
        torqueSpecs = emptyList(),
        requiredTools = emptyList(),
        repairSteps = emptyList(),
        commonSymptoms = emptyList()
    )
}
