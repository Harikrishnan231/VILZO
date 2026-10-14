package com.drugfreeindia.app.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.ExploreRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.data.repository.Source
import com.drugfreeindia.app.domain.model.Activity
import com.drugfreeindia.app.domain.model.ActivitySubmission
import com.drugfreeindia.app.domain.model.City
import com.drugfreeindia.app.domain.model.Mentor
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.AvatarInitials
import com.drugfreeindia.app.presentation.components.ChipRow
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.EmptyState
import com.drugfreeindia.app.presentation.components.ErrorState
import com.drugfreeindia.app.presentation.components.FallbackArt
import com.drugfreeindia.app.presentation.components.FilterPill
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.ScreenTitle
import com.drugfreeindia.app.presentation.components.SearchField
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SectionHeader
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.Format
import com.drugfreeindia.app.presentation.util.Intents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val activityCategories = linkedMapOf(
    "sports" to "Sports", "art" to "Arts", "music" to "Music", "dance" to "Dance", "poetry" to "Poetry",
    "volunteering" to "Volunteering", "fitness" to "Fitness", "skills" to "Skills", "other" to "Other"
)

// ============================================================== ViewModel

data class ExploreUiState(
    val loading: Boolean = true,
    val signedIn: Boolean = false,
    val tab: Int = 0,
    val activities: List<Activity> = emptyList(),
    val source: Source = Source.BUNDLED,
    val error: String? = null,
    val mentors: List<Mentor> = emptyList(),
    val joined: Set<String> = emptySet(),
    val busy: String? = null,
    val query: String = "",
    val category: String? = null,
    val cities: List<City> = emptyList(),
    val cityId: String? = null,
    val message: String? = null,
    val mine: List<Activity>? = null,
    val mineError: String? = null,
    val submitting: Boolean = false
) {
    private fun base(kind: Set<String>) = activities.filter { a ->
        a.kind in kind && (category == null || a.category == category) && (cityId == null || a.cityId == cityId) &&
            (query.isBlank() || listOfNotNull(a.title, a.organiserName, a.venue, a.description).any { it.contains(query.trim(), true) })
    }
    val events get() = base(setOf("event")).filter { it.startsAt == null || Format.isUpcoming(it.startsAt) }
    val clubs get() = base(setOf("club", "activity", "volunteering"))
    val cityName get() = cities.firstOrNull { it.id == cityId }?.name
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repo: ExploreRepository,
    private val content: ContentRepository,
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val prefs: PrefsStore
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        val cities = content.cities().data
        val slug = prefs.current().citySlug
        val cityId = profiles.profile.value?.cityId ?: cities.firstOrNull { it.slug == slug }?.id
        val r = repo.activities()
        val joined = repo.mySignupIds()
        val mentors = repo.mentors()
        _state.update {
            it.copy(loading = false, signedIn = auth.userId != null, activities = r.data, source = r.source,
                error = if (r.data.isEmpty() && r.error != null && r.error.code !in setOf("not_set_up")) r.error.message else null,
                joined = joined, mentors = mentors, cities = cities, cityId = it.cityId ?: cityId?.takeUnless { id -> id.startsWith("local:") && r.data.none { a -> a.cityId == id } })
        }
    }

    fun setTab(i: Int) = _state.update { it.copy(tab = i) }
    fun coverUrl(a: Activity): String? = repo.coverUrl(a.coverImagePath)
    fun onQuery(q: String) = _state.update { it.copy(query = q) }
    fun onCategory(label: String?) = _state.update { it.copy(category = label?.let { l -> activityCategories.entries.firstOrNull { e -> e.value == l }?.key }) }
    fun onCity(id: String?) = _state.update { it.copy(cityId = id) }
    fun clearFilters() = _state.update { it.copy(query = "", category = null, cityId = null) }
    fun dismissMessage() = _state.update { it.copy(message = null) }

    fun toggleJoin(a: Activity) {
        if (auth.userId == null) { _state.update { it.copy(message = "Sign in to join activities.") }; return }
        if (_state.value.busy != null) return
        val joined = a.id in _state.value.joined
        _state.update { it.copy(busy = a.id) }
        viewModelScope.launch {
            val r = if (joined) repo.leave(a.id) else repo.join(a.id)
            r.onSuccess { _state.update { s -> s.copy(busy = null, joined = if (joined) s.joined - a.id else s.joined + a.id) } }
                .onFailure { e -> _state.update { it.copy(busy = null, message = e.toAppError().message) } }
        }
    }

    fun loadMine() = viewModelScope.launch {
        repo.mySubmissions().onSuccess { l -> _state.update { it.copy(mine = l, mineError = null) } }
            .onFailure { e -> _state.update { it.copy(mine = emptyList(), mineError = e.toAppError().message) } }
    }

    fun submit(form: SubmitForm, onDone: () -> Unit) {
        if (_state.value.submitting) return
        _state.update { it.copy(submitting = true, message = null) }
        viewModelScope.launch {
            repo.submit { uid ->
                ActivitySubmission(
                    kind = form.kind, category = form.category, title = form.title.trim(), description = form.description.trim().ifBlank { null },
                    organiserName = form.organiser.trim(), cityId = form.cityId?.takeUnless { it.startsWith("local:") }, venue = form.venue.trim().ifBlank { null },
                    startsAt = null, recurrenceText = form.when_.trim().ifBlank { null }, registrationUrl = form.link.trim().ifBlank { null },
                    contact = form.contact.trim().ifBlank { null }, createdBy = uid
                )
            }.onSuccess {
                _state.update { it.copy(submitting = false, message = "Thanks! Your listing is pending review.") }
                onDone()
            }.onFailure { e -> _state.update { it.copy(submitting = false, message = e.toAppError().message) } }
        }
    }
}

data class SubmitForm(
    val kind: String = "event", val category: String = "sports", val title: String = "", val organiser: String = "",
    val cityId: String? = null, val venue: String = "", val when_: String = "", val description: String = "",
    val link: String = "", val contact: String = ""
) {
    val valid get() = title.trim().length >= 4 && organiser.trim().length >= 2
}

// ============================================================== Explore landing

@Composable
fun ExploreScreen(
    contentPadding: PaddingValues,
    onActivity: (String) -> Unit,
    onSubmit: () -> Unit,
    onMine: () -> Unit,
    vm: ExploreViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    val tabs = buildList { add("Activities"); add("Events"); if (s.mentors.isNotEmpty()) add("Mentors") }
    var cityMenu by rememberSaveable { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().background(c.background), contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 24.dp)) {
        item {
            Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
                ScreenTitle("Explore & connect", "Discover healthy activities and opportunities near you.")
                Spacer(Modifier.height(16.dp))
                SegmentedTabs(tabs, s.tab.coerceAtMost(tabs.lastIndex), vm::setTab)
                Spacer(Modifier.height(12.dp))
                if (s.tab != 2) SearchField(s.query, vm::onQuery, "Search activities in your city")
                Spacer(Modifier.height(10.dp))
            }
        }
        if (s.tab != 2) {
            item { ChipRow(activityCategories.values.toList(), s.category?.let { activityCategories[it] }, vm::onCategory) }
            item {
                Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterPill(s.cityName ?: "All cities", s.cityId != null) { cityMenu = !cityMenu }
                    Spacer(Modifier.weight(1f))
                    if (s.signedIn) {
                        IconButton(onClick = onMine) { Icon(Icons.Rounded.Inbox, "My submissions", tint = c.onSurfaceVariant) }
                        IconButton(onClick = onSubmit) { Icon(Icons.Rounded.Add, "Submit an activity", tint = c.primaryDark) }
                    }
                }
                if (cityMenu) {
                    Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                        Column {
                            (listOf<City?>(null) + s.cities).chunked(3).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                    row.forEach { ct -> FilterPill(ct?.name ?: "All cities", s.cityId == ct?.id) { vm.onCity(ct?.id); cityMenu = false } }
                                }
                            }
                        }
                    }
                }
            }
        }
        s.message?.let { m -> item { InfoNote(m, Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) } }
        when {
            s.loading -> item { LoadingState() }
            s.error != null -> item { ErrorState(s.error!!, Modifier.padding(20.dp), onRetry = { vm.load() }) }
            s.tab == 2 -> items(s.mentors, key = { it.id }) { m -> MentorRow(m) }
            else -> {
                val list = if (s.tab == 0) s.clubs else s.events
                if (list.isEmpty()) item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        EmptyState(
                            if (s.activities.isEmpty()) "No activities found in your area yet." else "Nothing matches these filters.",
                            if (s.activities.isEmpty()) (if (isSupabaseConfigured) "When organisers publish activities, they'll appear here. You can also suggest one." else "Activities appear here once the server is set up.")
                            else "Try another category or city.",
                            imageKey = "empty_activities", fallback = FallbackArt.HILLS
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SecondaryButton("Change city", { cityMenu = true }, Modifier.weight(1f))
                            SecondaryButton("Clear filters", vm::clearFilters, Modifier.weight(1f))
                        }
                    }
                } else {
                    if (s.tab == 1) item { SectionHeader("Happening in ${s.cityName ?: "your city"}", Modifier.padding(horizontal = 20.dp)) }
                    else item { SectionHeader("Popular clubs", Modifier.padding(horizontal = 20.dp)) }
                    items(list, key = { it.id }) { a ->
                        if (s.tab == 1) EventCard(a, vm.coverUrl(a), joined = a.id in s.joined, onClick = { onActivity(a.id) })
                        else ClubRow(a, onClick = { onActivity(a.id) })
                    }
                }
            }
        }
        if (s.source != Source.REMOTE && !s.loading && s.activities.isNotEmpty()) item {
            InfoNote("Showing saved listings. Pull the latest when you're online.", Modifier.padding(20.dp))
        }
    }
}


@Composable
private fun EventCard(a: Activity, coverPath: String?, joined: Boolean, onClick: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(Modifier.padding(horizontal = 20.dp, vertical = 6.dp).fillMaxWidth(), onClick = onClick, padding = PaddingValues(0.dp)) {
        Box {
            CoverImage(a, coverPath, Modifier.fillMaxWidth().height(140.dp))
            Format.dayBadge(a.startsAt)?.let { (d, m) ->
                Column(Modifier.padding(12.dp).clip(DfiTheme.shapes.tile).background(c.surface).padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(d, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                    Text(m, style = DfiTheme.typography.caption, color = c.primaryDark)
                }
            }
        }
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(a.title, style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (a.isDemo) Tag("Demo")
                if (joined) { Spacer(Modifier.width(6.dp)); Tag("Joined", color = c.success) }
            }
            Text(a.organiserName, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Text(listOfNotNull(Format.dateTime(a.startsAt) ?: a.recurrenceText, a.venue).joinToString(" · "), style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        }
    }
}

@Composable
private fun CoverImage(a: Activity, path: String?, modifier: Modifier) {
    val url = path?.let { p -> if (p.startsWith("http")) p else null }
    if (url != null) AsyncImage(url, null, modifier, contentScale = ContentScale.Crop)
    else AppImage("cat_${a.category}", null, modifier, fallback = FallbackArt.HILLS)
}

@Composable
private fun ClubRow(a: Activity, onClick: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(Modifier.padding(horizontal = 20.dp, vertical = 5.dp).fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarInitials(a.title, 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.title, style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (a.isDemo) { Spacer(Modifier.width(6.dp)); Tag("Demo") }
                }
                Text(listOfNotNull(activityCategories[a.category], a.recurrenceText ?: a.venue).joinToString(" · "), style = DfiTheme.typography.bodySmall,
                    color = c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun MentorRow(m: Mentor) {
    val c = DfiTheme.colors
    DfiCard(Modifier.padding(horizontal = 20.dp, vertical = 5.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarInitials(m.displayName, 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(m.displayName, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                m.roleTitle?.let { Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) }
                m.focus?.takeIf { it.isNotEmpty() }?.let { Text(it.joinToString(" · "), style = DfiTheme.typography.caption, color = c.primaryDark) }
            }
        }
        m.bio?.let { Spacer(Modifier.height(8.dp)); Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurface) }
    }
}

// ============================================================== Detail

@Composable
fun ActivityDetailScreen(id: String, onBack: () -> Unit, vm: ExploreViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    val ctx = LocalContext.current
    val a = s.activities.firstOrNull { it.id == id }
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Activity", onBack)
        if (a == null) {
            if (s.loading) LoadingState() else ErrorState("This listing is no longer available.", Modifier.padding(20.dp))
            return@Column
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            CoverImage(a, vm.coverUrl(a), Modifier.fillMaxWidth().height(180.dp).clip(DfiTheme.shapes.hero))
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Tag(activityCategories[a.category] ?: a.category)
                Tag(a.kind.replaceFirstChar { it.uppercase() })
                if (a.isDemo) Tag("Demo", color = c.warning)
                if (a.isVerified) Tag("Verified organiser", color = c.success)
            }
            Spacer(Modifier.height(10.dp))
            Text(a.title, style = DfiTheme.typography.screenTitle, color = c.onSurface)
            Spacer(Modifier.height(12.dp))
            Detail(Icons.Rounded.Person, a.organiserName)
            (Format.dateTime(a.startsAt) ?: a.recurrenceText)?.let { Detail(if (a.startsAt != null) Icons.Rounded.Event else Icons.Rounded.Repeat, it) }
            a.venue?.let { Detail(Icons.Rounded.Place, it) }
            a.eligibility?.let { Detail(Icons.Rounded.CheckCircle, "Who can join: $it") }
            a.description?.let { Spacer(Modifier.height(12.dp)); Text(it, style = DfiTheme.typography.body, color = c.onSurface) }
            a.contact?.let { Spacer(Modifier.height(12.dp)); Text("Contact: $it", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) }
            a.registrationUrl?.let { url ->
                Spacer(Modifier.height(8.dp))
                SecondaryButton("Open registration link", { Intents.openUrl(ctx, url) }, icon = Icons.Rounded.Link)
            }
            s.message?.let { Spacer(Modifier.height(12.dp)); InfoNote(it) }
            Spacer(Modifier.height(24.dp))
        }
        Box(Modifier.padding(20.dp).navigationBarsPadding()) {
            val joined = a.id in s.joined
            if (joined) SecondaryButton("Joined · tap to leave", { vm.toggleJoin(a) }, Modifier.fillMaxWidth(), icon = Icons.Rounded.CheckCircle)
            else PrimaryButton("Join", { vm.toggleJoin(a) }, Modifier.fillMaxWidth(), loading = s.busy == a.id)
        }
    }
}

@Composable
private fun Detail(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurface)
    }
}

// ============================================================== Submit & my submissions

@Composable
fun SubmitActivityScreen(onBack: () -> Unit, onDone: () -> Unit, vm: ExploreViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    var f by androidx.compose.runtime.remember { mutableStateOf(SubmitForm()) }
    Column(Modifier.fillMaxSize().background(c.background).imePadding()) {
        TopBar("Submit an activity", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            InfoNote("Listings are reviewed before they're published. Until then they show as \"Pending review\" in My submissions.")
            Spacer(Modifier.height(16.dp))
            Text("Type", style = DfiTheme.typography.labelSmall, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            SegmentedTabs(listOf("Event", "Club", "Activity"), listOf("event", "club", "activity").indexOf(f.kind), { f = f.copy(kind = listOf("event", "club", "activity")[it]) })
            Spacer(Modifier.height(14.dp))
            Text("Category", style = DfiTheme.typography.labelSmall, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            activityCategories.entries.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    row.forEach { (k, v) -> FilterPill(v, f.category == k) { f = f.copy(category = k) } }
                }
            }
            Spacer(Modifier.height(14.dp))
            DfiTextField(f.title, { f = f.copy(title = it.take(100)) }, "Title", error = if (f.title.isNotEmpty() && f.title.trim().length < 4) "At least 4 characters." else null)
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.organiser, { f = f.copy(organiser = it.take(80)) }, "Organiser")
            Spacer(Modifier.height(12.dp))
            Text("City", style = DfiTheme.typography.labelSmall, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            s.cities.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    row.forEach { ct -> FilterPill(ct.name, f.cityId == ct.id) { f = f.copy(cityId = ct.id) } }
                }
            }
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.venue, { f = f.copy(venue = it.take(120)) }, "Venue (optional)")
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.when_, { f = f.copy(when_ = it.take(120)) }, "When", placeholder = "e.g. Saturdays 7 am, or 12 Nov, 4 pm")
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.description, { f = f.copy(description = it.take(800)) }, "Description (optional)", singleLine = false, minLines = 3)
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.link, { f = f.copy(link = it.take(300)) }, "Registration link (optional)", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri))
            Spacer(Modifier.height(12.dp))
            DfiTextField(f.contact, { f = f.copy(contact = it.take(120)) }, "Public contact (optional)", supporting = "Shown on the listing once it's approved.")
            s.message?.let { Spacer(Modifier.height(12.dp)); InfoNote(it) }
            Spacer(Modifier.height(24.dp))
        }
        Box(Modifier.padding(20.dp).navigationBarsPadding()) {
            PrimaryButton("Submit for review", { vm.submit(f, onDone) }, Modifier.fillMaxWidth(), enabled = f.valid, loading = s.submitting)
        }
    }
}

@Composable
fun MySubmissionsScreen(onBack: () -> Unit, vm: ExploreViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    LaunchedEffect(Unit) { vm.loadMine() }
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("My submissions", onBack)
        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val mine = s.mine
            when {
                mine == null -> item { LoadingState() }
                s.mineError != null -> item { ErrorState(s.mineError!!, onRetry = { vm.loadMine() }) }
                mine.isEmpty() -> item { EmptyState("No submissions yet", "Activities you submit appear here with their review status.", fallback = FallbackArt.LEAVES) }
                else -> items(mine, key = { it.id }) { a ->
                    DfiCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(a.title, style = DfiTheme.typography.cardTitle, color = c.onSurface, modifier = Modifier.weight(1f))
                            when (a.status) {
                                "published" -> Tag("Published", color = c.success)
                                "rejected" -> Tag("Not approved", color = c.error)
                                else -> Tag("Pending review", color = c.warning)
                            }
                        }
                        Text(a.organiserName, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
