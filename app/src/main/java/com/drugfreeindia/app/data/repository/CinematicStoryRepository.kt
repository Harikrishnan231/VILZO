package com.drugfreeindia.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.drugfreeindia.app.domain.model.CinematicStory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cinematicDataStore by preferencesDataStore(name = "dfi_cinematic_story")

@Serializable
data class CinematicProgress(
    val storyId: String,
    val currentSceneId: String,
    val routeHistory: List<String> = emptyList(),
    val choiceHistory: List<String> = emptyList(),
    val completed: Boolean = false,
    val lastEndingId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Singleton
class CinematicStoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false; isLenient = true }

    private val cachedStories = mutableMapOf<String, CinematicStory>()

    fun loadStory(storyId: String): CinematicStory? {
        cachedStories[storyId]?.let { return it }
        return runCatching {
            val fileName = when (storyId) {
                "the_changes_nobody_noticed" -> "seed/the_changes_nobody_noticed.json"
                else -> "seed/$storyId.json"
            }
            val content = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val story = json.decodeFromString<CinematicStory>(content)
            cachedStories[storyId] = story
            story
        }.getOrNull()
    }

    suspend fun getProgress(storyId: String): CinematicProgress? {
        val key = stringPreferencesKey("progress_$storyId")
        return context.cinematicDataStore.data.map { prefs ->
            prefs[key]?.let { str ->
                runCatching { json.decodeFromString<CinematicProgress>(str) }.getOrNull()
            }
        }.first()
    }

    suspend fun saveProgress(progress: CinematicProgress) {
        val key = stringPreferencesKey("progress_${progress.storyId}")
        val str = json.encodeToString(CinematicProgress.serializer(), progress)
        context.cinematicDataStore.edit { prefs ->
            prefs[key] = str
        }
    }

    suspend fun clearProgress(storyId: String) {
        val key = stringPreferencesKey("progress_$storyId")
        context.cinematicDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}
