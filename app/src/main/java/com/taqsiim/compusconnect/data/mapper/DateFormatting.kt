package com.taqsiim.compusconnect.data.mapper

import com.taqsiim.compusconnect.data.model.Comment
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.PendingEvent
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.model.Reservation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val isoInputPatterns = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    "yyyy-MM-dd'T'HH:mm:ssXXX",
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
    "yyyy-MM-dd'T'HH:mm:ssZ"
)

private val dateTimeOutputFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

private fun String.normalizeIsoFraction(): String {
    // SimpleDateFormat can fail on >3 millisecond digits; trim to 3 when needed.
    val fractionRegex = Regex("\\.(\\d{3})\\d+")
    return replace(fractionRegex, ".$1")
}

fun String.formatIsoDateTime(): String {
    val normalized = normalizeIsoFraction()

    for (pattern in isoInputPatterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val parsed: Date = parser.parse(normalized) ?: continue
            return dateTimeOutputFormat.format(parsed)
        } catch (_: Exception) {
            // Try next known pattern.
        }
    }

    // If parsing fails, keep the source value to avoid breaking UI.
    return this
}

fun Post.formatDates(): Post = copy(createdAt = createdAt.formatIsoDateTime())

fun Comment.formatDates(): Comment = copy(createdAt = createdAt.formatIsoDateTime())

fun Event.formatDates(): Event = copy(
    startTime = startTime.formatIsoDateTime(),
    endTime = endTime.formatIsoDateTime()
)

fun PendingEvent.formatDates(): PendingEvent = copy(
    startTime = startTime.formatIsoDateTime(),
    endTime = endTime.formatIsoDateTime()
)

fun Reservation.formatDates(): Reservation = copy(
    startTime = startTime.formatIsoDateTime(),
    endTime = endTime.formatIsoDateTime()
)
