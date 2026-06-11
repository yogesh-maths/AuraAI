package com.yogesh.auraai.domain.repository

import com.yogesh.auraai.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendUserMessage(conversationId: String, content: String): String
    suspend fun insertAssistantMessage(
        conversationId: String,
        content: String,
        remoteId: String? = null,
    ): String
    suspend fun retryFailedMessage(messageId: String)
    suspend fun deleteMessage(id: String)
    suspend fun getMessage(id: String): Message?
}
