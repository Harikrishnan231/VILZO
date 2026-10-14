package com.drugfreeindia.app.data.remote

import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.net.UnknownHostException

/** A user-safe, already-localisable message. Never contains tokens, phones or emails. */
data class AppError(val message: String, val retryable: Boolean = true, val code: String? = null)

/**
 * Single place that turns Postgrest / Auth / network failures into friendly text.
 * Raw server messages are only used when they are the human-written ones from our SQL
 * (e.g. the college-suggestion guard).
 */
object ErrorMapper {
    private val friendlyServerMessages = mapOf(
        "This college is already listed." to "This college is already listed. Pick it from the list instead.",
        "You already have 5 suggestions waiting for review." to "You already have 5 suggestions waiting for review. Please wait until they're checked.",
        "already submitted" to "You've already answered this month's survey. Thank you!",
        "scene out of order" to "This answer arrived out of order. Your progress has been reloaded.",
        "scenario not finished" to "Finish the last scene before completing the scenario.",
        "story unavailable" to "This scenario isn't available on the server yet. You can still play it on this device.",
        "not authenticated" to "Please sign in to continue."
    )

    fun map(t: Throwable): AppError {
        val raw = t.message.orEmpty()
        friendlyServerMessages.entries.firstOrNull { raw.contains(it.key, ignoreCase = true) }?.let {
            return AppError(it.value, retryable = false, code = it.key)
        }
        return when (t) {
            is UnknownHostException, is HttpRequestTimeoutException ->
                AppError("You're offline. Check your connection and try again.", code = "offline")
            is HttpRequestException, is IOException ->
                AppError("We couldn't reach the server. Check your connection and try again.", code = "network")
            is AuthRestException -> mapAuth(t)
            is RestException -> when {
                raw.contains("PGRST205") || raw.contains("does not exist") || raw.contains("Could not find the") ->
                    AppError("The server isn't set up yet. Showing information saved on this device.", code = "not_set_up")
                raw.contains("row-level security", true) || raw.contains("42501") ->
                    AppError("You don't have permission to do that.", retryable = false, code = "rls")
                raw.contains("duplicate key", true) || raw.contains("23505") ->
                    AppError("That's already saved.", retryable = false, code = "duplicate")
                else -> AppError("Something went wrong on the server. Please try again.", code = "server")
            }
            else -> AppError("Something went wrong. Please try again.", code = "unknown")
        }
    }

    private fun mapAuth(e: AuthRestException): AppError {
        val raw = (e.error + " " + e.message).lowercase()
        return when {
            "phone" in raw && ("provider" in raw || "disabled" in raw || "unsupported" in raw || "sms" in raw) ->
                AppError("Phone verification isn't available right now. Continue with Google or email.", retryable = false, code = "phone_disabled")
            "expired" in raw || "otp_expired" in raw ->
                AppError("That code has expired. Request a new one.", retryable = false, code = "otp_expired")
            "invalid" in raw && ("otp" in raw || "token" in raw) ->
                AppError("That code isn't right. Check it and try again.", retryable = false, code = "otp_invalid")
            "rate" in raw || "too many" in raw || "over_" in raw ->
                AppError("Too many attempts. Please wait a minute and try again.", retryable = false, code = "rate_limited")
            "email" in raw && ("disabled" in raw || "provider" in raw) ->
                AppError("Email sign-in isn't enabled for this project yet.", retryable = false, code = "email_disabled")
            "provider" in raw && "google" in raw ->
                AppError("Google sign-in isn't enabled for this project yet.", retryable = false, code = "google_disabled")
            else -> AppError("We couldn't sign you in. Please try again.", code = "auth")
        }
    }
}

/** Run a block and wrap failures through the mapper. */
suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> =
    try { Result.success(block()) } catch (c: kotlinx.coroutines.CancellationException) { throw c } catch (t: Throwable) {
        Result.failure(AppException(ErrorMapper.map(t), t))
    }

class AppException(val error: AppError, cause: Throwable? = null) : Exception(error.message, cause)

fun Throwable.toAppError(): AppError = (this as? AppException)?.error ?: ErrorMapper.map(this)
