package com.echoes.app.data.cloud

import android.content.Context
import com.echoes.app.data.auth.AuthRepository
import com.echoes.app.data.auth.AuthSession
import com.echoes.app.data.local.model.CapsuleRecord
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CapsuleCloudSyncRepository(context: Context) {

    private val authRepository = AuthRepository(context)

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
