package com.example.model

/**
 * Evidence-aware status for a product domain. This is a reporting contract,
 * not a claim that a feature is complete or device-verified.
 */
enum class FeatureReadiness {
    SOURCE_PRESENT,
    LOCALLY_FUNCTIONAL,
    SANDBOX_VERIFIED,
    DEVICE_PENDING,
    PARTIAL,
    CONCEPTUAL
}

data class FeatureFoundationStatus(
    val domainId: String,
    val displayName: String,
    val readiness: FeatureReadiness,
    val evidenceNote: String,
    val nextFoundationStep: String
)

object FoundationTrackCatalog {
    val domains: List<FeatureFoundationStatus> = listOf(
        FeatureFoundationStatus(
            domainId = "procedural_3d",
            displayName = "Procedural 3D model",
            readiness = FeatureReadiness.DEVICE_PENDING,
            evidenceNote = "Compose Canvas renderer and generated reference hardware exist in source; physical runtime is unverified.",
            nextFoundationStep = "Prioritize A/C assemblies and expand coverage with evidence-backed geometry."
        ),
        FeatureFoundationStatus(
            domainId = "ac_workbench",
            displayName = "A/C Workbench",
            readiness = FeatureReadiness.LOCALLY_FUNCTIONAL,
            evidenceNote = "Local diagnostic paths, practice steps, safety boundaries, component links, and rehearsal gate exist in source.",
            nextFoundationStep = "Run the physical-device protocol and review A/C training content."
        ),
        FeatureFoundationStatus(
            domainId = "mentor",
            displayName = "Mentor",
            readiness = FeatureReadiness.PARTIAL,
            evidenceNote = "Guided repair mode, checklist persistence, TTS, voice commands, torque, and repair-step context exist in source.",
            nextFoundationStep = "Define context, question, answer, uncertainty, and parts-guidance contracts."
        ),
        FeatureFoundationStatus(
            domainId = "audio_diagnosis",
            displayName = "Audio diagnosis",
            readiness = FeatureReadiness.SOURCE_PRESENT,
            evidenceNote = "Audio-analysis and acoustic-reference source paths exist; microphone behavior and classification accuracy are unverified.",
            nextFoundationStep = "Define input, session, result, confidence, and uncertainty states."
        ),
        FeatureFoundationStatus(
            domainId = "part_store",
            displayName = "Part Store",
            readiness = FeatureReadiness.PARTIAL,
            evidenceNote = "Local catalog, ranking, readiness, and privacy-boundary UI exist; live scraping and ordering are not verified.",
            nextFoundationStep = "Define provider, listing provenance, price freshness, and no-order boundaries."
        ),
        FeatureFoundationStatus(
            domainId = "body_shop",
            displayName = "Body Shop",
            readiness = FeatureReadiness.CONCEPTUAL,
            evidenceNote = "Some customization and scene abstractions exist; a complete asset-generation/import pipeline is not established.",
            nextFoundationStep = "Define asset-generation, approval, import, and rollback contracts."
        )
    )
}
