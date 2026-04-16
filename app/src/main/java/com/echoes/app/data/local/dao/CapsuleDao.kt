package com.echoes.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.model.CapsuleWithUnlockCondition

@Dao
interface CapsuleDao {

    @Upsert
    suspend fun upsertCapsule(capsule: CapsuleEntity)

    @Query("SELECT * FROM capsules WHERE capsuleId = :capsuleId LIMIT 1")
    suspend fun getCapsuleById(capsuleId: String): CapsuleEntity?

    @Query("SELECT * FROM capsules WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    suspend fun getCapsulesForOwner(ownerId: String): List<CapsuleEntity>

    @Query("SELECT * FROM capsules ORDER BY createdAt DESC")
    suspend fun getAllCapsules(): List<CapsuleEntity>

    @Transaction
    @Query("SELECT * FROM capsules WHERE capsuleId = :capsuleId LIMIT 1")
    suspend fun getCapsuleWithUnlockCondition(capsuleId: String): CapsuleWithUnlockCondition?

    @Transaction
    @Query("SELECT * FROM capsules WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    suspend fun getCapsulesWithUnlockConditionsForOwner(ownerId: String): List<CapsuleWithUnlockCondition>

    @Delete
    suspend fun deleteCapsule(capsule: CapsuleEntity)
}
