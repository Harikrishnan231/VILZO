package com.drugfreeindia.app.presentation.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.repository.CollegeGrievanceRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.domain.model.AnonymousComplaint
import com.drugfreeindia.app.domain.model.AnonymousComplaintMessage
import com.drugfreeindia.app.domain.model.College
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudentGrievanceUiState(
    val loading: Boolean = false,
    val complaints: List<AnonymousComplaint> = emptyList(),
    val colleges: List<College> = emptyList(),
    val selectedCollege: College? = null,
    val selectedCategory: String = "substance_misuse",
    val subject: String = "",
    val message: String = "",
    val isCreating: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
    val activeComplaint: AnonymousComplaint? = null,
    val messages: List<AnonymousComplaintMessage> = emptyList(),
    val loadingMessages: Boolean = false,
    val chatInput: String = "",
    val sendingMessage: Boolean = false
)

@HiltViewModel
class StudentGrievanceViewModel @Inject constructor(
    private val grievanceRepo: CollegeGrievanceRepository,
    private val contentRepo: ContentRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentGrievanceUiState())
    val state: StateFlow<StudentGrievanceUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        val loadedColleges = contentRepo.allColleges()
        val userProfile = profileRepo.profile.value
        val preselectedCollege = loadedColleges.data.firstOrNull { it.id == userProfile?.collegeId }
            ?: loadedColleges.data.firstOrNull()

        val myComplaintsRes = grievanceRepo.getMyComplaints()
        _state.update {
            it.copy(
                loading = false,
                colleges = loadedColleges.data,
                selectedCollege = preselectedCollege,
                complaints = myComplaintsRes.getOrDefault(emptyList())
            )
        }
    }

    fun openCreateDialog() {
        _state.update { it.copy(isCreating = true, subject = "", message = "", error = null) }
    }

    fun closeCreateDialog() {
        _state.update { it.copy(isCreating = false) }
    }

    fun onCategoryChange(cat: String) {
        _state.update { it.copy(selectedCategory = cat) }
    }

    fun onCollegeChange(college: College) {
        _state.update { it.copy(selectedCollege = college) }
    }

    fun onSubjectChange(sub: String) {
        _state.update { it.copy(subject = sub, error = null) }
    }

    fun onMessageChange(msg: String) {
        _state.update { it.copy(message = msg, error = null) }
    }

    fun submitComplaint() {
        val s = _state.value
        val college = s.selectedCollege
        if (college == null) {
            _state.update { it.copy(error = "Please select your college") }
            return
        }
        if (s.subject.trim().length < 3) {
            _state.update { it.copy(error = "Subject must be at least 3 characters") }
            return
        }
        if (s.message.trim().isEmpty()) {
            _state.update { it.copy(error = "Please provide details in the message") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(submitting = true) }
            val res = grievanceRepo.submitComplaint(
                collegeId = college.id,
                category = s.selectedCategory,
                subject = s.subject.trim(),
                firstMessage = s.message.trim()
            )
            res.onSuccess { newComplaint ->
                _state.update {
                    it.copy(
                        submitting = false,
                        isCreating = false,
                        complaints = listOf(newComplaint) + it.complaints,
                        activeComplaint = newComplaint
                    )
                }
                openComplaintChat(newComplaint)
            }.onFailure { e ->
                _state.update { it.copy(submitting = false, error = e.localizedMessage) }
            }
        }
    }

    fun openComplaintChat(complaint: AnonymousComplaint) {
        _state.update { it.copy(activeComplaint = complaint, chatInput = "", messages = emptyList()) }
        loadMessages(complaint.id)
    }

    fun closeComplaintChat() {
        _state.update { it.copy(activeComplaint = null) }
    }

    fun onChatInputChange(text: String) {
        _state.update { it.copy(chatInput = text) }
    }

    private fun loadMessages(complaintId: String) = viewModelScope.launch {
        _state.update { it.copy(loadingMessages = true) }
        val res = grievanceRepo.getComplaintMessages(complaintId)
        _state.update { it.copy(loadingMessages = false, messages = res.getOrDefault(emptyList())) }
    }

    fun sendChatMessage() {
        val complaint = _state.value.activeComplaint ?: return
        val text = _state.value.chatInput.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(sendingMessage = true) }
            val res = grievanceRepo.sendComplaintMessage(
                complaintId = complaint.id,
                message = text,
                senderRole = "student"
            )
            res.onSuccess { newMsg ->
                _state.update {
                    it.copy(
                        sendingMessage = false,
                        chatInput = "",
                        messages = it.messages + newMsg
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(sendingMessage = false, error = e.localizedMessage) }
            }
        }
    }

    fun clearActiveChat() {
        val active = _state.value.activeComplaint ?: return
        viewModelScope.launch {
            grievanceRepo.clearActiveComplaintChat(active.id)
            _state.update {
                it.copy(
                    activeComplaint = null,
                    complaints = it.complaints.filterNot { c -> c.id == active.id },
                    messages = emptyList(),
                    chatInput = ""
                )
            }
        }
    }

    fun clearAllComplaints() {
        viewModelScope.launch {
            grievanceRepo.clearAllMyComplaints()
            _state.update {
                it.copy(
                    activeComplaint = null,
                    complaints = emptyList(),
                    messages = emptyList(),
                    chatInput = ""
                )
            }
        }
    }
}
