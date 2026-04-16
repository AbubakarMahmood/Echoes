package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.echoes.app.data.local.model.AffiliationType

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String,
    val affiliationType: AffiliationType = AffiliationType.STUDENT,
    val createdAt: Long,
    val updatedAt: Long = createdAt
)
