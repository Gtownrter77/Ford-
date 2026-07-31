package com.example.data

import com.example.model.*

object ForscanData {

    val knownSportTracDtcs = listOf(
        ForscanDtcCode(
            code = "P0171",
            module = FordModule.PCM,
            title = "System Too Lean (Bank 1)",
            status = "Confirmed Fault",
            fordSpecificDetails = "Fuel trim limits exceeded on Bank 1. Adaptive fuel tables reached maximum correction (+25%). Common cause on 2004 Sport Trac: PCV elbow rubber boot collapse behind intake or shrinking upper intake manifold O-rings.",
            targetComponentId = "intake_manifold",
            relevantPids = listOf("STFT1", "LTFT1", "MAF", "VPWR"),
            suggestedForscanTest = "Execute FORScan KOER (Key On Engine Running) test while spraying brake cleaner near PCV elbow to observe LTFT drop."
        ),
        ForscanDtcCode(
            code = "P0174",
            module = FordModule.PCM,
            title = "System Too Lean (Bank 2)",
            status = "Confirmed Fault",
            fordSpecificDetails = "Bank 2 fuel trim correction maxed out (+25%). Often occurs alongside P0171 due to hardened lower intake manifold gaskets or dirty Mass Air Flow sensor wire.",
            targetComponentId = "intake_manifold",
            relevantPids = listOf("STFT2", "LTFT2", "MAF", "TP"),
            suggestedForscanTest = "Read MAF_V PID at 2,000 RPM (should read ~1.2V - 1.6V) and clean sensor element."
        ),
        ForscanDtcCode(
            code = "P0300",
            module = FordModule.PCM,
            title = "Random / Multiple Cylinder Misfire",
            status = "Pending Fault",
            fordSpecificDetails = "PCM detected irregular crankshaft speed fluctuation across multiple cylinders. Common on 4.0L SOHC when Motorcraft EDIS coil pack insulation degrades or plug gaps exceed 0.054\".",
            targetComponentId = "spark_plugs_coils",
            relevantPids = listOf("RPM", "MISFIRE_CNT", "VPWR"),
            suggestedForscanTest = "Run FORScan Misfire Monitor PID log per cylinder (CYL_1_MIS to CYL_6_MIS)."
        ),
        ForscanDtcCode(
            code = "P0401",
            module = FordModule.PCM,
            title = "EGR Flow Insufficient Detected",
            status = "Memory Fault",
            fordSpecificDetails = "EGR Delta Pressure Feedback Sensor (DPFE) did not detect expected pressure differential during vacuum pulse. Moisture intrusion in plastic DPFE sensor.",
            targetComponentId = "egr_valve",
            relevantPids = listOf("DPFE_V", "EGRVR", "RPM"),
            suggestedForscanTest = "Monitor DPFE_V voltage PID in FORScan. Voltage should rise from ~1.0V to 3.5V when EGR command is actuated."
        ),
        ForscanDtcCode(
            code = "P0128",
            module = FordModule.PCM,
            title = "Coolant Temp Below Thermostat Regulating Temp",
            status = "Confirmed Fault",
            fordSpecificDetails = "Engine coolant temperature failed to reach target 180°F operating temp within time limit. Stock thermostat rubber seal degraded or plastic housing cracked.",
            targetComponentId = "thermostat_housing",
            relevantPids = listOf("ECT", "RPM", "IAT"),
            suggestedForscanTest = "Monitor ECT PID from cold startup. Normal 4.0L SOHC operating temp is 192°F - 210°F."
        ),
        ForscanDtcCode(
            code = "P0705",
            module = FordModule.PCM,
            title = "Transmission Range Sensor Circuit Malfunction (PRNDL)",
            status = "Confirmed Fault",
            fordSpecificDetails = "Digital TRS sensor on 5R55E transmission side case reported invalid switch combination to PCM. Causes delayed reverse engagement or O/D Light flashing.",
            targetComponentId = "transmission_5r55e",
            relevantPids = listOf("TR_STAT", "TFT", "RPM"),
            suggestedForscanTest = "Read TR_STAT PID in FORScan through all gear positions (P-R-N-D-2-1)."
        ),
        ForscanDtcCode(
            code = "B1318",
            module = FordModule.GEM,
            title = "Battery Voltage Low (< 10.0V)",
            status = "History Fault",
            fordSpecificDetails = "Generic Electronic Module (GEM) logged supply voltage drop during cold crank or failing Motorcraft 130A alternator diode bridge.",
            targetComponentId = "battery_alternator",
            relevantPids = listOf("VPWR", "RPM"),
            suggestedForscanTest = "Check VPWR PID at idle with headlights and A/C blower on high (must maintain > 13.5V)."
        ),
        ForscanDtcCode(
            code = "C1145",
            module = FordModule.ABS,
            title = "Right Front Wheel Speed Sensor Input Circuit Failure",
            status = "Confirmed Fault",
            fordSpecificDetails = "4WABS Module lost AC pulse signal from front right hub sensor harness. Causes ABS light and 4x4 High flashing lights.",
            targetComponentId = "front_brakes_rotors",
            relevantPids = listOf("RF_WSPD", "LF_WSPD", "LR_WSPD"),
            suggestedForscanTest = "Monitor RF_WSPD wheel speed graph in FORScan while rolling forward at 5 MPH."
        )
    )

    val defaultPids = listOf(
        ForscanPidData(
            pidId = "RPM",
            name = "Engine Speed",
            shortName = "RPM",
            currentValue = 750.0,
            unit = "RPM",
            minVal = 0.0,
            maxVal = 6000.0,
            normalMin = 650.0,
            normalMax = 800.0,
            module = FordModule.PCM,
            description = "PCM Crankshaft Position Sensor live RPM signal"
        ),
        ForscanPidData(
            pidId = "ECT",
            name = "Engine Coolant Temp",
            shortName = "ECT",
            currentValue = 198.5,
            unit = "°F",
            minVal = 32.0,
            maxVal = 260.0,
            normalMin = 188.0,
            normalMax = 212.0,
            module = FordModule.PCM,
            description = "Coolant temperature measured at aluminum sensor housing"
        ),
        ForscanPidData(
            pidId = "MAF",
            name = "Mass Air Flow Rate",
            shortName = "MAF",
            currentValue = 4.85,
            unit = "g/s",
            minVal = 0.0,
            maxVal = 250.0,
            normalMin = 4.2,
            normalMax = 5.5,
            module = FordModule.PCM,
            description = "Hot wire anemometer intake air mass rate"
        ),
        ForscanPidData(
            pidId = "STFT1",
            name = "Short Term Fuel Trim Bank 1",
            shortName = "STFT1",
            currentValue = 1.6,
            unit = "%",
            minVal = -25.0,
            maxVal = 25.0,
            normalMin = -10.0,
            normalMax = 10.0,
            module = FordModule.PCM,
            description = "Immediate closed-loop oxygen sensor fuel adjustment"
        ),
        ForscanPidData(
            pidId = "LTFT1",
            name = "Long Term Fuel Trim Bank 1",
            shortName = "LTFT1",
            currentValue = 3.2,
            unit = "%",
            minVal = -25.0,
            maxVal = 25.0,
            normalMin = -10.0,
            normalMax = 10.0,
            module = FordModule.PCM,
            description = "Learned adaptive fuel compensation map for Bank 1"
        ),
        ForscanPidData(
            pidId = "TFT",
            name = "Transmission Fluid Temp",
            shortName = "TFT",
            currentValue = 174.0,
            unit = "°F",
            minVal = 32.0,
            maxVal = 250.0,
            normalMin = 150.0,
            normalMax = 195.0,
            module = FordModule.PCM,
            description = "5R55E Transmission pan fluid temperature thermistor"
        ),
        ForscanPidData(
            pidId = "VPWR",
            name = "PCM Module Supply Voltage",
            shortName = "VPWR",
            currentValue = 14.1,
            unit = "V",
            minVal = 8.0,
            maxVal = 18.0,
            normalMin = 13.5,
            normalMax = 14.7,
            module = FordModule.PCM,
            description = "Alternator output voltage at main relay bus"
        ),
        ForscanPidData(
            pidId = "TP",
            name = "Throttle Position",
            shortName = "TP",
            currentValue = 18.2,
            unit = "%",
            minVal = 0.0,
            maxVal = 100.0,
            normalMin = 16.0,
            normalMax = 20.0,
            module = FordModule.PCM,
            description = "Potentiometer angle on 4.0L throttle body shaft"
        )
    )

    fun parseForscanLogText(logText: String): List<ForscanDtcCode> {
        val foundDtcs = mutableListOf<ForscanDtcCode>()
        knownSportTracDtcs.forEach { dtc ->
            if (logText.contains(dtc.code, ignoreCase = true) || logText.contains(dtc.title, ignoreCase = true)) {
                foundDtcs.add(dtc)
            }
        }
        return foundDtcs.ifEmpty { listOf(knownSportTracDtcs[0], knownSportTracDtcs[1]) }
    }
}
