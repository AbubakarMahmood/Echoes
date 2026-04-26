package com.echoes.app.ui.archive

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echoes.app.R
import com.echoes.app.data.local.model.CapsuleMediaType
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

enum class ArchiveLockFilter {
    ALL,
    LOCKED,
    UNLOCKED
}

enum class ArchiveContentFilter {
    ALL,
    TEXT_ONLY,
    IMAGES
}

enum class ArchiveSortOption {
    NEWEST_FIRST,
    OLDEST_FIRST
}

data class PersonalArchiveUiState(
    val isLoading: Boolean = false,
    val allCapsules: List<CapsuleRecord> = emptyList(),
    val capsules: List<CapsuleRecord> = emptyList(),
    val lockFilter: ArchiveLockFilter = ArchiveLockFilter.ALL,
    val contentFilter: ArchiveContentFilter = ArchiveContentFilter.ALL,
    val sortOption: ArchiveSortOption = ArchiveSortOption.NEWEST_FIRST,
    val hasLoaded: Boolean = false
) {
    val hasArchiveItems: Boolean
        get() = allCapsules.isNotEmpty()

    val hasActiveBrowsingRules: Boolean
        get() = lockFilter != ArchiveLockFilter.ALL ||
            contentFilter != ArchiveContentFilter.ALL ||
            sortOption != ArchiveSortOption.NEWEST_FIRST
}

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
            }.onSuccess { records ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        allCapsules = records,
                        hasLoaded = true
                    ).withArchiveRulesApplied()
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, hasLoaded = true) }
                _events.emit(PersonalArchiveEvent.ShowMessage(R.string.archive_load_failed_message))
            }
        }
    }

    fun setLockFilter(filter: ArchiveLockFilter) {
        _uiState.update {
            it.copy(lockFilter = filter).withArchiveRulesApplied()
        }
    }

    fun setContentFilter(filter: ArchiveContentFilter) {
        _uiState.update {
            it.copy(contentFilter = filter).withArchiveRulesApplied()
        }
    }

    fun setSortOption(sortOption: ArchiveSortOption) {
        _uiState.update {
            it.copy(sortOption = sortOption).withArchiveRulesApplied()
        }
    }

    fun resetBrowsingRules() {
        _uiState.update {
            it.copy(
                lockFilter = ArchiveLockFilter.ALL,
                contentFilter = ArchiveContentFilter.ALL,
                sortOption = ArchiveSortOption.NEWEST_FIRST
            ).withArchiveRulesApplied()
        }
    }

    private fun PersonalArchiveUiState.withArchiveRulesApplied(): PersonalArchiveUiState {
        val filteredCapsules = allCapsules
            .filter { record ->
                when (lockFilter) {
                    ArchiveLockFilter.ALL -> true
                    ArchiveLockFilter.LOCKED -> record.metadata.isLocked
                    ArchiveLockFilter.UNLOCKED -> !record.metadata.isLocked
                }
            }
            .filter { record ->
                when (contentFilter) {
                    ArchiveContentFilter.ALL -> true
                    ArchiveContentFilter.TEXT_ONLY -> record.capsule.mediaType == CapsuleMediaType.TEXT
                    ArchiveContentFilter.IMAGES -> record.capsule.mediaType == CapsuleMediaType.IMAGE
                }
            }
            .let { records ->
                when (sortOption) {
                    ArchiveSortOption.NEWEST_FIRST -> records.sortedByDescending { it.archiveSortTimestamp() }
                    ArchiveSortOption.OLDEST_FIRST -> records.sortedBy { it.archiveSortTimestamp() }
                }
            }

        return copy(capsules = filteredCapsules)
    }

    private fun CapsuleRecord.archiveSortTimestamp(): Long {
        return if (metadata.hasBeenEdited) {
            metadata.updatedAt
        } else {
            metadata.createdAt
        }
    }
}
