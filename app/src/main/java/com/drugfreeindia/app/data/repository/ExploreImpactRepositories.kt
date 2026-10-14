package com.drugfreeindia.app.data.repository

import com.drugfreeindia.app.data.local.Connectivity
import com.drugfreeindia.app.data.remote.AppError
import com.drugfreeindia.app.data.remote.AppException
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.safeCall
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.ActivitySignup
import com.drugfreeindia.app.domain.model.ActivitySubmission
import com.drugfreeindia.app.domain.model.CommunityImpact
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.domain.model.SelfAssessment
import com.drugfreeindia.app.domain.model.Survey
import com.drugfreeindia.app.domain.model.SurveySubmission
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val content: ContentRepository,
    private val auth: AuthRepository,
    private val connectivity: Connectivity
) {
    private fun uid() = auth.userId ?: throw AppException(AppError("Please sign in to join or submit activities.", false, "not_signed_in"))

    /** Published listings only (RLS also returns the user's own pending ones – filtered out here). No bundled fake events. */
    suspend fun activities(): Loaded<List<Activity>> =
        content.load("activities", ListSerializer(Activity.serializer()), { emptyList() }) {
            supabase.from("activities").select {
                filter { eq("status", "published") }
                order("starts_at", Order.ASCENDING, nullsFirst = false)
                limit(200)
            }.decodeList<Activity>()
        }

    suspend fun mySignupIds(): Set<String> {
        val id = auth.userId ?: return emptySet()
        return content.load("user:signups", ListSerializer(String.serializer()), { emptyList() }) {
            supabase.from("activity_signups").select { filter { eq("user_id", id) } }
                .decodeList<ActivitySignup>().map { it.activityId }
        }.data.toSet()
    }

    suspend fun join(activityId: String) = safeCall {
        supabase.from("activity_signups").insert(ActivitySignup(activityId = activityId, userId = uid())); Unit
    }

    suspend fun leave(activityId: String) = safeCall {
        val u = uid()
        supabase.from("activity_signups").delete { filter { eq("activity_id", activityId); eq("user_id", u) } }; Unit
    }

    suspend fun submit(s: (String) -> ActivitySubmission) = safeCall {
        supabase.from("activities").insert(s(uid())); Unit
    }

    suspend fun mySubmissions(): Result<List<Activity>> = safeCall {
        supabase.from("activities").select {
            filter { eq("created_by", uid()) }
            order("created_at", Order.DESCENDING)
        }.decodeList<Activity>()
    }

    /** Verified + active only (RLS). Mentors tab is hidden when this is empty. */
    suspend fun mentors(): List<Mentor> {
        if (auth.userId == null || !isSupabaseConfigured || !connectivity.isOnline.value) return emptyList()
        return runCatching { supabase.from("mentors").select().decodeList<Mentor>() }.getOrDefault(emptyList())
    }

    fun coverUrl(path: String?): String? =
        path?.takeIf { it.isNotBlank() && isSupabaseConfigured }?.let { supabase.storage.from("activity-images").publicUrl(it) }
}

@Singleton
class ImpactRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val auth: AuthRepository
) {
    private fun uid() = auth.userId ?: throw AppException(AppError("Please sign in to continue.", false, "not_signed_in"))

    suspend fun latestSurvey(): Result<Survey?> = safeCall {
        uid()
        supabase.from("surveys").select {
            order("month_key", Order.DESCENDING); limit(1)
        }.decodeList<Survey>().firstOrNull()
    }

    suspend fun hasSubmitted(surveyId: String): Result<Boolean> = safeCall {
        supabase.from("survey_submissions").select {
            filter { eq("survey_id", surveyId); eq("user_id", uid()) }
        }.decodeList<SurveySubmission>().isNotEmpty()
    }

    suspend fun submitSurvey(surveyId: String, answers: Map<String, String>) = safeCall {
        supabase.postgrest.rpc("submit_survey", buildJsonObject {
            put("p_survey", surveyId)
            put("p_answers", buildJsonObject { answers.forEach { (k, v) -> put(k, v) } })
        }); Unit
    }

    suspend fun communityImpact(collegeId: String?): Result<CommunityImpact> = safeCall {
        supabase.postgrest.rpc("community_impact", buildJsonObject {
            if (collegeId != null) put("p_college", collegeId)
        }).decodeAs<CommunityImpact>()
    }

    suspend fun selfAssessments(): Result<List<SelfAssessment>> = safeCall {
        supabase.from("self_assessments").select {
            filter { eq("user_id", uid()) }
            order("created_at", Order.ASCENDING)
        }.decodeList<SelfAssessment>()
    }

    suspend fun addSelfAssessment(kind: String, confidence: Int) = safeCall {
        supabase.from("self_assessments").insert(SelfAssessment(userId = uid(), kind = kind, confidence = confidence)); Unit
    }

    suspend fun deleteSelfAssessment(id: String) = safeCall {
        val u = uid()
        supabase.from("self_assessments").delete { filter { eq("id", id); eq("user_id", u) } }; Unit
    }
}
