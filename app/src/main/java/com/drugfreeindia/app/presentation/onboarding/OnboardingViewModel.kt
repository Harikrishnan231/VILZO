package com.drugfreeindia.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.DfiDataStore
import com.drugfreeindia.app.domain.model.UserProfile
import com.drugfreeindia.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val selectedLanguage: String = "en",
    val nickname: String = "Calm Falcon",
    val collegeCode: String = "",
    val suggestedNicknames: List<String> = listOf("Calm Falcon", "Steady Phoenix", "Brave Otter", "Mindful Deer", "Solar Hawk")
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStore: DfiDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNextPage() {
        if (_uiState.value.currentPage < 3) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    fun onLanguageSelected(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun onNicknameSelected(name: String) {
        _uiState.update { it.copy(nickname = name) }
    }

    fun onCollegeCodeChanged(code: String) {
        _uiState.update { it.copy(collegeCode = code.uppercase()) }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val anonId = "usr_" + UUID.randomUUID().toString().take(8)
            val profile = UserProfile(
                anonId = anonId,
                nickname = _uiState.value.nickname,
                collegeCode = _uiState.value.collegeCode.ifBlank { null },
                language = _uiState.value.selectedLanguage
            )
            userRepository.saveUserProfile(profile)
            dataStore.setOnboardingCompleted(true)
            onSuccess()
        }
    }
}
