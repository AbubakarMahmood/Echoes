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

enum class CapsuleRestoreStatus {
    CONFIG_MISSING,
    SIGN_IN_REQUIRED,
    NO_REMOTE_CAPSULES,
    RESTORED,
    RESTORED_WITH_MISSING_IMAGES
}

data class CapsuleRestoreResult(
    val status: CapsuleRestoreStatus,
    val attemptedCount: Int = 0,
    val restoredCount: Int = 0,
    val missingImageCount: Int = 0,
    val firestorePath: String? = null
)
