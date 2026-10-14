package com.drugfreeindia.app.domain.usecase

import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Centre
import com.drugfreeindia.app.domain.model.ConfidenceScore
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.repository.ExploreRepository
import com.drugfreeindia.app.domain.repository.ImpactRepository
import com.drugfreeindia.app.domain.repository.StoryRepository
import com.drugfreeindia.app.domain.repository.SupportRepository
import com.drugfreeindia.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayStoryUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(storyId: String): Story? {
        return storyRepository.getStoryById(storyId)
    }
}

class SubmitChoiceUseCase @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(storyId: String, score: Int, tag: String, badgeEarned: String?): ConfidenceScore {
        val result = storyRepository.recordStoryResult(storyId, score, tag)
        userRepository.updateDailyStreak()
        if (badgeEarned != null) {
            userRepository.unlockBadge(badgeEarned)
        }
        return result
    }
}

class ComputeConfidenceScoreUseCase @Inject constructor(
    private val impactRepository: ImpactRepository
) {
    operator fun invoke(): Flow<ConfidenceScore> {
        return impactRepository.getConfidenceScore()
    }
}

class UpdateStreakUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.updateDailyStreak()
    }
}

class UnlockBadgesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(badgeId: String) {
        userRepository.unlockBadge(badgeId)
    }
}

class SubmitSurveyUseCase @Inject constructor(
    private val impactRepository: ImpactRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(monthKey: String, questionId: String, answer: String) {
        impactRepository.submitSurveyAnswer(monthKey, questionId, answer)
        userRepository.unlockBadge("community_voice")
    }
}

class JoinActivityUseCase @Inject constructor(
    private val exploreRepository: ExploreRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(activityId: String, isJoined: Boolean) {
        exploreRepository.toggleActivityJoin(activityId, isJoined)
        if (isJoined) {
            userRepository.unlockBadge("sports_star")
        }
    }
}

class FindCentresUseCase @Inject constructor(
    private val supportRepository: SupportRepository
) {
    operator fun invoke(): Flow<List<Centre>> {
        return supportRepository.getAllCentres()
    }
}
