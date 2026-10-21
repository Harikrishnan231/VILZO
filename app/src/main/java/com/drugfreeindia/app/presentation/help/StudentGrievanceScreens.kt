package com.drugfreeindia.app.presentation.help

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.AnonymousComplaint
import com.drugfreeindia.app.domain.model.AnonymousComplaintMessage
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.ErrorState
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun StudentGrievanceRootScreen(
    onBack: () -> Unit,
    vm: StudentGrievanceViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()

    BackHandler {
        if (s.activeComplaint != null) {
            vm.closeComplaintChat()
        } else if (s.isCreating) {
            vm.closeCreateDialog()
        } else {
            onBack()
        }
    }

    if (s.activeComplaint != null) {
        StudentComplaintChatScreen(
            complaint = s.activeComplaint!!,
            messages = s.messages,
            loadingMessages = s.loadingMessages,
            chatInput = s.chatInput,
            sendingMessage = s.sendingMessage,
            onInputChange = vm::onChatInputChange,
            onSendMessage = vm::sendChatMessage,
            onClearChat = vm::clearActiveChat,
            onClose = vm::closeComplaintChat
        )
    } else {
        StudentGrievanceListScreen(
            state = s,
            onBack = onBack,
            onOpenCreate = vm::openCreateDialog,
            onSelectComplaint = vm::openComplaintChat
        )
    }

    if (s.isCreating) {
        NewGrievanceSheet(
            state = s,
            onCategoryChange = vm::onCategoryChange,
            onCollegeChange = vm::onCollegeChange,
            onSubjectChange = vm::onSubjectChange,
            onMessageChange = vm::onMessageChange,
            onSubmit = vm::submitComplaint,
            onDismiss = vm::closeCreateDialog
        )
    }
}

// ==============================================================
// 1. Student Grievance List Screen
// ==============================================================

@Composable
private fun StudentGrievanceListScreen(
    state: StudentGrievanceUiState,
    onBack: () -> Unit,
    onOpenCreate: () -> Unit,
    onSelectComplaint: (AnonymousComplaint) -> Unit
) {
    val c = DfiTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Bar
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = c.onSurface)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Anonymous Campus Grievance",
                    style = DfiTheme.typography.cardTitle,
                    color = c.onSurface
                )
                Text(
                    "Confidential reports seen only by your college authorities",
                    style = DfiTheme.typography.caption,
                    color = c.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Privacy Guarantee Card
        DfiCard(color = c.primarySoft, padding = PaddingValues(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lock, null, tint = c.primaryDark, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "100% Anonymous & Zero-Knowledge",
                        style = DfiTheme.typography.labelSmall,
                        color = c.primaryDark
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Your name, email, and phone are never stored or shared with your college. You are identified only as 'Anonymous Student'.",
                        style = DfiTheme.typography.caption,
                        color = c.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Action Button: File New Report
        PrimaryButton(
            text = "File Anonymous Report",
            onClick = onOpenCreate,
            icon = Icons.Rounded.Add,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Your Active Reports",
            style = DfiTheme.typography.sectionTitle,
            color = c.onSurface
        )

        Spacer(Modifier.height(10.dp))

        if (state.loading) {
            LoadingState()
        } else if (state.complaints.isEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.Security,
                    null,
                    tint = c.primaryDark,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "No Reports Filed Yet",
                    style = DfiTheme.typography.cardTitle,
                    color = c.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "If you notice substance use, ragging, or need confidential help from your college grievance cell, tap the button above to reach out safely.",
                    style = DfiTheme.typography.bodySmall,
                    color = c.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.complaints, key = { it.id }) { complaint ->
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
                                    "submitted" -> "Submitted" to c.warning
                                    "in_review" -> "Reviewed by College" to c.primary
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
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tap to view chat with college",
                                    style = DfiTheme.typography.caption,
                                    color = c.primaryDark
                                )
                                Icon(
                                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    null,
                                    tint = c.primaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==============================================================
// 2. New Anonymous Report Sheet
// ==============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewGrievanceSheet(
    state: StudentGrievanceUiState,
    onCategoryChange: (String) -> Unit,
    onCollegeChange: (com.drugfreeindia.app.domain.model.College) -> Unit,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val c = DfiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var categoryExpanded by remember { mutableStateOf(false) }
    var collegeExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "substance_misuse" to "Substance Misuse / Dealing on Campus",
        "ragging" to "Ragging / Harassment",
        "mental_distress" to "Mental Health & Stress Support",
        "campus_safety" to "Campus & Hostel Safety Concern",
        "other" to "Other Campus Grievance"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = c.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "New Anonymous Report",
                    style = DfiTheme.typography.sectionTitle,
                    color = c.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, "Close")
                }
            }

            Spacer(Modifier.height(10.dp))

            // College selector
            Text("Target Institution", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = collegeExpanded,
                onExpandedChange = { collegeExpanded = !collegeExpanded }
            ) {
                OutlinedTextField(
                    value = state.selectedCollege?.name ?: "Select College",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = collegeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = DfiTheme.shapes.input
                )
                ExposedDropdownMenu(
                    expanded = collegeExpanded,
                    onDismissRequest = { collegeExpanded = false }
                ) {
                    state.colleges.forEach { college ->
                        DropdownMenuItem(
                            text = { Text(college.name) },
                            onClick = {
                                onCollegeChange(college)
                                collegeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Category selector
            Text("Category", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                val currentLabel = categories.firstOrNull { it.first == state.selectedCategory }?.second ?: "Select Category"
                OutlinedTextField(
                    value = currentLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = DfiTheme.shapes.input
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onCategoryChange(key)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Subject
            Text("Subject Summary", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.subject,
                onValueChange = onSubjectChange,
                placeholder = { Text("Brief headline (e.g. Activity observed near hostel block)") },
                modifier = Modifier.fillMaxWidth(),
                shape = DfiTheme.shapes.input
            )

            Spacer(Modifier.height(14.dp))

            // Message detail
            Text("Details & Observations", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.message,
                onValueChange = onMessageChange,
                placeholder = { Text("Describe what happened or where help is needed. Please do not include your personal name if you wish to remain strictly anonymous.") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                shape = DfiTheme.shapes.input
            )

            state.error?.let {
                Spacer(Modifier.height(10.dp))
                ErrorState(it)
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Send Anonymously to College",
                onClick = onSubmit,
                loading = state.submitting,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ==============================================================
// 3. Student Grievance Chat Screen
// ==============================================================

@Composable
private fun StudentComplaintChatScreen(
    complaint: AnonymousComplaint,
    messages: List<AnonymousComplaintMessage>,
    loadingMessages: Boolean,
    chatInput: String,
    sendingMessage: Boolean,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onClearChat: () -> Unit,
    onClose: () -> Unit
) {
    val c = DfiTheme.colors
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = c.onSurface)
            }
            Spacer(Modifier.width(4.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    complaint.subject,
                    style = DfiTheme.typography.cardTitle,
                    color = c.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val statusText = when (complaint.status) {
                    "submitted" -> "Delivered to College"
                    "in_review" -> "Reviewed by Authority"
                    else -> "Resolved by Campus Cell"
                }
                Text(
                    statusText,
                    style = DfiTheme.typography.caption,
                    color = c.primaryDark
                )
            }
            // Clear Chat Option in Top Bar
            IconButton(onClick = { showClearDialog = true }) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Clear Chat",
                    tint = c.onSurfaceVariant
                )
            }
        }

        if (showClearDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Clear Chat") },
                text = { Text("Are you sure you want to clear this anonymous conversation? All messages will be permanently deleted.") },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showClearDialog = false
                            onClearChat()
                        }
                    ) {
                        Text("Clear", color = Color(0xFFD32F2F))
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Privacy Guarantee Banner
        Box(
            Modifier
                .fillMaxWidth()
                .background(c.primarySoft)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lock, null, tint = c.primaryDark, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "You are messaging as ${complaint.studentPseudonym}. Your identity is protected.",
                    style = DfiTheme.typography.caption,
                    color = c.primaryDark
                )
            }
        }

        // Messages List
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (loadingMessages) {
                LoadingState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isStudent = msg.senderRole == "student"
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isStudent) Alignment.End else Alignment.Start
                        ) {
                            Text(
                                if (isStudent) "You (Anonymous)" else "College Grievance Cell",
                                style = DfiTheme.typography.caption,
                                color = if (isStudent) c.primaryDark else c.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .clip(DfiTheme.shapes.card)
                                    .background(if (isStudent) c.primary else c.surface)
                                    .border(1.dp, if (isStudent) c.primary else c.outline, DfiTheme.shapes.card)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    msg.message,
                                    style = DfiTheme.typography.body,
                                    color = if (isStudent) Color.White else c.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chat Input Row
        Row(
            Modifier
                .fillMaxWidth()
                .background(c.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = onInputChange,
                placeholder = { Text("Send follow-up message anonymously...") },
                modifier = Modifier.weight(1f),
                shape = DfiTheme.shapes.input
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onSendMessage,
                enabled = chatInput.isNotBlank() && !sendingMessage
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    "Send message",
                    tint = if (chatInput.isNotBlank()) c.primary else c.disabled
                )
            }
        }
    }
}
