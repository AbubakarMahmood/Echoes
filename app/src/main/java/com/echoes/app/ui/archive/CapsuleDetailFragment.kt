package com.echoes.app.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.model.CapsuleWithUnlockCondition
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.DateFormatters
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CapsuleDetailFragment : Fragment() {

    private lateinit var titleLayout: TextInputLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var statusText: TextView
    private lateinit var createdAtText: TextView
    private lateinit var unlockSummaryText: TextView
    private lateinit var bodyLayout: TextInputLayout
    private lateinit var bodyInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private var currentCapsule: CapsuleEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_capsule_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleLayout = view.findViewById(R.id.detailTitleLayout)
        titleInput = view.findViewById(R.id.detailTitleInput)
        statusText = view.findViewById(R.id.detailStatusText)
        createdAtText = view.findViewById(R.id.detailCreatedAtText)
        unlockSummaryText = view.findViewById(R.id.detailUnlockSummaryText)
        bodyLayout = view.findViewById(R.id.detailBodyLayout)
        bodyInput = view.findViewById(R.id.detailBodyInput)
        saveButton = view.findViewById(R.id.detailSaveButton)
        deleteButton = view.findViewById(R.id.detailDeleteButton)
        backButton = view.findViewById(R.id.detailBackButton)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        saveButton.setOnClickListener {
            saveChanges()
        }

        deleteButton.setOnClickListener {
            confirmDelete()
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
        currentCapsule = capsule

        titleInput.setText(capsule.title)
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
        bodyInput.setText(capsule.storyText)
    }

    private fun saveChanges() {
        val capsule = currentCapsule ?: return
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val body = bodyInput.text?.toString()?.trim().orEmpty()

        var valid = true

        if (title.length < TITLE_MIN_LENGTH) {
            titleLayout.error = getString(R.string.error_title_too_short)
            valid = false
        } else {
            titleLayout.error = null
        }

        if (body.length < BODY_MIN_LENGTH) {
            bodyLayout.error = getString(R.string.error_story_too_short)
            valid = false
        } else {
            bodyLayout.error = null
        }

        if (!valid) return

        setActionState(isSaving = true, isDeleting = false)

        val updatedCapsule = capsule.copy(
            title = title,
            storyText = body,
            updatedAt = System.currentTimeMillis()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseProvider.getDatabase(requireContext())
                        .capsuleDao()
                        .upsertCapsule(updatedCapsule)
                }
            }.onSuccess {
                currentCapsule = updatedCapsule
                setActionState(isSaving = false, isDeleting = false)
                Toast.makeText(requireContext(), R.string.capsule_updated_message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                setActionState(isSaving = false, isDeleting = false)
                Snackbar.make(requireView(), R.string.capsule_update_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDelete() {
        if (currentCapsule == null) return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_capsule_dialog_title)
            .setMessage(R.string.delete_capsule_dialog_message)
            .setNegativeButton(R.string.cancel_button, null)
            .setPositiveButton(R.string.delete_capsule_confirm_button) { _, _ ->
                deleteCapsule()
            }
            .show()
    }

    private fun deleteCapsule() {
        val capsule = currentCapsule ?: return
        setActionState(isSaving = false, isDeleting = true)

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseProvider.getDatabase(requireContext())
                        .capsuleDao()
                        .deleteCapsule(capsule)
                }
            }.onSuccess {
                setActionState(isSaving = false, isDeleting = false)
                Toast.makeText(requireContext(), R.string.capsule_deleted_message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                setActionState(isSaving = false, isDeleting = false)
                Snackbar.make(requireView(), R.string.capsule_delete_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setActionState(isSaving: Boolean, isDeleting: Boolean) {
        val isBusy = isSaving || isDeleting
        titleInput.isEnabled = !isBusy
        bodyInput.isEnabled = !isBusy
        saveButton.isEnabled = !isBusy
        deleteButton.isEnabled = !isBusy
        backButton.isEnabled = !isBusy

        saveButton.text = getString(
            if (isSaving) R.string.detail_saving_button else R.string.detail_save_button
        )
        deleteButton.text = getString(
            if (isDeleting) R.string.detail_deleting_button else R.string.detail_delete_button
        )
    }

    companion object {
        const val ARG_CAPSULE_ID = "capsuleId"
        private const val TITLE_MIN_LENGTH = 3
        private const val BODY_MIN_LENGTH = 10
    }
}
