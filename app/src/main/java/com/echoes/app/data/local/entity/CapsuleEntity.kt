package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.UnlockType

@Entity(
    tableName = "capsules",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ownerId"),
        Index("unlockType"),
        Index("isLocked")
    ]
)
data class CapsuleEntity(
    @PrimaryKey val capsuleId: String,
    val ownerId: String,
    val title: String,
    val storyText: String,
    val mediaType: CapsuleMediaType = CapsuleMediaType.TEXT,
    val mediaLocalPath: String? = null,
    val unlockType: UnlockType = UnlockType.NONE,
    val isLocked: Boolean = true,
    val isPublic: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long = createdAt
)
