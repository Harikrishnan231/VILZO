package com.drugfreeindia.app.presentation.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.model.StoryProgress
import com.drugfreeindia.app.domain.model.Streak
import com.drugfreeindia.app.domain.repository.StoryRepository
import com.drugfreeindia.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayHubUiState(
    val totalPoints: Int = 140,
    val levelName: String = "Confident", // Starter, Steady, Confident, Champion
    val streak: Streak = Streak(4, 6, "2026-10-13"),
    val stories: List<Story> = emptyList(),
    val progressMap: Map<String, StoryProgress> = emptyMap(),
    val unlockedBadges: List<Badge> = emptyList()
)

@HiltViewModel
class PlayHubViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<PlayHubUiState> = combine(
        storyRepository.getAllStories(),
        storyRepository.getAllProgress(),
        userRepository.getStreak(),
        userRepository.getBadges()
    ) { stories, progressList, streak, badges ->
        val progressMap = progressList.associateBy { it.storyId }
        val totalPoints = progressList.sumOf { it.bestScore } + 80
        val level = when {
            totalPoints >= 150 -> "Champion"
            totalPoints >= 90 -> "Confident"
            totalPoints >= 40 -> "Steady"
            else -> "Starter"
        }
        PlayHubUiState(
            totalPoints = totalPoints,
            levelName = level,
            streak = streak,
            stories = stories,
            progressMap = progressMap,
            unlockedBadges = badges.filter { it.isUnlocked }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlayHubUiState()
    )
}
