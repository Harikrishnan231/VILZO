package com.drugfreeindia.app.presentation.help

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
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HeadsetMic
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.BuildConfig
import com.drugfreeindia.app.data.repository.Source
import com.drugfreeindia.app.data.repository.TrustedContactsRepository
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.domain.model.SupportCentre
import com.drugfreeindia.app.domain.model.TrustedContact
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.ChipRow
import com.drugfreeindia.app.presentation.components.ConfirmDialog
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.Divider
import com.drugfreeindia.app.presentation.components.EmptyState
import com.drugfreeindia.app.presentation.components.FallbackArt
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.LinkButton
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.QuickExitChip
import com.drugfreeindia.app.presentation.components.ScreenTitle
import com.drugfreeindia.app.presentation.components.SearchField
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.SegmentedTabs
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.util.Intents
import java.util.UUID

// ============================================================== Get Help landing

@Composable
fun HelpScreen(
    contentPadding: PaddingValues,
    onContacts: () -> Unit,
    onHelplines: () -> Unit,
    onCentres: () -> Unit,
    onFriend: () -> Unit,
    onPrivacy: () -> Unit,
    onQuickExit: () -> Unit,
    onGrievance: () -> Unit = {},
    vm: HelpViewModel = hiltViewModel()
) {
    val c = DfiTheme.colors
    var talk by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState())
        .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp)) {
        Row(Modifier.statusBarsPadding().padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp), verticalAlignment = Alignment.Top) {
            ScreenTitle("Get Help", "Private and free. Choose what you need.", Modifier.weight(1f))
            QuickExitChip(onQuickExit)
        }
        DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), color = c.primarySoft, padding = PaddingValues(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f).padding(20.dp)) {
                    Text("You are not alone", style = DfiTheme.typography.heroTitle, color = c.onSurface)
                    Spacer(Modifier.height(6.dp))
                    Text("Talking to someone can make things feel lighter.", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton("Talk to someone", { talk = true }, icon = Icons.Rounded.HeadsetMic)
                }
                AppImage("help_hero", null, Modifier.size(width = 120.dp, height = 170.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        DfiCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), padding = PaddingValues(vertical = 4.dp)) {
            ListRow("Anonymous campus grievance", subtitle = "Confidential chat with your college cell", icon = Icons.Rounded.Shield, onClick = onGrievance)
            Divider()
            ListRow("Trusted contacts", subtitle = "Up to 5 people, saved only on this phone", icon = Icons.Rounded.Favorite, onClick = onContacts)
            Divider()
            ListRow("Helplines", subtitle = "Free numbers you can call, including 112", icon = Icons.Rounded.Call, onClick = onHelplines)
            Divider()
            ListRow("Find support centres", subtitle = "List and map of places that can help", icon = Icons.Rounded.Place, onClick = onCentres)
            Divider()
            ListRow("Help a friend", subtitle = "What to say, and what to avoid", icon = Icons.Rounded.Groups, onClick = onFriend)
            Divider()
            ListRow("Privacy & safety", subtitle = "What's stored, where, and who can see it", icon = Icons.Rounded.Shield, onClick = onPrivacy)
        }
        Spacer(Modifier.height(16.dp))
        Text("Your information stays on your device unless you choose to share it.", style = DfiTheme.typography.caption,
            color = c.onSurfaceVariant, modifier = Modifier.padding(horizontal = 24.dp))
    }
    if (talk) {
        ModalBottomSheet(onDismissRequest = { talk = false }, containerColor = c.surface, shape = DfiTheme.shapes.sheet) {
            Column(Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
                Text("Talk to someone", style = DfiTheme.typography.sectionTitle, color = c.onSurface, modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(8.dp))
                ListRow("Call a trusted contact", subtitle = "Someone you've chosen", icon = Icons.Rounded.Favorite, onClick = { talk = false; onContacts() })
                ListRow("Call a helpline", subtitle = "Free, trained listeners", icon = Icons.Rounded.Call, onClick = { talk = false; onHelplines() })
                ListRow("Find a centre", subtitle = "In-person support near you", icon = Icons.Rounded.Place, onClick = { talk = false; onCentres() })
            }
        }
    }
}

// ============================================================== Trusted contacts

@Composable
fun TrustedContactsScreen(onBack: () -> Unit, vm: HelpViewModel = hiltViewModel()) {
    val c = DfiTheme.colors
    val ctx = LocalContext.current
    val contacts by vm.contacts.collectAsState()
    var editing by remember { mutableStateOf<TrustedContact?>(null) }
    var messaging by remember { mutableStateOf<TrustedContact?>(null) }
    var deleting by remember { mutableStateOf<TrustedContact?>(null) }

    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Trusted contacts", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                InfoNote(if (vm.encryptionAvailable) "Saved only on this phone, encrypted. Never uploaded." else "Secure storage isn't available on this device, so contacts can't be saved.",
                    icon = Icons.Rounded.Lock)
            }
            if (contacts.isEmpty()) item {
                EmptyState("No trusted contacts yet", "Add someone you'd feel okay calling or messaging when things get hard — a friend, family member or mentor.",
                    fallback = FallbackArt.LEAVES)
            }
            items(contacts, key = { it.id }) { tc ->
                DfiCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(tc.name, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                            Text(listOf(tc.relationship, tc.phone).filter { it.isNotBlank() }.joinToString(" · "), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                        }
                        IconButton(onClick = { editing = tc }) { Icon(Icons.Rounded.Edit, "Edit ${tc.name}", tint = c.onSurfaceVariant) }
                        IconButton(onClick = { deleting = tc }) { Icon(Icons.Rounded.Delete, "Delete ${tc.name}", tint = c.onSurfaceVariant) }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryButton("Call", { Intents.dial(ctx, tc.phone) }, Modifier.weight(1f), icon = Icons.Rounded.Call)
                        SecondaryButton("Message", { messaging = tc }, Modifier.weight(1f), icon = Icons.AutoMirrored.Rounded.Chat)
                    }
                }
            }
        }
        if (contacts.size < TrustedContactsRepository.MAX && vm.encryptionAvailable) {
            Box(Modifier.padding(20.dp).navigationBarsPadding()) {
                PrimaryButton("Add contact", { editing = TrustedContact("", "", "") }, Modifier.fillMaxWidth(), icon = Icons.Rounded.Add)
            }
        } else if (contacts.size >= TrustedContactsRepository.MAX) {
            Text("You've added the maximum of 5 contacts.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant,
                modifier = Modifier.padding(20.dp).navigationBarsPadding())
        }
    }

    editing?.let { tc -> ContactEditor(tc, onDismiss = { editing = null }, onSave = { vm.saveContact(it); editing = null }) }
    messaging?.let { tc ->
        var body by rememberSaveable { mutableStateOf("Hi ${tc.name.substringBefore(' ')}, could we talk for a bit? I could use some support.") }
        ConfirmDialog(title = "Message ${tc.name}", body = "Edit the message, then send it from your messaging app.", confirm = "Open messages",
            onConfirm = { Intents.sms(ctx, tc.phone, body); messaging = null }, onDismiss = { messaging = null },
            extra = { DfiTextField(body, { body = it }, "Message", singleLine = false, minLines = 3) })
    }
    deleting?.let { tc ->
        ConfirmDialog(title = "Remove ${tc.name}?", body = "This removes the contact from this phone.", confirm = "Remove", destructive = true,
            onConfirm = { vm.deleteContact(tc.id); deleting = null }, onDismiss = { deleting = null })
    }
}

@Composable
private fun ContactEditor(initial: TrustedContact, onDismiss: () -> Unit, onSave: (TrustedContact) -> Unit) {
    var name by rememberSaveable { mutableStateOf(initial.name) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) }
    var rel by rememberSaveable { mutableStateOf(initial.relationship) }
    val phoneOk = phone.count { it.isDigit() } in 3..15
    ConfirmDialog(
        title = if (initial.id.isBlank()) "Add trusted contact" else "Edit contact",
        body = "Saved only on this phone.",
        confirm = "Save", confirmEnabled = name.isNotBlank() && phoneOk,
        onConfirm = { onSave(initial.copy(id = initial.id.ifBlank { UUID.randomUUID().toString() }, name = name.trim(), phone = phone.trim(), relationship = rel.trim())) },
        onDismiss = onDismiss,
        extra = {
            Column(Modifier.imePadding()) {
                DfiTextField(name, { name = it.take(60) }, "Name")
                Spacer(Modifier.height(10.dp))
                DfiTextField(phone, { phone = it.filter { ch -> ch.isDigit() || ch == '+' || ch == ' ' }.take(18) }, "Phone",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    error = if (phone.isNotEmpty() && !phoneOk) "Enter a valid number." else null)
                Spacer(Modifier.height(10.dp))
                DfiTextField(rel, { rel = it.take(40) }, "Relationship (optional)", placeholder = "Friend, sister, mentor…")
            }
        }
    )
}

// ============================================================== Helplines

@Composable
fun HelplinesScreen(onBack: () -> Unit, vm: HelpViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Helplines", onBack)
        LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Tapping a number opens your phone's dialer. The app doesn't place calls or notify anyone.",
                    style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            }
            if (s.loading) item { LoadingState() }
            items(s.helplines, key = { it.id }) { h -> HelplineCard(h) { Intents.dial(ctx, h.number) } }
            if (s.helplineSource != Source.REMOTE && !s.loading) item {
                InfoNote("Showing the list saved in the app. Numbers should be confirmed with official sources.")
            }
        }
    }
}

@Composable
private fun HelplineCard(h: Helpline, onCall: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(Modifier.fillMaxWidth(), color = if (h.isEmergency) c.surface else c.surface) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(DfiTheme.shapes.tile).background(if (h.isEmergency) c.error.copy(alpha = 0.10f) else c.primarySoft),
                contentAlignment = Alignment.Center) {
                Icon(if (h.isEmergency) Icons.Rounded.Emergency else Icons.Rounded.Call, null, tint = if (h.isEmergency) c.error else c.primaryDark)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(h.name, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                Text(h.number, style = DfiTheme.typography.sectionTitle, color = if (h.isEmergency) c.error else c.primaryDark)
            }
        }
        h.description?.let { Spacer(Modifier.height(8.dp)); Text(it, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) }
        if (h.isEmergency) {
            Spacer(Modifier.height(6.dp))
            Text("For immediate danger to life: police, fire or ambulance.", style = DfiTheme.typography.caption, color = c.onSurface)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            h.hours?.let { Tag(it) }
            h.languages?.takeIf { it.isNotEmpty() }?.let { Tag(it.joinToString(", ")) }
            if (h.isVerified) Tag("Verified", color = c.success) else if (BuildConfig.DEBUG) Tag("Unverified")
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Call ${h.number}", onCall, Modifier.fillMaxWidth(), icon = Icons.Rounded.Call)
    }
}

// ============================================================== Support centres

@Composable
fun CentresScreen(onBack: () -> Unit, vm: HelpViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val c = DfiTheme.colors
    val list = s.filteredCentres
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar("Support centres", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            SearchField(s.query, vm::onQuery, "Search by name, city or pincode")
            Spacer(Modifier.height(10.dp))
        }
        ChipRow(centreTypes.values.toList(), s.type?.let { centreTypes[it] }, vm::onType)
        Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            SegmentedTabs(listOf("List", "Map"), if (s.showMap) 1 else 0, { vm.toggleMap(it == 1) })
            Spacer(Modifier.height(8.dp))
            Text("${list.size} listing${if (list.size == 1) "" else "s"}" + if (list.none { it.isVerified }) " · sample data, not yet verified" else "",
                style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        }
        if (s.showMap) {
            Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp).padding(bottom = 16.dp).clip(DfiTheme.shapes.card)) {
                CentresMap(list, s.citySlug, onSelect = vm::select)
                if (list.none { it.lat != null && it.lng != null }) {
                    InfoNote("Map pins appear for listings with a confirmed location. Use the list to see all centres.",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp))
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (s.loading) item { LoadingState() }
                if (!s.loading && list.isEmpty()) item {
                    EmptyState("No centres match", "Try a different name, city or filter.", imageKey = "empty_centres", fallback = FallbackArt.HILLS,
                        action = "Clear filters", onAction = { vm.onQuery(""); vm.onType(null) })
                }
                items(list, key = { it.id }) { ct -> CentreRow(ct) { vm.select(ct) } }
            }
        }
    }
    s.selected?.let { CentreSheet(it) { vm.select(null) } }
}

@Composable
private fun CentreRow(ct: SupportCentre, onClick: () -> Unit) {
    val c = DfiTheme.colors
    DfiCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.size(40.dp).clip(DfiTheme.shapes.tile).background(c.primarySoft), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.LocalHospital, null, tint = c.primaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ct.name, style = DfiTheme.typography.cardTitle, color = c.onSurface)
                Text(listOfNotNull(centreTypes[ct.type], ct.cityName).joinToString(" · "), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                ct.hours?.let { Text(it, style = DfiTheme.typography.caption, color = c.onSurfaceVariant) }
                Spacer(Modifier.height(6.dp))
                if (ct.isVerified) Tag("Verified", color = c.success) else Tag("Sample listing")
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("View details", style = DfiTheme.typography.labelSmall, color = c.primaryDark)
    }
}

@Composable
private fun CentreSheet(ct: SupportCentre, onDismiss: () -> Unit) {
    val c = DfiTheme.colors
    val ctx = LocalContext.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = c.surface, shape = DfiTheme.shapes.sheet) {
        Column(Modifier.padding(horizontal = 20.dp).navigationBarsPadding().padding(bottom = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ct.name, style = DfiTheme.typography.sectionTitle, color = c.onSurface, modifier = Modifier.weight(1f))
                if (ct.isVerified) Tag("Verified", color = c.success) else Tag("Sample listing")
            }
            Spacer(Modifier.height(4.dp))
            Text(listOfNotNull(centreTypes[ct.type], ct.cityName).joinToString(" · "), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            ct.address?.let { DetailLine(Icons.Rounded.Place, it) }
            ct.hours?.let { DetailLine(Icons.Rounded.Schedule, it) }
            ct.website?.let { DetailLine(Icons.Rounded.Language, it) }
            if (ct.address == null && ct.phone == null) {
                InfoNote("Address and phone haven't been added for this listing yet. Search the name online or call a helpline for directions.")
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ct.phone?.let { p -> PrimaryButton("Call", { Intents.dial(ctx, p) }, Modifier.weight(1f), icon = Icons.Rounded.Call) }
                if (ct.address != null || (ct.lat != null && ct.lng != null)) {
                    SecondaryButton("Directions", { Intents.directions(ctx, ct.lat, ct.lng, listOfNotNull(ct.name, ct.address).joinToString(", ")) },
                        Modifier.weight(1f), icon = Icons.Rounded.Directions)
                }
                ct.website?.let { w -> if (ct.phone == null) SecondaryButton("Website", { Intents.openUrl(ctx, w) }, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DetailLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurface)
    }
}

// ============================================================== Markdown documents

@Composable
fun DocScreen(title: String, asset: String, imageKey: String?, onBack: () -> Unit, vm: DocViewModel = hiltViewModel()) {
    val text by vm.text.collectAsState()
    val c = DfiTheme.colors
    LaunchedEffect(asset) { vm.load(asset) }
    Column(Modifier.fillMaxSize().background(c.background)) {
        TopBar(title, onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding().padding(bottom = 32.dp)) {
            if (imageKey != null) {
                AppImage(imageKey, null, Modifier.fillMaxWidth().height(180.dp).clip(DfiTheme.shapes.hero), fallback = FallbackArt.LEAVES)
                Spacer(Modifier.height(16.dp))
            }
            if (text == null) LoadingState() else Markdown(text!!)
        }
    }
}

/** Minimal renderer for the bundled guides: #/##/### headings, "- " bullets, **bold**, *italic*, "---". */
@Composable
fun Markdown(md: String) {
    val c = DfiTheme.colors
    md.lines().forEach { raw ->
        val line = raw.trimEnd()
        when {
            line.isBlank() -> Spacer(Modifier.height(6.dp))
            line.trim() == "---" -> { Spacer(Modifier.height(8.dp)); Divider(); Spacer(Modifier.height(8.dp)) }
            line.startsWith("# ") -> Text(inline(line.removePrefix("# ")), style = DfiTheme.typography.screenTitle, color = c.onSurface,
                modifier = Modifier.padding(vertical = 4.dp).semantics { heading() })
            line.startsWith("## ") || line.startsWith("### ") -> Text(inline(line.trimStart('#', ' ')), style = DfiTheme.typography.sectionTitle,
                color = c.onSurface, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp).semantics { heading() })
            line.trimStart().startsWith("- ") -> {
                val indent = (line.length - line.trimStart().length) / 2
                Row(Modifier.padding(start = (indent * 16).dp, top = 3.dp, bottom = 3.dp)) {
                    Text("•", style = DfiTheme.typography.body, color = c.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(inline(line.trimStart().removePrefix("- ")), style = DfiTheme.typography.body, color = c.onSurface)
                }
            }
            else -> Text(inline(line), style = DfiTheme.typography.body, color = c.onSurface, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

private fun inline(s: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < s.length) {
        when {
            s.startsWith("**", i) -> {
                val end = s.indexOf("**", i + 2)
                if (end > 0) { withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(s.substring(i + 2, end)) }; i = end + 2 } else { append(s[i]); i++ }
            }
            s[i] == '*' -> {
                val end = s.indexOf('*', i + 1)
                if (end > 0) { withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(s.substring(i + 1, end)) }; i = end + 1 } else { append(s[i]); i++ }
            }
            else -> { append(s[i]); i++ }
        }
    }
}

// ============================================================== Quick exit (neutral screen)

/** Ordinary-looking study planner shown by Quick exit. It is a real, simple checklist kept in memory. */
@Composable
fun StudyPlannerScreen() {
    val c = DfiTheme.colors
    val tasks = remember { mutableStateListOf("Revise chapter 4 notes" to false, "Finish maths problem set" to false, "Read 20 pages" to false, "Plan tomorrow's timetable" to false) }
    Column(Modifier.fillMaxSize().background(c.surface).statusBarsPadding().navigationBarsPadding().padding(20.dp)) {
        Text("Study planner", style = DfiTheme.typography.screenTitle, color = c.onSurface)
        Text("Today", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        tasks.forEachIndexed { i, (t, done) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(done, { tasks[i] = t to it }, colors = CheckboxDefaults.colors(checkedColor = c.primary))
                Text(t, style = DfiTheme.typography.body, color = if (done) c.onSurfaceVariant else c.onSurface)
            }
        }
    }
}

// ============================================================== Legal placeholder

@Composable
fun LegalScreen(kind: String, url: String?, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val title = if (kind == "terms") "Terms of use" else "Privacy policy"
    Column(Modifier.fillMaxSize().background(DfiTheme.colors.background)) {
        TopBar(title, onBack)
        Column(Modifier.padding(20.dp)) {
            if (url != null) {
                Text("The full $title is published online.", style = DfiTheme.typography.body, color = DfiTheme.colors.onSurface)
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Open $title", { Intents.openUrl(ctx, url) })
            } else {
                Text("The full $title will be published before public release.", style = DfiTheme.typography.body, color = DfiTheme.colors.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Until then, the Privacy & safety page in Get Help explains what the app stores and why.",
                    style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurfaceVariant)
            }
        }
    }
}
