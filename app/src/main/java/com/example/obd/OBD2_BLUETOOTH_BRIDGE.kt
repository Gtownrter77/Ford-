package com.example.obd

/**
 * Placeholder contract only.
 *
 * No Bluetooth discovery, permission request, socket creation, ELM327 transport,
 * command parsing, or connection state is implemented by this file.
 */
interface Obd2BluetoothBridge {
    suspend fun discoverAdapters(): List<Obd2AdapterCandidate>

    suspend fun connect(adapter: Obd2AdapterCandidate): Obd2Connection

    suspend fun sendElm327Command(command: String): Obd2CommandResponse

    suspend fun disconnect()
}

data class Obd2AdapterCandidate(
    val adapterId: String,
    val displayName: String,
    val macAddress: String?
)

data class Obd2Connection(
    val adapterId: String,
    val isConnected: Boolean
)

data class Obd2CommandResponse(
    val command: String,
    val rawResponse: String,
    val isSuccess: Boolean
)
