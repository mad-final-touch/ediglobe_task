package com.example.ediglobe_task.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // Local Room ID
    var firebaseId: String? = null, // Firebase's String ID
    var title: String,
    var description: String? = null, // Or whatever fields you have
    var isCompleted: Boolean = false
)