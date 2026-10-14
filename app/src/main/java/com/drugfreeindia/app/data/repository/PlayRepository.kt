package com.drugfreeindia.app.data.repository

import com.drugfreeindia.app.data.local.AppDatabase
import com.drugfreeindia.app.data.local.Connectivity
import com.drugfreeindia.app.data.local.LocalResult
import com.drugfreeindia.app.data.local.PendingChoice
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.safeCall
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.domain.model.AttemptState
import com.drugfreeindia.app.domain.model.CompleteResult
import com.drugfreeindia.app.domain.model.LeaderboardRow
import com.drugfreeindia.app.domain.model.MyStats
import com.drugfreeindia.app.domain.model.ProgressPoint
import com.drugfreeindia.app.domain.model.SubmitChoiceResult
import com.drugfreeindia.app.domain.model.UserBadge
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** How answers in this play-through are recorded. */
enum class GameMode {
    /** Signed in + online + story published on server: every answer goes through submit_choice. */
    REMOTE,
    /** Signed in but offline (or connection dropped): answers are queued and replayed later. */
    QUEUED,
    /** Not signed in, or story not on the server: practice only, nothing is scored. */
    LOCAL
}

data class GameSession(
    val storyId: String,
    val mode: GameMode,
    /** "srv:<uuid>" when the server attempt is known, otherwise "loc:<uuid>". */
    val attemptKey: String,
    val resumeScene: String? = null,
    val resumePoints: Int = 0,
    val resumeAnswered: Int = 0
) {
    val serverAttemptId: String? get() = attemptKey.removePrefix("srv:").takeIf { attemptKey.startsWith("srv:") }
}

/**
 * Single-player game + personal stats. The ONLY way scores are recorded is the RPCs
 * start_attempt / attempt_state / submit_choice / complete_attempt (server scoring is authoritative).
 */
@Singleton
class PlayRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val db: AppDatabase,
    private val content: ContentRepository,
    private val auth: AuthRepository,
    private val connectivity: Connectivity
) {
    private val syncMutex = Mutex()
    private val canReachServer get() = isSupabaseConfigured && connectivity.isOnline.value

    val localResults: Flow<List<LocalResult>> = db.results().observeAll()
    val pendingCount: Flow<Int> = db.pending().count()

    suspend fun start(storyId: String, restart: Boolean): GameSession {
        val uid = auth.userId
        val onServer = storyId in content.serverStoryIds()
        if (uid == null || !onServer) return GameSession(storyId, GameMode.LOCAL, "loc:${UUID.randomUUID()}")
        if (!canReachServer) return GameSession(storyId, GameMode.QUEUED, "loc:${UUID.randomUUID()}")
        return try {
            val id = supabase.postgrest.rpc("start_attempt", buildJsonObject {
                put("p_story", storyId); put("p_restart", restart)
            }).decodeAs<String>()
            val state = supabase.postgrest.rpc("attempt_state", buildJsonObject { put("p_attempt", id) }).decodeAs<AttemptState>()
            GameSession(storyId, GameMode.REMOTE, "srv:$id",
                resumeScene = state.nextScene.takeIf { state.answered > 0 && !state.completed },
                resumePoints = state.totalPoints, resumeAnswered = state.answered)
        } catch (c: kotlinx.coroutines.CancellationException) { throw c } catch (t: Throwable) {
            GameSession(storyId, GameMode.QUEUED, "loc:${UUID.randomUUID()}")
        }
    }

    /**
     * Records one answer. Returns the server result in REMOTE mode; null when queued/local.
     * If the network fails mid-game the caller switches the session to QUEUED.
     */
    suspend fun submit(session: GameSession, sceneId: String, choiceId: String, isFinal: Boolean): Result<SubmitChoiceResult?> {
        return when (session.mode) {
            GameMode.LOCAL -> Result.success(null)
            GameMode.QUEUED -> {
                queue(session, sceneId, choiceId, isFinal); Result.success(null)
            }
            GameMode.REMOTE -> safeCall {
                supabase.postgrest.rpc("submit_choice", buildJsonObject {
                    put("p_attempt", session.serverAttemptId!!); put("p_scene", sceneId); put("p_choice", choiceId)
                }).decodeAs<SubmitChoiceResult>()
            }
        }
    }

    suspend fun queue(session: GameSession, sceneId: String, choiceId: String, isFinal: Boolean) {
        val uid = auth.userId ?: return
        db.pending().add(PendingChoice(userId = uid, storyId = session.storyId, localAttemptId = session.attemptKey,
            sceneId = sceneId, choiceId = choiceId, completesAttempt = isFinal))
    }

    /** Only call after the last scene. */
    suspend fun complete(session: GameSession, localPoints: Int): Result<CompleteResult?> {
        val existing = db.results().get(session.storyId)
        val best = maxOf(existing?.bestPoints ?: 0, localPoints)
        return when (session.mode) {
            GameMode.REMOTE -> safeCall {
                supabase.postgrest.rpc("complete_attempt", buildJsonObject { put("p_attempt", session.serverAttemptId!!) })
                    .decodeAs<CompleteResult>()
            }.onSuccess { r ->
                db.results().put(LocalResult(session.storyId, maxOf(best, r?.totalPoints ?: 0), System.currentTimeMillis(), synced = true))
            }
            else -> {
                db.results().put(LocalResult(session.storyId, best, System.currentTimeMillis(), synced = false))
                Result.success(null)
            }
        }
    }

    /**
     * Replays queued answers in order. Local attempts get a fresh server attempt first.
     * If the server rejects an answer the attempt is dropped (the local result stays on the
     * device but does not count toward stats or leaderboards).
     */
    suspend fun syncPending(): Int = syncMutex.withLock {
        val uid = auth.userId ?: return 0
        if (!canReachServer) return 0
        var synced = 0
        db.pending().all(uid).groupBy { it.localAttemptId }.forEach { (attemptKey, answers) ->
            try {
                val serverId = if (attemptKey.startsWith("srv:")) attemptKey.removePrefix("srv:")
                else supabase.postgrest.rpc("start_attempt", buildJsonObject {
                    put("p_story", answers.first().storyId); put("p_restart", true)
                }).decodeAs<String>()
                for (a in answers) {
                    supabase.postgrest.rpc("submit_choice", buildJsonObject {
                        put("p_attempt", serverId); put("p_scene", a.sceneId); put("p_choice", a.choiceId)
                    })
                }
                if (answers.any { it.completesAttempt }) {
                    supabase.postgrest.rpc("complete_attempt", buildJsonObject { put("p_attempt", serverId) })
                    db.results().get(answers.first().storyId)?.let { db.results().put(it.copy(synced = true)) }
                }
                synced += answers.size
            } catch (c: kotlinx.coroutines.CancellationException) { throw c } catch (t: Throwable) {
                if (t.toAppError().code in setOf("offline", "network")) return synced
            }
            db.pending().deleteAttempt(attemptKey)
        }
        synced
    }

    suspend fun stats(): Loaded<MyStats>? {
        if (auth.userId == null) return null
        return content.load("user:stats", MyStats.serializer(), { MyStats() }) {
            supabase.postgrest.rpc("my_stats").decodeAs<MyStats>()
        }
    }

    suspend fun progressSeries(months: Int = 6): Loaded<List<ProgressPoint>>? {
        if (auth.userId == null) return null
        return content.load("user:series", ListSerializer(ProgressPoint.serializer()), { emptyList() }) {
            supabase.postgrest.rpc("my_progress_series", buildJsonObject { put("p_months", months) }).decodeList<ProgressPoint>()
        }
    }

    suspend fun myBadges(): Loaded<List<UserBadge>>? {
        val uid = auth.userId ?: return null
        return content.load("user:badges", ListSerializer(UserBadge.serializer()), { emptyList() }) {
            supabase.from("user_badges").select { filter { eq("user_id", uid) } }.decodeList<UserBadge>()
        }
    }

    suspend fun leaderboard(cityId: String?): Result<List<LeaderboardRow>> = safeCall {
        supabase.postgrest.rpc("college_leaderboard", buildJsonObject {
            if (cityId != null && !cityId.startsWith("local:")) put("p_city", cityId)
            put("p_limit", 20)
        }).decodeList<LeaderboardRow>()
    }
}
