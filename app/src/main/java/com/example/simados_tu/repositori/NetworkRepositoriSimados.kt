package com.example.simados_tu.repositori

import com.example.simados_tu.apiservice.SimadosApiService
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse
import kotlinx.coroutines.flow.first
import android.util.Log

class NetworkRepositoriSimados(
    private val api: SimadosApiService
) : RepositoriSimados {

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse {
        Log.d("NetworkRepo", "Login attempt for email: $email")
        try {
            val response = api.login(
                mapOf(
                    "email" to email,
                    "password" to password
                )
            )
            Log.d("NetworkRepo", "Login successful: ${response.message}")
            return response
        } catch (e: Exception) {
            Log.e("NetworkRepo", "Login failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getMasterList(): List<MasterResponse> {
        return api.getMasterList().data
    }

    override suspend fun getProfile(): StaffResponse {
        return api.getProfile()
    }

    override suspend fun insertMaster(
        nim: String,
        namaAsdos: String,
        kodeMk: String,
        namaMk: String,
        sks: String,
        nipNik: String,
        namaDosen: String,
        jabatan: String
    ) {
        val response = api.insertMaster(
            mapOf(
                "nim" to nim,
                "nama_lengkap" to namaAsdos,
                "kode_mk" to kodeMk,
                "nama_mk" to namaMk,
                "sks" to sks,
                "nip_nik" to nipNik,
                "nama_dosen" to namaDosen,
                "jabatan" to jabatan
            )
        )

        if (!response.isSuccessful) {
            throw Exception("Gagal menyimpan data")
        }
    }

    override suspend fun getDetailById(id: Int): MasterResponse {
        return api.getDetailMaster(id).data
    }

    override suspend fun deleteMaster(id: Int) {
        val response = api.deleteMaster(id)
        if (!response.isSuccessful) {
            throw Exception("Gagal menghapus data")
        }
    }

    override suspend fun updateMaster(
        id: Int,
        data: Map<String, String>
    ) {
        val response = api.updateMaster(id, data)
        if (!response.isSuccessful) {
            throw Exception("Gagal update data")
        }
    }
}
