package com.drugfreeindia.app.presentation.play

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.data.repository.GameMode
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.ErrorState
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.ScreenTitle
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.SwitchRow
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.theme.DfiMotion
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.Format

// ============================================================== Play landing

@Composable
fun PlayScreen(
    contentPadding: PaddingValues,
    onOpenStory: (String) -> Unit,
    onExploreStories: () -> Unit,
    vm: PlayViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    LaunchedEffect(Unit) { vm.refresh() }

    LazyColumn(Modifier.fillMaxSize().background(c.background), contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 24.dp)) {
        item {
            Column(Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    ScreenTitle("Play & Practise", "Build your confidence for real-life moments.", Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val stats = s.stats
                    if (s.signedIn && stats != null) {
                        val lvl = s.config.levelFor(stats.totalPoints)
                        Tag("${Format.number(stats.totalPoints)} points", color = c.primaryDark, bg = c.primarySoft)
                        Tag("Level ${lvl.level} · ${lvl.name}", color = c.onSurface)
                    } else {
                        Tag("Practice on this device", color = c.onSurfaceVariant)
                        if (!s.signedIn) Tag("Sign in to earn points", color = c.onSurfaceVariant)
                    }
                    if (s.pending > 0) Tag("${s.pending} answers waiting to sync", color = c.warning)
                }
            }
        }
        // Featured entry into the single-player game
        val featured = s.stories.firstOrNull { !it.completed } ?: s.stories.firstOrNull()
        if (featured != null) item {
            DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), onClick = { onOpenStory(featured.story.id) }, color = c.primarySoft,
                padding = PaddingValues(0.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f).padding(18.dp)) {
                        Tag(if (featured.completed) "Play again" else "Featured scenario", color = c.primaryDark, bg = c.surface)
                        Spacer(Modifier.height(10.dp))
                        Text(featured.story.title, style = DfiTheme.typography.heroTitle, color = c.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text(featured.story.description.orEmpty(), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(14.dp))
                        PrimaryButton("Start", { onOpenStory(featured.story.id) }, icon = Icons.Rounded.PlayArrow)
                    }
                    AppImage("hero_challenge", null, Modifier.width(120.dp).height(200.dp))
                }
            }
        }
        item { SectionHeader("Scenario Games", Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)) }
        if (s.loading) item { LoadingState() }
        items(s.stories, key = { it.story.id }) { row -> StoryListRow(row) { onOpenStory(row.story.id) } }

        // Story Mode Section (Fictional Life Journeys)
        item { SectionHeader("Story Mode", Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)) }
        item {
            DfiCard(
                Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                onClick = onExploreStories,
                color = c.surface,
                padding = PaddingValues(0.dp)
            ) {
                Column {
                    AppImage(
                        "story_tcnn_cover",
                        "Story Mode: A young Indian college student standing on a campus pathway with a subtle transition from moody to hopeful light",
                        Modifier.fillMaxWidth().height(160.dp).clip(DfiTheme.shapes.hero)
                    )
                    Column(Modifier.padding(18.dp)) {
                        Tag("Fictional Life Journeys", color = c.primaryDark, bg = c.primarySoft)
                        Spacer(Modifier.height(8.dp))
                        Text("Story Mode", style = DfiTheme.typography.heroTitle, color = c.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text("Every choice changes what happens next.", style = DfiTheme.typography.labelSmall, color = c.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Step into fictional lives, experience the consequences of difficult decisions, and discover how support can change a person's journey.",
                            style = DfiTheme.typography.bodySmall,
                            color = c.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(
                            "Explore Stories",
                            onClick = onExploreStories,
                            icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight
                        )
                    }
                }
            }
        }

        // Achievements strip
        if (s.badges.isNotEmpty()) {
            item { SectionHeader("Achievements", Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    s.badges.forEach { b -> BadgeChip(b.name, earned = b.id in s.earned, locked = b.id == "team_player") }
                }
                if (!s.signedIn) Text("Badges are awarded to signed-in players.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun StoryListRow(row: StoryRow, onClick: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(Modifier.padding(horizontal = 20.dp, vertical = 6.dp).fillMaxWidth(), onClick = onClick, padding = PaddingValues(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppImage(row.story.coverImageKey, null, Modifier.size(width = 96.dp, height = 72.dp).clip(DfiTheme.shapes.tile))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(row.story.title, style = DfiTheme.typography.cardTitle, color = c.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, null, tint = c.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${Format.minutes(row.story.estimatedSeconds)} · ${row.story.pathLength} scenes", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                }
                if (row.completed) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = c.success, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Completed · best ${row.bestPoints ?: 0} pts", style = DfiTheme.typography.caption, color = c.success)
                    }
                }
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = c.onSurfaceVariant)
        }
    }
}

@Composable
private fun BadgeChip(name: String, earned: Boolean, locked: Boolean) {
    val c = DfiTheme.colors
    Column(Modifier.width(84.dp).semantics(mergeDescendants = true) {
        contentDescription = "$name, " + when { earned -> "earned"; locked -> "coming with multiplayer"; else -> "not earned yet" }
    }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(if (earned) c.primarySoft else c.surfaceMuted), contentAlignment = Alignment.Center) {
            Icon(if (locked) Icons.Rounded.Lock else if (earned) Icons.Rounded.EmojiEvents else Icons.Rounded.Star, null,
                tint = if (earned) c.primaryDark else c.disabled, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(name, style = DfiTheme.typography.caption, color = if (earned) c.onSurface else c.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2)
    }
}

@Composable
private fun LockedGame(title: String, modifier: Modifier, onClick: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(modifier.heightIn(min = 120.dp), onClick = onClick, color = c.surfaceMuted) {
        Icon(Icons.Rounded.Lock, null, tint = c.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
        Text(title, style = DfiTheme.typography.labelSmall, color = c.onSurface, maxLines = 2)
        Spacer(Modifier.height(6.dp))
        Tag("Coming soon", bg = c.surface)
    }
}

// ============================================================== Gameplay

@Composable
fun GameScreen(onClose: () -> Unit, vm: GameViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    var confirmExit by remember { mutableStateOf(false) }
    val inProgress = s.sceneId != null && s.result == null
    BackHandler(inProgress) { confirmExit = true }

    Box(Modifier.fillMaxSize().background(c.background)) {
        when {
            s.loading -> LoadingState(Modifier.align(Alignment.Center))
            s.story == null -> Column(Modifier.statusBarsPadding().padding(20.dp)) { ErrorState(s.error ?: "Not found"); SecondaryButton("Back", onClose) }
            s.result != null -> ResultView(s, onReplay = vm::replay, onBack = onClose)
            s.sceneId != null -> SceneView(s, vm, onClose = { confirmExit = true })
            else -> StartPreview(s, onClose)
        }
    }

    if (s.showStartSheet && s.story != null && s.sceneId == null) {
        val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = onClose, sheetState = sheet, containerColor = c.surface, shape = DfiTheme.shapes.sheet) {
            Column(Modifier.padding(horizontal = 20.dp).navigationBarsPadding().padding(bottom = 16.dp)) {
                Text(s.story!!.title, style = DfiTheme.typography.sectionTitle, color = c.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(s.story!!.description.orEmpty(), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                SwitchRow("Relaxed mode", "No timer. Take as long as you like.", s.relaxed, vm::setRelaxed)
                Spacer(Modifier.height(8.dp))
                InfoNote("There are no wrong people here — only choices to practise. Nothing you pick is shared with anyone.")
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Start scenario", { vm.start() }, Modifier.fillMaxWidth(), loading = s.starting)
            }
        }
    }
    if (confirmExit) {
        com.drugfreeindia.app.presentation.components.ConfirmDialog(
            title = "Leave this scenario?",
            body = if (s.mode == GameMode.REMOTE) "Your progress is saved. You can continue later from the same scene." else "Progress in this play-through will be lost.",
            confirm = "Leave", onConfirm = { confirmExit = false; onClose() }, onDismiss = { confirmExit = false })
    }
}

@Composable
private fun StartPreview(s: GameUiState, onClose: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
        IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, "Close", tint = DfiTheme.colors.onSurface) }
        AppImage(s.story?.coverImageKey, null, Modifier.fillMaxWidth().height(220.dp).clip(DfiTheme.shapes.hero))
    }
}

@Composable
private fun SceneView(s: GameUiState, vm: GameViewModel, onClose: () -> Unit) {
    val c = DfiTheme.colors
    val scene = s.scene ?: return
    val screenH = LocalConfiguration.current.screenHeightDp
    val reduce = com.drugfreeindia.app.presentation.theme.LocalReduceMotion.current
    Column(Modifier.fillMaxSize()) {
        // Top bar inside the safe area: close, "Scene x of y", points chip, segmented progress.
        Column(Modifier.statusBarsPadding().padding(horizontal = 12.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, "Close scenario", tint = c.onSurface) }
                Text("Scene ${(s.sceneIndex + 1).coerceAtMost(s.totalScenes)} of ${s.totalScenes}", style = DfiTheme.typography.labelSmall,
                    color = c.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Tag("${s.points} pts", color = c.primaryDark, bg = c.primarySoft, modifier = Modifier.padding(end = 8.dp))
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(s.totalScenes) { i ->
                    Box(Modifier.weight(1f).height(4.dp).clip(DfiTheme.shapes.pill).background(if (i <= s.sceneIndex) c.primary else c.outline))
                }
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(14.dp))
            AnimatedContent(scene.id, transitionSpec = { fadeIn(DfiMotion.spec(reduce = reduce)) togetherWith fadeOut(DfiMotion.spec(reduce = reduce)) }, label = "scene") { _ ->
                AppImage(scene.imageKey, com.drugfreeindia.app.presentation.components.ImageAssets.altText[scene.imageKey],
                    Modifier.fillMaxWidth().height((screenH * 0.28f).dp).clip(DfiTheme.shapes.hero))
            }
            s.notice?.let { Spacer(Modifier.height(10.dp)); InfoNote(it) }
            scene.narration?.let {
                Spacer(Modifier.height(14.dp))
                Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            }
            if (scene.quote != null) {
                Spacer(Modifier.height(12.dp))
                DialogueCard(scene.speaker, scene.quote)
            }
            if (scene.isEnding) {
                Spacer(Modifier.height(16.dp))
                scene.takeaway?.let { t ->
                    DfiCard(color = c.primarySoft) {
                        Text("Takeaway", style = DfiTheme.typography.caption, color = c.primaryDark)
                        Spacer(Modifier.height(4.dp))
                        Text(t, style = DfiTheme.typography.body, color = c.onSurface)
                    }
                }
                Spacer(Modifier.height(16.dp))
                PrimaryButton("See results", { vm.complete() }, Modifier.fillMaxWidth(), loading = s.completing)
                Spacer(Modifier.height(24.dp))
                return@Column
            }
            Spacer(Modifier.height(16.dp))
            PressureAndTimer(scene.pressure ?: 0f, s.timerLeft, s.timerTotal, s.timerExpired)
            Spacer(Modifier.height(16.dp))
            Text("What will you do?", style = DfiTheme.typography.sectionTitle, color = c.onSurface, modifier = Modifier.semantics { heading() })
            Spacer(Modifier.height(10.dp))
            scene.choices.forEach { ch ->
                val selected = s.selected == ch.id
                val locked = s.feedback != null
                val dim = locked && s.feedback?.choice?.id != ch.id
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp).heightIn(min = 64.dp).clip(DfiTheme.shapes.card)
                        .background(if (selected) c.primarySoft.copy(alpha = 0.5f) else c.surface)
                        .border(if (selected) 2.dp else 1.dp, if (selected) c.primary else c.outline, DfiTheme.shapes.card)
                        .selectable(selected, enabled = !locked, role = Role.RadioButton) { vm.select(ch.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(ch.text, style = DfiTheme.typography.cardTitle, color = if (dim) c.onSurfaceVariant else c.onSurface)
                        ch.subtitle?.let { Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) }
                    }
                    Icon(if (selected) Icons.Rounded.CheckCircle else Icons.AutoMirrored.Rounded.KeyboardArrowRight, null,
                        tint = if (selected) c.primary else c.onSurfaceVariant)
                }
            }
            AnimatedVisibility(s.feedback != null) {
                s.feedback?.let { f ->
                    Column(Modifier.padding(top = 10.dp)) {
                        DfiCard(Modifier.semantics { liveRegion = LiveRegionMode.Polite }, color = c.surface) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Why this helps", style = DfiTheme.typography.labelSmall, color = c.primaryDark, modifier = Modifier.weight(1f))
                                Tag(when {
                                    f.duplicate -> "Already counted"
                                    f.pointsAwarded > 0 -> "+${f.pointsAwarded} pts"
                                    else -> "Practice"
                                }, color = c.primaryDark, bg = c.primarySoft)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(f.text, style = DfiTheme.typography.body, color = c.onSurface)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        if (!scene.isEnding) {
            Box(Modifier.background(c.background).padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding()) {
                if (s.feedback == null) PrimaryButton("Submit", vm::submit, Modifier.fillMaxWidth(), enabled = s.selected != null, loading = s.submitting)
                else PrimaryButton("Continue", vm::next, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun DialogueCard(speaker: String?, quote: String) {
    val c = DfiTheme.colors
    DfiCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(c.primarySoft), contentAlignment = Alignment.Center) {
                Text(speaker?.firstOrNull()?.uppercase() ?: "?", style = DfiTheme.typography.labelSmall, color = c.primaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                speaker?.let { Text(it, style = DfiTheme.typography.caption, color = c.onSurfaceVariant) }
                Text("“$quote”", style = DfiTheme.typography.dialogue, color = c.onSurface)
            }
        }
    }
}

@Composable
private fun PressureAndTimer(pressure: Float, left: Int, total: Int, expired: Boolean) {
    val c = DfiTheme.colors
    val p by animateFloatAsState(pressure.coerceIn(0f, 1f), DfiMotion.tweenOrSnap(500), label = "pressure")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).semantics(mergeDescendants = true) { contentDescription = "Peer pressure level ${(pressure * 100).toInt()} percent" }) {
            Text("Peer pressure", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth().height(8.dp).clip(DfiTheme.shapes.pill).background(c.surfaceMuted)) {
                Box(Modifier.fillMaxWidth(p).height(8.dp).clip(DfiTheme.shapes.pill)
                    .background(Brush.horizontalGradient(listOf(c.primary, c.warning))))
            }
        }
        if (total > 0) {
            Spacer(Modifier.width(16.dp))
            Box(Modifier.size(52.dp).semantics { contentDescription = if (expired) "Take your time" else "$left seconds to think" }, contentAlignment = Alignment.Center) {
                val track = c.surfaceMuted; val col = c.primary
                Canvas(Modifier.fillMaxSize()) {
                    val st = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(track, 0f, 360f, false, style = st, topLeft = Offset(st.width / 2, st.width / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - st.width, size.height - st.width))
                    drawArc(col, -90f, 360f * (left / total.toFloat()), false, style = st, topLeft = Offset(st.width / 2, st.width / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - st.width, size.height - st.width))
                }
                Text(if (expired) "∞" else "$left", style = DfiTheme.typography.labelSmall, color = c.onSurface)
            }
        }
    }
    if (expired) {
        Spacer(Modifier.height(6.dp))
        Text("Take your time. There's no rush.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
    }
}

// ============================================================== Result

@Composable
private fun ResultView(s: GameUiState, onReplay: () -> Unit, onBack: () -> Unit) {
    val c = DfiTheme.colors
    val r = s.result ?: return
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(12.dp))
        Medallion(r.points)
        Spacer(Modifier.height(16.dp))
        Text("Scenario complete", style = DfiTheme.typography.screenTitle, color = c.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(s.story?.title.orEmpty(), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Tag(when {
            r.scored && r.alreadyCompleted -> "Already counted earlier"
            r.scored -> "Saved to your progress"
            r.queued -> "Saved on this device; will sync"
            else -> "Practice play — not scored"
        }, color = if (r.scored) c.success else c.onSurfaceVariant)
        r.error?.let { Spacer(Modifier.height(10.dp)); InfoNote(it) }
        Spacer(Modifier.height(20.dp))
        if (r.newBadges.isNotEmpty()) {
            DfiCard(Modifier.fillMaxWidth()) {
                Text("New badge${if (r.newBadges.size > 1) "s" else ""}", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                r.newBadges.forEach { b ->
                    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.EmojiEvents, null, tint = c.primary)
                        Spacer(Modifier.width(10.dp))
                        Column { Text(b.name, style = DfiTheme.typography.cardTitle, color = c.onSurface); b.description?.let { Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) } }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        if (r.skills.isNotEmpty()) {
            DfiCard(Modifier.fillMaxWidth()) {
                Text("Skills you practised", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                r.skills.forEach { sk ->
                    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = c.success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(sk, style = DfiTheme.typography.body, color = c.onSurface)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        r.takeaway?.let {
            DfiCard(Modifier.fillMaxWidth(), color = c.primarySoft) {
                Text("One thing to remember", style = DfiTheme.typography.caption, color = c.primaryDark)
                Spacer(Modifier.height(4.dp))
                Text(it, style = DfiTheme.typography.body, color = c.onSurface)
            }
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Back to Play", onBack, Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Replay", onReplay, Modifier.fillMaxWidth(), icon = Icons.Rounded.Replay)
    }
}

@Composable
private fun Medallion(points: Int) {
    val c = DfiTheme.colors
    Box(Modifier.size(132.dp).semantics(mergeDescendants = true) { contentDescription = "$points points earned" }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(c.primarySoft)
            drawCircle(c.primary, radius = size.minDimension * 0.40f)
            drawCircle(c.surface.copy(alpha = 0.25f), radius = size.minDimension * 0.40f, style = Stroke(3.dp.toPx()))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$points", style = DfiTheme.typography.stat, color = c.onPrimary)
            Text("points", style = DfiTheme.typography.caption, color = c.onPrimary)
        }
    }
}
