package com.echoes.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.echoes.app.data.local.entity.CommentEntity

@Dao
interface CommentDao {

    @Upsert
    suspend fun upsertComment(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE capsuleId = :capsuleId ORDER BY createdAt ASC")
    suspend fun getCommentsForCapsule(capsuleId: String): List<CommentEntity>

    @Query("DELETE FROM comments WHERE capsuleId = :capsuleId")
    suspend fun deleteCommentsForCapsule(capsuleId: String)
}
