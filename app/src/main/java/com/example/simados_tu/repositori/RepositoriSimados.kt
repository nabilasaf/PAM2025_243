package com.example.simados_tu.repositori
import com.example.simados_tu.apiservice.SimadosApiService
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse

interface RepositoriSimados {
    // Fungsi untuk Login
    suspend fun login(username: String, password: String): LoginResponse

    // Fungsi untuk ambil data list (butuh token)
    suspend fun getMasterList(token: String): List<MasterResponse>

    //Fungsi untuk ambil data staff
    suspend fun getProfile(token: String): StaffResponse

    // Fungsi untuk ambil data detail (butuh token)
    suspend fun getDetailById(token: String, id: Int): MasterResponse

    //Menghapus data
    suspend fun deleteMaster(token: String, id: Int)

    // Fungsi untuk tambah data master (butuh token)
    suspend fun insertMaster(
        token: String,
        nim: String,
        namaAsdos: String,
        kodeMk: String,
        namaMk: String,
        sks: String,
        nipNik: String,
        namaDosen: String,
        jabatan: String
    )
    suspend fun updateMaster(token: String, id: Int, data: Map<String, String>)
}


