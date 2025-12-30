package com.example.simados_tu.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simados_tu.SimadosApplication
import com.example.simados_tu.repositori.SimadosContainer
import com.example.simados_tu.repositori.TokenManager
import com.example.simados_tu.viewmodel.DetailViewModel
import com.example.simados_tu.viewmodel.HomeViewModel
import com.example.simados_tu.viewmodel.LoginViewModel
import com.example.simados_tu.viewmodel.TambahViewModel
import com.example.simados_tu.viewmodel.UpdateViewModel


object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            // Pastikan casting ke SimadosContainer agar bisa ambil tokenManager
            val container = simadosApp().container as SimadosContainer
            LoginViewModel(
                repositoriSimados = container.repositoriSimados,
                tokenManager = simadosApp().container.tokenManager // Berikan akses tokenManager
            )
        }
        initializer {
            val container = simadosApp().container as SimadosContainer
            HomeViewModel(
                repositoriSimados = container.repositoriSimados,
                tokenManager = simadosApp().container.tokenManager// Berikan akses tokenManager
            )
        }
        initializer {
            val container = simadosApp().container as SimadosContainer
            TambahViewModel(
                repositoriSimados = simadosApp().container.repositoriSimados,
                tokenManager = simadosApp().container.tokenManager// Berikan akses tokenManager
            )
        }
        initializer {
            DetailViewModel(
                savedStateHandle = this.createSavedStateHandle(), // PENTING untuk ID navigasi
                repositoriSimados = simadosApp().container.repositoriSimados,
                tokenManager = simadosApp().container.tokenManager
            )
        }
        initializer {
            UpdateViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                repositoriSimados = simadosApp().container.repositoriSimados,
                tokenManager = simadosApp().container.tokenManager
            )
        }
    }
}

// Fungsi bantu untuk memanggil Application class
fun CreationExtras.simadosApp(): SimadosApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SimadosApplication)