package io.github.ch8n.jetplanner.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ActivityPlannerHomeBinding
import io.github.ch8n.jetplanner.databinding.LayoutCreateTaskBinding
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
        initTaskList()
        initCurrentTaskBottomSheet()

    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private fun initCurrentTaskBottomSheet(): Unit = with(binding) {
        //#2 Initializing the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(includedCreateTask.bottomSheet)
        //#3 Listening to State Changes of BottomSheet
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


    }

    private fun initTaskList(): Unit = with(binding) {
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