package com.drugfreeindia.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// ---------------------------------------------------------------------------
// These classes double as Postgrest DTOs. Field names map 1:1 to the columns in
// supabase/01_schema.sql via @SerialName. Do not rename the serial names.
// ---------------------------------------------------------------------------

@Serializable
data class City(
    val id: String = "",
    val name: String,
    val state: String? = null,
    val slug: String,
    @SerialName("sort_order") val sortOrder: Int = 100
)

@Serializable
data class College(
    val id: String = "",
    @SerialName("city_id") val cityId: String,
    val name: String,
    @SerialName("short_code") val shortCode: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false
)

@Serializable
data class CollegeSuggestion(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("city_id") val cityId: String,
    val name: String,
    val note: String? = null,
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Profile(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    val phone: String? = null,
    @SerialName("city_id") val cityId: String? = null,
    @SerialName("college_id") val collegeId: String? = null,
    @SerialName("custom_college_name") val customCollegeName: String? = null,
    val language: String = "en",
    @SerialName("include_in_college_stats") val includeInCollegeStats: Boolean = true,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = false
)

@Serializable
data class Helpline(
    val id: String = "",
    val name: String,
    val number: String,
    val description: String? = null,
    val hours: String? = null,
    val languages: List<String>? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("is_emergency") val isEmergency: Boolean = false,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("sort_order") val sortOrder: Int = 100
)

@Serializable
data class SupportCentre(
    val id: String = "",
    @SerialName("city_id") val cityId: String? = null,
    val name: String,
    val type: String,
    val address: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val hours: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    /** Not a DB column: resolved city name for display/search. */
    @kotlinx.serialization.Transient val cityName: String? = null
)

@Serializable
data class Activity(
    val id: String = "",
    val kind: String = "activity",
    val category: String = "other",
    val title: String,
    val description: String? = null,
    @SerialName("organiser_name") val organiserName: String,
    @SerialName("city_id") val cityId: String? = null,
    @SerialName("college_id") val collegeId: String? = null,
    val venue: String? = null,
    @SerialName("starts_at") val startsAt: String? = null,
    @SerialName("ends_at") val endsAt: String? = null,
    @SerialName("recurrence_text") val recurrenceText: String? = null,
    val eligibility: String? = null,
    @SerialName("registration_url") val registrationUrl: String? = null,
    val contact: String? = null,
    @SerialName("cover_image_path") val coverImagePath: String? = null,
    val status: String = "pending",
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("is_demo") val isDemo: Boolean = false,
    @SerialName("created_by") val createdBy: String? = null
)

/** Insert payload for a user submission (RLS: pending / unverified / non-demo only). */
@Serializable
data class ActivitySubmission(
    val kind: String,
    val category: String,
    val title: String,
    val description: String?,
    @SerialName("organiser_name") val organiserName: String,
    @SerialName("city_id") val cityId: String?,
    val venue: String?,
    @SerialName("starts_at") val startsAt: String?,
    @SerialName("recurrence_text") val recurrenceText: String?,
    @SerialName("registration_url") val registrationUrl: String?,
    val contact: String?,
    @SerialName("created_by") val createdBy: String,
    val status: String = "pending",
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("is_demo") val isDemo: Boolean = false
)

@Serializable
data class ActivitySignup(
    val id: String? = null,
    @SerialName("activity_id") val activityId: String,
    @SerialName("user_id") val userId: String
)

@Serializable
data class Mentor(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("role_title") val roleTitle: String? = null,
    val focus: List<String>? = null,
    val bio: String? = null,
    @SerialName("city_id") val cityId: String? = null
)

// ---------------- Play ----------------

@Serializable
data class StoryChoice(
    val id: String,
    val text: String,
    val subtitle: String? = null,
    val points: Int = 0,
    val tag: String? = null,
    val feedback: String? = null,
    val next: String? = null
)

@Serializable
data class StoryScene(
    val id: String,
    val imageKey: String? = null,
    val narration: String? = null,
    val speaker: String? = null,
    val quote: String? = null,
    val timerSeconds: Int? = null,
    val pressure: Float? = null,
    val takeaway: String? = null,
    val isEnd: Boolean = false,
    val choices: List<StoryChoice> = emptyList()
) {
    val isEnding: Boolean get() = isEnd || choices.isEmpty()
}

@Serializable
data class Story(
    val id: String,
    val title: String,
    val pack: String,
    val description: String? = null,
    @SerialName("estimated_seconds") val estimatedSeconds: Int = 35,
    @SerialName("sort_order") val sortOrder: Int = 100,
    @SerialName("cover_image_key") val coverImageKey: String? = null,
    val version: Int = 1,
    val scenes: List<StoryScene>
) {
    fun scene(id: String) = scenes.firstOrNull { it.id == id }
    /** Longest path length from the first scene to an ending (for "Scene x of y"). */
    val pathLength: Int
        get() {
            fun depth(id: String, seen: Set<String>): Int {
                val s = scene(id) ?: return 0
                if (s.isEnding || id in seen) return 1
                return 1 + (s.choices.mapNotNull { it.next }.maxOfOrNull { depth(it, seen + id) } ?: 0)
            }
            return scenes.firstOrNull()?.let { depth(it.id, emptySet()) } ?: 0
        }
}

@Serializable
data class SubmitChoiceResult(
    val accepted: Boolean = false,
    val duplicate: Boolean = false,
    val points: Int = 0,
    @SerialName("total_points") val totalPoints: Int = 0,
    val tag: String? = null,
    val next: String? = null,
    val feedback: String? = null
)

@Serializable
data class AttemptState(
    @SerialName("next_scene") val nextScene: String? = null,
    @SerialName("total_points") val totalPoints: Int = 0,
    val answered: Int = 0,
    val completed: Boolean = false
)

@Serializable
data class CompleteResult(
    @SerialName("total_points") val totalPoints: Int = 0,
    @SerialName("new_badges") val newBadges: List<String> = emptyList(),
    val streak: Int = 0,
    @SerialName("already_completed") val alreadyCompleted: Boolean = false
)

@Serializable
data class Badge(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("icon_key") val iconKey: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 100
)

@Serializable
data class UserBadge(
    @SerialName("badge_id") val badgeId: String,
    @SerialName("earned_at") val earnedAt: String? = null
)

@Serializable
data class MyStats(
    @SerialName("scenarios_completed") val scenariosCompleted: Int = 0,
    @SerialName("scenarios_total") val scenariosTotal: Int = 0,
    @SerialName("total_points") val totalPoints: Int = 0,
    @SerialName("streak_days") val streakDays: Int = 0,
    @SerialName("practice_score") val practiceScore: Int? = null,
    @SerialName("practice_score_delta") val practiceScoreDelta: Int? = null,
    @SerialName("badges_earned") val badgesEarned: Int = 0,
    @SerialName("activities_joined_this_month") val activitiesJoinedThisMonth: Int = 0
)

@Serializable
data class ProgressPoint(
    val month: String,
    @SerialName("practice_score") val practiceScore: Int? = null,
    val answers: Int = 0,
    @SerialName("scenarios_completed") val scenariosCompleted: Int = 0
)

@Serializable
data class LeaderboardRow(
    @SerialName("college_id") val collegeId: String,
    @SerialName("college_name") val collegeName: String,
    @SerialName("city_name") val cityName: String,
    val participants: Int,
    val points: Long
)

// ---------------- Surveys & impact ----------------

@Serializable
data class SurveyOption(val id: String, val label: String)

@Serializable
data class SurveyQuestion(val id: String, val prompt: String, val options: List<SurveyOption>)

@Serializable
data class Survey(
    val id: String,
    @SerialName("month_key") val monthKey: String,
    val title: String,
    val questions: List<SurveyQuestion>
)

@Serializable
data class SurveySubmission(@SerialName("survey_id") val surveyId: String)

@Serializable
data class ImpactOption(
    @SerialName("option_id") val optionId: String,
    val label: String,
    val count: Int
)

@Serializable
data class ImpactQuestion(
    @SerialName("question_id") val questionId: String,
    val prompt: String,
    val options: List<ImpactOption> = emptyList()
)

@Serializable
data class CommunityImpact(
    @SerialName("insufficient_data") val insufficientData: Boolean = true,
    @SerialName("min_cohort") val minCohort: Int = 10,
    val month: String? = null,
    val responses: Int = 0,
    val questions: List<ImpactQuestion> = emptyList()
)

@Serializable
data class SelfAssessment(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val kind: String,
    val confidence: Int,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Level(val level: Int, val name: String, @SerialName("min_points") val minPoints: Int)

@Serializable
data class AppConfigRow(val key: String, val value: JsonElement)

data class AppConfig(
    val minCohort: Int = 10,
    val levels: List<Level> = listOf(
        Level(1, "Starter", 0), Level(2, "Steady", 30), Level(3, "Confident", 80), Level(4, "Champion", 150)
    ),
    val privacyPolicyUrl: String? = null,
    val termsUrl: String? = null
) {
    fun levelFor(points: Int): Level = levels.sortedBy { it.minPoints }.lastOrNull { points >= it.minPoints } ?: levels.first()
    fun nextLevel(points: Int): Level? = levels.sortedBy { it.minPoints }.firstOrNull { it.minPoints > points }
}

/** Device-only. Never uploaded. */
@Serializable
data class TrustedContact(
    val id: String,
    val name: String,
    val phone: String,
    val relationship: String = ""
)
