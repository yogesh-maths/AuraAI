package com.yogesh.auraai.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map as pagingMap
import com.yogesh.auraai.core.common.truncate
import com.yogesh.auraai.data.local.dao.ConversationDao
import com.yogesh.auraai.data.local.entity.ConversationEntity
import com.yogesh.auraai.data.local.entity.ConversationWithPreview
import com.yogesh.auraai.data.mapper.toDomain
import com.yogesh.auraai.domain.model.Conversation
import com.yogesh.auraai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ConversationRepositoryImpl(
    private val conversationDao: ConversationDao,
) : ConversationRepository {

    override fun getConversations(searchQuery: String): Flow<PagingData<Conversation>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                conversationDao.getConversationsPaged(searchQuery.trim())
            },
        ).flow.map { pagingData ->
            pagingData.pagingMap { preview: ConversationWithPreview -> preview.toDomain() }
        }
    }

    override suspend fun createConversation(): String {
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        conversationDao.insert(
            ConversationEntity(
                id = id,
                title = "New Chat",
                createdAt = now,
                updatedAt = now,
            ),
        )
        return id
    }

    override suspend fun getConversation(id: String): Conversation? {
        return conversationDao.getWithPreview(id)?.toDomain()
    }

    override suspend fun updateTitle(id: String, title: String) {
        conversationDao.updateTitle(id, title, System.currentTimeMillis())
    }

    override suspend fun deleteConversation(id: String) {
        conversationDao.deleteById(id)
    }

    override suspend fun archiveConversation(id: String) {
        conversationDao.archive(id)
    }

    override suspend fun autoGenerateTitle(conversationId: String, firstMessage: String) {
        val conversation = conversationDao.getById(conversationId) ?: return
        if (conversation.title != "New Chat") return
        val title = firstMessage.trim().truncate(40).ifBlank { "New Chat" }
        conversationDao.updateTitle(conversationId, title, System.currentTimeMillis())
    }
}
