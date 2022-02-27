package io.github.ch8n.jetplanner.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.model.TaskStatus
import io.github.ch8n.jetplanner.databinding.ListItemTaskBinding


class TaskListAdapter private constructor(
    diffUtil: DiffUtil.ItemCallback<Task>,
    private val onItemClicked: (position: Int, item: Task) -> Unit,
    private val onItemLongClick: (position: Int, item: Task) -> Unit
) : ListAdapter<Task, TaskItemVH>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemVH {
        val binding = ListItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskItemVH(binding, onItemClicked, onItemLongClick)
    }

    override fun onBindViewHolder(holder: TaskItemVH, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {

        private val diffUtil: DiffUtil.ItemCallback<Task> =
            object : DiffUtil.ItemCallback<Task>() {
                override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                    return oldItem == newItem
                }
            }

        fun newInstance(
            onItemClicked: (position: Int, item: Task) -> Unit,
            onItemLongClick: (position: Int, item: Task) -> Unit
        ): TaskListAdapter {
            return TaskListAdapter(diffUtil, onItemClicked, onItemLongClick)
        }
    }

}

class TaskItemVH(
    binding: ListItemTaskBinding,
    private val onItemClicked: (position: Int, item: Task) -> Unit,
    private val onItemLongClick: (position: Int, item: Task) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val taskTextView = binding.labelTask
    private val timeTextView = binding.labelTaskTime
    private val statusImage = binding.status
    private val rootCard = binding.root
    private val context = binding.root.context

    fun onBind(taskItem: Task) {
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

        rootCard.setOnLongClickListener {
            onItemLongClick.invoke(adapterPosition, taskItem)
            true
        }
    }
}