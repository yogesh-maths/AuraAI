package com.yogesh.auraai.data.mapper

import com.yogesh.auraai.data.local.entity.ConversationEntity
import com.yogesh.auraai.data.local.entity.ConversationWithPreview
import com.yogesh.auraai.data.local.entity.MessageEntity
import com.yogesh.auraai.domain.model.Conversation
import com.yogesh.auraai.domain.model.Message
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus

fun ConversationWithPreview.toDomain(): Conversation = Conversation(
    id = conversation.id,
    title = conversation.title,
    createdAt = conversation.createdAt,
    updatedAt = conversation.updatedAt,
    messageCount = messageCount,
    lastMessagePreview = lastMessagePreview,
)

fun ConversationEntity.toDomain(messageCount: Int = 0, lastMessagePreview: String? = null): Conversation =
    Conversation(
        id = id,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        messageCount = messageCount,
        lastMessagePreview = lastMessagePreview,
    )

fun MessageEntity.toDomain(): Message = Message(
    id = id,
    conversationId = conversationId,
    role = MessageRole.valueOf(role),
    content = content,
    createdAt = createdAt,
    syncStatus = SyncStatus.valueOf(syncStatus),
    errorMessage = errorMessage,
    retryCount = retryCount,
)

fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    conversationId = conversationId,
    role = role.name,
    content = content,
    createdAt = createdAt,
    syncStatus = syncStatus.name,
    errorMessage = errorMessage,
    retryCount = retryCount,
)
