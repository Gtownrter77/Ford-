package com.example.data

import com.example.model.Point3D

/**
 * Exact 2004 Explorer Sport Trac scale.
 *
 * Printed Owner Guide dimensions are the authority for length, width,
 * height, wheelbase, and track. Overhang, bed, tire, and ground-clearance
 * numbers that the OG does not print are labeled as such.
 *
 * World units: 1.0 = 1 meter.
 * Axes: +X passenger / right, +Y up, +Z forward.
 * Origin: ground plane, mid-wheelbase, vehicle centerline.
 */
object SportTracVehicleScale {
    const val SOURCE_OG = OwnerGuideSpecs.SOURCE
    const val INCH = 0.0254f

    fun inches(value: Float): Float = value * INCH
    fun inches(value: Double): Float = (value * 0.0254).toFloat()

    const val LENGTH_IN = 205.9f
    const val WIDTH_IN = 71.8f
    const val HEIGHT_IN = 69.9f
    const val HEIGHT_4X4_16_IN = 70.6f
    const val WHEELBASE_IN = 125.9f
    const val TRACK_FRONT_IN = 58.5f
    const val TRACK_REAR_IN = 58.3f
    const val LENGTH_MM = 5230
    const val WIDTH_MM = 1823
    const val HEIGHT_MM = 1776
    const val HEIGHT_4X4_16_MM = 1794
    const val WHEELBASE_MM = 3198
    const val TRACK_FRONT_MM = 1486
    const val TRACK_REAR_MM = 1480

    val lengthM: Float = inches(LENGTH_IN)
    val widthM: Float = inches(WIDTH_IN)
    val heightM: Float = inches(HEIGHT_IN)
    val height4x4M: Float = inches(HEIGHT_4X4_16_IN)
    val wheelbaseM: Float = inches(WHEELBASE_IN)
    val trackFrontM: Float = inches(TRACK_FRONT_IN)
    val trackRearM: Float = inches(TRACK_REAR_IN)

    const val FRONT_OVERHANG_IN = 34.6f
    const val REAR_OVERHANG_IN = 45.4f
    const val BED_FLOOR_LENGTH_IN = 50.0f
    const val BED_INSIDE_HEIGHT_IN = 19.7f
    const val BED_WIDTH_FLOOR_IN = 51.2f
    const val BED_WIDTH_WHEELHOUSE_IN = 41.2f
    const val LOAD_FLOOR_HEIGHT_IN = 31.8f
    const val GROUND_CLEARANCE_IN = 6.7f
    const val SECONDARY_SOURCE =
        "Overhang / bed / clearance from published 2001-2005 Sport Trac dimensional sheets, not the OG table."

    val frontOverhangM: Float = inches(FRONT_OVERHANG_IN)
    val rearOverhangM: Float = inches(REAR_OVERHANG_IN)
    val bedFloorLengthM: Float = inches(BED_FLOOR_LENGTH_IN)
    val bedInsideHeightM: Float = inches(BED_INSIDE_HEIGHT_IN)
    val bedWidthFloorM: Float = inches(BED_WIDTH_FLOOR_IN)
    val bedWidthWheelhouseM: Float = inches(BED_WIDTH_WHEELHOUSE_IN)
    val loadFloorHeightM: Float = inches(LOAD_FLOOR_HEIGHT_IN)
    val groundClearanceM: Float = inches(GROUND_CLEARANCE_IN)

    const val TIRE_SECTION_M = 0.265f
    const val TIRE_ASPECT = 0.70f
    const val WHEEL_DIAMETER_IN = 16.0f
    val sidewallM: Float = TIRE_SECTION_M * TIRE_ASPECT
    val tireOuterDiameterM: Float = inches(WHEEL_DIAMETER_IN) + 2f * sidewallM
    val tireRadiusM: Float = tireOuterDiameterM / 2f
    val wheelRadiusM: Float = inches(WHEEL_DIAMETER_IN) / 2f

    val frontAxleZ: Float get() = wheelbaseM / 2f
    val rearAxleZ: Float get() = -wheelbaseM / 2f
    val frontBumperZ: Float get() = frontAxleZ + frontOverhangM
    val rearBumperZ: Float get() = rearAxleZ - rearOverhangM
    val frontTrackHalfM: Float get() = trackFrontM / 2f
    val rearTrackHalfM: Float get() = trackRearM / 2f

    val bedRearZ: Float get() = rearBumperZ + inches(4.0f)
    val bedFrontZ: Float get() = bedRearZ + bedFloorLengthM
    val cabRearZ: Float get() = bedFrontZ
    val cowlZ: Float get() = frontAxleZ - inches(8.0f)
    val hoodFrontZ: Float get() = frontBumperZ - inches(3.0f)

    fun wheelCenter(front: Boolean, passenger: Boolean): Point3D {
        val x = if (passenger) {
            if (front) frontTrackHalfM else rearTrackHalfM
        } else {
            if (front) -frontTrackHalfM else -rearTrackHalfM
        }
        val z = if (front) frontAxleZ else rearAxleZ
        return Point3D(x, tireRadiusM, z)
    }

    fun overallFitsOgEnvelope(): Boolean {
        val reconstructed = frontOverhangM + wheelbaseM + rearOverhangM
        return kotlin.math.abs(reconstructed - lengthM) < 0.002f
    }
}
