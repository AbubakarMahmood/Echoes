package com.echoes.app.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.entity.UserEntity

data class CapsuleRecord(
    @Embedded val capsule: CapsuleEntity,
    @Relation(
        parentColumn = "ownerId",
        entityColumn = "userId"
    )
    val owner: UserEntity?,
    @Relation(
        parentColumn = "capsuleId",
        entityColumn = "capsuleId"
    )
    val unlockCondition: UnlockConditionEntity?
) {
    val metadata: CapsuleMetadata
        get() = CapsuleMetadata(
            ownerId = capsule.ownerId,
            ownerDisplayName = owner?.displayName,
            createdAt = capsule.createdAt,
            updatedAt = capsule.updatedAt,
            unlockType = unlockCondition?.conditionType ?: capsule.unlockType,
            isLocked = capsule.isLocked
        )
}
