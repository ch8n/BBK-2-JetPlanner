package io.github.ch8n.jetplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerHomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()

    init {
        viewModelScope.launch {
            checkCurrentTaskChange()
        }
    }

    fun observeTask() = viewModelScope.launch {
        taskRepository
            .getTasks()
            .collect {
                _tasks.emit(it)
            }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        taskRepository.addUpdateTask(task)
    }

    private suspend fun checkCurrentTaskChange() {
        while (true) {
            val currentTask = _tasks.value.firstOrNull {
                it.startTime <= System.currentTimeMillis() && it.status == TaskStatus.PENDING
            }
            _currentTask.emit(currentTask)
            delay(1000 * 3)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.removeTask(task)
    }
}

