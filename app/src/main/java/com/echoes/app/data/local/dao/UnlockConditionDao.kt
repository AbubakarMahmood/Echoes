package com.echoes.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.echoes.app.data.local.entity.UnlockConditionEntity

@Dao
interface UnlockConditionDao {

    @Upsert
    suspend fun upsertUnlockCondition(unlockCondition: UnlockConditionEntity)

    @Query("SELECT * FROM unlock_conditions WHERE conditionId = :conditionId LIMIT 1")
    suspend fun getUnlockConditionById(conditionId: String): UnlockConditionEntity?

    @Query("SELECT * FROM unlock_conditions WHERE capsuleId = :capsuleId LIMIT 1")
    suspend fun getUnlockConditionForCapsule(capsuleId: String): UnlockConditionEntity?

    @Delete
    suspend fun deleteUnlockCondition(unlockCondition: UnlockConditionEntity)
}
