package com.example.simados_tu.repositori

import android.content.Context
import com.example.simados_tu.apiservice.SimadosClient
import com.example.simados_tu.apiservice.SimadosApiService

interface AppContainer {
    val tokenManager: TokenManager
    val repositoriSimados: RepositoriSimados
}

class SimadosContainer(private val context: Context) : AppContainer {

    // 1. Koneksi ke Backend (ApiService)
    private val simadosApiService: SimadosApiService by lazy {
        SimadosClient.service
    }

    // 2. Repositori untuk ambil data dari Backend
    override val repositoriSimados: RepositoriSimados by lazy {
        NetworkRepositoriSimados(simadosApiService)
    }
    // 3. TokenManager untuk simpan Token
    override val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }
}