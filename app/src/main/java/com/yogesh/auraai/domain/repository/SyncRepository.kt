package com.yogesh.auraai.domain.repository

import com.yogesh.auraai.core.common.Result

interface SyncRepository {
    suspend fun syncPendingMessages(): Result<Int>
    suspend fun syncConversation(conversationId: String): Result<Unit>
    suspend fun cancelSync(messageId: String)
}
