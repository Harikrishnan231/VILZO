package com.drugfreeindia.app.presentation.help

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.theme.DfiTheme

data class StudyTask(val id: Int, val title: String, val category: String, var isDone: Boolean)

@Composable
fun DiscreetNotesScreen(
    onReturnToApp: () -> Unit
) {
    val tasks = remember {
        mutableStateListOf(
            StudyTask(1, "Review Calculus Chapter 4 Problem Set", "Mathematics", true),
            StudyTask(2, "Complete Digital Electronics Lab report", "Engineering", false),
            StudyTask(3, "Submit Literature essay on modern Indian poetry", "Humanities", false),
            StudyTask(4, "Prepare slide deck for group seminar on Friday", "Seminar", true),
            StudyTask(5, "Library return: Operating System Concepts 10th Ed", "Library", false)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header: Looks like a student task & notes utility
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DfiTheme.colors.surfaceMuted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EditNote,
                            contentDescription = null,
                            tint = DfiTheme.colors.ink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Study Planner",
                            style = DfiTheme.typography.titleM,
                            color = DfiTheme.colors.ink
                        )
                        Text(
                            text = "Semester Tasks & Checklists",
                            style = DfiTheme.typography.caption,
                            color = DfiTheme.colors.inkSecondary
                        )
                    }
                }

                // Discreet return trigger
                Text(
                    text = "Sync ✓",
                    style = DfiTheme.typography.labelM.copy(color = DfiTheme.colors.inkTertiary),
                    modifier = Modifier.clickable { onReturnToApp() }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks) { task ->
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val idx = tasks.indexOf(task)
                            if (idx != -1) {
                                tasks[idx] = task.copy(isDone = !task.isDone)
                            }
                        },
                        padding = 16.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (task.isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (task.isDone) DfiTheme.colors.accent else DfiTheme.colors.inkTertiary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    style = DfiTheme.typography.bodyL.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (task.isDone) DfiTheme.colors.inkTertiary else DfiTheme.colors.ink
                                    )
                                )
                                Text(
                                    text = task.category,
                                    style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                DfiButton(
                    text = "Return to Dashboard",
                    onClick = onReturnToApp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
