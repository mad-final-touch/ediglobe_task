package com.example.ediglobe_task.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.ediglobe_task.data.local.AppDatabase
import com.example.ediglobe_task.data.model.Task
import com.example.ediglobe_task.data.remote.ApiService // Added
import com.example.ediglobe_task.data.remote.RetrofitClient // Added (you'll need to create this)
import com.example.ediglobe_task.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        // You will need to create RetrofitClient or your preferred way to get ApiService instance
        val apiService = RetrofitClient.createService(ApiService::class.java)
        repository = TaskRepository(taskDao, apiService) // Pass apiService to repository
        allTasks = repository.allTasks
        // Optionally, refresh tasks when ViewModel is created and user is logged in
        // Be mindful of doing this every time if not necessary.
        // refreshTasksFromServer() 
    }

    // Call this function when you want to explicitly refresh tasks from the server
    // e.g., on a swipe-to-refresh action, or when user logs in.
    fun refreshTasksFromServer() = viewModelScope.launch {
        repository.refreshTasks()
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}

// ViewModelFactory remains the same if Application is the only dependency for TaskViewModel.
// If you were using Dagger/Hilt, this factory might change or not be needed.
class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
