package com.drugfreeindia.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.DfiDataStore
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.UserProfile
import com.drugfreeindia.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val nickname: String = "Calm Falcon",
    val collegeCode: String = "PUDU-NIT",
    val selectedLanguage: String = "en",
    val isReduceMotion: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val badges: List<Badge> = emptyList(),
    val unlockedBadgesCount: Int = 3,
    val levelName: String = "Confident"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStore: DfiDataStore
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.getUserProfile(),
        dataStore.preferredLanguage,
        dataStore.isReduceMotion,
        dataStore.isNotificationsEnabled,
        userRepository.getBadges()
    ) { profile, lang, reduceMotion, notifs, badges ->
        val unlockedCount = badges.count { it.isUnlocked }
        val level = when {
            unlockedCount >= 6 -> "Champion"
            unlockedCount >= 3 -> "Confident"
            unlockedCount >= 1 -> "Steady"
            else -> "Starter"
        }
        ProfileUiState(
            userProfile = profile,
            nickname = profile?.nickname ?: "Calm Falcon",
            collegeCode = profile?.collegeCode ?: "PUDU-NIT",
            selectedLanguage = lang,
            isReduceMotion = reduceMotion,
            isNotificationsEnabled = notifs,
            badges = badges,
            unlockedBadgesCount = unlockedCount,
            levelName = level
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ProfileUiState()
    )

    fun onLanguageSelected(lang: String) {
        viewModelScope.launch {
            dataStore.setLanguage(lang)
            val current = uiState.value.userProfile
            if (current != null) {
                userRepository.saveUserProfile(current.copy(language = lang))
            }
        }
    }

    fun onToggleReduceMotion(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setReduceMotion(enabled)
        }
    }

    fun onToggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setNotificationsEnabled(enabled)
        }
    }

    fun deleteAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.clearAllData()
            onComplete()
        }
    }
}
