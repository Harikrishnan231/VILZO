package com.drugfreeindia.app.presentation.play.story

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.domain.model.Choice
import com.drugfreeindia.app.domain.model.Scene
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.usecase.PlayStoryUseCase
import com.drugfreeindia.app.domain.usecase.SubmitChoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StoryEffect {
    data class Completed(val storyId: String, val totalScore: Int, val delta: Int, val badgeId: String?) : StoryEffect()
    object TriggerHaptic : StoryEffect()
    object TriggerConfetti : StoryEffect()
}

data class StoryPlayUiState(
    val story: Story? = null,
    val currentSceneIndex: Int = 0,
    val currentScene: Scene? = null,
    val totalScenes: Int = 3,
    val totalScore: Int = 0,
    val timerRemaining: Int = 10,
    val pressureLevel: Float = 0.5f,
    val selectedChoiceId: String? = null,
    val choiceFeedback: String? = null,
    val isAnswerRevealed: Boolean = false,
    val lastBadgeEarned: String? = null
)

@HiltViewModel
class StoryPlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playStoryUseCase: PlayStoryUseCase,
    private val submitChoiceUseCase: SubmitChoiceUseCase
) : ViewModel() {

    private val storyId: String = checkNotNull(savedStateHandle["storyId"])

    private val _uiState = MutableStateFlow(StoryPlayUiState())
    val uiState: StateFlow<StoryPlayUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<StoryEffect>()
    val effects: SharedFlow<StoryEffect> = _effects.asSharedFlow()

    private var timerJob: Job? = null

    init {
        loadStory()
    }

    private fun loadStory() {
        viewModelScope.launch {
            val story = playStoryUseCase(storyId)
            if (story != null && story.scenes.isNotEmpty()) {
                val firstScene = story.scenes.first()
                _uiState.update {
                    it.copy(
                        story = story,
                        currentScene = firstScene,
                        currentSceneIndex = 0,
                        totalScenes = story.scenes.size,
                        pressureLevel = firstScene.pressure,
                        timerRemaining = firstScene.timerSeconds ?: 10
                    )
                }
                startTimer(firstScene.timerSeconds ?: 10)
            }
        }
    }

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 0) {
                _uiState.update { it.copy(timerRemaining = i) }
                delay(1000)
            }
            // Auto timeout: choose gentle first option or neutral transition
            if (!_uiState.value.isAnswerRevealed && _uiState.value.currentScene?.choices?.isNotEmpty() == true) {
                val fallback = _uiState.value.currentScene?.choices?.first()
                fallback?.let { onSelectChoice(it) }
            }
        }
    }

    fun onSelectChoice(choice: Choice) {
        if (_uiState.value.isAnswerRevealed) return
        timerJob?.cancel()

        val pointsEarned = choice.points
        val newTotalScore = _uiState.value.totalScore + pointsEarned
        val badge = choice.badgeEarned

        _uiState.update {
            it.copy(
                selectedChoiceId = choice.id,
                choiceFeedback = choice.feedback,
                isAnswerRevealed = true,
                totalScore = newTotalScore,
                lastBadgeEarned = badge ?: it.lastBadgeEarned
            )
        }

        viewModelScope.launch {
            _effects.emit(StoryEffect.TriggerHaptic)
            if (pointsEarned >= 8) {
                _effects.emit(StoryEffect.TriggerConfetti)
            }
        }
    }

    fun onContinueToNext() {
        val currentScene = _uiState.value.currentScene ?: return
        val selectedChoice = currentScene.choices.find { it.id == _uiState.value.selectedChoiceId }
        val nextSceneId = selectedChoice?.next

        val story = _uiState.value.story ?: return

        if (nextSceneId == null || nextSceneId == "s3" || currentScene.id == "s3") {
            // Finish scenario
            viewModelScope.launch {
                submitChoiceUseCase(
                    storyId = story.id,
                    score = _uiState.value.totalScore,
                    tag = selectedChoice?.tag ?: "calm_no",
                    badgeEarned = _uiState.value.lastBadgeEarned
                )
                _effects.emit(
                    StoryEffect.Completed(
                        storyId = story.id,
                        totalScore = _uiState.value.totalScore,
                        delta = 6,
                        badgeId = _uiState.value.lastBadgeEarned
                    )
                )
            }
            return
        }

        val nextScene = story.scenes.find { it.id == nextSceneId }
        if (nextScene != null) {
            val nextIndex = story.scenes.indexOf(nextScene)
            _uiState.update {
                it.copy(
                    currentScene = nextScene,
                    currentSceneIndex = nextIndex,
                    selectedChoiceId = null,
                    choiceFeedback = null,
                    isAnswerRevealed = false,
                    pressureLevel = nextScene.pressure,
                    timerRemaining = nextScene.timerSeconds ?: 10
                )
            }
            startTimer(nextScene.timerSeconds ?: 10)
        } else {
            // Wrap up if no explicit scene found
            viewModelScope.launch {
                submitChoiceUseCase(
                    storyId = story.id,
                    score = _uiState.value.totalScore,
                    tag = selectedChoice?.tag ?: "calm_no",
                    badgeEarned = _uiState.value.lastBadgeEarned
                )
                _effects.emit(
                    StoryEffect.Completed(
                        storyId = story.id,
                        totalScore = _uiState.value.totalScore,
                        delta = 6,
                        badgeId = _uiState.value.lastBadgeEarned
                    )
                )
            }
        }
    }
}
