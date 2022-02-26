package io.github.ch8n.jetplanner.ui.home.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ListItemTaskBinding
import java.util.*


data class TaskItem(
    val id: String,
    val name: String,
    val status: TaskStatus,
    val startTime: Long,
    val endTime: Long
) {
    companion object {
        val fake
            get() = TaskItem(
                id = UUID.randomUUID().toString(),
                name = "Title - ${UUID.randomUUID()}",
                status = TaskStatus.PENDING,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis()
            )
    }
}

class TaskListAdapter private constructor(
    diffUtil: DiffUtil.ItemCallback<TaskItem>,
    private val onItemClicked: (position: Int, item: TaskItem) -> Unit
) : ListAdapter<TaskItem, TaskItemVH>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemVH {
        val binding = ListItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskItemVH(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: TaskItemVH, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {

        private val diffUtil: DiffUtil.ItemCallback<TaskItem> =
            object : DiffUtil.ItemCallback<TaskItem>() {
                override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
                    return oldItem == newItem
                }
            }

        fun newInstance(onItemClicked: (position: Int, item: TaskItem) -> Unit): TaskListAdapter {
            return TaskListAdapter(diffUtil, onItemClicked)
        }
    }

}

class TaskItemVH(
    binding: ListItemTaskBinding,
    private val onItemClicked: (position: Int, item: TaskItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val taskTextView = binding.labelTask
    private val timeTextView = binding.labelTaskTime
    private val statusImage = binding.status
    private val rootCard = binding.root
    private val context = binding.root.context

    fun onBind(taskItem: TaskItem) {
        taskTextView.text = taskItem.name
        timeTextView.text = taskItem.startTime.toString()

        statusImage.setImageResource(
            when (taskItem.status) {
                TaskStatus.PENDING -> R.drawable.pending
                TaskStatus.DONE -> R.drawable.done
                TaskStatus.FAILED -> R.drawable.close
            }
        )

        rootCard.setCardBackgroundColor(
            when (taskItem.status) {
                TaskStatus.PENDING -> ContextCompat.getColor(context, R.color.yellow)
                TaskStatus.DONE -> ContextCompat.getColor(context, R.color.green)
                TaskStatus.FAILED -> ContextCompat.getColor(context, R.color.red)
            }
        )

        rootCard.setOnClickListener {
            onItemClicked.invoke(adapterPosition, taskItem)
        }
    }
}