package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.model.Component3DModel
import com.example.model.VehicleSystem

object HapticHelper {

    fun isComplexComponent(component: Component3DModel): Boolean {
        return component.difficulty.contains("Advanced", ignoreCase = true) ||
                component.difficulty.contains("Intermediate", ignoreCase = true) ||
                component.vertices.size >= 8 ||
                component.faces.size >= 6 ||
                component.torqueSpecs.isNotEmpty() ||
                component.system == VehicleSystem.ENGINE ||
                component.system == VehicleSystem.TRANSMISSION ||
                component.system == VehicleSystem.DRIVETRAIN_4WD ||
                component.system == VehicleSystem.BRAKES_CHASSIS
    }

    fun triggerComponentHaptic(
        context: Context,
        view: View?,
        haptic: HapticFeedback?,
        component: Component3DModel
    ) {
        if (isComplexComponent(component)) {
            triggerComplexComponentPulse(context, view, haptic)
        } else {
            triggerStandardComponentPulse(context, view, haptic)
        }
    }

    fun triggerComplexComponentPulse(
        context: Context,
        view: View?,
        haptic: HapticFeedback?
    ) {
        try {
            view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Double subtle tactile pulse waveform for complex mechanical assemblies
                    val timings = longArrayOf(0, 20, 35, 30)
                    val amplitudes = intArrayOf(0, 180, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40L)
                }
            } else {
                haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        } catch (_: Exception) {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun triggerStandardComponentPulse(
        context: Context,
        view: View?,
        haptic: HapticFeedback?
    ) {
        try {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(15L)
                }
            } else {
                haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        } catch (_: Exception) {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun triggerControlTick(context: Context, view: View?, haptic: HapticFeedback?) {
        try {
            view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(8, 100))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(10L)
                }
            } else {
                haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        } catch (_: Exception) {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun vibrateSuccess(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 30, 40, 50)
                    val amplitudes = intArrayOf(0, 200, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50L)
                }
            }
        } catch (_: Exception) {}
    }

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }
}
