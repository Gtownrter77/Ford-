package com.example.util

import com.example.model.Face3D
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural low-poly CAD primitives used by the exploded-view renderer.
 *
 * Each primitive is intentionally modelled as a discrete part rather than an
 * instanced count so that the assembly view can identify, filter, and explode
 * every displayed service fastener individually.
 */
object SubAssemblyMeshGenerator {

    /**
     * Backwards-compatible OEM hex bolt creator. It now delegates to the
     * threaded variant so existing assemblies receive the higher-detail mesh.
     */
    fun createHexBoltSubAssembly(
        id: String,
        name: String,
        headRadius: Float = 0.12f,
        headHeight: Float = 0.10f,
        shankRadius: Float = 0.05f,
        shankLength: Float = 0.40f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 3.5f,
        colorHex: String = "#E2E8F0",
        specDetails: String = "M8x1.25 Flange Bolt • 89 in-lbs"
    ): SubAssemblyPart = createThreadedHexBoltSubAssembly(
        id = id,
        name = name,
        headRadius = headRadius,
        headHeight = headHeight,
        shankRadius = shankRadius,
        shankLength = shankLength,
        localOffset = localOffset,
        explodeDir = explodeDir,
        explodeMultiplier = explodeMultiplier,
        colorHex = colorHex,
        specDetails = specDetails
    )

    /**
     * Generates a six-point hex-head bolt with a faceted helical thread
     * profile. The bolt axis is local Y, which matches the exploded-view mesh
     * convention used by the app.
     */
    fun createThreadedHexBoltSubAssembly(
        id: String,
        name: String,
        headRadius: Float = 0.12f,
        headHeight: Float = 0.10f,
        shankRadius: Float = 0.05f,
        shankLength: Float = 0.40f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 3.5f,
        colorHex: String = "#E2E8F0",
        specDetails: String = "M8x1.25 Flange Bolt • 89 in-lbs"
    ): SubAssemblyPart {
        val vertices = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()
        val hexSegments = 6
        val shankSegments = 10

        fun addRing(radius: Float, y: Float, segments: Int): Int {
            val start = vertices.size
            repeat(segments) { index ->
                val angle = 2.0 * PI * index / segments
                vertices += Point3D(
                    localOffset.x + radius * cos(angle).toFloat(),
                    localOffset.y + y,
                    localOffset.z + radius * sin(angle).toFloat()
                )
            }
            return start
        }

        fun bridgeRings(firstStart: Int, secondStart: Int, segments: Int, faceColor: String) {
            repeat(segments) { index ->
                val next = (index + 1) % segments
                faces += Face3D(
                    listOf(firstStart + index, firstStart + next, secondStart + next, secondStart + index),
                    faceColor
                )
            }
        }

        val headTop = addRing(headRadius, 0f, hexSegments)
        val headBottom = addRing(headRadius, -headHeight, hexSegments)
        bridgeRings(headTop, headBottom, hexSegments, colorHex)
        faces += Face3D((0 until hexSegments).map { headTop + it }, colorHex)

        // Transition ring under the head prevents a visual gap at the flange.
        val transition = addRing(shankRadius * 1.08f, -headHeight, shankSegments)
        repeat(hexSegments) { index ->
            val next = (index + 1) % hexSegments
            val transitionA = transition + ((index * shankSegments) / hexSegments)
            val transitionB = transition + ((next * shankSegments) / hexSegments % shankSegments)
            faces += Face3D(
                listOf(headBottom + index, headBottom + next, transitionB, transitionA),
                colorHex
            )
        }

        // Alternating crest/root rings provide a visual thread profile without
        // requiring a prohibitively dense mesh on a mobile Canvas renderer.
        val threadSteps = 10
        var previousRing = transition
        repeat(threadSteps) { step ->
            val fraction = (step + 1).toFloat() / threadSteps
            val radius = if (step % 2 == 0) shankRadius * 1.14f else shankRadius
            val ring = addRing(radius, -headHeight - shankLength * fraction, shankSegments)
            bridgeRings(previousRing, ring, shankSegments, colorHex)
            previousRing = ring
        }
        faces += Face3D((0 until shankSegments).map { previousRing + it }.reversed(), colorHex)

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.BOLT,
            vertices = vertices,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails,
            metallicFactor = 0.92f,
            roughnessFactor = 0.22f
        )
    }

    /** Generates a pan-head Torx screw with a visually distinct recessed drive. */
    fun createTorxScrewSubAssembly(
        id: String,
        name: String,
        headRadius: Float = 0.10f,
        headHeight: Float = 0.06f,
        shankRadius: Float = 0.035f,
        shankLength: Float = 0.28f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 3.2f,
        colorHex: String = "#94A3B8",
        specDetails: String = "T20 Torx self-tapping screw"
    ): SubAssemblyPart {
        val vertices = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()
        val segments = 12

        fun ring(radius: Float, y: Float): Int {
            val start = vertices.size
            repeat(segments) { index ->
                val angle = 2.0 * PI * index / segments
                vertices += Point3D(
                    localOffset.x + radius * cos(angle).toFloat(),
                    localOffset.y + y,
                    localOffset.z + radius * sin(angle).toFloat()
                )
            }
            return start
        }

        fun bridge(first: Int, second: Int, faceColor: String) {
            repeat(segments) { index ->
                val next = (index + 1) % segments
                faces += Face3D(listOf(first + index, first + next, second + next, second + index), faceColor)
            }
        }

        val headTop = ring(headRadius, 0f)
        val headBottom = ring(headRadius, -headHeight)
        val shankStart = ring(shankRadius, -headHeight)
        bridge(headTop, headBottom, colorHex)
        bridge(headBottom, shankStart, colorHex)

        var previous = shankStart
        repeat(8) { step ->
            val fraction = (step + 1).toFloat() / 8f
            val radius = if (step % 2 == 0) shankRadius * 1.12f else shankRadius
            val current = ring(radius, -headHeight - shankLength * fraction)
            bridge(previous, current, colorHex)
            previous = current
        }
        faces += Face3D((0 until segments).map { previous + it }.reversed(), colorHex)

        // A dark six-lobe recessed-drive motif is overlaid on the screw head.
        val recessCenter = vertices.size
        vertices += Point3D(localOffset.x, localOffset.y + 0.001f, localOffset.z)
        repeat(6) { index ->
            val angle = 2.0 * PI * index / 6.0
            val radius = if (index % 2 == 0) headRadius * 0.48f else headRadius * 0.30f
            vertices += Point3D(
                localOffset.x + radius * cos(angle).toFloat(),
                localOffset.y + 0.002f,
                localOffset.z + radius * sin(angle).toFloat()
            )
        }
        repeat(6) { index ->
            val next = (index + 1) % 6
            faces += Face3D(listOf(recessCenter, recessCenter + 1 + index, recessCenter + 1 + next), "#334155")
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.SCREW,
            vertices = vertices,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails,
            metallicFactor = 0.78f,
            roughnessFactor = 0.34f
        )
    }

    /** Generates a flat or Belleville-style washer with top, bottom, inner, and outer faces. */
    fun createWasherSubAssembly(
        id: String,
        name: String,
        innerRadius: Float = 0.06f,
        outerRadius: Float = 0.16f,
        thickness: Float = 0.03f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 2.6f,
        colorHex: String = "#CBD5E1",
        specDetails: String = "M8 Belleville Lock Washer"
    ): SubAssemblyPart {
        val vertices = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()
        val segments = 16

        fun ring(radius: Float, y: Float): Int {
            val start = vertices.size
            repeat(segments) { index ->
                val angle = 2.0 * PI * index / segments
                vertices += Point3D(
                    localOffset.x + radius * cos(angle).toFloat(),
                    localOffset.y + y,
                    localOffset.z + radius * sin(angle).toFloat()
                )
            }
            return start
        }

        val innerTop = ring(innerRadius, thickness / 2f)
        val outerTop = ring(outerRadius, thickness / 2f)
        val innerBottom = ring(innerRadius, -thickness / 2f)
        val outerBottom = ring(outerRadius, -thickness / 2f)

        repeat(segments) { index ->
            val next = (index + 1) % segments
            faces += Face3D(listOf(innerTop + index, outerTop + index, outerTop + next, innerTop + next), colorHex)
            faces += Face3D(listOf(innerBottom + next, outerBottom + next, outerBottom + index, innerBottom + index), colorHex)
            faces += Face3D(listOf(outerTop + index, outerBottom + index, outerBottom + next, outerTop + next), colorHex)
            faces += Face3D(listOf(innerTop + next, innerBottom + next, innerBottom + index, innerTop + index), colorHex)
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.WASHER,
            vertices = vertices,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails,
            metallicFactor = 0.88f,
            roughnessFactor = 0.26f
        )
    }

    /** Generates a simple flange gasket plate used where a CAD reference is not yet imported. */
    fun createGasketSubAssembly(
        id: String,
        name: String,
        width: Float = 1.4f,
        depth: Float = 0.8f,
        thickness: Float = 0.04f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 1.8f,
        colorHex: String = "#38BDF8",
        specDetails: String = "Molded Silicone Perimeter Seal Gasket"
    ): SubAssemblyPart {
        val w = width / 2f
        val d = depth / 2f
        val t = thickness / 2f
        val vertices = listOf(
            Point3D(localOffset.x - w, localOffset.y - t, localOffset.z - d),
            Point3D(localOffset.x + w, localOffset.y - t, localOffset.z - d),
            Point3D(localOffset.x + w, localOffset.y + t, localOffset.z - d),
            Point3D(localOffset.x - w, localOffset.y + t, localOffset.z - d),
            Point3D(localOffset.x - w, localOffset.y - t, localOffset.z + d),
            Point3D(localOffset.x + w, localOffset.y - t, localOffset.z + d),
            Point3D(localOffset.x + w, localOffset.y + t, localOffset.z + d),
            Point3D(localOffset.x - w, localOffset.y + t, localOffset.z + d)
        )
        val faces = listOf(
            Face3D(listOf(0, 1, 2, 3), colorHex),
            Face3D(listOf(4, 5, 6, 7), colorHex),
            Face3D(listOf(0, 4, 7, 3), colorHex),
            Face3D(listOf(1, 5, 6, 2), colorHex),
            Face3D(listOf(3, 2, 6, 7), colorHex),
            Face3D(listOf(0, 1, 5, 4), colorHex)
        )
        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.GASKET,
            vertices = vertices,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails,
            metallicFactor = 0.05f,
            roughnessFactor = 0.62f
        )
    }

    /** Generates a multi-ribbed serpentine-belt loop around supplied pulley centers. */
    fun createSerpentineBeltSubAssembly(
        id: String,
        name: String,
        pulleyCenters: List<Point3D>,
        beltWidth: Float = 0.12f,
        thickness: Float = 0.04f,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 0f, 1f),
        explodeMultiplier: Float = 2.2f,
        colorHex: String = "#1E293B",
        specDetails: String = "6-Rib EPDM Rubber Serpentine Belt • 82.5 Inch"
    ): SubAssemblyPart {
        val vertices = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()
        if (pulleyCenters.size < 2) return SubAssemblyPart(id, name, SubAssemblyType.BELT, emptyList(), emptyList())

        val halfWidth = beltWidth / 2f
        pulleyCenters.forEach { center ->
            val x = localOffset.x + center.x
            val y = localOffset.y + center.y
            val z = localOffset.z + center.z
            vertices += Point3D(x - halfWidth, y, z + thickness)
            vertices += Point3D(x + halfWidth, y, z + thickness)
            vertices += Point3D(x - halfWidth, y, z)
            vertices += Point3D(x + halfWidth, y, z)
        }
        repeat(pulleyCenters.size) { index ->
            val next = (index + 1) % pulleyCenters.size
            val currentStart = index * 4
            val nextStart = next * 4
            faces += Face3D(listOf(currentStart, currentStart + 1, nextStart + 1, nextStart), colorHex)
            faces += Face3D(listOf(currentStart + 2, currentStart + 3, nextStart + 3, nextStart + 2), "#0F172A")
            faces += Face3D(listOf(currentStart, currentStart + 2, nextStart + 2, nextStart), colorHex)
        }
        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.BELT,
            vertices = vertices,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails,
            metallicFactor = 0.0f,
            roughnessFactor = 0.78f
        )
    }

    /** Generates a spark plug with a threaded shell, hex, ceramic insulator, and terminal. */
    fun createSparkPlugSubAssembly(
        id: String,
        name: String,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 3.0f,
        specDetails: String = "Motorcraft AGSF-22PP Platinum Plug • Gap 0.054 in • torque per Ford workshop manual"
    ): SubAssemblyPart {
        val shell = createThreadedHexBoltSubAssembly(
            id = id,
            name = name,
            headRadius = 0.08f,
            headHeight = 0.09f,
            shankRadius = 0.045f,
            shankLength = 0.22f,
            localOffset = localOffset,
            explodeDir = explodeDir,
            explodeMultiplier = explodeMultiplier,
            colorHex = "#94A3B8",
            specDetails = specDetails
        )
        val vertices = shell.vertices.toMutableList()
        val faces = shell.faces.toMutableList()
        val segments = 10
        val ceramicStart = vertices.size
        repeat(segments) { index ->
            val angle = 2.0 * PI * index / segments
            vertices += Point3D(
                localOffset.x + 0.052f * cos(angle).toFloat(),
                localOffset.y + 0.28f,
                localOffset.z + 0.052f * sin(angle).toFloat()
            )
        }
        repeat(segments) { index ->
            val next = (index + 1) % segments
            val shellIndex = index % 6
            val shellNext = next % 6
            faces += Face3D(listOf(ceramicStart + index, ceramicStart + next, shellNext, shellIndex), "#F8FAFC")
        }
        return shell.copy(
            vertices = vertices,
            faces = faces,
            type = SubAssemblyType.SPARK_PLUG,
            metallicFactor = 0.68f,
            roughnessFactor = 0.28f
        )
    }
}
