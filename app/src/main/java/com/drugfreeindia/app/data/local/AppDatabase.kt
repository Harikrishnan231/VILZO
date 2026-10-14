package com.drugfreeindia.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Offline cache of reference/content data fetched from Supabase (cities, colleges,
 * helplines, centres, stories, activities, badges, config). Stored as JSON per key so
 * the cache always mirrors the server DTOs exactly.
 */
@Entity(tableName = "cache_entries")
data class CacheEntry(
    @PrimaryKey val key: String,
    val json: String,
    val updatedAt: Long = System.currentTimeMillis()
)

/** Answers given while offline, replayed through the RPCs in order when back online. */
@Entity(tableName = "pending_choices")
data class PendingChoice(
    @PrimaryKey(autoGenerate = true) val seq: Long = 0,
    val userId: String,
    val storyId: String,
    val localAttemptId: String,
    val sceneId: String,
    val choiceId: String,
    val completesAttempt: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/** Device-side record of a played scenario, so Play shows completion even before sync. */
@Entity(tableName = "local_results")
data class LocalResult(
    @PrimaryKey val storyId: String,
    val bestPoints: Int,
    val completedAt: Long,
    val synced: Boolean
)

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache_entries WHERE `key` = :key")
    suspend fun get(key: String): CacheEntry?

    @Query("SELECT * FROM cache_entries WHERE `key` = :key")
    fun observe(key: String): Flow<CacheEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: CacheEntry)

    @Query("DELETE FROM cache_entries WHERE `key` LIKE 'user:%'")
    suspend fun clearUserScoped()

    @Query("DELETE FROM cache_entries")
    suspend fun clearAll()
}

@Dao
interface PendingDao {
    @Insert
    suspend fun add(p: PendingChoice)

    @Query("SELECT * FROM pending_choices WHERE userId = :userId ORDER BY seq ASC")
    suspend fun all(userId: String): List<PendingChoice>

    @Query("SELECT COUNT(*) FROM pending_choices")
    fun count(): Flow<Int>

    @Query("DELETE FROM pending_choices WHERE localAttemptId = :attempt")
    suspend fun deleteAttempt(attempt: String)

    @Query("DELETE FROM pending_choices")
    suspend fun clear()
}

@Dao
interface ResultDao {
    @Query("SELECT * FROM local_results")
    fun observeAll(): Flow<List<LocalResult>>

    @Query("SELECT * FROM local_results WHERE storyId = :id")
    suspend fun get(id: String): LocalResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(r: LocalResult)

    @Query("DELETE FROM local_results")
    suspend fun clear()
}

@Database(
    entities = [CacheEntry::class, PendingChoice::class, LocalResult::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cache(): CacheDao
    abstract fun pending(): PendingDao
    abstract fun results(): ResultDao

    companion object {
        /** New file name; the legacy v1 DB (`drug_free_india.db`) is migrated then deleted. */
        const val NAME = "dfi_cache_v2.db"
        const val LEGACY_NAME = "drug_free_india.db"
    }
}
