package com.yogesh.auraai.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.yogesh.auraai.data.local.entity.ConversationEntity
import com.yogesh.auraai.data.local.entity.ConversationWithPreview
import com.yogesh.auraai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query(
        """
        SELECT c.*,
            (SELECT COUNT(*) FROM messages m WHERE m.conversationId = c.id) AS messageCount,
            (SELECT m.content FROM messages m WHERE m.conversationId = c.id
             ORDER BY m.createdAt DESC LIMIT 1) AS lastMessagePreview
        FROM conversations c
        WHERE c.isArchived = 0
            AND (:searchQuery = '' OR c.title LIKE '%' || :searchQuery || '%')
        ORDER BY c.updatedAt DESC
        """,
    )
    fun getConversationsPaged(searchQuery: String): PagingSource<Int, ConversationWithPreview>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getById(id: String): ConversationEntity?

    @Query(
        """
        SELECT c.*,
            (SELECT COUNT(*) FROM messages m WHERE m.conversationId = c.id) AS messageCount,
            (SELECT m.content FROM messages m WHERE m.conversationId = c.id
             ORDER BY m.createdAt DESC LIMIT 1) AS lastMessagePreview
        FROM conversations c
        WHERE c.id = :id
        """,
    )
    suspend fun getWithPreview(id: String): ConversationWithPreview?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: ConversationEntity)

    @Update
    suspend fun update(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE conversations SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: String)

    @Query("UPDATE conversations SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: String, title: String, updatedAt: Long)

    @Query("UPDATE conversations SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun touchUpdatedAt(id: String, updatedAt: Long)

    @Query("DELETE FROM conversations")
    suspend fun deleteAll()

    @Transaction
    suspend fun insertAndReturn(entity: ConversationEntity): ConversationEntity {
        insert(entity)
        return entity
    }
}
