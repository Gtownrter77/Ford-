package com.example.model

import java.util.UUID

enum class ChatSender {
    USER,
    GEMINI_MECHANIC
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: ChatSender,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val suggestedComponentId: String? = null,
    val suggestedComponentName: String? = null,
    val confidenceRating: String? = null,
    val urgencyLevel: String? = null,
    val isError: Boolean = false
)
