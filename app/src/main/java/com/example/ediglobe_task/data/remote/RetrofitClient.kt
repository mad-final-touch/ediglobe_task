package com.example.ediglobe_task.data.remote

import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
object RetrofitClient {
  // Replace with your Firebase Realtime Database URL
  private const val BASE_URL = "https://ediglobe-task-default-rtdb.firebaseio.com/"

  val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY  // Options: NONE, BASIC, HEADERS, BODY
  }

  val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  fun <T> createService(serviceClass: Class<T>): T {
    return retrofit.create(serviceClass)
  }
}
        