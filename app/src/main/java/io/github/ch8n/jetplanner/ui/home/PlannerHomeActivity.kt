package io.github.ch8n.jetplanner.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ActivityPlannerHomeBinding
import io.github.ch8n.jetplanner.ui.home.adapter.TaskListAdapter
import io.github.ch8n.jetplanner.ui.home.dialog.CreateTaskBottomSheet
import io.github.ch8n.jetplanner.ui.home.dialog.ModifyTaskBottomSheet
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
        initTaskList()
        initCurrentTaskBottomSheet()

    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private fun initCurrentTaskBottomSheet(): Unit = with(binding) {
        val createTaskBottomSheet = CreateTaskBottomSheet()

        createTaskBottomSheet.onTaskCreated = { newTask ->
            viewModel.addTask(newTask)
        }
        bottomSheetBehavior = BottomSheetBehavior.from(includedCreateTask.bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        val currentTask = viewModel.getCurrentTask()
                        includedCreateTask.btmLabelTaskTitle.text =
                            currentTask?.name ?: "Nothing right now...."
                    }
                }
            }
        })

        includedCreateTask.btmImgBtnCreateTask.setOnClickListener {
            createTaskBottomSheet.show(supportFragmentManager, CreateTaskBottomSheet.TAG)
        }

    }

    private fun initTaskList(): Unit = with(binding) {
        val modifyTaskBottomSheet = ModifyTaskBottomSheet()

        modifyTaskBottomSheet.onTaskUpdated = {
            viewModel.updateTask(it)
        }

        modifyTaskBottomSheet.onTaskDeleted = {
            viewModel.deleteTask(it)
        }

        val onRecyclerItemClicked: (position: Int, item: Task) -> Unit =
            { position: Int, item: Task ->
                val updated = when (item.status) {
                    TaskStatus.PENDING -> item.copy(status = TaskStatus.DONE)
                    TaskStatus.DONE -> item.copy(status = TaskStatus.FAILED)
                    TaskStatus.FAILED -> item.copy(status = TaskStatus.DONE)
                }
                viewModel.updateTask(updated)
            }

        val onRecyclerItemLongClick: (position: Int, item: Task) -> Unit =
            { position: Int, item: Task ->
                modifyTaskBottomSheet.show(supportFragmentManager, ModifyTaskBottomSheet.TAG)
                modifyTaskBottomSheet.taskData.tryEmit(item)
            }

        TaskListAdapter.newInstance(
            onItemClicked = onRecyclerItemClicked,
            onItemLongClick = onRecyclerItemLongClick
        )
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