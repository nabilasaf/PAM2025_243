package com.example.simados_tu.apiservice

import com.example.simados_tu.modeldata.AllMasterResponse
import com.example.simados_tu.modeldata.DetailResponse
import com.example.simados_tu.modeldata.LoginResponse
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.modeldata.StaffResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SimadosApiService {
    // Endpoint Login
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: Map<String, String>
    ): LoginResponse

    // Endpoint mengambil semua data master (Butuh Token JWT)
    @GET("master/list")
    suspend fun getMasterList(
        @Header("Authorization") token: String
    ): AllMasterResponse

    @GET("master/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): StaffResponse

    //Kirim request POST ke backend dengan format JSON
    @POST("master/create")
    suspend fun insertMaster(
        @Header("Authorization") token: String,
        @Body data: Map<String, String> // Mengirim JSON objek
    ): Response<Unit>

    @GET("master/detail/{id}")
    suspend fun getDetailMaster(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DetailResponse// Mengembalikan objek MasterResponse
    @DELETE("master/delete/{id}")
    suspend fun deleteMaster(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): retrofit2.Response<okhttp3.ResponseBody>

    @PUT("master/update/{id}")
    suspend fun updateMaster(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body data: Map<String, String>
    ): Response<ResponseBody>
}