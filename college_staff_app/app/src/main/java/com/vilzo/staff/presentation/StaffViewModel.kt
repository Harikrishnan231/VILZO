package com.vilzo.staff.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vilzo.staff.data.StaffRepository
import com.vilzo.staff.domain.AnonymousComplaint
import com.vilzo.staff.domain.AnonymousComplaintMessage
import com.vilzo.staff.domain.College
import com.vilzo.staff.domain.CollegeDashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StaffUiState(
    val loadingColleges: Boolean = false,
    val colleges: List<College> = emptyList(),
    val collegeQuery: String = "",
    val selectedCollege: College? = null,
    val loadingStats: Boolean = false,
    val stats: CollegeDashboardStats = CollegeDashboardStats(),
    val loadingComplaints: Boolean = false,
    val complaints: List<AnonymousComplaint> = emptyList(),
    val filterStatus: String = "all",
    val selectedComplaint: AnonymousComplaint? = null,
    val messages: List<AnonymousComplaintMessage> = emptyList(),
    val loadingMessages: Boolean = false,
    val replyText: String = "",
    val sendingReply: Boolean = false,
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
class StaffViewModel @Inject constructor(
    private val repo: StaffRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StaffUiState())
    val state: StateFlow<StaffUiState> = _state.asStateFlow()

    init {
        loadColleges()
    }

    fun loadColleges() = viewModelScope.launch {
        _state.update { it.copy(loadingColleges = true) }
        val res = repo.getColleges()
        _state.update { it.copy(loadingColleges = false, colleges = res.getOrDefault(emptyList())) }
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
            val statsRes = repo.getCollegeDashboardStats(cid)
            val complaintsRes = repo.getCollegeComplaints(cid)

            _state.update { s ->
                s.copy(
                    loadingStats = false,
                    loadingComplaints = false,
                    stats = statsRes.getOrDefault(CollegeDashboardStats()),
                    complaints = complaintsRes.getOrDefault(emptyList())
                )
            }
        }
    }

    fun setFilterStatus(status: String) {
        _state.update { it.copy(filterStatus = status) }
    }

    fun openComplaintDetail(complaint: AnonymousComplaint) {
        _state.update { it.copy(selectedComplaint = complaint, replyText = "", messages = emptyList()) }
        loadMessages(complaint.id)
    }

    fun closeComplaintDetail() {
        _state.update { it.copy(selectedComplaint = null) }
    }

    private fun loadMessages(complaintId: String) = viewModelScope.launch {
        _state.update { it.copy(loadingMessages = true) }
        val res = repo.getComplaintMessages(complaintId)
        _state.update { it.copy(loadingMessages = false, messages = res.getOrDefault(emptyList())) }
    }

    fun onReplyChange(text: String) {
        _state.update { it.copy(replyText = text) }
    }

    fun sendReply() {
        val complaint = _state.value.selectedComplaint ?: return
        val text = _state.value.replyText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(sendingReply = true) }
            val res = repo.sendStaffReply(complaint.id, text)
            res.onSuccess { newMsg ->
                _state.update {
                    it.copy(
                        sendingReply = false,
                        replyText = "",
                        messages = it.messages + newMsg
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(sendingReply = false, error = e.localizedMessage) }
            }
        }
    }

    fun updateStatus(newStatus: String) {
        val complaint = _state.value.selectedComplaint ?: return
        viewModelScope.launch {
            repo.updateComplaintStatus(complaint.id, newStatus)
            val updated = complaint.copy(status = newStatus)
            _state.update { s ->
                s.copy(
                    selectedComplaint = updated,
                    complaints = s.complaints.map { if (it.id == updated.id) updated else it }
                )
            }
        }
    }
}
