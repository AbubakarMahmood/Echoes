package com.echoes.app.ui.archive

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echoes.app.R
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.local.model.CapsuleSocialState
import com.echoes.app.data.repository.CapsuleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CapsuleDetailUiState(
    val isLoading: Boolean = false,
    val record: CapsuleRecord? = null,
    val socialState: CapsuleSocialState = CapsuleSocialState(),
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isUpdatingSocial: Boolean = false,
    val isCheckingLocation: Boolean = false,
    @StringRes val titleErrorResId: Int? = null,
    @StringRes val bodyErrorResId: Int? = null,
    @StringRes val commentErrorResId: Int? = null
)

sealed interface CapsuleDetailEvent {
    data class ShowMessage(@StringRes val messageResId: Int) : CapsuleDetailEvent
    data object NavigateBack : CapsuleDetailEvent
}

class CapsuleDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CapsuleRepository(application)
    private val _uiState = MutableStateFlow(CapsuleDetailUiState())
    val uiState: StateFlow<CapsuleDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CapsuleDetailEvent>()
    val events: SharedFlow<CapsuleDetailEvent> = _events.asSharedFlow()

    private var loadedCapsuleId: String? = null

    fun loadCapsule(capsuleId: String?) {
        if (capsuleId.isNullOrBlank()) {
            viewModelScope.launch {
                _events.emit(CapsuleDetailEvent.NavigateBack)
            }
            return
        }

        if (loadedCapsuleId == capsuleId && _uiState.value.record != null) return

        loadedCapsuleId = capsuleId
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                val record = repository.getCapsuleRecord(capsuleId)
                record to if (record == null) {
                    CapsuleSocialState()
                } else {
                    repository.getCapsuleSocialState(record.capsule.capsuleId)
                }
            }.onSuccess { (record, socialState) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        record = record,
                        socialState = socialState
                    )
                }

                if (record == null) {
                    _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_detail_missing_message))
                    _events.emit(CapsuleDetailEvent.NavigateBack)
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_detail_load_failed_message))
            }
        }
    }

    fun toggleFavorite() {
        val record = _uiState.value.record ?: return
        if (record.metadata.isLocked) {
            viewModelScope.launch {
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.social_locked_capsule_message))
            }
            return
        }
        if (_uiState.value.isUpdatingSocial) return

        val shouldFavorite = !_uiState.value.socialState.isFavorite
        _uiState.update { it.copy(isUpdatingSocial = true) }
        viewModelScope.launch {
            runCatching {
                repository.setFavorite(record.capsule.capsuleId, shouldFavorite)
            }.onSuccess { socialState ->
                _uiState.update {
                    it.copy(
                        isUpdatingSocial = false,
                        socialState = socialState
                    )
                }
                _events.emit(
                    CapsuleDetailEvent.ShowMessage(
                        if (socialState.isFavorite) {
                            R.string.favorite_added_message
                        } else {
                            R.string.favorite_removed_message
                        }
                    )
                )
            }.onFailure {
                _uiState.update { it.copy(isUpdatingSocial = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.favorite_update_failed_message))
            }
        }
    }

    fun addComment(body: String) {
        val record = _uiState.value.record ?: return
        if (record.metadata.isLocked) {
            viewModelScope.launch {
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.social_locked_capsule_message))
            }
            return
        }
        if (_uiState.value.isUpdatingSocial) return

        val trimmedBody = body.trim()
        val commentError = if (trimmedBody.length < COMMENT_MIN_LENGTH) {
            R.string.comment_body_error
        } else {
            null
        }
        _uiState.update { it.copy(commentErrorResId = commentError) }
        if (commentError != null) return

        _uiState.update { it.copy(isUpdatingSocial = true) }
        viewModelScope.launch {
            runCatching {
                repository.addComment(record.capsule.capsuleId, trimmedBody)
            }.onSuccess { socialState ->
                _uiState.update {
                    it.copy(
                        isUpdatingSocial = false,
                        socialState = socialState,
                        commentErrorResId = null
                    )
                }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.comment_added_message))
            }.onFailure {
                _uiState.update { it.copy(isUpdatingSocial = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.comment_add_failed_message))
            }
        }
    }

    fun checkLocationUnlock(currentLatitude: Double, currentLongitude: Double) {
        val record = _uiState.value.record ?: return
        if (_uiState.value.isCheckingLocation) return

        _uiState.update { it.copy(isCheckingLocation = true) }
        viewModelScope.launch {
            runCatching {
                repository.checkLocationUnlock(
                    capsuleId = record.capsule.capsuleId,
                    currentLatitude = currentLatitude,
                    currentLongitude = currentLongitude
                )
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isCheckingLocation = false,
                        record = result.record ?: it.record
                    )
                }
                _events.emit(
                    CapsuleDetailEvent.ShowMessage(
                        when {
                            result.didUnlock -> R.string.location_unlock_success_message
                            !result.isWithinRange -> R.string.location_unlock_not_close_message
                            else -> R.string.location_unlock_already_open_message
                        }
                    )
                )
            }.onFailure {
                _uiState.update { it.copy(isCheckingLocation = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.location_unlock_check_failed_message))
            }
        }
    }

    fun saveChanges(title: String, body: String) {
        val capsule = _uiState.value.record?.capsule ?: return
        if (_uiState.value.isSaving) return

        val titleError = if (title.length < TITLE_MIN_LENGTH) R.string.error_title_too_short else null
        val bodyError = if (body.length < BODY_MIN_LENGTH) R.string.error_story_too_short else null

        _uiState.update {
            it.copy(
                titleErrorResId = titleError,
                bodyErrorResId = bodyError
            )
        }

        if (titleError != null || bodyError != null) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                repository.updateCapsule(capsule, title, body)
            }.onSuccess { updatedCapsule ->
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        record = state.record?.copy(capsule = updatedCapsule)
                    )
                }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_updated_message))
                _events.emit(CapsuleDetailEvent.NavigateBack)
            }.onFailure {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_update_failed_message))
            }
        }
    }

    fun deleteCapsule() {
        val capsule = _uiState.value.record?.capsule ?: return
        if (_uiState.value.isDeleting) return

        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            runCatching {
                repository.deleteCapsule(capsule)
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_deleted_message))
                _events.emit(CapsuleDetailEvent.NavigateBack)
            }.onFailure {
                _uiState.update { it.copy(isDeleting = false) }
                _events.emit(CapsuleDetailEvent.ShowMessage(R.string.capsule_delete_failed_message))
            }
        }
    }

    companion object {
        private const val TITLE_MIN_LENGTH = 3
        private const val BODY_MIN_LENGTH = 10
        private const val COMMENT_MIN_LENGTH = 2
    }
}
