package com.drugfreeindia.app.presentation.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.R
import com.drugfreeindia.app.data.repository.Source
import com.drugfreeindia.app.presentation.components.AppImage
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiTextField
import com.drugfreeindia.app.presentation.components.ErrorState
import com.drugfreeindia.app.presentation.components.InfoNote
import com.drugfreeindia.app.presentation.components.LinkButton
import com.drugfreeindia.app.presentation.components.ListRow
import com.drugfreeindia.app.presentation.components.LoadingState
import com.drugfreeindia.app.presentation.components.PrimaryButton
import com.drugfreeindia.app.presentation.components.SearchField
import com.drugfreeindia.app.presentation.components.SecondaryButton
import com.drugfreeindia.app.presentation.components.Tag
import com.drugfreeindia.app.presentation.components.TopBar
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun LogoMark(size: androidx.compose.ui.unit.Dp = 56.dp) {
    Box(Modifier.size(size).clip(DfiTheme.shapes.tile).background(DfiTheme.colors.primarySoft), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Image(painterResource(R.drawable.vilzo_logo_mark), contentDescription = "VILZO logo", modifier = Modifier.size(size * 1.4f))
    }
}

// ============================================================== Welcome

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit, onSignIn: () -> Unit, onNeedHelp: () -> Unit) {
    val c = DfiTheme.colors
    Box(Modifier.fillMaxSize().background(c.background)) {
        // Illustration fills the lower part with a soft fade into the background at the top.
        Box(Modifier.fillMaxWidth().fillMaxHeight(0.62f).align(Alignment.BottomCenter)) {
            AppImage("welcome_hero", "Illustration of college friends walking together on campus", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxWidth().height(120.dp).background(Brush.verticalGradient(listOf(c.background, Color.Transparent))))
            Box(Modifier.fillMaxWidth().height(260.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, c.background.copy(alpha = 0.92f), c.background))))
        }
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(28.dp))
            LogoMark()
            Spacer(Modifier.height(18.dp))
            Text("Drug-Free India", style = DfiTheme.typography.screenTitle, color = c.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.v3_tagline), style = DfiTheme.typography.body, color = c.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            PrimaryButton(stringResource(R.string.v3_get_started), onGetStarted, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.v3_have_account), style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                LinkButton(stringResource(R.string.v3_sign_in), onSignIn)
            }
            Row(Modifier.fillMaxWidth().clickable(onClick = onNeedHelp).heightIn(min = 48.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.SupportAgent, null, tint = c.primaryDark, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.v3_need_support), style = DfiTheme.typography.captionLarge, color = c.primaryDark)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ============================================================== Create account / Sign in / OTP

@Composable
fun AuthScreen(
    signIn: Boolean,
    onBack: () -> Unit,
    onDone: (needsLocation: Boolean) -> Unit,
    onGuest: () -> Unit,
    onLegal: (String) -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(signIn) { vm.setMode(signIn) }
    LaunchedEffect(Unit) { vm.effects.collect { if (it is AuthEffect.Done) onDone(it.needsLocation) } }

    val c = DfiTheme.colors
    Column(Modifier.fillMaxSize().background(c.background).imePadding()) {
        TopBar("", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            LogoMark(48.dp)
            Spacer(Modifier.height(16.dp))
            Text("Welcome to VILZO", style = DfiTheme.typography.screenTitle, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("Empowering youth with awareness, confidential support, and healthy lifestyle choices.",
                style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))

            // Student Sign In Card
            DfiCard(Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
                Tag("Students", color = c.primaryDark, bg = c.primarySoft)
                Spacer(Modifier.height(10.dp))
                Text("Student Sign In", style = DfiTheme.typography.cardTitle, color = c.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("Sign in with Google to save progress, participate in competitions, and file confidential reports.",
                    style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                PrimaryButton(
                    text = "Continue with Google",
                    onClick = { vm.google(context) },
                    modifier = Modifier.fillMaxWidth(),
                    loading = s.googleLoading
                )
            }

            s.error?.let {
                Spacer(Modifier.height(14.dp))
                ErrorState(it)
            }

            Spacer(Modifier.height(24.dp))
            LinkButton("Continue without an account (Guest Mode)", onGuest, Modifier.align(Alignment.CenterHorizontally), color = c.onSurfaceVariant)
            Text("Without an account you can practise scenarios and view helplines. Scores and progress stay on this device.",
                style = DfiTheme.typography.caption, color = c.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LinkButton("Terms", { onLegal("terms") }, color = c.onSurfaceVariant)
                LinkButton("Privacy", { onLegal("privacy") }, color = c.onSurfaceVariant)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(DfiTheme.colors.outline))
        Text("  or  ", style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant)
        Box(Modifier.weight(1f).height(1.dp).background(DfiTheme.colors.outline))
    }
}

@Composable
private fun OtpStep(s: AuthUiState, vm: AuthViewModel) {
    val c = DfiTheme.colors
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Text("Enter the code", style = DfiTheme.typography.screenTitle, color = c.onSurface)
    Spacer(Modifier.height(6.dp))
    Text(s.info ?: "We sent a 6-digit code to ${s.maskedDestination}.", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))
    // One hidden field drives six boxes: auto-advance and paste work naturally.
    BasicTextField(
        value = s.code, onValueChange = vm::onCode,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        modifier = Modifier.focusRequester(focus).semantics { contentDescription = "6-digit verification code" },
        decorationBox = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { i ->
                    val ch = s.code.getOrNull(i)?.toString() ?: ""
                    val active = i == s.code.length
                    Box(
                        Modifier.weight(1f).aspectRatio(0.85f).widthIn(max = 56.dp).clip(DfiTheme.shapes.input).background(c.surface)
                            .border(if (active) 2.dp else 1.dp, if (s.error != null) c.error else if (active) c.primary else c.outline, DfiTheme.shapes.input),
                        contentAlignment = Alignment.Center
                    ) { Text(ch, style = DfiTheme.typography.stat, color = c.onSurface) }
                }
            }
        }
    )
    s.error?.let { Spacer(Modifier.height(12.dp)); ErrorState(it) }
    Spacer(Modifier.height(20.dp))
    PrimaryButton("Verify", vm::verify, Modifier.fillMaxWidth(), enabled = s.code.length == 6, loading = s.loading)
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        LinkButton(if (s.useEmail) "Change email" else "Change number", vm::backToForm)
        if (s.resendIn > 0) Text("Resend in 0:${s.resendIn.toString().padStart(2, '0')}", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
        else LinkButton("Resend code", vm::resend)
    }
    Spacer(Modifier.height(12.dp))
    SecondaryButton("Auto-fill test code (123456)", { vm.onCode("123456") }, Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    InfoNote("For testing, dummy OTP 123456 is accepted for any phone or email.")
}

// ============================================================== Location & college

@SuppressLint("MissingPermission")
@Composable
fun LocationScreen(editing: Boolean, onBack: (() -> Unit)?, onDone: () -> Unit, vm: LocationViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val context = LocalContext.current
    val c = DfiTheme.colors
    var explain by rememberSaveable { mutableStateOf(false) }
    var cityPicker by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.done.collect { onDone() } }

    fun fetchLocation() {
        vm.setLocating(true)
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { loc -> if (loc != null) vm.useApproxLocation(loc.latitude, loc.longitude) else vm.locationUnavailable("Location isn't available right now. Please pick your city.") }
            .addOnFailureListener { vm.locationUnavailable("Location isn't available right now. Please pick your city.") }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) fetchLocation() else vm.locationUnavailable("No problem — choose your city from the list.")
    }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Column(Modifier.fillMaxSize().background(c.background).imePadding()) {
        TopBar(if (editing) "College & location" else "", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            if (!imeVisible) {
                AppImage("onboarding_location", null, Modifier.fillMaxWidth().height(120.dp).clip(DfiTheme.shapes.hero))
                Spacer(Modifier.height(20.dp))
            }
            Text("Where do you study?", style = DfiTheme.typography.screenTitle, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("We use this to show activities and help near you.", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))

            if (s.signedIn) {
                Text("Personal details", style = DfiTheme.typography.labelSmall, color = c.onSurface)
                Spacer(Modifier.height(6.dp))
                DfiTextField(
                    value = s.fullName,
                    onValueChange = vm::onFullName,
                    label = "Full name",
                    placeholder = "Your full name"
                )
                Spacer(Modifier.height(10.dp))
                DfiTextField(
                    value = s.phone,
                    onValueChange = vm::onPhone,
                    label = "Mobile number",
                    placeholder = "10-digit mobile number"
                )
                Spacer(Modifier.height(18.dp))
            }

            // City
            Text("City", style = DfiTheme.typography.labelSmall, color = c.onSurface)
            Spacer(Modifier.height(6.dp))
            DfiCard(onClick = { cityPicker = true }, padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                ListRow(s.city?.name ?: "Choose your city", subtitle = s.city?.state ?: (if (s.loadingCities) "Loading cities…" else "${s.cities.size} cities available"),
                    icon = Icons.Rounded.LocationCity)
            }
            Spacer(Modifier.height(4.dp))
            LinkButton(if (s.locating) "Finding nearest city…" else "Use my approximate location", { explain = true })
            if (s.citySource != Source.REMOTE && !s.loadingCities) {
                Text("Showing the list saved in the app.", style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
            }

            // College
            if (s.city != null) {
                Spacer(Modifier.height(20.dp))
                Text("College", style = DfiTheme.typography.labelSmall, color = c.onSurface)
                Spacer(Modifier.height(6.dp))
                val chosen = s.college
                if (chosen != null) {
                    DfiCard(padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        ListRow(if (chosen.id == OTHER_COLLEGE_ID) "Other (not listed)" else chosen.name,
                            subtitle = if (chosen.isVerified) "Verified listing" else null, icon = Icons.Rounded.School,
                            trailing = { LinkButton("Change", { vm.selectCollege(null) }) })
                    }
                    if (chosen.id == OTHER_COLLEGE_ID) {
                        Spacer(Modifier.height(12.dp))
                        DfiTextField(s.otherName, vm::onOtherName, "Enter college name", placeholder = "Full name of your college",
                            supporting = "Saved privately on your profile. It isn't added to the public list.",
                            error = if (s.otherName.isNotEmpty() && s.otherName.trim().length < 3) "Enter at least 3 letters." else null)
                    }
                } else {
                    SearchField(s.collegeQuery, vm::onCollegeQuery, "Search colleges in ${s.city!!.name}")
                    Spacer(Modifier.height(8.dp))
                    when {
                        s.loadingColleges -> LoadingState()
                        else -> DfiCard(padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                            val list = s.filteredColleges.take(30)
                            if (list.isEmpty()) {
                                Text(if (s.colleges.isEmpty()) "No colleges listed for this city yet." else "No match for \"${s.collegeQuery}\".",
                                    style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                            }
                            list.forEach { col ->
                                ListRow(col.name, subtitle = if (col.isVerified) "Verified listing" else null, icon = Icons.Rounded.School,
                                    trailing = if (col.isVerified) ({ Icon(Icons.Rounded.CheckCircle, "Verified", tint = c.primary) }) else null,
                                    onClick = { vm.selectCollege(col) })
                            }
                            ListRow("Other: enter college name", icon = Icons.Rounded.School, iconBg = c.surfaceMuted, iconTint = c.onSurfaceVariant, onClick = vm::selectOther)
                        }
                    }
                    if (s.signedIn) LinkButton("Can't find it? Suggest a college", vm::openSuggest)
                    else LinkButton("Skip for now", { vm.selectCollege(null); vm.save() })
                }
                if (s.pendingSuggestions.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    s.pendingSuggestions.forEach { n ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(n, style = DfiTheme.typography.bodySmall, color = c.onSurface, modifier = Modifier.weight(1f))
                            Tag("Pending review", color = c.warning)
                        }
                    }
                }
            }
            s.info?.let { Spacer(Modifier.height(12.dp)); InfoNote(it) }
            s.error?.let { Spacer(Modifier.height(12.dp)); ErrorState(it, onRetry = { s.city?.let { ct -> vm.selectCity(ct) } }) }
            if (!s.signedIn) {
                Spacer(Modifier.height(12.dp))
                InfoNote("You're not signed in, so only your city is saved on this device.")
            }
            Spacer(Modifier.height(24.dp))
        }
        Column(Modifier.background(c.background).padding(horizontal = 24.dp, vertical = 12.dp).navigationBarsPadding()) {
            if (s.city != null) {
                val collegeText = s.college?.let { if (it.id == OTHER_COLLEGE_ID) s.otherName.ifBlank { "Other" } else it.name }
                Text(buildAnnotatedString {
                    append("You chose ")
                    withStyle(SpanStyle(color = c.onSurface)) { append(s.city!!.name) }
                    if (collegeText != null) { append(" · "); withStyle(SpanStyle(color = c.onSurface)) { append(collegeText) } }
                }, style = DfiTheme.typography.caption, color = c.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton(if (editing) "Save" else "Continue", vm::save, Modifier.fillMaxWidth(), enabled = s.canContinue, loading = s.saving)
        }
    }

    if (explain) {
        AlertDialog(
            onDismissRequest = { explain = false },
            containerColor = c.surface, shape = DfiTheme.shapes.hero,
            icon = { Icon(Icons.Rounded.MyLocation, null, tint = c.primary) },
            title = { Text("Use approximate location?", style = DfiTheme.typography.sectionTitle) },
            text = { Text("We'll use your approximate (not exact) location once, on this phone, to pick the nearest city. Your location is never sent to our servers. You can always choose manually.",
                style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant) },
            confirmButton = { TextButton(onClick = {
                explain = false
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) fetchLocation()
                else permission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }) { Text("Continue", color = c.primaryDark) } },
            dismissButton = { TextButton(onClick = { explain = false }) { Text("Choose manually", color = c.onSurfaceVariant) } }
        )
    }
    if (cityPicker) {
        AlertDialog(
            onDismissRequest = { cityPicker = false },
            containerColor = c.surface, shape = DfiTheme.shapes.hero,
            title = { Text("Choose your city", style = DfiTheme.typography.sectionTitle) },
            text = {
                Column {
                    SearchField(s.cityQuery, vm::onCityQuery, "Search city or state")
                    Spacer(Modifier.height(8.dp))
                    Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                        if (s.loadingCities) LoadingState()
                        s.filteredCities.forEach { city ->
                            ListRow(city.name, subtitle = city.state, trailing = null, onClick = { vm.selectCity(city); cityPicker = false })
                        }
                        if (!s.loadingCities && s.filteredCities.isEmpty()) Text("No matching city. Your city may not be listed yet.",
                            style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant, modifier = Modifier.padding(8.dp))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { cityPicker = false }) { Text("Close", color = c.onSurfaceVariant) } }
        )
    }
    if (s.suggestOpen) {
        AlertDialog(
            onDismissRequest = vm::closeSuggest,
            containerColor = c.surface, shape = DfiTheme.shapes.hero,
            title = { Text("Suggest a college", style = DfiTheme.typography.sectionTitle) },
            text = {
                Column {
                    Text("Our team reviews suggestions before they appear in the list.", style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    DfiTextField(s.suggestName, vm::onSuggestName, "College name", error = s.suggestError)
                    Spacer(Modifier.height(10.dp))
                    DfiTextField(s.suggestNote, vm::onSuggestNote, "Area or note (optional)")
                }
            },
            confirmButton = { TextButton(onClick = vm::sendSuggestion, enabled = !s.suggestSending) { Text(if (s.suggestSending) "Sending…" else "Send", color = c.primaryDark) } },
            dismissButton = { TextButton(onClick = vm::closeSuggest) { Text("Cancel", color = c.onSurfaceVariant) } }
        )
    }
}
