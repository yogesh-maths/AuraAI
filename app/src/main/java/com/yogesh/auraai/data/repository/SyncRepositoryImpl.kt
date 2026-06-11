package com.yogesh.auraai.data.repository

import com.yogesh.auraai.core.common.Result
import com.yogesh.auraai.data.local.dao.ConversationDao
import com.yogesh.auraai.data.local.dao.MessageDao
import com.yogesh.auraai.data.local.preferences.UserPreferencesDataStore
import com.yogesh.auraai.data.mapper.toDomain
import com.yogesh.auraai.data.remote.AiApiService
import com.yogesh.auraai.data.remote.dto.ChatCompletionRequest
import com.yogesh.auraai.data.remote.dto.ChatMessageDto
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.repository.MessageRepository
import com.yogesh.auraai.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first

class SyncRepositoryImpl(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val messageRepository: MessageRepository,
    private val dataStore: UserPreferencesDataStore,
    private val aiApiService: AiApiService,
) : SyncRepository {

    companion object {
        private const val CONTEXT_MESSAGE_LIMIT = 20
    }

    override suspend fun syncPendingMessages(): Result<Int> {
        val pending = messageDao.getPendingMessages()
        if (pending.isEmpty()) return Result.Success(0)

        var syncedCount = 0
        val settings = dataStore.settings.first()
        if (settings.apiKey.isBlank()) {
            return Result.Error("API key is not configured")
        }

        val conversationIds = pending.map { it.conversationId }.distinct()
        for (conversationId in conversationIds) {
            when (val result = syncConversationInternal(conversationId, settings.modelName, settings.systemPrompt)) {
                is Result.Success -> syncedCount++
                is Result.Error -> return result
                Result.Loading -> Unit
            }
        }
        return Result.Success(syncedCount)
    }

    override suspend fun syncConversation(conversationId: String): Result<Unit> {
        val settings = dataStore.settings.first()
        if (settings.apiKey.isBlank()) {
            return Result.Error("API key is not configured")
        }
        return syncConversationInternal(conversationId, settings.modelName, settings.systemPrompt)
    }

    override suspend fun cancelSync(messageId: String) {
        messageDao.markFailed(messageId, "Sync cancelled")
    }

    private suspend fun syncConversationInternal(
        conversationId: String,
        modelName: String,
        systemPrompt: String,
    ): Result<Unit> {
        val pendingMessage = messageDao.getNextPendingForConversation(conversationId) ?: return Result.Success(Unit)

        messageDao.markSyncing(listOf(pendingMessage.id))

        return try {
            val syncedHistory = messageDao.getSyncedMessagesForConversation(conversationId)
                .map { it.toDomain() }
                .takeLast(CONTEXT_MESSAGE_LIMIT)

            val apiMessages = buildList {
                add(ChatMessageDto(role = MessageRole.SYSTEM.name.lowercase(), content = systemPrompt))
                syncedHistory.forEach { message ->
                    add(
                        ChatMessageDto(
                            role = message.role.name.lowercase(),
                            content = message.content,
                        ),
                    )
                }
                add(
                    ChatMessageDto(
                        role = MessageRole.USER.name.lowercase(),
                        content = pendingMessage.content,
                    ),
                )
            }

            val response = aiApiService.createChatCompletion(
                ChatCompletionRequest(
                    model = modelName,
                    messages = apiMessages,
                    stream = false,
                ),
            )

            val assistantContent = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Empty response from AI")

            messageDao.markSynced(pendingMessage.id)
            messageRepository.insertAssistantMessage(
                conversationId = conversationId,
                content = assistantContent,
                remoteId = response.id,
            )

            val nextPending = messageDao.getNextPendingForConversation(conversationId)
            if (nextPending != null) {
                syncConversationInternal(conversationId, modelName, systemPrompt)
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            messageDao.markFailed(pendingMessage.id, e.message ?: "Sync failed")
            Result.Error(e.message ?: "Sync failed", e)
        }
    }
}
