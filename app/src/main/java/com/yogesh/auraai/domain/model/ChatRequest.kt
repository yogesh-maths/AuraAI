package com.yogesh.auraai.domain.model

data class ChatRequest(
    val conversationId: String,
    val messages: List<Message>,
)
