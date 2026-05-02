package com.echoes.app.ui.discovery

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

data class DiscoveryUiState(
    val isLoading: Boolean = false,
    val capsules: List<CapsuleRecord> = emptyList(),
    val hasLoaded: Boolean = false
)

sealed interface DiscoveryEvent {
    data class ShowMessage(@StringRes val messageResId: Int) : DiscoveryEvent
}

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CapsuleRepository(application)
    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DiscoveryEvent>()
    val events: SharedFlow<DiscoveryEvent> = _events.asSharedFlow()

    fun loadDiscovery() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                repository.getDiscoveryRecords()
            }.onSuccess { records ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        capsules = records,
                        hasLoaded = true
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, hasLoaded = true) }
                _events.emit(DiscoveryEvent.ShowMessage(R.string.discovery_load_failed_message))
            }
        }
    }
}
