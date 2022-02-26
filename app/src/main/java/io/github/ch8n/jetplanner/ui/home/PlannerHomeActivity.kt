package io.github.ch8n.jetplanner.ui.home

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ActivityPlannerHomeBinding
import io.github.ch8n.jetplanner.ui.home.adapter.TaskListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

class PlannerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlannerHomeBinding
    private lateinit var listAdapter: TaskListAdapter
    private val viewModel by lazy { PlannerHomeViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlannerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup(): Unit = with(binding) {

        val onRecyclerItemClicked: (position: Int, item: Task) -> Unit =
            { position: Int, item: Task ->
                val updated = when (item.status) {
                    TaskStatus.PENDING -> item.copy(status = TaskStatus.DONE)
                    TaskStatus.DONE -> item.copy(status = TaskStatus.FAILED)
                    TaskStatus.FAILED -> item.copy(status = TaskStatus.DONE)
                }
                viewModel.updateTask(updated)
            }

        TaskListAdapter.newInstance(onItemClicked = onRecyclerItemClicked)
            .also { listAdapter = it }
            .also { listTask.adapter = it }
            .also { LinearSnapHelper().attachToRecyclerView(listTask) }
            .also { adapter ->
                viewModel.tasks
                    .onEach { adapter.submitList(it) }
                    .launchIn(lifecycleScope)
            }
            .also { viewModel.getTasks() }
    }

}