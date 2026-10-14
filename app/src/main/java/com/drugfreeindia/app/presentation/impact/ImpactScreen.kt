package com.drugfreeindia.app.presentation.impact

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.CollegeStat
import com.drugfreeindia.app.domain.model.SurveyQuestion
import com.drugfreeindia.app.presentation.components.ComparisonBar
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiScreen
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.Sparkline
import com.drugfreeindia.app.presentation.components.StatTile
import com.drugfreeindia.app.presentation.components.TintCard
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.formatPoints
import com.drugfreeindia.app.presentation.util.formatStudents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(
    viewModel: ImpactViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showSurveySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val bottomPadding = LocalBottomBarPadding.current

    val tabs = listOf("My progress", "Community", "Institution")

    DfiScreen(
        title = "Track impact",
        subtitle = "Real progress, confidence and community health",
        tabs = {
            SegmentedTabs(
                tabs = tabs,
                selectedIndex = state.selectedSegment,
                onTabSelected = viewModel::onSegmentSelected
            )
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
            // Monthly Pulse Survey Card (TintCard)
            item {
                TintCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(
                            icon = if (state.isSurveyCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Poll,
                            size = 48.dp,
                            iconSize = 24.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Monthly pulse survey",
                                style = DfiTheme.typography.titleM,
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (state.isSurveyCompleted) "Completed for October · Thank you!" else "Anonymous, aggregated insights · takes 1 min",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                            )
                        }
                        if (!state.isSurveyCompleted) {
                            DfiButton(
                                text = "Start",
                                onClick = { showSurveySheet = true },
                                modifier = Modifier.heightIn(min = 36.dp)
                            )
                        }
                    }
                }
            }

            // Tab 0: My Progress
            if (state.selectedSegment == 0) {
                // Confidence Card: StatXL + ProgressRing 104dp
                item {
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 20.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Refusal Confidence",
                                    style = DfiTheme.typography.titleS,
                                    color = DfiTheme.colors.inkSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${state.confidence.overallScore}",
                                        style = DfiTheme.typography.statXL,
                                        color = DfiTheme.colors.ink
                                    )
                                    Text(
                                        text = "/100",
                                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary),
                                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DfiTheme.colors.accentSoft)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
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

                            ProgressRing(
                                progress = state.confidence.overallScore / 100f,
                                size = 104.dp,
                                strokeWidth = 12.dp,
                                color = DfiTheme.colors.accent
                            ) {
                                Text(
                                    text = "${state.confidence.overallScore}%",
                                    style = DfiTheme.typography.titleM.copy(fontWeight = FontWeight.Bold),
                                    color = DfiTheme.colors.ink
                                )
                            }
                        }
                    }
                }

                // Confidence Trend Sparkline Card with Month labels
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
                                    text = "Confidence trend",
                                    style = DfiTheme.typography.titleM,
                                    color = DfiTheme.colors.ink
                                )
                                Text(
                                    text = "+34% overall",
                                    style = DfiTheme.typography.labelM.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Sparkline(
                                points = state.confidenceTrend,
                                labels = listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct"),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                            )
                        }
                    }
                }

                // Stat Tiles (Meaningful labels per prompt)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatTile(
                            label = "Stories completed",
                            value = "${state.storiesCompletedCount} of 7",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            label = "Activities joined",
                            value = "${state.activitiesJoinedCount}",
                            trailing = "This month",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            label = "Current streak",
                            value = "${state.streak.current} days",
                            delta = "Active",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Tab 1: Community View
            if (state.selectedSegment == 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Community real impact",
                            style = DfiTheme.typography.titleM,
                            color = DfiTheme.colors.ink
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DfiTheme.colors.surfaceMuted)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sample data · ${state.cohortSize} students",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                            )
                        }
                    }
                }

                // Four ComparisonBars
                item {
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 16.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ComparisonBar(label = "Refusal Skills", before = 48, after = 82)
                            ComparisonBar(label = "Support Awareness", before = 52, after = 87)
                            ComparisonBar(label = "Positive Activities", before = 21, after = 64)
                            ComparisonBar(label = "Risk Knowledge", before = 56, after = 86)
                        }
                    }
                }

                // Insight Card ("Top awareness gap")
                item {
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 16.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            IconTile(
                                icon = Icons.Rounded.Insights,
                                size = 44.dp,
                                iconSize = 22.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Top awareness gap",
                                    style = DfiTheme.typography.titleM,
                                    color = DfiTheme.colors.ink
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Students identified social gathering pressure at farewell parties and hostel weekends as the highest-risk moments. Our scenario modules directly target these specific contexts.",
                                    style = DfiTheme.typography.bodyL,
                                    color = DfiTheme.colors.inkSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Tab 2: Institution View
            if (state.selectedSegment == 2) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "College participation roster",
                            style = DfiTheme.typography.titleM,
                            color = DfiTheme.colors.ink,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DfiTheme.colors.surfaceMuted)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Aggregates only",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.accentDark, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Roster Cards with 28dp rank medallion, TitleS, Caption students, right-aligned formatted points
                item {
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            state.collegeStats.forEachIndexed { index, college ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 28dp rank medallion (top 3 filled accent tint)
                                        val isTop3 = index < 3
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (isTop3) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${college.rank}",
                                                style = DfiTheme.typography.caption.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isTop3) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = college.collegeName,
                                                style = DfiTheme.typography.titleS,
                                                color = DfiTheme.colors.ink,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = formatStudents(college.participants),
                                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = formatPoints(college.points),
                                            style = DfiTheme.typography.titleS.copy(
                                                color = DfiTheme.colors.accentDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    // Relative thin progress bar under row
                                    val maxPoints = state.collegeStats.firstOrNull()?.points ?: 1
                                    val ratio = (college.points.toFloat() / maxPoints).coerceIn(0.1f, 1f)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(DfiTheme.colors.surfaceMuted)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(ratio)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(DfiTheme.colors.accent)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Export Button (Secondary full-width with download icon) + Sample Data Disclaimer Chip
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DfiButton(
                            text = "Export institutional report (CSV)",
                            onClick = {
                                val csv = viewModel.exportInstitutionCsv()
                                Toast.makeText(context, "Report exported (aggregated): ${csv.lines().size} rows", Toast.LENGTH_SHORT).show()
                            },
                            variant = DfiButtonVariant.Secondary,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.FileDownload,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.ink,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DfiTheme.colors.surfaceMuted)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Illustrative sample data · Neutral aggregates",
                                style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary)
                            )
                        }
                    }
                }
            }
        }

        // Survey Modal Bottom Sheet
        if (showSurveySheet) {
            ModalBottomSheet(
                onDismissRequest = { showSurveySheet = false },
                sheetState = sheetState,
                containerColor = DfiTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Monthly Pulse Survey",
                        style = DfiTheme.typography.titleL,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Anonymous · Never tied to your identity",
                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.accentDark, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.surveyQuestions.forEachIndexed { qIdx, question ->
                        Text(
                            text = "${qIdx + 1}. ${question.questionText}",
                            style = DfiTheme.typography.bodyM.copy(fontWeight = FontWeight.SemiBold),
                            color = DfiTheme.colors.ink
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            question.options.forEach { option ->
                                val isSelected = state.selectedAnswers[question.id] == option
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.surfaceMuted
                                        )
                                        .clickable { viewModel.onSelectAnswer(question.id, option) }
                                        .padding(horizontal = 4.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        style = DfiTheme.typography.labelM.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else DfiTheme.colors.ink
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    DfiButton(
                        text = "Submit pulse response",
                        onClick = {
                            viewModel.submitSurvey()
                            showSurveySheet = false
                            Toast.makeText(context, "Thank you! Your feedback strengthens campus programmes.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
