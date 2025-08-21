package com.example.ediglobe_task.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ediglobe_task.data.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
