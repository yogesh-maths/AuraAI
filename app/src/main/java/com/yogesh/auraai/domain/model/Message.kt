package com.yogesh.auraai.domain.model

data class Message(
    val id: String,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val createdAt: Long,
    val syncStatus: SyncStatus,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
)
