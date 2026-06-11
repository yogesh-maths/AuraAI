package com.yogesh.auraai.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yogesh.auraai.data.local.AuraDatabase
import com.yogesh.auraai.data.local.entity.ConversationEntity
import com.yogesh.auraai.data.local.entity.MessageEntity
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageDaoTest {

    private lateinit var database: AuraDatabase
    private lateinit var messageDao: MessageDao
    private lateinit var conversationDao: ConversationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AuraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        messageDao = database.messageDao()
        conversationDao = database.conversationDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun observeMessages_returnsMessagesInOrder() = runTest {
        conversationDao.insert(
            ConversationEntity(
                id = "conv-1",
                title = "Test",
                createdAt = 1L,
                updatedAt = 1L,
            ),
        )
        messageDao.insert(
            MessageEntity(
                id = "m1",
                conversationId = "conv-1",
                role = MessageRole.USER.name,
                content = "First",
                createdAt = 1L,
                syncStatus = SyncStatus.SYNCED.name,
            ),
        )
        messageDao.insert(
            MessageEntity(
                id = "m2",
                conversationId = "conv-1",
                role = MessageRole.ASSISTANT.name,
                content = "Second",
                createdAt = 2L,
                syncStatus = SyncStatus.SYNCED.name,
            ),
        )

        val messages = messageDao.observeMessages("conv-1").first()

        assertEquals(2, messages.size)
        assertEquals("First", messages[0].content)
        assertEquals("Second", messages[1].content)
    }

    @Test
    fun getPendingMessages_returnsPendingUserMessages() = runTest {
        conversationDao.insert(
            ConversationEntity(
                id = "conv-1",
                title = "Test",
                createdAt = 1L,
                updatedAt = 1L,
            ),
        )
        messageDao.insert(
            MessageEntity(
                id = "m1",
                conversationId = "conv-1",
                role = MessageRole.USER.name,
                content = "Pending",
                createdAt = 1L,
                syncStatus = SyncStatus.PENDING.name,
            ),
        )

        val pending = messageDao.getPendingMessages()

        assertEquals(1, pending.size)
        assertTrue(pending.first().syncStatus == SyncStatus.PENDING.name)
    }
}
