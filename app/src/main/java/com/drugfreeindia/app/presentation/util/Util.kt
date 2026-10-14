package com.drugfreeindia.app.presentation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.NumberFormat
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** All outgoing actions go through the system so the user stays in control (no CALL_PHONE). */
object Intents {
    private fun start(context: Context, intent: Intent, fallback: String) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, fallback, Toast.LENGTH_LONG).show()
        }
    }

    /** Opens the dialer with the number filled in. Never places the call itself. */
    fun dial(context: Context, number: String) =
        start(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number.filter { it.isDigit() || it == '+' })), "No phone app found to dial $number")

    fun sms(context: Context, number: String, body: String) =
        start(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number.filter { it.isDigit() || it == '+' })).putExtra("sms_body", body),
            "No messaging app found")

    fun openUrl(context: Context, url: String) {
        val u = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(u)), "No browser found")
    }

    fun directions(context: Context, lat: Double?, lng: Double?, query: String) {
        val uri = if (lat != null && lng != null) Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(query)})")
        else Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        start(context, Intent(Intent.ACTION_VIEW, uri), "No maps app found")
    }
}

object Format {
    private val indian = NumberFormat.getIntegerInstance(Locale("en", "IN"))
    fun number(n: Number): String = indian.format(n)

    fun greeting(now: LocalTime = LocalTime.now()): String = when (now.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    fun firstName(full: String?): String? = full?.trim()?.split(Regex("\\s+"))?.firstOrNull()?.takeIf { it.isNotBlank() }

    private fun parse(iso: String?): OffsetDateTime? = iso?.let { runCatching { OffsetDateTime.parse(it) }.getOrNull() }

    fun dateTime(iso: String?): String? = parse(iso)?.atZoneSameInstant(ZoneId.systemDefault())
        ?.format(DateTimeFormatter.ofPattern("EEE, d MMM · h:mm a", Locale.ENGLISH))

    fun dayBadge(iso: String?): Pair<String, String>? = parse(iso)?.atZoneSameInstant(ZoneId.systemDefault())?.let {
        it.format(DateTimeFormatter.ofPattern("d")) to it.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)).uppercase()
    }

    fun isUpcoming(iso: String?): Boolean = parse(iso)?.isAfter(OffsetDateTime.now().minusHours(3)) ?: false

    /** "2026-10" -> "Oct" */
    fun monthShort(key: String): String = runCatching {
        java.time.YearMonth.parse(key.take(7)).format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))
    }.getOrDefault(key)

    fun minutes(seconds: Int): String = if (seconds < 60) "$seconds sec" else "${(seconds + 30) / 60} min"

    fun maskPhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        val last4 = digits.takeLast(4)
        return "+91 ••••• •$last4"
    }

    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    fun formatDistance(km: Double): String = when {
        km < 1.0 -> "Nearby"
        km < 10.0 -> String.format(Locale.US, "%.1f km away", km)
        else -> "${km.toInt()} km away"
    }
}

const val PRACTICE_SCORE_LABEL = "A practice indicator based on your scenario choices, not a clinical or behavioural measure."
const val PRACTICE_SCORE_EMPTY = "Play a few scenarios to see your practice score"
