package com.example.techpulse.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    val api: TechPulseApi by lazy {
        Retrofit.Builder()
            .baseUrl(TechPulseApi.GITHUB_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TechPulseApi::class.java)
    }
}