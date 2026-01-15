package com.example.simados_tu.repositori

import com.example.simados_tu.apiservice.SimadosApiService
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse
import kotlinx.coroutines.flow.first

class NetworkRepositoriSimados(
    private val simadosApiService: SimadosApiService
) : RepositoriSimados {

    override suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val request = mapOf("email" to email, "password" to password)
            simadosApiService.login(request)
        } catch (e: retrofit2.HttpException) {
            // Parse error berdasarkan status code
            val errorMessage = when (e.code()) {
                401 -> "Email atau password salah"
                404 -> "User tidak ditemukan"
                500 -> "Server sedang bermasalah, coba lagi nanti"
                else -> "Gagal login: ${e.message()}"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            // Error lain (network, timeout, dll)
            throw Exception("Tidak dapat terhubung ke server")
        }
    }

    override suspend fun getMasterList(token: String): List<MasterResponse> {
        val response = simadosApiService.getMasterList("Bearer $token")
        return response.data
    }

    override suspend fun getProfile(token: String): StaffResponse {
        return simadosApiService.getProfile("Bearer $token")
    }

    override suspend fun insertMaster(
        token: String, nim: String, namaAsdos: String, kodeMk: String,
        namaMk: String, sks: String, nipNik: String, namaDosen: String, jabatan: String
    ) {
        val data = mapOf(
            "nim" to nim,
            "nama_lengkap" to namaAsdos,
            "kode_mk" to kodeMk,
            "nama_mk" to namaMk,
            "sks" to sks,
            "nip_nik" to nipNik,
            "nama_dosen" to namaDosen,
            "jabatan" to jabatan
        )

        val response = simadosApiService.insertMaster("Bearer $token", data)

        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Gagal simpan"
            throw Exception(errorMsg)
        }
    }

    override suspend fun getDetailById(token: String, id: Int): MasterResponse {
        val response = simadosApiService.getDetailMaster("Bearer $token", id)
        return response.data
    }

    override suspend fun deleteMaster(token: String, id: Int) {
        val response = simadosApiService.deleteMaster("Bearer $token", id)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Gagal menghapus data"
            throw Exception(errorMsg)
        }
    }

    override suspend fun updateMaster(token: String, id: Int, data: Map<String, String>) {
        val response = simadosApiService.updateMaster("Bearer $token", id, data)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Gagal update data"
            throw Exception(errorMsg)
        }
    }
}