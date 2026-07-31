package com.example.model

import androidx.compose.ui.graphics.Color

enum class FordModule(val codeName: String, val fullName: String, val color: Color) {
    PCM("PCM", "Powertrain Control Module", Color(0xFFEF4444)),
    GEM("GEM/CTM", "Generic Electronic / Central Timer Module", Color(0xFF38BDF8)),
    ABS("ABS", "Anti-Lock Brake System", Color(0xFFFFD700)),
    IC("IC", "Instrument Cluster", Color(0xFF10B981)),
    RCM("RCM", "Restraint Control Module (Airbag)", Color(0xFFF97316)),
    FOUR_X_FOUR("4X4M", "4WD Control Module", Color(0xFFA855F7)),
    PATS("PATS", "Passive Anti-Theft System", Color(0xFFEC4899))
}

data class ForscanDtcCode(
    val code: String, // e.g., "P0171"
    val module: FordModule,
    val title: String, // e.g., "System Too Lean (Bank 1)"
    val status: String, // "Confirmed Fault", "Pending", "Memory"
    val fordSpecificDetails: String, // e.g. "Fuel trim balance limits exceeded on Bank 1"
    val targetComponentId: String, // Maps to 3D model component e.g. "intake_manifold"
    val relevantPids: List<String>, // e.g. ["STFT1", "LTFT1", "MAF", "BARO"]
    val suggestedForscanTest: String // e.g. "Perform KOER (Key On Engine Running) Self-Test"
)

data class ForscanPidData(
    val pidId: String,
    val name: String,
    val shortName: String,
    val currentValue: Double,
    val unit: String,
    val minVal: Double,
    val maxVal: Double,
    val normalMin: Double,
    val normalMax: Double,
    val module: FordModule,
    val description: String
)

data class ForscanDiagnosticSession(
    val timestampMillis: Long,
    val adapterName: String, // e.g. "OBDLink MX+ Bluetooth (ELM327 v2.2)"
    val batteryVoltage: Double, // e.g. 13.8V
    val activeDtcs: List<ForscanDtcCode>,
    val pids: List<ForscanPidData>
)
