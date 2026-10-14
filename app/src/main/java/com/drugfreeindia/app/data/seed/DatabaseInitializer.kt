package com.drugfreeindia.app.data.seed

import android.content.Context
import com.drugfreeindia.app.data.local.AppDatabase
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
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.Centre
import com.drugfreeindia.app.domain.model.CollegeStat
import com.drugfreeindia.app.domain.model.Event
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.model.SurveyQuestion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val storyCount = database.storyDao().getCount()
        if (storyCount == 0) {
            seedStories()
            seedCentres()
            seedHelplines()
            seedActivities()
            seedEvents()
            seedMentors()
            seedSurveys()
            seedCollegeStats()
            seedBadges()
            seedInitialStreakAndProgress()
        }
    }

    private suspend fun seedStories() {
        val storyFiles = listOf(
            "stories/story_farewell_party.json",
            "stories/story_group_chat_dare.json",
            "stories/story_hostel_night.json",
            "stories/story_after_the_match.json",
            "stories/story_cousins_wedding.json",
            "stories/story_the_cool_senior.json",
            "stories/story_stressed_before_exams.json"
        )
        val entities = storyFiles.mapNotNull { path ->
            try {
                val content = context.assets.open(path).bufferedReader().use { it.readText() }
                val story = json.decodeFromString<Story>(content)
                StoryEntity(
                    id = story.id,
                    title = story.title,
                    pack = story.pack,
                    estimatedSeconds = story.estimatedSeconds,
                    scenesJson = content
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        database.storyDao().insertStories(entities)
    }

    private suspend fun seedCentres() {
        try {
            val content = context.assets.open("centres.json").bufferedReader().use { it.readText() }
            val centres = json.decodeFromString<List<Centre>>(content)
            val entities = centres.map {
                CentreEntity(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    address = it.address,
                    city = it.city,
                    state = it.state,
                    pincode = it.pincode,
                    phone = it.phone,
                    hours = it.hours,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    isVerified = it.isVerified
                )
            }
            database.centreDao().insertCentres(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedHelplines() {
        try {
            val content = context.assets.open("helplines.json").bufferedReader().use { it.readText() }
            val helplines = json.decodeFromString<List<Helpline>>(content)
            val entities = helplines.map {
                HelplineEntity(
                    id = it.id,
                    name = it.name,
                    number = it.number,
                    hours = it.hours,
                    description = it.description,
                    category = it.category,
                    lastVerified = it.lastVerified
                )
            }
            database.helplineDao().insertHelplines(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedActivities() {
        try {
            val content = context.assets.open("activities.json").bufferedReader().use { it.readText() }
            val activities = json.decodeFromString<List<Activity>>(content)
            val entities = activities.map {
                ActivityEntity(
                    id = it.id,
                    title = it.title,
                    category = it.category,
                    organizer = it.organizer,
                    location = it.location,
                    schedule = it.schedule,
                    description = it.description,
                    whatToBring = it.whatToBring,
                    isVerified = it.isVerified,
                    isJoined = it.isJoined
                )
            }
            database.activityDao().insertActivities(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedEvents() {
        try {
            val content = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val events = json.decodeFromString<List<Event>>(content)
            val entities = events.map {
                EventEntity(
                    id = it.id,
                    title = it.title,
                    category = it.category,
                    date = it.date,
                    time = it.time,
                    location = it.location,
                    organizer = it.organizer,
                    description = it.description,
                    isReminderSet = it.isReminderSet
                )
            }
            database.eventDao().insertEvents(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedMentors() {
        try {
            val content = context.assets.open("mentors.json").bufferedReader().use { it.readText() }
            val mentors = json.decodeFromString<List<Mentor>>(content)
            val entities = mentors.map {
                MentorEntity(
                    id = it.id,
                    name = it.name,
                    role = it.role,
                    organization = it.organization,
                    bio = it.bio,
                    isAvailable = it.isAvailable
                )
            }
            database.mentorDao().insertMentors(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedSurveys() {
        try {
            val content = context.assets.open("surveys.json").bufferedReader().use { it.readText() }
            val questions = json.decodeFromString<List<SurveyQuestion>>(content)
            val entities = questions.map {
                SurveyQuestionEntity(
                    id = it.id,
                    category = it.category,
                    questionText = it.questionText,
                    optionsJson = json.encodeToString<List<String>>(it.options)
                )
            }
            database.surveyDao().insertQuestions(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedCollegeStats() {
        try {
            val content = context.assets.open("colleges.json").bufferedReader().use { it.readText() }
            val stats = json.decodeFromString<List<CollegeStat>>(content)
            val entities = stats.map {
                CollegeStatEntity(
                    collegeCode = it.collegeCode,
                    collegeName = it.collegeName,
                    points = it.points,
                    participants = it.participants,
                    rank = it.rank
                )
            }
            database.collegeStatDao().insertCollegeStats(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedBadges() {
        try {
            val content = context.assets.open("badges.json").bufferedReader().use { it.readText() }
            val badges = json.decodeFromString<List<Badge>>(content)
            val entities = badges.map {
                BadgeEntity(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    iconName = it.iconName,
                    isUnlocked = it.isUnlocked,
                    unlockedAt = it.unlockedAt
                )
            }
            database.badgeDao().insertBadges(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedInitialStreakAndProgress() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        database.streakDao().updateStreak(
            StreakEntity(id = 1, current = 4, longest = 6, lastActiveDate = today)
        )

        // Seed initial progress for "farewell_party" and "group_chat_dare"
        database.storyProgressDao().recordProgress(
            StoryProgressEntity(
                storyId = "farewell_party",
                bestScore = 18,
                completedAt = System.currentTimeMillis() - 86400000L,
                attempts = 1
            )
        )
        database.storyProgressDao().recordScoreEvent(
            ScoreEventEntity(
                storyId = "farewell_party",
                points = 18,
                tag = "calm_no",
                timestamp = System.currentTimeMillis() - 86400000L
            )
        )
    }
}
