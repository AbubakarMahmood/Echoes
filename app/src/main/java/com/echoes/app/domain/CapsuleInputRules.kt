package com.echoes.app.domain

enum class CapsuleInputError {
    TOO_SHORT,
    TOO_LONG
}

/**
 * Pure validation rules for user-supplied text inputs.
 *
 * Centralises length constraints for capsule titles, story bodies, comments,
 * passwords, and display names. All rules operate on trimmed input and return
 * a [CapsuleInputError] if the value falls outside the accepted range, or null
 * if the input is valid. Verified by `CapsuleInputRulesTest`.
 */
object CapsuleInputRules {

    const val TITLE_MIN_LENGTH = 3
    const val TITLE_MAX_LENGTH = 80
    const val STORY_MIN_LENGTH = 10
    const val STORY_MAX_LENGTH = 2000
    const val COMMENT_MIN_LENGTH = 2
    const val COMMENT_MAX_LENGTH = 300
    const val PASSWORD_MIN_LENGTH = 6
    const val PASSWORD_MAX_LENGTH = 128
    const val DISPLAY_NAME_MIN_LENGTH = 2
    const val DISPLAY_NAME_MAX_LENGTH = 40

    fun validateTitle(title: String): CapsuleInputError? {
        return validateTrimmedLength(title, TITLE_MIN_LENGTH, TITLE_MAX_LENGTH)
    }

    fun validateStory(story: String): CapsuleInputError? {
        return validateTrimmedLength(story, STORY_MIN_LENGTH, STORY_MAX_LENGTH)
    }

    fun validateComment(comment: String): CapsuleInputError? {
        return validateTrimmedLength(comment, COMMENT_MIN_LENGTH, COMMENT_MAX_LENGTH)
    }

    fun isPasswordLengthValid(password: String): Boolean {
        return password.length in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH
    }

    fun validateDisplayName(displayName: String): CapsuleInputError? {
        return validateTrimmedLength(displayName, DISPLAY_NAME_MIN_LENGTH, DISPLAY_NAME_MAX_LENGTH)
    }

    private fun validateTrimmedLength(
        value: String,
        minLength: Int,
        maxLength: Int
    ): CapsuleInputError? {
        val length = value.trim().length
        return when {
            length < minLength -> CapsuleInputError.TOO_SHORT
            length > maxLength -> CapsuleInputError.TOO_LONG
            else -> null
        }
    }
}
