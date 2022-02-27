package io.github.ch8n.jetplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.ch8n.jetplanner.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class PlannerHomeViewModel : ViewModel() {

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks = _tasks.asStateFlow()

    private val dummyList = mutableListOf<Task>().also {
        var currentMoment = Clock.System.now()
        repeat(10) { index ->
            currentMoment = currentMoment.plus(value = 2, unit = DateTimeUnit.MINUTE)
            it.add(
                Task.fake.copy(
                    name = "Title : $index $currentMoment",
                    startTime = currentMoment.toEpochMilliseconds()
                )
            )
        }
        it.sortBy { it.startTime }
    }

    fun getTasks() = viewModelScope.launch {
        _tasks.emit(dummyList)
    }

    fun addTask(task: Task) = viewModelScope.launch {
        _tasks.emit(
            (_tasks.value + task).sortedBy { it.startTime }
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
            }
                .sortedBy { it.startTime }
                .let { _tasks.emit(it) }
        }
    }

    fun getCurrentTask(): Task? =
        _tasks.value.firstOrNull { it.startTime >= System.currentTimeMillis() }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            val remainingTask = _tasks.value.filter { it.id != task.id }
            _tasks.emit(remainingTask)
        }
    }
}

