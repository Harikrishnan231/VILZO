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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dfi_settings")

data class AppPrefs(
    val welcomeSeen: Boolean = false,
    val reduceMotion: Boolean = false,
    val relaxedMode: Boolean = false,
    val language: String = "en",
    /** Last city chosen on this device (slug) – used for Explore/Centres filtering offline. */
    val citySlug: String? = null,
    val showSampleData: Boolean = false
)

@Singleton
class PrefsStore @Inject constructor(@ApplicationContext private val context: Context) {
    private object K {
        val WELCOME = booleanPreferencesKey("welcome_seen")
        val REDUCE = booleanPreferencesKey("reduce_motion")
        val RELAXED = booleanPreferencesKey("relaxed_mode")
        val LANG = stringPreferencesKey("language")
        val CITY = stringPreferencesKey("city_slug")
        val SAMPLE = booleanPreferencesKey("debug_sample_data")
    }

    val prefs: Flow<AppPrefs> = context.dataStore.data.map {
        AppPrefs(
            welcomeSeen = it[K.WELCOME] ?: false,
            reduceMotion = it[K.REDUCE] ?: false,
            relaxedMode = it[K.RELAXED] ?: false,
            language = it[K.LANG] ?: "en",
            citySlug = it[K.CITY],
            showSampleData = it[K.SAMPLE] ?: false
        )
    }

    suspend fun current(): AppPrefs = prefs.first()
    suspend fun setWelcomeSeen() = context.dataStore.edit { it[K.WELCOME] = true }
    suspend fun setReduceMotion(v: Boolean) = context.dataStore.edit { it[K.REDUCE] = v }
    suspend fun setRelaxedMode(v: Boolean) = context.dataStore.edit { it[K.RELAXED] = v }
    suspend fun setLanguage(v: String) = context.dataStore.edit { it[K.LANG] = v }
    suspend fun setCitySlug(v: String?) = context.dataStore.edit { if (v == null) it.remove(K.CITY) else it[K.CITY] = v }
    suspend fun setShowSampleData(v: Boolean) = context.dataStore.edit { it[K.SAMPLE] = v }
    suspend fun clear() = context.dataStore.edit { it.clear() }
}
