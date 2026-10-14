package com.drugfreeindia.app.presentation.help

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ContactPhone
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.PhoneInTalk
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiScreen
import com.drugfreeindia.app.presentation.components.GroupedList
import com.drugfreeindia.app.presentation.components.GroupedListDivider
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.components.QuickExitChip
import com.drugfreeindia.app.presentation.components.TintCard
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun HelpScreen(
    onNavigateToContacts: () -> Unit,
    onNavigateToCentres: () -> Unit,
    onNavigateToHelplines: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToHelpFriend: () -> Unit,
    onQuickExit: () -> Unit,
    viewModel: HelpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val bottomPadding = LocalBottomBarPadding.current

    DfiScreen(
        title = "Get help",
        subtitle = "Private and free. Choose what you need.",
        actions = {
            QuickExitChip(onQuickExit = onQuickExit)
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
            // 1. Calm Hero Card (TintCard)
            item {
                TintCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Soft background drawn illustration (hands and leaf motif)
                    Canvas(
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        val centerOffset = Offset(size.width * 0.85f, size.height * 0.85f)
                        drawCircle(
                            color = Color(0xFFDDF2EE).copy(alpha = 0.6f),
                            radius = 65.dp.toPx(),
                            center = centerOffset
                        )
                        drawCircle(
                            color = Color(0xFF0E9F8E).copy(alpha = 0.15f),
                            radius = 95.dp.toPx(),
                            center = centerOffset,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Spa,
                                contentDescription = null,
                                tint = DfiTheme.colors.accentDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confidential support",
                                style = DfiTheme.typography.labelM.copy(
                                    color = DfiTheme.colors.accentDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "You are not alone",
                            style = DfiTheme.typography.titleL,
                            color = DfiTheme.colors.ink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reach out in complete privacy. Trained counsellors, verified centres, and trusted peers are here for you.",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DfiButton(
                            text = "Talk to someone",
                            onClick = onNavigateToContacts,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Call,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.height(48.dp)
                        )
                    }
                }
            }

            // 2. Grouped List (76dp min height rows)
            item {
                GroupedList {
                    // Talk to someone
                    ListRow(
                        icon = Icons.Rounded.ContactPhone,
                        title = "Talk to someone",
                        subtitle = if (state.trustedContacts.isNotEmpty()) "${state.trustedContacts.size} trusted contacts available" else "Connect with your chosen support circle",
                        onClick = onNavigateToContacts
                    )

                    GroupedListDivider()

                    // Find rehabilitation centres
                    ListRow(
                        icon = Icons.Rounded.LocalHospital,
                        title = "Find rehabilitation centres",
                        subtitle = "Government & certified counselling clinics",
                        onClick = onNavigateToCentres
                    )

                    GroupedListDivider()

                    // National helplines (chip "24x7 toll-free")
                    ListRow(
                        icon = Icons.Rounded.PhoneInTalk,
                        title = "National helplines",
                        subtitle = "Toll-free, anonymous assistance across India",
                        trailing = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DfiTheme.colors.accentSoft)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "24×7 toll-free",
                                    style = DfiTheme.typography.caption.copy(
                                        color = DfiTheme.colors.accentDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        },
                        onClick = onNavigateToHelplines
                    )

                    GroupedListDivider()

                    // Emergency support (112) - icon tinted DangerTint with Danger icon
                    ListRow(
                        iconTile = {
                            IconTile(
                                icon = Icons.Rounded.MedicalServices,
                                tint = DfiTheme.colors.danger,
                                backgroundColor = DfiTheme.colors.dangerTint
                            )
                        },
                        title = "Emergency support (112)",
                        subtitle = "Immediate medical or crisis emergency response",
                        onClick = onNavigateToEmergency
                    )

                    GroupedListDivider()

                    // Help a friend
                    ListRow(
                        icon = Icons.Rounded.VolunteerActivism,
                        title = "Help a friend",
                        subtitle = "Non-judgmental guide on speaking with peers",
                        onClick = onNavigateToHelpFriend
                    )
                }
            }

            // 3. Privacy Note Card
            item {
                DfiCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(
                            icon = Icons.Rounded.Shield,
                            size = 36.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Your information stays on your device unless you choose to share it.",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary
                        )
                    }
                }
            }
        }
    }
}
