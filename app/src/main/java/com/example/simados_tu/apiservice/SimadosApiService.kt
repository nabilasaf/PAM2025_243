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

    @POST("auth/login")
    suspend fun login(
        @Body body: Map<String, String>
    ): LoginResponse

    @GET("master/list")
    suspend fun getMasterList(): AllMasterResponse

    @GET("master/profile")
    suspend fun getProfile(): StaffResponse

    @POST("master/create")
    suspend fun insertMaster(
        @Body data: Map<String, String>
    ): Response<Unit>

    @GET("master/detail/{id}")
    suspend fun getDetailMaster(
        @Path("id") id: Int
    ): DetailResponse

    @DELETE("master/delete/{id}")
    suspend fun deleteMaster(
        @Path("id") id: Int
    ): Response<ResponseBody>

    @PUT("master/update/{id}")
    suspend fun updateMaster(
        @Path("id") id: Int,
        @Body data: Map<String, String>
    ): Response<ResponseBody>
}
