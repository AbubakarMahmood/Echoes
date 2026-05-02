package com.echoes.app.ui.archive

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.CapsuleImageStorage
import com.echoes.app.util.CapsuleMetadataFormatter
import com.echoes.app.util.DateFormatters
import com.echoes.app.util.ForegroundLocationReader
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CapsuleDetailFragment : Fragment() {

    private val viewModel: CapsuleDetailViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            checkCurrentLocationUnlock()
        } else {
            Snackbar.make(requireView(), R.string.location_permission_denied_message, Snackbar.LENGTH_LONG).show()
        }
    }

    private lateinit var titleLayout: TextInputLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var statusText: TextView
    private lateinit var createdAtText: TextView
    private lateinit var metadataOwnerText: TextView
    private lateinit var metadataUpdatedAtText: TextView
    private lateinit var metadataMediaTypeText: TextView
    private lateinit var metadataUnlockTypeText: TextView
    private lateinit var metadataUnlockScheduleText: TextView
    private lateinit var metadataLockStatusText: TextView
    private lateinit var locationUnlockCard: View
    private lateinit var locationUnlockStatusText: TextView
    private lateinit var checkLocationUnlockButton: MaterialButton
    private lateinit var imageCard: View
    private lateinit var imagePreview: ImageView
    private lateinit var imagePlaceholderText: TextView
    private lateinit var bodyLayout: TextInputLayout
    private lateinit var bodyInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private lateinit var favoriteButton: MaterialButton
    private lateinit var commentsContainer: LinearLayout
    private lateinit var commentLayout: TextInputLayout
    private lateinit var commentInput: TextInputEditText
    private lateinit var addCommentButton: MaterialButton
    private lateinit var socialLockedText: TextView
    private var lastBoundCapsuleKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_capsule_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindActions()
        collectViewModel()
        viewModel.loadCapsule(arguments?.getString(ARG_CAPSULE_ID))
    }

    private fun bindViews(view: View) {
        titleLayout = view.findViewById(R.id.detailTitleLayout)
        titleInput = view.findViewById(R.id.detailTitleInput)
        statusText = view.findViewById(R.id.detailStatusText)
        createdAtText = view.findViewById(R.id.detailCreatedAtText)
        metadataOwnerText = view.findViewById(R.id.detailOwnerText)
        metadataUpdatedAtText = view.findViewById(R.id.detailUpdatedAtText)
        metadataMediaTypeText = view.findViewById(R.id.detailMediaTypeText)
        metadataUnlockTypeText = view.findViewById(R.id.detailUnlockTypeText)
        metadataUnlockScheduleText = view.findViewById(R.id.detailUnlockScheduleText)
        metadataLockStatusText = view.findViewById(R.id.detailLockStatusText)
        locationUnlockCard = view.findViewById(R.id.detailLocationUnlockCard)
        locationUnlockStatusText = view.findViewById(R.id.detailLocationUnlockStatusText)
        checkLocationUnlockButton = view.findViewById(R.id.detailCheckLocationUnlockButton)
        imageCard = view.findViewById(R.id.detailImageCard)
        imagePreview = view.findViewById(R.id.detailImagePreview)
        imagePlaceholderText = view.findViewById(R.id.detailImagePlaceholderText)
        bodyLayout = view.findViewById(R.id.detailBodyLayout)
        bodyInput = view.findViewById(R.id.detailBodyInput)
        saveButton = view.findViewById(R.id.detailSaveButton)
        deleteButton = view.findViewById(R.id.detailDeleteButton)
        backButton = view.findViewById(R.id.detailBackButton)
        favoriteButton = view.findViewById(R.id.detailFavoriteButton)
        commentsContainer = view.findViewById(R.id.detailCommentsContainer)
        commentLayout = view.findViewById(R.id.detailCommentLayout)
        commentInput = view.findViewById(R.id.detailCommentInput)
        addCommentButton = view.findViewById(R.id.detailAddCommentButton)
        socialLockedText = view.findViewById(R.id.detailSocialLockedText)
    }

    private fun bindActions() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        saveButton.setOnClickListener {
            viewModel.saveChanges(
                title = titleInput.text?.toString()?.trim().orEmpty(),
                body = bodyInput.text?.toString()?.trim().orEmpty()
            )
        }

        deleteButton.setOnClickListener {
            confirmDelete()
        }

        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        addCommentButton.setOnClickListener {
            viewModel.addComment(commentInput.text?.toString().orEmpty())
        }

        checkLocationUnlockButton.setOnClickListener {
            requestLocationUnlockCheck()
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

    private fun renderState(state: CapsuleDetailUiState) {
        titleLayout.error = state.titleErrorResId?.let(::getString)
        bodyLayout.error = state.bodyErrorResId?.let(::getString)
        commentLayout.error = state.commentErrorResId?.let(::getString)
        setActionState(isSaving = state.isSaving, isDeleting = state.isDeleting)

        val record = state.record ?: return
        val capsule = record.capsule
        val capsuleKey = "${capsule.capsuleId}:${capsule.updatedAt}:${capsule.mediaType}:${capsule.mediaLocalPath}:${capsule.isLocked}"
        if (capsuleKey != lastBoundCapsuleKey) {
            lastBoundCapsuleKey = capsuleKey
            bindCapsule(record)
        }
        bindLocationUnlockControls(state)
        bindSocialState(state)
    }

    private fun handleEvent(event: CapsuleDetailEvent) {
        when (event) {
            is CapsuleDetailEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_LONG).show()
                if (event.messageResId == R.string.comment_added_message) {
                    commentInput.setText("")
                }
            }
            CapsuleDetailEvent.NavigateBack -> findNavController().navigateUp()
        }
    }

    private fun bindCapsule(capsuleRecord: CapsuleRecord) {
        val capsule = capsuleRecord.capsule
        val metadata = capsuleRecord.metadata

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
        val unlockSchedule = CapsuleMetadataFormatter.unlockScheduleLabel(requireContext(), metadata)
        metadataUnlockScheduleText.visibility = if (unlockSchedule == null) View.GONE else View.VISIBLE
        metadataUnlockScheduleText.text = unlockSchedule
        metadataLockStatusText.text = getString(
            R.string.detail_lock_status_value,
            CapsuleMetadataFormatter.lockStatusLabel(requireContext(), metadata)
        )
        bindImage(capsule)
        bodyInput.setText(capsule.storyText)
    }

    private fun bindSocialState(state: CapsuleDetailUiState) {
        val record = state.record ?: return
        val isUnlocked = !record.metadata.isLocked
        val isBusy = state.isSaving || state.isDeleting || state.isUpdatingSocial || state.isCheckingLocation

        favoriteButton.isEnabled = isUnlocked && !isBusy
        favoriteButton.text = getString(
            if (state.socialState.isFavorite) {
                R.string.favorite_remove_button
            } else {
                R.string.favorite_add_button
            }
        )
        commentInput.isEnabled = isUnlocked && !isBusy
        addCommentButton.isEnabled = isUnlocked && !isBusy
        addCommentButton.text = getString(
            if (state.isUpdatingSocial) R.string.comment_saving_button else R.string.comment_add_button
        )
        socialLockedText.visibility = if (isUnlocked) View.GONE else View.VISIBLE

        commentsContainer.removeAllViews()
        if (state.socialState.comments.isEmpty()) {
            commentsContainer.addView(commentTextView(getString(R.string.comments_empty_message), isMuted = true))
        } else {
            state.socialState.comments.forEach { comment ->
                commentsContainer.addView(
                    commentTextView(
                        getString(
                            R.string.comment_item_value,
                            comment.authorDisplayName,
                            DateFormatters.formatTimestamp(comment.createdAt),
                            comment.body
                        )
                    )
                )
            }
        }
    }

    private fun bindLocationUnlockControls(state: CapsuleDetailUiState) {
        val record = state.record ?: return
        val metadata = record.metadata
        val isLocationLockedCapsule = metadata.unlockType == UnlockType.LOCATION
        locationUnlockCard.visibility = if (isLocationLockedCapsule) View.VISIBLE else View.GONE
        if (!isLocationLockedCapsule) return

        locationUnlockStatusText.text = getString(
            if (metadata.isLocked) {
                R.string.detail_location_unlock_locked
            } else {
                R.string.detail_location_unlock_open
            },
            metadata.radiusMeters ?: DEFAULT_LOCATION_RADIUS_METERS
        )
        checkLocationUnlockButton.visibility = if (metadata.isLocked) View.VISIBLE else View.GONE
        checkLocationUnlockButton.isEnabled = !state.isCheckingLocation
        checkLocationUnlockButton.text = getString(
            if (state.isCheckingLocation) {
                R.string.location_unlock_checking_button
            } else {
                R.string.location_unlock_check_button
            }
        )
    }

    private fun commentTextView(text: String, isMuted: Boolean = false): TextView {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (8 * resources.displayMetrics.density).toInt()
            }
            setText(text)
            setTextColor(
                requireContext().getColor(
                    if (isMuted) R.color.echoes_muted else R.color.echoes_ink
                )
            )
            textSize = 14f
        }
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

    private fun confirmDelete() {
        if (viewModel.uiState.value.record == null) return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_capsule_dialog_title)
            .setMessage(R.string.delete_capsule_dialog_message)
            .setNegativeButton(R.string.cancel_button, null)
            .setPositiveButton(R.string.delete_capsule_confirm_button) { _, _ ->
                viewModel.deleteCapsule()
            }
            .show()
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

    private fun requestLocationUnlockCheck() {
        if (ForegroundLocationReader.hasLocationPermission(requireContext())) {
            checkCurrentLocationUnlock()
            return
        }

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun checkCurrentLocationUnlock() {
        val location = ForegroundLocationReader.currentBestLocation(requireContext())
        if (location == null) {
            Snackbar.make(requireView(), R.string.location_unavailable_message, Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.checkLocationUnlock(location.latitude, location.longitude)
    }

    companion object {
        const val ARG_CAPSULE_ID = "capsuleId"
        private const val DEFAULT_LOCATION_RADIUS_METERS = 150
    }
}
