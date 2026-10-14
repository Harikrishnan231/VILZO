package com.drugfreeindia.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dfi_settings")

@Singleton
class DfiDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_ANON_ID = stringPreferencesKey("anon_id")
        val KEY_NICKNAME = stringPreferencesKey("nickname")
        val KEY_COLLEGE_CODE = stringPreferencesKey("college_code")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETED] ?: false }

    val userNickname: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_NICKNAME] ?: "Calm Falcon" }

    val collegeCode: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_COLLEGE_CODE] }

    val preferredLanguage: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_LANGUAGE] ?: "en" }

    val isReduceMotion: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_REDUCE_MOTION] ?: false }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setUserProfile(anonId: String, nickname: String, collegeCode: String?, language: String) {
        context.dataStore.edit {
            it[KEY_ANON_ID] = anonId
            it[KEY_NICKNAME] = nickname
            if (collegeCode != null) it[KEY_COLLEGE_CODE] = collegeCode
            it[KEY_LANGUAGE] = language
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REDUCE_MOTION] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
