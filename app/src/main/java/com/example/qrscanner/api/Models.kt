package com.example.qrscanner.api

data class BaseResponse(
    val code: Int,
    val message: String,
    val success: Boolean
)

data class QrCodeResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: QrCodeData
)

data class QrCodeData(
    val qrcodeId: String,
    val qrcodeUrl: String
)

data class QrCodeStatusResponse(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: QrCodeStatusData
)

data class QrCodeStatusData(
    val status: Int, // 0: 未扫描, 1: 已扫描, 2: 已确认
    val scannedTime: Long?,
    val confirmedTime: Long?
) 