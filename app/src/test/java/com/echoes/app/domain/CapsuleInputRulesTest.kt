package com.echoes.app.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CapsuleInputRulesTest {

    @Test
    fun titleValidationTrimsInputAndChecksBounds() {
        assertEquals(CapsuleInputError.TOO_SHORT, CapsuleInputRules.validateTitle("  ab  "))
        assertNull(CapsuleInputRules.validateTitle("  abc  "))
        assertEquals(CapsuleInputError.TOO_LONG, CapsuleInputRules.validateTitle("x".repeat(81)))
    }

    @Test
    fun storyAndCommentValidationUseTheirOwnLimits() {
        assertEquals(CapsuleInputError.TOO_SHORT, CapsuleInputRules.validateStory("too short"))
        assertNull(CapsuleInputRules.validateStory("This story is long enough."))
        assertEquals(CapsuleInputError.TOO_LONG, CapsuleInputRules.validateStory("x".repeat(2001)))

        assertEquals(CapsuleInputError.TOO_SHORT, CapsuleInputRules.validateComment(" "))
        assertNull(CapsuleInputRules.validateComment("ok"))
        assertEquals(CapsuleInputError.TOO_LONG, CapsuleInputRules.validateComment("x".repeat(301)))
    }

    @Test
    fun authLengthRulesKeepCredentialsBounded() {
        assertFalse(CapsuleInputRules.isPasswordLengthValid("12345"))
        assertTrue(CapsuleInputRules.isPasswordLengthValid("123456"))
        assertFalse(CapsuleInputRules.isPasswordLengthValid("x".repeat(129)))

        assertEquals(CapsuleInputError.TOO_SHORT, CapsuleInputRules.validateDisplayName("A"))
        assertNull(CapsuleInputRules.validateDisplayName("Echo User"))
        assertEquals(CapsuleInputError.TOO_LONG, CapsuleInputRules.validateDisplayName("x".repeat(41)))
    }
}
