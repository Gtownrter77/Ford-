package com.example.model

data class DiagnosticOption(
    val optionText: String,
    val nextQuestionId: String? = null,
    val resultDiagnosis: DiagnosticResult? = null
)

data class DiagnosticQuestion(
    val id: String,
    val questionText: String,
    val options: List<DiagnosticOption>
)

data class DiagnosticResult(
    val title: String,
    val problemSummary: String,
    val probableCause: String,
    val obdCode: String? = null,
    val targetComponentId: String,
    val confidencePercentage: Int,
    val urgencyLevel: String, // "Critical", "High", "Medium", "Low"
    val recommendedAction: String
)

data class DiagnosticSymptomCategory(
    val id: String,
    val categoryName: String,
    val system: VehicleSystem,
    val commonSymptoms: List<String>,
    val rootQuestion: DiagnosticQuestion
)
