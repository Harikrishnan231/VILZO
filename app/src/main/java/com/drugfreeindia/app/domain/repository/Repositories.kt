package com.drugfreeindia.app.domain.repository

import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.Centre
import com.drugfreeindia.app.domain.model.CollegeStat
import com.drugfreeindia.app.domain.model.ConfidenceScore
import com.drugfreeindia.app.domain.model.Event
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.domain.model.Story
import com.drugfreeindia.app.domain.model.StoryProgress
import com.drugfreeindia.app.domain.model.Streak
import com.drugfreeindia.app.domain.model.SurveyQuestion
import com.drugfreeindia.app.domain.model.TrustedContact
import com.drugfreeindia.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    fun getAllStories(): Flow<List<Story>>
    suspend fun getStoryById(id: String): Story?
    fun getAllProgress(): Flow<List<StoryProgress>>
    suspend fun recordStoryResult(storyId: String, score: Int, tag: String): ConfidenceScore
}

interface SupportRepository {
    fun getAllCentres(): Flow<List<Centre>>
    suspend fun getCentreById(id: String): Centre?
    fun getAllHelplines(): Flow<List<Helpline>>
    fun getTrustedContacts(): Flow<List<TrustedContact>>
    suspend fun addTrustedContact(contact: TrustedContact)
    suspend fun deleteTrustedContact(id: String)
}

interface ExploreRepository {
    fun getActivities(): Flow<List<Activity>>
    suspend fun toggleActivityJoin(id: String, isJoined: Boolean)
    fun getEvents(): Flow<List<Event>>
    suspend fun toggleEventReminder(id: String, isSet: Boolean)
    fun getMentors(): Flow<List<Mentor>>
    suspend fun requestMentorChat(mentorId: String, note: String)
}

interface ImpactRepository {
    fun getSurveyQuestions(): Flow<List<SurveyQuestion>>
    suspend fun submitSurveyAnswer(monthKey: String, questionId: String, answer: String)
    fun getCollegeStats(): Flow<List<CollegeStat>>
    fun getConfidenceScore(): Flow<ConfidenceScore>
}

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)
    fun getStreak(): Flow<Streak>
    suspend fun updateDailyStreak()
    fun getBadges(): Flow<List<Badge>>
    suspend fun unlockBadge(badgeId: String)
    suspend fun clearAllData()
}
