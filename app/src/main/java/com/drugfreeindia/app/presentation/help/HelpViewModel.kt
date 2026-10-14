package com.drugfreeindia.app.presentation.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.domain.model.Centre
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.TrustedContact
import com.drugfreeindia.app.domain.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class HelpUiState(
    val trustedContacts: List<TrustedContact> = emptyList(),
    val helplines: List<Helpline> = emptyList(),
    val centres: List<Centre> = emptyList(),
    val selectedFilter: String = "All", // All, Government, Counselling, De-addiction
    val searchQuery: String = ""
)

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val supportRepository: SupportRepository
) : ViewModel() {

    private val _filter = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HelpUiState> = combine(
        supportRepository.getTrustedContacts(),
        supportRepository.getAllHelplines(),
        supportRepository.getAllCentres(),
        _filter,
        _searchQuery
    ) { contacts, helplines, centres, filter, query ->
        val filteredCentres = centres.filter { centre ->
            val matchesFilter = when (filter) {
                "Government" -> centre.type.contains("Government", ignoreCase = true)
                "Counselling" -> centre.type.contains("Counselling", ignoreCase = true)
                "De-addiction" -> centre.type.contains("De-addiction", ignoreCase = true)
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    centre.name.contains(query, ignoreCase = true) ||
                    centre.city.contains(query, ignoreCase = true) ||
                    centre.state.contains(query, ignoreCase = true) ||
                    centre.pincode.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }

        HelpUiState(
            trustedContacts = contacts,
            helplines = helplines,
            centres = filteredCentres,
            selectedFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HelpUiState()
    )

    fun onFilterSelected(filter: String) {
        _filter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addContact(name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            val contact = TrustedContact(
                id = UUID.randomUUID().toString(),
                name = name,
                phone = phone,
                relationship = relationship
            )
            supportRepository.addTrustedContact(contact)
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            supportRepository.deleteTrustedContact(id)
        }
    }
}
