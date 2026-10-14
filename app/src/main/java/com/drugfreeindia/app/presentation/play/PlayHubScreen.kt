package com.drugfreeindia.app.presentation.play

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiScreen
import com.drugfreeindia.app.presentation.components.HeroCard
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.formatPoints

@Composable
fun PlayHubScreen(
    onPlayStory: (String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    viewModel: PlayHubViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val bottomPadding = LocalBottomBarPadding.current

    DfiScreen(
        title = "Play & practice",
        subtitle = "Build muscle memory for tough moments",
        actions = {
            // Points chip with trophy icon and level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(DfiTheme.colors.surfaceMuted)
                    .clickable { onNavigateToLeaderboard() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                IconTile(
                    icon = Icons.Rounded.EmojiEvents,
                    size = 30.dp,
                    iconSize = 16.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = formatPoints(state.totalPoints),
                        style = DfiTheme.typography.labelL.copy(
                            fontWeight = FontWeight.Bold,
                            color = DfiTheme.colors.ink
                        )
                    )
                    Text(
                        text = state.levelName,
                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.accentDark)
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = DfiTheme.spacing.screenHorizontal,
                end = DfiTheme.spacing.screenHorizontal,
                top = 8.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Weekly Hero Card (Shared story art)
            item {
                HeroCard(
                    onClick = { onPlayStory("farewell_party") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                ) {
                    // Decorative rings
                    Canvas(
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        val center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.85f)
                        drawCircle(
                            color = Color(0xFFDDF2EE).copy(alpha = 0.5f),
                            radius = 80.dp.toPx(),
                            center = center,
                            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawCircle(
                            color = Color(0xFFDDF2EE).copy(alpha = 0.35f),
                            radius = 120.dp.toPx(),
                            center = center,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(DfiTheme.colors.surface, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SportsEsports,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.accentDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "WEEKLY SCENARIO",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.inkSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "35 sec",
                                    style = DfiTheme.typography.labelM.copy(color = DfiTheme.colors.inkSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "The Farewell Party",
                            style = DfiTheme.typography.titleL,
                            color = DfiTheme.colors.ink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Arjun faces terrace party pressure from close friends. Test calm refusal scripts and safe exit timing.",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DfiButton(
                            text = "Play simulation",
                            onClick = { onPlayStory("farewell_party") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }
            }

            // 2. Level progress card
            item {
                DfiCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 16.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Level Progress · ${state.levelName}",
                                style = DfiTheme.typography.titleS,
                                color = DfiTheme.colors.ink
                            )
                            Text(
                                text = "Next badge: Calm Boundary",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.accentDark)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 10dp XP bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(DfiTheme.colors.surfaceMuted)
                        ) {
                            val xpProgress = 0.65f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(xpProgress)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(DfiTheme.colors.accent)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${state.totalPoints} XP earned",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary)
                            )
                            Text(
                                text = "150 XP for next level",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary)
                            )
                        }
                    }
                }
            }

            // 3. Scenario Packs (horizontal row of 120x140dp pack tiles)
            item {
                SectionHeader(
                    title = "Scenario packs",
                    actionLabel = "Leaderboard",
                    onAction = onNavigateToLeaderboard
                )

                val packItems = listOf(
                    Triple("Campus", "2 of 3 done", 0.66f),
                    Triple("Hostel", "1 of 2 done", 0.5f),
                    Triple("Online chats", "New", 0.0f),
                    Triple("Sports ground", "1 of 2 done", 0.5f),
                    Triple("Family functions", "New", 0.0f)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(packItems) { (packName, statusText, progressVal) ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DfiTheme.colors.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DfiTheme.colors.outline.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .size(width = 120.dp, height = 140.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconTile(
                                        icon = Icons.Rounded.SportsEsports,
                                        size = 36.dp,
                                        iconSize = 18.dp
                                    )
                                    ProgressRing(
                                        progress = progressVal,
                                        size = 24.dp,
                                        strokeWidth = 3.dp,
                                        color = if (progressVal > 0f) DfiTheme.colors.accent else DfiTheme.colors.outline
                                    )
                                }

                                Column {
                                    Text(
                                        text = packName,
                                        style = DfiTheme.typography.titleS,
                                        color = DfiTheme.colors.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = statusText,
                                        style = DfiTheme.typography.caption.copy(
                                            color = if (progressVal > 0f) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. All scenarios header
            item {
                SectionHeader(title = "All interactive scenarios")
            }

            // 5. Scenarios List
            items(state.stories) { story ->
                val progress = state.progressMap[story.id]
                val isCompleted = progress != null

                ListRow(
                    leading = {
                        ProgressRing(
                            progress = if (isCompleted) 1f else 0.15f,
                            size = 52.dp,
                            strokeWidth = 5.dp,
                            color = if (isCompleted) DfiTheme.colors.accent else DfiTheme.colors.outline
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.accentDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    title = story.title,
                    subtitle = "${story.pack} · ${story.estimatedSeconds}s${if (progress != null) " · Best ${progress.bestScore} pts" else ""}",
                    trailing = {
                        DfiButton(
                            text = if (isCompleted) "Replay" else "Play",
                            variant = if (isCompleted) DfiButtonVariant.Secondary else DfiButtonVariant.Primary,
                            onClick = { onPlayStory(story.id) },
                            modifier = Modifier.heightIn(min = 36.dp)
                        )
                    },
                    onClick = { onPlayStory(story.id) }
                )
            }
        }
    }
}
