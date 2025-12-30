package com.example.simados_tu.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simados_tu.modeldata.UpdateUiState
import com.example.simados_tu.modeldata.toUpdateUiState
import com.example.simados_tu.repositori.RepositoriSimados
import com.example.simados_tu.repositori.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdateViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriSimados: RepositoriSimados,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Ambil ID dari argument navigasi
    private val idMaster: Int = checkNotNull(savedStateHandle["idMaster"])

    // State UI untuk mengontrol form dan validasi
    var uiState by mutableStateOf(UpdateUiState())
        private set

    init {
        loadDataLama()
    }

    //Memuat data lama untuk pre-filled form
    private fun loadDataLama() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken.first()
                if (token != null) {
                    val data = repositoriSimados.getDetailById(token, idMaster)
                    // Konversi data master ke UI state dan jalankan validasi awal
                    val loadedState = data.toUpdateUiState()
                    uiState = loadedState.copy(isEntryValid = validateInput(loadedState))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Gagal memuat data lama: ${e.message}")
            }
        }
    }

    // Fungsi Validasi: Memastikan semua field wajib tidak kosong
    private fun validateInput(uiState: UpdateUiState): Boolean {
        return with(uiState) {
            nim.isNotBlank() &&
                    nama_lengkap.isNotBlank() &&
                    kode_mk.isNotBlank() &&
                    nama_mk.isNotBlank() &&
                    sks.isNotBlank() &&
                    nip_nik.isNotBlank() &&
                    nama_dosen.isNotBlank()
        }
    }

    // Fungsi untuk memperbarui state setiap kali user mengetik di form
    fun updateUiState(newState: UpdateUiState) {
        uiState = newState.copy(isEntryValid = validateInput(newState))
    }

    // REQ-UPDATE-05: Mengirim data yang diperbarui ke backend (MySQL)
    fun simpanUpdate(onSuccess: () -> Unit) {
        if (!uiState.isEntryValid) return // Keamanan tambahan sebelum mengirim

        viewModelScope.launch {
            try {
                val token = tokenManager.getToken.first()
                if (token != null) {
                    val body = mapOf(
                        "nim" to uiState.nim,
                        "nama_lengkap" to uiState.nama_lengkap,
                        "kode_mk" to uiState.kode_mk,
                        "nama_mk" to uiState.nama_mk,
                        "sks" to uiState.sks,
                        "nip_nik" to uiState.nip_nik,
                        "nama_dosen" to uiState.nama_dosen,
                        "jabatan" to uiState.jabatan,
                        "status_aktif_asdos" to uiState.status_aktif_asdos.toString()
                    )
                    // Eksekusi request PUT
                    repositoriSimados.updateMaster(token, idMaster, body)
                    onSuccess() // Navigasi kembali
                }
            } catch (e: Exception) {
                //Menampilkan pesan kesalahan jika gagal
                uiState = uiState.copy(errorMessage = "Gagal memperbarui data: ${e.message}")
            }
        }
    }
}