package com.example.qrscanner.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Header

interface QrCodeApi {
    @GET("api/qrcode/generate")
    suspend fun generateQrCode(): QrCodeResponse

    @GET("api/qrcode/check/{qrcodeId}")
    suspend fun checkQrCodeStatus(@Path("qrcodeId") qrcodeId: String): QrCodeStatusResponse

    @POST("api/qrcode/scan/{qrcodeId}")
    suspend fun scanQrCode(
        @Path("qrcodeId") qrcodeId: String,
        @Header("Authorization") token: String
    ): BaseResponse

    @POST("api/qrcode/confirm/{qrcodeId}")
    suspend fun confirmLogin(
        @Path("qrcodeId") qrcodeId: String,
        @Header("Authorization") token: String
    ): BaseResponse
} 