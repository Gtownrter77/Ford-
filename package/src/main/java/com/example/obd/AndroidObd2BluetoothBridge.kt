package com.example.obd

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/** Real classic-Bluetooth ELM327 transport. It never fabricates connected/PID state. */
class AndroidObd2BluetoothBridge(private val context: Context) : Obd2BluetoothBridge {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var connection: Obd2Connection? = null
    private val sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override suspend fun discoverAdapters(): List<Obd2AdapterCandidate> = withContext(Dispatchers.IO) {
        if (!hasConnectPermission()) return@withContext emptyList()
        adapter?.bondedDevices.orEmpty().map { Obd2AdapterCandidate(it.address, it.name ?: "Unnamed Bluetooth adapter", it.address) }
    }

    override suspend fun connect(adapterCandidate: Obd2AdapterCandidate): Obd2Connection = withContext(Dispatchers.IO) {
        if (!hasConnectPermission()) throw SecurityException("BLUETOOTH_CONNECT permission is required")
        val bt = adapter ?: throw IOException("Bluetooth is unavailable on this device")
        val address = adapterCandidate.macAddress ?: throw IOException("Adapter has no Bluetooth address")
        val device = bt.getRemoteDevice(address)
        bt.cancelDiscovery()
        closeSocket()
        val newSocket = device.createRfcommSocketToServiceRecord(sppUuid)
        newSocket.connect()
        socket = newSocket
        initializeElm327(newSocket)
        return@withContext Obd2Connection(adapterCandidate.adapterId, true)
            .also { connection = it }
    }

    override suspend fun sendElm327Command(command: String): Obd2CommandResponse = withContext(Dispatchers.IO) {
        val active = socket ?: return@withContext Obd2CommandResponse(command, "Not connected", false)
        return@withContext try {
            val normalized = command.trim().uppercase().removeSuffix("\r")
            active.outputStream.write((normalized + "\r").toByteArray(Charsets.US_ASCII))
            active.outputStream.flush()
            val response = readUntilPrompt(active)
            Obd2CommandResponse(normalized, response, !response.contains("NO DATA") && !response.contains("ERROR"))
        } catch (e: IOException) {
            closeSocket()
            connection = null
            Obd2CommandResponse(command, e.message ?: "Transport error", false)
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) { closeSocket(); connection = null }

    private fun initializeElm327(active: BluetoothSocket) {
        listOf("ATZ", "ATE0", "ATL0", "ATS0", "ATSP0").forEach { command ->
            active.outputStream.write((command + "\r").toByteArray(Charsets.US_ASCII)); active.outputStream.flush(); readUntilPrompt(active)
        }
    }

    private fun readUntilPrompt(active: BluetoothSocket): String {
        val output = StringBuilder(); val deadline = System.currentTimeMillis() + 3500
        while (System.currentTimeMillis() < deadline) {
            while (active.inputStream.available() > 0) {
                val ch = active.inputStream.read().toChar()
                if (ch == '>') return output.toString().replace("\r", "\n").trim()
                output.append(ch)
            }
            Thread.sleep(20)
        }
        return output.toString().replace("\r", "\n").trim()
    }

    private fun closeSocket() { try { socket?.close() } catch (_: IOException) {}; socket = null }
    private fun hasConnectPermission() = android.os.Build.VERSION.SDK_INT < 31 || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
}

data class Obd2PidReading(val pid: String, val value: Double, val unit: String, val raw: String)

object Obd2PidParser {
    fun parse(response: Obd2CommandResponse): Obd2PidReading? {
        if (!response.isSuccess) return null
        val bytes = response.rawResponse.replace("\\s".toRegex(), " ").trim().split(" ").filter { it.matches(Regex("[0-9A-Fa-f]{2}")) }.map { it.toInt(16) }
        if (bytes.size < 3 || bytes[0] != 0x41) return null
        return when (bytes[1]) {
            0x0C -> Obd2PidReading("010C", ((bytes[2] * 256 + (bytes.getOrElse(3) { 0 })) / 4.0), "rpm", response.rawResponse)
            0x05 -> Obd2PidReading("0105", bytes[2] - 40.0, "°C", response.rawResponse)
            0x0B -> Obd2PidReading("010B", bytes[2].toDouble(), "kPa", response.rawResponse)
            0x0D -> Obd2PidReading("010D", bytes[2].toDouble(), "km/h", response.rawResponse)
            0x11 -> Obd2PidReading("0111", bytes[2] * 100.0 / 255.0, "%", response.rawResponse)
            else -> null
        }
    }

    fun parseDtc(response: Obd2CommandResponse): List<String> {
        if (!response.isSuccess) return emptyList()
        val b = response.rawResponse.replace("\\s".toRegex(), " ").trim().split(" ").filter { it.matches(Regex("[0-9A-Fa-f]{2}")) }.map { it.toInt(16) }
        if (b.size < 3 || b[0] != 0x43) return emptyList()
        return b.drop(1).chunked(2).mapNotNull { pair ->
            if (pair.size < 2 || pair[0] == 0 && pair[1] == 0) null else {
                val prefix = "PCBU"[(pair[0] shr 6) and 3]
                val digit = (pair[0] shr 4) and 3
                val suffix = "${pair[0] and 0x0F}${pair[1].toString(16).uppercase().padStart(2, '0')}"
                "${prefix}${digit}${suffix}"
            }
        }
    }
}
