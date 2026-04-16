package com.echoes.app.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity

data class CapsuleWithUnlockCondition(
    @Embedded val capsule: CapsuleEntity,
    @Relation(
        parentColumn = "capsuleId",
        entityColumn = "capsuleId"
    )
    val unlockCondition: UnlockConditionEntity?
)
