package com.example.simados_tu.repositori

import android.content.Context
import com.example.simados_tu.apiservice.SimadosClient
import com.example.simados_tu.apiservice.SimadosApiService

interface AppContainer {
    val tokenManager: TokenManager
    val repositoriSimados: RepositoriSimados
}

class SimadosContainer(private val context: Context) : AppContainer {

    // 1. TokenManager untuk simpan Token (harus dibuat dulu)
    override val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    // 2. Koneksi ke Backend (ApiService)
    private val simadosApiService: SimadosApiService by lazy {
        SimadosClient.create(tokenManager)
    }

    // 3. Repositori untuk ambil data dari Backend
    override val repositoriSimados: RepositoriSimados by lazy {
        NetworkRepositoriSimados(simadosApiService)
    }
}