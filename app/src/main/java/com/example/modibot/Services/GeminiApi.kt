package com.example.modibot.Services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface GeminiApiService {
    @POST("models/gemini-1.5-flash-latest:generateContent") // Gemini API endpoint'inizi burada belirtin
    fun getSuggestions(@Body requestBody: RequestBody): Call<YourResponseModel>
}
