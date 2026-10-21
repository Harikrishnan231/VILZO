package com.drugfreeindia.app.presentation.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MarkChatUnread
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.AnonymousComplaint
import com.drugfreeindia.app.domain.model.AnonymousComplaintMessage
import com.drugfreeindia.app.domain.model.College
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SearchField
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun CollegeAdminRootScreen(
    onBack: () -> Unit,
    vm: CollegeAdminViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()

    BackHandler {
        if (s.selectedComplaint != null) {
            vm.selectComplaint(null)
        } else if (s.selectedCollege != null) {
            vm.clearSelectedCollege()
        } else {
            onBack()
        }
    }

    if (s.selectedCollege == null) {
        CollegePickerScreen(
            state = s,
            onQuery = vm::onCollegeQuery,
            onSelect = vm::selectCollege,
            onClose = onBack
        )
    } else {
        CollegeDashboardScreen(
            state = s,
            onSwitchCollege = vm::clearSelectedCollege,
            onClose = onBack,
            onFilterStatus = vm::setFilterStatus,
            onSelectComplaint = vm::selectComplaint,
            onUpdateStatus = vm::updateComplaintStatus,
            onReplyText = vm::onReplyText,
            onSendReply = vm::sendReply,
            onNewKind = vm::onNewKind,
            onNewCategory = vm::onNewCategory,
            onNewTitle = vm::onNewTitle,
            onNewVenue = vm::onNewVenue,
            onNewWhen = vm::onNewWhen,
            onNewLink = vm::onNewLink,
            onNewDescription = vm::onNewDescription,
            onPublishActivity = vm::publishActivity,
            onDismissPublishSuccess = vm::dismissPublishSuccess
        )
    }
}

// ==============================================================
// 1. College Selection Screen (Searchable Dropdown list)
// ==============================================================

@Composable
private fun CollegePickerScreen(
    state: CollegeAdminUiState,
    onQuery: (String) -> Unit,
    onSelect: (College) -> Unit,
    onClose: () -> Unit
) {
    val c = DfiTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = c.onSurface)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "College Administrator Portal",
                    style = DfiTheme.typography.cardTitle,
                    color = c.onSurface
                )
                Text(
                    "Select your institution to view analytics & grievances",
                    style = DfiTheme.typography.caption,
                    color = c.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        SearchField(
            value = state.collegeQuery,
            onValueChange = onQuery,
            placeholder = "Search college by name or code...",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        if (state.loadingColleges) {
            LoadingState()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val list = state.filteredColleges
                if (list.isEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No college found matching \"${state.collegeQuery}\"",
                                style = DfiTheme.typography.bodySmall,
                                color = c.onSurfaceVariant
                            )
                        }
                    }
                }
                items(list, key = { it.id }) { college ->
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect(college) },
                        padding = PaddingValues(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(c.primarySoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.AccountBalance,
                                    contentDescription = null,
                                    tint = c.primaryDark
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    college.name,
                                    style = DfiTheme.typography.cardTitle,
                                    color = c.onSurface
                                )
                                college.shortCode?.let {
                                    Text(
                                        it,
                                        style = DfiTheme.typography.caption,
                                        color = c.primaryDark
                                    )
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = c.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==============================================================
// 2. College Administrator Dashboard Screen
// ==============================================================

@Composable
private fun CollegeDashboardScreen(
    state: CollegeAdminUiState,
    onSwitchCollege: () -> Unit,
    onClose: () -> Unit,
    onFilterStatus: (String) -> Unit,
    onSelectComplaint: (AnonymousComplaint?) -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    onReplyText: (String) -> Unit,
    onSendReply: () -> Unit,
    onNewKind: (String) -> Unit,
    onNewCategory: (String) -> Unit,
    onNewTitle: (String) -> Unit,
    onNewVenue: (String) -> Unit,
    onNewWhen: (String) -> Unit,
    onNewLink: (String) -> Unit,
    onNewDescription: (String) -> Unit,
    onPublishActivity: () -> Unit,
    onDismissPublishSuccess: () -> Unit
) {
    val c = DfiTheme.colors
    val college = state.selectedCollege ?: return
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
    ) {
        // College Header Bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to app", tint = c.onSurface)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    college.name,
                    style = DfiTheme.typography.cardTitle,
                    color = c.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag("Admin Portal", color = c.primaryDark, bg = c.primarySoft)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Tap to switch institution",
                        style = DfiTheme.typography.caption,
                        color = c.onSurfaceVariant,
                        modifier = Modifier.clickable { onSwitchCollege() }
                    )
                }
            }
            IconButton(onClick = onSwitchCollege) {
                Icon(Icons.Rounded.SwapHoriz, "Switch college", tint = c.primaryDark)
            }
        }

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = c.surface,
            contentColor = c.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = c.primary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Overview & Analytics", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Anonymous Reports", fontWeight = FontWeight.SemiBold)
                        if (state.stats.activeComplaints > 0) {
                            Spacer(Modifier.width(6.dp))
                            Tag(
                                "${state.stats.activeComplaints}",
                                color = Color.White,
                                bg = c.warning
                            )
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Add Event / Activity", fontWeight = FontWeight.SemiBold) }
            )
        }

        Box(Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> OverviewTab(state.stats)
                1 -> ComplaintsTab(
                    state = state,
                    onFilterStatus = onFilterStatus,
                    onSelectComplaint = onSelectComplaint
                )
                2 -> AddActivityTab(
                    state = state,
                    onNewKind = onNewKind,
                    onNewCategory = onNewCategory,
                    onNewTitle = onNewTitle,
                    onNewVenue = onNewVenue,
                    onNewWhen = onNewWhen,
                    onNewLink = onNewLink,
                    onNewDescription = onNewDescription,
                    onPublish = onPublishActivity,
                    onDismissSuccess = onDismissPublishSuccess
                )
            }
        }
    }

    // Detail & Chat Sheet when a complaint is selected
    if (state.selectedComplaint != null) {
        AdminComplaintDetailSheet(
            complaint = state.selectedComplaint,
            messages = state.messages,
            loadingMessages = state.loadingMessages,
            replyText = state.replyText,
            sendingReply = state.sendingReply,
            onReplyText = onReplyText,
            onSendReply = onSendReply,
            onUpdateStatus = { status -> onUpdateStatus(state.selectedComplaint.id, status) },
            onDismiss = { onSelectComplaint(null) }
        )
    }
}

// ==============================================================
// 3. Tab: Overview & Analytics
// ==============================================================

@Composable
private fun OverviewTab(stats: com.drugfreeindia.app.domain.model.CollegeDashboardStats) {
    val c = DfiTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Campus Engagement Metrics", style = DfiTheme.typography.sectionTitle, color = c.onSurface)
        Spacer(Modifier.height(14.dp))

        // 4 Stat Cards Grid
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Registered Students",
                value = "${stats.totalStudents}",
                subtitle = "Active on Drug-Free India",
                icon = Icons.Rounded.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Points",
                value = "${stats.totalPoints}",
                subtitle = "Scenarios & Quizzes",
                icon = Icons.Rounded.EmojiEvents,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Active Grievances",
                value = "${stats.activeComplaints}",
                subtitle = "Awaiting review/action",
                icon = Icons.Rounded.MarkChatUnread,
                color = if (stats.activeComplaints > 0) c.warning else c.primaryDark,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Issues Resolved",
                value = "${stats.resolvedComplaints}",
                subtitle = "Successfully addressed",
                icon = Icons.Rounded.CheckCircle,
                color = c.success,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Competition & Activities Participation
        Text("Student Participation in Events", style = DfiTheme.typography.sectionTitle, color = c.onSurface)
        Spacer(Modifier.height(12.dp))

        DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityRow(
                    name = "Viksit Bharat Hackathon 2027",
                    category = "National Tech",
                    participants = "12 student teams nominated"
                )
                ActivityRow(
                    name = "Auroville Marathon 2027",
                    category = "Sports & Fitness",
                    participants = "28 students signed up"
                )
                ActivityRow(
                    name = "Smart India Hackathon 2026",
                    category = "Innovation Challenge",
                    participants = "4 teams in grand finale"
                )
                ActivityRow(
                    name = "International Yoga Festival",
                    category = "Wellbeing",
                    participants = "45 attendees registered"
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Security / Safety Note
        DfiCard(color = c.primarySoft, padding = PaddingValues(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Security, null, tint = c.primaryDark)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Institution Privacy: All student complaints use zero-knowledge blind tokens. You can communicate freely with students without compromising their privacy.",
                    style = DfiTheme.typography.caption,
                    color = c.onSurface
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = DfiTheme.colors.primaryDark,
    modifier: Modifier = Modifier
) {
    val c = DfiTheme.colors
    DfiCard(modifier, padding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Text(value, style = DfiTheme.typography.screenTitle, color = c.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
    }
}

@Composable
private fun ActivityRow(name: String, category: String, participants: String) {
    val c = DfiTheme.colors
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, style = DfiTheme.typography.cardTitle, color = c.onSurface)
            Text(category, style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        }
        Tag(participants, color = c.primaryDark, bg = c.primarySoft)
    }
}

// ==============================================================
// 4. Tab: Anonymous Complaints Inbox
// ==============================================================

@Composable
private fun ComplaintsTab(
    state: CollegeAdminUiState,
    onFilterStatus: (String) -> Unit,
    onSelectComplaint: (AnonymousComplaint) -> Unit
) {
    val c = DfiTheme.colors

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
        // Status Filter Chips
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "all" to "All",
                "submitted" to "New",
                "in_review" to "Under Review",
                "resolved" to "Resolved"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = state.filterStatus == key,
                    onClick = { onFilterStatus(key) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = c.primarySoft,
                        selectedLabelColor = c.primaryDark
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (state.loadingComplaints) {
            LoadingState()
        } else {
            val list = state.filteredComplaints
            if (list.isEmpty()) {
                Column(
                    Modifier.fillMaxWidth().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        null,
                        tint = c.success,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No grievances in this filter",
                        style = DfiTheme.typography.cardTitle,
                        color = c.onSurface
                    )
                    Text(
                        "Your campus has no unhandled anonymous reports.",
                        style = DfiTheme.typography.caption,
                        color = c.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(list, key = { it.id }) { complaint ->
                        DfiCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSelectComplaint(complaint) },
                            padding = PaddingValues(16.dp)
                        ) {
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Tag(
                                        complaint.category.replace('_', ' ').replaceFirstChar { it.uppercase() },
                                        color = c.primaryDark,
                                        bg = c.primarySoft
                                    )
                                    val (statusText, statusColor) = when (complaint.status) {
                                        "submitted" -> "New Report" to c.warning
                                        "in_review" -> "Under Review" to c.primary
                                        else -> "Resolved" to c.success
                                    }
                                    Tag(statusText, color = statusColor)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    complaint.subject,
                                    style = DfiTheme.typography.cardTitle,
                                    color = c.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        complaint.studentPseudonym,
                                        style = DfiTheme.typography.caption,
                                        color = c.onSurfaceVariant
                                    )
                                    Text(
                                        "Open Chat →",
                                        style = DfiTheme.typography.captionLarge,
                                        color = c.primaryDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==============================================================
// 5. Admin Grievance Chat & Action Bottom Sheet
// ==============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminComplaintDetailSheet(
    complaint: AnonymousComplaint,
    messages: List<AnonymousComplaintMessage>,
    loadingMessages: Boolean,
    replyText: String,
    sendingReply: Boolean,
    onReplyText: (String) -> Unit,
    onSendReply: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val c = DfiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = c.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(complaint.subject, style = DfiTheme.typography.sectionTitle, color = c.onSurface)
                    Text("From ${complaint.studentPseudonym}", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, "Close")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Zero Knowledge Privacy Guarantee Chip
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(DfiTheme.shapes.chip)
                    .background(c.primarySoft)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Lock, null, tint = c.primaryDark, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Student identity is fully protected. Replies are received anonymously.",
                    style = DfiTheme.typography.caption,
                    color = c.primaryDark
                )
            }

            Spacer(Modifier.height(12.dp))

            // Status Actions Row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                listOf("submitted" to "New", "in_review" to "Under Review", "resolved" to "Resolved").forEach { (key, label) ->
                    val selected = complaint.status == key
                    Box(
                        Modifier
                            .clip(DfiTheme.shapes.pill)
                            .background(if (selected) c.primary else c.surfaceMuted)
                            .clickable { onUpdateStatus(key) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            label,
                            style = DfiTheme.typography.caption,
                            color = if (selected) Color.White else c.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Message History
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 300.dp)
                    .clip(DfiTheme.shapes.card)
                    .background(c.background)
                    .padding(12.dp)
            ) {
                if (loadingMessages) {
                    LoadingState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            val isAdmin = msg.senderRole == "college_admin"
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isAdmin) Alignment.End else Alignment.Start
                            ) {
                                Text(
                                    if (isAdmin) "College Grievance Cell" else complaint.studentPseudonym,
                                    style = DfiTheme.typography.caption,
                                    color = c.onSurfaceVariant
                                )
                                Spacer(Modifier.height(2.dp))
                                Box(
                                    Modifier
                                        .clip(DfiTheme.shapes.card)
                                        .background(if (isAdmin) c.primary else c.surface)
                                        .border(1.dp, if (isAdmin) c.primary else c.outline, DfiTheme.shapes.card)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        msg.message,
                                        style = DfiTheme.typography.body,
                                        color = if (isAdmin) Color.White else c.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Reply input
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = onReplyText,
                    placeholder = { Text("Send administrative response to student...") },
                    modifier = Modifier.weight(1f),
                    shape = DfiTheme.shapes.input
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onSendReply,
                    enabled = replyText.isNotBlank() && !sendingReply
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Send,
                        "Send reply",
                        tint = if (replyText.isNotBlank()) c.primary else c.disabled
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivityTab(
    state: CollegeAdminUiState,
    onNewKind: (String) -> Unit,
    onNewCategory: (String) -> Unit,
    onNewTitle: (String) -> Unit,
    onNewVenue: (String) -> Unit,
    onNewWhen: (String) -> Unit,
    onNewLink: (String) -> Unit,
    onNewDescription: (String) -> Unit,
    onPublish: () -> Unit,
    onDismissSuccess: () -> Unit
) {
    val c = DfiTheme.colors
    val categories = listOf(
        "sports" to "Sports",
        "skills" to "Skills & Tech",
        "art" to "Arts",
        "music" to "Music",
        "dance" to "Dance",
        "volunteering" to "Volunteering",
        "fitness" to "Fitness"
    )

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .navigationBarsPadding()
    ) {
        Text("Create Campus Listing", style = DfiTheme.typography.screenTitle, color = c.onSurface)
        Text("Published directly to the student Explore tab under your institution.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        if (state.publishSuccess != null) {
            DfiCard(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = c.success)
                    Spacer(Modifier.width(10.dp))
                    Text(state.publishSuccess, style = DfiTheme.typography.bodySmall, color = c.onSurface, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismissSuccess) {
                        Icon(Icons.Rounded.Close, null, tint = c.onSurfaceVariant)
                    }
                }
            }
        }

        Text("Type", style = DfiTheme.typography.labelSmall, color = c.onSurface)
        Spacer(Modifier.height(6.dp))
        SegmentedTabs(
            listOf("Event", "Activity / Club", "Volunteering"),
            when (state.newKind) {
                "club" -> 1
                "volunteering" -> 2
                else -> 0
            },
            {
                onNewKind(when (it) {
                    1 -> "club"
                    2 -> "volunteering"
                    else -> "event"
                })
            }
        )

        Spacer(Modifier.height(14.dp))
        Text("Category", style = DfiTheme.typography.labelSmall, color = c.onSurface)
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catKey, catLabel) ->
                FilterChip(
                    selected = state.newCategory == catKey,
                    onClick = { onNewCategory(catKey) },
                    label = { Text(catLabel) }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        DfiTextField(
            value = state.newTitle,
            onValueChange = onNewTitle,
            label = "Event / Activity Title",
            placeholder = "e.g. Annual Tech Symposium 2026"
        )

        Spacer(Modifier.height(12.dp))
        DfiTextField(
            value = state.newVenue,
            onValueChange = onNewVenue,
            label = "Venue & Location",
            placeholder = "e.g. College Auditorium / Sports Complex"
        )

        Spacer(Modifier.height(12.dp))
        DfiTextField(
            value = state.newWhen,
            onValueChange = onNewWhen,
            label = "Date / Schedule",
            placeholder = "e.g. 15-16 Nov 2026, 10 AM"
        )

        Spacer(Modifier.height(12.dp))
        DfiTextField(
            value = state.newLink,
            onValueChange = onNewLink,
            label = "Registration Link / Official URL",
            placeholder = "https://yourcollege.edu/event"
        )

        Spacer(Modifier.height(12.dp))
        DfiTextField(
            value = state.newDescription,
            onValueChange = onNewDescription,
            label = "Description & Eligibility",
            placeholder = "Event details, eligibility rules, and what to bring...",
            singleLine = false,
            modifier = Modifier.heightIn(min = 90.dp)
        )

        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = "Publish to Explore",
            onClick = onPublish,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.newTitle.trim().length >= 3 && !state.publishingActivity,
            loading = state.publishingActivity
        )
        Spacer(Modifier.height(30.dp))
    }
}
