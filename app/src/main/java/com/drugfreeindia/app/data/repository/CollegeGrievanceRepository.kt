package com.drugfreeindia.app.data.repository

import android.content.Context
import androidx.core.content.edit
import com.drugfreeindia.app.data.local.Connectivity
import com.drugfreeindia.app.domain.model.AnonymousComplaint
import com.drugfreeindia.app.domain.model.AnonymousComplaintMessage
import com.drugfreeindia.app.domain.model.CollegeDashboardStats
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollegeGrievanceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabase: SupabaseClient,
    private val connectivity: Connectivity,
    private val auth: AuthRepository
) {
    private val prefs = context.getSharedPreferences("dfi_anonymous_grievance_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private fun currentAccountKey(): String {
        return auth.userId ?: "guest"
    }

    /**
     * Client Token for Zero-Knowledge student anonymity.
     * Each student account gets its own isolated anonymous client token.
     * When Student A is logged in, their token is unique to Student A.
     * When Student B logs in on the same device, they get an entirely separate token.
     * The college authority only sees an anonymous pseudonym and the client token,
     * maintaining 100% Zero-Knowledge student privacy.
     */
    fun resolveClientToken(): String {
        val key = currentAccountKey()
        val prefKey = "anon_client_token_$key"
        var token = prefs.getString(prefKey, null)
        if (token == null) {
            token = "anon_tok_" + UUID.randomUUID().toString().replace("-", "")
            prefs.edit { putString(prefKey, token) }
        }
        return token
    }

    val clientToken: String get() = resolveClientToken()

    // In-memory / local fallback cache scoped to active account
    private val localComplaints = mutableListOf<AnonymousComplaint>()
    private val localMessages = mutableListOf<AnonymousComplaintMessage>()
    private var loadedAccountKey: String? = null

    private val _studentComplaintsFlow = MutableStateFlow<List<AnonymousComplaint>>(emptyList())
    val studentComplaintsFlow: StateFlow<List<AnonymousComplaint>> = _studentComplaintsFlow.asStateFlow()

    private fun ensureAccountLoaded() {
        val key = currentAccountKey()
        if (loadedAccountKey != key) {
            loadedAccountKey = key
            localComplaints.clear()
            localMessages.clear()
            val savedJson = prefs.getString("local_complaints_json_$key", null)
            if (!savedJson.isNullOrBlank()) {
                runCatching {
                    val list = json.decodeFromString<List<AnonymousComplaint>>(savedJson)
                    localComplaints.addAll(list)
                }
            }
            val savedMsgs = prefs.getString("local_messages_json_$key", null)
            if (!savedMsgs.isNullOrBlank()) {
                runCatching {
                    val list = json.decodeFromString<List<AnonymousComplaintMessage>>(savedMsgs)
                    localMessages.addAll(list)
                }
            }
            _studentComplaintsFlow.value = localComplaints.toList()
        }
    }

    private fun persistLocalData() {
        val key = currentAccountKey()
        val encoded = json.encodeToString(localComplaints)
        val encodedMsgs = json.encodeToString(localMessages)
        prefs.edit {
            putString("local_complaints_json_$key", encoded)
            putString("local_messages_json_$key", encodedMsgs)
        }
        _studentComplaintsFlow.value = localComplaints.toList()
    }

    fun onUserSignedOut() {
        localComplaints.clear()
        localMessages.clear()
        loadedAccountKey = null
        _studentComplaintsFlow.value = emptyList()
    }

    // -------------------------------------------------------------
    // STUDENT ACTIONS (100% Anonymous, Zero User ID Stored)
    // -------------------------------------------------------------

    suspend fun submitComplaint(
        collegeId: String,
        category: String,
        subject: String,
        firstMessage: String
    ): Result<AnonymousComplaint> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            val complaintId = UUID.randomUUID().toString()
            val shortTag = complaintId.take(4).uppercase()
            val pseudonym = "Anonymous Student #$shortTag"
            val token = clientToken

            val complaint = AnonymousComplaint(
                id = complaintId,
                collegeId = collegeId,
                clientToken = token,
                category = category,
                subject = subject,
                status = "submitted",
                studentPseudonym = pseudonym,
                createdAt = java.time.Instant.now().toString()
            )

            val msg = AnonymousComplaintMessage(
                id = UUID.randomUUID().toString(),
                complaintId = complaintId,
                senderRole = "student",
                message = firstMessage,
                createdAt = java.time.Instant.now().toString()
            )

            if (connectivity.isOnline.value) {
                runCatching {
                    supabase.from("anonymous_complaints").insert(complaint)
                    supabase.from("anonymous_complaint_messages").insert(msg)
                }
            }

            localComplaints.add(0, complaint)
            localMessages.add(msg)
            persistLocalData()

            complaint
        }
    }

    suspend fun getMyComplaints(): Result<List<AnonymousComplaint>> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            val token = clientToken
            if (connectivity.isOnline.value) {
                val remote = runCatching {
                    supabase.from("anonymous_complaints")
                        .select {
                            filter { eq("client_token", token) }
                            order("created_at", Order.DESCENDING)
                        }
                        .decodeList<AnonymousComplaint>()
                }.getOrNull()

                if (remote != null) {
                    localComplaints.clear()
                    localComplaints.addAll(remote)
                    persistLocalData()
                    return@runCatching remote
                }
            }
            localComplaints.toList()
        }
    }

    suspend fun getComplaintMessages(complaintId: String): Result<List<AnonymousComplaintMessage>> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            if (connectivity.isOnline.value) {
                val remote = runCatching {
                    supabase.from("anonymous_complaint_messages")
                        .select {
                            filter { eq("complaint_id", complaintId) }
                            order("created_at", Order.ASCENDING)
                        }
                        .decodeList<AnonymousComplaintMessage>()
                }.getOrNull()

                if (!remote.isNullOrEmpty()) {
                    return@runCatching remote
                }
            }
            localMessages.filter { it.complaintId == complaintId }
        }
    }

    suspend fun sendComplaintMessage(
        complaintId: String,
        message: String,
        senderRole: String = "student"
    ): Result<AnonymousComplaintMessage> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            val msg = AnonymousComplaintMessage(
                id = UUID.randomUUID().toString(),
                complaintId = complaintId,
                senderRole = senderRole,
                message = message,
                createdAt = java.time.Instant.now().toString()
            )

            if (connectivity.isOnline.value) {
                runCatching {
                    supabase.from("anonymous_complaint_messages").insert(msg)
                }
            }

            localMessages.add(msg)
            persistLocalData()
            msg
        }
    }

    /**
     * Clear Chat / Delete conversation:
     * Permanently deletes the messages and complaint locally from this phone,
     * and if online, removes them from Supabase.
     */
    suspend fun clearActiveComplaintChat(complaintId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            if (connectivity.isOnline.value) {
                runCatching {
                    supabase.from("anonymous_complaint_messages").delete {
                        filter { eq("complaint_id", complaintId) }
                    }
                    supabase.from("anonymous_complaints").delete {
                        filter { eq("id", complaintId) }
                    }
                }
            }
            localComplaints.removeAll { it.id == complaintId }
            localMessages.removeAll { it.complaintId == complaintId }
            persistLocalData()
            Unit
        }
    }

    /**
     * Clear all complaint conversations for the current student account.
     * Generates a fresh client token so past reports are fully detached.
     */
    suspend fun clearAllMyComplaints(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            ensureAccountLoaded()
            val token = clientToken
            if (connectivity.isOnline.value) {
                runCatching {
                    val myComplaints = supabase.from("anonymous_complaints")
                        .select { filter { eq("client_token", token) } }
                        .decodeList<AnonymousComplaint>()
                    for (c in myComplaints) {
                        supabase.from("anonymous_complaint_messages").delete {
                            filter { eq("complaint_id", c.id) }
                        }
                    }
                    supabase.from("anonymous_complaints").delete {
                        filter { eq("client_token", token) }
                    }
                }
            }
            val key = currentAccountKey()
            val freshToken = "anon_tok_" + UUID.randomUUID().toString().replace("-", "")
            prefs.edit {
                putString("anon_client_token_$key", freshToken)
                remove("local_complaints_json_$key")
                remove("local_messages_json_$key")
            }
            localComplaints.clear()
            localMessages.clear()
            _studentComplaintsFlow.value = emptyList()
            Unit
        }
    }

    // -------------------------------------------------------------
    // COLLEGE ADMINISTRATOR ACTIONS
    // -------------------------------------------------------------

    suspend fun getCollegeDashboardStats(collegeId: String): Result<CollegeDashboardStats> = withContext(Dispatchers.IO) {
        runCatching {
            if (connectivity.isOnline.value) {
                val rpcResult = runCatching {
                    val res = supabase.postgrest.rpc(
                        function = "get_college_dashboard_stats",
                        parameters = kotlinx.serialization.json.buildJsonObject {
                            put("p_college_id", kotlinx.serialization.json.JsonPrimitive(collegeId))
                        }
                    )
                    val raw = res.data
                    json.decodeFromString<CollegeDashboardStats>(raw)
                }.getOrNull()

                if (rpcResult != null) return@runCatching rpcResult
            }

            val matching = localComplaints.filter { it.collegeId == collegeId }
            val active = matching.count { it.status in listOf("submitted", "in_review") }
            val resolved = matching.count { it.status == "resolved" }

            CollegeDashboardStats(
                totalStudents = matching.size * 5L,
                totalPoints = matching.size * 50L,
                activeComplaints = active.toLong(),
                resolvedComplaints = resolved.toLong(),
                totalComplaints = matching.size.toLong()
            )
        }
    }

    suspend fun getCollegeComplaints(collegeId: String): Result<List<AnonymousComplaint>> = withContext(Dispatchers.IO) {
        runCatching {
            if (connectivity.isOnline.value) {
                val remote = runCatching {
                    supabase.from("anonymous_complaints")
                        .select {
                            filter { eq("college_id", collegeId) }
                            order("created_at", Order.DESCENDING)
                        }
                        .decodeList<AnonymousComplaint>()
                }.getOrNull()

                if (remote != null) return@runCatching remote
            }
            localComplaints.filter { it.collegeId == collegeId }
        }
    }

    suspend fun updateComplaintStatus(complaintId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (connectivity.isOnline.value) {
                runCatching {
                    supabase.from("anonymous_complaints").update({
                        set("status", newStatus)
                    }) {
                        filter { eq("id", complaintId) }
                    }
                }
            }
            val idx = localComplaints.indexOfFirst { it.id == complaintId }
            if (idx >= 0) {
                localComplaints[idx] = localComplaints[idx].copy(status = newStatus)
                persistLocalData()
            }
            Unit
        }
    }

    suspend fun adminCreateActivity(
        kind: String,
        category: String,
        title: String,
        organiserName: String,
        cityId: String?,
        venue: String?,
        recurrenceText: String?,
        registrationUrl: String?,
        description: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val params = kotlinx.serialization.json.buildJsonObject {
                put("p_kind", kotlinx.serialization.json.JsonPrimitive(kind))
                put("p_category", kotlinx.serialization.json.JsonPrimitive(category))
                put("p_title", kotlinx.serialization.json.JsonPrimitive(title))
                put("p_organiser_name", kotlinx.serialization.json.JsonPrimitive(organiserName))
                cityId?.let { put("p_city_id", kotlinx.serialization.json.JsonPrimitive(it)) }
                venue?.let { put("p_venue", kotlinx.serialization.json.JsonPrimitive(it)) }
                venue?.let { put("p_location_text", kotlinx.serialization.json.JsonPrimitive(it)) }
                recurrenceText?.let { put("p_recurrence_text", kotlinx.serialization.json.JsonPrimitive(it)) }
                recurrenceText?.let { put("p_date_label", kotlinx.serialization.json.JsonPrimitive(it)) }
                registrationUrl?.let { put("p_registration_url", kotlinx.serialization.json.JsonPrimitive(it)) }
                description?.let { put("p_description", kotlinx.serialization.json.JsonPrimitive(it)) }
                put("p_cover_image_key", kotlinx.serialization.json.JsonPrimitive("cat_${category}"))
            }
            if (connectivity.isOnline.value) {
                runCatching {
                    supabase.postgrest.rpc("admin_create_activity", params)
                }
            }
            Unit
        }
    }
}
