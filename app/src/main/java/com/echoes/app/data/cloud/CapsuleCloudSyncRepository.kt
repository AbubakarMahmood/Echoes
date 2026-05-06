package com.echoes.app.data.cloud

import android.content.Context
import com.echoes.app.data.auth.AuthRepository
import com.echoes.app.data.auth.AuthSession
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.SeedData
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.entity.UserEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.local.model.UnlockType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Handles explicit cloud backup and restore for capsule records.
 *
 * Capsule documents are written to `users/{firebaseUid}/capsules/{capsuleId}`
 * using Firestore's merge-set strategy so partial updates do not overwrite
 * fields managed by other clients. Restore downloads those same documents back
 * into Room after local app data is cleared. Local image files are not restored
 * because this version does not upload media bytes to Firebase Storage.
 */
class CapsuleCloudSyncRepository(context: Context) {

    private val appContext = context.applicationContext
    private val authRepository = AuthRepository(context)
    private val database = DatabaseProvider.getDatabase(appContext)

    suspend fun syncCapsules(records: List<CapsuleRecord>): CapsuleSyncResult {
        return withContext(Dispatchers.IO) {
            if (!authRepository.isFirebaseConfigured) {
                return@withContext CapsuleSyncResult(status = CapsuleSyncStatus.CONFIG_MISSING)
            }

            val session = authRepository.currentFirebaseSession()
                ?: return@withContext CapsuleSyncResult(status = CapsuleSyncStatus.SIGN_IN_REQUIRED)

            if (records.isEmpty()) {
                return@withContext CapsuleSyncResult(status = CapsuleSyncStatus.NO_LOCAL_CAPSULES)
            }

            val firestore = FirebaseFirestore.getInstance()
            val syncedAt = System.currentTimeMillis()
            val capsulesPath = "users/${session.userId}/capsules"

            records.forEach { record ->
                firestore.collection("users")
                    .document(session.userId)
                    .collection("capsules")
                    .document(record.capsule.capsuleId)
                    .set(record.toFirestoreMap(session, syncedAt), SetOptions.merge())
                    .awaitCompletion()
            }

            CapsuleSyncResult(
                status = CapsuleSyncStatus.SYNCED,
                attemptedCount = records.size,
                syncedCount = records.size,
                firestorePath = capsulesPath
            )
        }
    }

    suspend fun restoreCapsulesToRoom(): CapsuleRestoreResult {
        return withContext(Dispatchers.IO) {
            if (!authRepository.isFirebaseConfigured) {
                return@withContext CapsuleRestoreResult(status = CapsuleRestoreStatus.CONFIG_MISSING)
            }

            val session = authRepository.currentFirebaseSession()
                ?: return@withContext CapsuleRestoreResult(status = CapsuleRestoreStatus.SIGN_IN_REQUIRED)

            val firestore = FirebaseFirestore.getInstance()
            val capsulesPath = "users/${session.userId}/capsules"
            val snapshot = firestore.collection("users")
                .document(session.userId)
                .collection("capsules")
                .get()
                .awaitResult()

            if (snapshot.isEmpty) {
                return@withContext CapsuleRestoreResult(
                    status = CapsuleRestoreStatus.NO_REMOTE_CAPSULES,
                    firestorePath = capsulesPath
                )
            }

            val now = System.currentTimeMillis()
            val owner = database.userDao().getUserById(SeedData.LOCAL_USER_ID)
            database.userDao().upsertUser(session.toLocalUser(owner, now))

            var restoredCount = 0
            var missingImageCount = 0
            snapshot.documents.forEach { document ->
                val capsuleId = document.getString("capsuleId") ?: document.id
                val title = document.getString("title")?.takeIf { it.isNotBlank() } ?: return@forEach
                val storyText = document.getString("storyText") ?: ""
                val mediaType = document.getString("mediaType").toMediaType()
                val hasRemoteImage = mediaType == CapsuleMediaType.IMAGE &&
                    document.getBoolean("hasLocalImage") == true
                val unlockType = document.getString("unlockType").toUnlockType()
                val existingCondition = database.unlockConditionDao()
                    .getUnlockConditionForCapsule(capsuleId)
                val createdAt = document.getLong("createdAt") ?: now
                val updatedAt = document.getLong("updatedAt") ?: createdAt

                val capsule = CapsuleEntity(
                    capsuleId = capsuleId,
                    ownerId = SeedData.LOCAL_USER_ID,
                    title = title,
                    storyText = storyText,
                    mediaType = mediaType,
                    mediaLocalPath = null,
                    unlockType = unlockType,
                    isLocked = document.getBoolean("isLocked") ?: (unlockType != UnlockType.NONE),
                    isPublic = false,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
                val unlockCondition = UnlockConditionEntity(
                    conditionId = existingCondition?.conditionId ?: "$capsuleId-unlock",
                    capsuleId = capsuleId,
                    conditionType = unlockType,
                    unlockAt = document.getLong("unlockAt"),
                    latitude = document.getDouble("latitude"),
                    longitude = document.getDouble("longitude"),
                    radiusMeters = document.getLong("radiusMeters")?.toInt(),
                    satisfiedAt = document.getLong("satisfiedAt")
                )

                database.capsuleDao().upsertCapsule(capsule)
                database.unlockConditionDao().upsertUnlockCondition(unlockCondition)
                restoredCount += 1
                if (hasRemoteImage) missingImageCount += 1
            }

            val status = if (missingImageCount > 0) {
                CapsuleRestoreStatus.RESTORED_WITH_MISSING_IMAGES
            } else {
                CapsuleRestoreStatus.RESTORED
            }
            CapsuleRestoreResult(
                status = status,
                attemptedCount = snapshot.size(),
                restoredCount = restoredCount,
                missingImageCount = missingImageCount,
                firestorePath = capsulesPath
            )
        }
    }

    private fun CapsuleRecord.toFirestoreMap(session: AuthSession, syncedAt: Long): Map<String, Any?> {
        val capsuleMetadata = metadata
        return mapOf(
            "schemaVersion" to 1,
            "syncVisibility" to "owner_private",
            "capsuleId" to capsule.capsuleId,
            "ownerFirebaseUid" to session.userId,
            "ownerEmail" to session.email,
            "localOwnerId" to capsuleMetadata.ownerId,
            "ownerDisplayName" to capsuleMetadata.ownerDisplayName,
            "title" to capsule.title,
            "storyText" to capsule.storyText,
            "mediaType" to capsule.mediaType.name,
            "hasLocalImage" to !capsule.mediaLocalPath.isNullOrBlank(),
            "unlockType" to capsuleMetadata.unlockType.name,
            "isLocked" to capsuleMetadata.isLocked,
            "unlockAt" to capsuleMetadata.unlockAt,
            "latitude" to capsuleMetadata.latitude,
            "longitude" to capsuleMetadata.longitude,
            "radiusMeters" to capsuleMetadata.radiusMeters,
            "satisfiedAt" to capsuleMetadata.satisfiedAt,
            "createdAt" to capsuleMetadata.createdAt,
            "updatedAt" to capsuleMetadata.updatedAt,
            "syncedAt" to syncedAt
        )
    }

    private fun AuthSession.toLocalUser(existingUser: UserEntity?, now: Long): UserEntity {
        return UserEntity(
            userId = SeedData.LOCAL_USER_ID,
            email = email ?: existingUser?.email ?: "local@echoes.app",
            displayName = displayName ?: existingUser?.displayName ?: SeedData.LOCAL_USER_NAME,
            createdAt = existingUser?.createdAt ?: now,
            updatedAt = now
        )
    }

    private fun String?.toMediaType(): CapsuleMediaType {
        return this.toEnumOrDefault(CapsuleMediaType.TEXT)
    }

    private fun String?.toUnlockType(): UnlockType {
        return this.toEnumOrDefault(UnlockType.NONE)
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(defaultValue: T): T {
        return runCatching {
            enumValueOf<T>(orEmpty())
        }.getOrDefault(defaultValue)
    }

    private suspend fun Task<QuerySnapshot>.awaitResult(): QuerySnapshot {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (!continuation.isActive) return@addOnCompleteListener

                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Firestore restore task failed.")
                    )
                }
            }
        }
    }

    private suspend fun Task<*>.awaitCompletion() {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (!continuation.isActive) return@addOnCompleteListener

                if (task.isSuccessful) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Firestore sync task failed.")
                    )
                }
            }
        }
    }
}
