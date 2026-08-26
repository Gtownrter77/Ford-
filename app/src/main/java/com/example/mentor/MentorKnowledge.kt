package com.example.mentor

import com.example.data.CharmWorkshopIndex
import com.example.data.CommunityForumThreads
import com.example.data.CommunityRepairVideos
import com.example.data.OfficialOwnerPublications
import com.example.data.SportTracPartsCatalog
import com.example.data.SportTracServiceManualDiagnostics
import com.example.model.Component3DModel
import com.example.model.ServiceManualTroubleMatch

/**
 * Ground-truth Mentor briefing for the selected 3D part.
 *
 * Answers come from the packaged Sport Trac catalog and Ford-pattern
 * service-manual matches already in this repo. This is not a claim of
 * live OEM subscription data or a completed conversational LLM.
 */
data class MentorBriefing(
    val vehicleLine: String,
    val componentName: String,
    val oemPartNumber: String,
    val systemName: String,
    val location: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val knownFailures: List<ServiceManualTroubleMatch>,
    val torqueCallouts: List<String>,
    val tools: List<String>,
    val firstStepTitle: String?,
    val firstStepInstruction: String?,
    val relatedPartNumbers: List<String>,
    val uncertaintyNote: String,
    val communityVideos: List<String>,
    val workshopLeaves: List<String>
)

object MentorKnowledge {
    const val VEHICLE_LINE = "2004 Ford Explorer Sport Trac 4.0L SOHC V6"

    fun briefing(component: Component3DModel): MentorBriefing {
        val failures = SportTracServiceManualDiagnostics.manualTroubleMatches.filter { match ->
            match.targetComponentId == component.id ||
                component.name.contains(match.title.take(12), ignoreCase = true) ||
                match.title.contains(component.name.take(12), ignoreCase = true)
        }
        val relatedParts = SportTracPartsCatalog.getPartsForComponent(component.id)
            .map { "${it.partName} (${it.partNumber})" }
            .take(4)
        val videos = CommunityRepairVideos.matching(component.name, component)
        val workshop = CharmWorkshopIndex.matching(component.name, component)

        return MentorBriefing(
            vehicleLine = VEHICLE_LINE,
            componentName = component.name,
            oemPartNumber = component.oemPartNumber,
            systemName = component.system.displayName,
            location = component.locationDescription,
            difficulty = component.difficulty,
            estimatedMinutes = component.estimatedTimeMinutes,
            knownFailures = failures,
            torqueCallouts = component.torqueSpecs.map { spec ->
                "${spec.fastenerName}: ${spec.torqueFtLbs} ft-lb / ${spec.torqueNm} N\u00b7m${if (spec.notes.isNotBlank()) " \u2014 ${spec.notes}" else ""}"
            },
            tools = component.requiredTools,
            firstStepTitle = component.repairSteps.firstOrNull()?.title,
            firstStepInstruction = component.repairSteps.firstOrNull()?.instruction,
            relatedPartNumbers = relatedParts,
            uncertaintyNote = "Owner-guide fluids and Motorcraft numbers are from the 2004 P207 Sport Trac Owners Guide. Torque sequences and teardown steps still need the workshop manual before turning a wrench.",
            communityVideos = videos.map { "${it.title} ${it.url}" },
            workshopLeaves = workshop.map { "${it.title} ${it.url}" }
        )
    }

    const val OWNER_GUIDE_FLUIDS =
        "2004 OG: oil 5.0 qt 5W-30 + FL-820S; coolant 14.0 qt Premium Gold; 5R55E 4x4 MERCON V (Mercon 5) dry-fill about 10.3 qt set by dipstick; 4x2 about 10.0 qt. Transfer case official: 1.3 qt MERCON ATF XT-2-QDX, not MERCON V. Power steering official: MERCON ATF. Brake: DOT 3 to reservoir line. Washer: 2.7 qt. Common sense: XT-2 is discontinued, so shops use MERCON V in the t-case and PS when original Mercon cannot be sourced. Do not treat that as the printed OG spec. Ford printed do-not-mix MERCON and MERCON V."
    const val OWNER_GUIDE_PARTS =
        "2004 OG Motorcraft: air FA-1744, fuel FG-1036, oil FL-820S, battery BXT-65-650, PCV EV-243, plugs AGSF-22PP gap 0.052-0.056 in. Lug nuts 1/2-20 at 84-114 lb-ft."
    const val OWNER_GUIDE_AXLES =
        "2004 OG: front axle 1.8 qt 80W-90 on 4x4. Rear axle 5.5-5.8 pints 75W-90 FE synthetic, fill 6-14 mm below the hole. Add XL-7 4 oz on a complete Traction-Lok refill. Rear synthetic is lubricated for life unless leak, service, or water submersion. 75W-140 is not the printed OG fill."
    const val OWNER_GUIDE_ENGINE =
        "2004 OG engine data: 4.0L SOHC V6, 245 ci, compression 9.7:1, firing order 1-4-2-5-3-6, EDIS ignition, 87 octane or E85 max, AGSF-22PP gap 0.052-0.056 in. VECI decal overrides the table if it differs."
    const val OWNER_GUIDE_DIMENSIONS =
        "2004 OG 4-door: length 205.9 in, width 71.8 in, height 69.9 in (70.6 in max 4x4 16-in tires), wheelbase 125.9 in, track 58.5 / 58.3 in."

    fun spokenBriefing(component: Component3DModel): String {
        val brief = briefing(component)
        val failure = brief.knownFailures.firstOrNull()
        val torque = brief.torqueCallouts.firstOrNull() ?: "No packaged torque value for the opening step."
        val failureLine = if (failure != null) {
            "Known failure pattern: ${failure.title}. ${failure.probableCause}"
        } else {
            "No packaged TSB match is indexed to this exact component id."
        }
        val videoLine = if (brief.communityVideos.isNotEmpty()) {
            " Community how-to: ${brief.communityVideos.first()}. ${CommunityRepairVideos.DISCLAIMER}"
        } else {
            ""
        }
        val workshopLine = if (brief.workshopLeaves.isNotEmpty()) {
            " 4WD VIN K workshop: ${brief.workshopLeaves.first()}. ${CharmWorkshopIndex.DISCLAIMER}"
        } else {
            ""
        }
        return buildString {
            append("Mentor on the ${brief.vehicleLine}. ")
            append("Selected ${brief.componentName}, OEM ${brief.oemPartNumber}, ${brief.systemName}. ")
            append("It sits ${brief.location}. ")
            append("Rated ${brief.difficulty}, about ${brief.estimatedMinutes} minutes. ")
            append(failureLine)
            append(" Opening torque callout: $torque. ")
            brief.firstStepInstruction?.let { append("First practice step: $it ") }
            append(brief.uncertaintyNote)
            append(videoLine)
            append(workshopLine)
        }
    }

    fun answer(component: Component3DModel, question: String): String {
        val q = question.lowercase()
        val brief = briefing(component)
        val askedForVideo = q.contains("video") || q.contains("youtube") || q.contains("watch") || q.contains("tutorial")
        val askedForWorkshop = q.contains("workshop") || q.contains("charm") || q.contains("fsm") ||
            q.contains("service manual") || q.contains("repair manual")
        val askedForOfficial = q.contains("owner guide") || q.contains("owners guide") ||
            q.contains("official pdf") || q.contains("quick reference") || q.contains("warranty") ||
            q.contains("fordservicecontent") || q.contains("owner manual")
        val askedForForum = q.contains("forum") || q.contains("explorerforum") || q.contains("thread") ||
            q.contains("blend door") || q.contains("valve body")
        return when {
            askedForWorkshop -> {
                CharmWorkshopIndex.format(CharmWorkshopIndex.matching(question, component))
            }
            askedForOfficial -> {
                OfficialOwnerPublications.format(OfficialOwnerPublications.matching(question))
            }
            askedForForum -> {
                CommunityForumThreads.format(CommunityForumThreads.matching(question))
            }
            askedForVideo -> {
                val videos = CommunityRepairVideos.matching(question, component)
                if (videos.isEmpty()) {
                    CommunityRepairVideos.format(CommunityRepairVideos.matching(component.name, component))
                } else {
                    CommunityRepairVideos.format(videos)
                }
            }
            q.contains("torque") || q.contains("ft-lb") || q.contains("tighten") -> {
                if (brief.torqueCallouts.isEmpty()) {
                    "No packaged torque value for ${component.name}. Use the Ford workshop manual, not guesswork."
                } else {
                    "Torque for ${component.name}: " + brief.torqueCallouts.joinToString("; ")
                }
            }
            q.contains("tool") || q.contains("socket") || q.contains("wrench") -> {
                if (brief.tools.isEmpty()) {
                    "No packaged tool list for ${component.name}."
                } else {
                    "Tools for ${component.name}: " + brief.tools.joinToString(", ")
                }
            }
            q.contains("oem") || q.contains("part number") || q.contains("part #") -> {
                "OEM callout on this node is ${component.oemPartNumber}."
            }
            q.contains("symptom") || q.contains("fail") || q.contains("tsb") || q.contains("noise") || q.contains("leak") -> {
                val match = brief.knownFailures.firstOrNull()
                if (match == null) {
                    "No packaged TSB is indexed to ${component.id}. Describe the symptom and I will stay inside the Sport Trac catalog instead of inventing a diagnosis."
                } else {
                    "${match.title}. ${match.problemSummary} Cause on this truck: ${match.probableCause} Codes: ${match.obdCodes.joinToString().ifBlank { "none packaged" }}. Section ${match.serviceManualSection}."
                }
            }
            q.contains("axle") || q.contains("75w") || q.contains("xl-7") || q.contains("traction-lok") || q.contains("traction lok") -> {
                OWNER_GUIDE_AXLES
            }
            q.contains("displacement") || q.contains("firing") || q.contains("edis") || q.contains("compression") || q.contains("octane") -> {
                OWNER_GUIDE_ENGINE
            }
            q.contains("wheelbase") || q.contains("dimension") || q.contains("overall length") -> {
                OWNER_GUIDE_DIMENSIONS
            }
            q.contains("fluid") || q.contains("capacity") || q.contains("quart") || q.contains("coolant") || q.contains("mercon") -> {
                OWNER_GUIDE_FLUIDS
            }
            q.contains("filter") || q.contains("motorcraft") || q.contains("gap") || q.contains("lug") -> {
                OWNER_GUIDE_PARTS
            }
            q.contains("where") || q.contains("location") || q.contains("find") -> {
                "${component.name} is ${component.locationDescription}."
            }
            q.contains("step") || q.contains("how") || q.contains("replace") || q.contains("start") -> {
                val step = component.repairSteps.firstOrNull()
                val stepText = if (step == null) {
                    "No packaged repair steps for ${component.name}."
                } else {
                    "Step ${step.stepNumber}: ${step.title}. ${step.instruction}" +
                        (step.warning?.let { " Warning: $it" } ?: "")
                }
                val videos = CommunityRepairVideos.matching(question, component)
                if (videos.isEmpty()) {
                    stepText
                } else {
                    "$stepText Community how-to: ${CommunityRepairVideos.format(videos)}"
                }
            }
            else -> spokenBriefing(component)
        }
    }
}
