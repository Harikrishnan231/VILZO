package com.vilzo.staff.presentation

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
