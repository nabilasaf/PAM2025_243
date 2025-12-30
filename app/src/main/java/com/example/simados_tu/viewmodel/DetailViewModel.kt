package com.example.simados_tu.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.repositori.RepositoriSimados
import com.example.simados_tu.repositori.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//Definisi status UI
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val data: MasterResponse) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriSimados: RepositoriSimados,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Menangkap ID dari PetaNavigasi
    private val idMaster: Int = checkNotNull(savedStateHandle["idMaster"])

    var uiState by mutableStateOf<DetailUiState>(DetailUiState.Loading)
    var showDeleteDialog by mutableStateOf(false)

    init {
        getDetail()
    }

    fun getDetail() {
        viewModelScope.launch {
            uiState = DetailUiState.Loading
            try {
                val token = tokenManager.getToken.first()
                if (token != null) {
                    val result = repositoriSimados.getDetailById(token, idMaster)
                    uiState = DetailUiState.Success(result)
                }
            } catch (e: Exception) {
                uiState = DetailUiState.Error("Gagal memuat detail: ${e.message}")
            }
        }
    }

    fun hapusData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken.first()
                if (token != null) {
                    repositoriSimados.deleteMaster(token, idMaster)
                    onSuccess()
                }
            } catch (e: Exception) {
                // Log error hapus jika diperlukan
            }
        }
    }
}