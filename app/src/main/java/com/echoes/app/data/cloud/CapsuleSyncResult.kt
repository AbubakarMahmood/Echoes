package com.echoes.app.data.cloud

enum class CapsuleSyncStatus {
    CONFIG_MISSING,
    SIGN_IN_REQUIRED,
    NO_LOCAL_CAPSULES,
    SYNCED
}

data class CapsuleSyncResult(
    val status: CapsuleSyncStatus,
    val attemptedCount: Int = 0,
    val syncedCount: Int = 0,
    val firestorePath: String? = null
)
