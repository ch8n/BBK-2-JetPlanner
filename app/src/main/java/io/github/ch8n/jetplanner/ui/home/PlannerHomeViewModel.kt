package io.github.ch8n.jetplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.ch8n.jetplanner.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerHomeViewModel : ViewModel() {

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks = _tasks.asStateFlow()

    fun getTasks() = viewModelScope.launch {
        _tasks.emit(
            listOf(
                Task.fake,
                Task.fake,
                Task.fake,
                Task.fake,
                Task.fake,
                Task.fake,
                Task.fake,
                Task.fake,
            )
        )
    }

    fun addTask(task: Task) = viewModelScope.launch {
        _tasks.emit(
            _tasks.value + task
        )
    }

    fun getTask(id: String): Task? = _tasks.value.find { it.id == id }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            _tasks.value.map {
                if (it.id == updatedTask.id) {
                    updatedTask
                } else {
                    it
                }
            }.let {
                _tasks.emit(it)
            }
        }
    }

}

