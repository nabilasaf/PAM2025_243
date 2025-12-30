package com.example.simados_tu.repositori

import com.example.simados_tu.apiservice.SimadosApiService
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse
import com.example.simados_tu.modeldata.UpdateUiState
import retrofit2.http.DELETE


class NetworkRepositoriSimados(
    private val simadosApiService: SimadosApiService
) : RepositoriSimados {

    override suspend fun login(username: String, password: String): LoginResponse {
        val request = mapOf("username" to username, "password" to password)
        return simadosApiService.login(request)
    }

    override suspend fun getMasterList(token: String): List<MasterResponse> {
        // Tambahkan kata "Bearer " di depan token untuk otentikasi JWT
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
        // Menyusun data ke Map agar menjadi JSON di Backend
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
        return response.data // mengambil isi MasterResponse dari dalam properti data
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


