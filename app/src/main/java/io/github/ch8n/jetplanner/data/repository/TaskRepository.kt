package io.github.ch8n.jetplanner.data.repository

import io.github.ch8n.jetplanner.data.local.sources.TaskDao
import io.github.ch8n.jetplanner.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    suspend fun getTasks(): Flow<List<Task>> = withContext(Dispatchers.IO) {
        taskDao.getAll()
    }

    suspend fun addUpdateTask(task: Task): Unit = withContext(Dispatchers.IO) {
        taskDao.insertAll(task)
    }

    suspend fun removeTask(task: Task): Unit = withContext(Dispatchers.IO) {
        taskDao.delete(task)
    }

}
