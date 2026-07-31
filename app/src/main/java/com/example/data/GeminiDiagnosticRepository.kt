package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.api.GeminiClient
import com.example.api.GeminiContent
import com.example.api.GeminiGenerationConfig
import com.example.api.GeminiPart
import com.example.api.GeminiRequest
import com.example.model.ChatMessage
import com.example.model.ChatSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiDiagnosticRepository {

    private val systemPrompt = """
        You are the Master Mechanic AI for a 2004 Ford Explorer Sport Trac 4.0L SOHC V6.
        Your job is to analyze user-reported symptoms, OBD-II DTC fault codes (e.g., P0171, P0174, P0300, P0128, P0732), sounds, and vehicle behaviors.
        
        Provide structured, clear, and actionable diagnostic guidance:
        - Primary Suspected Cause(s) & Probabilities
        - Diagnostic Inspection Steps (e.g., STFT/LTFT fuel trims, vacuum gauge test, FORScan PID test)
        - Key Replacement Parts (e.g., Intake Manifold Gasket, PCV Boot, Thermostat Housing, Timing Tensioner, 5R55E Solenoid)
        - Urgency Level: "Immediate Attention Needed", "Repair Soon", or "Monitor / Safe to Drive"
        
        Keep your tone professional, practical, and specialized for the Cologne 4.0L SOHC engine and 5R55E transmission.
    """.trimIndent()

    suspend fun analyzeSymptom(
        userMessageText: String,
        history: List<ChatMessage>
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // Check if API key is present and non-placeholder
        val isValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (isValidKey) {
            try {
                val contentsList = mutableListOf<GeminiContent>()

                // Add previous conversation context (up to last 6 messages)
                history.takeLast(6).forEach { msg ->
                    val role = if (msg.sender == ChatSender.USER) "user" else "model"
                    contentsList.add(
                        GeminiContent(
                            role = role,
                            parts = listOf(GeminiPart(text = msg.text))
                        )
                    )
                }

                // Add current user prompt
                contentsList.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = userMessageText))
                    )
                )

                val request = GeminiRequest(
                    contents = contentsList,
                    systemInstruction = GeminiContent(
                        role = "system",
                        parts = listOf(GeminiPart(text = systemPrompt))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.3f
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!aiText.isNullOrBlank()) {
                    val (componentId, componentName) = detectComponentMatch(aiText)
                    val urgency = detectUrgencyLevel(aiText)

                    return@withContext ChatMessage(
                        sender = ChatSender.GEMINI_MECHANIC,
                        text = aiText,
                        suggestedComponentId = componentId,
                        suggestedComponentName = componentName,
                        urgencyLevel = urgency
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiRepo", "Gemini API call failed, falling back to local mechanic engine: ${e.message}")
            }
        }

        // Offline / Fallback Local Rule-Based Engine
        val localResponse = generateLocalFallbackAnalysis(userMessageText)
        val (componentId, componentName) = detectComponentMatch(localResponse.text)

        ChatMessage(
            sender = ChatSender.GEMINI_MECHANIC,
            text = localResponse.text,
            suggestedComponentId = componentId ?: localResponse.suggestedComponentId,
            suggestedComponentName = componentName ?: localResponse.suggestedComponentName,
            urgencyLevel = localResponse.urgencyLevel
        )
    }

    private fun detectComponentMatch(text: String): Pair<String?, String?> {
        val lower = text.lowercase()
        return when {
            lower.contains("pcv") || lower.contains("vacuum elbow") || lower.contains("intake gasket") || lower.contains("p0171") || lower.contains("p0174") ->
                Pair("intake_manifold", "Upper & Lower Intake Manifold")
            lower.contains("thermostat") || lower.contains("coolant valley") || lower.contains("p0128") || lower.contains("antifreeze puddle") ->
                Pair("thermostat_housing", "Coolant Thermostat Housing Assembly")
            lower.contains("timing chain") || lower.contains("rattle") || lower.contains("tensioner") || lower.contains("cologne") ->
                Pair("engine_block", "4.0L SOHC V6 Engine Block & Heads")
            lower.contains("5r55e") || lower.contains("separator plate") || lower.contains("epc solenoid") || lower.contains("shift flare") || lower.contains("p0732") ->
                Pair("transmission_solenoids", "Valve Body Solenoid Pack & Filter")
            lower.contains("misfire") || lower.contains("spark plug") || lower.contains("coil pack") || lower.contains("p0300") || lower.contains("p0301") ->
                Pair("alternator_ignition", "130-Amp Alternator & EDIS Coil Pack")
            lower.contains("maf") || lower.contains("mass air") || lower.contains("throttle") || lower.contains("iac") ->
                Pair("throttle_body", "Throttle Body & Mass Air Flow (MAF) Sensor")
            lower.contains("water pump") || lower.contains("weep hole") ->
                Pair("water_pump", "Engine Coolant Water Pump")
            lower.contains("fan clutch") || lower.contains("radiator") ->
                Pair("radiator_assembly", "Radiator & Mechanical Fan Clutch")
            lower.contains("a/c") || lower.contains("ac compressor") || lower.contains("clutch gap") ->
                Pair("ac_compressor", "A/C Scroll Compressor & Magnetic Clutch")
            lower.contains("brake") || lower.contains("rotor") || lower.contains("caliper") ->
                Pair("brakes_suspension", "Front Disc Brakes & Torsion Bar Suspension")
            lower.contains("frame") || lower.contains("cargo bed") || lower.contains("body mount") || lower.contains("tailgate") || lower.contains("hitch") ->
                Pair("truck_frame_body", "Sport Trac Frame & Composite Cargo Bed")
            lower.contains("4x4") || lower.contains("4wd") || lower.contains("transfer case") || lower.contains("driveshaft") || lower.contains("u-joint") || lower.contains("differential") ->
                Pair("driveshaft_4x4", "Control Trac 4WD Transfer Case & Driveshafts")
            lower.contains("exhaust") || lower.contains("catalytic") || lower.contains("converter") || lower.contains("p0420") || lower.contains("p0430") || lower.contains("o2 sensor") ->
                Pair("exhaust_system", "Exhaust Manifolds & Catalytic Converter Y-Pipe")
            lower.contains("fuel pump") || lower.contains("fuel tank") || lower.contains("fuel pressure") || lower.contains("tank strap") ->
                Pair("fuel_tank_pump", "22.5 Gallon Fuel Tank & High-Pressure Pump Module")
            lower.contains("steering rack") || lower.contains("power steering") || lower.contains("tie rod") || lower.contains("rack and pinion") ->
                Pair("steering_rack", "Power Steering Rack & Pinion Assembly")
            else -> Pair(null, null)
        }
    }

    private fun detectUrgencyLevel(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("immediate") || lower.contains("stop driving") || lower.contains("overheating") || lower.contains("critical") ->
                "Immediate Attention Needed"
            lower.contains("soon") || lower.contains("high probability") || lower.contains("replace") ->
                "Repair Soon"
            else -> "Monitor / Safe to Drive"
        }
    }

    private fun generateLocalFallbackAnalysis(userQuery: String): ChatMessage {
        val query = userQuery.lowercase()

        return when {
            query.contains("p0171") || query.contains("p0174") || query.contains("lean") || query.contains("rough idle") -> {
                ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = """
                        **DIAGNOSIS FOR LEAN CODES (P0171 / P0174):**
                        
                        On the 2004 Ford Explorer Sport Trac 4.0L SOHC V6, lean system codes on both Bank 1 and Bank 2 almost always indicate unmetered intake vacuum leaks.
                        
                        **Primary Causes (Ordered by Probability):**
                        1. **Shrunk Upper Intake Manifold Gaskets (85% Probability)**: Heat cycling hardens press-in rubber plenum seals.
                        2. **Cracked PCV Valve Rubber Elbow (70% Probability)**: Rear rubber elbow under intake plenum degrades and collapses under vacuum.
                        3. **Dirty Mass Air Flow (MAF) Sensor Wire**: Dust buildup skews air volume measurements.
                        
                        **Recommended Inspection Steps:**
                        - Perform a smoke test or carefully spray brake cleaner around intake plenum seams while idling; watch for RPM drops.
                        - Inspect live FORScan PIDs: Long Term Fuel Trims (LTFT1 & LTFT2) > +15% at idle that drop back to normal at 2,500 RPM confirm a vacuum leak.
                        
                        **Suggested Repair:**
                        Replace Upper & Lower Intake Manifold Gaskets and PCV boot.
                    """.trimIndent(),
                    suggestedComponentId = "intake_manifold",
                    suggestedComponentName = "Upper & Lower Intake Manifold",
                    urgencyLevel = "Repair Soon"
                )
            }

            query.contains("rattle") || query.contains("ticking") || query.contains("cold start") || query.contains("chain") -> {
                ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = """
                        **DIAGNOSIS FOR COLD START ENGINE RATTLE:**
                        
                        The Cologne 4.0L SOHC V6 uses hydraulic timing chain tensioners for the front, rear, and jackshaft timing cassettes.
                        
                        **Primary Causes:**
                        1. **Bleeding Hydraulic Timing Chain Tensioners (90% Probability)**: Check valves fail inside hydraulic tensioners, letting oil bleed down overnight.
                        2. **Worn Timing Cassette Guide Plastic**: Prolonged chain slap wears through plastic guide guides into oil pan.
                        
                        **Recommended Inspection Steps:**
                        - Observe rattle duration on cold start: 1-3 seconds is hydraulic tensioner oil bleed. Rattle at all RPMs indicates broken cassette guide.
                        - Check oil filter for fine metal shavings or plastic cassette pieces during oil drain.
                        
                        **Suggested Repair:**
                        Replace primary hydraulic timing chain tensioners (OEM Motorcraft) promptly.
                    """.trimIndent(),
                    suggestedComponentId = "engine_block",
                    suggestedComponentName = "4.0L SOHC V6 Engine Block & Heads",
                    urgencyLevel = "Immediate Attention Needed"
                )
            }

            query.contains("overheat") || query.contains("coolant") || query.contains("puddle") || query.contains("p0128") -> {
                ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = """
                        **DIAGNOSIS FOR COOLANT LEAK & OVERHEATING:**
                        
                        **Primary Causes:**
                        1. **Cracked Plastic Thermostat Housing (95% Probability)**: Factory composite two-piece thermostat housing seams split behind the belt pulley, pooling coolant in the engine valley.
                        2. **Failed Radiator End Tank / Fan Clutch**: Thermal clutch fails to engage under high heat.
                        
                        **Recommended Inspection Steps:**
                        - Shine flashlight behind generator/belt pulley onto top of engine valley. Look for gold/green fluid pool.
                        - Pressure test cooling system at 16 PSI with hand pump.
                        
                        **Suggested Repair:**
                        Replace factory plastic housing with upgraded cast aluminum thermostat housing assembly.
                    """.trimIndent(),
                    suggestedComponentId = "thermostat_housing",
                    suggestedComponentName = "Coolant Thermostat Housing Assembly",
                    urgencyLevel = "Immediate Attention Needed"
                )
            }

            query.contains("trans") || query.contains("shift") || query.contains("flare") || query.contains("5r55e") || query.contains("p0732") -> {
                ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = """
                        **DIAGNOSIS FOR 5R55E TRANSMISSION SHIFT FLARE / O/D LIGHT:**
                        
                        **Primary Causes:**
                        1. **Blown Valve Body Separator Plate Gasket**: Paper gasket near EPC solenoid blows out, dropping pressure during 2-3 gear shift.
                        2. **Worn Electronic Pressure Control (EPC) Solenoid**: Sticky pressure solenoid causes erratic line pressure.
                        
                        **Recommended Inspection Steps:**
                        - Connect FORScan OBD scanner and monitor Transmission Fluid Temp (TFT) and EPC duty cycle PIDs.
                        - Drop transmission pan and inspect magnet for metal clutch debris.
                        
                        **Suggested Repair:**
                        Install updated bonded separator plate gasket and new EPC solenoid pack.
                    """.trimIndent(),
                    suggestedComponentId = "transmission_solenoids",
                    suggestedComponentName = "Valve Body Solenoid Pack & Filter",
                    urgencyLevel = "Repair Soon"
                )
            }

            else -> {
                ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = """
                        **GENERAL DIAGNOSTIC ANALYSIS (2004 Ford Sport Trac 4.0L V6):**
                        
                        I analyzed your description: "$userQuery".
                        
                        **Recommended Next Steps:**
                        1. Scan OBD-II system for pending or stored Diagnostic Trouble Codes (DTCs).
                        2. Check basic fluid levels: Engine Oil (FL-820S), Motorcraft Gold Coolant, and MERCON V Transmission fluid.
                        3. Test relevant sensor PIDs using the FORScan OBD Analyzer in this app.
                        
                        *Tip: Ask me about specific fault codes (e.g. P0171, P0300, P0128) or symptoms like "cold start rattle", "coolant leak", or "engine misfire" for targeted part recommendations!*
                    """.trimIndent(),
                    suggestedComponentId = "engine_block",
                    suggestedComponentName = "4.0L SOHC V6 Engine Block & Heads",
                    urgencyLevel = "Monitor / Safe to Drive"
                )
            }
        }
    }
}
