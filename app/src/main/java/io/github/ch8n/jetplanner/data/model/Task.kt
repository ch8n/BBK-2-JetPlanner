package io.github.ch8n.jetplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
        val Empty
            get() = Task(
                id = UUID.randomUUID().toString(),
                name = "",
                status = TaskStatus.PENDING,
                startTime = 0,
                endTime = 0,
            )
    }
}