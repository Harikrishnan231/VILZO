package com.drugfreeindia.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.repository.CollegeGrievanceRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.domain.model.AnonymousComplaint
import com.drugfreeindia.app.domain.model.AnonymousComplaintMessage
import com.drugfreeindia.app.domain.model.College
import com.drugfreeindia.app.domain.model.CollegeDashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollegeAdminUiState(
    val loadingColleges: Boolean = false,
    val colleges: List<College> = emptyList(),
    val collegeQuery: String = "",
    val selectedCollege: College? = null,
    val loadingStats: Boolean = false,
    val stats: CollegeDashboardStats = CollegeDashboardStats(),
    val loadingComplaints: Boolean = false,
    val complaints: List<AnonymousComplaint> = emptyList(),
    val filterStatus: String = "all", // "all", "submitted", "in_review", "resolved"
    val selectedComplaint: AnonymousComplaint? = null,
    val messages: List<AnonymousComplaintMessage> = emptyList(),
    val loadingMessages: Boolean = false,
    val replyText: String = "",
    val sendingReply: Boolean = false,
    val newKind: String = "event", // "event", "club", "volunteering"
    val newCategory: String = "sports",
    val newTitle: String = "",
    val newVenue: String = "",
    val newWhen: String = "",
    val newLink: String = "",
    val newDescription: String = "",
    val publishingActivity: Boolean = false,
    val publishSuccess: String? = null,
    val error: String? = null
) {
    val filteredColleges: List<College>
        get() = if (collegeQuery.isBlank()) colleges else colleges.filter {
            it.name.contains(collegeQuery.trim(), ignoreCase = true) ||
            it.shortCode?.contains(collegeQuery.trim(), ignoreCase = true) == true
        }

    val filteredComplaints: List<AnonymousComplaint>
        get() = when (filterStatus) {
            "all" -> complaints
            else -> complaints.filter { it.status == filterStatus }
        }
}

@HiltViewModel
class CollegeAdminViewModel @Inject constructor(
    private val grievanceRepo: CollegeGrievanceRepository,
    private val contentRepo: ContentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollegeAdminUiState())
    val state: StateFlow<CollegeAdminUiState> = _state.asStateFlow()

    init {
        loadColleges()
    }

    fun loadColleges() = viewModelScope.launch {
        _state.update { it.copy(loadingColleges = true) }
        val loaded = contentRepo.allColleges()
        _state.update { it.copy(loadingColleges = false, colleges = loaded.data) }
    }

    fun onCollegeQuery(q: String) {
        _state.update { it.copy(collegeQuery = q) }
    }

    fun selectCollege(college: College) {
        _state.update { it.copy(selectedCollege = college, collegeQuery = "") }
        refreshDashboard(college.id)
    }

    fun clearSelectedCollege() {
        _state.update { it.copy(selectedCollege = null, selectedComplaint = null) }
    }

    fun refreshDashboard(collegeId: String? = _state.value.selectedCollege?.id) {
        val cid = collegeId ?: return
        viewModelScope.launch {
            _state.update { it.copy(loadingStats = true, loadingComplaints = true) }
            val statsRes = grievanceRepo.getCollegeDashboardStats(cid)
            val complaintsRes = grievanceRepo.getCollegeComplaints(cid)

            _state.update { s ->
                s.copy(
                    loadingStats = false,
                    loadingComplaints = false,
                    stats = statsRes.getOrDefault(s.stats),
                    complaints = complaintsRes.getOrDefault(emptyList())
                )
            }
        }
    }

    fun setFilterStatus(status: String) {
        _state.update { it.copy(filterStatus = status) }
    }

    fun selectComplaint(complaint: AnonymousComplaint?) {
        _state.update { it.copy(selectedComplaint = complaint, replyText = "", messages = emptyList()) }
        if (complaint != null) {
            loadMessages(complaint.id)
        }
    }

    private fun loadMessages(complaintId: String) = viewModelScope.launch {
        _state.update { it.copy(loadingMessages = true) }
        val res = grievanceRepo.getComplaintMessages(complaintId)
        _state.update { it.copy(loadingMessages = false, messages = res.getOrDefault(emptyList())) }
    }

    fun onReplyText(text: String) {
        _state.update { it.copy(replyText = text) }
    }

    fun sendReply() {
        val complaint = _state.value.selectedComplaint ?: return
        val text = _state.value.replyText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(sendingReply = true) }
            val res = grievanceRepo.sendComplaintMessage(
                complaintId = complaint.id,
                message = text,
                senderRole = "college_admin"
            )
            res.onSuccess { newMsg ->
                _state.update { s ->
                    s.copy(
                        sendingReply = false,
                        replyText = "",
                        messages = s.messages + newMsg
                    )
                }
                // Automatically move to 'in_review' if it was 'submitted'
                if (complaint.status == "submitted") {
                    updateComplaintStatus(complaint.id, "in_review")
                }
            }.onFailure { e ->
                _state.update { it.copy(sendingReply = false, error = e.localizedMessage) }
            }
        }
    }

    fun updateComplaintStatus(complaintId: String, newStatus: String) = viewModelScope.launch {
        grievanceRepo.updateComplaintStatus(complaintId, newStatus)
        _state.update { s ->
            val updatedList = s.complaints.map { if (it.id == complaintId) it.copy(status = newStatus) else it }
            val updatedSelected = if (s.selectedComplaint?.id == complaintId) s.selectedComplaint.copy(status = newStatus) else s.selectedComplaint
            s.copy(complaints = updatedList, selectedComplaint = updatedSelected)
        }
    }

    fun onNewKind(k: String) = _state.update { it.copy(newKind = k) }
    fun onNewCategory(c: String) = _state.update { it.copy(newCategory = c) }
    fun onNewTitle(t: String) = _state.update { it.copy(newTitle = t) }
    fun onNewVenue(v: String) = _state.update { it.copy(newVenue = v) }
    fun onNewWhen(w: String) = _state.update { it.copy(newWhen = w) }
    fun onNewLink(l: String) = _state.update { it.copy(newLink = l) }
    fun onNewDescription(d: String) = _state.update { it.copy(newDescription = d) }
    fun dismissPublishSuccess() = _state.update { it.copy(publishSuccess = null) }

    fun publishActivity() {
        val s = _state.value
        val col = s.selectedCollege ?: return
        if (s.newTitle.trim().length < 3) return
        viewModelScope.launch {
            _state.update { it.copy(publishingActivity = true, publishSuccess = null) }
            val res = grievanceRepo.adminCreateActivity(
                kind = s.newKind,
                category = s.newCategory,
                title = s.newTitle.trim(),
                organiserName = col.name,
                cityId = col.cityId,
                venue = s.newVenue.trim().ifBlank { null },
                recurrenceText = s.newWhen.trim().ifBlank { null },
                registrationUrl = s.newLink.trim().ifBlank { null },
                description = s.newDescription.trim().ifBlank { null }
            )
            res.onSuccess {
                _state.update {
                    it.copy(
                        publishingActivity = false,
                        newTitle = "",
                        newVenue = "",
                        newWhen = "",
                        newLink = "",
                        newDescription = "",
                        publishSuccess = "Listing published successfully to Explore!"
                    )
                }
            }.onFailure { err ->
                _state.update { it.copy(publishingActivity = false, publishSuccess = "Could not publish listing: ${err.message}") }
            }
        }
    }
}
