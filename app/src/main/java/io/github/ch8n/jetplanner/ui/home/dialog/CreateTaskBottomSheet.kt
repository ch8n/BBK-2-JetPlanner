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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.data.model.toTime
import io.github.ch8n.jetplanner.databinding.BottomSheetCreateTaskBinding
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*


class CreateTaskBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCreateTaskBinding
    var onTaskCreated: (Task) -> Unit = {}

    private val taskData = MutableStateFlow<Task>(
        Task(
            id = UUID.randomUUID().toString(),
            name = "",
            startTime = 0L,
            endTime = 0L,
            status = TaskStatus.PENDING
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSheetCreateTaskBinding
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
        attachTimePicker()

        editTaskName.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val taskName = s?.toString() ?: ""
                taskData.tryEmit(taskData.value.copy(name = taskName))
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btmBtnSave.setOnClickListener {
            val task = taskData.value
            val isTaskNameValid = task.name.trim().isNotBlank()
            val isStartTimeValid = task.startTime != 0L
            val isEndTimeValid = task.endTime != 0L
            val isValidTask = isTaskNameValid && isStartTimeValid && isEndTimeValid

            if (isValidTask) {
                editTaskName.error = null
                onTaskCreated.invoke(task)
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

        btmImgBtnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun attachTimePicker() {
        var timePickerRequestCode = 1000
        val calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = calendar.get(Calendar.MINUTE)

        val onTimeSelected = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            when {
                timePickerRequestCode == 1000 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val selectedTime = calendar.timeInMillis
                    taskData.tryEmit(taskData.value.copy(startTime = selectedTime))
                    binding.textTaskFrom.setText(selectedTime.toTime())
                }
                timePickerRequestCode == 1001 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val selectedTime = calendar.timeInMillis
                    taskData.tryEmit(taskData.value.copy(endTime = selectedTime))
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
        const val TAG = "ModalBottomSheet"
    }
}