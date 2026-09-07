package com.example.obd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Obd2PidParserTest {
    @Test fun decodesRpmAndCoolantTemperature() {
        val rpm = Obd2PidParser.parse(Obd2CommandResponse("010C", "41 0C 1A F8", true))!!
        assertEquals(1726.0, rpm.value, 0.01)
        assertEquals("rpm", rpm.unit)
        val ect = Obd2PidParser.parse(Obd2CommandResponse("0105", "41 05 7B", true))!!
        assertEquals(83.0, ect.value, 0.01)
    }

    @Test fun rejectsTransportFailureAndDecodesDtc() {
        assertEquals(null, Obd2PidParser.parse(Obd2CommandResponse("010C", "NO DATA", false)))
        val dtcs = Obd2PidParser.parseDtc(Obd2CommandResponse("03", "43 01 71 00 00", true))
        assertTrue(dtcs.contains("P0171"))
    }
}
