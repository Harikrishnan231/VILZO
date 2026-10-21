package com.drugfreeindia.app.presentation.play

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun StoryLibraryScreen(
    onBack: () -> Unit,
    onOpenStory: (String) -> Unit,
    vm: StoryLibraryViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    LaunchedEffect(Unit) { vm.refresh() }

    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Story Mode", onBack)

        if (s.loading) {
            LoadingState()
            return@Column
        }

        val story = s.story
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Fictional Life Journeys",
                style = DfiTheme.typography.screenTitle,
                color = c.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Every decision opens a different path. Discover how everyday choices and support can change a student's journey.",
                style = DfiTheme.typography.bodySmall,
                color = c.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            if (story != null) {
                DfiCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = PaddingValues(0.dp)
                ) {
                    Column {
                        AppImage(
                            key = "story_tcnn_cover",
                            contentDescription = "Illustration of Arjun standing on a campus pathway",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(DfiTheme.shapes.hero)
                        )

                        Column(Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Tag("Interactive Drama", color = c.primaryDark, bg = c.primarySoft)
                                if (s.hasCompleted) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Rounded.CheckCircle,
                                            null,
                                            tint = c.success,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Completed",
                                            style = DfiTheme.typography.caption,
                                            color = c.success
                                        )
                                    }
                                } else if (s.hasActiveProgress) {
                                    Tag("In Progress", color = c.warning)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            Text(
                                story.title,
                                style = DfiTheme.typography.heroTitle,
                                color = c.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            story.tagline?.let {
                                Text(
                                    it,
                                    style = DfiTheme.typography.label,
                                    color = c.primaryDark
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                            Text(
                                "Arjun's life looks ordinary from the outside. But small changes in his routine are beginning to affect his studies, friendships, and family. Every decision opens a different path.",
                                style = DfiTheme.typography.body,
                                color = c.onSurfaceVariant
                            )

                            Spacer(Modifier.height(14.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Schedule,
                                        null,
                                        tint = c.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "~5 mins",
                                        style = DfiTheme.typography.caption,
                                        color = c.onSurfaceVariant
                                    )
                                }
                                Text("·", color = c.onSurfaceVariant)
                                Text(
                                    "English",
                                    style = DfiTheme.typography.caption,
                                    color = c.onSurfaceVariant
                                )
                                Text("·", color = c.onSurfaceVariant)
                                Text(
                                    "3 Endings",
                                    style = DfiTheme.typography.caption,
                                    color = c.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            when {
                                s.hasActiveProgress -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        PrimaryButton(
                                            text = "Continue",
                                            onClick = { onOpenStory(story.id) },
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.Rounded.PlayArrow
                                        )
                                        SecondaryButton(
                                            text = "Restart",
                                            onClick = {
                                                vm.restartStory(story.id) { onOpenStory(story.id) }
                                            },
                                            icon = Icons.Rounded.Replay
                                        )
                                    }
                                }
                                s.hasCompleted -> {
                                    PrimaryButton(
                                        text = "Replay Story",
                                        onClick = {
                                            vm.restartStory(story.id) { onOpenStory(story.id) }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        icon = Icons.Rounded.Replay
                                    )
                                }
                                else -> {
                                    PrimaryButton(
                                        text = "Start Story",
                                        onClick = { onOpenStory(story.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        icon = Icons.Rounded.PlayArrow
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            SectionHeader("More stories")
            Spacer(Modifier.height(10.dp))
            DfiCard(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(16.dp),
                color = c.surfaceMuted
            ) {
                Column {
                    Tag("Coming Soon", color = c.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "More life journeys in development",
                        style = DfiTheme.typography.cardTitle,
                        color = c.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "New narrative paths exploring campus life, peer dynamics, and recovery will be added in upcoming editions.",
                        style = DfiTheme.typography.bodySmall,
                        color = c.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
