package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class VehicleConfiguration(
    val year: Int = 2004,
    val model: String = "Explorer Sport Trac",
    val engine: String = "4.0L SOHC V6",
    val drivetrain: String = "4WD",
    val vinEngineCode: String = "K",
    val fuel: String = "Flex Fuel"
)

data class SourceExcerpt(
    val sourceId: String,
    val title: String,
    val section: String,
    val pageLabel: String,
    val configuration: String,
    val excerpt: String,
    val sourceUrl: String?,
    val evidenceType: String,
    val fitWarning: String? = null
)

interface MentorSourceRetriever {
    suspend fun search(query: String, configuration: VehicleConfiguration, limit: Int = 5): List<SourceExcerpt>
}

/** First bounded retrieval layer. It uses the verified 4WD VIN-K workshop leaf index and refuses 2WD URLs. */
class CuratedMentorSourceRetriever : MentorSourceRetriever {
    override suspend fun search(query: String, configuration: VehicleConfiguration, limit: Int): List<SourceExcerpt> = withContext(Dispatchers.Default) {
        if (configuration.drivetrain != "4WD" || configuration.vinEngineCode != "K") return@withContext emptyList()
        val haystack = query.lowercase()
        val hits = CharmWorkshopIndex.leaves.filterNot { CharmWorkshopIndex.isRejectedTwoWheelDriveUrl(it.url) }.filter { leaf ->
            leaf.keywords.any { keyword -> haystack.contains(keyword) } ||
                (haystack.contains("timing") && leaf.title == "Engine") ||
                (haystack.contains("oil") && leaf.title == "Engine")
        }.distinctBy { it.url }.take(limit)
        hits.mapIndexed { index, leaf ->
            SourceExcerpt(
                sourceId = "charm-4wd-vin-k-${leaf.title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')}",
                title = leaf.title,
                section = "CHARM 4WD VIN K workshop tree",
                pageLabel = "Workshop leaf ${index + 1}; page-level text retrieval pending",
                configuration = CharmWorkshopIndex.VEHICLE_TREE,
                excerpt = "Use the linked workshop leaf for the exact procedure. This indexed result is a source pointer, not a quotation.",
                sourceUrl = leaf.url,
                evidenceType = if (leaf.title.contains("DTC", true)) "diagnosis" else "procedure",
                fitWarning = CharmWorkshopIndex.DISCLAIMER
            )
        }
    }
}

object MentorSourceContext {
    fun format(excerpts: List<SourceExcerpt>): String {
        if (excerpts.isEmpty()) return "SOURCE EVIDENCE: No configuration-safe indexed source matched. Do not invent a manual value; say that the exact page must be checked."
        return buildString {
            appendLine("SOURCE EVIDENCE (4WD VIN K pointers; not quotations):")
            excerpts.forEach { source ->
                appendLine("[${source.sourceId}] ${source.title} — ${source.pageLabel}")
                appendLine("Configuration: ${source.configuration}")
                appendLine("Pointer: ${source.sourceUrl ?: "not available"}")
                appendLine("Evidence: ${source.excerpt}")
                source.fitWarning?.let { appendLine("Warning: $it") }
            }
            appendLine("CITATION RULE: cite source IDs for factual claims; if exact page text or a specification is not retrieved, say so explicitly.")
        }.trim()
    }
}
