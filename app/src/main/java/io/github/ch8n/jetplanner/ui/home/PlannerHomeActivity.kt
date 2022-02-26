package io.github.ch8n.jetplanner.ui.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.ch8n.jetplanner.R
import io.github.ch8n.jetplanner.databinding.ActivityPlannerHomeBinding
import io.github.ch8n.jetplanner.ui.home.adapter.TaskItem
import io.github.ch8n.jetplanner.ui.home.adapter.TaskListAdapter

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

class PlannerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlannerHomeBinding
    private lateinit var listAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlannerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    fun setup(): Unit = with(binding) {
        listAdapter = TaskListAdapter.newInstance(
            onItemClicked = { position: Int, item: TaskItem ->
                toast(item.name)
            }
        )

        listTask.adapter = listAdapter

        listAdapter.submitList(
            listOf(
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
                TaskItem.fake,
            )
        )

    }

}