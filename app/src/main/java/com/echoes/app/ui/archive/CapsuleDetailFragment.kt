package com.echoes.app.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.util.CapsuleImageStorage
import com.echoes.app.util.DateFormatters
import com.echoes.app.util.CapsuleMetadataFormatter
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
    private lateinit var metadataOwnerText: TextView
    private lateinit var metadataUpdatedAtText: TextView
    private lateinit var metadataMediaTypeText: TextView
    private lateinit var metadataUnlockTypeText: TextView
    private lateinit var metadataLockStatusText: TextView
    private lateinit var imageCard: View
    private lateinit var imagePreview: ImageView
    private lateinit var imagePlaceholderText: TextView
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
        metadataOwnerText = view.findViewById(R.id.detailOwnerText)
        metadataUpdatedAtText = view.findViewById(R.id.detailUpdatedAtText)
        metadataMediaTypeText = view.findViewById(R.id.detailMediaTypeText)
        metadataUnlockTypeText = view.findViewById(R.id.detailUnlockTypeText)
        metadataLockStatusText = view.findViewById(R.id.detailLockStatusText)
        imageCard = view.findViewById(R.id.detailImageCard)
        imagePreview = view.findViewById(R.id.detailImagePreview)
        imagePlaceholderText = view.findViewById(R.id.detailImagePlaceholderText)
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
                        .getCapsuleRecord(capsuleId)
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

    private fun bindCapsule(capsuleRecord: CapsuleRecord) {
        val capsule = capsuleRecord.capsule
        val metadata = capsuleRecord.metadata
        currentCapsule = capsule

        titleInput.setText(capsule.title)
        statusText.text = getString(
            if (metadata.isLocked) R.string.capsule_status_locked else R.string.capsule_status_unlocked
        )
        createdAtText.text = getString(
            R.string.detail_created_at,
            DateFormatters.formatTimestamp(metadata.createdAt)
        )
        metadataOwnerText.text = getString(
            R.string.detail_owner_value,
            CapsuleMetadataFormatter.ownerSummary(requireContext(), metadata)
        )
        metadataUpdatedAtText.text = getString(
            R.string.detail_updated_at,
            DateFormatters.formatTimestamp(metadata.updatedAt)
        )
        metadataMediaTypeText.text = getString(
            R.string.detail_media_type_value,
            CapsuleMetadataFormatter.mediaTypeLabel(requireContext(), capsule.mediaType)
        )
        metadataUnlockTypeText.text = getString(
            R.string.detail_unlock_type_value,
            CapsuleMetadataFormatter.unlockTypeLabel(requireContext(), metadata.unlockType)
        )
        metadataLockStatusText.text = getString(
            R.string.detail_lock_status_value,
            CapsuleMetadataFormatter.lockStatusLabel(requireContext(), metadata)
        )
        bindImage(capsule)
        bodyInput.setText(capsule.storyText)
    }

    private fun bindImage(capsule: CapsuleEntity) {
        val imageUri = CapsuleImageStorage.uriForStoredPath(capsule.mediaLocalPath)
        val shouldShowImageCard = capsule.mediaType == CapsuleMediaType.IMAGE || imageUri != null
        val hasUsableImage = imageUri != null

        imageCard.visibility = if (shouldShowImageCard) View.VISIBLE else View.GONE

        if (!shouldShowImageCard) return

        imagePreview.setImageURI(null)
        if (hasUsableImage) {
            imagePreview.setImageURI(imageUri)
        }

        imagePreview.visibility = if (hasUsableImage) View.VISIBLE else View.GONE
        imagePlaceholderText.visibility = if (hasUsableImage) View.GONE else View.VISIBLE
        imagePlaceholderText.text = getString(R.string.detail_image_missing)
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
                    val database = DatabaseProvider.getDatabase(requireContext())
                    database.capsuleDao().deleteCapsule(capsule)
                    if (capsule.mediaType == CapsuleMediaType.IMAGE) {
                        CapsuleImageStorage.deleteStoredImage(capsule.mediaLocalPath)
                    }
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
