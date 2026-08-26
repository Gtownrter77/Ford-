package com.example.mentor

import com.example.data.CommunityRepairVideos
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
    val communityVideos: List<String>
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
            communityVideos = videos.map { "${it.title} ${it.url}" }
        )
    }

    const val OWNER_GUIDE_FLUIDS = "2004 OG: oil 5.0 qt 5W-30 + FL-820S; coolant 14.0 qt Premium Gold; 5R55E 4x4 MERCON V dry-fill about 10.3 qt set by dipstick; transfer case 1.3 qt MERCON ATF not MERCON V; PS MERCON ATF; rear axle 75W-90 FE synthetic 5.5-5.8 pints plus XL-7 4 oz if Traction-Lok."
    const val OWNER_GUIDE_PARTS = "2004 OG Motorcraft: air FA-1744, fuel FG-1036, oil FL-820S, battery BXT-65-650, PCV EV-243, plugs AGSF-22PP gap 0.052-0.056 in. Lug nuts 1/2-20 at 84-114 lb-ft."

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
        }
    }

    fun answer(component: Component3DModel, question: String): String {
        val q = question.lowercase()
        val brief = briefing(component)
        val askedForVideo = q.contains("video") || q.contains("youtube") || q.contains("watch") || q.contains("tutorial")
        return when {
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
