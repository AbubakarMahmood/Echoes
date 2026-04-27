package com.echoes.app.data.auth

data class AuthSession(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val isFirebaseBacked: Boolean
)
