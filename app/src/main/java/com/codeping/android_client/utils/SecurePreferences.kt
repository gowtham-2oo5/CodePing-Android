package com.codeping.android_client.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePreferences {
    private const val PREFS_NAME = "codeping_secure_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHOTO_URL = "user_photo_url"
    private const val KEY_AUTH_PROVIDER = "auth_provider"
    private const val KEY_LAST_LOGIN = "last_login"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Cache the encrypted preferences instance
    @Volatile
    private var encryptedPrefs: SharedPreferences? = null
    
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return encryptedPrefs ?: synchronized(this) {
            encryptedPrefs ?: run {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                ).also { encryptedPrefs = it }
            }
        }
    }
    
    fun saveUserData(
        context: Context,
        userId: String,
        email: String?,
        name: String?,
        photoUrl: String?,
        authProvider: String
    ) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHOTO_URL, photoUrl)
            putString(KEY_AUTH_PROVIDER, authProvider)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_ID, null)
    }
    
    fun getUserEmail(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserName(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_NAME, null)
    }
    
    fun getUserPhotoUrl(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_PHOTO_URL, null)
    }
    
    fun getAuthProvider(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_AUTH_PROVIDER, null)
    }
    
    fun getLastLogin(context: Context): Long {
        return getEncryptedPrefs(context).getLong(KEY_LAST_LOGIN, 0)
    }
    
    fun isLoggedIn(context: Context): Boolean {
        return getEncryptedPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun isSessionExpired(context: Context): Boolean {
        val lastLogin = getLastLogin(context)
        val currentTime = System.currentTimeMillis()
        val threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L // 3 days
        
        return (currentTime - lastLogin) > threeDaysInMillis
    }
    
    fun clearUserData(context: Context) {
        getEncryptedPrefs(context).edit().clear().apply()
    }
    
    fun updateLastLogin(context: Context) {
        getEncryptedPrefs(context).edit()
            .putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            .apply()
    }
}
