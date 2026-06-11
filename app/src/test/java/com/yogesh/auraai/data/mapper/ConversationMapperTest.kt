package com.yogesh.auraai.data.mapper

import com.yogesh.auraai.data.local.entity.ConversationEntity
import com.yogesh.auraai.data.local.entity.ConversationWithPreview
import com.yogesh.auraai.data.local.entity.MessageEntity
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationMapperTest {

    @Test
    fun conversationWithPreview_toDomain_mapsAllFields() {
        val entity = ConversationEntity(
            id = "conv-1",
            title = "Test Chat",
            createdAt = 1000L,
            updatedAt = 2000L,
        )
        val withPreview = ConversationWithPreview(
            conversation = entity,
            messageCount = 3,
            lastMessagePreview = "Hello",
        )

        val domain = withPreview.toDomain()

        assertEquals("conv-1", domain.id)
        assertEquals("Test Chat", domain.title)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
        assertEquals(3, domain.messageCount)
        assertEquals("Hello", domain.lastMessagePreview)
    }

    @Test
    fun messageEntity_toDomain_mapsSyncStatusAndRole() {
        val entity = MessageEntity(
            id = "msg-1",
            conversationId = "conv-1",
            role = MessageRole.USER.name,
            content = "Hi",
            createdAt = 500L,
            syncStatus = SyncStatus.PENDING.name,
            retryCount = 1,
        )

        val domain = entity.toDomain()

        assertEquals(MessageRole.USER, domain.role)
        assertEquals(SyncStatus.PENDING, domain.syncStatus)
        assertEquals(1, domain.retryCount)
    }
}
