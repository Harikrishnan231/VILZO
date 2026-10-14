package com.drugfreeindia.app.presentation.play.story

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.R
import com.drugfreeindia.app.domain.model.Choice
import com.drugfreeindia.app.presentation.components.Avatar
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow
import com.drugfreeindia.app.presentation.util.formatPoints

@Composable
fun StoryPlayScreen(
    onStoryFinished: (String, Int, Int, String?) -> Unit,
    onClose: () -> Unit,
    viewModel: StoryPlayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val view = LocalView.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is StoryEffect.Completed -> {
                    onStoryFinished(effect.storyId, effect.totalScore, effect.delta, effect.badgeId)
                }
                is StoryEffect.TriggerHaptic -> {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
                is StoryEffect.TriggerConfetti -> {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            }
        }
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
            // 1. Top Bar (height 56dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DfiTheme.colors.surfaceMuted)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Exit",
                        tint = DfiTheme.colors.ink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Scene ${state.currentSceneIndex + 1} of ${state.totalScenes}",
                    style = DfiTheme.typography.labelM,
                    color = DfiTheme.colors.inkSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(DfiTheme.colors.accentSoft, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Score ${formatPoints(state.totalScore)}",
                        style = DfiTheme.typography.labelM.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Segmented scene progress bars (4dp tall, 6dp gaps, 20dp side padding)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val totalBars = state.totalScenes.coerceAtLeast(1)
                for (i in 0 until totalBars) {
                    val isDoneOrCurrent = i <= state.currentSceneIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isDoneOrCurrent) DfiTheme.colors.accent else DfiTheme.colors.surfaceMuted
                            )
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // 2. Scene Illustration Card (height ≈ 180dp, radius 24dp)
                val scene = state.currentScene
                SceneIllustration(
                    sceneKey = state.story?.id ?: "terrace_party",
                    speakerName = scene?.speaker ?: "Rohan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Pressure + timer row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pressure meter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pressure",
                            style = DfiTheme.typography.labelM,
                            color = DfiTheme.colors.inkSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DfiTheme.colors.accentSoft)
                        ) {
                            val isCautionTime = state.timerRemaining <= 3
                            val fillWidth = state.pressureLevel.coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fillWidth)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isCautionTime) DfiTheme.colors.caution else DfiTheme.colors.accent
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 52dp timer ring with seconds in TitleS
                    val isLowTime = state.timerRemaining <= 3
                    ProgressRing(
                        progress = state.timerRemaining / 10f,
                        size = 52.dp,
                        strokeWidth = 5.dp,
                        color = if (isLowTime) DfiTheme.colors.caution else DfiTheme.colors.accent,
                        trackColor = DfiTheme.colors.accentSoft
                    ) {
                        Text(
                            text = "${state.timerRemaining}s",
                            style = DfiTheme.typography.titleS,
                            color = if (isLowTime) DfiTheme.colors.caution else DfiTheme.colors.ink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Dialogue block
                if (scene != null) {
                    val rawLines = scene.lines
                    val narration = rawLines.firstOrNull { !it.contains("\"") && !it.contains("“") }
                    val speechLines = rawLines.filter { it != narration }
                    val dialogueText = if (speechLines.isNotEmpty()) speechLines.joinToString(" ") else (narration ?: "")

                    if (!narration.isNullOrBlank() && speechLines.isNotEmpty()) {
                        Text(
                            text = narration,
                            style = DfiTheme.typography.caption.copy(
                                fontStyle = FontStyle.Italic,
                                color = DfiTheme.colors.inkSecondary
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    // Speech bubble card
                    val bubbleShape = RoundedCornerShape(20.dp)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .softShadow(bubbleShape, elevation = 8.dp),
                        shape = bubbleShape,
                        color = DfiTheme.colors.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Avatar(
                                initials = scene.speaker.take(1).uppercase(),
                                size = 40.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = scene.speaker,
                                    style = DfiTheme.typography.titleS,
                                    color = DfiTheme.colors.ink
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dialogueText,
                                    style = DfiTheme.typography.bodyL.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    ),
                                    color = DfiTheme.colors.ink
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5. Question
                    Text(
                        text = stringResource(R.string.play_scene_question),
                        style = DfiTheme.typography.titleM,
                        color = DfiTheme.colors.ink
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Choice Cards (anchored with letter badges, 64dp min height)
                    val letters = listOf("A", "B", "C", "D")
                    scene.choices.forEachIndexed { index, choice ->
                        val isSelected = state.selectedChoiceId == choice.id
                        val letter = letters.getOrElse(index) { "${index + 1}" }

                        ChoiceCardV2(
                            choice = choice,
                            letter = letter,
                            isSelected = isSelected,
                            isAnswerRevealed = state.isAnswerRevealed,
                            onSelect = { viewModel.onSelectChoice(choice) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Continue button
                    AnimatedVisibility(visible = state.isAnswerRevealed) {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)) {
                            DfiButton(
                                text = "Continue",
                                onClick = { viewModel.onContinueToNext() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceCardV2(
    choice: Choice,
    letter: String,
    isSelected: Boolean,
    isAnswerRevealed: Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isAnswerRevealed) 0.98f else 1f,
        animationSpec = DfiTheme.motion.defaultSpring,
        label = "choiceScale"
    )

    val cardAlpha = if (isAnswerRevealed && !isSelected) 0.4f else 1f
    val isPositiveFeedback = choice.points >= 8

    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(cardAlpha)
            .softShadow(shape, elevation = if (isSelected) 10.dp else 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.1f)),
                enabled = !isAnswerRevealed,
                onClick = onSelect
            ),
        shape = shape,
        color = DfiTheme.colors.surface,
        border = if (isSelected) BorderStroke(1.5.dp, DfiTheme.colors.accent) else BorderStroke(1.dp, DfiTheme.colors.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle letter badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.accentSoft
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter,
                        style = DfiTheme.typography.labelL.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else DfiTheme.colors.accentDark
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = choice.text,
                    style = DfiTheme.typography.bodyL,
                    color = DfiTheme.colors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Expanded feedback when answer is revealed
            if (isSelected && isAnswerRevealed && !choice.feedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPositiveFeedback) DfiTheme.colors.accentWash else DfiTheme.colors.surfaceMuted
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (isPositiveFeedback) Icons.Rounded.CheckCircle else Icons.Rounded.Info,
                        contentDescription = null,
                        tint = if (isPositiveFeedback) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = choice.feedback,
                        style = DfiTheme.typography.bodyM,
                        color = DfiTheme.colors.ink
                    )
                }
            }
        }
    }
}
