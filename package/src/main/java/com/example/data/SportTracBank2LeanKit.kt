package com.example.data

import com.example.model.Component3DModel
import com.example.model.FastenerCategory
import com.example.model.FastenerInventoryItem
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.TorqueSpec
import com.example.util.SubAssemblyMeshGenerator

/**
 * Deeper kit for Bank 2 lean + misfire on the 4.0L SOHC.
 * Bank 1 = passenger / +X (cyl 1-2-3). Bank 2 = driver / -X (cyl 4-5-6).
 * Torque strings are workshop-table values. Confirm CHARM / WSM for this VIN.
 */
object SportTracBank2LeanKit {

    const val SOURCE_INTAKE =
        "2004 Sport Trac 4WD VIN K workshop specs + CHARM 2005 VIN K intake table: intake manifold bolts 10 Nm (89 in-lb)."
    const val SOURCE_TB =
        "CHARM 2005 VIN K: throttle body bolts 9 Nm (80 in-lb)."
    const val SOURCE_COIL =
        "Mitchell 2004 4.0L SOHC Explorer engine-performance table: ignition coil bolts 6 Nm (53 in-lb); coil bracket 10 Nm (89 in-lb)."
    const val SOURCE_PLUGS =
        "Mitchell 2004 4.0L SOHC Explorer engine torque table: spark plug 20 Nm (15 lb-ft). Gap from 2004 OG."
    const val SOURCE_RAIL =
        "Mitchell 2004 4.0L SOHC engine-performance table: fuel injection supply manifold bolts 23 Nm (17 lb-ft)."

    data class SpecRow(
        val id: String,
        val name: String,
        val qty: Int,
        val torque: String,
        val tool: String,
        val source: String,
        val bank2: Boolean,
        val category: FastenerCategory = FastenerCategory.BOLT
    )

    val rows: List<SpecRow> = listOf(
        SpecRow("intake_b2", "Intake manifold bolt Bank 2 (driver)", 4, "89 in-lb / 10 Nm", "8 mm + in-lb wrench", SOURCE_INTAKE, true),
        SpecRow("intake_b1", "Intake manifold bolt Bank 1 (passenger)", 4, "89 in-lb / 10 Nm", "8 mm + in-lb wrench", SOURCE_INTAKE, false),
        SpecRow("tb", "Throttle body bolt", 4, "80 in-lb / 9 Nm", "8 mm + in-lb wrench", SOURCE_TB, false),
        SpecRow("coil", "Ignition coil pack bolt", 4, "53 in-lb / 6 Nm", "8 mm + in-lb wrench", SOURCE_COIL, false),
        SpecRow("coil_brkt", "Ignition coil bracket bolt", 2, "89 in-lb / 10 Nm", "10 mm + in-lb wrench", SOURCE_COIL, false),
        SpecRow("plug_b2", "Spark plug Bank 2", 3, "15 lb-ft / 20 Nm", "5/8 spark-plug socket", SOURCE_PLUGS, true),
        SpecRow("plug_b1", "Spark plug Bank 1", 3, "15 lb-ft / 20 Nm", "5/8 spark-plug socket", SOURCE_PLUGS, false),
        SpecRow("rail", "Fuel rail / supply manifold bolt", 4, "17 lb-ft / 23 Nm", "10 mm", SOURCE_RAIL, false),
        SpecRow("pcv", "PCV elbow clamp / hose at rear of intake", 1, "Snug. Do not crush the elbow.", "Hose-clamp pliers", "Community / prior Mentor path: dry-rot PCV elbow is a Bank 2 lean source.", true, FastenerCategory.WASHER_SEAL)
    )

    val torqueSpecs: List<TorqueSpec> = rows.map {
        TorqueSpec(it.name, it.torque.substringBefore("/").trim(), it.torque.substringAfter("/").trim(), it.source)
    }

    fun inventory(): List<FastenerInventoryItem> = rows.map {
        FastenerInventoryItem(
            name = it.name,
            category = it.category,
            quantity = it.qty,
            specOrThread = it.torque,
            toolRequired = it.tool,
            notes = "${if (it.bank2) "Bank 2 lean path. " else ""}${it.source} Confirm VIN workshop manual."
        )
    }

    fun meshes(): List<SubAssemblyPart> {
        val s = SportTracVehicleScale
        val engineZ = s.frontAxleZ + 0.12f
        val parts = mutableListOf<SubAssemblyPart>()
        fun bolt(id: String, name: String, pos: Point3D, spec: String, explode: Point3D) {
            parts += SubAssemblyMeshGenerator.createThreadedHexBoltSubAssembly(
                id = id, name = name,
                headRadius = 0.012f, headHeight = 0.008f, shankRadius = 0.005f, shankLength = 0.028f,
                localOffset = pos, explodeDir = explode, specDetails = spec
            )
            parts += SubAssemblyMeshGenerator.createWasherSubAssembly(
                id = "${id}_w", name = "$name washer",
                innerRadius = 0.0055f, outerRadius = 0.013f, thickness = 0.002f,
                localOffset = Point3D(pos.x, pos.y - 0.010f, pos.z), explodeDir = explode, specDetails = spec
            )
        }
        repeat(4) { i ->
            bolt("b2_intake_${i + 1}", "Bank 2 intake bolt ${i + 1}",
                Point3D(-0.18f, 1.02f, engineZ + 0.22f - i * 0.12f),
                "89 in-lb / 10 Nm. $SOURCE_INTAKE", Point3D(-0.25f, 0.15f, 0f))
        }
        repeat(4) { i ->
            bolt("b1_intake_${i + 1}", "Bank 1 intake bolt ${i + 1}",
                Point3D(0.18f, 1.02f, engineZ + 0.22f - i * 0.12f),
                "89 in-lb / 10 Nm. $SOURCE_INTAKE", Point3D(0.25f, 0.15f, 0f))
        }
        repeat(3) { i ->
            parts += SubAssemblyMeshGenerator.createSparkPlugSubAssembly(
                id = "plug_b2_${i + 1}", name = "Bank 2 plug cyl ${i + 4}",
                localOffset = Point3D(-0.24f, 0.92f, engineZ + 0.18f - i * 0.16f),
                explodeDir = Point3D(-0.2f, 0.35f, 0f),
                specDetails = "AGSF-22PP. 15 lb-ft / 20 Nm. $SOURCE_PLUGS")
        }
        repeat(3) { i ->
            parts += SubAssemblyMeshGenerator.createSparkPlugSubAssembly(
                id = "plug_b1_${i + 1}", name = "Bank 1 plug cyl ${i + 1}",
                localOffset = Point3D(0.24f, 0.92f, engineZ + 0.18f - i * 0.16f),
                explodeDir = Point3D(0.2f, 0.35f, 0f),
                specDetails = "AGSF-22PP. 15 lb-ft / 20 Nm. $SOURCE_PLUGS")
        }
        repeat(4) { i ->
            bolt("coil_${i + 1}", "Coil pack bolt ${i + 1}",
                Point3D(-0.04f + i * 0.03f, 1.12f, engineZ + 0.08f),
                "53 in-lb / 6 Nm. $SOURCE_COIL", Point3D(0f, 0.35f, 0f))
        }
        parts += SubAssemblyMeshGenerator.createGasketSubAssembly(
            id = "pcv_elbow", name = "PCV elbow at rear intake (Bank 2 lean suspect)",
            width = 0.06f, depth = 0.08f, thickness = 0.03f,
            localOffset = Point3D(-0.08f, 1.05f, engineZ - 0.28f),
            explodeDir = Point3D(0f, 0.2f, -0.25f),
            specDetails = "Inspect for collapse / dry rot. Common Bank 2 unmetered-air path."
        )
        return parts
    }

    fun attachToEngine(component: Component3DModel): Component3DModel {
        if (component.id != "scaled_engine_40l" && component.id != "intake_manifold" && component.id != "spark_plugs_coils") {
            return component
        }
        return component.copy(
            fasteners = component.fasteners + inventory(),
            subAssemblies = component.subAssemblies + meshes(),
            torqueSpecs = component.torqueSpecs + torqueSpecs,
            commonSymptoms = (component.commonSymptoms + listOf(
                "Bank 2 long-term fuel trim lean", "P0174", "P0300 / Bank 2 misfire", "PCV elbow dry rot"
            )).distinct()
        )
    }
}
