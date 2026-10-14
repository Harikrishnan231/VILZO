package com.drugfreeindia.app.data.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                "dfi_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to standard context shared prefs if Keystore hardware is unavailable in specific test environments
            context.getSharedPreferences("dfi_secure_fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveString(key: String, value: String) {
        securePrefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String? = null): String? {
        return securePrefs.getString(key, default)
    }

    fun clearAll() {
        securePrefs.edit().clear().apply()
    }
}
