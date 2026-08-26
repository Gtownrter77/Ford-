package com.example.mentor

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
    val uncertaintyNote: String
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
                "${spec.fastenerName}: ${spec.torqueFtLbs} ft-lb / ${spec.torqueNm} N·m${if (spec.notes.isNotBlank()) " — ${spec.notes}" else ""}"
            },
            tools = component.requiredTools,
            firstStepTitle = component.repairSteps.firstOrNull()?.title,
            firstStepInstruction = component.repairSteps.firstOrNull()?.instruction,
            relatedPartNumbers = relatedParts,
            uncertaintyNote = "This briefing is packaged training data for this VIN family. Confirm torque, fastener, and procedure against the current Ford workshop manual before turning a wrench on a real truck."
        )
    }

    fun spokenBriefing(component: Component3DModel): String {
        val brief = briefing(component)
        val failure = brief.knownFailures.firstOrNull()
        val torque = brief.torqueCallouts.firstOrNull() ?: "No packaged torque value for the opening step."
        val failureLine = if (failure != null) {
            "Known failure pattern: ${failure.title}. ${failure.probableCause}"
        } else {
            "No packaged TSB match is indexed to this exact component id."
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
        }
    }

    fun answer(component: Component3DModel, question: String): String {
        val q = question.lowercase()
        val brief = briefing(component)
        return when {
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
            q.contains("where") || q.contains("location") || q.contains("find") -> {
                "${component.name} is ${component.locationDescription}."
            }
            q.contains("step") || q.contains("how") || q.contains("replace") || q.contains("start") -> {
                val step = component.repairSteps.firstOrNull()
                if (step == null) {
                    "No packaged repair steps for ${component.name}."
                } else {
                    "Step ${step.stepNumber}: ${step.title}. ${step.instruction}" +
                        (step.warning?.let { " Warning: $it" } ?: "")
                }
            }
            else -> spokenBriefing(component)
        }
    }
}
