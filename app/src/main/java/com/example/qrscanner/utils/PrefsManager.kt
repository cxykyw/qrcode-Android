package com.example.qrscanner.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {
    private const val PREFS_NAME = "QRScannerPrefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }
    
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
} 