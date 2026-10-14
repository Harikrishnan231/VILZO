package com.drugfreeindia.app.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.Event
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.domain.repository.ExploreRepository
import com.drugfreeindia.app.domain.usecase.JoinActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val selectedTab: Int = 0, // 0: Activities, 1: Events, 2: Mentors
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val activities: List<Activity> = emptyList(),
    val events: List<Event> = emptyList(),
    val mentors: List<Mentor> = emptyList(),
    val requestedMentorId: String? = null
)

private data class FilterState(
    val tab: Int,
    val category: String,
    val query: String
)

private data class ExploreRawData(
    val activities: List<Activity>,
    val events: List<Event>,
    val mentors: List<Mentor>
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository,
    private val joinActivityUseCase: JoinActivityUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _requestedMentorId = MutableStateFlow<String?>(null)

    private val filterStateFlow = combine(
        _selectedTab,
        _selectedCategory,
        _searchQuery
    ) { tab, category, query ->
        FilterState(tab, category, query)
    }

    private val rawDataFlow = combine(
        exploreRepository.getActivities(),
        exploreRepository.getEvents(),
        exploreRepository.getMentors()
    ) { activities, events, mentors ->
        ExploreRawData(activities, events, mentors)
    }

    val uiState: StateFlow<ExploreUiState> = combine(
        filterStateFlow,
        rawDataFlow,
        _requestedMentorId
    ) { filters, raw, mentorId ->
        val filteredActivities = raw.activities.filter { act ->
            val matchCat = filters.category == "All" || act.category.equals(filters.category, ignoreCase = true)
            val matchQuery = filters.query.isBlank() ||
                    act.title.contains(filters.query, ignoreCase = true) ||
                    act.category.contains(filters.query, ignoreCase = true) ||
                    act.location.contains(filters.query, ignoreCase = true)
            matchCat && matchQuery
        }

        val filteredEvents = raw.events.filter { ev ->
            filters.query.isBlank() ||
                    ev.title.contains(filters.query, ignoreCase = true) ||
                    ev.location.contains(filters.query, ignoreCase = true)
        }

        val filteredMentors = raw.mentors.filter { m ->
            filters.query.isBlank() ||
                    m.name.contains(filters.query, ignoreCase = true) ||
                    m.role.contains(filters.query, ignoreCase = true)
        }

        ExploreUiState(
            selectedTab = filters.tab,
            selectedCategory = filters.category,
            searchQuery = filters.query,
            activities = filteredActivities,
            events = filteredEvents,
            mentors = filteredMentors,
            requestedMentorId = mentorId
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ExploreUiState()
    )

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleActivityJoin(activityId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            joinActivityUseCase(activityId, !currentStatus)
        }
    }

    fun toggleEventReminder(eventId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            exploreRepository.toggleEventReminder(eventId, !currentStatus)
        }
    }

    fun requestMentorChat(mentorId: String) {
        viewModelScope.launch {
            _requestedMentorId.value = mentorId
            exploreRepository.requestMentorChat(mentorId, "Student requested introductory conversation.")
        }
    }
}
