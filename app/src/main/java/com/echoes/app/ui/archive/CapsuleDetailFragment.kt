package com.echoes.app.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.model.CapsuleWithUnlockCondition
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.DateFormatters
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CapsuleDetailFragment : Fragment() {

    private lateinit var titleText: TextView
    private lateinit var statusText: TextView
    private lateinit var createdAtText: TextView
    private lateinit var unlockSummaryText: TextView
    private lateinit var bodyText: TextView
    private lateinit var backButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_capsule_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = view.findViewById(R.id.detailTitleText)
        statusText = view.findViewById(R.id.detailStatusText)
        createdAtText = view.findViewById(R.id.detailCreatedAtText)
        unlockSummaryText = view.findViewById(R.id.detailUnlockSummaryText)
        bodyText = view.findViewById(R.id.detailBodyText)
        backButton = view.findViewById(R.id.detailBackButton)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        loadCapsule()
    }

    private fun loadCapsule() {
        val capsuleId = arguments?.getString(ARG_CAPSULE_ID)
        if (capsuleId.isNullOrBlank()) {
            findNavController().navigateUp()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseProvider.getDatabase(requireContext())
                        .capsuleDao()
                        .getCapsuleWithUnlockCondition(capsuleId)
                }
            }.onSuccess { capsuleWithUnlock ->
                if (capsuleWithUnlock == null) {
                    Snackbar.make(requireView(), R.string.capsule_detail_missing_message, Snackbar.LENGTH_LONG).show()
                    findNavController().navigateUp()
                } else {
                    bindCapsule(capsuleWithUnlock)
                }
            }.onFailure {
                Snackbar.make(requireView(), R.string.capsule_detail_load_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun bindCapsule(capsuleWithUnlock: CapsuleWithUnlockCondition) {
        val capsule = capsuleWithUnlock.capsule
        val unlockCondition = capsuleWithUnlock.unlockCondition

        titleText.text = capsule.title
        statusText.text = getString(
            if (capsule.isLocked) R.string.capsule_status_locked else R.string.capsule_status_unlocked
        )
        createdAtText.text = getString(
            R.string.detail_created_at,
            DateFormatters.formatTimestamp(capsule.createdAt)
        )
        unlockSummaryText.text = when (unlockCondition?.conditionType ?: UnlockType.NONE) {
            UnlockType.NONE -> getString(R.string.detail_unlock_now)
            UnlockType.DATE -> getString(R.string.detail_unlock_date_pending)
            UnlockType.LOCATION -> getString(R.string.detail_unlock_location_pending)
            UnlockType.EVENT -> getString(R.string.detail_unlock_event_pending)
        }
        bodyText.text = capsule.storyText
    }

    companion object {
        const val ARG_CAPSULE_ID = "capsuleId"
    }
}
