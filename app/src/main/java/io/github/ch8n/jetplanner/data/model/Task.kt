package io.github.ch8n.jetplanner.data.model

enum class TaskStatus {
    PENDING,
    DONE,
    FAILED
}

data class Task(
    val title: String,
    val startTime: String,
    val endTime: String,
    val status: TaskStatus
)