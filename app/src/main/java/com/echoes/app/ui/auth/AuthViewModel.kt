package com.echoes.app.ui.auth

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echoes.app.R
import com.echoes.app.data.auth.AuthRepository
import com.echoes.app.data.auth.MissingFirebaseConfigurationException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isFirebaseConfigured: Boolean = false,
    @StringRes val emailErrorResId: Int? = null,
    @StringRes val passwordErrorResId: Int? = null,
    @StringRes val displayNameErrorResId: Int? = null
)

sealed interface AuthEvent {
    data object NavigateToApp : AuthEvent
    data class ShowMessage(@StringRes val messageResId: Int) : AuthEvent
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
    private val _uiState = MutableStateFlow(
        AuthUiState(isFirebaseConfigured = repository.isFirebaseConfigured)
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        repository.currentFirebaseSession()?.let {
            viewModelScope.launch {
                _events.emit(AuthEvent.NavigateToApp)
            }
        }
    }

    fun signIn(email: String, password: String) {
        val trimmedEmail = email.trim()
        val emailError = validateEmail(trimmedEmail)
        val passwordError = validatePassword(password)
        _uiState.update {
            it.copy(
                emailErrorResId = emailError,
                passwordErrorResId = passwordError,
                displayNameErrorResId = null
            )
        }
        if (emailError != null || passwordError != null) return

        authenticate {
            repository.signIn(trimmedEmail, password)
        }
    }

    fun register(email: String, password: String, displayName: String) {
        val trimmedEmail = email.trim()
        val trimmedDisplayName = displayName.trim()
        val emailError = validateEmail(trimmedEmail)
        val passwordError = validatePassword(password)
        val displayNameError = validateDisplayName(trimmedDisplayName)
        _uiState.update {
            it.copy(
                emailErrorResId = emailError,
                passwordErrorResId = passwordError,
                displayNameErrorResId = displayNameError
            )
        }
        if (emailError != null || passwordError != null || displayNameError != null) return

        authenticate {
            repository.register(trimmedEmail, password, trimmedDisplayName)
        }
    }

    fun continueLocally() {
        repository.localSession()
        viewModelScope.launch {
            _events.emit(AuthEvent.NavigateToApp)
        }
    }

    private fun authenticate(operation: suspend () -> Any) {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                operation()
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFirebaseConfigured = repository.isFirebaseConfigured
                    )
                }
                _events.emit(AuthEvent.NavigateToApp)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFirebaseConfigured = repository.isFirebaseConfigured
                    )
                }
                _events.emit(AuthEvent.ShowMessage(error.toMessageResId()))
            }
        }
    }

    private fun validateEmail(email: String): Int? {
        return if (email.isBlank() || !email.contains("@")) {
            R.string.auth_email_error
        } else {
            null
        }
    }

    private fun validatePassword(password: String): Int? {
        return if (password.length < 6) {
            R.string.auth_password_error
        } else {
            null
        }
    }

    private fun validateDisplayName(displayName: String): Int? {
        return if (displayName.length < 2) {
            R.string.auth_display_name_error
        } else {
            null
        }
    }

    private fun Throwable.toMessageResId(): Int {
        return when (this) {
            is MissingFirebaseConfigurationException -> R.string.auth_missing_firebase_config_message
            is FirebaseAuthUserCollisionException -> R.string.auth_email_in_use_message
            is FirebaseAuthInvalidUserException -> R.string.auth_invalid_user_message
            is FirebaseAuthInvalidCredentialsException -> R.string.auth_invalid_credentials_message
            is FirebaseNetworkException -> R.string.auth_network_error_message
            else -> R.string.auth_generic_error_message
        }
    }
}
