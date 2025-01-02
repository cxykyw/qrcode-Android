package com.example.qrscanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.qrscanner.api.RetrofitClient
import com.example.qrscanner.utils.PrefsManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

class ScannerActivity : AppCompatActivity() {
    private val TAG = "ScannerActivity"
    private lateinit var capture: CaptureManager
    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        
        barcodeView = findViewById(R.id.barcode_scanner)
        capture = CaptureManager(this, barcodeView)
        capture.initializeFromIntent(intent, savedInstanceState)
        
        barcodeView.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                result.text?.let { qrContent ->
                    handleQrCodeResult(qrContent)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    private fun handleQrCodeResult(qrContent: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "扫描到二维码: $qrContent")
                
                // 从URL中提取token
                val qrToken = qrContent.substringAfterLast("/")
                
                // 获取存储的登录token
                val token = PrefsManager.getToken()
                if (token == null) {
                    Toast.makeText(this@ScannerActivity, "请先登录", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }
                
                // 发送扫码请求
                val response = RetrofitClient.apiService.scanQrCode(
                    qrToken = qrToken,
                    authorization = "Bearer $token"
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(this@ScannerActivity, "扫码成功", Toast.LENGTH_SHORT).show()
                    // 跳转到确认登录页面
                    val intent = Intent(this@ScannerActivity, ConfirmLoginActivity::class.java).apply {
                        putExtra(ConfirmLoginActivity.EXTRA_QR_TOKEN, qrToken)
                    }
                    startActivity(intent)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "扫码失败: code=${response.code()}, error=$errorBody")
                    Toast.makeText(this@ScannerActivity, "扫码失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "扫码处理出错: ${e.message}", e)
                Toast.makeText(this@ScannerActivity, "扫码处理出错: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                finish()
            }
        }
    }
} 