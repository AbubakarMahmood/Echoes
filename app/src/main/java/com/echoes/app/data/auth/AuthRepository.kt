package com.echoes.app.data.auth

import android.content.Context
import com.echoes.app.data.local.SeedData
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MissingFirebaseConfigurationException : IllegalStateException(
    "Firebase is not configured. Add app/google-services.json for this package."
)

class AuthRepository(context: Context) {

    private val appContext = context.applicationContext

    val isFirebaseConfigured: Boolean
        get() = FirebaseApp.getApps(appContext).isNotEmpty()

    fun currentFirebaseSession(): AuthSession? {
        if (!isFirebaseConfigured) return null

        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return AuthSession(
            userId = user.uid,
            email = user.email,
            displayName = user.displayName,
            isFirebaseBacked = true
        )
    }

    fun localSession(): AuthSession {
        return AuthSession(
            userId = SeedData.LOCAL_USER_ID,
            email = "local@echoes.app",
            displayName = SeedData.LOCAL_USER_NAME,
            isFirebaseBacked = false
        )
    }

    suspend fun register(email: String, password: String, displayName: String): AuthSession {
        return withContext(Dispatchers.IO) {
            val authResult = requireFirebaseAuth()
                .createUserWithEmailAndPassword(email, password)
                .awaitResult()
            val user = authResult.user
                ?: throw IllegalStateException("Firebase registration succeeded without a user.")

            if (displayName.isNotBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).awaitCompletion()
            }

            AuthSession(
                userId = user.uid,
                email = user.email,
                displayName = displayName.ifBlank { user.displayName },
                isFirebaseBacked = true
            )
        }
    }

    suspend fun signIn(email: String, password: String): AuthSession {
        return withContext(Dispatchers.IO) {
            val authResult = requireFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                .awaitResult()
            val user = authResult.user
                ?: throw IllegalStateException("Firebase sign-in succeeded without a user.")

            AuthSession(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName,
                isFirebaseBacked = true
            )
        }
    }

    private fun requireFirebaseAuth(): FirebaseAuth {
        if (!isFirebaseConfigured) {
            throw MissingFirebaseConfigurationException()
        }

        return FirebaseAuth.getInstance()
    }

    private suspend fun <T> Task<T>.awaitResult(): T {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Firebase task failed.")
                    )
                }
            }
        }
    }

    private suspend fun Task<*>.awaitCompletion() {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Firebase task failed.")
                    )
                }
            }
        }
    }
}
