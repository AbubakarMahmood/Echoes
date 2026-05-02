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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PersonalArchiveFragment : Fragment() {

    private val viewModel: PersonalArchiveViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTitle: TextView
    private lateinit var emptyStateBody: TextView
    private lateinit var emptyStateButton: Button
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
            findNavController().navigate(R.id.action_archiveFragment_to_createTextCapsuleFragment)
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
        adapter.submitList(state.capsules)
        val hasCapsules = state.capsules.isNotEmpty()
        recyclerView.visibility = if (hasCapsules) View.VISIBLE else View.GONE
        emptyStateTitle.visibility = if (hasCapsules) View.GONE else View.VISIBLE
        emptyStateBody.visibility = if (hasCapsules) View.GONE else View.VISIBLE
        emptyStateButton.visibility = if (hasCapsules) View.GONE else View.VISIBLE
    }

    private fun handleEvent(event: PersonalArchiveEvent) {
        when (event) {
            is PersonalArchiveEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
