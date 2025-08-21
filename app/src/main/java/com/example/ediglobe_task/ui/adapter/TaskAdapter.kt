package com.example.ediglobe_task.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ediglobe_task.data.model.Task
import com.example.ediglobe_task.databinding.ItemTaskBinding // Assuming you will create item_task.xml

class TaskAdapter(
    private val onTaskClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskClicked(getItem(position))
                }
            }
            binding.buttonDelete.setOnClickListener { // Assuming a delete button with id buttonDelete in item_task.xml
                 val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClicked(getItem(position))
                }
            }
        }

        fun bind(task: Task) {
            binding.textViewTaskTitle.text = task.title // Assuming a TextView with id textViewTaskTitle
            binding.checkBoxTaskCompleted.isChecked = task.isCompleted // Assuming a CheckBox with id checkBoxTaskCompleted

            binding.checkBoxTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onTaskClicked(task) // Or a specific update status function if you prefer
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task):
 Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
