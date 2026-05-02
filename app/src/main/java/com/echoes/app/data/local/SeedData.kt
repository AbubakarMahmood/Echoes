package com.echoes.app.data.local

import com.echoes.app.data.local.entity.UserEntity
import com.echoes.app.data.local.model.AffiliationType

object SeedData {
    const val LOCAL_USER_ID = "local-user"
    const val LOCAL_USER_NAME = "Echoes Local User"

    fun localUser(now: Long): UserEntity {
        return UserEntity(
            userId = LOCAL_USER_ID,
            email = "local@echoes.app",
            displayName = LOCAL_USER_NAME,
            affiliationType = AffiliationType.STUDENT,
            createdAt = now,
            updatedAt = now
        )
    }

    fun localUserForWrite(existingUser: UserEntity?, now: Long): UserEntity {
        return existingUser?.copy(updatedAt = now) ?: localUser(now)
    }
}
