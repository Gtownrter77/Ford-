package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Standalone feature addition: local, opt-in mentor narration for the repair realm.
 *
 * This class does not read preferences, open Room, access network services, retain personal
 * data, or initialize at application start. Instantiate it only after the listener taps
 * "Listen to Mentor" inside [InteractiveRepairViewer].
 */
interface MentorAudioPlayer {
    fun speak(partName: String, script: String)
    fun stop()
    fun release()
}

/**
 * Android TTS implementation used as a deliberately local stub until a licensed mentor voice
 * recording or approved voice service is supplied. Android's installed TTS voice is used; no
 * audio is uploaded and no recording is made.
 */
class AndroidTtsMentorAudioPlayer(context: Context) : MentorAudioPlayer, TextToSpeech.OnInitListener {
    private var isReady = false
    private var pendingSpeech: String? = null
    private val textToSpeech = TextToSpeech(context.applicationContext, this)

    override fun onInit(status: Int) {
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            textToSpeech.language = Locale.US
            textToSpeech.setSpeechRate(0.92f)
            pendingSpeech?.let { narration -> speakNow(narration) }
            pendingSpeech = null
        }
    }

    override fun speak(partName: String, script: String) {
        val narration = "Mentor guidance for $partName. $script"
        if (isReady) {
            speakNow(narration)
        } else {
            pendingSpeech = narration
        }
    }

    override fun stop() {
        textToSpeech.stop()
    }

    override fun release() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun speakNow(narration: String) {
        textToSpeech.speak(narration, TextToSpeech.QUEUE_FLUSH, null, "repair_realm_mentor")
    }
}
