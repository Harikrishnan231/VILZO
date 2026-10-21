package com.vilzo.staff.domain

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
