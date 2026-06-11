package com.yogesh.auraai.domain.repository

import androidx.paging.PagingData
import com.yogesh.auraai.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(searchQuery: String = ""): Flow<PagingData<Conversation>>
    suspend fun createConversation(): String
    suspend fun getConversation(id: String): Conversation?
    suspend fun updateTitle(id: String, title: String)
    suspend fun deleteConversation(id: String)
    suspend fun archiveConversation(id: String)
    suspend fun autoGenerateTitle(conversationId: String, firstMessage: String)
}
