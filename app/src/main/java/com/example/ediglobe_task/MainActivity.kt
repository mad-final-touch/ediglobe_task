package com.example.ediglobe_task

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ediglobe_task.data.model.Task
import com.example.ediglobe_task.databinding.ActivityMainBinding
import com.example.ediglobe_task.ui.adapter.TaskAdapter
import com.example.ediglobe_task.ui.viewmodel.TaskViewModel
import com.example.ediglobe_task.ui.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        setupRecyclerView()

        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        }

        binding.buttonAddTask.setOnClickListener {
            val taskTitle = binding.editTextNewTask.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                taskViewModel.insert(Task(title = taskTitle))
                binding.editTextNewTask.text.clear()
            } else {
                Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                // Task clicked - toggle status and update
                val updatedTask = task.copy(isCompleted = !task.isCompleted)
                taskViewModel.update(updatedTask)
            },
            onDeleteClicked = { task ->
                // Delete task
                taskViewModel.delete(task)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}
