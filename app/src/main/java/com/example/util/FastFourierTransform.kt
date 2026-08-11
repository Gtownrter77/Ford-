package com.example.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-performance pure-Kotlin Cooley-Tukey Radix-2 Fast Fourier Transform (FFT) implementation
 * for real-time audio signal processing and spectral analysis of engine sound recordings.
 */
object FastFourierTransform {

    /**
     * Compute FFT magnitudes for a real-valued audio sample buffer.
     * @param pcmSamples Input PCM audio buffer (length must be a power of 2, e.g. 1024 or 2048)
     * @param sampleRate Audio recording sampling rate in Hz (e.g. 44100 or 22050)
     * @return FftResult containing frequency magnitudes, bin frequencies, peak frequency, and spectral band energies.
     */
    fun computeSpectrum(pcmSamples: FloatArray, sampleRate: Int = 44100): FftResult {
        val n = pcmSamples.size
        require(n > 0 && (n and (n - 1)) == 0) { "FFT buffer size must be a power of 2 (e.g., 1024)" }

        // Apply Hann Windowing to minimize spectral leakage at window boundaries
        val real = FloatArray(n)
        val imag = FloatArray(n)
        for (i in 0 until n) {
            val window = 0.5f * (1f - cos(2.0 * Math.PI * i / (n - 1)).toFloat())
            real[i] = pcmSamples[i] * window
            imag[i] = 0f
        }

        // Bit-reversal permutation
        var j = 0
        for (i in 0 until n - 1) {
            if (i < j) {
                val tempR = real[i]
                real[i] = real[j]
                real[j] = tempR

                val tempI = imag[i]
                imag[i] = imag[j]
                imag[j] = tempI
            }
            var k = n shr 1
            while (k <= j) {
                j -= k
                k = k shr 1
            }
            j += k
        }

        // Cooley-Tukey Radix-2 FFT computation
        var len = 2
        while (len <= n) {
            val halfLen = len shr 1
            val angle = -2.0 * Math.PI / len
            val wStepReal = cos(angle).toFloat()
            val wStepImag = sin(angle).toFloat()

            var i = 0
            while (i < n) {
                var wReal = 1.0f
                var wImag = 0.0f
                for (k in 0 until halfLen) {
                    val pos = i + k + halfLen
                    val uReal = real[i + k]
                    val uImag = imag[i + k]
                    val vReal = real[pos] * wReal - imag[pos] * wImag
                    val vImag = real[pos] * wImag + imag[pos] * wReal

                    real[i + k] = uReal + vReal
                    imag[i + k] = uImag + vImag
                    real[pos] = uReal - vReal
                    imag[pos] = uImag - vImag

                    val nextWReal = wReal * wStepReal - wImag * wStepImag
                    val nextWImag = wReal * wStepImag + wImag * wStepReal
                    wReal = nextWReal
                    wImag = nextWImag
                }
                i += len
            }
            len = len shl 1
        }

        // Compute magnitude spectrum for positive frequencies (0 to Nyquist)
        val numBins = n / 2
        val magnitudes = FloatArray(numBins)
        val frequencies = FloatArray(numBins)
        val binWidth = sampleRate.toFloat() / n

        var maxMagnitude = 0f
        var maxBinIndex = 0

        for (k in 0 until numBins) {
            val mag = sqrt((real[k] * real[k] + imag[k] * imag[k])) / n
            magnitudes[k] = mag
            frequencies[k] = k * binWidth

            if (k > 1 && mag > maxMagnitude) { // Skip DC component (k=0)
                maxMagnitude = mag
                maxBinIndex = k
            }
        }

        val peakFrequencyHz = (maxBinIndex * binWidth).toInt()

        // Calculate spectral band energy breakdown
        var subBassEnergy = 0f    // 20Hz - 100Hz (Exhaust rumble, idle RPM)
        var lowRumbleEnergy = 0f  // 100Hz - 400Hz (Piston slap, manifold resonance)
        var valvetrainEnergy = 0f // 400Hz - 1200Hz (Timing chain clatter, lifter tick)
        var highWhineEnergy = 0f  // 1200Hz - 3500Hz (Fuel pump hum, alternator whistle)
        var frictionEnergy = 0f   // 3500Hz - 10000Hz (Belt squeal, bearing screech)

        for (k in 0 until numBins) {
            val freq = frequencies[k]
            val mag = magnitudes[k]
            when {
                freq in 20f..100f -> subBassEnergy += mag
                freq in 100f..400f -> lowRumbleEnergy += mag
                freq in 400f..1200f -> valvetrainEnergy += mag
                freq in 1200f..3500f -> highWhineEnergy += mag
                freq in 3500f..10000f -> frictionEnergy += mag
            }
        }

        val totalEnergy = (subBassEnergy + lowRumbleEnergy + valvetrainEnergy + highWhineEnergy + frictionEnergy).coerceAtLeast(0.001f)

        return FftResult(
            magnitudes = magnitudes,
            frequencies = frequencies,
            numBins = numBins,
            peakFrequencyHz = peakFrequencyHz,
            peakMagnitude = maxMagnitude,
            subBassEnergyRatio = subBassEnergy / totalEnergy,
            lowRumbleEnergyRatio = lowRumbleEnergy / totalEnergy,
            valvetrainEnergyRatio = valvetrainEnergy / totalEnergy,
            highWhineEnergyRatio = highWhineEnergy / totalEnergy,
            frictionEnergyRatio = frictionEnergy / totalEnergy
        )
    }
}

data class FftResult(
    val magnitudes: FloatArray,
    val frequencies: FloatArray,
    val numBins: Int,
    val peakFrequencyHz: Int,
    val peakMagnitude: Float,
    val subBassEnergyRatio: Float,
    val lowRumbleEnergyRatio: Float,
    val valvetrainEnergyRatio: Float,
    val highWhineEnergyRatio: Float,
    val frictionEnergyRatio: Float
)
