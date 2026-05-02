package com.echoes.app.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["capsuleId", "userId"]
)
data class FavoriteEntity(
    val capsuleId: String,
    val userId: String,
    val createdAt: Long
)
