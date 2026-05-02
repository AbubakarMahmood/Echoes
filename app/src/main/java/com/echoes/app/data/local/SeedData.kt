package com.echoes.app.data.local

import com.echoes.app.data.local.entity.UserEntity
import com.echoes.app.data.local.model.AffiliationType

object SeedData {
    const val LOCAL_USER_ID = "local-user"

    fun localUser(now: Long): UserEntity {
        return UserEntity(
            userId = LOCAL_USER_ID,
            email = "local@echoes.app",
            displayName = "Echoes Local User",
            affiliationType = AffiliationType.STUDENT,
            createdAt = now,
            updatedAt = now
        )
    }
}
