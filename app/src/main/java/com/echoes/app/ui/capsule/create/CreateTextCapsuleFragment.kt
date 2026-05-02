package com.echoes.app.ui.capsule.create

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.local.SeedData
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.LocationUnlockTarget
import com.echoes.app.data.local.model.UnlockType
import com.echoes.app.util.CapsuleImageStorage
import com.echoes.app.util.CapsuleMetadataFormatter
import com.echoes.app.util.DateFormatters
import com.echoes.app.util.ForegroundLocationReader
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import kotlinx.coroutines.launch

class CreateTextCapsuleFragment : Fragment() {

    private val viewModel: CreateTextCapsuleViewModel by viewModels()

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importSelectedImage(uri)
        }
    }

    private val captureImageLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.handleCameraCaptureResult(success)
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            captureLocationUnlockTarget()
        } else {
            Snackbar.make(requireView(), R.string.location_permission_denied_message, Snackbar.LENGTH_LONG).show()
        }
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
    private lateinit var chooseUnlockDateButton: Button
    private lateinit var clearUnlockDateButton: Button
    private lateinit var timeUnlockStatusText: TextView
    private lateinit var chooseLocationUnlockButton: Button
    private lateinit var clearLocationUnlockButton: Button
    private lateinit var locationUnlockStatusText: TextView
    private lateinit var metadataOwnerText: TextView
    private lateinit var metadataMediaTypeText: TextView
    private lateinit var metadataUnlockTypeText: TextView
    private lateinit var metadataLockStatusText: TextView
    private lateinit var metadataTimestampText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_create_text_capsule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindActions()
        collectViewModel()
    }

    private fun bindViews(view: View) {
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
        chooseUnlockDateButton = view.findViewById(R.id.chooseUnlockDateButton)
        clearUnlockDateButton = view.findViewById(R.id.clearUnlockDateButton)
        timeUnlockStatusText = view.findViewById(R.id.timeUnlockStatusText)
        chooseLocationUnlockButton = view.findViewById(R.id.chooseLocationUnlockButton)
        clearLocationUnlockButton = view.findViewById(R.id.clearLocationUnlockButton)
        locationUnlockStatusText = view.findViewById(R.id.locationUnlockStatusText)
        metadataOwnerText = view.findViewById(R.id.createMetadataOwnerText)
        metadataMediaTypeText = view.findViewById(R.id.createMetadataMediaTypeText)
        metadataUnlockTypeText = view.findViewById(R.id.createMetadataUnlockTypeText)
        metadataLockStatusText = view.findViewById(R.id.createMetadataLockStatusText)
        metadataTimestampText = view.findViewById(R.id.createMetadataTimestampText)
    }

    private fun bindActions() {
        saveButton.setOnClickListener {
            viewModel.saveCapsule(
                title = titleInput.text?.toString()?.trim().orEmpty(),
                body = bodyInput.text?.toString()?.trim().orEmpty()
            )
        }

        cancelButton.setOnClickListener {
            viewModel.cleanupPendingAttachments()
            findNavController().navigateUp()
        }

        chooseImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        captureImageButton.setOnClickListener {
            viewModel.prepareCameraCapture()
        }

        removeImageButton.setOnClickListener {
            viewModel.clearSelectedImage()
        }

        chooseUnlockDateButton.setOnClickListener {
            showDatePicker()
        }

        clearUnlockDateButton.setOnClickListener {
            viewModel.clearDateUnlock()
        }

        chooseLocationUnlockButton.setOnClickListener {
            requestLocationUnlockTarget()
        }

        clearLocationUnlockButton.setOnClickListener {
            viewModel.clearLocationUnlock()
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

    private fun renderState(state: CreateTextCapsuleUiState) {
        titleLayout.error = state.titleErrorResId?.let(::getString)
        bodyLayout.error = state.bodyErrorResId?.let(::getString)
        setSavingState(state.isSaving)
        bindMetadataPreview(state.selectedImagePath, state.selectedUnlockAt, state.locationUnlockTarget)
        bindImagePreview(state.selectedImagePath)
        bindTimeUnlockPreview(state.selectedUnlockAt)
        bindLocationUnlockPreview(state.locationUnlockTarget)
    }

    private fun handleEvent(event: CreateTextCapsuleEvent) {
        when (event) {
            is CreateTextCapsuleEvent.LaunchCamera -> captureImageLauncher.launch(event.uri)
            is CreateTextCapsuleEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_SHORT).show()
            }
            CreateTextCapsuleEvent.NavigateToArchive -> findNavController().navigate(R.id.archiveFragment)
        }
    }

    private fun bindMetadataPreview(
        selectedImagePath: String?,
        selectedUnlockAt: Long?,
        locationUnlockTarget: LocationUnlockTarget?
    ) {
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
            CapsuleMetadataFormatter.unlockTypeLabel(
                requireContext(),
                when {
                    locationUnlockTarget != null -> UnlockType.LOCATION
                    selectedUnlockAt != null -> UnlockType.DATE
                    else -> UnlockType.NONE
                }
            )
        )
        metadataLockStatusText.text = getString(
            R.string.create_metadata_lock_value,
            getString(
                if (selectedUnlockAt == null && locationUnlockTarget == null) {
                    R.string.capsule_status_unlocked
                } else {
                    R.string.capsule_status_locked
                }
            )
        )
        metadataTimestampText.text = getString(R.string.create_metadata_timestamps_value)
    }

    private fun bindTimeUnlockPreview(selectedUnlockAt: Long?) {
        val hasTimeUnlock = selectedUnlockAt != null
        timeUnlockStatusText.text = if (selectedUnlockAt == null) {
            getString(R.string.time_unlock_open_now)
        } else {
            getString(R.string.time_unlock_selected, DateFormatters.formatTimestamp(selectedUnlockAt))
        }
        clearUnlockDateButton.visibility = if (hasTimeUnlock) View.VISIBLE else View.GONE
    }

    private fun bindLocationUnlockPreview(locationUnlockTarget: LocationUnlockTarget?) {
        val hasLocationUnlock = locationUnlockTarget != null
        locationUnlockStatusText.text = if (locationUnlockTarget == null) {
            getString(R.string.location_unlock_open_now)
        } else {
            getString(R.string.location_unlock_selected, locationUnlockTarget.radiusMeters)
        }
        clearLocationUnlockButton.visibility = if (hasLocationUnlock) View.VISIBLE else View.GONE
    }

    private fun bindImagePreview(selectedImagePath: String?) {
        val imageUri: Uri? = CapsuleImageStorage.uriForStoredPath(selectedImagePath)
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

    private fun setSavingState(isSaving: Boolean) {
        saveButton.isEnabled = !isSaving
        cancelButton.isEnabled = !isSaving
        chooseImageButton.isEnabled = !isSaving
        captureImageButton.isEnabled = !isSaving
        removeImageButton.isEnabled = !isSaving
        chooseUnlockDateButton.isEnabled = !isSaving
        clearUnlockDateButton.isEnabled = !isSaving
        chooseLocationUnlockButton.isEnabled = !isSaving
        clearLocationUnlockButton.isEnabled = !isSaving
        saveButton.text = getString(
            if (isSaving) R.string.saving_capsule_button else R.string.save_capsule_button
        )
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                showTimePicker(year, month, dayOfMonth)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun showTimePicker(year: Int, month: Int, dayOfMonth: Int) {
        val now = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, hourOfDay, minute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                viewModel.setDateUnlock(selectedTime.timeInMillis)
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun requestLocationUnlockTarget() {
        if (ForegroundLocationReader.hasLocationPermission(requireContext())) {
            captureLocationUnlockTarget()
            return
        }

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun captureLocationUnlockTarget() {
        val location = ForegroundLocationReader.currentBestLocation(requireContext())
        if (location == null) {
            Snackbar.make(requireView(), R.string.location_unavailable_message, Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.setLocationUnlock(location.latitude, location.longitude)
        Snackbar.make(requireView(), R.string.location_unlock_target_added_message, Snackbar.LENGTH_SHORT).show()
    }
}
