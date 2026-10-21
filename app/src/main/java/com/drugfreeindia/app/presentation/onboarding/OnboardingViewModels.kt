package com.drugfreeindia.app.presentation.onboarding

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drugfreeindia.app.BuildConfig
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.toAppError
import com.drugfreeindia.app.data.repository.AuthRepository
import com.drugfreeindia.app.data.repository.ContentRepository
import com.drugfreeindia.app.data.repository.ProfileRepository
import com.drugfreeindia.app.data.repository.Source
import com.drugfreeindia.app.domain.model.City
import com.drugfreeindia.app.domain.model.College
import com.drugfreeindia.app.domain.model.Profile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

// ============================================================== Auth

enum class AuthStep { FORM, OTP }

data class AuthUiState(
    val signIn: Boolean = false,
    val step: AuthStep = AuthStep.FORM,
    val name: String = "",
    val countryCode: String = "+91",
    val phone: String = "",
    val email: String = "",
    val useEmail: Boolean = false,
    val code: String = "",
    val resendIn: Int = 0,
    val loading: Boolean = false,
    val googleLoading: Boolean = false,
    val nameError: String? = null,
    val contactError: String? = null,
    val error: String? = null,
    val info: String? = null
) {
    val destination: String get() = if (useEmail) email.trim() else "$countryCode${phone.filter { it.isDigit() }}"
    val maskedDestination: String get() = if (useEmail) email.trim() else "$countryCode ${phone.take(2)}••••••${phone.takeLast(2)}"
}

sealed interface AuthEffect {
    data class Done(val needsLocation: Boolean) : AuthEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val prefs: PrefsStore
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()
    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var timer: Job? = null

    val googleConfigured = BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank() && isSupabaseConfigured

    fun setMode(signIn: Boolean) = _state.update { it.copy(signIn = signIn) }
    fun onName(v: String) = _state.update { it.copy(name = v.take(60), nameError = null) }
    fun onCountry(v: String) = _state.update { it.copy(countryCode = ("+" + v.filter { c -> c.isDigit() }).take(4), contactError = null) }
    fun onPhone(v: String) = _state.update { it.copy(phone = v.filter { c -> c.isDigit() }.take(12), contactError = null) }
    fun onEmail(v: String) = _state.update { it.copy(email = v.trim().take(120), contactError = null) }
    fun toggleEmail() = _state.update { it.copy(useEmail = !it.useEmail, contactError = null, error = null) }
    fun onCode(v: String) {
        val digits = v.filter { it.isDigit() }.take(6)
        _state.update { it.copy(code = digits, error = null) }
        if (digits.length == 6) verify()
    }
    fun backToForm() { timer?.cancel(); _state.update { it.copy(step = AuthStep.FORM, code = "", error = null, info = null) } }

    private fun validate(): Boolean {
        val s = _state.value
        var ok = true
        if (!s.signIn && s.name.trim().length < 2) { _state.update { it.copy(nameError = "Enter your name (at least 2 letters).") }; ok = false }
        if (s.useEmail) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) { _state.update { it.copy(contactError = "Enter a valid email address.") }; ok = false }
        } else {
            val len = s.phone.length
            val valid = if (s.countryCode == "+91") len == 10 && s.phone.first() in '6'..'9' else len in 6..12
            if (!valid) { _state.update { it.copy(contactError = if (s.countryCode == "+91") "Enter a 10-digit Indian mobile number." else "Enter a valid phone number.") }; ok = false }
        }
        return ok
    }

    fun sendCode() {
        if (_state.value.loading || !validate()) return
        val s = _state.value
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val name = s.name.trim().ifBlank { null }
            val r = if (s.useEmail) auth.sendEmailOtp(s.destination, name) else auth.sendPhoneOtp(s.destination, name)
            r.onSuccess {
                _state.update { it.copy(loading = false, step = AuthStep.OTP, code = "", info = "We sent a 6-digit code to ${s.maskedDestination}.\n(Test dummy OTP: 123456)") }
                startResendTimer()
            }.onFailure { e -> _state.update { it.copy(loading = false, error = e.toAppError().message) } }
        }
    }

    private fun startResendTimer() {
        timer?.cancel()
        timer = viewModelScope.launch {
            for (i in 30 downTo 0) { _state.update { it.copy(resendIn = i) }; delay(1000) }
        }
    }

    fun resend() { if (_state.value.resendIn == 0) sendCode() }

    fun verify() {
        val s = _state.value
        if (s.loading || s.code.length != 6) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val r = if (s.useEmail) auth.verifyEmail(s.destination, s.code) else auth.verifyPhone(s.destination, s.code)
            r.onSuccess { finishSignIn(s.name.trim().ifBlank { null }) }
                .onFailure { e -> _state.update { it.copy(loading = false, code = "", error = e.toAppError().message) } }
        }
    }

    private suspend fun finishSignIn(name: String?) {
        var profile: Profile? = profiles.refresh().getOrNull()
        if (name != null && profile?.fullName.isNullOrBlank()) {
            profile = profiles.update(buildJsonObject { put("full_name", name) }).getOrNull() ?: profile
        }
        _state.update { it.copy(loading = false, googleLoading = false) }
        _effects.send(AuthEffect.Done(needsLocation = profile?.onboardingCompleted != true))
    }

    /** Credential Manager → Google ID token (with hashed nonce) → Supabase IDToken sign-in. */
    fun google(activityContext: Context) {
        if (!googleConfigured) {
            _state.update { it.copy(error = "Google sign-in isn't set up in this build yet. Use your phone or email instead.") }
            return
        }
        if (_state.value.googleLoading) return
        _state.update { it.copy(googleLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val rawNonce = UUID.randomUUID().toString()
                val hashed = MessageDigest.getInstance("SHA-256").digest(rawNonce.toByteArray()).joinToString("") { "%02x".format(it) }
                val option = GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .setNonce(hashed)
                    .build()
                val result = CredentialManager.create(activityContext)
                    .getCredential(activityContext, GetCredentialRequest.Builder().addCredentialOption(option).build())
                val cred = result.credential
                if (cred !is CustomCredential || cred.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    _state.update { it.copy(googleLoading = false, error = "That account type isn't supported. Try another option.") }
                    return@launch
                }
                val google = GoogleIdTokenCredential.createFrom(cred.data)
                auth.signInWithGoogle(google.idToken, rawNonce)
                    .onSuccess { finishSignIn(google.displayName ?: _state.value.name.ifBlank { null }) }
                    .onFailure { e -> _state.update { it.copy(googleLoading = false, error = e.toAppError().message) } }
            } catch (c: kotlinx.coroutines.CancellationException) {
                throw c
            } catch (_: GetCredentialCancellationException) {
                _state.update { it.copy(googleLoading = false) }
            } catch (_: NoCredentialException) {
                _state.update { it.copy(googleLoading = false, error = "No Google account found on this device. Add one in Settings or use phone/email.") }
            } catch (t: Throwable) {
                val msg = t.localizedMessage?.takeIf { it.isNotBlank() } ?: "Google sign-in didn't complete. Please try again."
                _state.update { it.copy(googleLoading = false, error = msg) }
            }
        }
    }

    fun continueAsGuest(onDone: () -> Unit) = viewModelScope.launch { onDone() }
}

// ============================================================== Location & college

/** Approximate city centres (public geography) for on-device nearest-city matching only. */
val cityCentres = mapOf(
    "puducherry" to (11.9416 to 79.8083), "karaikal" to (10.9254 to 79.8380), "chennai" to (13.0827 to 80.2707),
    "coimbatore" to (11.0168 to 76.9558), "bengaluru" to (12.9716 to 77.5946), "hyderabad" to (17.3850 to 78.4867),
    "kochi" to (9.9312 to 76.2673), "mumbai" to (19.0760 to 72.8777), "pune" to (18.5204 to 73.8567),
    "delhi" to (28.6139 to 77.2090), "ghaziabad" to (28.6692 to 77.4538), "kolkata" to (22.5726 to 88.3639)
)

const val OTHER_COLLEGE_ID = "__other__"

data class LocationUiState(
    val fullName: String = "",
    val phone: String = "",
    val loadingCities: Boolean = true,
    val cities: List<City> = emptyList(),
    val citySource: Source = Source.BUNDLED,
    val city: City? = null,
    val cityQuery: String = "",
    val loadingColleges: Boolean = false,
    val colleges: List<College> = emptyList(),
    val collegeQuery: String = "",
    val college: College? = null,
    val otherName: String = "",
    val signedIn: Boolean = false,
    val saving: Boolean = false,
    val locating: Boolean = false,
    val error: String? = null,
    val info: String? = null,
    val suggestOpen: Boolean = false,
    val suggestName: String = "",
    val suggestNote: String = "",
    val suggestSending: Boolean = false,
    val suggestError: String? = null,
    val pendingSuggestions: List<String> = emptyList()
) {
    val filteredColleges get() = if (collegeQuery.isBlank()) colleges else colleges.filter { it.name.contains(collegeQuery.trim(), true) || it.shortCode?.contains(collegeQuery.trim(), true) == true }
    val filteredCities get() = if (cityQuery.isBlank()) cities else cities.filter { it.name.contains(cityQuery.trim(), true) || it.state?.contains(cityQuery.trim(), true) == true }
    val canContinue get() = city != null && !saving && (college == null || college.id != OTHER_COLLEGE_ID || otherName.trim().length >= 3)
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val content: ContentRepository,
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
    private val prefs: PrefsStore
) : ViewModel() {
    private val _state = MutableStateFlow(LocationUiState(signedIn = auth.userId != null))
    val state: StateFlow<LocationUiState> = _state.asStateFlow()
    private val _done = Channel<Unit>(Channel.BUFFERED)
    val done = _done.receiveAsFlow()

    init {
        loadCities()
        profiles.profile.value?.let { p ->
            _state.update { it.copy(fullName = p.fullName.orEmpty(), phone = p.phone.orEmpty()) }
        }
    }

    fun onFullName(v: String) = _state.update { it.copy(fullName = v.take(80)) }
    fun onPhone(v: String) = _state.update { it.copy(phone = v.filter { c -> c.isDigit() }.take(12)) }

    fun loadCities() = viewModelScope.launch {
        _state.update { it.copy(loadingCities = true, error = null) }
        val r = content.cities()
        val profile = profiles.profile.value
        val savedSlug = prefs.current().citySlug
        val pre = r.data.firstOrNull { it.id == profile?.cityId } ?: r.data.firstOrNull { it.slug == savedSlug }
        _state.update { it.copy(loadingCities = false, cities = r.data, citySource = r.source) }
        if (pre != null && _state.value.city == null) selectCity(pre, preselectCollegeId = profile?.collegeId, otherName = profile?.customCollegeName)
        if (_state.value.signedIn) profiles.mySuggestions().onSuccess { list ->
            _state.update { s -> s.copy(pendingSuggestions = list.filter { it.status == "pending" }.map { it.name }) }
        }
    }

    fun onCityQuery(q: String) = _state.update { it.copy(cityQuery = q) }
    fun onCollegeQuery(q: String) = _state.update { it.copy(collegeQuery = q) }
    fun onOtherName(v: String) = _state.update { it.copy(otherName = v.take(120)) }

    fun selectCity(city: City, preselectCollegeId: String? = null, otherName: String? = null) {
        _state.update { it.copy(city = city, cityQuery = "", college = null, colleges = emptyList(), loadingColleges = true, collegeQuery = "", info = null) }
        viewModelScope.launch {
            val r = content.colleges(city.id)
            val pre = when {
                !otherName.isNullOrBlank() -> College(OTHER_COLLEGE_ID, city.id, "Other")
                else -> r.data.firstOrNull { it.id == preselectCollegeId }
            }
            _state.update { it.copy(loadingColleges = false, colleges = r.data, college = pre, otherName = otherName ?: it.otherName,
                error = if (r.data.isEmpty() && r.error != null) r.error.message else null) }
        }
    }

    fun selectCollege(c: College?) = _state.update { it.copy(college = c, collegeQuery = "") }
    fun selectOther() = _state.value.city?.let { city -> _state.update { it.copy(college = College(OTHER_COLLEGE_ID, city.id, "Other"), collegeQuery = "") } }

    /** Nearest seeded city from a coarse fix. Runs on the device; coordinates are discarded right after. */
    fun useApproxLocation(lat: Double, lng: Double) {
        val cities = _state.value.cities
        val nearest = cities.minByOrNull { c ->
            val p = cityCentres[c.slug] ?: return@minByOrNull Double.MAX_VALUE
            val dLat = p.first - lat; val dLng = (p.second - lng) * kotlin.math.cos(Math.toRadians(lat))
            dLat * dLat + dLng * dLng
        }
        _state.update { it.copy(locating = false) }
        if (nearest != null && cityCentres.containsKey(nearest.slug)) {
            selectCity(nearest)
            _state.update { it.copy(info = "Nearest listed city: ${nearest.name}. You can change it.") }
        } else _state.update { it.copy(info = "We couldn't match a nearby city. Please pick one from the list.") }
    }
    fun setLocating(v: Boolean) = _state.update { it.copy(locating = v) }
    fun locationUnavailable(msg: String) = _state.update { it.copy(locating = false, info = msg) }

    // ---- suggest college
    fun openSuggest() = _state.update { it.copy(suggestOpen = true, suggestName = it.collegeQuery, suggestError = null) }
    fun closeSuggest() = _state.update { it.copy(suggestOpen = false) }
    fun onSuggestName(v: String) = _state.update { it.copy(suggestName = v.take(120), suggestError = null) }
    fun onSuggestNote(v: String) = _state.update { it.copy(suggestNote = v.take(200)) }
    fun sendSuggestion() {
        val s = _state.value
        val city = s.city ?: return
        if (s.suggestName.trim().length < 3) { _state.update { it.copy(suggestError = "Enter the full college name.") }; return }
        if (city.id.startsWith("local:")) { _state.update { it.copy(suggestError = "Suggestions need a connection to the server. Please try again when online.") }; return }
        _state.update { it.copy(suggestSending = true, suggestError = null) }
        viewModelScope.launch {
            profiles.suggestCollege(city.id, s.suggestName, s.suggestNote)
                .onSuccess {
                    _state.update { it.copy(suggestSending = false, suggestOpen = false, suggestNote = "",
                        pendingSuggestions = it.pendingSuggestions + s.suggestName.trim(),
                        info = "Thanks! \"${s.suggestName.trim()}\" is pending review. You can use \"Other\" for now.") }
                }
                .onFailure { e -> _state.update { it.copy(suggestSending = false, suggestError = e.toAppError().message) } }
        }
    }

    fun save() {
        val s = _state.value
        val city = s.city ?: return
        if (!s.canContinue) return
        _state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            prefs.setCitySlug(city.slug)
            if (s.signedIn) {
                val serverCity = !city.id.startsWith("local:")
                val college = s.college
                val fields = buildJsonObject {
                    if (s.fullName.isNotBlank()) put("full_name", JsonPrimitive(s.fullName.trim()))
                    if (s.phone.isNotBlank()) put("phone", JsonPrimitive(s.phone.trim()))
                    if (serverCity) put("city_id", city.id) else put("city_id", JsonNull)
                    when {
                        college == null -> { put("college_id", JsonNull); put("custom_college_name", JsonNull) }
                        college.id == OTHER_COLLEGE_ID -> { put("college_id", JsonNull); put("custom_college_name", s.otherName.trim()) }
                        college.id.startsWith("local:") -> { put("college_id", JsonNull); put("custom_college_name", college.name) }
                        else -> { put("college_id", college.id); put("custom_college_name", JsonNull) }
                    }
                    put("onboarding_completed", JsonPrimitive(true))
                }
                val r = profiles.update(fields)
                if (r.isFailure) {
                    // Keep the device choice and continue; the profile can be updated later from Profile.
                    _state.update { it.copy(info = r.exceptionOrNull()!!.toAppError().message) }
                }
            }
            prefs.setWelcomeSeen()
            _state.update { it.copy(saving = false) }
            _done.send(Unit)
        }
    }
}
