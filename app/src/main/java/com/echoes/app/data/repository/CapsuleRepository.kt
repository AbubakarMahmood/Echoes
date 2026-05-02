package com.echoes.app.data.repository

import android.content.Context
import android.net.Uri
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.SeedData
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.CapsuleImageStorage
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CameraCaptureTarget(
    val imagePath: String,
    val imageUri: Uri
)

class CapsuleRepository(context: Context) {

    private val appContext = context.applicationContext
    private val database = DatabaseProvider.getDatabase(appContext)

    suspend fun createCapsule(
        title: String,
        body: String,
        imagePath: String?,
        unlockAt: Long?
    ) {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val capsuleId = UUID.randomUUID().toString()
            val conditionId = UUID.randomUUID().toString()
            val hasFutureDateUnlock = unlockAt != null && unlockAt > now
            val unlockType = if (hasFutureDateUnlock) UnlockType.DATE else UnlockType.NONE
            val storedUnlockAt = if (hasFutureDateUnlock) unlockAt else null

            val capsule = CapsuleEntity(
                capsuleId = capsuleId,
                ownerId = SeedData.LOCAL_USER_ID,
                title = title,
                storyText = body,
                mediaType = if (imagePath.isNullOrBlank()) CapsuleMediaType.TEXT else CapsuleMediaType.IMAGE,
                mediaLocalPath = imagePath,
                unlockType = unlockType,
                isLocked = hasFutureDateUnlock,
                isPublic = false,
                createdAt = now,
                updatedAt = now
            )

            val unlockCondition = UnlockConditionEntity(
                conditionId = conditionId,
                capsuleId = capsuleId,
                conditionType = unlockType,
                unlockAt = storedUnlockAt
            )

            val existingUser = database.userDao().getUserById(SeedData.LOCAL_USER_ID)
            database.userDao().upsertUser(SeedData.localUserForWrite(existingUser, now))
            database.capsuleDao().upsertCapsule(capsule)
            database.unlockConditionDao().upsertUnlockCondition(unlockCondition)
        }
    }

    suspend fun getArchiveRecords(): List<CapsuleRecord> {
        return withContext(Dispatchers.IO) {
            database.capsuleDao()
                .getCapsuleRecordsForOwner(SeedData.LOCAL_USER_ID)
                .map { resolveTimeUnlockState(it) }
        }
    }

    suspend fun getCapsuleRecord(capsuleId: String): CapsuleRecord? {
        return withContext(Dispatchers.IO) {
            database.capsuleDao().getCapsuleRecord(capsuleId)?.let { resolveTimeUnlockState(it) }
        }
    }

    suspend fun updateCapsule(capsule: CapsuleEntity, title: String, body: String): CapsuleEntity {
        return withContext(Dispatchers.IO) {
            val updatedCapsule = capsule.copy(
                title = title,
                storyText = body,
                updatedAt = System.currentTimeMillis()
            )
            database.capsuleDao().upsertCapsule(updatedCapsule)
            updatedCapsule
        }
    }

    suspend fun deleteCapsule(capsule: CapsuleEntity) {
        withContext(Dispatchers.IO) {
            database.capsuleDao().deleteCapsule(capsule)
            if (capsule.mediaType == CapsuleMediaType.IMAGE) {
                CapsuleImageStorage.deleteStoredImage(capsule.mediaLocalPath)
            }
        }
    }

    suspend fun importImage(sourceUri: Uri): String {
        return withContext(Dispatchers.IO) {
            CapsuleImageStorage.importImageFromUri(appContext, sourceUri)
        }
    }

    fun createCameraCaptureTarget(): CameraCaptureTarget {
        val imageFile = CapsuleImageStorage.createCameraImageFile(appContext)
        return CameraCaptureTarget(
            imagePath = imageFile.absolutePath,
            imageUri = CapsuleImageStorage.createCameraImageUri(appContext, imageFile)
        )
    }

    fun deleteStoredImage(path: String?) {
        CapsuleImageStorage.deleteStoredImage(path)
    }

    private suspend fun resolveTimeUnlockState(record: CapsuleRecord): CapsuleRecord {
        val unlockCondition = record.unlockCondition ?: return record
        if (unlockCondition.conditionType != UnlockType.DATE || unlockCondition.unlockAt == null) {
            return record
        }

        val now = System.currentTimeMillis()
        val shouldBeLocked = unlockCondition.unlockAt > now
        val hasCorrectSatisfiedState = if (shouldBeLocked) {
            unlockCondition.satisfiedAt == null
        } else {
            unlockCondition.satisfiedAt != null
        }
        if (record.capsule.isLocked == shouldBeLocked && hasCorrectSatisfiedState) {
            return record
        }

        val updatedCapsule = record.capsule.copy(
            isLocked = shouldBeLocked
        )
        val updatedCondition = unlockCondition.copy(
            satisfiedAt = if (shouldBeLocked) null else unlockCondition.satisfiedAt ?: now
        )

        database.capsuleDao().upsertCapsule(updatedCapsule)
        database.unlockConditionDao().upsertUnlockCondition(updatedCondition)

        return record.copy(
            capsule = updatedCapsule,
            unlockCondition = updatedCondition
        )
    }
}
