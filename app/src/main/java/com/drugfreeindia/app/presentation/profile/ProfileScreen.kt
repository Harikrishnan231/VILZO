package com.drugfreeindia.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.data.local.AppPrefs
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.data.repository.AccountInfo
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.LocalDataWiper
import com.drugfreeindia.app.data.repository.PlayRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.domain.model.AppConfig
import com.drugfreeindia.app.domain.model.Badge
import com.drugfreeindia.app.domain.model.MyStats
import com.drugfreeindia.app.domain.model.Profile
import com.drugfreeindia.app.presentation.components.AvatarInitials
import com.drugfreeindia.app.presentation.components.ConfirmDialog
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.Divider
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.LinkButton
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.SwitchRow
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.Format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

data class ProfileUiState(
    val account: AccountInfo? = null,
    val profile: Profile? = null,
    val cityName: String? = null,
    val collegeName: String? = null,
    val phoneVerified: Boolean? = null,
    val stats: MyStats? = null,
    val config: AppConfig = AppConfig(),
    val badges: List<Badge> = emptyList(),
    val earned: Set<String> = emptySet(),
    val busy: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val content: ContentRepository,
    private val play: PlayRepository,
    private val prefsStore: PrefsStore,
    private val wiper: LocalDataWiper
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()
    val prefs: StateFlow<AppPrefs> = prefsStore.prefs.stateIn(viewModelScope, SharingStarted.Eagerly, AppPrefs())
    private val _signedOut = Channel<Unit>(Channel.BUFFERED)
    val signedOut = _signedOut.receiveAsFlow()

    fun refresh() = viewModelScope.launch {
        val account = auth.account()
        val p = if (account != null) profiles.refresh().getOrNull() ?: profiles.profile.value else null
        val cities = content.cities().data
        val city = cities.firstOrNull { it.id == p?.cityId } ?: cities.firstOrNull { it.slug == prefsStore.current().citySlug }
        val collegeName = p?.customCollegeName ?: p?.collegeId?.let { id -> city?.let { content.colleges(it.id).data.firstOrNull { c -> c.id == id }?.name } }
        val verified = if (account?.phone != null) profiles.isPhoneVerified().getOrNull() else null
        _state.update {
            it.copy(account = account, profile = p, cityName = city?.name, collegeName = collegeName, phoneVerified = verified,
                stats = play.stats()?.data, config = content.config().data, badges = content.badges().data,
                earned = play.myBadges()?.data?.map { b -> b.badgeId }?.toSet().orEmpty())
        }
    }

    fun setName(name: String) = viewModelScope.launch {
        profiles.update(buildJsonObject { put("full_name", name.trim()) })
            .onSuccess { refresh() }.onFailure { e -> _state.update { it.copy(message = e.toAppError().message) } }
    }

    fun setIncludeStats(v: Boolean) = viewModelScope.launch {
        _state.update { it.copy(profile = it.profile?.copy(includeInCollegeStats = v)) }
        profiles.update(buildJsonObject { put("include_in_college_stats", v) })
            .onFailure { e -> _state.update { it.copy(profile = it.profile?.copy(includeInCollegeStats = !v), message = e.toAppError().message) } }
    }

    fun setReduceMotion(v: Boolean) = viewModelScope.launch { prefsStore.setReduceMotion(v) }
    fun setRelaxed(v: Boolean) = viewModelScope.launch { prefsStore.setRelaxedMode(v) }
    fun setLanguage(code: String) = viewModelScope.launch {
        prefsStore.setLanguage(code)
        if (auth.userId != null) profiles.update(buildJsonObject { put("language", code) })
    }

    fun signOut() = viewModelScope.launch {
        _state.update { it.copy(busy = true) }
        auth.signOut()
        wiper.wipeUserData(includeContacts = false)
        _state.update { it.copy(busy = false) }
        _signedOut.send(Unit)
    }

    fun deleteAccount() = viewModelScope.launch {
        _state.update { it.copy(busy = true, message = null) }
        auth.deleteAccount()
            .onSuccess {
                wiper.wipeUserData(includeContacts = true)
                _state.update { it.copy(busy = false) }
                _signedOut.send(Unit)
            }
            .onFailure { e -> _state.update { it.copy(busy = false, message = e.toAppError().message) } }
    }

    /** Guests: clears device practice data and returns to Welcome. */
    fun resetDevice() = viewModelScope.launch {
        wiper.wipeUserData(includeContacts = true)
        _signedOut.send(Unit)
    }
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditLocation: () -> Unit,
    onContacts: () -> Unit,
    onPrivacy: () -> Unit,
    onSignIn: () -> Unit,
    onSignedOut: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val prefs by vm.prefs.collectAsState()
    val c = DfiTheme.colors
    var editName by remember { mutableStateOf(false) }
    var confirmSignOut by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.refresh() }
    LaunchedEffect(Unit) { vm.signedOut.collect { onSignedOut() } }
    val signedIn = s.account != null

    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Profile", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding()) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarInitials(s.profile?.fullName, 64.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.profile?.fullName ?: if (signedIn) "Add your name" else "Guest", style = DfiTheme.typography.sectionTitle, color = c.onSurface)
                    Text(listOfNotNull(s.collegeName, s.cityName).joinToString(" · ").ifEmpty { "No college or city set" },
                        style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                }
            }
            val st = s.stats
            if (signedIn && st != null) {
                Spacer(Modifier.height(16.dp))
                val lvl = s.config.levelFor(st.totalPoints)
                val next = s.config.nextLevel(st.totalPoints)
                DfiCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Level ${lvl.level} · ${lvl.name}", style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f))
                        Text("${Format.number(st.totalPoints)} pts", style = DfiTheme.typography.labelSmall, color = c.primaryDark)
                    }
                    if (next != null) {
                        val span = (next.minPoints - lvl.minPoints).coerceAtLeast(1)
                        val prog = ((st.totalPoints - lvl.minPoints) / span.toFloat()).coerceIn(0f, 1f)
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().height(8.dp).clip(DfiTheme.shapes.pill).background(c.surfaceMuted)
                            .semantics { contentDescription = "${next.minPoints - st.totalPoints} points to ${next.name}" }) {
                            Box(Modifier.fillMaxWidth(prog).height(8.dp).clip(DfiTheme.shapes.pill).background(c.primary))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${Format.number(next.minPoints - st.totalPoints)} points to ${next.name}", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        MiniStat("${st.streakDays}", "day streak", Modifier.weight(1f))
                        MiniStat("${st.badgesEarned}", "badges", Modifier.weight(1f))
                        MiniStat("${st.scenariosCompleted}/${st.scenariosTotal}", "scenarios", Modifier.weight(1f))
                    }
                }
            }
            // Achievements
            if (s.badges.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Group("Achievements")
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    s.badges.forEach { b ->
                        val earned = b.id in s.earned
                        val locked = b.id == "team_player"
                        Column(Modifier.width(88.dp).semantics(mergeDescendants = true) {}, horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(52.dp).clip(CircleShape).background(if (earned) c.primarySoft else c.surfaceMuted), contentAlignment = Alignment.Center) {
                                Icon(if (locked) Icons.Rounded.Lock else if (earned) Icons.Rounded.EmojiEvents else Icons.Rounded.Star, null,
                                    tint = if (earned) c.primaryDark else c.disabled)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(b.name, style = DfiTheme.typography.caption, color = c.onSurface, textAlign = TextAlign.Center, maxLines = 2)
                            Text(if (locked) "Coming with multiplayer" else if (earned) "Earned" else "Locked", style = DfiTheme.typography.caption,
                                color = c.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2)
                        }
                    }
                }
            }
            // Account
            Spacer(Modifier.height(20.dp))
            Group("Account")
            DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
                if (!signedIn) {
                    ListRow("Sign in or create an account", subtitle = "Keep your progress and badges", onClick = onSignIn)
                } else {
                    ListRow("Name", subtitle = s.profile?.fullName ?: "Not set", onClick = { editName = true })
                    s.account?.phone?.let { ph ->
                        Divider()
                        ListRow("Phone", subtitle = ph, trailing = {
                            when (s.phoneVerified) {
                                true -> Tag("Verified", color = c.success)
                                false -> Tag("Not verified", color = c.warning)
                                null -> Tag("Status unavailable")
                            }
                        })
                    }
                    s.account?.email?.let { em -> Divider(); ListRow("Email", subtitle = em, trailing = null) }
                    Divider()
                    ListRow("Google", subtitle = if ("google" in s.account!!.providers) "Connected" else "Not connected", trailing = null)
                }
            }
            // College & location
            Spacer(Modifier.height(20.dp))
            Group("College & location")
            DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
                ListRow("Change city or college", subtitle = listOfNotNull(s.cityName, s.collegeName).joinToString(" · ").ifEmpty { null }, onClick = onEditLocation)
            }
            // Support & privacy
            Spacer(Modifier.height(20.dp))
            Group("Support & privacy")
            DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
                ListRow("Trusted contacts", subtitle = "Saved only on this phone", onClick = onContacts)
                Divider()
                ListRow("Privacy information", onClick = onPrivacy)
                if (signedIn) {
                    Divider()
                    SwitchRow("Include my college in aggregate stats", "Counts you in totals only. Nobody sees your answers.",
                        s.profile?.includeInCollegeStats ?: true, vm::setIncludeStats)
                }
            }
            // Preferences
            Spacer(Modifier.height(20.dp))
            Group("Preferences")
            DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Language", style = DfiTheme.typography.cardTitle, color = c.onSurface)
                    Spacer(Modifier.height(8.dp))
                    SegmentedTabs(listOf("English", "हिन्दी"), if (prefs.language == "hi") 1 else 0, { vm.setLanguage(if (it == 1) "hi" else "en") })
                    Spacer(Modifier.height(4.dp))
                    Text("Navigation and key labels are translated; more screens are being translated.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                }
                Divider()
                SwitchRow("Relaxed mode", "No timer in scenarios", prefs.relaxedMode, vm::setRelaxed)
                Divider()
                SwitchRow("Reduce motion", "Fewer animations", prefs.reduceMotion, vm::setReduceMotion)
            }
            s.message?.let { Spacer(Modifier.height(12.dp)); InfoNote(it) }
            // Actions
            Spacer(Modifier.height(24.dp))
            if (signedIn) {
                SecondaryButton("Sign out", { confirmSignOut = true }, Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                SecondaryButton("Delete account", { confirmDelete = true }, Modifier.fillMaxWidth(), contentColor = c.error)
            } else {
                SecondaryButton("Clear data on this phone", { confirmReset = true }, Modifier.fillMaxWidth(), contentColor = c.error)
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (editName) {
        var name by remember { mutableStateOf(s.profile?.fullName.orEmpty()) }
        ConfirmDialog("Your name", "Shown only to you in the app.", "Save", confirmEnabled = name.trim().length >= 2,
            onConfirm = { vm.setName(name); editName = false }, onDismiss = { editName = false },
            extra = { DfiTextField(name, { name = it.take(60) }, "Full name") })
    }
    if (confirmSignOut) ConfirmDialog("Sign out?", "Your progress stays in your account. Trusted contacts stay on this phone.", "Sign out",
        onConfirm = { confirmSignOut = false; vm.signOut() }, onDismiss = { confirmSignOut = false })
    if (confirmReset) ConfirmDialog("Clear data on this phone?", "This removes practice results, trusted contacts and settings from this phone.", "Clear",
        destructive = true, onConfirm = { confirmReset = false; vm.resetDevice() }, onDismiss = { confirmReset = false })
    if (confirmDelete) {
        var typed by remember { mutableStateOf("") }
        ConfirmDialog(
            "Delete account?",
            "This permanently deletes your account, profile, scenario history, badges, activity sign-ups and submissions from the server, and clears this phone. Anonymous survey answers can't be linked to you and stay in totals. This can't be undone.",
            if (s.busy) "Deleting…" else "Delete", destructive = true, confirmEnabled = typed == "DELETE" && !s.busy,
            onConfirm = { vm.deleteAccount(); confirmDelete = false }, onDismiss = { confirmDelete = false },
            extra = { DfiTextField(typed, { typed = it.trim() }, "Type DELETE to confirm") })
    }
}

@Composable
private fun Group(title: String) {
    Text(title.uppercase(), style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
}

@Composable
private fun MiniStat(value: String, label: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = DfiTheme.typography.cardTitle, color = DfiTheme.colors.onSurface)
        Text(label, style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant)
    }
}
