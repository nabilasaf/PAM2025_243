package com.example.simados_tu.apiservice

import com.example.simados_tu.repositori.TokenManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.simados_tu.apiservice.SimadosClient
import com.example.simados_tu.apiservice.SimadosApiService
import java.util.concurrent.TimeUnit


object SimadosClient {

    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun create(tokenManager: TokenManager): SimadosApiService {

        // HTTP Logging Interceptor untuk debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor) // Tambahkan logging
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout koneksi
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout baca
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout tulis
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(SimadosApiService::class.java)
    }
}
