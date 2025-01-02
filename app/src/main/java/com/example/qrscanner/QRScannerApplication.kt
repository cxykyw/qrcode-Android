package com.example.qrscanner

import android.app.Application
import android.util.Log

class QRScannerApplication : Application() {
    private val TAG = "QRScannerApplication"

    override fun onCreate() {
        try {
            super.onCreate()
            Log.d(TAG, "Application onCreate")
            // 在这里初始化全局组件
        } catch (e: Exception) {
            Log.e(TAG, "Error in Application onCreate: ${e.message}", e)
        }
    }
} 