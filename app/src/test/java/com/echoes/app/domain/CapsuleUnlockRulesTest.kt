package com.echoes.app.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CapsuleUnlockRulesTest {

    @Test
    fun futureTimeUnlockOnlyLocksWhenUnlockTimeIsAhead() {
        val now = 10_000L

        assertFalse(CapsuleUnlockRules.isFutureTimeUnlock(null, now))
        assertFalse(CapsuleUnlockRules.isFutureTimeUnlock(now, now))
        assertFalse(CapsuleUnlockRules.isFutureTimeUnlock(now - 1, now))
        assertTrue(CapsuleUnlockRules.isFutureTimeUnlock(now + 1, now))
    }

    @Test
    fun resolvedDateSatisfiedAtClearsFutureAndPreservesPastProof() {
        val now = 10_000L
        val previousSatisfiedAt = 9_000L

        assertNull(
            CapsuleUnlockRules.resolvedDateSatisfiedAt(
                unlockAt = now + 1,
                now = now,
                currentSatisfiedAt = previousSatisfiedAt
            )
        )
        assertEquals(
            previousSatisfiedAt,
            CapsuleUnlockRules.resolvedDateSatisfiedAt(
                unlockAt = now - 1,
                now = now,
                currentSatisfiedAt = previousSatisfiedAt
            )
        )
        assertEquals(
            now,
            CapsuleUnlockRules.resolvedDateSatisfiedAt(
                unlockAt = now,
                now = now,
                currentSatisfiedAt = null
            )
        )
    }

    @Test
    fun locationUnlockAllowsExactRadiusBoundary() {
        assertTrue(CapsuleUnlockRules.isLocationWithinRadius(distanceMeters = 150f, radiusMeters = 150))
        assertTrue(CapsuleUnlockRules.isLocationWithinRadius(distanceMeters = 149.9f, radiusMeters = 150))
        assertFalse(CapsuleUnlockRules.isLocationWithinRadius(distanceMeters = 150.1f, radiusMeters = 150))
    }
}
