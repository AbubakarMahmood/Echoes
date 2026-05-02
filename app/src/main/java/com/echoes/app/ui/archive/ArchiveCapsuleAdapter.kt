package com.echoes.app.ui.archive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.echoes.app.R
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.util.DateFormatters

class ArchiveCapsuleAdapter(
    private val onCapsuleSelected: (CapsuleEntity) -> Unit
) : RecyclerView.Adapter<ArchiveCapsuleAdapter.ArchiveCapsuleViewHolder>() {

    private val items = mutableListOf<CapsuleEntity>()

    fun submitList(capsules: List<CapsuleEntity>) {
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
        private val onCapsuleSelected: (CapsuleEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.archiveItemTitle)
        private val statusText: TextView = itemView.findViewById(R.id.archiveItemStatus)
        private val timestampText: TextView = itemView.findViewById(R.id.archiveItemTimestamp)
        private val previewText: TextView = itemView.findViewById(R.id.archiveItemPreview)

        fun bind(capsule: CapsuleEntity) {
            titleText.text = capsule.title
            statusText.text = itemView.context.getString(
                if (capsule.isLocked) R.string.capsule_status_locked else R.string.capsule_status_unlocked
            )
            timestampText.text = itemView.context.getString(
                R.string.archive_item_created_at,
                DateFormatters.formatTimestamp(capsule.createdAt)
            )
            previewText.text = capsule.storyText

            itemView.setOnClickListener {
                onCapsuleSelected(capsule)
            }
        }
    }
}
