package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    data class Processing(val text: String) : VoiceState()
    data class Success(val text: String, val commandFeedback: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

class VoiceCommandManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizerAvailable = SpeechRecognizer.isRecognitionAvailable(context)

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _lastRecognizedText = MutableStateFlow("")
    val lastRecognizedText: StateFlow<String> = _lastRecognizedText.asStateFlow()

    private val _lastFeedback = MutableStateFlow<String?>(null)
    val lastFeedback: StateFlow<String?> = _lastFeedback.asStateFlow()

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (isRecognizerAvailable) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _voiceState.value = VoiceState.Listening
                        }

                        override fun onBeginningOfSpeech() {
                            _voiceState.value = VoiceState.Listening
                        }

                        override fun onRmsChanged(rmsdB: Float) {}

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _voiceState.value = VoiceState.Processing("Processing audio...")
                        }

                        override fun onError(error: Int) {
                            val errStr = when (error) {
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission denied"
                                else -> "Recognition error ($error)"
                            }
                            Log.w("VoiceCommandManager", "Speech error: $errStr")
                            _voiceState.value = VoiceState.Error(errStr)
                        }

                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val spokenText = matches?.firstOrNull() ?: ""
                            _lastRecognizedText.value = spokenText
                            _voiceState.value = VoiceState.Processing(spokenText)
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            matches?.firstOrNull()?.let {
                                _lastRecognizedText.value = it
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            } catch (e: Exception) {
                Log.e("VoiceCommandManager", "Failed to create SpeechRecognizer", e)
                isRecognizerAvailable = false
            }
        }
    }

    fun startListening() {
        if (!isRecognizerAvailable || speechRecognizer == null) {
            initRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for Sport Trac command...")
        }

        try {
            _voiceState.value = VoiceState.Listening
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _voiceState.value = VoiceState.Error("Mic unavailable: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _voiceState.value = VoiceState.Idle
        } catch (e: Exception) {
            _voiceState.value = VoiceState.Idle
        }
    }

    fun setCommandExecuted(recognized: String, feedback: String) {
        _lastRecognizedText.value = recognized
        _lastFeedback.value = feedback
        _voiceState.value = VoiceState.Success(recognized, feedback)
    }

    fun resetState() {
        _voiceState.value = VoiceState.Idle
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
    }
}
