package com.drugfreeindia.app.presentation.help

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.Source
import com.drugfreeindia.app.data.repository.TrustedContactsRepository
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.SupportCentre
import com.drugfreeindia.app.domain.model.TrustedContact
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

val centreTypes = linkedMapOf(
    "government" to "Government",
    "counselling" to "Counselling",
    "de_addiction" to "De-addiction",
    "hospital" to "Hospital",
    "ngo" to "NGO"
)

data class HelpUiState(
    val loading: Boolean = true,
    val helplines: List<Helpline> = emptyList(),
    val helplineSource: Source = Source.BUNDLED,
    val centres: List<SupportCentre> = emptyList(),
    val centreSource: Source = Source.BUNDLED,
    val query: String = "",
    val type: String? = null,
    val citySlug: String? = null,
    val selected: SupportCentre? = null,
    val showMap: Boolean = false
) {
    val filteredCentres: List<SupportCentre> get() = centres.filter { c ->
        (type == null || c.type == type) &&
            (query.isBlank() || listOfNotNull(c.name, c.cityName, c.address).any { it.contains(query.trim(), ignoreCase = true) })
    }
}

/** Help works without an account: everything here is public reference data or device-only. */
@HiltViewModel
class HelpViewModel @Inject constructor(
    private val content: ContentRepository,
    private val contactsRepo: TrustedContactsRepository,
    private val prefs: PrefsStore
) : ViewModel() {
    private val _state = MutableStateFlow(HelpUiState())
    val state: StateFlow<HelpUiState> = _state.asStateFlow()
    val contacts: StateFlow<List<TrustedContact>> = contactsRepo.contacts
    val encryptionAvailable get() = contactsRepo.isEncryptionAvailable

    init { load() }

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        val h = content.helplines()
        val c = content.centres()
        _state.update {
            it.copy(loading = false, helplines = h.data.sortedBy { x -> x.sortOrder }, helplineSource = h.source,
                centres = c.data, centreSource = c.source, citySlug = prefs.current().citySlug)
        }
    }

    fun onQuery(q: String) = _state.update { it.copy(query = q) }
    fun onType(t: String?) = _state.update { it.copy(type = t?.let { label -> centreTypes.entries.firstOrNull { e -> e.value == label }?.key }) }
    fun select(c: SupportCentre?) = _state.update { it.copy(selected = c) }
    fun toggleMap(v: Boolean) = _state.update { it.copy(showMap = v) }

    fun saveContact(c: TrustedContact): Boolean = contactsRepo.save(c)
    fun deleteContact(id: String) = contactsRepo.delete(id)
}

/** Loads a bundled markdown document from assets off the main thread. */
@HiltViewModel
class DocViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {
    private val _text = MutableStateFlow<String?>(null)
    val text: StateFlow<String?> = _text.asStateFlow()
    fun load(asset: String) = viewModelScope.launch {
        _text.value = withContext(Dispatchers.IO) { runCatching { context.assets.open(asset).bufferedReader().use { it.readText() } }.getOrNull() ?: "" }
    }
}
