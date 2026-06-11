package com.yogesh.auraai.domain.model

data class ChatResponse(
    val content: String,
    val model: String,
    val tokenUsage: Int? = null,
)
