package com.example.simados_tu.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.repositori.RepositoriSimados
import com.example.simados_tu.repositori.TokenManager
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val dataMaster: List<MasterResponse>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repositoriSimados: RepositoriSimados,
    private val tokenManager: TokenManager
) : ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    var namaStaff by mutableStateOf("Memuat...")
        private set

    var emailStaff by mutableStateOf("-")
        private set

    // State untuk teks di Search Bar
    var searchQuery by mutableStateOf("")
        private set

    // Backup data asli dari DB untuk difilter
    private var allMasterData: List<MasterResponse> = emptyList()

    init {
        getProfileData()
        getMasterList()
    }

    // Fungsi READ Profil (REQ-AKUN-02)
    private fun getProfileData() {
        viewModelScope.launch {
            try {
                val profile = repositoriSimados.getProfile()
                namaStaff = profile.nama_staff
                emailStaff = profile.email
            } catch (e: Exception) {
                Log.e("HOME_VM", "Error Profile: ${e.message}")
                namaStaff = "Staff User"
            }
        }
    }

    fun getMasterList() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val result = repositoriSimados.getMasterList()
                allMasterData = result // Simpan ke backup
                homeUiState = HomeUiState.Success(result)
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error("Gagal mengambil data: ${e.message}")
            }
        }
    }

    // Update Query Pencarian parsial
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        filterData()
    }

    private fun filterData() {
        if (searchQuery.isEmpty()) {
            homeUiState = HomeUiState.Success(allMasterData)
        } else {
            val filteredList = allMasterData.filter {
                it.nama_lengkap.contains(searchQuery, ignoreCase = true) ||
                        it.nama_mk.contains(searchQuery, ignoreCase = true) ||
                        it.nama_dosen.contains(searchQuery, ignoreCase = true)
            }

            // Logika menampilkan pesan jika data tidak ditemukan
            if (filteredList.isEmpty()) {
                homeUiState = HomeUiState.Error("Data tidak tersedia")
            } else {
                homeUiState = HomeUiState.Success(filteredList)
            }
        }
    }

    // Fungsi Logout (REQ-AKUN-04)
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.deleteToken()
            onSuccess()
        }
    }
}