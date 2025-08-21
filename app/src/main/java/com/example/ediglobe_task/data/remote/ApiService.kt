package com.example.ediglobe_task.data.remote

import com.example.ediglobe_task.data.model.Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("tasks.json") // Assuming Firebase Realtime Database endpoint
    suspend fun getTasks(): Response<Map<String, Task>> // Firebase returns objects as Maps

    @POST("tasks.json")
    suspend fun createTask(@Body task: Task): Response<Map<String, String>> // Firebase returns the key of the new item

    @PUT("tasks/{id}.json")
    suspend fun updateTask(@Path("id") taskId: String, @Body task: Task): Response<Task>

    @DELETE("tasks/{id}.json")
    suspend fun deleteTask(@Path("id") taskId: String): Response<Unit>
}
