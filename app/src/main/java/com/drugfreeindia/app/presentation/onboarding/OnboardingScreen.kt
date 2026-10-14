package com.drugfreeindia.app.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Morphing Pill Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0..3).forEach { index ->
                    val isSelected = state.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 32.dp else 8.dp,
                        animationSpec = DfiTheme.motion.dpSpring,
                        label = "pillIndicator"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.outline
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Page Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (state.currentPage) {
                    0 -> OnboardingIllustrationPage(
                        icon = Icons.Rounded.Diversity3,
                        title = stringResource(R.string.onboarding_title_1),
                        description = stringResource(R.string.onboarding_desc_1),
                        illustrationTint = DfiTheme.colors.surfaceTint
                    )
                    1 -> OnboardingIllustrationPage(
                        icon = Icons.Rounded.SportsEsports,
                        title = stringResource(R.string.onboarding_title_2),
                        description = stringResource(R.string.onboarding_desc_2),
                        illustrationTint = DfiTheme.colors.surfaceTint
                    )
                    2 -> OnboardingIllustrationPage(
                        icon = Icons.Rounded.Security,
                        title = stringResource(R.string.onboarding_title_3),
                        description = stringResource(R.string.onboarding_desc_3),
                        illustrationTint = DfiTheme.colors.surfaceTint
                    )
                    3 -> OnboardingSetupPage(
                        state = state,
                        onLanguageSelected = viewModel::onLanguageSelected,
                        onNicknameSelected = viewModel::onNicknameSelected,
                        onCollegeCodeChanged = viewModel::onCollegeCodeChanged
                    )
                }
            }

            // Bottom CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (state.currentPage < 3) {
                    DfiButton(
                        text = stringResource(R.string.onboarding_btn_continue),
                        onClick = { viewModel.onNextPage() },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    DfiButton(
                        text = stringResource(R.string.onboarding_btn_get_started),
                        onClick = { viewModel.completeOnboarding(onFinish) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingIllustrationPage(
    icon: ImageVector,
    title: String,
    description: String,
    illustrationTint: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(illustrationTint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(170.dp)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = size.minDimension / 2.2f,
                    center = center
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DfiTheme.colors.accent,
                modifier = Modifier.size(76.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = title,
            style = DfiTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = DfiTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = description,
            style = DfiTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = DfiTheme.colors.inkSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingSetupPage(
    state: OnboardingUiState,
    onLanguageSelected: (String) -> Unit,
    onNicknameSelected: (String) -> Unit,
    onCollegeCodeChanged: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "hi" to "हिंदी",
        "ta" to "தமிழ்",
        "te" to "తెలుగు",
        "kn" to "ಕನ್ನಡ",
        "ml" to "മലയാളം",
        "bn" to "বাংলা",
        "mr" to "मराठी"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.setup_title),
            style = DfiTheme.typography.titleLarge,
            color = DfiTheme.colors.ink
        )
        Text(
            text = "Zero personal info required. You stay completely anonymous.",
            style = DfiTheme.typography.bodyMedium,
            color = DfiTheme.colors.inkSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.setup_language_label),
            style = DfiTheme.typography.titleSmall,
            color = DfiTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { (code, name) ->
                val isSelected = state.selectedLanguage == code
                Surface(
                    shape = DfiTheme.shapes.chip,
                    color = if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.surfaceMuted,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DfiTheme.colors.outline),
                    modifier = Modifier.clickable { onLanguageSelected(code) }
                ) {
                    Text(
                        text = name,
                        style = DfiTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else DfiTheme.colors.ink
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setup_nickname_label),
            style = DfiTheme.typography.titleSmall,
            color = DfiTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.suggestedNicknames.forEach { name ->
                val isSelected = state.nickname == name
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) DfiTheme.colors.surfaceTint else DfiTheme.colors.surfaceMuted,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.outline
                    ),
                    modifier = Modifier.clickable { onNicknameSelected(name) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = DfiTheme.colors.accentDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = name,
                            style = DfiTheme.typography.label.copy(
                                color = if (isSelected) DfiTheme.colors.accentDark else DfiTheme.colors.ink,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setup_college_label),
            style = DfiTheme.typography.titleSmall,
            color = DfiTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.collegeCode,
            onValueChange = onCollegeCodeChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.setup_college_hint),
                    style = DfiTheme.typography.bodyMedium,
                    color = DfiTheme.colors.inkTertiary
                )
            },
            shape = DfiTheme.shapes.input,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DfiTheme.colors.surfaceMuted,
                unfocusedContainerColor = DfiTheme.colors.surfaceMuted,
                focusedBorderColor = DfiTheme.colors.accent,
                unfocusedBorderColor = DfiTheme.colors.outline
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
