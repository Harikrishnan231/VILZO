package com.drugfreeindia.app.presentation.explore

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.R
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Event
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.presentation.components.Avatar
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiChipRow
import com.drugfreeindia.app.presentation.components.DfiScreen
import com.drugfreeindia.app.presentation.components.HeroCard
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.theme.DfiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    var mentorChatDialogMentor by remember { mutableStateOf<Mentor?>(null) }
    var showTrustSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomPadding = LocalBottomBarPadding.current

    val tabs = listOf("Activities", "Events", "Mentors")

    DfiScreen(
        title = "Explore & connect",
        subtitle = "Healthy habits, clubs and communities",
        tabs = {
            SegmentedTabs(
                tabs = tabs,
                selectedIndex = state.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = {
                        Text(
                            text = "Search clubs, events, or mentors…",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = DfiTheme.colors.inkTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear",
                                    tint = DfiTheme.colors.inkSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DfiTheme.colors.surfaceMuted,
                        unfocusedContainerColor = DfiTheme.colors.surfaceMuted,
                        focusedBorderColor = DfiTheme.colors.accent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }

            // Category Chips (Only on Activities tab)
            if (state.selectedTab == 0) {
                val categories = listOf("All", "Sports", "Art", "Music", "Dance", "Poetry", "Volunteering", "Fitness", "Skills")
                DfiChipRow(
                    items = categories,
                    selectedItem = state.selectedCategory,
                    onItemSelected = viewModel::onCategorySelected,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Content List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = DfiTheme.spacing.screenHorizontal,
                    end = DfiTheme.spacing.screenHorizontal,
                    top = 6.dp,
                    bottom = bottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Shortened Trust Banner
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DfiTheme.colors.surfaceMuted)
                            .clickable { showTrustSheet = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Security,
                                contentDescription = null,
                                tint = DfiTheme.colors.accentDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Private by design · Community moderated",
                                style = DfiTheme.typography.labelM.copy(
                                    color = DfiTheme.colors.ink,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Details",
                            tint = DfiTheme.colors.inkTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // "Happening this week" horizontally scrolling 260x150dp featured cards
                if (state.selectedTab == 0 && state.activities.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Happening this week",
                                style = DfiTheme.typography.titleM,
                                color = DfiTheme.colors.ink,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                state.activities.take(3).forEach { act ->
                                    HeroCard(
                                        onClick = { selectedActivity = act },
                                        modifier = Modifier
                                            .size(width = 260.dp, height = 150.dp)
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
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(DfiTheme.colors.surface)
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = act.category,
                                                        style = DfiTheme.typography.caption.copy(
                                                            color = DfiTheme.colors.accentDark,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }
                                                Text(
                                                    text = act.schedule,
                                                    style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary),
                                                    maxLines = 1
                                                )
                                            }

                                            Text(
                                                text = act.title,
                                                style = DfiTheme.typography.titleM,
                                                color = DfiTheme.colors.ink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            DfiButton(
                                                text = if (act.isJoined) "Joined" else "Join",
                                                variant = if (act.isJoined) DfiButtonVariant.Secondary else DfiButtonVariant.Primary,
                                                onClick = { viewModel.toggleActivityJoin(act.id, act.isJoined) },
                                                modifier = Modifier.heightIn(min = 36.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab Content Feeds
                when (state.selectedTab) {
                    0 -> {
                        // Activities Feed
                        items(state.activities) { activity ->
                            ActivityCardV2(
                                activity = activity,
                                onCardClick = { selectedActivity = activity },
                                onToggleJoin = { viewModel.toggleActivityJoin(activity.id, activity.isJoined) }
                            )
                        }
                    }
                    1 -> {
                        // Events Feed
                        items(state.events) { event ->
                            EventCardV2(
                                event = event,
                                onToggleReminder = { viewModel.toggleEventReminder(event.id, event.isReminderSet) }
                            )
                        }
                    }
                    2 -> {
                        // Mentors Feed
                        items(state.mentors) { mentor ->
                            MentorCardV2(
                                mentor = mentor,
                                onRequestChat = { mentorChatDialogMentor = mentor }
                            )
                        }
                    }
                }
            }
        }

        // Trust Sheet
        if (showTrustSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTrustSheet = false },
                containerColor = DfiTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Safe & Verified Clubs",
                        style = DfiTheme.typography.titleL,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.explore_trust_banner),
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.inkSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    DfiButton(
                        text = "Understood",
                        onClick = { showTrustSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Activity Detail Bottom Sheet
        selectedActivity?.let { activity ->
            ModalBottomSheet(
                onDismissRequest = { selectedActivity = null },
                sheetState = sheetState,
                shape = DfiTheme.shapes.bottomSheet,
                containerColor = DfiTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activity.title,
                                style = DfiTheme.typography.titleL,
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${activity.category} · Organized by ${activity.organizer}",
                                style = DfiTheme.typography.bodyM.copy(
                                    color = DfiTheme.colors.accentDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        if (activity.isVerified) {
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Verified",
                                tint = DfiTheme.colors.accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "About this Club",
                        style = DfiTheme.typography.titleS,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activity.description,
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.inkSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Location & Timings",
                        style = DfiTheme.typography.titleS,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${activity.location}\n${activity.schedule}",
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.inkSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    DfiButton(
                        text = if (activity.isJoined) "Leave Club" else "Join this Activity",
                        variant = if (activity.isJoined) DfiButtonVariant.Secondary else DfiButtonVariant.Primary,
                        onClick = {
                            viewModel.toggleActivityJoin(activity.id, activity.isJoined)
                            selectedActivity = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityCardV2(
    activity: Activity,
    onCardClick: () -> Unit,
    onToggleJoin: () -> Unit
) {
    DfiCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onCardClick,
        padding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 56dp category IconTile
            IconTile(
                icon = Icons.Rounded.SportsSoccer,
                size = 56.dp,
                iconSize = 28.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activity.title,
                        style = DfiTheme.typography.titleM,
                        color = DfiTheme.colors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (activity.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = "Verified",
                            tint = DfiTheme.colors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${activity.category} · ${activity.location}",
                    style = DfiTheme.typography.bodyM,
                    color = DfiTheme.colors.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Schedule chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DfiTheme.colors.accentSoft)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = activity.schedule,
                        style = DfiTheme.typography.labelM.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Trailing Join 36dp pill button morphing to Joined check
            DfiButton(
                text = if (activity.isJoined) "Joined" else "Join",
                variant = if (activity.isJoined) DfiButtonVariant.Secondary else DfiButtonVariant.Primary,
                onClick = onToggleJoin,
                leadingIcon = if (activity.isJoined) {
                    {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = DfiTheme.colors.ink,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else null,
                modifier = Modifier.heightIn(min = 36.dp)
            )
        }
    }
}

@Composable
private fun EventCardV2(
    event: Event,
    onToggleReminder: () -> Unit
) {
    DfiCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date column (day number StatL + month Caption)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DfiTheme.colors.surfaceMuted),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = event.date.takeLast(2),
                        style = DfiTheme.typography.titleM.copy(fontWeight = FontWeight.Bold),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "OCT",
                        style = DfiTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            color = DfiTheme.colors.accentDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = DfiTheme.typography.titleM,
                    color = DfiTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = DfiTheme.colors.inkTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Remind me bell button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (event.isReminderSet) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted)
                    .clickable { onToggleReminder() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (event.isReminderSet) Icons.Rounded.NotificationsActive else Icons.Rounded.Notifications,
                    contentDescription = "Remind me",
                    tint = if (event.isReminderSet) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MentorCardV2(
    mentor: Mentor,
    onRequestChat: () -> Unit
) {
    DfiCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    initials = mentor.name.take(2).uppercase(),
                    size = 48.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mentor.name,
                        style = DfiTheme.typography.titleM,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mentor.role,
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.accentDark,
                        maxLines = 1
                    )
                }

                DfiButton(
                    text = "Request chat",
                    onClick = onRequestChat,
                    variant = DfiButtonVariant.Secondary,
                    modifier = Modifier.heightIn(min = 36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Focus chips
            val mentorTags = listOf(mentor.organization, if (mentor.isAvailable) "Available" else "On call")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mentorTags.forEach { area ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DfiTheme.colors.surfaceMuted)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = area,
                            style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                        )
                    }
                }
            }
        }
    }
}
