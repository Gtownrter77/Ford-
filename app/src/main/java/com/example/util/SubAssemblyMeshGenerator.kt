package com.example.util

import com.example.model.Face3D
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType
import kotlin.math.cos
import kotlin.math.sin

object SubAssemblyMeshGenerator {

    /**
     * Generates a high-detail OEM Hex Head Bolt sub-assembly part mesh.
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
    ): SubAssemblyPart {
        val verts = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()

        // 6-sided Hex Head Top & Bottom
        val headSegments = 6
        for (i in 0 until headSegments) {
            val angle = 2.0 * Math.PI * i / headSegments
            val x = headRadius * cos(angle).toFloat()
            val z = headRadius * sin(angle).toFloat()
            verts.add(Point3D(localOffset.x + x, localOffset.y + headHeight, localOffset.z + z)) // Top ring 0..5
            verts.add(Point3D(localOffset.x + x, localOffset.y, localOffset.z + z))              // Bottom ring 6..11
        }

        // Shank Bottom Ring
        val shankSegments = 8
        val shankStartIdx = verts.size
        for (i in 0 until shankSegments) {
            val angle = 2.0 * Math.PI * i / shankSegments
            val x = shankRadius * cos(angle).toFloat()
            val z = shankRadius * sin(angle).toFloat()
            verts.add(Point3D(localOffset.x + x, localOffset.y - shankLength, localOffset.z + z))
        }

        // Hex Head Side Faces
        for (i in 0 until headSegments) {
            val nextI = (i + 1) % headSegments
            val t1 = i * 2
            val b1 = i * 2 + 1
            val t2 = nextI * 2
            val b2 = nextI * 2 + 1
            faces.add(Face3D(listOf(t1, b1, b2, t2), colorHex))
        }

        // Hex Top Cap Face
        val topCapIndices = (0 until headSegments * 2 step 2).toList()
        faces.add(Face3D(topCapIndices, colorHex))

        // Shank Side Faces
        for (i in 0 until shankSegments) {
            val nextI = (i + 1) % shankSegments
            val b1 = shankStartIdx + i
            val b2 = shankStartIdx + nextI
            // Connect to center bottom of hex head
            faces.add(Face3D(listOf(b1, b2, 1, 3), colorHex))
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.BOLT,
            vertices = verts,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails
        )
    }

    /**
     * Generates a Belleville / Lock Washer ring sub-assembly part mesh.
     */
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
        val verts = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()

        val segments = 12
        // Inner & Outer top ring
        for (i in 0 until segments) {
            val angle = 2.0 * Math.PI * i / segments
            val cosA = cos(angle).toFloat()
            val sinA = sin(angle).toFloat()

            // Inner top 0..11
            verts.add(Point3D(localOffset.x + innerRadius * cosA, localOffset.y + thickness, localOffset.z + innerRadius * sinA))
            // Outer top 12..23
            verts.add(Point3D(localOffset.x + outerRadius * cosA, localOffset.y + thickness, localOffset.z + outerRadius * sinA))
            // Outer bottom 24..35
            verts.add(Point3D(localOffset.x + outerRadius * cosA, localOffset.y, localOffset.z + outerRadius * sinA))
        }

        // Top Ring Quad Faces
        for (i in 0 until segments) {
            val nextI = (i + 1) % segments
            val in1 = i * 3
            val out1 = i * 3 + 1
            val in2 = nextI * 3
            val out2 = nextI * 3 + 1
            val bot1 = i * 3 + 2
            val bot2 = nextI * 3 + 2

            // Top ring
            faces.add(Face3D(listOf(in1, out1, out2, in2), colorHex))
            // Outer rim
            faces.add(Face3D(listOf(out1, bot1, bot2, out2), colorHex))
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.WASHER,
            vertices = verts,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails
        )
    }

    /**
     * Generates a Flange Silicone / Rubber Gasket plate mesh with central cutouts.
     */
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

        val verts = listOf(
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
            vertices = verts,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails
        )
    }

    /**
     * Generates a Multi-Ribbed Serpentine Belt loop sub-assembly mesh wrapping around engine pulleys.
     */
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
        val verts = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()

        if (pulleyCenters.size < 2) return SubAssemblyPart(id, name, SubAssemblyType.BELT, emptyList(), emptyList())

        val wHalf = beltWidth / 2f
        pulleyCenters.forEachIndexed { i, p ->
            val vx = localOffset.x + p.x
            val vy = localOffset.y + p.y
            val vz = localOffset.z + p.z

            // Front edge
            verts.add(Point3D(vx - wHalf, vy, vz + thickness))
            // Back edge
            verts.add(Point3D(vx + wHalf, vy, vz + thickness))
            // Inner front edge
            verts.add(Point3D(vx - wHalf, vy, vz))
            // Inner back edge
            verts.add(Point3D(vx + wHalf, vy, vz))
        }

        val count = pulleyCenters.size
        for (i in 0 until count) {
            val nextI = (i + 1) % count
            val f1 = i * 4
            val f2 = nextI * 4

            // Top surface
            faces.add(Face3D(listOf(f1, f1 + 1, f2 + 1, f2), colorHex))
            // Inner surface
            faces.add(Face3D(listOf(f1 + 2, f1 + 3, f2 + 3, f2 + 2), "#0F172A"))
            // Outer side
            faces.add(Face3D(listOf(f1, f1 + 2, f2 + 2, f2), colorHex))
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.BELT,
            vertices = verts,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails
        )
    }

    /**
     * Generates a Spark Plug sub-assembly mesh with ceramic insulator & electrode.
     */
    fun createSparkPlugSubAssembly(
        id: String,
        name: String,
        localOffset: Point3D = Point3D(0f, 0f, 0f),
        explodeDir: Point3D = Point3D(0f, 1f, 0f),
        explodeMultiplier: Float = 3.0f,
        specDetails: String = "Motorcraft AGSF-22PP Platinum Plug • Gap 0.054in • Torque 15 lb-ft"
    ): SubAssemblyPart {
        val verts = mutableListOf<Point3D>()
        val faces = mutableListOf<Face3D>()

        // 1. Threaded Steel Hex Body
        val segments = 6
        for (i in 0 until segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = 0.08f * cos(angle).toFloat()
            val z = 0.08f * sin(angle).toFloat()
            verts.add(Point3D(localOffset.x + x, localOffset.y + 0.10f, localOffset.z + z)) // Top ring 0..5
            verts.add(Point3D(localOffset.x + x, localOffset.y - 0.15f, localOffset.z + z)) // Bottom ring 6..11
        }

        // 2. White Ceramic Insulator Top
        val cSegments = 8
        val ceramicStartIdx = verts.size
        for (i in 0 until cSegments) {
            val angle = 2.0 * Math.PI * i / cSegments
            val x = 0.05f * cos(angle).toFloat()
            val z = 0.05f * sin(angle).toFloat()
            verts.add(Point3D(localOffset.x + x, localOffset.y + 0.35f, localOffset.z + z))
        }

        // Steel Body Faces
        for (i in 0 until segments) {
            val nextI = (i + 1) % segments
            val t1 = i * 2
            val b1 = i * 2 + 1
            val t2 = nextI * 2
            val b2 = nextI * 2 + 1
            faces.add(Face3D(listOf(t1, b1, b2, t2), "#94A3B8")) // Steel silver
        }

        // Ceramic Faces
        for (i in 0 until cSegments) {
            val nextI = (i + 1) % cSegments
            val t1 = ceramicStartIdx + i
            val t2 = ceramicStartIdx + nextI
            faces.add(Face3D(listOf(t1, t2, 0, 2), "#F8FAFC")) // White porcelain
        }

        return SubAssemblyPart(
            id = id,
            name = name,
            type = SubAssemblyType.SPARK_PLUG,
            vertices = verts,
            faces = faces,
            localOffset = localOffset,
            explodeDirection = explodeDir,
            explodeDistanceMultiplier = explodeMultiplier,
            specDetails = specDetails
        )
    }
}
