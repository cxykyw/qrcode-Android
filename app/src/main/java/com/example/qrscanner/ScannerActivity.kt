package com.example.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.qrscanner.api.ApiClient
import com.example.qrscanner.databinding.ActivityScannerBinding
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.launch

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private val CAMERA_PERMISSION_REQUEST = 100
    private var isProcessingQrCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (hasCameraPermission()) {
            startScanning()
        } else {
            requestCameraPermission()
        }
    }

    private fun startScanning() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (!isProcessingQrCode) {
                        handleScanResult(it.text)
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {}
        })
    }

    private fun handleScanResult(result: String) {
        isProcessingQrCode = true
        binding.barcodeScanner.pause()

        // 解析二维码内容，获取qrcodeId
        val qrcodeId = try {
            result.substringAfterLast("/")
        } catch (e: Exception) {
            Toast.makeText(this, "无效的二维码格式", Toast.LENGTH_SHORT).show()
            isProcessingQrCode = false
            binding.barcodeScanner.resume()
            return
        }

        // 调用接口处理扫描结果
        lifecycleScope.launch {
            try {
                // 这里使用模拟的token，实际应用中应该使用真实的用户token
                val token = "Bearer your-auth-token"
                val response = ApiClient.api.scanQrCode(qrcodeId, token)
                
                if (response.success) {
                    // 扫描成功，调用确认接口
                    val confirmResponse = ApiClient.api.confirmLogin(qrcodeId, token)
                    if (confirmResponse.success) {
                        Toast.makeText(this@ScannerActivity, "登录确认成功", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ScannerActivity, confirmResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ScannerActivity, response.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ScannerActivity, "网络请求失败: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isProcessingQrCode = false
                binding.barcodeScanner.resume()
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "需要相机权限才能扫描二维码", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }
} 