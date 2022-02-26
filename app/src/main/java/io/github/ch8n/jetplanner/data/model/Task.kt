package io.github.ch8n.jetplanner.data.model

import java.util.*

enum class TaskStatus {
    PENDING,
    DONE,
    FAILED
}


data class Task(
    val id: String,
    val name: String,
    val status: TaskStatus,
    val startTime: Long,
    val endTime: Long
) {
    companion object {
        val fake
            get() = Task(
                id = UUID.randomUUID().toString(),
                name = "Title - ${UUID.randomUUID()}",
                status = TaskStatus.PENDING,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis()
            )
    }
}