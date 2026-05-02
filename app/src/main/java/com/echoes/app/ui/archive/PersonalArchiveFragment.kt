package com.echoes.app.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echoes.app.R
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.SeedData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalArchiveFragment : Fragment() {

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

        emptyStateButton.setOnClickListener {
            findNavController().navigate(R.id.action_archiveFragment_to_createTextCapsuleFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        loadArchive()
    }

    private fun loadArchive() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseProvider.getDatabase(requireContext())
                        .capsuleDao()
                        .getCapsuleRecordsForOwner(SeedData.LOCAL_USER_ID)
                }
            }.onSuccess { capsules ->
                adapter.submitList(capsules)
                val hasCapsules = capsules.isNotEmpty()
                recyclerView.visibility = if (hasCapsules) View.VISIBLE else View.GONE
                emptyStateTitle.visibility = if (hasCapsules) View.GONE else View.VISIBLE
                emptyStateBody.visibility = if (hasCapsules) View.GONE else View.VISIBLE
                emptyStateButton.visibility = if (hasCapsules) View.GONE else View.VISIBLE
            }.onFailure {
                Snackbar.make(requireView(), R.string.archive_load_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
