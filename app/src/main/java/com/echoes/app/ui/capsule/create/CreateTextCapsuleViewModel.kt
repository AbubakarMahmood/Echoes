package com.echoes.app.ui.capsule.create

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echoes.app.R
import com.echoes.app.data.local.model.LocationUnlockTarget
import com.echoes.app.data.repository.CapsuleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateTextCapsuleUiState(
    val isSaving: Boolean = false,
    val selectedImagePath: String? = null,
    val selectedUnlockAt: Long? = null,
    val locationUnlockTarget: LocationUnlockTarget? = null,
    @StringRes val titleErrorResId: Int? = null,
    @StringRes val bodyErrorResId: Int? = null
)

sealed interface CreateTextCapsuleEvent {
    data class LaunchCamera(val uri: Uri) : CreateTextCapsuleEvent
    data class ShowMessage(@StringRes val messageResId: Int) : CreateTextCapsuleEvent
    data object NavigateToArchive : CreateTextCapsuleEvent
}

class CreateTextCapsuleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CapsuleRepository(application)
    private val _uiState = MutableStateFlow(CreateTextCapsuleUiState())
    val uiState: StateFlow<CreateTextCapsuleUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateTextCapsuleEvent>()
    val events: SharedFlow<CreateTextCapsuleEvent> = _events.asSharedFlow()

    private var pendingCameraImagePath: String? = null
    private var hasSavedCapsule = false

    fun prepareCameraCapture() {
        viewModelScope.launch {
            runCatching {
                repository.createCameraCaptureTarget()
            }.onSuccess { target ->
                pendingCameraImagePath = target.imagePath
                _events.emit(CreateTextCapsuleEvent.LaunchCamera(target.imageUri))
            }.onFailure {
                pendingCameraImagePath = null
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.image_capture_failed_message))
            }
        }
    }

    fun handleCameraCaptureResult(success: Boolean) {
        viewModelScope.launch {
            val capturedImagePath = pendingCameraImagePath
            pendingCameraImagePath = null

            if (!success) {
                repository.deleteStoredImage(capturedImagePath)
                return@launch
            }

            if (capturedImagePath.isNullOrBlank()) {
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.image_capture_failed_message))
                return@launch
            }

            replaceSelectedImage(capturedImagePath)
            _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.image_captured_message))
        }
    }

    fun importSelectedImage(sourceUri: Uri) {
        viewModelScope.launch {
            runCatching {
                repository.importImage(sourceUri)
            }.onSuccess { importedImagePath ->
                replaceSelectedImage(importedImagePath)
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.image_added_message))
            }.onFailure {
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.image_import_failed_message))
            }
        }
    }

    fun clearSelectedImage() {
        repository.deleteStoredImage(_uiState.value.selectedImagePath)
        _uiState.update { it.copy(selectedImagePath = null) }
    }

    fun setDateUnlock(unlockAt: Long) {
        viewModelScope.launch {
            if (unlockAt <= System.currentTimeMillis()) {
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.time_unlock_must_be_future))
                return@launch
            }

            _uiState.update {
                it.copy(
                    selectedUnlockAt = unlockAt,
                    locationUnlockTarget = null
                )
            }
        }
    }

    fun clearDateUnlock() {
        _uiState.update { it.copy(selectedUnlockAt = null) }
    }

    fun setLocationUnlock(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                selectedUnlockAt = null,
                locationUnlockTarget = LocationUnlockTarget(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = DEFAULT_LOCATION_UNLOCK_RADIUS_METERS
                )
            )
        }
    }

    fun clearLocationUnlock() {
        _uiState.update { it.copy(locationUnlockTarget = null) }
    }

    fun cleanupPendingAttachments() {
        if (hasSavedCapsule) return

        repository.deleteStoredImage(_uiState.value.selectedImagePath)
        repository.deleteStoredImage(pendingCameraImagePath)
        pendingCameraImagePath = null
        _uiState.update { it.copy(selectedImagePath = null) }
    }

    fun saveCapsule(title: String, body: String) {
        if (_uiState.value.isSaving) return

        val trimmedTitle = title.trim()
        val trimmedBody = body.trim()
        if (!validateDraft(trimmedTitle, trimmedBody)) return

        val imagePath = _uiState.value.selectedImagePath
        val unlockAt = _uiState.value.selectedUnlockAt
        val locationUnlockTarget = _uiState.value.locationUnlockTarget
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                repository.createCapsule(trimmedTitle, trimmedBody, imagePath, unlockAt, locationUnlockTarget)
            }.onSuccess {
                hasSavedCapsule = true
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(CreateTextCapsuleEvent.NavigateToArchive)
            }.onFailure {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(CreateTextCapsuleEvent.ShowMessage(R.string.capsule_save_failed_message))
            }
        }
    }

    fun validateDraft(title: String, body: String): Boolean {
        val trimmedTitle = title.trim()
        val trimmedBody = body.trim()
        val titleError = when {
            trimmedTitle.length < TITLE_MIN_LENGTH -> R.string.error_title_too_short
            trimmedTitle.length > TITLE_MAX_LENGTH -> R.string.error_title_too_long
            else -> null
        }
        val bodyError = when {
            trimmedBody.length < BODY_MIN_LENGTH -> R.string.error_story_too_short
            trimmedBody.length > BODY_MAX_LENGTH -> R.string.error_story_too_long
            else -> null
        }

        _uiState.update {
            it.copy(
                titleErrorResId = titleError,
                bodyErrorResId = bodyError
            )
        }

        return titleError == null && bodyError == null
    }

    override fun onCleared() {
        cleanupPendingAttachments()
        super.onCleared()
    }

    private fun replaceSelectedImage(newImagePath: String) {
        val currentImagePath = _uiState.value.selectedImagePath
        if (currentImagePath != null && currentImagePath != newImagePath) {
            repository.deleteStoredImage(currentImagePath)
        }

        _uiState.update { it.copy(selectedImagePath = newImagePath) }
    }

    companion object {
        private const val TITLE_MIN_LENGTH = 3
        private const val TITLE_MAX_LENGTH = 80
        private const val BODY_MIN_LENGTH = 10
        private const val BODY_MAX_LENGTH = 2000
        private const val DEFAULT_LOCATION_UNLOCK_RADIUS_METERS = 150
    }
}
