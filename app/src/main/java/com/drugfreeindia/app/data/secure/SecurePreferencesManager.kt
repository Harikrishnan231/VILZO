package com.drugfreeindia.app.data.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keystore-backed (AES-256-GCM) encrypted preferences. Holds the auth session and
 * trusted contacts. If the Keystore is unusable we fail closed for writes of sensitive data
 * rather than silently writing plain text.
 */
@Singleton
class SecurePreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val securePrefs: SharedPreferences? by lazy {
        runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "dfi_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }.getOrNull()
    }

    val isAvailable: Boolean get() = securePrefs != null

    fun saveString(key: String, value: String) {
        securePrefs?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String, default: String? = null): String? =
        securePrefs?.getString(key, default) ?: default

    fun remove(key: String) {
        securePrefs?.edit()?.remove(key)?.apply()
    }

    fun clearAll() {
        securePrefs?.edit()?.clear()?.apply()
        // Remove the legacy plain-text fallback file used by older builds, if present.
        context.deleteSharedPreferences("dfi_secure_fallback_prefs")
    }
}
