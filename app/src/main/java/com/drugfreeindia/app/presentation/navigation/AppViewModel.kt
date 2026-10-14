package com.drugfreeindia.app.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.AppPrefs
import com.drugfreeindia.app.data.local.Connectivity
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.AuthState
import com.drugfreeindia.app.data.repository.PlayRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.data.repository.TrustedContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

object Routes {
    const val WELCOME = "welcome"
    const val AUTH = "auth?signIn={signIn}"
    fun auth(signIn: Boolean) = "auth?signIn=$signIn"
    const val OTP = "otp"
    const val LOCATION = "location?edit={edit}"
    fun location(edit: Boolean = false) = "location?edit=$edit"

    const val HOME = "home"
    const val PLAY = "play"
    const val HELP = "help"
    const val EXPLORE = "explore"
    const val IMPACT = "impact"
    val tabs = listOf(HOME, PLAY, HELP, EXPLORE, IMPACT)

    const val STORY = "story/{id}"
    fun story(id: String) = "story/$id"
    const val PROFILE = "profile"
    const val CONTACTS = "help/contacts"
    const val HELPLINES = "help/helplines"
    const val CENTRES = "help/centres"
    const val HELP_FRIEND = "help/friend"
    const val PRIVACY = "help/privacy"
    const val QUICK_EXIT = "planner"
    const val ACTIVITY = "explore/activity/{id}"
    fun activity(id: String) = "explore/activity/$id"
    const val SUBMIT_ACTIVITY = "explore/submit"
    const val MY_SUBMISSIONS = "explore/mine"
    const val SURVEY = "impact/survey"
    const val LEGAL = "legal/{kind}"
    fun legal(kind: String) = "legal/$kind"
}

/** Decides the first screen and owns app-wide flags (reduce motion, connectivity, sync). */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val prefsStore: PrefsStore,
    private val contacts: TrustedContactsRepository,
    private val play: PlayRepository,
    private val content: com.drugfreeindia.app.data.repository.ContentRepository,
    connectivity: Connectivity
) : ViewModel() {
    private val _config = MutableStateFlow(com.drugfreeindia.app.domain.model.AppConfig())
    val config: StateFlow<com.drugfreeindia.app.domain.model.AppConfig> = _config.asStateFlow()
    private val _start = MutableStateFlow<String?>(null)
    val start: StateFlow<String?> = _start.asStateFlow()

    val prefs: StateFlow<AppPrefs> = prefsStore.prefs.stateIn(viewModelScope, SharingStarted.Eagerly, AppPrefs())
    val online: StateFlow<Boolean> = connectivity.isOnline

    init {
        viewModelScope.launch {
            runCatching { contacts.migrateLegacy() }
            val state = withTimeoutOrNull(3000) { auth.state.filter { it != AuthState.LOADING }.first() } ?: AuthState.SIGNED_OUT
            val p = prefsStore.current()
            _start.value = when {
                state == AuthState.SIGNED_IN -> {
                    val profile = withTimeoutOrNull(4000) { profiles.refresh().getOrNull() }
                    // Offline with a session: go home and let the user finish setup later.
                    if (profile == null || profile.onboardingCompleted || p.welcomeSeen) Routes.HOME else Routes.location()
                }
                p.welcomeSeen -> Routes.HOME
                else -> Routes.WELCOME
            }
        }
        // Replay queued answers whenever we come back online.
        viewModelScope.launch {
            connectivity.isOnline.collect { if (it) runCatching { play.syncPending() } }
        }
        viewModelScope.launch { runCatching { _config.value = content.config().data } }
    }
}
