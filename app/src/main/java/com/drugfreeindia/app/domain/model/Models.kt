package com.drugfreeindia.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val anonId: String,
    val nickname: String,
    val collegeCode: String? = null,
    val language: String = "en",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class Story(
    val id: String,
    val title: String,
    val pack: String,
    val estimatedSeconds: Int = 30,
    val scenes: List<Scene>
)

@Serializable
data class Scene(
    val id: String,
    val speaker: String,
    val lines: List<String>,
    val timerSeconds: Int? = null,
    val pressure: Float = 0.5f,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val id: String,
    val text: String,
    val points: Int,
    val tag: String, // e.g. calm_no, exit_plan, gave_in, redirect
    val feedback: String,
    val next: String? = null, // null means wrap-up/end
    val badgeEarned: String? = null
)

@Serializable
data class StoryProgress(
    val storyId: String,
    val bestScore: Int,
    val completedAt: Long,
    val attempts: Int
)

@Serializable
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Serializable
data class Streak(
    val current: Int = 0,
    val longest: Int = 0,
    val lastActiveDate: String = "" // YYYY-MM-DD
)

@Serializable
data class TrustedContact(
    val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

@Serializable
data class Centre(
    val id: String,
    val name: String,
    val type: String, // De-addiction, Counselling, Rehabilitation, Youth Care
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val phone: String,
    val hours: String,
    val latitude: Double,
    val longitude: Double,
    val isVerified: Boolean = true
)

@Serializable
data class Helpline(
    val id: String,
    val name: String,
    val number: String,
    val hours: String,
    val description: String,
    val category: String, // National, Mental Health, Emergency
    val lastVerified: String
)

@Serializable
data class Activity(
    val id: String,
    val title: String,
    val category: String, // Sports, Art, Music, Dance, Poetry, Volunteering, Fitness, Skills
    val organizer: String,
    val location: String,
    val schedule: String,
    val description: String,
    val whatToBring: String = "Your enthusiasm and a positive attitude!",
    val isVerified: Boolean = true,
    val isJoined: Boolean = false
)

@Serializable
data class Event(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val time: String,
    val location: String,
    val organizer: String,
    val description: String,
    val isReminderSet: Boolean = false
)

@Serializable
data class Mentor(
    val id: String,
    val name: String,
    val role: String,
    val organization: String,
    val bio: String,
    val isAvailable: Boolean = true
)

@Serializable
data class SurveyQuestion(
    val id: String,
    val category: String,
    val questionText: String,
    val options: List<String>
)

@Serializable
data class SurveyResponse(
    val id: Long = 0,
    val monthKey: String, // YYYY-MM
    val questionId: String,
    val selectedAnswer: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CollegeStat(
    val collegeCode: String,
    val collegeName: String,
    val points: Int,
    val participants: Int,
    val rank: Int
)

data class ConfidenceScore(
    val overallScore: Int, // 0 to 100
    val storyRefusalRate: Int,
    val surveyRating: Int,
    val streakConsistency: Int,
    val deltaThisWeek: Int
)
