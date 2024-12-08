package com.example.modibot.Services

import com.google.android.gms.common.api.Response
import okhttp3.Interceptor

class ApiKeyInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain):okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "AIzaSyAIm0zwsRb4wvZLuTPNYj0cnyek038QYp4") // API anahtarını buraya ekle
            .build()
        return chain.proceed(request)
    }
}