package com.example.ediglobe_task.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.ediglobe_task.data.local.TaskDao
import com.example.ediglobe_task.data.model.Task
import com.example.ediglobe_task.data.remote.ApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao, private val apiService: ApiService) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun refreshTasks() {
        withContext(Dispatchers.IO) {
            val userId = currentUserId
            Log.d("TaskRepository", "Refreshing tasks for user: $userId")
            if (userId == null) {
                Log.e("TaskRepository", "User not logged in. Cannot refresh tasks.")
                // Optionally clear local tasks or handle as per app logic
                // taskDao.deleteAllTasks()
                return@withContext
            }
            try {
                val response = apiService.getTasks(userId)
                if (response.isSuccessful) {
                    val tasksMap = response.body()
                    taskDao.deleteAllTasks() // Simple approach: clear local and insert all from remote
                    tasksMap?.forEach { (firebaseKey, taskValue) ->
                        val taskWithFirebaseId = taskValue.copy(firebaseId = firebaseKey)
                        taskDao.insert(taskWithFirebaseId)
                    }
                    Log.d("TaskRepository", "Tasks refreshed successfully from remote.")
                } else {
                    Log.e("TaskRepository", "Error refreshing tasks: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("TaskRepository", "Exception refreshing tasks", e)
                // TODO: Handle network errors, maybe load from local cache only or show error
            }
        }
    }

    suspend fun insert(task: Task) {
        withContext(Dispatchers.IO) {
            val userId = currentUserId
            if (userId == null) {
                Log.e("TaskRepository", "User not logged in. Cannot insert task.")
                // TODO: Handle offline scenario - maybe save locally and sync later?
                // For now, just insert locally if you want offline-first for creates
                // taskDao.insert(task) // This would only save locally without a firebaseId
                return@withContext
            }
            try {
                // Optimistically insert into local DAO first with a temporary local ID
                // val localId = taskDao.insert(task) // If insert returned Long for ID
                // task.id = localId.toInt() // update task object if needed

                val response = apiService.createTask(userId, task)
                if (response.isSuccessful) {
                    val firebaseResponse = response.body()
                    val firebaseKey = firebaseResponse?.get("name")
                    if (firebaseKey != null) {
                        task.firebaseId = firebaseKey // Set the Firebase-generated ID
                        taskDao.insert(task) // Insert/Update in Room with the firebaseId
                        Log.d("TaskRepository", "Task inserted successfully with Firebase ID: $firebaseKey")
                    } else {
                        Log.e("TaskRepository", "Firebase key not found in response on insert.")
                        // Fallback: still insert locally but without firebaseId, or handle error
                        taskDao.insert(task) // Or decide on error handling
                    }
                } else {
                    Log.e("TaskRepository", "Error inserting task to remote: ${response.errorBody()?.string()}")
                    // Fallback: still insert locally, or handle error
                    taskDao.insert(task) // Or decide on error handling
                }
            } catch (e: Exception) {
                Log.e("TaskRepository", "Exception inserting task", e)
                // Fallback: still insert locally, or handle error
                taskDao.insert(task) // Or decide on error handling
                // TODO: Better offline handling - queue for later sync
            }
        }
    }

    suspend fun update(task: Task) {
        withContext(Dispatchers.IO) {
            val userId = currentUserId
            if (userId == null) {
                Log.e("TaskRepository", "User not logged in. Cannot update task.")
                // TODO: Handle offline scenario
                return@withContext
            }
            if (task.firebaseId == null) {
                Log.e("TaskRepository", "Firebase ID is null for task: ${task.title}. Cannot update on remote. Attempting local update.")
                // Potentially, if firebaseId is null, this task was never synced.
                // You might want to try to 'insert' it instead, or just update locally.
                taskDao.update(task)
                return@withContext
            }
            try {
                val response = apiService.updateTask(userId, task.firebaseId!!, task)
                if (response.isSuccessful) {
                    taskDao.update(task)
                    Log.d("TaskRepository", "Task updated successfully on remote and local.")
                } else {
                    Log.e("TaskRepository", "Error updating task on remote: ${response.errorBody()?.string()}")
                    // TODO: Handle error - maybe revert local change or queue for later
                }
            } catch (e: Exception) {
                Log.e("TaskRepository", "Exception updating task", e)
                // TODO: Handle network errors
            }
        }
    }

    suspend fun delete(task: Task) {
        withContext(Dispatchers.IO) {
            val userId = currentUserId
            if (userId == null) {
                Log.e("TaskRepository", "User not logged in. Cannot delete task.")
                // TODO: Handle offline scenario
                return@withContext
            }
            if (task.firebaseId == null) {
                Log.e("TaskRepository", "Firebase ID is null for task: ${task.title}. Cannot delete on remote. Attempting local delete.")
                taskDao.delete(task)
                return@withContext
            }
            try {
                val response = apiService.deleteTask(userId, task.firebaseId!!)
                if (response.isSuccessful) {
                    taskDao.delete(task)
                    Log.d("TaskRepository", "Task deleted successfully on remote and local.")
                } else {
                    Log.e("TaskRepository", "Error deleting task on remote: ${response.errorBody()?.string()}")
                    // TODO: Handle error
                }
            } catch (e: Exception) {
                Log.e("TaskRepository", "Exception deleting task", e)
                // TODO: Handle network errors
            }
        }
    }
}
