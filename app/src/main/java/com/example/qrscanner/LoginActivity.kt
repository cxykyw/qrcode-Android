package com.example.qrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.qrscanner.api.LoginRequest
import com.example.qrscanner.api.RetrofitClient
import com.example.qrscanner.databinding.ActivityLoginBinding
import com.example.qrscanner.utils.PrefsManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val CAMERA_PERMISSION_REQUEST = 100
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            Log.d(TAG, "Starting LoginActivity")
            
            // 初始化PrefsManager
            PrefsManager.init(applicationContext)
            
            binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
            setupLoginButton()
            
            // 在应用启动时就请求相机权限
            if (!checkCameraPermission()) {
                requestCameraPermission()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "应用初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupLoginButton() {
        try {
            binding.loginButton.setOnClickListener {
                val username = binding.usernameEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 执行网络请求登录
                lifecycleScope.launch {
                    try {
                        Log.d(TAG, "开始登录请求: username=$username")
                        val loginRequest = LoginRequest(username, password)
                        val response = RetrofitClient.apiService.login(loginRequest)
                        Log.d(TAG, "收到登录响应: isSuccessful=${response.isSuccessful}, code=${response.code()}")
                        
                        if (response.isSuccessful) {
                            response.body()?.let { loginResponse ->
                                Log.d(TAG, "登录成功: token=${loginResponse.token}")
                                // 保存token和用户名
                                PrefsManager.saveToken(loginResponse.token)
                                PrefsManager.saveUsername(loginResponse.username)
                                
                                // 登录成功，跳转到主界面
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e(TAG, "登录失败: code=${response.code()}, error=$errorBody")
                            Toast.makeText(this@LoginActivity, "登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "登录出错: ${e.message}", e)
                        Toast.makeText(this@LoginActivity, "登录出错: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupLoginButton: ${e.message}", e)
            Toast.makeText(this, "设置登录按钮失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "需要相机权限来扫描二维码", Toast.LENGTH_LONG).show()
            }
            
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in requestCameraPermission: ${e.message}", e)
            Toast.makeText(this, "请求相机权限失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                CAMERA_PERMISSION_REQUEST -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Camera permission granted")
                        Toast.makeText(this, "相机权限已授予", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "Camera permission denied")
                        Toast.makeText(this, "没有相机权限将无法扫描二维码", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onRequestPermissionsResult: ${e.message}", e)
            Toast.makeText(this, "处理权限结果失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 