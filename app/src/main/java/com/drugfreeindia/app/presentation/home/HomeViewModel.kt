package com.drugfreeindia.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.DfiDataStore
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.CollegeStat
import com.drugfreeindia.app.domain.model.ConfidenceScore
import com.drugfreeindia.app.domain.model.Streak
import com.drugfreeindia.app.domain.repository.ExploreRepository
import com.drugfreeindia.app.domain.repository.ImpactRepository
import com.drugfreeindia.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val nickname: String = "Calm Falcon",
    val greeting: String = "Good morning",
    val confidence: ConfidenceScore = ConfidenceScore(84, 88, 80, 82, 6),
    val streak: Streak = Streak(4, 6, "2026-10-13"),
    val earnedBadgesCount: Int = 3,
    val totalBadgesCount: Int = 12,
    val lastBadge: Badge? = null,
    val nextActivity: Activity? = null,
    val topColleges: List<CollegeStat> = emptyList(),
    val confidenceTrend: List<Float> = listOf(68f, 72f, 70f, 75f, 79f, 84f)
)

private data class HomeUserData(
    val nickname: String,
    val confidence: ConfidenceScore,
    val streak: Streak,
    val badges: List<Badge>
)

private data class HomeExploreData(
    val activities: List<Activity>,
    val colleges: List<CollegeStat>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val impactRepository: ImpactRepository,
    private val exploreRepository: ExploreRepository,
    private val dataStore: DfiDataStore
) : ViewModel() {

    private val userFlow = combine(
        dataStore.userNickname,
        impactRepository.getConfidenceScore(),
        userRepository.getStreak(),
        userRepository.getBadges()
    ) { nickname, confidence, streak, badges ->
        HomeUserData(nickname, confidence, streak, badges)
    }

    private val exploreFlow = combine(
        exploreRepository.getActivities(),
        impactRepository.getCollegeStats()
    ) { activities, colleges ->
        HomeExploreData(activities, colleges)
    }

    val uiState: StateFlow<HomeUiState> = combine(userFlow, exploreFlow) { userData, exploreData ->
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        val unlockedBadges = userData.badges.filter { it.isUnlocked }
        HomeUiState(
            nickname = userData.nickname,
            greeting = greeting,
            confidence = userData.confidence,
            streak = userData.streak,
            earnedBadgesCount = unlockedBadges.size,
            totalBadgesCount = userData.badges.size.coerceAtLeast(12),
            lastBadge = unlockedBadges.maxByOrNull { it.unlockedAt ?: 0L },
            nextActivity = exploreData.activities.firstOrNull(),
            topColleges = exploreData.colleges.take(3),
            confidenceTrend = listOf(68f, 72f, 70f, 75f, 79f, userData.confidence.overallScore.toFloat())
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState()
    )
}
