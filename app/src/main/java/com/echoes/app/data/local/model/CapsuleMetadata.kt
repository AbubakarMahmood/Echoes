package com.echoes.app.data.local.model

data class CapsuleMetadata(
    val ownerId: String,
    val ownerDisplayName: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val unlockType: UnlockType,
    val isLocked: Boolean,
    val unlockAt: Long? = null,
    val satisfiedAt: Long? = null
) {
    val hasBeenEdited: Boolean
        get() = updatedAt > createdAt
}
