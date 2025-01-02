package com.example.qrscanner

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.qrscanner.api.RetrofitClient
import com.example.qrscanner.utils.PrefsManager
import kotlinx.coroutines.launch

class ConfirmLoginActivity : AppCompatActivity() {
    private val TAG = "ConfirmLoginActivity"

    companion object {
        const val EXTRA_QR_TOKEN = "qr_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_login)

        val qrToken = intent.getStringExtra(EXTRA_QR_TOKEN)
        if (qrToken == null) {
            Toast.makeText(this, "无效的二维码", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 确认登录按钮
        findViewById<android.widget.Button>(R.id.confirmButton).setOnClickListener {
            confirmLogin(qrToken)
        }

        // 取消按钮
        findViewById<android.widget.Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }

    private fun confirmLogin(qrToken: String) {
        lifecycleScope.launch {
            try {
                val token = PrefsManager.getToken()
                if (token == null) {
                    Toast.makeText(this@ConfirmLoginActivity, "请先登录", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                val response = RetrofitClient.apiService.confirmQrLogin(
                    qrToken = qrToken,
                    authorization = "Bearer $token"
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@ConfirmLoginActivity, "确认登录成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "确认登录失败: code=${response.code()}, error=$errorBody")
                    Toast.makeText(this@ConfirmLoginActivity, "确认登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "确认登录出错: ${e.message}", e)
                Toast.makeText(this@ConfirmLoginActivity, "确认登录出错: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 