package com.echoes.app.ui.archive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.echoes.app.R
import com.echoes.app.data.local.model.CapsuleRecord
import com.echoes.app.util.CapsuleMetadataFormatter
import com.echoes.app.util.DateFormatters

class ArchiveCapsuleAdapter(
    private val onCapsuleSelected: (CapsuleRecord) -> Unit
) : RecyclerView.Adapter<ArchiveCapsuleAdapter.ArchiveCapsuleViewHolder>() {

    private val items = mutableListOf<CapsuleRecord>()

    fun submitList(capsules: List<CapsuleRecord>) {
        items.clear()
        items.addAll(capsules)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveCapsuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_archive_capsule, parent, false)
        return ArchiveCapsuleViewHolder(view, onCapsuleSelected)
    }

    override fun onBindViewHolder(holder: ArchiveCapsuleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ArchiveCapsuleViewHolder(
        itemView: View,
        private val onCapsuleSelected: (CapsuleRecord) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.archiveItemTitle)
        private val statusText: TextView = itemView.findViewById(R.id.archiveItemStatus)
        private val metadataText: TextView = itemView.findViewById(R.id.archiveItemMetadata)
        private val unlockScheduleText: TextView = itemView.findViewById(R.id.archiveItemUnlockSchedule)
        private val timestampText: TextView = itemView.findViewById(R.id.archiveItemTimestamp)
        private val previewText: TextView = itemView.findViewById(R.id.archiveItemPreview)

        fun bind(record: CapsuleRecord) {
            val capsule = record.capsule
            val metadata = record.metadata

            titleText.text = capsule.title
            statusText.text = itemView.context.getString(
                if (metadata.isLocked) R.string.capsule_status_locked else R.string.capsule_status_unlocked
            )
            metadataText.text = itemView.context.getString(
                R.string.archive_item_metadata_summary,
                CapsuleMetadataFormatter.ownerSummary(itemView.context, metadata),
                CapsuleMetadataFormatter.mediaTypeLabel(itemView.context, capsule.mediaType),
                CapsuleMetadataFormatter.unlockTypeLabel(itemView.context, metadata.unlockType)
            )
            val unlockSchedule = CapsuleMetadataFormatter.unlockScheduleLabel(itemView.context, metadata)
            unlockScheduleText.visibility = if (unlockSchedule == null) View.GONE else View.VISIBLE
            unlockScheduleText.text = unlockSchedule
            timestampText.text = itemView.context.getString(
                if (metadata.hasBeenEdited) R.string.archive_item_updated_at else R.string.archive_item_created_at,
                DateFormatters.formatTimestamp(if (metadata.hasBeenEdited) metadata.updatedAt else metadata.createdAt)
            )
            previewText.text = capsule.storyText

            itemView.setOnClickListener {
                onCapsuleSelected(record)
            }
        }
    }
}
