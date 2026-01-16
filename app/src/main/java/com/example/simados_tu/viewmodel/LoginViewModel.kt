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
        // Validasi format email yang lebih permissive
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return details.email.isNotBlank() && 
               details.password.isNotBlank() &&
               details.email.matches(emailPattern.toRegex())
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
            } catch (e: retrofit2.HttpException) {
                // Handle HTTP errors
                val errorMessage = when (e.code()) {
                    401 -> "Email atau password salah"
                    404 -> "Endpoint tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${e.message()}"
                }
                uiState = uiState.copy(loginStatus = LoginStatus.Error(errorMessage))
            } catch (e: java.net.UnknownHostException) {
                // Handle network errors
                uiState = uiState.copy(
                    loginStatus = LoginStatus.Error("Tidak dapat terhubung ke server")
                )
            } catch (e: kotlinx.serialization.SerializationException) {
                // Handle JSON parsing errors
                uiState = uiState.copy(
                    loginStatus = LoginStatus.Error("Format data tidak sesuai")
                )
            } catch (e: Exception) {
                // Handle other errors
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