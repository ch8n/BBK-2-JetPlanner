package io.github.ch8n.jetplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()

    init {
        viewModelScope.launch {
            checkTaskStatus()
        }
    }

    fun observeTask() = taskRepository
        .getTasks()
        .onEach {
            _tasks.emit(it)
        }
        .launchIn(viewModelScope)


    fun addTask(task: Task) = viewModelScope.launch {
        taskRepository.addUpdateTask(task)
    }

    private suspend fun checkTaskStatus() {
        while (true) {
            setCurrentTask()
            checkTaskTimePassed()
            delay(1000)
        }
    }

    private suspend fun checkTaskTimePassed() {
        val failedTask = _tasks.value
            .filter { it.endTime < System.currentTimeMillis() }
            .filter { it.status == TaskStatus.PENDING }
            .map { it.copy(status = TaskStatus.FAILED) }

        if (failedTask.isNotEmpty()) {
            taskRepository.addUpdateTask(*failedTask.toTypedArray())
        }
    }

    private suspend fun setCurrentTask() {
        val currentTask = _tasks.value.firstOrNull {
            it.startTime <= System.currentTimeMillis() && it.status == TaskStatus.PENDING
        }
        _currentTask.emit(currentTask)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.removeTask(task)
    }
}

