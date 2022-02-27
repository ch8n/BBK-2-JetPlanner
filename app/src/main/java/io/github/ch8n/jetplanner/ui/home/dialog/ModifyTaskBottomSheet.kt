package io.github.ch8n.jetplanner.ui.home.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.BottomSheetCreateTaskBinding
import io.github.ch8n.jetplanner.databinding.BottomSheetModifyTaskBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import java.util.*

class ModifyTaskBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetModifyTaskBinding
    val taskData = MutableStateFlow<Task?>(null)

    var onTaskDeleted: (Task) -> Unit = {}
    var onTaskUpdated: (Task) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSheetModifyTaskBinding
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

        binding.btmImgBtnClose.setOnClickListener {
            val currentTask = taskData.value ?: return@setOnClickListener
            onTaskDeleted.invoke(currentTask)
            dismiss()
        }

        lifecycleScope.launchWhenResumed {
            taskData.collect {
                val task = it ?: return@collect
                binding.editTaskName.editText?.setText(task.name)
                binding.editTaskFrom.editText?.setText(task.startTime.toString())
                binding.editTaskTo.editText?.setText(task.endTime.toString())
            }
        }

        binding.btmBtnSave.setOnClickListener {
            val task = taskData.value ?: return@setOnClickListener
            val taskName = binding.editTaskName.editText?.text?.toString() ?: ""
            val taskFrom = binding.editTaskFrom.editText?.text?.toString()?.toLongOrNull() ?: 0L
            val taskTo = binding.editTaskTo.editText?.text?.toString()?.toLongOrNull() ?: 0L

            if (taskName.isEmpty() || taskFrom == 0L || taskTo == 0L) {
                return@setOnClickListener
            }

            if (task.name != taskName || task.startTime != taskFrom || task.endTime != taskFrom) {
                onTaskUpdated.invoke(
                    task.copy(
                        name = taskName,
                        startTime = taskFrom,
                        endTime = taskTo
                    )
                )
            }
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModifyTaskBottomSheet"
    }
}