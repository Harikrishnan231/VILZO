package com.drugfreeindia.app.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhoneInTalk
import androidx.compose.material.icons.rounded.Spa
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.presentation.components.Avatar
import com.drugfreeindia.app.presentation.components.BentoSpan
import com.drugfreeindia.app.presentation.components.BentoTile
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.HeroCard
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.Sparkline
import com.drugfreeindia.app.presentation.components.StatTile
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.formatPoints

@Composable
fun HomeScreen(
    onNavigateToPlayStory: (String) -> Unit,
    onNavigateToHelpTab: () -> Unit,
    onNavigateToTrustedContacts: () -> Unit,
    onNavigateToHelplines: () -> Unit,
    onNavigateToCentres: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToImpact: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val bottomPadding = LocalBottomBarPadding.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header Top Bar
            HomeHeader(
                greeting = state.greeting,
                nickname = state.nickname,
                onProfileClick = onNavigateToProfile
            )

            // Bento Grid Content
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal),
                contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 2. Hero "Weekly challenge" card (2x1)
                item(span = { GridItemSpan(2) }) {
                    HeroCard(
                        onClick = { onNavigateToPlayStory("farewell_party") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                    ) {
                        // Decorative concentric rings drawn in AccentSoft bleeding off bottom-right
                        Canvas(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            val centerOffset = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.9f)
                            drawCircle(
                                color = Color(0xFFDDF2EE).copy(alpha = 0.5f),
                                radius = 90.dp.toPx(),
                                center = centerOffset,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawCircle(
                                color = Color(0xFFDDF2EE).copy(alpha = 0.35f),
                                radius = 130.dp.toPx(),
                                center = centerOffset,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(DfiTheme.colors.surface, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Timer,
                                        contentDescription = null,
                                        tint = DfiTheme.colors.accentDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "30 sec",
                                        style = DfiTheme.typography.labelM.copy(
                                            color = DfiTheme.colors.accentDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Text(
                                    text = "Weekly challenge",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.inkSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Say No with Confidence",
                                style = DfiTheme.typography.titleL,
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Arjun faces terrace party pressure. Practise calm refusal and clear boundary setting.",
                                style = DfiTheme.typography.bodyM,
                                color = DfiTheme.colors.inkSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DfiButton(
                                text = "Start challenge",
                                onClick = { onNavigateToPlayStory("farewell_party") },
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

                // 3. Bento row A: Confidence (1x2) on left
                item(span = { GridItemSpan(1) }) {
                    BentoTile(
                        span = BentoSpan.Span1x2,
                        onClick = onNavigateToImpact
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Refusal Confidence",
                                style = DfiTheme.typography.labelM,
                                color = DfiTheme.colors.inkSecondary,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            ProgressRing(
                                progress = state.confidence.overallScore / 100f,
                                size = 132.dp,
                                strokeWidth = 12.dp
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${state.confidence.overallScore}",
                                        style = DfiTheme.typography.statXL,
                                        color = DfiTheme.colors.ink
                                    )
                                    Text(
                                        text = "/100",
                                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(DfiTheme.colors.accentSoft, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "↗ +${state.confidence.deltaThisWeek} this week",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                // Right column: Streak (1x1) & Badges (1x1)
                item(span = { GridItemSpan(1) }) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Streak Tile (1x1)
                        BentoTile(
                            span = BentoSpan.Span1x1,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current Streak",
                                        style = DfiTheme.typography.labelM,
                                        color = DfiTheme.colors.inkSecondary
                                    )
                                    IconTile(
                                        icon = Icons.Rounded.LocalFireDepartment,
                                        size = 32.dp,
                                        iconSize = 18.dp
                                    )
                                }

                                Text(
                                    text = "${state.streak.current} days",
                                    style = DfiTheme.typography.statL,
                                    color = DfiTheme.colors.ink
                                )

                                // 7-dot weekly strip with M T W T F S S
                                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    days.forEachIndexed { idx, day ->
                                        val isActive = idx < (state.streak.current % 8)
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isActive) DfiTheme.colors.accent else DfiTheme.colors.outline
                                                    )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = day,
                                                style = DfiTheme.typography.caption.copy(
                                                    fontSize = 9.sp,
                                                    color = if (isActive) DfiTheme.colors.ink else DfiTheme.colors.inkTertiary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Badges Tile (1x1)
                        BentoTile(
                            span = BentoSpan.Span1x1,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNavigateToProfile
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Badges",
                                        style = DfiTheme.typography.labelM,
                                        color = DfiTheme.colors.inkSecondary
                                    )
                                    IconTile(
                                        icon = Icons.Rounded.EmojiEvents,
                                        size = 32.dp,
                                        iconSize = 18.dp
                                    )
                                }

                                Text(
                                    text = "${state.earnedBadgesCount}",
                                    style = DfiTheme.typography.statL,
                                    color = DfiTheme.colors.ink
                                )

                                Text(
                                    text = "Latest: Calm No",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.inkSecondary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 4. Help card (white, no tint, softShadow)
                item(span = { GridItemSpan(2) }) {
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 20.dp,
                        onClick = onNavigateToHelpTab
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Help is one tap away",
                                    style = DfiTheme.typography.titleM,
                                    color = DfiTheme.colors.ink
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(DfiTheme.colors.surfaceMuted, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = DfiTheme.colors.accentDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Private by design",
                                        style = DfiTheme.typography.labelM.copy(
                                            color = DfiTheme.colors.accentDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HomeQuickActionTile(
                                    icon = Icons.Rounded.Call,
                                    label = "Trusted person",
                                    onClick = onNavigateToTrustedContacts,
                                    modifier = Modifier.weight(1f)
                                )
                                HomeQuickActionTile(
                                    icon = Icons.Rounded.PhoneInTalk,
                                    label = "Helplines",
                                    onClick = onNavigateToHelplines,
                                    modifier = Modifier.weight(1f)
                                )
                                HomeQuickActionTile(
                                    icon = Icons.Rounded.LocalHospital,
                                    label = "Centres",
                                    onClick = onNavigateToCentres,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 5. Bento row B: Next activity & Confidence trend
                item(span = { GridItemSpan(1) }) {
                    BentoTile(
                        span = BentoSpan.Span1x1,
                        onClick = onNavigateToExplore
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Next Activity",
                                style = DfiTheme.typography.labelM,
                                color = DfiTheme.colors.inkSecondary
                            )
                            Column {
                                Text(
                                    text = "Football Club",
                                    style = DfiTheme.typography.titleS,
                                    color = DfiTheme.colors.ink,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Sat 5 PM · Stadium",
                                    style = DfiTheme.typography.labelM.copy(color = DfiTheme.colors.accentDark),
                                    maxLines = 1
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(DfiTheme.colors.accentSoft, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Join",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(1) }) {
                    BentoTile(
                        span = BentoSpan.Span1x1,
                        onClick = onNavigateToImpact
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Confidence trend",
                                style = DfiTheme.typography.labelM,
                                color = DfiTheme.colors.inkSecondary
                            )
                            Sparkline(
                                points = state.confidenceTrend,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(DfiTheme.colors.accentSoft, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "↗ +12% in 6 mo",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                // 6. Community snapshot (horizontally scrolling row of 4 StatTiles)
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "Community impact",
                            actionLabel = "View all",
                            onAction = onNavigateToImpact
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatTile(
                                label = "Refusal skills",
                                value = "82%",
                                delta = "+34%",
                                trailing = "Sample programme data",
                                modifier = Modifier.width(160.dp)
                            )
                            StatTile(
                                label = "Support awareness",
                                value = "87%",
                                delta = "+35%",
                                trailing = "Sample programme data",
                                modifier = Modifier.width(160.dp)
                            )
                            StatTile(
                                label = "Positive activities",
                                value = "64%",
                                delta = "+43%",
                                trailing = "Sample programme data",
                                modifier = Modifier.width(160.dp)
                            )
                            StatTile(
                                label = "Risk understanding",
                                value = "86%",
                                delta = "+30%",
                                trailing = "Sample programme data",
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // 7. College leaderboard preview
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "College leaderboard",
                            actionLabel = "See all",
                            onAction = onNavigateToImpact
                        )

                        DfiCard(
                            modifier = Modifier.fillMaxWidth(),
                            padding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                state.topColleges.take(3).forEachIndexed { index, college ->
                                    ListRow(
                                        leading = {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        if (index == 0) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted,
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    style = DfiTheme.typography.labelL.copy(
                                                        color = if (index == 0) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        },
                                        title = college.collegeName,
                                        subtitle = "${college.participants} students participated",
                                        trailing = {
                                            Text(
                                                text = formatPoints(college.points),
                                                style = DfiTheme.typography.titleS.copy(
                                                    color = DfiTheme.colors.accentDark,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        },
                                        onClick = onNavigateToImpact
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

@Composable
private fun HomeHeader(
    greeting: String,
    nickname: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(DfiTheme.colors.accentSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Spa,
                    contentDescription = null,
                    tint = DfiTheme.colors.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = greeting,
                    style = DfiTheme.typography.bodyM,
                    color = DfiTheme.colors.inkSecondary
                )
                Text(
                    text = nickname,
                    style = DfiTheme.typography.display,
                    color = DfiTheme.colors.ink
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DfiTheme.colors.surfaceMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = DfiTheme.colors.inkSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Avatar(
                initials = nickname.take(2).uppercase(),
                size = 44.dp,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun HomeQuickActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = DfiTheme.colors.surfaceMuted
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconTile(
                icon = icon,
                size = 36.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = DfiTheme.typography.labelM.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DfiTheme.colors.ink
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
