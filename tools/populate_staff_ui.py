import os

base_pkg = "college_staff_app/app/src/main/java/com/vilzo/staff"

# 1. StaffViewModel.kt
with open(f"{base_pkg}/presentation/StaffViewModel.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff.presentation

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
''')

# 2. StaffScreens.kt
with open(f"{base_pkg}/presentation/StaffScreens.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vilzo.staff.domain.AnonymousComplaint

val VilzoTeal = Color(0xFF0A4D4E)
val VilzoSaffron = Color(0xFFFF7A00)
val VilzoGreen = Color(0xFF168B4B)
val VilzoBg = Color(0xFFF9FBFB)

@Composable
fun StaffAppRoot(vm: StaffViewModel) {
    val s by vm.state.collectAsState()

    MaterialTheme {
        if (s.selectedComplaint != null) {
            ComplaintDetailScreen(
                complaint = s.selectedComplaint!!,
                messages = s.messages,
                loadingMessages = s.loadingMessages,
                replyText = s.replyText,
                sendingReply = s.sendingReply,
                onReplyChange = vm::onReplyChange,
                onSendReply = vm::sendReply,
                onUpdateStatus = vm::updateStatus,
                onBack = vm::closeComplaintDetail
            )
        } else if (s.selectedCollege != null) {
            CollegeDashboardScreen(
                state = s,
                onSelectComplaint = vm::openComplaintDetail,
                onFilterStatus = vm::setFilterStatus,
                onRefresh = { vm.refreshDashboard() },
                onSwitchCollege = vm::clearSelectedCollege
            )
        } else {
            SelectCollegeScreen(
                state = s,
                onQueryChange = vm::onCollegeQuery,
                onSelectCollege = vm::selectCollege
            )
        }
    }
}

@Composable
fun SelectCollegeScreen(
    state: StaffUiState,
    onQueryChange: (String) -> Unit,
    onSelectCollege: (com.vilzo.staff.domain.College) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(VilzoBg)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Text("VILZO Staff Portal", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = VilzoTeal)
        Text("Campus Grievance & Student Welfare Console", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.collegeQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Search your institution...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Search, null) }
        )

        Spacer(Modifier.height(16.dp))

        if (state.loadingColleges) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VilzoTeal)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.filteredColleges) { college ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCollege(college) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.School, null, tint = VilzoTeal)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(college.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                college.shortCode?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
                            }
                            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollegeDashboardScreen(
    state: StaffUiState,
    onSelectComplaint: (AnonymousComplaint) -> Unit,
    onFilterStatus: (String) -> Unit,
    onRefresh: () -> Unit,
    onSwitchCollege: () -> Unit
) {
    val college = state.selectedCollege ?: return

    Column(
        Modifier
            .fillMaxSize()
            .background(VilzoBg)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(college.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VilzoTeal)
                Text("Campus Administrator Portal", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Rounded.Refresh, "Refresh", tint = VilzoTeal)
            }
            IconButton(onClick = onSwitchCollege) {
                Icon(Icons.Rounded.Logout, "Switch College", tint = Color.Gray)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Analytics Cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Active", fontSize = 12.sp, color = VilzoSaffron)
                    Text("${state.stats.activeComplaints}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VilzoSaffron)
                }
            }
            Card(
                Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Resolved", fontSize = 12.sp, color = VilzoGreen)
                    Text("${state.stats.resolvedComplaints}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VilzoGreen)
                }
            }
            Card(
                Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Total", fontSize = 12.sp, color = VilzoTeal)
                    Text("${state.stats.totalComplaints}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VilzoTeal)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Filter chips
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("all" to "All", "submitted" to "Submitted", "in_review" to "In Review", "resolved" to "Resolved").forEach { (key, label) ->
                FilterChip(
                    selected = state.filterStatus == key,
                    onClick = { onFilterStatus(key) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Complaints List
        if (state.loadingComplaints) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VilzoTeal)
            }
        } else if (state.filteredComplaints.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports found for this filter.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.filteredComplaints) { complaint ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectComplaint(complaint) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(complaint.studentPseudonym, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VilzoTeal)
                                val statusColor = when (complaint.status) {
                                    "submitted" -> VilzoSaffron
                                    "in_review" -> Color(0xFF1976D2)
                                    else -> VilzoGreen
                                }
                                Text(complaint.status.replace("_", " ").uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(complaint.subject, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintDetailScreen(
    complaint: AnonymousComplaint,
    messages: List<com.vilzo.staff.domain.AnonymousComplaintMessage>,
    loadingMessages: Boolean,
    replyText: String,
    sendingReply: Boolean,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(VilzoBg)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(complaint.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text("Replying as College Authority", fontSize = 12.sp, color = Color.Gray)
            }
        }

        // Status updater
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onUpdateStatus("in_review") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Mark In Review", fontSize = 12.sp)
            }
            Button(
                onClick = { onUpdateStatus("resolved") },
                colors = ButtonDefaults.buttonColors(containerColor = VilzoGreen),
                modifier = Modifier.weight(1f)
            ) {
                Text("Mark Resolved", fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Messages
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (loadingMessages) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = VilzoTeal)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(messages) { msg ->
                        val isStaff = msg.senderRole == "college_admin"
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isStaff) Alignment.End else Alignment.Start
                        ) {
                            Text(
                                if (isStaff) "You (College Staff)" else complaint.studentPseudonym,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isStaff) VilzoTeal else Color.White)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    msg.message,
                                    color = if (isStaff) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = replyText,
                onValueChange = onReplyChange,
                placeholder = { Text("Reply to student...") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onSendReply,
                enabled = replyText.isNotBlank() && !sendingReply
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, "Send", tint = VilzoTeal)
            }
        }
    }
}
''')

# 3. MainActivity.kt
with open(f"{base_pkg}/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write('''package com.vilzo.staff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vilzo.staff.presentation.StaffAppRoot
import com.vilzo.staff.presentation.StaffViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: StaffViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaffAppRoot(vm = vm)
        }
    }
}
''')

print("Created presentation layer & MainActivity for college_staff_app")
