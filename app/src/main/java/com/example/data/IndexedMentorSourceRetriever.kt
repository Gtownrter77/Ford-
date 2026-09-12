package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

data class IndexedSourcePage(
    val sourceId: String,
    val title: String,
    val section: String,
    val pageLabel: String,
    val configuration: String,
    val excerpt: String,
    val sourceUrl: String?,
    val evidenceType: String,
    val eligibility: String,
    val checksum: String
)

object MentorIndexHolder {
    @Volatile
    var pages: List<IndexedSourcePage> = emptyList()
        private set

    @Synchronized
    fun load(pages: List<IndexedSourcePage>) {
        this.pages = pages
    }

    @Synchronized
    fun loadFromGzipJsonl(stream: InputStream) {
        pages = parseIndexedJsonl(GZIPInputStream(stream))
    }

    fun ensureAndroidAssets(context: Context) {
        if (pages.isNotEmpty()) return
        context.assets.open("mentor/source_index.jsonl.gz").use { loadFromGzipJsonl(it) }
    }
}

fun parseIndexedJsonl(stream: InputStream): List<IndexedSourcePage> {
    val out = ArrayList<IndexedSourcePage>(1024)
    BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
        reader.lineSequence().forEach { line ->
            if (line.isBlank()) return@forEach
            val obj = JSONObject(line)
            out += IndexedSourcePage(
                sourceId = obj.optString("sourceId"),
                title = obj.optString("title"),
                section = obj.optString("section"),
                pageLabel = obj.optString("pageLabel"),
                configuration = obj.optString("configuration"),
                excerpt = obj.optString("excerpt"),
                sourceUrl = obj.optString("sourceUrl").ifBlank { null },
                evidenceType = obj.optString("evidenceType"),
                eligibility = obj.optString("eligibility"),
                checksum = obj.optString("checksum")
            )
        }
    }
    return out
}

private val STOP = setOf("the", "and", "for", "with", "from", "that", "this", "are", "was")

class IndexedMentorSourceRetriever(
    private val pages: () -> List<IndexedSourcePage> = { MentorIndexHolder.pages }
) : MentorSourceRetriever {
    override suspend fun search(
        query: String,
        configuration: VehicleConfiguration,
        limit: Int
    ): List<SourceExcerpt> = withContext(Dispatchers.Default) {
        if (configuration.drivetrain != "4WD" || configuration.vinEngineCode != "K") {
            return@withContext emptyList()
        }
        val tokens = query.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.length >= 3 && it !in STOP }
        if (tokens.isEmpty()) return@withContext emptyList()
        pages()
            .asSequence()
            .filter { it.eligibility != "blocked" }
            .filter { !it.title.contains("2WD", ignoreCase = true) }
            .filter { it.sourceUrl.orEmpty().contains("2WD", ignoreCase = true).not() }
            .mapNotNull { page ->
                val hay = (page.title + " " + page.excerpt).lowercase()
                var score = 0
                tokens.forEach { token ->
                    if (page.title.lowercase().contains(token)) score += 4
                    else if (hay.contains(token)) score += 1
                }
                if (score == 0) null else score to page
            }
            .sortedByDescending { it.first }
            .take(limit)
            .map { (_, page) ->
                SourceExcerpt(
                    sourceId = page.sourceId,
                    title = page.title,
                    section = page.section,
                    pageLabel = "page ${page.pageLabel}",
                    configuration = page.configuration,
                    excerpt = page.excerpt,
                    sourceUrl = page.sourceUrl,
                    evidenceType = page.evidenceType,
                    fitWarning = if ("2WD" in page.configuration) "2WD page — do not use for 4WD VIN K procedures" else null
                )
            }
            .toList()
    }
}

class CompositeMentorSourceRetriever(
    private val primary: MentorSourceRetriever,
    private val fallback: MentorSourceRetriever
) : MentorSourceRetriever {
    override suspend fun search(
        query: String,
        configuration: VehicleConfiguration,
        limit: Int
    ): List<SourceExcerpt> {
        val hits = primary.search(query, configuration, limit)
        if (hits.isNotEmpty()) return hits
        return fallback.search(query, configuration, limit)
    }
}
