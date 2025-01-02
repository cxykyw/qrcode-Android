package com.example.qrscanner.api

import com.example.qrscanner.data.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val username: String,
    val password: String
)

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>
    
    @POST("api/qrcode/scan/{qrToken}")
    suspend fun scanQrCode(
        @Path("qrToken") qrToken: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @POST("api/qrcode/confirm/{qrToken}")
    suspend fun confirmQrLogin(
        @Path("qrToken") qrToken: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>
} 