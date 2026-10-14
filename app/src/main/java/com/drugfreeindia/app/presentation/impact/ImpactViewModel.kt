package com.drugfreeindia.app.presentation.impact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.CollegeStat
import com.drugfreeindia.app.domain.model.ConfidenceScore
import com.drugfreeindia.app.domain.model.StoryProgress
import com.drugfreeindia.app.domain.model.Streak
import com.drugfreeindia.app.domain.model.SurveyQuestion
import com.drugfreeindia.app.domain.repository.ExploreRepository
import com.drugfreeindia.app.domain.repository.ImpactRepository
import com.drugfreeindia.app.domain.repository.StoryRepository
import com.drugfreeindia.app.domain.repository.UserRepository
import com.drugfreeindia.app.domain.usecase.SubmitSurveyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ImpactUiState(
    val selectedSegment: Int = 0, // 0: My Progress, 1: Community View, 2: Institution View
    val confidence: ConfidenceScore = ConfidenceScore(84, 88, 80, 82, 6),
    val streak: Streak = Streak(4, 6, "2026-10-13"),
    val storiesCompletedCount: Int = 2,
    val activitiesJoinedCount: Int = 1,
    val collegeStats: List<CollegeStat> = emptyList(),
    val confidenceTrend: List<Float> = listOf(68f, 72f, 70f, 75f, 79f, 84f),
    val isSurveyCompleted: Boolean = false,
    val surveyQuestions: List<SurveyQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val cohortSize: Int = 640 // Demo sample cohort size (>= 20)
)

private data class ImpactMetrics(
    val confidence: ConfidenceScore,
    val streak: Streak,
    val progressList: List<StoryProgress>
)

private data class ImpactCommunityData(
    val activities: List<Activity>,
    val colleges: List<CollegeStat>,
    val questions: List<SurveyQuestion>
)

private data class ImpactSurveySession(
    val selectedAnswers: Map<String, String>,
    val currentQuestionIndex: Int,
    val isCompleted: Boolean
)

@HiltViewModel
class ImpactViewModel @Inject constructor(
    private val impactRepository: ImpactRepository,
    private val storyRepository: StoryRepository,
    private val exploreRepository: ExploreRepository,
    private val userRepository: UserRepository,
    private val submitSurveyUseCase: SubmitSurveyUseCase
) : ViewModel() {

    private val _selectedSegment = MutableStateFlow(0)
    private val _currentQuestionIndex = MutableStateFlow(0)
    private val _selectedAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _isSurveyCompleted = MutableStateFlow(false)

    private val metricsFlow = combine(
        impactRepository.getConfidenceScore(),
        userRepository.getStreak(),
        storyRepository.getAllProgress()
    ) { confidence, streak, progressList ->
        ImpactMetrics(confidence, streak, progressList)
    }

    private val communityFlow = combine(
        exploreRepository.getActivities(),
        impactRepository.getCollegeStats(),
        impactRepository.getSurveyQuestions()
    ) { activities, colleges, questions ->
        ImpactCommunityData(activities, colleges, questions)
    }

    private val surveySessionFlow = combine(
        _selectedAnswers,
        _currentQuestionIndex,
        _isSurveyCompleted
    ) { answers, qIndex, isDone ->
        ImpactSurveySession(answers, qIndex, isDone)
    }

    val uiState: StateFlow<ImpactUiState> = combine(
        _selectedSegment,
        metricsFlow,
        communityFlow,
        surveySessionFlow
    ) { segment, metrics, community, session ->
        ImpactUiState(
            selectedSegment = segment,
            confidence = metrics.confidence,
            streak = metrics.streak,
            storiesCompletedCount = metrics.progressList.size,
            activitiesJoinedCount = community.activities.count { it.isJoined },
            collegeStats = community.colleges,
            confidenceTrend = listOf(68f, 72f, 70f, 75f, 79f, metrics.confidence.overallScore.toFloat()),
            isSurveyCompleted = session.isCompleted,
            surveyQuestions = community.questions.take(4),
            currentQuestionIndex = session.currentQuestionIndex,
            selectedAnswers = session.selectedAnswers
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImpactUiState()
    )

    fun onSegmentSelected(segment: Int) {
        _selectedSegment.value = segment
    }

    fun onSelectAnswer(questionId: String, answer: String) {
        val current = _selectedAnswers.value.toMutableMap()
        current[questionId] = answer
        _selectedAnswers.value = current
    }

    fun onNextQuestion() {
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx < 3) {
            _currentQuestionIndex.value = currentIdx + 1
        }
    }

    fun onPreviousQuestion() {
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx > 0) {
            _currentQuestionIndex.value = currentIdx - 1
        }
    }

    fun submitSurvey() {
        viewModelScope.launch {
            val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            _selectedAnswers.value.forEach { (qId, ans) ->
                submitSurveyUseCase(monthKey, qId, ans)
            }
            _isSurveyCompleted.value = true
        }
    }

    fun resetSurveyFlow() {
        _isSurveyCompleted.value = false
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
    }

    fun exportInstitutionCsv(): String {
        val builder = StringBuilder("Rank,CollegeCode,CollegeName,Participants,Points\n")
        uiState.value.collegeStats.forEach {
            builder.append("${it.rank},${it.collegeCode},\"${it.collegeName}\",${it.participants},${it.points}\n")
        }
        return builder.toString()
    }
}
