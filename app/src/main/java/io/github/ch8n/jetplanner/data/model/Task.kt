package io.github.ch8n.jetplanner.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.time.toDuration

enum class TaskStatus {
    PENDING,
    DONE,
    FAILED
}


fun Long.toTime(formatting: String = "hh:mm a"): String {
    val timeInstant = Instant.fromEpochMilliseconds(this)
    val dateTime = timeInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val timeFormat = DateTimeFormatter.ofPattern(formatting)
    // Formatter doesn't exist use Java native formatter
    return dateTime.toJavaLocalDateTime().format(timeFormat)
}


@Entity
data class Task(
    @PrimaryKey val id: String,
    val name: String,
    val status: TaskStatus,
    val startTime: Long,
    val endTime: Long,
) {

    val displayStartTime: String
        get() = startTime.toTime()

    val displayEndTime: String
        get() = endTime.toTime()

    companion object {
        val fake
            get() = Task(
                id = UUID.randomUUID().toString(),
                name = "Title -",
                status = TaskStatus.PENDING,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis(),
            )
    }
}