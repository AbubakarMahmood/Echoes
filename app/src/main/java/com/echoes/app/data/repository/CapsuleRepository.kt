package com.echoes.app.data.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.SeedData
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.CommentEntity
import com.echoes.app.data.local.entity.FavoriteEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.local.model.CapsuleSocialState
import com.echoes.app.data.local.model.LocationUnlockTarget
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.CapsuleImageStorage
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CameraCaptureTarget(
    val imagePath: String,
    val imageUri: Uri
)

data class LocationUnlockCheckResult(
    val record: CapsuleRecord?,
    val didUnlock: Boolean,
    val isWithinRange: Boolean,
    val distanceMeters: Float?
)

class CapsuleRepository(context: Context) {

    private val appContext = context.applicationContext
    private val database = DatabaseProvider.getDatabase(appContext)

    suspend fun createCapsule(
        title: String,
        body: String,
        imagePath: String?,
        unlockAt: Long?,
        locationUnlockTarget: LocationUnlockTarget?
    ) {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val capsuleId = UUID.randomUUID().toString()
            val conditionId = UUID.randomUUID().toString()
            val hasFutureDateUnlock = unlockAt != null && unlockAt > now
            val hasLocationUnlock = locationUnlockTarget != null
            val unlockType = when {
                hasLocationUnlock -> UnlockType.LOCATION
                hasFutureDateUnlock -> UnlockType.DATE
                else -> UnlockType.NONE
            }
            val storedUnlockAt = if (unlockType == UnlockType.DATE) unlockAt else null

            val capsule = CapsuleEntity(
                capsuleId = capsuleId,
                ownerId = SeedData.LOCAL_USER_ID,
                title = title,
                storyText = body,
                mediaType = if (imagePath.isNullOrBlank()) CapsuleMediaType.TEXT else CapsuleMediaType.IMAGE,
                mediaLocalPath = imagePath,
                unlockType = unlockType,
                isLocked = hasFutureDateUnlock || hasLocationUnlock,
                isPublic = false,
                createdAt = now,
                updatedAt = now
            )

            val unlockCondition = UnlockConditionEntity(
                conditionId = conditionId,
                capsuleId = capsuleId,
                conditionType = unlockType,
                unlockAt = storedUnlockAt,
                latitude = locationUnlockTarget?.latitude,
                longitude = locationUnlockTarget?.longitude,
                radiusMeters = locationUnlockTarget?.radiusMeters
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

    suspend fun checkLocationUnlock(
        capsuleId: String,
        currentLatitude: Double,
        currentLongitude: Double
    ): LocationUnlockCheckResult {
        return withContext(Dispatchers.IO) {
            val record = database.capsuleDao().getCapsuleRecord(capsuleId)?.let { resolveTimeUnlockState(it) }
                ?: return@withContext LocationUnlockCheckResult(
                    record = null,
                    didUnlock = false,
                    isWithinRange = false,
                    distanceMeters = null
                )
            val unlockCondition = record.unlockCondition
            if (unlockCondition?.conditionType != UnlockType.LOCATION || !record.capsule.isLocked) {
                return@withContext LocationUnlockCheckResult(
                    record = record,
                    didUnlock = false,
                    isWithinRange = true,
                    distanceMeters = null
                )
            }

            val targetLatitude = unlockCondition.latitude
            val targetLongitude = unlockCondition.longitude
            val radiusMeters = unlockCondition.radiusMeters
            if (targetLatitude == null || targetLongitude == null || radiusMeters == null) {
                return@withContext LocationUnlockCheckResult(
                    record = record,
                    didUnlock = false,
                    isWithinRange = false,
                    distanceMeters = null
                )
            }

            val distanceResults = FloatArray(1)
            Location.distanceBetween(
                currentLatitude,
                currentLongitude,
                targetLatitude,
                targetLongitude,
                distanceResults
            )
            val distanceMeters = distanceResults.first()
            val isWithinRange = distanceMeters <= radiusMeters
            if (!isWithinRange) {
                return@withContext LocationUnlockCheckResult(
                    record = record,
                    didUnlock = false,
                    isWithinRange = false,
                    distanceMeters = distanceMeters
                )
            }

            val updatedCapsule = record.capsule.copy(isLocked = false)
            val updatedCondition = unlockCondition.copy(satisfiedAt = System.currentTimeMillis())
            database.capsuleDao().upsertCapsule(updatedCapsule)
            database.unlockConditionDao().upsertUnlockCondition(updatedCondition)

            LocationUnlockCheckResult(
                record = record.copy(
                    capsule = updatedCapsule,
                    unlockCondition = updatedCondition
                ),
                didUnlock = true,
                isWithinRange = true,
                distanceMeters = distanceMeters
            )
        }
    }

    suspend fun getDiscoveryRecords(): List<CapsuleRecord> {
        return withContext(Dispatchers.IO) {
            database.capsuleDao()
                .getAllCapsuleRecords()
                .map { resolveTimeUnlockState(it) }
                .filter { !it.metadata.isLocked }
        }
    }

    suspend fun getCapsuleSocialState(capsuleId: String): CapsuleSocialState {
        return withContext(Dispatchers.IO) {
            CapsuleSocialState(
                isFavorite = database.favoriteDao()
                    .favoriteCount(capsuleId, SeedData.LOCAL_USER_ID) > 0,
                comments = database.commentDao().getCommentsForCapsule(capsuleId)
            )
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
            database.favoriteDao().deleteFavoritesForCapsule(capsule.capsuleId)
            database.commentDao().deleteCommentsForCapsule(capsule.capsuleId)
            database.capsuleDao().deleteCapsule(capsule)
            if (capsule.mediaType == CapsuleMediaType.IMAGE) {
                CapsuleImageStorage.deleteStoredImage(capsule.mediaLocalPath)
            }
        }
    }

    suspend fun setFavorite(capsuleId: String, shouldFavorite: Boolean): CapsuleSocialState {
        return withContext(Dispatchers.IO) {
            if (shouldFavorite) {
                database.favoriteDao().upsertFavorite(
                    FavoriteEntity(
                        capsuleId = capsuleId,
                        userId = SeedData.LOCAL_USER_ID,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                database.favoriteDao().deleteFavorite(capsuleId, SeedData.LOCAL_USER_ID)
            }

            CapsuleSocialState(
                isFavorite = database.favoriteDao()
                    .favoriteCount(capsuleId, SeedData.LOCAL_USER_ID) > 0,
                comments = database.commentDao().getCommentsForCapsule(capsuleId)
            )
        }
    }

    suspend fun addComment(capsuleId: String, body: String): CapsuleSocialState {
        return withContext(Dispatchers.IO) {
            database.commentDao().upsertComment(
                CommentEntity(
                    commentId = UUID.randomUUID().toString(),
                    capsuleId = capsuleId,
                    userId = SeedData.LOCAL_USER_ID,
                    authorDisplayName = SeedData.LOCAL_USER_NAME,
                    body = body,
                    createdAt = System.currentTimeMillis()
                )
            )

            CapsuleSocialState(
                isFavorite = database.favoriteDao()
                    .favoriteCount(capsuleId, SeedData.LOCAL_USER_ID) > 0,
                comments = database.commentDao().getCommentsForCapsule(capsuleId)
            )
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
