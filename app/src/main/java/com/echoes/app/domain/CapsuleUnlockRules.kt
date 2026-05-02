package com.echoes.app.domain

object CapsuleUnlockRules {

    fun isFutureTimeUnlock(unlockAt: Long?, now: Long): Boolean {
        return unlockAt != null && unlockAt > now
    }

    fun isLocationWithinRadius(distanceMeters: Float, radiusMeters: Int): Boolean {
        return distanceMeters <= radiusMeters
    }

    fun resolvedDateSatisfiedAt(
        unlockAt: Long,
        now: Long,
        currentSatisfiedAt: Long?
    ): Long? {
        return if (isFutureTimeUnlock(unlockAt, now)) {
            null
        } else {
            currentSatisfiedAt ?: now
        }
    }
}
