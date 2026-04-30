package com.echoes.app.util

import android.content.Context
import com.echoes.app.R
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.CapsuleMetadata
import com.echoes.app.data.local.model.UnlockType

object CapsuleMetadataFormatter {

    fun ownerSummary(context: Context, metadata: CapsuleMetadata): String {
        val ownerName = metadata.ownerDisplayName ?: context.getString(R.string.metadata_owner_unknown)
        return context.getString(R.string.metadata_owner_summary, ownerName, metadata.ownerId)
    }

    fun unlockTypeLabel(context: Context, unlockType: UnlockType): String {
        val labelRes = when (unlockType) {
            UnlockType.NONE -> R.string.unlock_type_none
            UnlockType.DATE -> R.string.unlock_type_date
            UnlockType.LOCATION -> R.string.unlock_type_location
            UnlockType.EVENT -> R.string.unlock_type_event
        }
        return context.getString(labelRes)
    }

    fun lockStatusLabel(context: Context, metadata: CapsuleMetadata): String {
        return context.getString(
            if (metadata.isLocked) R.string.capsule_status_locked else R.string.capsule_status_unlocked
        )
    }

    fun unlockScheduleLabel(context: Context, metadata: CapsuleMetadata): String? {
        return when (metadata.unlockType) {
            UnlockType.DATE -> {
                if (metadata.unlockAt == null) return null

                context.getString(
                    if (metadata.isLocked) R.string.unlock_schedule_locked_until else R.string.unlock_schedule_unlocked_at,
                    DateFormatters.formatTimestamp(metadata.unlockAt)
                )
            }

            UnlockType.LOCATION -> {
                val radiusMeters = metadata.radiusMeters ?: return null
                context.getString(
                    if (metadata.isLocked) R.string.unlock_location_locked_near else R.string.unlock_location_unlocked,
                    radiusMeters
                )
            }

            UnlockType.NONE,
            UnlockType.EVENT -> null
        }
    }

    fun mediaTypeLabel(context: Context, mediaType: CapsuleMediaType): String {
        val labelRes = when (mediaType) {
            CapsuleMediaType.TEXT -> R.string.media_type_text_only
            CapsuleMediaType.IMAGE -> R.string.media_type_text_plus_image
            CapsuleMediaType.AUDIO -> R.string.media_type_text_plus_audio
        }
        return context.getString(labelRes)
    }
}
