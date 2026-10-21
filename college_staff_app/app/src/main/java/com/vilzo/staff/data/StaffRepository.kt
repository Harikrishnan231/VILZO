package com.vilzo.staff.data

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
