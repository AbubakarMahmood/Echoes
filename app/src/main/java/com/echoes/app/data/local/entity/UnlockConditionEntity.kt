package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.echoes.app.data.local.model.UnlockType

/**
 * Room entity storing the unlock condition for a single [CapsuleEntity].
 *
 * Each capsule has at most one unlock condition (enforced by a unique index on
 * [capsuleId]). The [conditionType] determines which nullable fields are
 * meaningful: [unlockAt] for date-based conditions and
 * [latitude]/[longitude]/[radiusMeters] for location-based conditions.
 *
 * When the condition is satisfied, [satisfiedAt] is set to the epoch millis of
 * the satisfaction moment by [com.echoes.app.domain.CapsuleUnlockRules].
 */
@Entity(
    tableName = "unlock_conditions",
    foreignKeys = [
        ForeignKey(
            entity = CapsuleEntity::class,
            parentColumns = ["capsuleId"],
            childColumns = ["capsuleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["capsuleId"], unique = true),
        Index("conditionType")
    ]
)
data class UnlockConditionEntity(
    @PrimaryKey val conditionId: String,
    val capsuleId: String,
    val conditionType: UnlockType = UnlockType.NONE,
    val unlockAt: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Int? = null,
    val eventCode: String? = null,
    val satisfiedAt: Long? = null
)
