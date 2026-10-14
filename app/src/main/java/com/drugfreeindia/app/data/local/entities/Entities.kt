package com.drugfreeindia.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val anonId: String,
    val nickname: String,
    val collegeCode: String?,
    val language: String,
    val createdAt: Long
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val pack: String,
    val estimatedSeconds: Int,
    val scenesJson: String
)

@Entity(tableName = "story_progress")
data class StoryProgressEntity(
    @PrimaryKey val storyId: String,
    val bestScore: Int,
    val completedAt: Long,
    val attempts: Int
)

@Entity(tableName = "score_events")
data class ScoreEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: String,
    val points: Int,
    val tag: String,
    val timestamp: Long
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val id: Int = 1,
    val current: Int,
    val longest: Int,
    val lastActiveDate: String
)

@Entity(tableName = "trusted_contacts")
data class TrustedContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean
)

@Entity(tableName = "centres")
data class CentreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val phone: String,
    val hours: String,
    val latitude: Double,
    val longitude: Double,
    val isVerified: Boolean
)

@Entity(tableName = "helplines")
data class HelplineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val number: String,
    val hours: String,
    val description: String,
    val category: String,
    val lastVerified: String
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val organizer: String,
    val location: String,
    val schedule: String,
    val description: String,
    val whatToBring: String,
    val isVerified: Boolean,
    val isJoined: Boolean
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val date: String,
    val time: String,
    val location: String,
    val organizer: String,
    val description: String,
    val isReminderSet: Boolean
)

@Entity(tableName = "mentors")
data class MentorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val organization: String,
    val bio: String,
    val isAvailable: Boolean
)

@Entity(tableName = "survey_questions")
data class SurveyQuestionEntity(
    @PrimaryKey val id: String,
    val category: String,
    val questionText: String,
    val optionsJson: String
)

@Entity(tableName = "survey_responses")
data class SurveyResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthKey: String,
    val questionId: String,
    val selectedAnswer: String,
    val timestamp: Long
)

@Entity(tableName = "college_stats")
data class CollegeStatEntity(
    @PrimaryKey val collegeCode: String,
    val collegeName: String,
    val points: Int,
    val participants: Int,
    val rank: Int
)
