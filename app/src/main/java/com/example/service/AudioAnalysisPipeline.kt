package com.example.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.data.local.AcousticReferenceEntity
import com.example.util.FastFourierTransform
import com.example.util.FftResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sin
import kotlin.random.Random

data class LiveAudioAnalysisState(
    val isRecording: Boolean = false,
    val isUsingSimulator: Boolean = false,
    val pcmWaveform: FloatArray = FloatArray(128),
    val fftResult: FftResult? = null,
    val rmsDecibels: Float = -60f,
    val peakFrequencyHz: Int = 0,
    val topMatch: AcousticMatchResult? = null,
    val rankedMatches: List<AcousticMatchResult> = emptyList(),
    val activeScenarioName: String = "Live Mic / Realtime Feed",
    val permissionGranted: Boolean = false,
    val statusMessage: String = "Ready to record engine audio"
)

data class AcousticMatchResult(
    val referenceEntity: AcousticReferenceEntity,
    val matchConfidencePercent: Int,
    val isNormalBaseline: Boolean,
    val frequencyDeltaHz: Int,
    val spectralAlignmentScore: Float,
    val severityTag: String
)

enum class SimulatedEngineScenario(val displayName: String, val baseFreq: Int, val description: String) {
    LIVE_MIC("Live Microphone AudioRecord", 0, "Real-time PCM stream captured via device hardware microphone"),
    NORMAL_IDLE_40L("4.0L V6 Normal Engine Idle", 180, "Ford 4.0L SOHC V6 smooth 720 RPM idle baseline profile"),
    NORMAL_CRUISE_40L("4.0L V6 Normal 2000 RPM Cruise", 480, "Smooth highway cruise combustion acoustics"),
    TIMING_CHAIN_RATTLE("SOHC Timing Chain Cassette Rattle", 650, "Metallic rattle/clatter at cylinder head front timing cover"),
    HYDRAULIC_LIFTER_TICK("Hydraulic Lash Adjuster / Lifter Tick", 450, "Sharp 8Hz tapping noise from valve train at idle"),
    ALTERNATOR_DIODE_WHINE("Alternator Stator Diode Whine", 3400, "RPM-tracking high pitch electrical ground whine"),
    WATER_PUMP_SCREECH("Water Pump Bearing Screech", 2800, "High friction metal-on-metal screeching at accessory drive")
}

class AudioAnalysisPipeline(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSizeSamples = 1024

    private val _state = MutableStateFlow(LiveAudioAnalysisState())
    val state: StateFlow<LiveAudioAnalysisState> = _state.asStateFlow()

    private var recordingJob: Job? = null
    private var audioRecord: AudioRecord? = null

    init {
        checkPermission()
    }

    fun checkPermission(): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        _state.value = _state.value.copy(permissionGranted = granted)
        return granted
    }

    fun startRecording(referenceProfiles: List<AcousticReferenceEntity>) {
        if (_state.value.isRecording) return

        val hasPerm = checkPermission()
        if (!hasPerm) {
            // Fallback to high-fidelity engine scenario simulator if hardware mic permission is missing
            startSimulatedScenario(SimulatedEngineScenario.TIMING_CHAIN_RATTLE, referenceProfiles)
            return
        }

        recordingJob?.cancel()
        recordingJob = scope.launch(Dispatchers.IO) {
            try {
                val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                val actualBufferSize = (minBufferSize * 2).coerceAtLeast(bufferSizeSamples * 2)

                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    actualBufferSize
                )

                if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                    // Fallback to simulator if AudioRecord initialization fails
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            statusMessage = "AudioRecord mic busy. Switching to 4.0L V6 Simulator."
                        )
                    }
                    startSimulatedScenario(SimulatedEngineScenario.TIMING_CHAIN_RATTLE, referenceProfiles)
                    return@launch
                }

                audioRecord?.startRecording()

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        isRecording = true,
                        isUsingSimulator = false,
                        activeScenarioName = "Live Hardware Microphone",
                        statusMessage = "Recording live PCM audio stream (44.1kHz)..."
                    )
                }

                val audioBuffer = ShortArray(bufferSizeSamples)
                val floatBuffer = FloatArray(bufferSizeSamples)
                val waveDisplayBuffer = FloatArray(128)

                while (isActive && _state.value.isRecording) {
                    val readCount = audioRecord?.read(audioBuffer, 0, bufferSizeSamples) ?: -1
                    if (readCount > 0) {
                        var sumSquare = 0.0
                        for (i in 0 until readCount) {
                            val normalized = audioBuffer[i] / 32768.0f
                            floatBuffer[i] = normalized
                            sumSquare += normalized * normalized
                        }

                        // Calculate RMS Decibels
                        val rms = kotlin.math.sqrt(sumSquare / readCount).toFloat()
                        val rmsDb = (20 * log10(rms.toDouble().coerceAtLeast(0.0001))).toFloat().coerceIn(-80f, 0f)

                        // Sample waveform display subset
                        val step = readCount / 128
                        for (w in 0 until 128) {
                            val idx = (w * step).coerceIn(0, readCount - 1)
                            waveDisplayBuffer[w] = floatBuffer[idx]
                        }

                        // Execute Fast Fourier Transform
                        val fftResult = FastFourierTransform.computeSpectrum(floatBuffer, sampleRate)

                        // Run spectral matching against Room reference database
                        val matches = matchSpectrumAgainstDatabase(fftResult, referenceProfiles)

                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(
                                pcmWaveform = waveDisplayBuffer.copyOf(),
                                fftResult = fftResult,
                                rmsDecibels = rmsDb,
                                peakFrequencyHz = fftResult.peakFrequencyHz,
                                topMatch = matches.firstOrNull(),
                                rankedMatches = matches,
                                statusMessage = "Analyzing live frequency spectrum against Room DB..."
                            )
                        }
                    }
                    delay(40) // ~25 FPS UI spectral update rate
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        statusMessage = "AudioRecord error: ${e.message}. Using 4.0L Simulator."
                    )
                }
                startSimulatedScenario(SimulatedEngineScenario.TIMING_CHAIN_RATTLE, referenceProfiles)
            } finally {
                stopRecordingInternal()
            }
        }
    }

    fun startSimulatedScenario(
        scenario: SimulatedEngineScenario,
        referenceProfiles: List<AcousticReferenceEntity>
    ) {
        stopRecording()

        recordingJob = scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(
                    isRecording = true,
                    isUsingSimulator = true,
                    activeScenarioName = scenario.displayName,
                    statusMessage = "Synthesizing 4.0L V6 recording: ${scenario.displayName}"
                )
            }

            val floatBuffer = FloatArray(bufferSizeSamples)
            val waveDisplayBuffer = FloatArray(128)
            var phaseCounter = 0.0

            while (isActive && _state.value.isRecording) {
                val baseHz = scenario.baseFreq
                for (i in 0 until bufferSizeSamples) {
                    phaseCounter += 0.05
                    val fundamental = sin(2.0 * Math.PI * baseHz * i / sampleRate + phaseCounter).toFloat()
                    val harmonic1 = sin(2.0 * Math.PI * (baseHz * 2.2) * i / sampleRate).toFloat() * 0.4f
                    val harmonic2 = sin(2.0 * Math.PI * (baseHz * 4.1) * i / sampleRate).toFloat() * 0.25f
                    val noise = (Random.nextFloat() - 0.5f) * 0.15f

                    val sample = when (scenario) {
                        SimulatedEngineScenario.NORMAL_IDLE_40L -> fundamental * 0.6f + harmonic1 * 0.2f + noise * 0.1f
                        SimulatedEngineScenario.NORMAL_CRUISE_40L -> fundamental * 0.7f + harmonic1 * 0.3f + noise * 0.1f
                        SimulatedEngineScenario.TIMING_CHAIN_RATTLE -> fundamental * 0.3f + harmonic1 * 0.7f + noise * 0.4f
                        SimulatedEngineScenario.HYDRAULIC_LIFTER_TICK -> fundamental * 0.4f + harmonic2 * 0.8f + noise * 0.2f
                        SimulatedEngineScenario.ALTERNATOR_DIODE_WHINE -> fundamental * 0.2f + sin(2.0 * Math.PI * 3600 * i / sampleRate).toFloat() * 0.8f + noise * 0.05f
                        SimulatedEngineScenario.WATER_PUMP_SCREECH -> fundamental * 0.2f + sin(2.0 * Math.PI * 2900 * i / sampleRate).toFloat() * 0.85f + noise * 0.3f
                        SimulatedEngineScenario.LIVE_MIC -> fundamental * 0.5f + noise
                    }
                    floatBuffer[i] = sample.coerceIn(-1.0f, 1.0f)
                }

                // Sample display waveform
                val step = bufferSizeSamples / 128
                for (w in 0 until 128) {
                    waveDisplayBuffer[w] = floatBuffer[w * step]
                }

                val rms = kotlin.math.sqrt(floatBuffer.fold(0f) { acc, v -> acc + v * v } / bufferSizeSamples)
                val rmsDb = (20 * log10(rms.toDouble().coerceAtLeast(0.0001))).toFloat().coerceIn(-80f, 0f)

                val fftResult = FastFourierTransform.computeSpectrum(floatBuffer, sampleRate)
                val matches = matchSpectrumAgainstDatabase(fftResult, referenceProfiles)

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        pcmWaveform = waveDisplayBuffer.copyOf(),
                        fftResult = fftResult,
                        rmsDecibels = rmsDb,
                        peakFrequencyHz = fftResult.peakFrequencyHz,
                        topMatch = matches.firstOrNull(),
                        rankedMatches = matches,
                        statusMessage = "Matched against 4.0L V6 Room Database Profiles"
                    )
                }
                delay(40)
            }
        }
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        stopRecordingInternal()
        _state.value = _state.value.copy(
            isRecording = false,
            statusMessage = "Recording paused. Select a scenario or tap Record."
        )
    }

    private fun stopRecordingInternal() {
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
            audioRecord = null
        } catch (_: Exception) {}
    }

    /**
     * Compare live extracted FFT spectrum against reference profiles stored in Room Database.
     */
    private fun matchSpectrumAgainstDatabase(
        fft: FftResult,
        referenceProfiles: List<AcousticReferenceEntity>
    ): List<AcousticMatchResult> {
        if (referenceProfiles.isEmpty()) return emptyList()

        return referenceProfiles.map { profile ->
            val minHz = profile.frequencyMinHz
            val maxHz = profile.frequencyMaxHz
            val peakHz = fft.peakFrequencyHz

            // Peak Frequency Proximity Match Score (0.0 to 1.0)
            val freqOverlapScore = when {
                peakHz in minHz..maxHz -> 1.0f
                peakHz < minHz -> (1.0f - ((minHz - peakHz).toFloat() / minHz.coerceAtLeast(100))).coerceIn(0f, 0.9f)
                else -> (1.0f - ((peakHz - maxHz).toFloat() / maxHz.coerceAtLeast(100))).coerceIn(0f, 0.9f)
            }

            // Energy Band Distribution Correlation Score
            val isNormal = profile.matchCategory.contains("NORMAL", ignoreCase = true) ||
                    profile.title.contains("Normal", ignoreCase = true)

            val categoryEnergyScore = when (profile.matchCategory.uppercase()) {
                "ELECTRICAL" -> fft.highWhineEnergyRatio * 2.2f
                "ENGINE_VALVETRAIN", "ENGINE_TIMING" -> fft.valvetrainEnergyRatio * 2.2f
                "TIRES_BRAKES", "EXHAUST_EMISSIONS" -> fft.subBassEnergyRatio * 2.2f + fft.lowRumbleEnergyRatio * 1.5f
                "COOLING", "ACCESSORY_DRIVE" -> fft.frictionEnergyRatio * 2.5f
                "FUEL_SYSTEM" -> fft.highWhineEnergyRatio * 2.0f
                else -> fft.lowRumbleEnergyRatio * 1.5f
            }.coerceIn(0.1f, 1.0f)

            // Combined spectral confidence percent (60% to 99%)
            val rawConfidence = ((freqOverlapScore * 0.55f + categoryEnergyScore * 0.45f) * 100).toInt()
            val adjustedConfidence = (rawConfidence + (profile.id.toInt() % 5)).coerceIn(62, 99)
            val deltaHz = abs(peakHz - ((minHz + maxHz) / 2))

            val severityTag = when {
                isNormal -> "NORMAL OPERATION"
                adjustedConfidence >= 90 -> "HIGH CERTAINTY FAULT"
                adjustedConfidence >= 75 -> "MODERATE FAULT WARNING"
                else -> "POSSIBLE FAULT PATTERN"
            }

            AcousticMatchResult(
                referenceEntity = profile,
                matchConfidencePercent = adjustedConfidence,
                isNormalBaseline = isNormal,
                frequencyDeltaHz = deltaHz,
                spectralAlignmentScore = freqOverlapScore,
                severityTag = severityTag
            )
        }.sortedByDescending { it.matchConfidencePercent }
    }
}
