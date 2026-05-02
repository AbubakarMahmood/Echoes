package com.echoes.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.echoes.app.data.local.model.UnlockType

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
