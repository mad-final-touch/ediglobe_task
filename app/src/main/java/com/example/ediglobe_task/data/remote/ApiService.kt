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
    // Note: {userId} is a placeholder for the actual authenticated user's ID.
    // This will typically be passed in from your Repository/ViewModel layer.

    @GET("users/{userId}/tasks.json")
    suspend fun getTasks(@Path("userId") userId: String): Response<Map<String, Task>>

    @POST("users/{userId}/tasks.json")
    suspend fun createTask(@Path("userId") userId: String, @Body task: Task): Response<Map<String, String>> // Firebase returns the key of the new item

    @PUT("users/{userId}/tasks/{taskId}.json")
    suspend fun updateTask(@Path("userId") userId: String, @Path("taskId") taskId: String, @Body task: Task): Response<Task>

    @DELETE("users/{userId}/tasks/{taskId}.json")
    suspend fun deleteTask(@Path("userId") userId: String, @Path("taskId") taskId: String): Response<Unit>
}
