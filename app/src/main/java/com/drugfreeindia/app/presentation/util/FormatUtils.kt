package com.drugfreeindia.app.presentation.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val indianNumberFormat = NumberFormat.getInstance(Locale("en", "IN"))

    fun formatNumber(number: Number): String {
        return indianNumberFormat.format(number)
    }

    fun formatPoints(points: Int): String {
        return "${formatNumber(points)} pts"
    }

    fun formatStudents(count: Int): String {
        return "${formatNumber(count)} students"
    }
}

fun formatNumber(number: Number): String = FormatUtils.formatNumber(number)
fun formatPoints(points: Int): String = FormatUtils.formatPoints(points)
fun formatStudents(count: Int): String = FormatUtils.formatStudents(count)
