package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Minimal repair-part record used by inspection UI and raycast lookups. */
data class PartData(
    val id: String,
    val name: String,
    val system: String,
    val description: String
)

/**
 * Standalone feature addition: asset-backed TSV/JSON part lookup.
 *
 * This loader is intentionally synchronous and explicit. It has no dependency on Compose,
 * Room, the ExplorerViewModel, SceneView, or application startup. Call [fromAssets] only from
 * a feature that actually needs part metadata.
 *
 * Supported TSV header: `id\tname\tsystem\tdescription`
 * Supported JSON: either `[ {...} ]` or `{ "parts": [ {...} ] }`. The repair realm's
 * `parts_data.json` is accepted through its `id`, `partName`, and `mentorScript` fields.
 */
class PartDataLoader private constructor(
    val partsById: Map<String, PartData>
) {
    fun getPartData(partId: String): PartData? = partsById[partId.trim().lowercase()]

    companion object {
        const val DEFAULT_TSV_ASSET = "parts.tsv"
        const val DEFAULT_JSON_ASSET = "parts_data.json"

        /** Reads a TSV file or JSON equivalent from `app/src/main/assets/`. */
        fun fromAssets(context: Context, assetName: String = DEFAULT_TSV_ASSET): PartDataLoader {
            val text = context.assets.open(assetName).bufferedReader().use { it.readText() }
            return if (assetName.endsWith(".json", ignoreCase = true)) fromJson(text) else fromTsv(text)
        }

        fun fromTsv(tsv: String): PartDataLoader = PartDataLoader(
            tsv.lineSequence()
                .map(String::trim)
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .dropWhile { line -> line.lowercase().startsWith("id\t") }
                .mapNotNull(::parseTsvRow)
                .associateBy { it.id.lowercase() }
        )

        fun fromJson(json: String): PartDataLoader {
            val trimmed = json.trim()
            val items = if (trimmed.startsWith("[")) {
                JSONArray(trimmed)
            } else {
                JSONObject(trimmed).optJSONArray("parts") ?: JSONArray()
            }
            return PartDataLoader(
                List(items.length()) { index -> items.optJSONObject(index)?.toPartData() }
                    .filterNotNull()
                    .associateBy { it.id.lowercase() }
            )
        }

        /** Five local entries for unit-like UI previews and loader smoke checks. */
        val sampleParts: List<PartData> = listOf(
            PartData("front_bumper", "Front Bumper Cover Assembly", "Body", "Front impact inspection and bumper-cover replacement zone."),
            PartData("front_left_wheel", "Front Left Wheel and Brake Zone", "Chassis", "Wheel, tire, brake hose, steering, and suspension inspection zone."),
            PartData("driver_front_door", "Driver Front Door Assembly", "Body", "Door shell, hinge, latch, glass, and wiring inspection zone."),
            PartData("engine_assembly", "4.0L SOHC V6 Engine Inspection Zone", "Engine", "Engine mounts, accessories, harness routing, leaks, and impact inspection zone."),
            PartData("rear_bumper", "Rear Bumper and Step Pad Assembly", "Body", "Rear bumper brackets, harnesses, hitch package, and tailgate-clearance inspection zone.")
        )

        fun sample(): PartDataLoader = PartDataLoader(sampleParts.associateBy { it.id.lowercase() })

        private fun parseTsvRow(line: String): PartData? {
            val columns = line.split('\t', limit = 4).map(String::trim)
            if (columns.size < 4 || columns[0].isBlank()) return null
            return PartData(
                id = columns[0],
                name = columns[1],
                system = columns[2],
                description = columns[3]
            )
        }

        private fun JSONObject.toPartData(): PartData? {
            val id = optString("id").trim()
            if (id.isEmpty()) return null
            val name = optString("name").ifBlank { optString("partName") }.ifBlank { id }
            val system = optString("system").ifBlank { inferSystem(id) }
            val description = optString("description")
                .ifBlank { optString("mentorScript") }
                .ifBlank { "$name inspection zone." }
            return PartData(id, name, system, description)
        }

        private fun inferSystem(partId: String): String = when {
            "engine" in partId -> "Engine"
            "wheel" in partId || "brake" in partId || "suspension" in partId -> "Chassis"
            else -> "Body"
        }
    }
}
