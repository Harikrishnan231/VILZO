package com.drugfreeindia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drugfreeindia.app.data.local.entities.ActivityEntity
import com.drugfreeindia.app.data.local.entities.BadgeEntity
import com.drugfreeindia.app.data.local.entities.CentreEntity
import com.drugfreeindia.app.data.local.entities.CollegeStatEntity
import com.drugfreeindia.app.data.local.entities.EventEntity
import com.drugfreeindia.app.data.local.entities.HelplineEntity
import com.drugfreeindia.app.data.local.entities.MentorEntity
import com.drugfreeindia.app.data.local.entities.ScoreEventEntity
import com.drugfreeindia.app.data.local.entities.StoryEntity
import com.drugfreeindia.app.data.local.entities.StoryProgressEntity
import com.drugfreeindia.app.data.local.entities.StreakEntity
import com.drugfreeindia.app.data.local.entities.SurveyQuestionEntity
import com.drugfreeindia.app.data.local.entities.SurveyResponseEntity
import com.drugfreeindia.app.data.local.entities.TrustedContactEntity
import com.drugfreeindia.app.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clear()
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id")
    suspend fun getStoryById(id: String): StoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getCount(): Int

    @Query("DELETE FROM stories")
    suspend fun clear()
}

@Dao
interface StoryProgressDao {
    @Query("SELECT * FROM story_progress")
    fun getAllProgress(): Flow<List<StoryProgressEntity>>

    @Query("SELECT * FROM story_progress WHERE storyId = :storyId")
    suspend fun getProgressForStory(storyId: String): StoryProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordProgress(progress: StoryProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordScoreEvent(event: ScoreEventEntity)

    @Query("SELECT * FROM score_events ORDER BY timestamp DESC")
    fun getAllScoreEvents(): Flow<List<ScoreEventEntity>>

    @Query("DELETE FROM story_progress")
    suspend fun clearProgress()

    @Query("DELETE FROM score_events")
    suspend fun clearScoreEvents()
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE isUnlocked = 1")
    fun getUnlockedBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockBadge(id: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM badges")
    suspend fun getCount(): Int

    @Query("DELETE FROM badges")
    suspend fun clear()
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE id = 1")
    fun getStreak(): Flow<StreakEntity?>

    @Query("SELECT * FROM streak WHERE id = 1")
    suspend fun getStreakOnce(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStreak(streak: StreakEntity)

    @Query("DELETE FROM streak")
    suspend fun clear()
}

@Dao
interface TrustedContactDao {
    @Query("SELECT * FROM trusted_contacts")
    fun getAllContacts(): Flow<List<TrustedContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: TrustedContactEntity)

    @Query("DELETE FROM trusted_contacts WHERE id = :id")
    suspend fun deleteContact(id: String)

    @Query("DELETE FROM trusted_contacts")
    suspend fun clear()
}

@Dao
interface CentreDao {
    @Query("SELECT * FROM centres")
    fun getAllCentres(): Flow<List<CentreEntity>>

    @Query("SELECT * FROM centres WHERE id = :id")
    suspend fun getCentreById(id: String): CentreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCentres(centres: List<CentreEntity>)

    @Query("SELECT COUNT(*) FROM centres")
    suspend fun getCount(): Int

    @Query("DELETE FROM centres")
    suspend fun clear()
}

@Dao
interface HelplineDao {
    @Query("SELECT * FROM helplines")
    fun getAllHelplines(): Flow<List<HelplineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHelplines(helplines: List<HelplineEntity>)

    @Query("SELECT COUNT(*) FROM helplines")
    suspend fun getCount(): Int

    @Query("DELETE FROM helplines")
    suspend fun clear()
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: String): ActivityEntity?

    @Query("UPDATE activities SET isJoined = :isJoined WHERE id = :id")
    suspend fun toggleJoin(id: String, isJoined: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getCount(): Int

    @Query("DELETE FROM activities")
    suspend fun clear()
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("UPDATE events SET isReminderSet = :isSet WHERE id = :id")
    suspend fun toggleReminder(id: String, isSet: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int

    @Query("DELETE FROM events")
    suspend fun clear()
}

@Dao
interface MentorDao {
    @Query("SELECT * FROM mentors")
    fun getAllMentors(): Flow<List<MentorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMentors(mentors: List<MentorEntity>)

    @Query("SELECT COUNT(*) FROM mentors")
    suspend fun getCount(): Int

    @Query("DELETE FROM mentors")
    suspend fun clear()
}

@Dao
interface SurveyDao {
    @Query("SELECT * FROM survey_questions")
    fun getQuestions(): Flow<List<SurveyQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<SurveyQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordResponse(response: SurveyResponseEntity)

    @Query("SELECT * FROM survey_responses")
    fun getAllResponses(): Flow<List<SurveyResponseEntity>>

    @Query("SELECT COUNT(*) FROM survey_questions")
    suspend fun getQuestionCount(): Int

    @Query("DELETE FROM survey_responses")
    suspend fun clearResponses()
}

@Dao
interface CollegeStatDao {
    @Query("SELECT * FROM college_stats ORDER BY points DESC")
    fun getCollegeStats(): Flow<List<CollegeStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollegeStats(stats: List<CollegeStatEntity>)

    @Query("SELECT COUNT(*) FROM college_stats")
    suspend fun getCount(): Int

    @Query("DELETE FROM college_stats")
    suspend fun clear()
}
