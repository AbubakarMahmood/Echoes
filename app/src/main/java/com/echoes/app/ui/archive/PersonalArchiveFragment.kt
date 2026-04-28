package com.echoes.app.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echoes.app.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PersonalArchiveFragment : Fragment() {

    private val viewModel: PersonalArchiveViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTitle: TextView
    private lateinit var emptyStateBody: TextView
    private lateinit var emptyStateButton: Button
    private lateinit var archiveControlsCard: View
    private lateinit var archiveResultSummaryText: TextView
    private lateinit var cloudSyncStatusText: TextView
    private lateinit var syncCloudButton: Button
    private lateinit var lockFilterGroup: MaterialButtonToggleGroup
    private lateinit var contentFilterGroup: MaterialButtonToggleGroup
    private lateinit var sortOptionGroup: MaterialButtonToggleGroup
    private lateinit var adapter: ArchiveCapsuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_personal_archive, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindActions()
        collectViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadArchive()
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.archiveRecyclerView)
        emptyStateTitle = view.findViewById(R.id.emptyStateTitle)
        emptyStateBody = view.findViewById(R.id.emptyStateBody)
        emptyStateButton = view.findViewById(R.id.emptyStateButton)
        archiveControlsCard = view.findViewById(R.id.archiveControlsCard)
        archiveResultSummaryText = view.findViewById(R.id.archiveResultSummaryText)
        cloudSyncStatusText = view.findViewById(R.id.archiveCloudSyncStatusText)
        syncCloudButton = view.findViewById(R.id.archiveSyncCloudButton)
        lockFilterGroup = view.findViewById(R.id.archiveLockFilterGroup)
        contentFilterGroup = view.findViewById(R.id.archiveContentFilterGroup)
        sortOptionGroup = view.findViewById(R.id.archiveSortOptionGroup)

        adapter = ArchiveCapsuleAdapter { capsule ->
            findNavController().navigate(
                R.id.action_archiveFragment_to_capsuleDetailFragment,
                bundleOf(CapsuleDetailFragment.ARG_CAPSULE_ID to capsule.capsule.capsuleId)
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun bindActions() {
        emptyStateButton.setOnClickListener {
            if (viewModel.uiState.value.hasArchiveItems) {
                viewModel.resetBrowsingRules()
            } else {
                findNavController().navigate(R.id.action_archiveFragment_to_createTextCapsuleFragment)
            }
        }

        lockFilterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.setLockFilter(
                when (checkedId) {
                    R.id.archiveLockedButton -> ArchiveLockFilter.LOCKED
                    R.id.archiveUnlockedButton -> ArchiveLockFilter.UNLOCKED
                    else -> ArchiveLockFilter.ALL
                }
            )
        }

        contentFilterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.setContentFilter(
                when (checkedId) {
                    R.id.archiveTextOnlyButton -> ArchiveContentFilter.TEXT_ONLY
                    R.id.archiveImagesButton -> ArchiveContentFilter.IMAGES
                    else -> ArchiveContentFilter.ALL
                }
            )
        }

        sortOptionGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.setSortOption(
                when (checkedId) {
                    R.id.archiveOldestFirstButton -> ArchiveSortOption.OLDEST_FIRST
                    else -> ArchiveSortOption.NEWEST_FIRST
                }
            )
        }

        syncCloudButton.setOnClickListener {
            viewModel.syncArchiveToCloud()
        }
    }

    private fun collectViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        handleEvent(event)
                    }
                }
            }
        }
    }

    private fun renderState(state: PersonalArchiveUiState) {
        bindBrowsingControls(state)

        adapter.submitList(state.capsules)
        val hasVisibleCapsules = state.capsules.isNotEmpty()
        val hasArchiveItems = state.hasArchiveItems

        archiveControlsCard.visibility = if (hasArchiveItems) View.VISIBLE else View.GONE
        cloudSyncStatusText.text = getString(state.cloudSyncStatusResId)
        syncCloudButton.isEnabled = hasArchiveItems && !state.isSyncing
        syncCloudButton.text = getString(
            if (state.isSyncing) R.string.archive_cloud_syncing_button else R.string.archive_sync_cloud_button
        )
        recyclerView.visibility = if (hasVisibleCapsules) View.VISIBLE else View.GONE
        emptyStateTitle.visibility = if (hasVisibleCapsules) View.GONE else View.VISIBLE
        emptyStateBody.visibility = if (hasVisibleCapsules) View.GONE else View.VISIBLE
        emptyStateButton.visibility = if (
            hasVisibleCapsules || (hasArchiveItems && !state.hasActiveBrowsingRules)
        ) {
            View.GONE
        } else {
            View.VISIBLE
        }

        emptyStateTitle.text = getString(
            if (hasArchiveItems) R.string.archive_filtered_empty_title else R.string.archive_empty_title
        )
        emptyStateBody.text = getString(
            if (hasArchiveItems) R.string.archive_filtered_empty_body else R.string.archive_empty_body
        )
        emptyStateButton.text = getString(
            if (hasArchiveItems) R.string.archive_clear_filters_button else R.string.archive_empty_button
        )
    }

    private fun bindBrowsingControls(state: PersonalArchiveUiState) {
        archiveResultSummaryText.text = getString(
            R.string.archive_result_summary,
            state.capsules.size,
            state.allCapsules.size
        )

        checkButton(
            lockFilterGroup,
            when (state.lockFilter) {
                ArchiveLockFilter.ALL -> R.id.archiveAllStatusButton
                ArchiveLockFilter.LOCKED -> R.id.archiveLockedButton
                ArchiveLockFilter.UNLOCKED -> R.id.archiveUnlockedButton
            }
        )
        checkButton(
            contentFilterGroup,
            when (state.contentFilter) {
                ArchiveContentFilter.ALL -> R.id.archiveAllContentButton
                ArchiveContentFilter.TEXT_ONLY -> R.id.archiveTextOnlyButton
                ArchiveContentFilter.IMAGES -> R.id.archiveImagesButton
            }
        )
        checkButton(
            sortOptionGroup,
            when (state.sortOption) {
                ArchiveSortOption.NEWEST_FIRST -> R.id.archiveNewestFirstButton
                ArchiveSortOption.OLDEST_FIRST -> R.id.archiveOldestFirstButton
            }
        )
    }

    private fun checkButton(group: MaterialButtonToggleGroup, buttonId: Int) {
        if (group.checkedButtonId != buttonId) {
            group.check(buttonId)
        }
    }

    private fun handleEvent(event: PersonalArchiveEvent) {
        when (event) {
            is PersonalArchiveEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
