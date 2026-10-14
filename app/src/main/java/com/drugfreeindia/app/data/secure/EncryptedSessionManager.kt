package com.drugfreeindia.app.data.secure

import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the Supabase session in EncryptedSharedPreferences (Android Keystore AES-GCM)
 * instead of the library's default plain SharedPreferences. Tokens are never logged.
 */
@Singleton
class EncryptedSessionManager @Inject constructor(
    private val securePrefs: SecurePreferencesManager
) : SessionManager {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    override suspend fun saveSession(session: UserSession) = withContext(Dispatchers.IO) {
        securePrefs.saveString(KEY, json.encodeToString(UserSession.serializer(), session))
    }

    override suspend fun loadSession(): UserSession? = withContext(Dispatchers.IO) {
        val raw = securePrefs.getString(KEY) ?: return@withContext null
        runCatching { json.decodeFromString(UserSession.serializer(), raw) }.getOrNull()
    }

    override suspend fun deleteSession() = withContext(Dispatchers.IO) {
        securePrefs.remove(KEY)
    }

    private companion object { const val KEY = "supabase_session_v1" }
}
