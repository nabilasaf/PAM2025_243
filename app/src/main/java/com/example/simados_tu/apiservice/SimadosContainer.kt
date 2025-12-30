package com.example.simados_tu.apiservice

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object SimadosClient {
    // Gunakan 10.0.2.2 jika pakai emulator untuk akses localhost laptop
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val json = Json {
        ignoreUnknownKeys = true // Agar tidak error jika API kirim data tambahan
    }

    val service: SimadosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SimadosApiService::class.java)
    }
}