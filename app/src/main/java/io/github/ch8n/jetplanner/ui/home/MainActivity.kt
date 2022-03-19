package io.github.ch8n.jetplanner.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ActivityMainBinding
import io.github.ch8n.jetplanner.ui.home.adapter.TaskListAdapter
import io.github.ch8n.jetplanner.ui.home.dialog.TaskBottomSheetType
import io.github.ch8n.jetplanner.ui.home.dialog.TaskCreateModifyBottomSheet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: TaskListAdapter

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        initDate()
        val taskBottomSheet = TaskCreateModifyBottomSheet()
        initTaskList(taskBottomSheet)
        initCurrentTaskBottomSheet(taskBottomSheet)
    }

    private fun String.capitalFirst(): String {
        return this.lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun View.setVisible(isVisible: Boolean) {
        if (isVisible) {
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    private fun initDate() = with(binding) {
        val currentMoment = Clock.System.now()
        val dateTimeInUTC = currentMoment.toLocalDateTime(TimeZone.UTC)
        labelDay.text = "${dateTimeInUTC.dayOfWeek.name.capitalFirst()},"
        labelDate.text = dateTimeInUTC.date
            .let { "${it.dayOfMonth} ${it.month.name.capitalFirst()} ${it.year}" }
    }

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private fun initCurrentTaskBottomSheet(taskBottomSheet: TaskCreateModifyBottomSheet): Unit =
        with(binding) {
            bottomSheetBehavior = BottomSheetBehavior.from(includedCurrentTask.bottomSheet)

            includedCurrentTask.btmImgBtnCreateTask.setOnClickListener {
                if (!taskBottomSheet.isVisible) {
                    taskBottomSheet.setBottomSheetType(
                        TaskBottomSheetType.CreateTask(
                            onCreated = {
                                viewModel.addTask(it)
                            }
                        )
                    )
                    taskBottomSheet.show(supportFragmentManager, TaskCreateModifyBottomSheet.TAG)
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentTask.collect {
                val task = it
                includedCurrentTask.btmLabelTaskTitle.setText(
                    task?.name ?: getString(R.string.nothing_for_now)
                )
                includedCurrentTask.btmBtnDone.setOnClickListener {
                    if (task != null) {
                        viewModel.addTask(task.copy(status = TaskStatus.DONE))
                    }
                }
            }
        }

    }

    private fun initTaskList(taskBottomSheet: TaskCreateModifyBottomSheet): Unit = with(binding) {

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
                        TaskBottomSheetType.ModifyTask(
                            task = item,
                            onUpdated = {
                                viewModel.addTask(it)
                            }
                        )
                    )
                    taskBottomSheet.show(supportFragmentManager, TaskCreateModifyBottomSheet.TAG)
                }
            }

        TaskListAdapter.newInstance(
            onItemClicked = onRecyclerItemClicked,
            onItemLongClick = onRecyclerItemLongClick
        )
            .also { listAdapter = it }
            .also { listTask.adapter = it }
            .also { adapter ->
                viewModel.observeTask()
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
            .also {
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val pos = viewHolder.adapterPosition
                        val item = listAdapter.getItemAt(pos)
                        if (item != null) {
                            viewModel.deleteTask(item)
                        }
                    }
                }
                ).attachToRecyclerView(listTask)
            }


    }

}