package com.example.data

/**
 * Ford-hosted 2004 Explorer Sport Trac owner publications.
 * These are owner documents, not the workshop manual.
 */
data class OfficialOwnerPublication(
    val title: String,
    val url: String,
    val keywords: List<String>
)

object OfficialOwnerPublications {
    const val HUB = "https://www.ford.com/support/owner-manuals-details/explorer-sport-trac/2004"
    const val DISCLAIMER =
        "Official Ford owner publication. Not the workshop manual. Fluids and Motorcraft numbers come from the Owner Guide; teardown steps do not."

    val catalog: List<OfficialOwnerPublication> = listOf(
        OfficialOwnerPublication(
            "2004 Sport Trac owner-manuals hub",
            HUB,
            listOf("hub", "owner manual", "owners guide", "official")
        ),
        OfficialOwnerPublication(
            "Owner Guide printing 1 (04p27og1e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og1e.pdf",
            listOf("owner guide", "owners guide", "og", "pdf", "fluid", "capacity", "motorcraft")
        ),
        OfficialOwnerPublication(
            "Owner Guide printing 2 (04p27og2e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og2e.pdf",
            listOf("owner guide", "owners guide", "og", "pdf")
        ),
        OfficialOwnerPublication(
            "Owner Guide printing 3 (04p27og3e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og3e.pdf",
            listOf("owner guide", "owners guide", "og", "pdf")
        ),
        OfficialOwnerPublication(
            "Quick Reference Guide printing 1 (04p27qg1e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27qg1e.pdf",
            listOf("quick reference", "qrg", "glovebox")
        ),
        OfficialOwnerPublication(
            "Quick Reference Guide printing 2 (04p27qg2e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27qg2e.pdf",
            listOf("quick reference", "qrg")
        ),
        OfficialOwnerPublication(
            "Warranty supplement printing 1 (04p27og1s)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og1s.pdf",
            listOf("warranty")
        ),
        OfficialOwnerPublication(
            "Warranty supplement printing 2 (04p27og2s)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og2s.pdf",
            listOf("warranty")
        ),
        OfficialOwnerPublication(
            "Warranty supplement printing 3 (04p27og3s)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og3s.pdf",
            listOf("warranty")
        ),
        OfficialOwnerPublication(
            "2004 scheduled maintenance guide (04nmgmg5e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04nmgmg5e.pdf",
            listOf("maintenance schedule", "scheduled maintenance", "interval")
        ),
        OfficialOwnerPublication(
            "2004 scheduled maintenance guide alt (04frdmg2e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04frdmg2e.pdf",
            listOf("maintenance schedule", "scheduled maintenance")
        ),
        OfficialOwnerPublication(
            "Roadside assistance card printing 1",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04frdra1e.pdf",
            listOf("roadside", "tow", "flat")
        ),
        OfficialOwnerPublication(
            "Roadside assistance card printing 2",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04frdra2e.pdf",
            listOf("roadside")
        ),
        OfficialOwnerPublication(
            "Driving Your SUV or Truck printing 1 (044x4fw1e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/044x4fw1e.pdf",
            listOf("4wd", "4x4", "suv", "truck driving", "controltrac")
        ),
        OfficialOwnerPublication(
            "Driving Your SUV or Truck printing 2 (044x4fw2e)",
            "https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/044x4fw2e.pdf",
            listOf("4wd", "4x4", "suv")
        )
    )

    fun matching(query: String): List<OfficialOwnerPublication> {
        val haystack = query.lowercase()
        val hits = catalog.filter { pub ->
            pub.keywords.any { keyword -> haystack.contains(keyword) }
        }.distinctBy { it.url }
        return hits.ifEmpty { listOf(catalog.first()) }
    }

    fun format(pubs: List<OfficialOwnerPublication>, limit: Int = 3): String {
        val shown = pubs.take(limit)
        if (shown.isEmpty()) {
            return "No official owner publication matched. Hub: $HUB. $DISCLAIMER"
        }
        return shown.joinToString(" ") { pub ->
            "${pub.title}: ${pub.url}."
        } + " $DISCLAIMER"
    }
}
