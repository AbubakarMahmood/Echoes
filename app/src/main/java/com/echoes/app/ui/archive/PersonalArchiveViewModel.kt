package com.echoes.app.ui.archive

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echoes.app.R
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.repository.CapsuleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalArchiveUiState(
    val isLoading: Boolean = false,
    val capsules: List<CapsuleRecord> = emptyList(),
    val hasLoaded: Boolean = false
)

sealed interface PersonalArchiveEvent {
    data class ShowMessage(@StringRes val messageResId: Int) : PersonalArchiveEvent
}

class PersonalArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CapsuleRepository(application)
    private val _uiState = MutableStateFlow(PersonalArchiveUiState())
    val uiState: StateFlow<PersonalArchiveUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PersonalArchiveEvent>()
    val events: SharedFlow<PersonalArchiveEvent> = _events.asSharedFlow()

    fun loadArchive() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                repository.getArchiveRecords()
            }.onSuccess { capsules ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        capsules = capsules,
                        hasLoaded = true
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, hasLoaded = true) }
                _events.emit(PersonalArchiveEvent.ShowMessage(R.string.archive_load_failed_message))
            }
        }
    }
}
