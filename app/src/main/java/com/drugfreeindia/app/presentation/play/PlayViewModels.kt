package com.drugfreeindia.app.presentation.play

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.GameMode
import com.drugfreeindia.app.data.repository.GameSession
import com.drugfreeindia.app.data.repository.PlayRepository
import com.drugfreeindia.app.domain.model.AppConfig
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.MyStats
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.model.StoryChoice
import com.drugfreeindia.app.domain.model.StoryScene
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Friendly names for choice tags (skills practised). "gave_in" is intentionally not a skill. */
val skillNames = mapOf(
    "calm_no" to "Saying no calmly",
    "redirect" to "Changing the plan",
    "exit_plan" to "Having an exit plan",
    "trusted_contact" to "Reaching out to someone you trust",
    "reflect" to "Reflecting and preparing",
    "boundary" to "Setting a boundary",
    "support" to "Supporting a friend",
    "self_care" to "Looking after yourself"
)

// ============================================================== Play landing

data class StoryRow(val story: Story, val bestPoints: Int?, val completed: Boolean)

data class PlayUiState(
    val loading: Boolean = true,
    val signedIn: Boolean = false,
    val stories: List<StoryRow> = emptyList(),
    val stats: MyStats? = null,
    val config: AppConfig = AppConfig(),
    val badges: List<Badge> = emptyList(),
    val earned: Set<String> = emptySet(),
    val pending: Int = 0
)

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val content: ContentRepository,
    private val play: PlayRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PlayUiState())
    val state: StateFlow<PlayUiState> = _state.asStateFlow()

    private var lastLocal: List<com.drugfreeindia.app.data.local.LocalResult> = emptyList()

    init {
        viewModelScope.launch { play.localResults.collect { refresh(it) } }
        viewModelScope.launch { play.pendingCount.collect { n -> _state.update { it.copy(pending = n) } } }
    }

    fun refresh(local: List<com.drugfreeindia.app.data.local.LocalResult> = lastLocal) = viewModelScope.launch {
        lastLocal = local
        val stories = content.stories().data
        val results = local.associate { it.storyId to it.bestPoints }
        val stats = play.stats()?.data
        val badges = content.badges().data
        val earned = play.myBadges()?.data?.map { it.badgeId }?.toSet().orEmpty()
        _state.update {
            it.copy(
                loading = false, signedIn = auth.userId != null,
                stories = stories.map { s -> StoryRow(s, results[s.id], results.containsKey(s.id)) },
                stats = stats, config = content.config().data, badges = badges, earned = earned
            )
        }
    }
}

// ============================================================== Gameplay

data class Feedback(val choice: StoryChoice, val pointsAwarded: Int, val duplicate: Boolean, val text: String)

data class GameResult(
    val points: Int,
    val newBadges: List<Badge>,
    val skills: List<String>,
    val takeaway: String?,
    val scored: Boolean,
    val queued: Boolean,
    val alreadyCompleted: Boolean,
    val error: String?
)

data class GameUiState(
    val loading: Boolean = true,
    val story: Story? = null,
    val showStartSheet: Boolean = true,
    val relaxed: Boolean = false,
    val starting: Boolean = false,
    val mode: GameMode = GameMode.LOCAL,
    val sceneId: String? = null,
    val sceneIndex: Int = 0,
    val points: Int = 0,
    val selected: String? = null,
    val submitting: Boolean = false,
    val feedback: Feedback? = null,
    val timerTotal: Int = 0,
    val timerLeft: Int = 0,
    val timerExpired: Boolean = false,
    val tags: List<String> = emptyList(),
    val notice: String? = null,
    val completing: Boolean = false,
    val result: GameResult? = null,
    val error: String? = null
) {
    val scene: StoryScene? get() = sceneId?.let { story?.scene(it) }
    val totalScenes: Int get() = story?.pathLength ?: 0
}

@HiltViewModel
class GameViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val content: ContentRepository,
    private val play: PlayRepository,
    private val prefs: PrefsStore
) : ViewModel() {
    private val storyId: String = checkNotNull(savedState["id"])
    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()
    private var session: GameSession? = null
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val story = content.story(storyId)
            val relaxed = prefs.current().relaxedMode
            _state.update { it.copy(loading = false, story = story, relaxed = relaxed, error = if (story == null) "This scenario couldn't be found." else null) }
        }
    }

    fun setRelaxed(v: Boolean) {
        _state.update { it.copy(relaxed = v) }
        viewModelScope.launch { prefs.setRelaxedMode(v) }
    }

    fun start(restart: Boolean = false) {
        val story = _state.value.story ?: return
        if (_state.value.starting) return
        _state.update { it.copy(starting = true) }
        viewModelScope.launch {
            val s = play.start(story.id, restart)
            session = s
            val resumeId = s.resumeScene?.takeIf { story.scene(it) != null }
            val first = resumeId ?: story.scenes.first().id
            _state.update {
                it.copy(
                    starting = false, showStartSheet = false, mode = s.mode, sceneId = first,
                    sceneIndex = if (resumeId != null) s.resumeAnswered else 0,
                    points = if (resumeId != null) s.resumePoints else 0,
                    selected = null, feedback = null, result = null, tags = emptyList(),
                    notice = when {
                        resumeId != null -> "Welcome back — continuing where you left off."
                        s.mode == GameMode.QUEUED -> "You're offline. Answers are saved on this device and will sync."
                        s.mode == GameMode.LOCAL -> "Practice mode — this play-through isn't scored on the server."
                        else -> null
                    }
                )
            }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        val scene = _state.value.scene ?: return
        val secs = scene.timerSeconds ?: 0
        if (_state.value.relaxed || secs <= 0 || scene.isEnding) {
            _state.update { it.copy(timerTotal = 0, timerLeft = 0, timerExpired = false) }
            return
        }
        _state.update { it.copy(timerTotal = secs, timerLeft = secs, timerExpired = false) }
        timerJob = viewModelScope.launch {
            for (i in secs - 1 downTo 0) {
                delay(1000)
                _state.update { it.copy(timerLeft = i) }
            }
            // A nudge only: nothing is auto-picked and nothing is penalised.
            _state.update { it.copy(timerExpired = true) }
        }
    }

    fun select(choiceId: String) {
        if (_state.value.feedback != null || _state.value.submitting) return
        _state.update { it.copy(selected = choiceId) }
    }

    fun submit() {
        val st = _state.value
        val story = st.story ?: return
        val scene = st.scene ?: return
        val choice = scene.choices.firstOrNull { it.id == st.selected } ?: return
        val s = session ?: return
        if (st.submitting || st.feedback != null) return
        timerJob?.cancel()
        _state.update { it.copy(submitting = true) }
        val isFinal = choice.next?.let { story.scene(it) }?.isEnding ?: true
        viewModelScope.launch {
            var awarded = choice.points
            var duplicate = false
            var total = st.points + choice.points
            var text = choice.feedback ?: "Thanks for thinking it through."
            if (s.mode == GameMode.REMOTE) {
                val r = play.submit(s, scene.id, choice.id, isFinal)
                r.onSuccess { res ->
                    if (res != null) {
                        duplicate = res.duplicate
                        awarded = if (res.duplicate) 0 else res.points
                        total = res.totalPoints // server scoring is authoritative
                        res.feedback?.let { text = it }
                    }
                }.onFailure { e ->
                    val err = e.toAppError()
                    if (err.code == "offline" || err.code == "network") {
                        // Connection dropped mid-game: keep playing and queue this and later answers.
                        val queued = s.copy(mode = GameMode.QUEUED)
                        session = queued
                        play.queue(queued, scene.id, choice.id, isFinal)
                        _state.update { it.copy(mode = GameMode.QUEUED, notice = "Saved on this device; will sync.") }
                    } else {
                        session = s.copy(mode = GameMode.LOCAL)
                        _state.update { it.copy(mode = GameMode.LOCAL, notice = err.message) }
                    }
                }
            } else {
                play.submit(s, scene.id, choice.id, isFinal)
            }
            _state.update {
                it.copy(
                    submitting = false, points = total,
                    tags = it.tags + listOfNotNull(choice.tag),
                    feedback = Feedback(choice, awarded, duplicate, text)
                )
            }
        }
    }

    fun next() {
        val st = _state.value
        val story = st.story ?: return
        val choice = st.feedback?.choice ?: return
        val nextScene = choice.next?.let { story.scene(it) }
        if (nextScene == null) { complete(null); return }
        _state.update { it.copy(sceneId = nextScene.id, sceneIndex = it.sceneIndex + 1, selected = null, feedback = null) }
        startTimer()
    }

    fun complete(ending: StoryScene? = _state.value.scene) {
        val st = _state.value
        val s = session ?: return
        if (st.completing || st.result != null) return
        _state.update { it.copy(completing = true) }
        viewModelScope.launch {
            val r = play.complete(s, st.points)
            val badges = content.badges().data
            val res = r.getOrNull()
            val err = r.exceptionOrNull()?.toAppError()
            _state.update {
                it.copy(
                    completing = false,
                    points = res?.totalPoints ?: it.points,
                    result = GameResult(
                        points = res?.totalPoints ?: it.points,
                        newBadges = res?.newBadges.orEmpty().mapNotNull { id -> badges.firstOrNull { b -> b.id == id } },
                        skills = it.tags.mapNotNull { t -> skillNames[t] }.distinct(),
                        takeaway = ending?.takeaway ?: ending?.narration,
                        scored = s.mode == GameMode.REMOTE && err == null,
                        queued = s.mode == GameMode.QUEUED,
                        alreadyCompleted = res?.alreadyCompleted == true,
                        error = err?.message
                    )
                )
            }
        }
    }

    fun replay() {
        _state.update { GameUiState(loading = false, story = it.story, relaxed = it.relaxed, showStartSheet = false) }
        start(restart = true)
    }

    override fun onCleared() { timerJob?.cancel() }
}
