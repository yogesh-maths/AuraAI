package com.yogesh.auraai.data.repository

import com.yogesh.auraai.data.local.dao.ConversationDao
import com.yogesh.auraai.data.local.dao.MessageDao
import com.yogesh.auraai.data.local.entity.MessageEntity
import com.yogesh.auraai.data.mapper.toDomain
import com.yogesh.auraai.domain.model.Message
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus
import com.yogesh.auraai.domain.repository.ConversationRepository
import com.yogesh.auraai.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val conversationRepository: ConversationRepository,
    private val syncScheduler: SyncScheduler,
) : MessageRepository {
    override suspend fun markAsSynced(messageId: String) {
        messageDao.markAsSynced(messageId)
    }
    override fun observeMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.observeMessages(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendUserMessage(conversationId: String, content: String): String {
        val trimmed = content.trim()
        require(trimmed.isNotEmpty()) { "Message cannot be empty" }

        val now = System.currentTimeMillis()
        val messageId = UUID.randomUUID().toString()
        val entity = MessageEntity(
            id = messageId,
            conversationId = conversationId,
            role = MessageRole.USER.name,
            content = trimmed,
            createdAt = now,
            syncStatus = SyncStatus.PENDING.name,
        )
        messageDao.insert(entity)
        conversationDao.touchUpdatedAt(conversationId, now)

        val userMessageCount = messageDao.countUserMessages(conversationId)
        if (userMessageCount == 1) {
            conversationRepository.autoGenerateTitle(conversationId, trimmed)
        }

        syncScheduler.enqueueSync()
        return messageId
    }

    override suspend fun insertAssistantMessage(
        conversationId: String,
        content: String,
        remoteId: String?,
    ): String {
        val now = System.currentTimeMillis()
        val messageId = UUID.randomUUID().toString()
        messageDao.insert(
            MessageEntity(
                id = messageId,
                conversationId = conversationId,
                role = MessageRole.ASSISTANT.name,
                content = content,
                createdAt = now,
                syncStatus = SyncStatus.SYNCED.name,
                remoteId = remoteId,
            ),
        )
        conversationDao.touchUpdatedAt(conversationId, now)
        return messageId
    }

    override suspend fun retryFailedMessage(messageId: String) {
        messageDao.resetToPending(messageId)
        syncScheduler.enqueueSync()
    }

    override suspend fun deleteMessage(id: String) {
        messageDao.deleteById(id)
    }

    override suspend fun getMessage(id: String): Message? {
        return messageDao.getById(id)?.toDomain()
    }
}

interface SyncScheduler {
    fun enqueueSync()
}
