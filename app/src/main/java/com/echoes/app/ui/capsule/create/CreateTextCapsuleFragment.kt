package com.echoes.app.ui.capsule.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTextCapsuleFragment : Fragment() {

    private lateinit var titleLayout: TextInputLayout
    private lateinit var bodyLayout: TextInputLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var bodyInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

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

        saveButton.setOnClickListener {
            saveCapsule()
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
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
            mediaType = CapsuleMediaType.TEXT,
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
                    database.userDao().upsertUser(SeedData.localUser(now))
                    database.capsuleDao().upsertCapsule(capsule)
                    database.unlockConditionDao().upsertUnlockCondition(unlockCondition)
                }
            }.onSuccess {
                setSavingState(false)
                Snackbar.make(requireView(), R.string.capsule_saved_message, Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            }.onFailure {
                setSavingState(false)
                Snackbar.make(requireView(), R.string.capsule_save_failed_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setSavingState(isSaving: Boolean) {
        saveButton.isEnabled = !isSaving
        cancelButton.isEnabled = !isSaving
        saveButton.text = getString(
            if (isSaving) R.string.saving_capsule_button else R.string.save_capsule_button
        )
    }

    companion object {
        private const val TITLE_MIN_LENGTH = 3
        private const val BODY_MIN_LENGTH = 10
    }
}
