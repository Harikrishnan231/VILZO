package com.drugfreeindia.app.presentation.impact

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.ImpactRepository
import com.drugfreeindia.app.data.repository.PlayRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.domain.model.CommunityImpact
import com.drugfreeindia.app.domain.model.LeaderboardRow
import com.drugfreeindia.app.domain.model.MyStats
import com.drugfreeindia.app.domain.model.ProgressPoint
import com.drugfreeindia.app.domain.model.SelfAssessment
import com.drugfreeindia.app.domain.model.Survey
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.EmptyState
import com.drugfreeindia.app.presentation.components.ErrorState
import com.drugfreeindia.app.presentation.components.FallbackArt
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.ProgressRing
import com.drugfreeindia.app.presentation.components.ScreenTitle
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.components.TrendChart
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

data class ImpactUiState(
    val signedIn: Boolean = false,
    val tab: Int = 0,
    val loading: Boolean = true,
    val stats: MyStats? = null,
    val series: List<ProgressPoint> = emptyList(),
    val survey: Survey? = null,
    val surveyDone: Boolean = false,
    val selfChecks: List<SelfAssessment> = emptyList(),
    val community: CommunityImpact? = null,
    val communityError: String? = null,
    val institution: CommunityImpact? = null,
    val institutionError: String? = null,
    val hasCollege: Boolean = false,
    val leaderboard: List<LeaderboardRow>? = null,
    val leaderboardError: String? = null,
    val minCohort: Int = 10,
    val message: String? = null,
    val surveySubmitting: Boolean = false
)

@HiltViewModel
class ImpactViewModel @Inject constructor(
    private val impact: ImpactRepository,
    private val play: PlayRepository,
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val content: ContentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ImpactUiState())
    val state: StateFlow<ImpactUiState> = _state.asStateFlow()

    fun refresh() = viewModelScope.launch {
        val signedIn = auth.userId != null
        val cfg = content.config().data
        _state.update { it.copy(signedIn = signedIn, minCohort = cfg.minCohort, loading = true) }
        if (!signedIn) { _state.update { it.copy(loading = false) }; return@launch }
        val stats = play.stats()?.data
        val series = play.progressSeries(6)?.data.orEmpty()
        val survey = impact.latestSurvey().getOrNull()
        val done = survey?.let { impact.hasSubmitted(it.id).getOrDefault(false) } ?: false
        val checks = impact.selfAssessments().getOrDefault(emptyList())
        _state.update { it.copy(loading = false, stats = stats, series = series, survey = survey, surveyDone = done, selfChecks = checks) }
    }

    fun setTab(i: Int) {
        _state.update { it.copy(tab = i) }
        if (!_state.value.signedIn) return
        viewModelScope.launch {
            when (i) {
                1 -> if (_state.value.community == null) impact.communityImpact(null)
                    .onSuccess { r -> _state.update { it.copy(community = r, communityError = null) } }
                    .onFailure { e -> _state.update { it.copy(communityError = e.toAppError().message) } }
                2 -> {
                    val p = profiles.profile.value ?: profiles.refresh().getOrNull()
                    val college = p?.collegeId
                    _state.update { it.copy(hasCollege = college != null) }
                    if (college != null && _state.value.institution == null) impact.communityImpact(college)
                        .onSuccess { r -> _state.update { it.copy(institution = r, institutionError = null) } }
                        .onFailure { e -> _state.update { it.copy(institutionError = e.toAppError().message) } }
                    if (_state.value.leaderboard == null) play.leaderboard(p?.cityId)
                        .onSuccess { l -> _state.update { it.copy(leaderboard = l, leaderboardError = null) } }
                        .onFailure { e -> _state.update { it.copy(leaderboard = emptyList(), leaderboardError = e.toAppError().message) } }
                }
            }
        }
    }

    fun addSelfCheck(kind: String, confidence: Int) = viewModelScope.launch {
        impact.addSelfAssessment(kind, confidence)
            .onSuccess { _state.update { it.copy(selfChecks = impact.selfAssessments().getOrDefault(it.selfChecks), message = "Saved.") } }
            .onFailure { e -> _state.update { it.copy(message = e.toAppError().message) } }
    }

    fun deleteSelfCheck(id: String) = viewModelScope.launch {
        impact.deleteSelfAssessment(id).onSuccess { _state.update { s -> s.copy(selfChecks = s.selfChecks.filterNot { it.id == id }) } }
    }

    fun submitSurvey(answers: Map<String, String>, onDone: () -> Unit) {
        val sv = _state.value.survey ?: return
        if (_state.value.surveySubmitting) return
        _state.update { it.copy(surveySubmitting = true) }
        viewModelScope.launch {
            impact.submitSurvey(sv.id, answers)
                .onSuccess { _state.update { it.copy(surveySubmitting = false, surveyDone = true, message = "Thanks for completing this month's survey.") }; onDone() }
                .onFailure { e ->
                    val err = e.toAppError()
                    _state.update { it.copy(surveySubmitting = false, surveyDone = it.surveyDone || err.code == "already submitted", message = err.message) }
                    if (err.code == "already submitted") onDone()
                }
        }
    }
}

@Composable
fun ImpactScreen(contentPadding: PaddingValues, onSurvey: () -> Unit, onSignIn: () -> Unit, vm: ImpactViewModel) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    LaunchedEffect(Unit) { vm.refresh() }
    Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState())
        .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp)) {
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
            ScreenTitle("Track Impact", "Understand what you're learning and how your community is participating.")
            Spacer(Modifier.height(16.dp))
            SegmentedTabs(listOf("My progress", "Community", "Institution"), s.tab, vm::setTab)
            Spacer(Modifier.height(16.dp))
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            if (!s.signedIn) {
                EmptyState("Sign in to track your progress", "Your practice history, the monthly pulse survey and community insights need an account. Practice without an account stays on this device.",
                    imageKey = "impact_empty", fallback = FallbackArt.SUNRISE, action = "Sign in", onAction = onSignIn)
                return@Column
            }
            s.message?.let { InfoNote(it); Spacer(Modifier.height(12.dp)) }
            if (s.survey != null && !s.surveyDone) {
                DfiCard(Modifier.fillMaxWidth(), onClick = onSurvey, padding = PaddingValues(0.dp)) {
                    ListRow("Monthly pulse survey", subtitle = "Anonymous · 1 min · Start", icon = Icons.Rounded.Assignment, onClick = onSurvey)
                }
                Spacer(Modifier.height(16.dp))
            }
            when (s.tab) {
                0 -> MyProgress(s, vm)
                1 -> CommunityView(s.community, s.communityError, s.minCohort, "Across all participants this month")
                else -> {
                    if (!s.hasCollege) InfoNote("Add your college in Profile to see your institution's aggregate view.")
                    else CommunityView(s.institution, s.institutionError, s.minCohort, "Your college, this month")
                    Spacer(Modifier.height(20.dp))
                    Leaderboard(s.leaderboard, s.leaderboardError)
                }
            }
        }
    }
}

@Composable
private fun MyProgress(s: ImpactUiState, vm: ImpactViewModel) {
    val c = DfiTheme.colors
    if (s.loading) { LoadingState(); return }
    val st = s.stats
    DfiCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val score = st?.practiceScore
            ProgressRing((score ?: 0) / 100f, size = 104.dp, description = if (score != null) "Practice score $score out of 100" else PRACTICE_SCORE_EMPTY) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(score?.toString() ?: "–", style = DfiTheme.typography.stat, color = c.onSurface)
                    Text("of 100", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Practice score", style = DfiTheme.typography.cardTitle, color = c.onSurface)
                val d = st?.practiceScoreDelta
                if (score == null) Text(PRACTICE_SCORE_EMPTY, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                else if (d != null) Text("${if (d >= 0) "+" else ""}$d since last month", style = DfiTheme.typography.labelSmall, color = if (d >= 0) c.success else c.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(PRACTICE_SCORE_LABEL, style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
    }
    Spacer(Modifier.height(12.dp))
    val pts = s.series.map { Format.monthShort(it.month) to it.practiceScore }
    if (pts.any { it.second != null }) {
        DfiCard(Modifier.fillMaxWidth()) {
            Text("Practice score by month", style = DfiTheme.typography.cardTitle, color = c.onSurface)
            Spacer(Modifier.height(12.dp))
            TrendChart(pts)
        }
        Spacer(Modifier.height(12.dp))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatTile("Scenarios", if (st != null) "${st.scenariosCompleted} of ${st.scenariosTotal}" else "–", Modifier.weight(1f))
        StatTile("Joined this month", Format.number(st?.activitiesJoinedThisMonth ?: 0), Modifier.weight(1f))
        StatTile("Streak", "${st?.streakDays ?: 0} days", Modifier.weight(1f))
    }
    Spacer(Modifier.height(20.dp))
    SelfCheck(s, vm)
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier) {
    DfiCard(modifier, padding = PaddingValues(12.dp)) {
        Text(value, style = DfiTheme.typography.cardTitle, color = DfiTheme.colors.onSurface, maxLines = 1)
        Text(label, style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant, maxLines = 2)
    }
}

@Composable
private fun SelfCheck(s: ImpactUiState, vm: ImpactViewModel) {
    val c = DfiTheme.colors
    var value by remember { mutableIntStateOf(0) }
    SectionHeader("Confidence self-check")
    DfiCard(Modifier.fillMaxWidth()) {
        Text("How confident do you feel about saying no to something you don't want to do?", style = DfiTheme.typography.body, color = c.onSurface)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { n ->
                Box(Modifier.weight(1f).heightIn(min = 48.dp).clip(DfiTheme.shapes.tile)
                    .background(if (value == n) c.primary else c.surfaceMuted)
                    .selectable(value == n, role = Role.RadioButton) { value = n }
                    .semantics { contentDescription = "$n of 5" }, contentAlignment = Alignment.Center) {
                    Text("$n", style = DfiTheme.typography.label, color = if (value == n) c.onPrimary else c.onSurface)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Not confident", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Text("Very confident", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        val kind = if (s.selfChecks.isEmpty()) "before" else "after"
        PrimaryButton(if (kind == "before") "Save as my starting point" else "Save check-in", { vm.addSelfCheck(kind, value); value = 0 },
            Modifier.fillMaxWidth(), enabled = value > 0)
        if (s.selfChecks.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("What was measured and when", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            s.selfChecks.forEach { sa ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${if (sa.kind == "before") "Starting point" else "Check-in"} · ${sa.confidence}/5 · ${sa.createdAt?.take(10) ?: ""}",
                        style = DfiTheme.typography.bodySmall, color = c.onSurface, modifier = Modifier.weight(1f))
                    sa.id?.let { id -> IconButton(onClick = { vm.deleteSelfCheck(id) }) { Icon(Icons.Rounded.Delete, "Delete this check-in", tint = c.onSurfaceVariant) } }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("This is how you feel, in your own words. It isn't a measure of behaviour.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
    }
}

@Composable
private fun CommunityView(data: CommunityImpact?, error: String?, minCohort: Int, subtitle: String) {
    val c = DfiTheme.colors
    when {
        error != null -> ErrorState(error)
        data == null -> LoadingState()
        data.insufficientData -> EmptyState("Not enough answers yet",
            "Results appear once at least ${data.minCohort.takeIf { it > 0 } ?: minCohort} people have answered, to protect privacy.",
            imageKey = "impact_empty", fallback = FallbackArt.SUNRISE)
        else -> {
            Text("$subtitle · ${Format.number(data.responses)} responses", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            data.questions.forEach { q ->
                DfiCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Text(q.prompt, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                    val total = q.options.sumOf { it.count }.coerceAtLeast(1)
                    q.options.forEach { o ->
                        Spacer(Modifier.height(10.dp))
                        Row { Text(o.label, style = DfiTheme.typography.bodySmall, color = c.onSurface, modifier = Modifier.weight(1f)); Text(Format.number(o.count), style = DfiTheme.typography.labelSmall, color = c.onSurface) }
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.fillMaxWidth().height(8.dp).clip(DfiTheme.shapes.pill).background(c.surfaceMuted)) {
                            Box(Modifier.fillMaxWidth(o.count / total.toFloat()).height(8.dp).clip(DfiTheme.shapes.pill).background(c.primary))
                        }
                    }
                }
            }
            InfoNote("Anonymous, aggregated insights. No names, phone numbers or individual answers are shown.", icon = Icons.Rounded.Lock)
        }
    }
}

@Composable
private fun Leaderboard(rows: List<LeaderboardRow>?, error: String?) {
    val c = DfiTheme.colors
    SectionHeader("College participation")
    Text("Participation and learning: total practice points from students who opted in. Colleges appear only above the privacy minimum.",
        style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
    Spacer(Modifier.height(10.dp))
    when {
        rows == null -> LoadingState()
        error != null && rows.isEmpty() -> ErrorState(error)
        rows.isEmpty() -> InfoNote("No colleges have enough participants yet.")
        else -> DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
            rows.forEachIndexed { i, r ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}", style = DfiTheme.typography.labelSmall, color = c.onSurfaceVariant, modifier = Modifier.width(28.dp))
                    Column(Modifier.weight(1f)) {
                        Text(r.collegeName, style = DfiTheme.typography.bodySmall, color = c.onSurface)
                        Text("${r.cityName} · ${Format.number(r.participants)} participants", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                    }
                    Text("${Format.number(r.points)} pts", style = DfiTheme.typography.labelSmall, color = c.primaryDark)
                }
            }
        }
    }
}

// ============================================================== Survey (one question per screen)

@Composable
fun SurveyScreen(onBack: () -> Unit, vm: ImpactViewModel) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    val sv = s.survey
    var index by remember { mutableIntStateOf(-1) }
    val answers = remember { mutableStateMapOf<String, String>() }
    var done by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar(sv?.title ?: "Pulse survey", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            when {
                sv == null -> InfoNote("There's no survey this month.")
                done || s.surveyDone -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = c.success, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Thanks for completing this month's survey.", style = DfiTheme.typography.sectionTitle, color = c.onSurface, textAlign = TextAlign.Center)
                }
                index < 0 -> {
                    Text("Before you start", style = DfiTheme.typography.screenTitle, color = c.onSurface)
                    Spacer(Modifier.height(10.dp))
                    Text("${sv.questions.size} quick questions. It's optional.", style = DfiTheme.typography.body, color = c.onSurface)
                    Spacer(Modifier.height(10.dp))
                    InfoNote("Only your answers are stored — not your name, phone or a timestamp linked to you. You can only answer once a month.", icon = Icons.Rounded.Lock)
                }
                else -> {
                    val q = sv.questions[index]
                    Text("Question ${index + 1} of ${sv.questions.size}", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(q.prompt, style = DfiTheme.typography.heroTitle, color = c.onSurface)
                    Spacer(Modifier.height(16.dp))
                    q.options.forEach { o ->
                        val sel = answers[q.id] == o.id
                        DfiCard(Modifier.fillMaxWidth().padding(vertical = 5.dp), onClick = { answers[q.id] = o.id },
                            color = if (sel) c.primarySoft else c.surface) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(o.label, style = DfiTheme.typography.body, color = c.onSurface, modifier = Modifier.weight(1f))
                                if (sel) Icon(Icons.Rounded.CheckCircle, "Selected", tint = c.primary)
                            }
                        }
                    }
                }
            }
            s.message?.let { if (!s.surveyDone) { Spacer(Modifier.height(12.dp)); InfoNote(it) } }
        }
        Box(Modifier.padding(20.dp).navigationBarsPadding()) {
            when {
                sv == null || done || s.surveyDone -> PrimaryButton("Done", onBack, Modifier.fillMaxWidth())
                index < 0 -> PrimaryButton("Start", { index = 0 }, Modifier.fillMaxWidth())
                index < sv.questions.lastIndex -> PrimaryButton("Next", { index++ }, Modifier.fillMaxWidth(), enabled = answers.containsKey(sv.questions[index].id))
                else -> PrimaryButton("Submit", { vm.submitSurvey(answers.toMap()) { done = true } }, Modifier.fillMaxWidth(),
                    enabled = answers.containsKey(sv.questions[index].id), loading = s.surveySubmitting)
            }
        }
    }
}
