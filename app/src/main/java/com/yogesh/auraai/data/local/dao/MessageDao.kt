package com.yogesh.auraai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yogesh.auraai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    companion object {
        const val MAX_RETRY_COUNT = 5
    }

    @Query(
        """
        SELECT * FROM messages
        WHERE conversationId = :conversationId
        ORDER BY createdAt ASC
        """,
    )
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getById(id: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Update
    suspend fun update(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @Query(
        """
        SELECT * FROM messages
        WHERE syncStatus IN ('PENDING', 'FAILED')
            AND role = 'USER'
            AND retryCount < :maxRetry
        ORDER BY createdAt ASC
        """,
    )
    suspend fun getPendingMessages(maxRetry: Int = MAX_RETRY_COUNT): List<MessageEntity>

    @Query(
        """
        SELECT * FROM messages
        WHERE conversationId = :conversationId
            AND syncStatus IN ('PENDING', 'FAILED')
            AND role = 'USER'
            AND retryCount < :maxRetry
        ORDER BY createdAt ASC
        LIMIT 1
        """,
    )
    suspend fun getNextPendingForConversation(
        conversationId: String,
        maxRetry: Int = MAX_RETRY_COUNT,
    ): MessageEntity?

    @Query(
        """
        SELECT * FROM messages
        WHERE conversationId = :conversationId
            AND syncStatus = 'SYNCED'
        ORDER BY createdAt ASC
        """,
    )
    suspend fun getSyncedMessagesForConversation(conversationId: String): List<MessageEntity>

    @Query("UPDATE messages SET syncStatus = 'SYNCING' WHERE id IN (:ids)")
    suspend fun markSyncing(ids: List<String>)

    @Query(
        """
        UPDATE messages
        SET syncStatus = 'SYNCED', errorMessage = NULL
        WHERE id = :id
        """,
    )
    suspend fun markSynced(id: String)

    @Query(
        """
        UPDATE messages
        SET syncStatus = 'FAILED', errorMessage = :error, retryCount = retryCount + 1
        WHERE id = :id
        """,
    )
    suspend fun markFailed(id: String, error: String)

    @Query(
        """
        UPDATE messages
        SET syncStatus = 'PENDING', errorMessage = NULL
        WHERE id = :id
        """,
    )
    suspend fun resetToPending(id: String)

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND role = 'USER'")
    suspend fun countUserMessages(conversationId: String): Int

    @Query(
        """
    UPDATE messages
    SET syncStatus = 'SYNCED'
    WHERE id = :id
    """
    )
    suspend fun markAsSynced(id: String)
}