import os

base_pkg = "college_staff_app/app/src/main/java/com/vilzo/staff"
os.makedirs(f"{base_pkg}/data", exist_ok=True)
os.makedirs(f"{base_pkg}/domain", exist_ok=True)
os.makedirs(f"{base_pkg}/presentation", exist_ok=True)
os.makedirs(f"{base_pkg}/di", exist_ok=True)

# 1. Models.kt
with open(f"{base_pkg}/domain/Models.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class College(
    val id: String,
    val name: String,
    @SerialName("short_code") val shortCode: String? = null,
    @SerialName("city_id") val cityId: String? = null
)

@Serializable
data class AnonymousComplaint(
    val id: String,
    @SerialName("college_id") val collegeId: String,
    @SerialName("client_token") val clientToken: String = "",
    val category: String,
    val subject: String,
    val status: String = "submitted",
    @SerialName("student_pseudonym") val studentPseudonym: String = "Anonymous Student",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AnonymousComplaintMessage(
    val id: String,
    @SerialName("complaint_id") val complaintId: String,
    @SerialName("sender_role") val senderRole: String, // "student" or "college_admin"
    val message: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CollegeDashboardStats(
    @SerialName("total_students") val totalStudents: Long = 0,
    @SerialName("total_points") val totalPoints: Long = 0,
    @SerialName("active_complaints") val activeComplaints: Long = 0,
    @SerialName("resolved_complaints") val resolvedComplaints: Long = 0,
    @SerialName("total_complaints") val totalComplaints: Long = 0
)
''')

# 2. StaffRepository.kt
with open(f"{base_pkg}/data/StaffRepository.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff.data

import com.vilzo.staff.domain.AnonymousComplaint
import com.vilzo.staff.domain.AnonymousComplaintMessage
import com.vilzo.staff.domain.College
import com.vilzo.staff.domain.CollegeDashboardStats
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getColleges(): Result<List<College>> = withContext(Dispatchers.IO) {
        runCatching {
            supabase.from("colleges")
                .select { order("name", Order.ASCENDING) }
                .decodeList<College>()
        }
    }

    suspend fun getCollegeDashboardStats(collegeId: String): Result<CollegeDashboardStats> = withContext(Dispatchers.IO) {
        runCatching {
            val res = supabase.postgrest.rpc(
                function = "get_college_dashboard_stats",
                parameters = buildJsonObject {
                    put("p_college_id", kotlinx.serialization.json.JsonPrimitive(collegeId))
                }
            )
            json.decodeFromString<CollegeDashboardStats>(res.data)
        }
    }

    suspend fun getCollegeComplaints(collegeId: String): Result<List<AnonymousComplaint>> = withContext(Dispatchers.IO) {
        runCatching {
            supabase.from("anonymous_complaints")
                .select {
                    filter { eq("college_id", collegeId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AnonymousComplaint>()
        }
    }

    suspend fun getComplaintMessages(complaintId: String): Result<List<AnonymousComplaintMessage>> = withContext(Dispatchers.IO) {
        runCatching {
            supabase.from("anonymous_complaint_messages")
                .select {
                    filter { eq("complaint_id", complaintId) }
                    order("created_at", Order.ASCENDING)
                }
                .decodeList<AnonymousComplaintMessage>()
        }
    }

    suspend fun sendStaffReply(complaintId: String, message: String): Result<AnonymousComplaintMessage> = withContext(Dispatchers.IO) {
        runCatching {
            val msg = AnonymousComplaintMessage(
                id = UUID.randomUUID().toString(),
                complaintId = complaintId,
                senderRole = "college_admin",
                message = message,
                createdAt = java.time.Instant.now().toString()
            )
            supabase.from("anonymous_complaint_messages").insert(msg)
            msg
        }
    }

    suspend fun updateComplaintStatus(complaintId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            supabase.from("anonymous_complaints").update({
                set("status", newStatus)
            }) {
                filter { eq("id", complaintId) }
            }
            Unit
        }
    }

    suspend fun adminCreateActivity(
        collegeId: String,
        kind: String,
        category: String,
        title: String,
        organiserName: String,
        venue: String?,
        recurrenceText: String?,
        registrationUrl: String?,
        description: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val params = buildJsonObject {
                put("p_kind", kotlinx.serialization.json.JsonPrimitive(kind))
                put("p_category", kotlinx.serialization.json.JsonPrimitive(category))
                put("p_title", kotlinx.serialization.json.JsonPrimitive(title))
                put("p_organiser_name", kotlinx.serialization.json.JsonPrimitive(organiserName))
                put("p_college_id", kotlinx.serialization.json.JsonPrimitive(collegeId))
                venue?.let { put("p_venue", kotlinx.serialization.json.JsonPrimitive(it)) }
                recurrenceText?.let { put("p_recurrence_text", kotlinx.serialization.json.JsonPrimitive(it)) }
                registrationUrl?.let { put("p_registration_url", kotlinx.serialization.json.JsonPrimitive(it)) }
                description?.let { put("p_description", kotlinx.serialization.json.JsonPrimitive(it)) }
                put("p_cover_image_key", kotlinx.serialization.json.JsonPrimitive("cat_${category}"))
            }
            supabase.postgrest.rpc("admin_create_activity", params)
            Unit
        }
    }
}
''')

# 3. StaffApplication.kt
with open(f"{base_pkg}/StaffApplication.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StaffApplication : Application()
''')

# 4. SupabaseModule.kt
with open(f"{base_pkg}/di/SupabaseModule.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff.di

import com.vilzo.staff.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Postgrest)
        }
    }
}
''')

print("Created data & domain layer for college_staff_app")
