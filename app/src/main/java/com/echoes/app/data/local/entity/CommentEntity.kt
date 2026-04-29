package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    indices = [Index("capsuleId")]
)
data class CommentEntity(
    @PrimaryKey val commentId: String,
    val capsuleId: String,
    val userId: String,
    val authorDisplayName: String,
    val body: String,
    val createdAt: Long
)
