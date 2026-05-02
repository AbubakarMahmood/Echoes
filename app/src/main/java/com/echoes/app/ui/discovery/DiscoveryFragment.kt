package com.echoes.app.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.echoes.app.ui.archive.ArchiveCapsuleAdapter
import com.echoes.app.ui.archive.CapsuleDetailFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTitle: TextView
    private lateinit var emptyStateBody: TextView
    private lateinit var adapter: ArchiveCapsuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_discovery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        collectViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDiscovery()
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.discoveryRecyclerView)
        emptyStateTitle = view.findViewById(R.id.discoveryEmptyTitle)
        emptyStateBody = view.findViewById(R.id.discoveryEmptyBody)

        adapter = ArchiveCapsuleAdapter { capsule ->
            findNavController().navigate(
                R.id.action_discoveryFragment_to_capsuleDetailFragment,
                bundleOf(CapsuleDetailFragment.ARG_CAPSULE_ID to capsule.capsule.capsuleId)
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
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

    private fun renderState(state: DiscoveryUiState) {
        adapter.submitList(state.capsules)
        val hasCapsules = state.capsules.isNotEmpty()
        recyclerView.visibility = if (hasCapsules) View.VISIBLE else View.GONE
        emptyStateTitle.visibility = if (hasCapsules) View.GONE else View.VISIBLE
        emptyStateBody.visibility = if (hasCapsules) View.GONE else View.VISIBLE
    }

    private fun handleEvent(event: DiscoveryEvent) {
        when (event) {
            is DiscoveryEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
