package com.drugfreeindia.app.presentation.play.story

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.StatTile
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.formatPoints

@Composable
fun StoryResultScreen(
    storyId: String,
    score: Int,
    delta: Int,
    badgeId: String?,
    onReplay: () -> Unit,
    onNextScenario: () -> Unit,
    onSaveToProgress: () -> Unit
) {
    var pulseTarget by remember { mutableFloatStateOf(0.9f) }
    val medallionScale by animateFloatAsState(
        targetValue = pulseTarget,
        animationSpec = tween(durationMillis = 600),
        label = "trophyPulse"
    )

    LaunchedEffect(Unit) {
        pulseTarget = 1f
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
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. 120dp Trophy Medallion with concentric AccentSoft rings
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(medallionScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ring
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(DfiTheme.colors.accentSoft.copy(alpha = 0.45f), CircleShape)
                    )
                    // Middle ring
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(DfiTheme.colors.accentSoft, CircleShape)
                    )
                    // Inner trophy circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(DfiTheme.colors.surface, CircleShape)
                            .border(1.5.dp, DfiTheme.colors.accentSoft, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Scenario Trophy",
                            tint = DfiTheme.colors.accent,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Scenario completed",
                    style = DfiTheme.typography.display,
                    color = DfiTheme.colors.ink
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Refusal confidence +$delta",
                    style = DfiTheme.typography.titleM.copy(
                        color = DfiTheme.colors.accentDark,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Stat row of 3 StatTiles (Points, Confidence delta, Badge)
                val badgeName = if (!badgeId.isNullOrBlank() && badgeId != "none") "Calm No" else "Participant"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = "Points",
                        value = formatPoints(score),
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Confidence",
                        value = "+$delta",
                        delta = "Boost",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Badge",
                        value = badgeName,
                        trailingContent = {
                            IconTile(
                                icon = Icons.Rounded.MilitaryTech,
                                size = 28.dp,
                                iconSize = 16.dp
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. "Your choice" recap card
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
                                text = "Your decision recap",
                                style = DfiTheme.typography.titleS,
                                color = DfiTheme.colors.ink
                            )
                            Box(
                                modifier = Modifier
                                    .background(DfiTheme.colors.accentSoft, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Calm no",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "You stated your limit clearly without judging others or hesitating. This stops peer pressure before it escalates.",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. "Try this next time" card
                DfiCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        IconTile(
                            icon = Icons.Rounded.Lightbulb,
                            size = 36.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Try this next time",
                                style = DfiTheme.typography.titleS,
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"No thanks, I've got an early project review tomorrow.\" — Simple, truthful, and leaves no room for debate.",
                                style = DfiTheme.typography.bodyM,
                                color = DfiTheme.colors.inkSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Level Progress Bar
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
                                text = "Level progress",
                                style = DfiTheme.typography.titleS,
                                color = DfiTheme.colors.ink
                            )
                            Text(
                                text = "75% to Next Tier",
                                style = DfiTheme.typography.labelM.copy(
                                    color = DfiTheme.colors.accentDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DfiTheme.colors.surfaceMuted)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DfiTheme.colors.accent)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. "Why this works" card (Neutral, accurate text without invented statistic per Section 8)
                DfiCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 16.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconTile(
                                icon = Icons.Rounded.TrendingUp,
                                size = 32.dp,
                                iconSize = 16.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Why this works",
                                style = DfiTheme.typography.titleS,
                                color = DfiTheme.colors.ink
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Practising what to say before the moment arrives makes it easier to say no when it counts. Short, calm lines work best.",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Pinned Bottom Actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DfiButton(
                    text = "Save to progress",
                    onClick = onSaveToProgress,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DfiButton(
                        text = "Replay",
                        onClick = onReplay,
                        variant = DfiButtonVariant.Secondary,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Replay,
                                contentDescription = null,
                                tint = DfiTheme.colors.ink,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DfiButton(
                        text = "Next scenario",
                        onClick = onNextScenario,
                        variant = DfiButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
