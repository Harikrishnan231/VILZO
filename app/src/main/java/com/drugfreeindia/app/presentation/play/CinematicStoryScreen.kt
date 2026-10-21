package com.drugfreeindia.app.presentation.play

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.CinematicBeat
import com.drugfreeindia.app.domain.model.CinematicChoice
import com.drugfreeindia.app.domain.model.CinematicScene
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun CinematicStoryScreen(
    storyId: String,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onOpenHelp: (() -> Unit)? = null,
    onExplore: (() -> Unit)? = null,
    vm: CinematicPlayViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (s.isEnding) onFinish() else showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Pause story?", style = DfiTheme.typography.cardTitle) },
            text = {
                Text(
                    "Your progress is saved on this device. You can continue anytime from Story Mode.",
                    style = DfiTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    vm.stopSpeech()
                    onBack()
                }) {
                    Text("Save & Exit", color = c.primaryDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Stay", color = c.onSurfaceVariant)
                }
            },
            containerColor = c.surface,
            shape = DfiTheme.shapes.card
        )
    }

    Column(Modifier.fillMaxSize().background(c.background)) {
        // Top bar with audio narration toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                if (s.isEnding) onFinish() else showExitDialog = true
            }) {
                Icon(Icons.Rounded.Close, contentDescription = "Exit story", tint = c.onSurface)
            }

            Text(
                s.story?.title ?: "Story Mode",
                style = DfiTheme.typography.label,
                color = c.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(DfiTheme.shapes.pill)
                        .background(if (s.isSpeaking) c.primarySoft else c.surface)
                        .border(1.dp, if (s.isSpeaking) c.primary else c.outline, DfiTheme.shapes.pill)
                        .clickable { vm.toggleSpeech() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        if (s.isSpeaking) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                        contentDescription = if (s.isSpeaking) "Mute audio" else "Audio mode",
                        tint = if (s.isSpeaking) c.primaryDark else c.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (s.isSpeaking) "Listening..." else "Audio mode",
                        style = DfiTheme.typography.caption,
                        color = if (s.isSpeaking) c.primaryDark else c.onSurfaceVariant
                    )
                }
            }
        }

        if (s.loading || s.currentScene == null) {
            LoadingState()
            return@Column
        }

        val scene = s.currentScene ?: return@Column

        AnimatedContent(
            targetState = scene,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "scene_transition",
            modifier = Modifier.weight(1f)
        ) { targetScene ->
            if (targetScene.isEnding) {
                EndingContent(
                    scene = targetScene,
                    routeNotes = s.routeNotes,
                    onRestart = { vm.restartStory() },
                    onFinish = onFinish,
                    onOpenHelp = onOpenHelp,
                    onExplore = onExplore
                )
            } else {
                SceneContent(
                    scene = targetScene,
                    beats = s.activeBeats,
                    isSelecting = s.isSelecting,
                    onSelectChoice = { choice -> vm.selectChoice(choice) }
                )
            }
        }
    }
}

@Composable
private fun SceneContent(
    scene: CinematicScene,
    beats: List<CinematicBeat>,
    isSelecting: Boolean,
    onSelectChoice: (CinematicChoice) -> Unit
) {
    val c = DfiTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        // 16:9 Illustration
        AppImage(
            key = scene.imageKey,
            contentDescription = "Scene illustration for ${scene.title}",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(DfiTheme.shapes.hero)
        )

        Spacer(Modifier.height(14.dp))

        // Chapter tag & scene title
        scene.chapter?.let { chapterText ->
            Tag(chapterText, color = c.primaryDark, bg = c.primarySoft)
            Spacer(Modifier.height(6.dp))
        }

        Text(
            scene.title,
            style = DfiTheme.typography.screenTitle,
            color = c.onSurface
        )

        Spacer(Modifier.height(16.dp))

        // Story Beats
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            beats.forEach { beat ->
                BeatItem(beat)
            }
        }

        Spacer(Modifier.height(28.dp))

        // Exactly 2 choices
        Text(
            "What should Arjun do?",
            style = DfiTheme.typography.label,
            color = c.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        scene.choices.forEachIndexed { index, choice ->
            ChoiceCard(
                choice = choice,
                index = index,
                enabled = !isSelecting,
                onClick = { onSelectChoice(choice) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun BeatItem(beat: CinematicBeat) {
    val c = DfiTheme.colors
    when (beat.type) {
        "dialogue" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface)
                    .border(1.dp, c.outline, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Rounded.FormatQuote,
                    null,
                    tint = c.primaryDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    beat.speaker?.let { speaker ->
                        Text(
                            speaker,
                            style = DfiTheme.typography.labelSmall,
                            color = c.primaryDark
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                    Text(
                        beat.text,
                        style = DfiTheme.typography.body,
                        color = c.onSurface
                    )
                }
            }
        }
        "chat" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.primarySoft.copy(alpha = 0.6f))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Chat,
                    null,
                    tint = c.primaryDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    beat.from?.let { from ->
                        Text(
                            from,
                            style = DfiTheme.typography.caption,
                            color = c.primaryDark
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                    Text(
                        beat.text,
                        style = DfiTheme.typography.bodySmall,
                        color = c.onSurface
                    )
                }
            }
        }
        "thought" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.surfaceMuted)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Psychology,
                    null,
                    tint = c.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    beat.text,
                    style = DfiTheme.typography.bodySmall,
                    color = c.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        else -> {
            // Narration
            Text(
                beat.text,
                style = DfiTheme.typography.body,
                color = c.onSurface
            )
        }
    }
}

@Composable
private fun ChoiceCard(
    choice: CinematicChoice,
    index: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val c = DfiTheme.colors
    val prefix = if (index == 0) "A" else "B"

    DfiCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        padding = PaddingValues(16.dp),
        color = c.surface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(DfiTheme.shapes.tile)
                    .background(c.primarySoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    prefix,
                    style = DfiTheme.typography.label,
                    color = c.primaryDark
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                choice.text,
                style = DfiTheme.typography.body,
                color = if (enabled) c.onSurface else c.disabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EndingContent(
    scene: CinematicScene,
    routeNotes: List<String>,
    onRestart: () -> Unit,
    onFinish: () -> Unit,
    onOpenHelp: (() -> Unit)?,
    onExplore: (() -> Unit)? = null
) {
    val c = DfiTheme.colors
    val isSupportedEnding = scene.endingType == "supported"

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        AppImage(
            key = scene.imageKey,
            contentDescription = "Ending illustration for ${scene.title}",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(DfiTheme.shapes.hero)
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSupportedEnding) {
                Tag("Story completed", color = c.success, bg = c.primarySoft)
            } else {
                Tag(scene.label ?: "Unresolved ending", color = c.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            scene.title,
            style = DfiTheme.typography.screenTitle,
            color = c.onSurface
        )

        Spacer(Modifier.height(16.dp))

        // Narrative
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            scene.beats.forEach { beat ->
                Text(
                    beat.text,
                    style = DfiTheme.typography.body,
                    color = c.onSurface
                )
            }
        }

        // Reflection Card
        scene.reflection?.let { reflectionText ->
            Spacer(Modifier.height(20.dp))
            DfiCard(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(16.dp),
                color = c.primarySoft
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Rounded.Lightbulb,
                        null,
                        tint = c.primaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Key Takeaway",
                            style = DfiTheme.typography.labelSmall,
                            color = c.primaryDark
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            reflectionText,
                            style = DfiTheme.typography.bodySmall,
                            color = c.onSurface
                        )
                    }
                }
            }
        }

        // Actions
        Spacer(Modifier.height(24.dp))
        if (isSupportedEnding) {
            PrimaryButton(
                text = "Replay Story",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.Replay
            )
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                text = "Explore Other Choices",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            )
            if (onExplore != null) {
                Spacer(Modifier.height(10.dp))
                SecondaryButton(
                    text = "See Activities in Explore",
                    onClick = onExplore,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            PrimaryButton(
                text = "Try Another Path",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.Replay
            )
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                text = "Replay Story",
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(14.dp))
        SecondaryButton(
            text = "Back to Stories",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )

        // Safety disclaimer
        Spacer(Modifier.height(20.dp))
        Text(
            "This story is fictional and educational. It does not provide medical or clinical assessment. If you or someone you know is experiencing substance-use difficulties, open Get Help to find verified helplines and centres.",
            style = DfiTheme.typography.caption,
            color = c.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
    }
}
