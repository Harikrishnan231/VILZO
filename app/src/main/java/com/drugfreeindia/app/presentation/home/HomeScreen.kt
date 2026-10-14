package com.drugfreeindia.app.presentation.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.ExploreRepository
import com.drugfreeindia.app.data.repository.PlayRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.MyStats
import com.drugfreeindia.app.domain.model.Profile
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.AvatarInitials
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.EmptyState
import com.drugfreeindia.app.presentation.components.FallbackArt
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.Format
import com.drugfreeindia.app.presentation.util.PRACTICE_SCORE_EMPTY
import com.drugfreeindia.app.presentation.util.PRACTICE_SCORE_LABEL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val signedIn: Boolean = false,
    val profile: Profile? = null,
    val stats: MyStats? = null,
    val localCompleted: Int = 0,
    val totalStories: Int = 0,
    val featured: Story? = null,
    val next: Activity? = null,
    val joined: Boolean = false,
    val joining: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val content: ContentRepository,
    private val play: PlayRepository,
    private val explore: ExploreRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { profiles.profile.collect { p -> _state.update { it.copy(profile = p) } } }
        viewModelScope.launch { play.localResults.collect { l -> _state.update { it.copy(localCompleted = l.size) } } }
    }

    fun refresh() = viewModelScope.launch {
        val signedIn = auth.userId != null
        if (signedIn && profiles.profile.value == null) profiles.refresh()
        val stories = content.stories().data
        val stats = play.stats()?.data
        val next = explore.activities().data.filter { Format.isUpcoming(it.startsAt) }.minByOrNull { it.startsAt.orEmpty() }
        val joined = next != null && next.id in explore.mySignupIds()
        _state.update { it.copy(signedIn = signedIn, stats = stats, totalStories = stories.size, featured = stories.firstOrNull(), next = next, joined = joined) }
    }

    fun toggleJoin() {
        val a = _state.value.next ?: return
        if (!_state.value.signedIn) { _state.update { it.copy(message = "Sign in to join activities.") }; return }
        _state.update { it.copy(joining = true) }
        viewModelScope.launch {
            val r = if (_state.value.joined) explore.leave(a.id) else explore.join(a.id)
            r.onSuccess { _state.update { it.copy(joining = false, joined = !it.joined) } }
                .onFailure { e -> _state.update { it.copy(joining = false, message = e.toAppError().message) } }
        }
    }
}

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onStartChallenge: (String) -> Unit,
    onProfile: () -> Unit,
    onExplore: () -> Unit,
    onImpact: () -> Unit,
    onActivity: (String) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    var scoreInfo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.refresh() }
    val name = Format.firstName(s.profile?.fullName)

    Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState())
        .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp)) {
        // 1. Header
        Row(Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(Format.greeting(), style = DfiTheme.typography.captionLarge, color = c.onSurfaceVariant)
                Text(name ?: "Welcome", style = DfiTheme.typography.screenTitle, color = c.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(Modifier.clip(androidx.compose.foundation.shape.CircleShape).clickable(onClickLabel = "Open profile and settings", onClick = onProfile)) {
                AvatarInitials(s.profile?.fullName)
            }
        }

        // 2. Hero
        DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), color = c.primarySoft, padding = PaddingValues(0.dp)) {
            Row {
                Column(Modifier.weight(1f).padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                    Text("Say No with Confidence", style = DfiTheme.typography.heroTitle, color = c.onSurface)
                    Spacer(Modifier.height(6.dp))
                    Text("Small choices make a big difference.", style = DfiTheme.typography.labelSmall, color = c.primaryDark)
                    Spacer(Modifier.height(6.dp))
                    Text("Practising what to say before the moment arrives makes it easier to say no when it counts.",
                        style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton("Start challenge", { s.featured?.let { onStartChallenge(it.id) } }, enabled = s.featured != null, icon = Icons.Rounded.PlayArrow)
                }
                AppImage("hero_challenge", null, Modifier.width(118.dp).height(250.dp))
            }
        }

        // 3. Progress (real data only)
        SectionHeader("Your progress", Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp))
        val stats = s.stats
        if (!s.signedIn || stats == null || stats.scenariosCompleted == 0) {
            DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                Text(if (s.localCompleted > 0) "You've practised ${s.localCompleted} scenario${if (s.localCompleted > 1) "s" else ""} on this device" else "Start your first scenario",
                    style = DfiTheme.typography.cardTitle, color = c.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(if (s.signedIn) "Your practice score, streak and badges appear here after you play." else "Sign in to keep your practice score, streak and badges across devices.",
                    style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            }
        } else {
            DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val score = stats.practiceScore
                    ProgressRing((score ?: 0) / 100f, size = 92.dp, stroke = 9.dp,
                        description = if (score != null) "Practice score $score out of 100" else PRACTICE_SCORE_EMPTY) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(score?.toString() ?: "–", style = DfiTheme.typography.stat, color = c.onSurface)
                            Text("of 100", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Practice score", style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f))
                            IconButton(onClick = { scoreInfo = true }) { Icon(Icons.Rounded.Info, "What is the practice score?", tint = c.onSurfaceVariant) }
                        }
                        Text(if (score == null) PRACTICE_SCORE_EMPTY else "Scenarios ${stats.scenariosCompleted} of ${stats.scenariosTotal}",
                            style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                        if (stats.streakDays > 0) {
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.LocalFireDepartment, null, tint = c.warning, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${stats.streakDays}-day streak", style = DfiTheme.typography.labelSmall, color = c.onSurface)
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.semantics(mergeDescendants = true) {
                                contentDescription = "Practised ${stats.streakDays.coerceAtMost(7)} of the last 7 days"
                            }) {
                                repeat(7) { i -> Box(Modifier.size(width = 18.dp, height = 6.dp).clip(DfiTheme.shapes.pill)
                                    .background(if (i >= 7 - stats.streakDays.coerceAtMost(7)) c.primary else c.surfaceMuted)) }
                            }
                        }
                    }
                }
            }
        }

        // 4. Next activity
        SectionHeader("Next activity", Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp), action = "Explore", onAction = onExplore)
        val a = s.next
        if (a == null) {
            DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                EmptyState("No upcoming activities yet.", "When organisers publish events near you, the next one shows up here.",
                    imageKey = "empty_activities", fallback = FallbackArt.HILLS, action = "Explore", onAction = onExplore)
            }
        } else {
            DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), onClick = { onActivity(a.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Format.dayBadge(a.startsAt)?.let { (d, m) ->
                        Column(Modifier.clip(DfiTheme.shapes.tile).background(c.primarySoft).padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(d, style = DfiTheme.typography.sectionTitle, color = c.primaryDark)
                            Text(m, style = DfiTheme.typography.caption, color = c.primaryDark)
                        }
                        Spacer(Modifier.width(14.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(a.title, style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f, fill = false), maxLines = 2)
                            if (a.isDemo) { Spacer(Modifier.width(6.dp)); Tag("Demo") }
                        }
                        Text(a.organiserName, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                        Text(listOfNotNull(Format.dateTime(a.startsAt), a.venue).joinToString(" · "), style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (s.joined) SecondaryButton("Joined", vm::toggleJoin, Modifier.fillMaxWidth())
                else PrimaryButton("Join", vm::toggleJoin, Modifier.fillMaxWidth(), loading = s.joining)
            }
        }
        s.message?.let { Text(it, style = DfiTheme.typography.caption, color = c.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) }

        // 5. Track Impact entry
        Spacer(Modifier.height(16.dp))
        DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), padding = PaddingValues(0.dp)) {
            ListRow("Track Impact", subtitle = "See what you're learning and how your community takes part", icon = Icons.Rounded.Insights, onClick = onImpact)
        }
    }

    if (scoreInfo) {
        ModalBottomSheet(onDismissRequest = { scoreInfo = false }, containerColor = c.surface, shape = DfiTheme.shapes.sheet) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 32.dp)) {
                Text("About the practice score", style = DfiTheme.typography.sectionTitle, color = c.onSurface)
                Spacer(Modifier.height(8.dp))
                Text(PRACTICE_SCORE_LABEL, style = DfiTheme.typography.body, color = c.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("It's worked out from the choices you make in scenarios (0–100). It isn't shared with your college, and it doesn't say anything about your health or habits.",
                    style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            }
        }
    }
}
