package com.drugfreeindia.app.presentation.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.ContactPhone
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.presentation.components.Avatar
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiScreen
import com.drugfreeindia.app.presentation.components.DfiSwitch
import com.drugfreeindia.app.presentation.components.GroupedList
import com.drugfreeindia.app.presentation.components.GroupedListDivider
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.StatTile
import com.drugfreeindia.app.presentation.components.TintCard
import com.drugfreeindia.app.presentation.theme.DfiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToTrustedContacts: () -> Unit,
    onDataWiped: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showAllBadgesSheet by remember { mutableStateOf(false) }
    val badgesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomPadding = LocalBottomBarPadding.current

    val completedStoriesCount = state.unlockedBadgesCount.coerceAtLeast(1)
    val streakDays = 4

    DfiScreen(
        title = "Profile & settings",
        subtitle = "Manage your preferences and private progress"
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
            // 1. Profile Header Hero Card (TintCard)
            item {
                TintCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Avatar(
                                initials = state.nickname.take(2).uppercase(),
                                size = 72.dp
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = state.nickname,
                                    style = DfiTheme.typography.display.copy(fontSize = 22.sp),
                                    color = DfiTheme.colors.ink
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DfiTheme.colors.surface)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = state.levelName,
                                            style = DfiTheme.typography.caption.copy(
                                                color = DfiTheme.colors.accentDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DfiTheme.colors.surface)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = state.collegeCode,
                                            style = DfiTheme.typography.caption.copy(
                                                color = DfiTheme.colors.inkSecondary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // XP bar to next level
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DfiTheme.colors.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DfiTheme.colors.accent)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "65% to next level · Confident Leader",
                            style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.accentDark)
                        )
                    }
                }
            }

            // 2. Stats Row (3 StatTiles: Streak, Badges, Stories)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = "Streak",
                        value = "${streakDays}d",
                        delta = "Active",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Badges",
                        value = "${state.unlockedBadgesCount}",
                        trailing = "Unlocked",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Stories",
                        value = "$completedStoriesCount",
                        trailing = "Completed",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Badges: 3-column Grid
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(
                        title = "Achievements & badges",
                        actionLabel = "See all",
                        onAction = { showAllBadgesSheet = true }
                    )

                    // 3-column row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.badges.take(3).forEach { badge ->
                            val isEarned = badge.isUnlocked
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isEarned) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(110.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isEarned) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = if (isEarned) DfiTheme.colors.accent else DfiTheme.colors.inkTertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = badge.title,
                                        style = DfiTheme.typography.caption.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isEarned) DfiTheme.colors.ink else DfiTheme.colors.inkTertiary
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Settings Grouped Lists with uppercase section headers
            // PREFERENCES
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PREFERENCES",
                        style = DfiTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = DfiTheme.colors.inkTertiary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    GroupedList {
                        ListRow(
                            icon = Icons.Rounded.Language,
                            title = "Language",
                            subtitle = if (state.selectedLanguage == "en") "English" else state.selectedLanguage,
                            onClick = { showLanguagePicker = true }
                        )

                        GroupedListDivider()

                        ListRow(
                            icon = Icons.Rounded.Notifications,
                            title = "Notifications",
                            subtitle = "Daily practice reminders & community updates",
                            trailing = {
                                DfiSwitch(
                                    checked = state.isNotificationsEnabled,
                                    onCheckedChange = viewModel::onToggleNotifications
                                )
                            }
                        )

                        GroupedListDivider()

                        ListRow(
                            icon = Icons.Rounded.Animation,
                            title = "Reduce motion",
                            subtitle = "Simplify transition effects across scenarios",
                            trailing = {
                                DfiSwitch(
                                    checked = state.isReduceMotion,
                                    onCheckedChange = viewModel::onToggleReduceMotion
                                )
                            }
                        )
                    }
                }
            }

            // SUPPORT
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SUPPORT",
                        style = DfiTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = DfiTheme.colors.inkTertiary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    GroupedList {
                        ListRow(
                            icon = Icons.Rounded.ContactPhone,
                            title = "Trusted contacts",
                            subtitle = "Manage your emergency and check-in network",
                            onClick = onNavigateToTrustedContacts
                        )
                    }
                }
            }

            // PRIVACY
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PRIVACY & DATA",
                        style = DfiTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = DfiTheme.colors.inkTertiary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    GroupedList {
                        ListRow(
                            icon = Icons.Rounded.Shield,
                            title = "Privacy centre",
                            subtitle = "Private by design · No identity stored",
                            onClick = { showPrivacyDialog = true }
                        )

                        GroupedListDivider()

                        ListRow(
                            icon = Icons.Rounded.FileDownload,
                            title = "Export my data",
                            subtitle = "Download all local story records and survey responses",
                            onClick = {
                                val exportData = "{\"nickname\":\"${state.nickname}\",\"level\":\"${state.levelName}\",\"college\":\"${state.collegeCode}\"}"
                                Toast.makeText(context, "Exported ${exportData.length} bytes of private data", Toast.LENGTH_SHORT).show()
                            }
                        )

                        GroupedListDivider()

                        ListRow(
                            iconTile = {
                                IconTile(
                                    icon = Icons.Rounded.DeleteForever,
                                    tint = DfiTheme.colors.danger,
                                    backgroundColor = DfiTheme.colors.dangerTint
                                )
                            },
                            title = "Delete everything",
                            subtitle = "Instant, irreversible zeroization of local app state",
                            onClick = { showDeleteConfirmDialog = true }
                        )
                    }
                }
            }

            // ABOUT
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ABOUT",
                        style = DfiTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = DfiTheme.colors.inkTertiary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    GroupedList {
                        ListRow(
                            icon = Icons.Rounded.Info,
                            title = "About Drug-Free India",
                            subtitle = "Version 2.0 (Clinical Redesign) · MIT / Public Health",
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }
        }

        // All Badges Bottom Sheet
        if (showAllBadgesSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAllBadgesSheet = false },
                sheetState = badgesSheetState,
                containerColor = DfiTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "All scenario badges",
                        style = DfiTheme.typography.titleL,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    state.badges.forEach { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconTile(
                                icon = if (badge.isUnlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                                tint = if (badge.isUnlocked) DfiTheme.colors.accent else DfiTheme.colors.inkTertiary,
                                backgroundColor = if (badge.isUnlocked) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    style = DfiTheme.typography.titleS,
                                    color = DfiTheme.colors.ink
                                )
                                Text(
                                    text = badge.description,
                                    style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirm Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete all data?", style = DfiTheme.typography.titleM) },
                text = { Text("This will permanently wipe all local scenario progress, streak data, and settings. This cannot be undone.", style = DfiTheme.typography.bodyM) },
                confirmButton = {
                    DfiButton(
                        text = "Wipe everything",
                        variant = DfiButtonVariant.Danger,
                        onClick = {
                            viewModel.deleteAllData {
                                showDeleteConfirmDialog = false
                                onDataWiped()
                            }
                        }
                    )
                },
                dismissButton = {
                    DfiButton(
                        text = "Cancel",
                        variant = DfiButtonVariant.Ghost,
                        onClick = { showDeleteConfirmDialog = false }
                    )
                }
            )
        }

        // Privacy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text("Privacy by design", style = DfiTheme.typography.titleM) },
                text = { Text("Drug-Free India stores your progress locally on device. No personal identifiers or phone numbers are transmitted to external servers without your direct action.", style = DfiTheme.typography.bodyM) },
                confirmButton = {
                    DfiButton(text = "OK", onClick = { showPrivacyDialog = false })
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Drug-Free India v2.0", style = DfiTheme.typography.titleM) },
                text = { Text("A clinical health-tech initiative for empowering youth with refusal skills and private support networks.", style = DfiTheme.typography.bodyM) },
                confirmButton = {
                    DfiButton(text = "Close", onClick = { showAboutDialog = false })
                }
            )
        }

        // Language Picker
        if (showLanguagePicker) {
            AlertDialog(
                onDismissRequest = { showLanguagePicker = false },
                title = { Text("Select language", style = DfiTheme.typography.titleM) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Pair("en", "English"),
                            Pair("hi", "Hindi (हिंदी)"),
                            Pair("ta", "Tamil (தமிழ்)"),
                            Pair("te", "Telugu (తెలుగు)")
                        ).forEach { (code, label) ->
                            Text(
                                text = label,
                                style = DfiTheme.typography.bodyL.copy(
                                    color = if (state.selectedLanguage == code) DfiTheme.colors.accentDark else DfiTheme.colors.ink,
                                    fontWeight = if (state.selectedLanguage == code) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onLanguageSelected(code)
                                        showLanguagePicker = false
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    DfiButton(text = "Done", variant = DfiButtonVariant.Ghost, onClick = { showLanguagePicker = false })
                }
            )
        }
    }
}
