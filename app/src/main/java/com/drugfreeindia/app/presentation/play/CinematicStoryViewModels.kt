package com.drugfreeindia.app.presentation.play

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.repository.CinematicProgress
import com.drugfreeindia.app.data.repository.CinematicStoryRepository
import com.drugfreeindia.app.domain.model.CinematicBeat
import com.drugfreeindia.app.domain.model.CinematicChoice
import com.drugfreeindia.app.domain.model.CinematicScene
import com.drugfreeindia.app.domain.model.CinematicStory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// ============================================================== Story Library

data class StoryLibraryUiState(
    val loading: Boolean = true,
    val story: CinematicStory? = null,
    val progress: CinematicProgress? = null
) {
    val hasActiveProgress: Boolean get() = progress != null && !progress.completed
    val hasCompleted: Boolean get() = progress?.completed == true
}

@HiltViewModel
class StoryLibraryViewModel @Inject constructor(
    private val repo: CinematicStoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StoryLibraryUiState())
    val state: StateFlow<StoryLibraryUiState> = _state.asStateFlow()

    fun refresh(storyId: String = "the_changes_nobody_noticed") = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        val story = repo.loadStory(storyId)
        val progress = repo.getProgress(storyId)
        _state.update { it.copy(loading = false, story = story, progress = progress) }
    }

    fun restartStory(storyId: String = "the_changes_nobody_noticed", onRestarted: () -> Unit) = viewModelScope.launch {
        repo.clearProgress(storyId)
        onRestarted()
    }
}

// ============================================================== Cinematic Game

data class CinematicPlayUiState(
    val loading: Boolean = true,
    val story: CinematicStory? = null,
    val currentScene: CinematicScene? = null,
    val activeBeats: List<CinematicBeat> = emptyList(),
    val routeHistory: List<String> = emptyList(),
    val routeNotes: List<String> = emptyList(),
    val isSelecting: Boolean = false,
    val isSpeaking: Boolean = false,
    val ttsAvailable: Boolean = false
) {
    val isEnding: Boolean get() = currentScene?.isEnding == true
}

@HiltViewModel
class CinematicPlayViewModel @Inject constructor(
    private val repo: CinematicStoryRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val storyId: String = savedStateHandle["id"] ?: "the_changes_nobody_noticed"

    private val _state = MutableStateFlow(CinematicPlayUiState())
    val state: StateFlow<CinematicPlayUiState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        initTts()
        loadStorySession()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.ENGLISH)
                    isTtsInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                    _state.update { it.copy(ttsAvailable = isTtsInitialized) }
                }
            }
        } catch (_: Exception) {
            isTtsInitialized = false
        }
    }

    private fun loadStorySession() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        val story = repo.loadStory(storyId)
        if (story == null) {
            _state.update { it.copy(loading = false) }
            return@launch
        }

        val progress = repo.getProgress(storyId)
        val initialSceneId = if (progress != null && !progress.completed) {
            progress.currentSceneId
        } else {
            story.startScene
        }

        val scene = story.scene(initialSceneId) ?: story.scene(story.startScene)
        val history = progress?.routeHistory ?: listOf(story.startScene)
        val beats = computeBeats(scene, history)

        _state.update {
            it.copy(
                loading = false,
                story = story,
                currentScene = scene,
                activeBeats = beats,
                routeHistory = history,
                routeNotes = emptyList(),
                isSelecting = false
            )
        }
    }

    private fun computeBeats(scene: CinematicScene?, history: List<String>): List<CinematicBeat> {
        if (scene == null) return emptyList()
        val prevSceneId = if (history.size >= 2) history[history.size - 2] else null

        // Check if an opening variant matches where the player came from
        if (prevSceneId != null && scene.openingVariants.isNotEmpty()) {
            val matchingVariant = scene.openingVariants.firstOrNull { it.cameFrom.contains(prevSceneId) }
            if (matchingVariant != null && matchingVariant.beats.isNotEmpty()) {
                return matchingVariant.beats + scene.beats
            }
        }
        return scene.beats
    }

    fun selectChoice(choice: CinematicChoice) {
        val s = _state.value
        val story = s.story ?: return
        val currentScene = s.currentScene ?: return
        if (s.isSelecting) return // Prevent accidental double taps

        _state.update { it.copy(isSelecting = true) }
        stopSpeech()

        viewModelScope.launch {
            val nextScene = story.scene(choice.next)
            if (nextScene == null) {
                _state.update { it.copy(isSelecting = false) }
                return@launch
            }

            val newHistory = s.routeHistory + nextScene.id
            val newNotes = if (!choice.routeNote.isNullOrBlank()) s.routeNotes + choice.routeNote else s.routeNotes
            val beats = computeBeats(nextScene, newHistory)

            val isCompleted = nextScene.isEnding
            val lastEnding = if (isCompleted) nextScene.id else null

            // Save progress
            repo.saveProgress(
                CinematicProgress(
                    storyId = story.id,
                    currentSceneId = nextScene.id,
                    routeHistory = newHistory,
                    choiceHistory = s.routeNotes + choice.id,
                    completed = isCompleted,
                    lastEndingId = lastEnding
                )
            )

            _state.update {
                it.copy(
                    currentScene = nextScene,
                    activeBeats = beats,
                    routeHistory = newHistory,
                    routeNotes = newNotes,
                    isSelecting = false
                )
            }
        }
    }

    fun restartStory() {
        stopSpeech()
        val s = _state.value
        val story = s.story ?: return
        val start = story.scene(story.startScene) ?: return
        val beats = computeBeats(start, listOf(start.id))

        viewModelScope.launch {
            repo.clearProgress(story.id)
            _state.update {
                it.copy(
                    currentScene = start,
                    activeBeats = beats,
                    routeHistory = listOf(start.id),
                    routeNotes = emptyList(),
                    isSelecting = false
                )
            }
        }
    }

    fun toggleSpeech() {
        if (_state.value.isSpeaking) {
            stopSpeech()
        } else {
            speakCurrentScene()
        }
    }

    private fun speakCurrentScene() {
        if (!isTtsInitialized || tts == null) return
        val textToSpeak = _state.value.activeBeats.joinToString(". ") { beat ->
            val speakerPrefix = beat.speaker?.let { "$it says: " } ?: beat.from?.let { "$it messages: " } ?: ""
            "$speakerPrefix${beat.text}"
        }
        if (textToSpeak.isBlank()) return

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "story_narration")
        _state.update { it.copy(isSpeaking = true) }
    }

    fun stopSpeech() {
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (_: Exception) {}
        _state.update { it.copy(isSpeaking = false) }
    }

    override fun onCleared() {
        stopSpeech()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        super.onCleared()
    }
}
