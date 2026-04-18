package com.echoes.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatters {
    private val archiveFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    fun formatTimestamp(timestamp: Long): String {
        return archiveFormatter.format(
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
        )
    }
}
