package com.example.simados_tu

import android.app.Application
import com.example.simados_tu.repositori.AppContainer
import com.example.simados_tu.repositori.SimadosContainer

class SimadosApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = SimadosContainer(
            context = this
        )
    }
}