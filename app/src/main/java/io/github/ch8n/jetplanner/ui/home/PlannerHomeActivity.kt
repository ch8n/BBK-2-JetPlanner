package io.github.ch8n.jetplanner.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ActivityPlannerHomeBinding
import io.github.ch8n.jetplanner.ui.home.adapter.TaskListAdapter
import io.github.ch8n.jetplanner.ui.home.dialog.TaskBottomSheet
import io.github.ch8n.jetplanner.ui.home.dialog.TaskBottomSheetType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun String.capitalFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun View.setVisible(isVisible: Boolean) {
    if (isVisible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@AndroidEntryPoint
class PlannerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlannerHomeBinding
    private lateinit var listAdapter: TaskListAdapter

    private val viewModel: PlannerHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlannerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        initDate()
        val taskBottomSheet = TaskBottomSheet()
        initTaskList(taskBottomSheet)
        initCurrentTaskBottomSheet(taskBottomSheet)
    }

    private fun initDate() = with(binding) {
        val currentMoment = Clock.System.now()
        val dateTimeInUTC = currentMoment.toLocalDateTime(TimeZone.UTC)
        labelDay.text = "${dateTimeInUTC.dayOfWeek.name.capitalFirst()},"
        labelDate.text = dateTimeInUTC.date
            .let { "${it.dayOfMonth} ${it.month.name.capitalFirst()} ${it.year}" }
    }

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private fun initCurrentTaskBottomSheet(taskBottomSheet: TaskBottomSheet): Unit = with(binding) {
        bottomSheetBehavior = BottomSheetBehavior.from(includedCreateTask.bottomSheet)
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        })

        includedCreateTask.btmImgBtnCreateTask.setOnClickListener {
            if (!taskBottomSheet.isVisible) {
                taskBottomSheet.setBottomSheetType(
                    TaskBottomSheetType.CreateBottomSheet(
                        onCreated = {
                            viewModel.addTask(it)
                        }
                    )
                )
                taskBottomSheet.show(supportFragmentManager, TaskBottomSheet.TAG)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentTask.collect {
                val task = it
                includedCreateTask.btmLabelTaskTitle.setText(
                    task?.name ?: getString(R.string.nothing_for_now)
                )
                includedCreateTask.btmBtnDone.setOnClickListener {
                    if (task != null) {
                        viewModel.addTask(task.copy(status = TaskStatus.DONE))
                    }
                }
            }
        }

    }

    private fun initTaskList(taskBottomSheet: TaskBottomSheet): Unit = with(binding) {

        val onRecyclerItemClicked: (position: Int, item: Task) -> Unit =
            { position: Int, item: Task ->
                val updated = when (item.status) {
                    TaskStatus.PENDING -> item.copy(status = TaskStatus.DONE)
                    TaskStatus.DONE -> item.copy(status = TaskStatus.FAILED)
                    TaskStatus.FAILED -> item.copy(status = TaskStatus.DONE)
                }
                viewModel.addTask(updated)
            }

        val onRecyclerItemLongClick: (position: Int, item: Task) -> Unit =
            { position: Int, item: Task ->
                if (!taskBottomSheet.isVisible) {
                    taskBottomSheet.setBottomSheetType(
                        TaskBottomSheetType.ModifyBottomSheet(
                            task = item,
                            onUpdated = {
                                viewModel.addTask(it)
                            },
                            onDeleted = {
                                viewModel.deleteTask(it)
                            }
                        )
                    )
                    taskBottomSheet.show(supportFragmentManager, TaskBottomSheet.TAG)
                }
            }

        TaskListAdapter.newInstance(
            onItemClicked = onRecyclerItemClicked,
            onItemLongClick = onRecyclerItemLongClick
        )
            .also { listAdapter = it }
            .also { listTask.adapter = it }
            .also { adapter ->
                viewModel.tasks
                    .onEach {
                        adapter.submitList(it) {

                            imageWork.setVisible(it.isEmpty())
                            labelWork.setVisible(it.isEmpty())

                            bottomSheetBehavior?.state = if (it.isEmpty()) {
                                BottomSheetBehavior.STATE_COLLAPSED
                            } else {
                                BottomSheetBehavior.STATE_EXPANDED
                            }
                        }
                    }
                    .launchIn(lifecycleScope)
            }
            .also { viewModel.observeTask() }

    }

}