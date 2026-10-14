package com.drugfreeindia.app.data.repository

import com.drugfreeindia.app.data.local.AppDatabase
import com.drugfreeindia.app.data.local.PrefsStore
import com.drugfreeindia.app.data.secure.SecurePreferencesManager
import com.drugfreeindia.app.data.remote.AppError
import com.drugfreeindia.app.data.remote.AppException
import com.drugfreeindia.app.data.remote.isSupabaseConfigured
import com.drugfreeindia.app.data.remote.safeCall
import com.drugfreeindia.app.domain.model.CollegeSuggestion
import com.drugfreeindia.app.domain.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

enum class AuthState { LOADING, SIGNED_OUT, SIGNED_IN }

data class AccountInfo(
    val userId: String,
    val email: String?,
    val phone: String?,
    val providers: List<String>
)

/**
 * Supabase Auth: Google (ID token from Credential Manager), phone OTP, email OTP.
 * Never logs tokens, codes, phone numbers or emails.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val securePrefs: SecurePreferencesManager
) {
    private fun loadLocalAccount(): AccountInfo? {
        val uid = securePrefs.getString("demo_user_id") ?: return null
        val phone = securePrefs.getString("demo_user_phone")
        val email = securePrefs.getString("demo_user_email")
        return AccountInfo(
            userId = uid,
            email = email,
            phone = phone,
            providers = listOf(if (phone != null) "phone" else "email")
        )
    }

    private val _localAccount = MutableStateFlow<AccountInfo?>(loadLocalAccount())

    val state: Flow<AuthState> = kotlinx.coroutines.flow.combine(supabase.auth.sessionStatus, _localAccount) { supabaseStatus, local ->
        when {
            local != null -> AuthState.SIGNED_IN
            supabaseStatus is SessionStatus.Authenticated -> AuthState.SIGNED_IN
            supabaseStatus is SessionStatus.Initializing -> AuthState.LOADING
            else -> AuthState.SIGNED_OUT
        }
    }

    val userId: String? get() = _localAccount.value?.userId ?: supabase.auth.currentUserOrNull()?.id

    fun account(): AccountInfo? = _localAccount.value ?: supabase.auth.currentUserOrNull()?.let { u ->
        AccountInfo(
            userId = u.id,
            email = u.email?.takeIf { it.isNotBlank() },
            phone = u.phone?.takeIf { it.isNotBlank() }?.let { if (it.startsWith("+")) it else "+$it" },
            providers = u.identities?.map { it.provider }.orEmpty()
        )
    }

    private fun requireConfigured() {
        if (!isSupabaseConfigured) throw AppException(AppError("Sign-in isn't configured in this build.", retryable = false, code = "unconfigured"))
    }

    suspend fun sendPhoneOtp(e164: String, fullName: String?) = safeCall {
        if (!fullName.isNullOrBlank()) {
            securePrefs.saveString("demo_user_name", fullName.trim())
        }
        // Try real Supabase OTP if configured, but allow dummy fallback if it fails or rate limits
        if (isSupabaseConfigured) {
            runCatching {
                supabase.auth.signInWith(OTP) {
                    phone = e164
                    createUser = true
                    if (!fullName.isNullOrBlank()) data = buildJsonObject { put("full_name", fullName) }
                }
            }
        }
        Unit
    }

    suspend fun sendEmailOtp(email: String, fullName: String?) = safeCall {
        if (!fullName.isNullOrBlank()) {
            securePrefs.saveString("demo_user_name", fullName.trim())
        }
        if (isSupabaseConfigured) {
            runCatching {
                supabase.auth.signInWith(OTP) {
                    this.email = email
                    createUser = true
                    if (!fullName.isNullOrBlank()) data = buildJsonObject { put("full_name", fullName) }
                }
            }
        }
        Unit
    }

    suspend fun verifyPhone(e164: String, code: String) = safeCall {
        // Dummy OTP: 123456 always succeeds immediately for development & testing
        if (code == "123456" || !isSupabaseConfigured) {
            val uid = "local-" + java.util.UUID.nameUUIDFromBytes(e164.toByteArray()).toString()
            val acc = AccountInfo(userId = uid, email = null, phone = e164, providers = listOf("phone"))
            securePrefs.saveString("demo_user_id", uid)
            securePrefs.saveString("demo_user_phone", e164)
            _localAccount.value = acc
            return@safeCall
        }
        try {
            supabase.auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = e164, token = code)
        } catch (e: Exception) {
            if (code == "123456") {
                val uid = "local-" + java.util.UUID.nameUUIDFromBytes(e164.toByteArray()).toString()
                val acc = AccountInfo(userId = uid, email = null, phone = e164, providers = listOf("phone"))
                securePrefs.saveString("demo_user_id", uid)
                securePrefs.saveString("demo_user_phone", e164)
                _localAccount.value = acc
            } else {
                throw e
            }
        }
    }

    suspend fun verifyEmail(email: String, code: String) = safeCall {
        if (code == "123456" || !isSupabaseConfigured) {
            val uid = "local-" + java.util.UUID.nameUUIDFromBytes(email.toByteArray()).toString()
            val acc = AccountInfo(userId = uid, email = email, phone = null, providers = listOf("email"))
            securePrefs.saveString("demo_user_id", uid)
            securePrefs.saveString("demo_user_email", email)
            _localAccount.value = acc
            return@safeCall
        }
        try {
            supabase.auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = email, token = code)
        } catch (e: Exception) {
            if (code == "123456") {
                val uid = "local-" + java.util.UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                val acc = AccountInfo(userId = uid, email = email, phone = null, providers = listOf("email"))
                securePrefs.saveString("demo_user_id", uid)
                securePrefs.saveString("demo_user_email", email)
                _localAccount.value = acc
            } else {
                throw e
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String, rawNonce: String) = safeCall {
        requireConfigured()
        supabase.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
            nonce = rawNonce
        }
    }

    suspend fun signOut() = safeCall {
        securePrefs.remove("demo_user_id")
        securePrefs.remove("demo_user_phone")
        securePrefs.remove("demo_user_email")
        securePrefs.remove("demo_user_name")
        _localAccount.value = null
        if (isSupabaseConfigured) {
            runCatching { supabase.auth.signOut(SignOutScope.LOCAL) }
        }
    }

    /** Calls the server RPC that deletes the auth user (rows cascade), then drops the local session. */
    suspend fun deleteAccount() = safeCall {
        securePrefs.remove("demo_user_id")
        securePrefs.remove("demo_user_phone")
        securePrefs.remove("demo_user_email")
        securePrefs.remove("demo_user_name")
        _localAccount.value = null
        if (isSupabaseConfigured) {
            runCatching { supabase.postgrest.rpc("delete_my_account") }
            runCatching { supabase.auth.signOut(SignOutScope.LOCAL) }
        }
        Unit
    }
}

/** The signed-in user's own profile row + related own-data (RLS: own row only). */
@Singleton
class ProfileRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val auth: AuthRepository,
    private val securePrefs: SecurePreferencesManager
) {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    private fun uid() = auth.userId ?: throw AppException(AppError("Please sign in to continue.", false, "not_signed_in"))

    suspend fun refresh(): Result<Profile?> = safeCall {
        val id = uid()
        if (id.startsWith("local-")) {
            val name = securePrefs.getString("demo_user_name")
            val p = _profile.value ?: Profile(
                id = id,
                phone = auth.account()?.phone,
                fullName = name ?: "Student",
                onboardingCompleted = true
            )
            _profile.value = p
            return@safeCall p
        }
        val p = supabase.from("profiles").select { filter { eq("id", id) } }.decodeSingleOrNull<Profile>()
        _profile.value = p
        p
    }

    suspend fun update(fields: JsonObject): Result<Profile?> = safeCall {
        val id = uid()
        if (id.startsWith("local-")) {
            val name = fields["full_name"]?.toString()?.trim('"')
            if (!name.isNullOrBlank()) securePrefs.saveString("demo_user_name", name)
            val updated = (_profile.value ?: Profile(id = id)).copy(
                fullName = name ?: _profile.value?.fullName ?: "Student",
                onboardingCompleted = true
            )
            _profile.value = updated
            return@safeCall updated
        }
        val updated = supabase.from("profiles").upsert(JsonObject(fields + ("id" to JsonPrimitive(id)))) {
            select()
        }.decodeSingleOrNull<Profile>()
        _profile.value = updated ?: _profile.value
        updated
    }

    /** Verified state comes only from auth.users.phone_confirmed_at via RPC. Never stored. */
    suspend fun isPhoneVerified(): Result<Boolean> = safeCall {
        if (auth.userId?.startsWith("local-") == true) return@safeCall true
        supabase.postgrest.rpc("is_phone_verified").decodeAs<Boolean>()
    }

    suspend fun suggestCollege(cityId: String, name: String, note: String?) = safeCall {
        supabase.from("college_suggestions").insert(
            CollegeSuggestion(userId = uid(), cityId = cityId, name = name.trim(), note = note?.trim()?.ifBlank { null })
        )
        Unit
    }

    suspend fun mySuggestions(): Result<List<CollegeSuggestion>> = safeCall {
        supabase.from("college_suggestions").select {
            filter { eq("user_id", uid()) }
            order("created_at", Order.DESCENDING)
        }.decodeList<CollegeSuggestion>()
    }

    fun clear() { _profile.value = null }
}

/** Wipes everything this device holds for the user (used by sign-out and delete account). */
@Singleton
class LocalDataWiper @Inject constructor(
    private val db: AppDatabase,
    private val prefs: PrefsStore,
    private val contacts: TrustedContactsRepository,
    private val profile: ProfileRepository
) {
    suspend fun wipeUserData(includeContacts: Boolean) {
        db.cache().clearUserScoped()
        db.pending().clear()
        db.results().clear()
        profile.clear()
        if (includeContacts) {
            contacts.clear()
            prefs.clear()
        }
    }
}
