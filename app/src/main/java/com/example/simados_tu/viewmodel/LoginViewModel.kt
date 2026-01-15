package com.example.simados_tu.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simados_tu.repositori.RepositoriSimados
import com.example.simados_tu.repositori.TokenManager
import kotlinx.coroutines.launch

// 1. Definisikan State UI
data class LoginUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val isEntryValid: Boolean = false,
    val loginStatus: LoginStatus = LoginStatus.Idle
)

data class LoginDetails(
    val email: String = "",
    val password: String = ""
)

sealed interface LoginStatus {
    object Idle : LoginStatus
    object Loading : LoginStatus
    data class Success(val token: String) : LoginStatus
    data class Error(val message: String) : LoginStatus
}

// 2. Implementasi ViewModel
class LoginViewModel(
    private val repositoriSimados: RepositoriSimados,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateUiState(loginDetails: LoginDetails) {
        uiState = uiState.copy(
            loginDetails = loginDetails,
            isEntryValid = validateInput(loginDetails)
        )
    }

    private fun validateInput(details: LoginDetails): Boolean {
        return details.email.isNotBlank() && details.password.isNotBlank()
    }

    fun login() {
        if (!uiState.isEntryValid) return

        viewModelScope.launch {
            uiState = uiState.copy(loginStatus = LoginStatus.Loading)
            try {
                val response = repositoriSimados.login(
                    uiState.loginDetails.email,
                    uiState.loginDetails.password
                )

                tokenManager.saveToken(response.token)

                uiState = uiState.copy(loginStatus = LoginStatus.Success(response.token))
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loginStatus = LoginStatus.Error(e.message ?: "Login Gagal")
                )
            }
        }
    }

    // Fungsi untuk reset status login (dismiss alert)
    fun resetLoginStatus() {
        uiState = uiState.copy(loginStatus = LoginStatus.Idle)
    }
}