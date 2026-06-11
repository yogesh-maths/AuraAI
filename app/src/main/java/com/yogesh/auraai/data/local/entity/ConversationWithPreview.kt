package com.yogesh.auraai.data.local.entity

import androidx.room.Embedded

data class ConversationWithPreview(
    @Embedded val conversation: ConversationEntity,
    val messageCount: Int,
    val lastMessagePreview: String?,
)
