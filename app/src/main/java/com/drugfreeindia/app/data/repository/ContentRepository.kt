package com.drugfreeindia.app.data.repository

import android.content.Context
import com.drugfreeindia.app.data.local.AppDatabase
import com.drugfreeindia.app.data.local.CacheEntry
import com.drugfreeindia.app.data.local.Connectivity
import com.drugfreeindia.app.data.remote.AppError
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.AppConfig
import com.drugfreeindia.app.domain.model.AppConfigRow
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.City
import com.drugfreeindia.app.domain.model.College
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.Level
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.model.SupportCentre
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/** Where the data on screen came from – used for the discreet "Offline" / "Saved on this device" labels. */
enum class Source { REMOTE, CACHE, BUNDLED }

data class Loaded<T>(val data: T, val source: Source, val error: AppError? = null)

val AppJson = Json { ignoreUnknownKeys = true; explicitNulls = false; encodeDefaults = true; isLenient = true }

/** Bundled seed (assets/seed) – same content as supabase/04_seed.sql. */
@Singleton
class SeedSource @Inject constructor(@ApplicationContext private val context: Context) {
    @Serializable private data class SeedCity(val slug: String, val name: String, val state: String? = null, val sort_order: Int = 100)
    @Serializable private data class SeedCollege(val city: String, val name: String)
    @Serializable private data class SeedCentre(val city: String, val name: String, val type: String)
    @Serializable private data class SeedConfig(val min_cohort: Int = 10, val levels: List<Level> = emptyList(), val privacy_policy_url: String? = null, val terms_url: String? = null)
    @Serializable private data class SeedRef(
        val cities: List<SeedCity>, val colleges: List<SeedCollege>, val helplines: List<Helpline>,
        val centres: List<SeedCentre>, val badges: List<Badge>, val config: SeedConfig
    )

    private val ref: SeedRef by lazy { AppJson.decodeFromString(read("seed/reference.json")) }
    val stories: List<Story> by lazy { AppJson.decodeFromString(read("seed/stories.json")) }
    val activities: List<Activity> by lazy {
        runCatching { AppJson.decodeFromString<List<Activity>>(read("seed/activities.json")) }.getOrDefault(emptyList())
    }

    private fun read(path: String) = context.assets.open(path).bufferedReader().use { it.readText() }

    // Bundled rows have no server UUIDs, so we use a stable "local:" id derived from the slug/name.
    val cities get() = ref.cities.map { City(id = "local:${it.slug}", name = it.name, state = it.state, slug = it.slug, sortOrder = it.sort_order) }
    val colleges get() = ref.colleges.map { College(id = "local:${it.city}:${it.name.hashCode()}", cityId = "local:${it.city}", name = it.name) }
    val helplines get() = ref.helplines.mapIndexed { i, h -> h.copy(id = "local:h$i") }
    val centres get() = ref.centres.mapIndexed { i, c ->
        SupportCentre(id = "local:c$i", cityId = "local:${c.city}", name = c.name, type = c.type,
            cityName = ref.cities.firstOrNull { it.slug == c.city }?.name)
    }
    val badges get() = ref.badges
    val config get() = AppConfig(ref.config.min_cohort, ref.config.levels.ifEmpty { AppConfig().levels }, ref.config.privacy_policy_url, ref.config.terms_url)
}

/**
 * Public reference/content data: remote first when online, Room cache otherwise,
 * bundled seed as the last resort. Works before sign-in (anon RLS read policies).
 */
@Singleton
class ContentRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val db: AppDatabase,
    private val seed: SeedSource,
    private val connectivity: Connectivity
) {
    private val online get() = isSupabaseConfigured && connectivity.isOnline.value

    /** Generic cache-aside loader used by every list in the app. */
    suspend fun <T> load(
        key: String,
        serializer: KSerializer<T>,
        bundled: () -> T,
        forceRemote: Boolean = true,
        remote: suspend () -> T
    ): Loaded<T> = withContext(Dispatchers.IO) {
        var error: AppError? = null
        if (online && forceRemote) {
            try {
                val data = remote()
                db.cache().put(CacheEntry(key, AppJson.encodeToString(serializer, data)))
                return@withContext Loaded(data, Source.REMOTE)
            } catch (c: kotlinx.coroutines.CancellationException) { throw c } catch (t: Throwable) {
                error = t.toAppError()
            }
        }
        db.cache().get(key)?.let { entry ->
            runCatching { AppJson.decodeFromString(serializer, entry.json) }.getOrNull()?.let {
                return@withContext Loaded(it, Source.CACHE, error)
            }
        }
        Loaded(bundled(), Source.BUNDLED, error)
    }

    fun <T> stream(key: String, serializer: KSerializer<T>, bundled: () -> T, remote: suspend () -> T): Flow<Loaded<T>> = flow {
        // Emit local immediately, then the fresh copy.
        emit(load(key, serializer, bundled, forceRemote = false, remote))
        if (online) emit(load(key, serializer, bundled, forceRemote = true, remote))
    }

    suspend fun cities() = load("cities", ListSerializer(City.serializer()), { seed.cities }) {
        supabase.from("cities").select { order("sort_order", Order.ASCENDING) }.decodeList<City>()
    }

    suspend fun colleges(cityId: String): Loaded<List<College>> {
        if (cityId.startsWith("local:")) {
            return Loaded(seed.colleges.filter { it.cityId == cityId }, Source.BUNDLED)
        }
        return load("colleges:$cityId", ListSerializer(College.serializer()), {
            // Map seed colleges by city slug when the server is unreachable.
            emptyList()
        }) {
            supabase.from("colleges").select {
                filter { eq("city_id", cityId) }
                order("name", Order.ASCENDING)
            }.decodeList<College>()
        }
    }

    suspend fun allColleges(): Loaded<List<College>> {
        return load("all_colleges", ListSerializer(College.serializer()), {
            seed.colleges.sortedBy { it.name }
        }) {
            supabase.from("colleges").select {
                filter { eq("is_active", true) }
                order("name", Order.ASCENDING)
            }.decodeList<College>()
        }
    }

    suspend fun helplines() = load("helplines", ListSerializer(Helpline.serializer()), { seed.helplines }) {
        supabase.from("helplines").select { order("sort_order", Order.ASCENDING) }.decodeList<Helpline>()
    }

    suspend fun centres(): Loaded<List<SupportCentre>> {
        val cities = cities().data
        val loaded = load("centres", ListSerializer(SupportCentre.serializer()), { seed.centres }) {
            supabase.from("support_centres").select { order("name", Order.ASCENDING) }.decodeList<SupportCentre>()
        }
        return loaded.copy(data = loaded.data.map { c ->
            c.copy(cityName = c.cityName ?: cities.firstOrNull { it.id == c.cityId }?.name
                ?: seed.cities.firstOrNull { it.id == c.cityId }?.name)
        })
    }

    suspend fun badges() = load("badges", ListSerializer(Badge.serializer()), { seed.badges }) {
        supabase.from("badges").select { order("sort_order", Order.ASCENDING) }.decodeList<Badge>()
    }

    suspend fun stories(): Loaded<List<Story>> {
        val loaded = load("stories", ListSerializer(Story.serializer()), { seed.stories }) {
            supabase.from("stories").select { order("sort_order", Order.ASCENDING) }.decodeList<Story>()
        }
        // Always offer bundled stories too (server may only have some of them published).
        val ids = loaded.data.map { it.id }.toSet()
        val merged = (loaded.data + seed.stories.filter { it.id !in ids }).sortedBy { it.sortOrder }
        return loaded.copy(data = merged)
    }

    suspend fun story(id: String): Story? = stories().data.firstOrNull { it.id == id }

    /** Set of story ids that are published on the server (only these can be scored). */
    suspend fun serverStoryIds(): Set<String> {
        val entry = db.cache().get("stories") ?: return emptySet()
        return runCatching { AppJson.decodeFromString(ListSerializer(Story.serializer()), entry.json).map { it.id }.toSet() }.getOrDefault(emptySet())
    }

    suspend fun config(): Loaded<AppConfig> {
        val rows = load("app_config", ListSerializer(AppConfigRow.serializer()), { emptyList() }) {
            supabase.from("app_config").select().decodeList<AppConfigRow>()
        }
        if (rows.data.isEmpty()) return Loaded(seed.config, rows.source, rows.error)
        val map = rows.data.associate { it.key to it.value }
        val base = seed.config
        val cfg = AppConfig(
            minCohort = runCatching { map["min_cohort"]?.jsonPrimitive?.int }.getOrNull() ?: base.minCohort,
            levels = runCatching { map["levels"]?.let { AppJson.decodeFromJsonElement<List<Level>>(it) } }.getOrNull()?.ifEmpty { null } ?: base.levels,
            privacyPolicyUrl = map["privacy_policy_url"]?.takeIf { it != JsonNull }?.jsonPrimitive?.contentOrNull,
            termsUrl = map["terms_url"]?.takeIf { it != JsonNull }?.jsonPrimitive?.contentOrNull
        )
        return Loaded(cfg, rows.source, rows.error)
    }

    val bundledActivities: List<Activity> get() = seed.activities
}
