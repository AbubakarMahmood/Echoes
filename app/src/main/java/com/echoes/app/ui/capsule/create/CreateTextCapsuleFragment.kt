package com.echoes.app.ui.capsule.create

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.DatabaseProvider
import com.echoes.app.data.local.SeedData
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.CapsuleImageStorage
import com.echoes.app.util.CapsuleMetadataFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTextCapsuleFragment : Fragment() {

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importSelectedImage(uri)
        }
    }

    private val captureImageLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        handleCameraCaptureResult(success)
    }

    private lateinit var titleLayout: TextInputLayout
    private lateinit var bodyLayout: TextInputLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var bodyInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var chooseImageButton: Button
    private lateinit var captureImageButton: Button
    private lateinit var removeImageButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var imagePlaceholderText: TextView
    private lateinit var imageStatusText: TextView
    private lateinit var metadataOwnerText: TextView
    private lateinit var metadataMediaTypeText: TextView
    private lateinit var metadataUnlockTypeText: TextView
    private lateinit var metadataLockStatusText: TextView
    private lateinit var metadataTimestampText: TextView
    private var selectedImagePath: String? = null
    private var pendingCameraImagePath: String? = null
    private var hasSavedCapsule = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_create_text_capsule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleLayout = view.findViewById(R.id.titleLayout)
        bodyLayout = view.findViewById(R.id.bodyLayout)
        titleInput = view.findViewById(R.id.titleInput)
        bodyInput = view.findViewById(R.id.bodyInput)
        saveButton = view.findViewById(R.id.saveCapsuleButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        chooseImageButton = view.findViewById(R.id.chooseImageButton)
        captureImageButton = view.findViewById(R.id.captureImageButton)
        removeImageButton = view.findViewById(R.id.removeImageButton)
        imagePreview = view.findViewById(R.id.createImagePreview)
        imagePlaceholderText = view.findViewById(R.id.createImagePlaceholderText)
        imageStatusText = view.findViewById(R.id.createImageStatusText)
        metadataOwnerText = view.findViewById(R.id.createMetadataOwnerText)
        metadataMediaTypeText = view.findViewById(R.id.createMetadataMediaTypeText)
        metadataUnlockTypeText = view.findViewById(R.id.createMetadataUnlockTypeText)
        metadataLockStatusText = view.findViewById(R.id.createMetadataLockStatusText)
        metadataTimestampText = view.findViewById(R.id.createMetadataTimestampText)

        bindMetadataPreview()
        bindImagePreview()

        saveButton.setOnClickListener {
            saveCapsule()
        }

        cancelButton.setOnClickListener {
            cleanupPendingAttachments()
            findNavController().navigateUp()
        }

        chooseImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        captureImageButton.setOnClickListener {
            launchCameraCapture()
        }

        removeImageButton.setOnClickListener {
            clearSelectedImage()
        }
    }

    private fun bindMetadataPreview() {
        metadataOwnerText.text = getString(
            R.string.create_metadata_owner_value,
            SeedData.LOCAL_USER_NAME,
            SeedData.LOCAL_USER_ID
        )
        metadataMediaTypeText.text = getString(
            R.string.create_metadata_media_value,
            CapsuleMetadataFormatter.mediaTypeLabel(
                requireContext(),
                if (selectedImagePath.isNullOrBlank()) CapsuleMediaType.TEXT else CapsuleMediaType.IMAGE
            )
        )
        metadataUnlockTypeText.text = getString(
            R.string.create_metadata_unlock_value,
            CapsuleMetadataFormatter.unlockTypeLabel(requireContext(), UnlockType.NONE)
        )
        metadataLockStatusText.text = getString(
            R.string.create_metadata_lock_value,
            getString(R.string.capsule_status_unlocked)
        )
        metadataTimestampText.text = getString(R.string.create_metadata_timestamps_value)
    }

    private fun bindImagePreview() {
        val imageUri = CapsuleImageStorage.uriForStoredPath(selectedImagePath)
        val hasImage = imageUri != null

        imagePreview.setImageURI(null)
        if (hasImage) {
            imagePreview.setImageURI(imageUri)
        }

        imagePreview.visibility = if (hasImage) View.VISIBLE else View.GONE
        imagePlaceholderText.visibility = if (hasImage) View.GONE else View.VISIBLE
        removeImageButton.visibility = if (hasImage) View.VISIBLE else View.GONE
        imageStatusText.text = getString(
            if (hasImage) R.string.image_attachment_ready else R.string.image_attachment_empty
        )
    }

    private fun launchCameraCapture() {
        runCatching {
            val imageFile = CapsuleImageStorage.createCameraImageFile(requireContext())
            pendingCameraImagePath = imageFile.absolutePath
            CapsuleImageStorage.createCameraImageUri(requireContext(), imageFile)
        }.onSuccess { captureUri ->
            captureImageLauncher.launch(captureUri)
        }.onFailure {
            pendingCameraImagePath = null
            Snackbar.make(requireView(), R.string.image_capture_failed_message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun handleCameraCaptureResult(success: Boolean) {
        val capturedImagePath = pendingCameraImagePath
        pendingCameraImagePath = null

        if (!success) {
            CapsuleImageStorage.deleteStoredImage(capturedImagePath)
            return
        }

        if (capturedImagePath.isNullOrBlank()) {
            Snackbar.make(requireView(), R.string.image_capture_failed_message, Snackbar.LENGTH_LONG).show()
            return
        }

        replaceSelectedImage(capturedImagePath)
        Snackbar.make(requireView(), R.string.image_captured_message, Snackbar.LENGTH_SHORT).show()
    }

    private fun importSelectedImage(sourceUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    CapsuleImageStorage.importImageFromUri(requireContext(), sourceUri)
                }
            }.onSuccess { importedImagePath ->
                replaceSelectedImage(importedImagePath)
                Snackbar.make(requireView(), R.string.image_added_message, Snackbar.LENGTH_SHORT).show()
            }.onFailure {
                Snackbar.make(requireView(), R.string.image_import_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun replaceSelectedImage(newImagePath: String) {
        if (selectedImagePath != null && selectedImagePath != newImagePath) {
            CapsuleImageStorage.deleteStoredImage(selectedImagePath)
        }

        selectedImagePath = newImagePath
        bindMetadataPreview()
        bindImagePreview()
    }

    private fun clearSelectedImage() {
        CapsuleImageStorage.deleteStoredImage(selectedImagePath)
        selectedImagePath = null
        bindMetadataPreview()
        bindImagePreview()
    }

    private fun cleanupPendingAttachments() {
        if (hasSavedCapsule) return

        CapsuleImageStorage.deleteStoredImage(selectedImagePath)
        CapsuleImageStorage.deleteStoredImage(pendingCameraImagePath)
        selectedImagePath = null
        pendingCameraImagePath = null
    }

    private fun saveCapsule() {
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

        setSavingState(true)

        val now = System.currentTimeMillis()
        val capsuleId = UUID.randomUUID().toString()
        val conditionId = UUID.randomUUID().toString()

        val capsule = CapsuleEntity(
            capsuleId = capsuleId,
            ownerId = SeedData.LOCAL_USER_ID,
            title = title,
            storyText = body,
            mediaType = if (selectedImagePath.isNullOrBlank()) CapsuleMediaType.TEXT else CapsuleMediaType.IMAGE,
            mediaLocalPath = selectedImagePath,
            unlockType = UnlockType.NONE,
            isLocked = false,
            isPublic = false,
            createdAt = now,
            updatedAt = now
        )

        val unlockCondition = UnlockConditionEntity(
            conditionId = conditionId,
            capsuleId = capsuleId,
            conditionType = UnlockType.NONE
        )

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val database = DatabaseProvider.getDatabase(requireContext())
                    val existingUser = database.userDao().getUserById(SeedData.LOCAL_USER_ID)
                    database.userDao().upsertUser(SeedData.localUserForWrite(existingUser, now))
                    database.capsuleDao().upsertCapsule(capsule)
                    database.unlockConditionDao().upsertUnlockCondition(unlockCondition)
                }
            }.onSuccess {
                hasSavedCapsule = true
                setSavingState(false)
                findNavController().navigate(R.id.archiveFragment)
            }.onFailure {
                setSavingState(false)
                Snackbar.make(requireView(), R.string.capsule_save_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setSavingState(isSaving: Boolean) {
        saveButton.isEnabled = !isSaving
        cancelButton.isEnabled = !isSaving
        chooseImageButton.isEnabled = !isSaving
        captureImageButton.isEnabled = !isSaving
        removeImageButton.isEnabled = !isSaving
        saveButton.text = getString(
            if (isSaving) R.string.saving_capsule_button else R.string.save_capsule_button
        )
    }

    companion object {
        private const val TITLE_MIN_LENGTH = 3
        private const val BODY_MIN_LENGTH = 10
    }
}
