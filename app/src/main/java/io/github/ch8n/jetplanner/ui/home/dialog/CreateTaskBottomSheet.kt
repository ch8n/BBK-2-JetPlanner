package io.github.ch8n.jetplanner.ui.home.dialog

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.BottomSheetCreateTaskBinding
import java.util.*


class CreateTaskBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCreateTaskBinding
    var onTaskCreated: (Task) -> Unit = {}

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

        val bottomSheet = (view.parent as View)
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)



        binding.btmBtnSave.setOnClickListener {
            val taskName = binding.editTaskName.editText?.text?.toString() ?: ""
            val taskFrom = binding.textTaskFrom.text?.toString() ?: ""
            val taskTo = binding.textTaskFrom.text?.toString() ?: ""
            onTaskCreated.invoke(
                Task(
                    id = UUID.randomUUID().toString(),
                    name = taskName,
                    startTime = taskFrom.toLongOrNull() ?: 0L,
                    endTime = taskTo.toLongOrNull() ?: 0L,
                    status = TaskStatus.PENDING
                )
            )
            dismiss()
        }

        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context, { view, hourOfDay, minute ->
                binding.textTaskFrom.setText("$hourOfDay:$minute")
            },
            hour,
            minute,
            false
        )

        binding.btmImgBtnClose.setOnClickListener {
            dismiss()
        }

        binding.textTaskFrom.setOnClickListener {
            timePickerDialog.show()
        }

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}