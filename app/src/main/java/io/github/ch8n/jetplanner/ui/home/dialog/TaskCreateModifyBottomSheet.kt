package io.github.ch8n.jetplanner.ui.home.dialog

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.toTime
import io.github.ch8n.jetplanner.databinding.BottomSheetCreateModifyTaskBinding
import java.util.*


sealed class TaskBottomSheetType {
    data class CreateTask(
        val onCreated: (task: Task) -> Unit,
    ) : TaskBottomSheetType()

    data class ModifyTask(
        val task: Task,
        val onUpdated: (task: Task) -> Unit,
    ) : TaskBottomSheetType()
}


class TaskCreateModifyBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCreateModifyTaskBinding
    private lateinit var bottomSheetType: TaskBottomSheetType

    fun setBottomSheetType(taskBottomSheet: TaskBottomSheetType) {
        bottomSheetType = taskBottomSheet
    }

    private var currentTask: Task = Task.Empty

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSheetCreateModifyTaskBinding
        .inflate(layoutInflater)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() = with(binding) {
        bottomSheetTransparentBackground(root)
        initDefaultTask()
        autoPopulateUI()
        attachTaskNameUpdateListener()
        attachTimePicker()
        attachSaveOrUpdateBehaviour()
        attachCloseBehaviour()
    }

    private fun attachCloseBehaviour() = with(binding) {
        btmImgBtnClose.setImageResource(R.drawable.close)
        btmImgBtnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun attachSaveOrUpdateBehaviour() = with(binding) {
        btmBtnSave.setOnClickListener {
            val task = currentTask
            val isTaskNameValid = task.name.trim().isNotBlank()
            val isStartTimeValid = task.startTime != 0L
            val isEndTimeValid = task.endTime != 0L
            val isValidTask = isTaskNameValid && isStartTimeValid && isEndTimeValid
            if (isValidTask) {
                editTaskName.error = null
                when (val taskBottomSheet = bottomSheetType) {
                    is TaskBottomSheetType.CreateTask -> taskBottomSheet.onCreated.invoke(
                        task
                    )
                    is TaskBottomSheetType.ModifyTask -> taskBottomSheet.onUpdated.invoke(
                        task
                    )
                }
                dismiss()
            } else {
                editTaskName.error = when {
                    !isTaskNameValid -> "Please enter valid task name!"
                    !isStartTimeValid -> "Check start time"
                    !isEndTimeValid -> "Check end time"
                    else -> "Something went wrong!"
                }
            }
        }
    }

    private fun attachTaskNameUpdateListener() {
        binding.editTaskName.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val taskName = s?.toString() ?: ""
                currentTask = currentTask.copy(name = taskName)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun autoPopulateUI() = with(binding) {
        when (val taskBottomSheet = bottomSheetType) {
            is TaskBottomSheetType.CreateTask -> {
                btmLabelActionType.setText(getString(R.string.create_task))
            }
            is TaskBottomSheetType.ModifyTask -> with(taskBottomSheet.task) {
                btmLabelActionType.setText(getString(R.string.modify_task))
                editTaskName.editText?.setText(name)
                textTaskFrom.setText(displayStartTime)
                textTaskTo.setText(displayEndTime)
            }
        }
    }

    private fun initDefaultTask() {
        currentTask = when (val taskBottomSheet = bottomSheetType) {
            is TaskBottomSheetType.CreateTask -> Task.Empty
            is TaskBottomSheetType.ModifyTask -> taskBottomSheet.task
        }
    }

    private fun attachTimePicker() {
        var timePickerRequestCode = 1000
        val calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = calendar.get(Calendar.MINUTE)

        val onTimeSelected = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val selectedTime = calendar.time.toInstant().epochSecond
            when (timePickerRequestCode) {
                1000 -> {
                    currentTask = currentTask.copy(startTime = selectedTime)
                    binding.textTaskFrom.setText(selectedTime.toTime())
                }
                1001 -> {
                    currentTask = currentTask.copy(endTime = selectedTime)
                    binding.textTaskTo.setText(selectedTime.toTime())
                }
            }
        }

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            onTimeSelected,
            currentHour,
            currentMinute,
            false
        )

        binding.textTaskFrom.setOnClickListener {
            timePickerRequestCode = 1000
            timePickerDialog.show()
        }

        binding.textTaskTo.setOnClickListener {
            timePickerRequestCode = 1001
            timePickerDialog.show()
        }
    }

    private fun bottomSheetTransparentBackground(root: ConstraintLayout) {
        val bottomSheet = (root.parent as View)
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        const val TAG = "TASK_BOTTOM_SHEET"
    }
}