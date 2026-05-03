package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.UnlockType

/**
 * Room entity representing a single time capsule in the local SQLite database.
 *
 * Each capsule belongs to an [ownerId] (foreign key to [UserEntity]) and stores
 * the user's story text together with optional image media. The [isLocked] flag
 * determines whether the capsule's content is visible; unlock evaluation is
 * handled by [com.echoes.app.domain.CapsuleUnlockRules] based on the companion
 * [com.echoes.app.data.local.entity.UnlockConditionEntity].
 *
 * Indices on [ownerId], [unlockType], and [isLocked] accelerate the most common
 * query patterns: owner-scoped archive retrieval and lock-status filtering.
 */
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
    /** Universally unique identifier for this capsule, generated at creation time. */
    @PrimaryKey val capsuleId: String,
    /** Foreign key referencing [UserEntity.userId]. */
    val ownerId: String,
    /** User-supplied capsule title (3–80 characters after trimming). */
    val title: String,
    /** User-supplied story body text (10–2000 characters after trimming). */
    val storyText: String,
    /** Content type: TEXT for text-only capsules, IMAGE when a photo is attached. */
    val mediaType: CapsuleMediaType = CapsuleMediaType.TEXT,
    /** Absolute path to the locally stored image file, or null for text-only capsules. */
    val mediaLocalPath: String? = null,
    /** High-level unlock strategy copied from the related [UnlockConditionEntity]. */
    val unlockType: UnlockType = UnlockType.NONE,
    /** True while the capsule's unlock condition has not yet been satisfied. */
    val isLocked: Boolean = true,
    /** Reserved for future community visibility; currently always false. */
    val isPublic: Boolean = false,
    /** Epoch millis when this capsule was first created. */
    val createdAt: Long,
    /** Epoch millis when this capsule was last modified. */
    val updatedAt: Long = createdAt
)
