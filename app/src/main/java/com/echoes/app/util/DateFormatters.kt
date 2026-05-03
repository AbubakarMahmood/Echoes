package com.echoes.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Shared date/time formatting utilities for display throughout the app.
 */
object DateFormatters {
    private val archiveFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    private val shortDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun formatTimestamp(timestamp: Long): String {
        return archiveFormatter.format(
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
        )
    }

    /** Returns a short date string such as "03 May 2026". */
    fun shortDate(timestamp: Long): String {
        return shortDateFormatter.format(
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
        )
    }
}
