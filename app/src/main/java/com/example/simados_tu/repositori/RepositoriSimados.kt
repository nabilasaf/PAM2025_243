package com.example.simados_tu.repositori
import com.example.simados_tu.apiservice.SimadosApiService
import com.example.simados_tu.modeldata.DetailResponse
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse

interface RepositoriSimados {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse

    suspend fun getMasterList(): List<MasterResponse>

    suspend fun getProfile(): StaffResponse

    suspend fun insertMaster(
        nim: String,
        namaAsdos: String,
        kodeMk: String,
        namaMk: String,
        sks: String,
        nipNik: String,
        namaDosen: String,
        jabatan: String
    )

    suspend fun getDetailById(id: Int): MasterResponse

    suspend fun deleteMaster(id: Int)

    suspend fun updateMaster(
        id: Int,
        data: Map<String, String>
    )
}
