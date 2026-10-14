package com.drugfreeindia.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val DfiBackground = Color(0xFFF6F8FA)
val DfiSurface = Color(0xFFFFFFFF)
val DfiSurfaceMuted = Color(0xFFF1F4F7)
val DfiSurfaceTint = Color(0xFFEAF6F3)
val DfiOutline = Color(0xFFE7ECF1)
val DfiInk = Color(0xFF0F1B2D)
val DfiInkSecondary = Color(0xFF566678)
val DfiInkTertiary = Color(0xFF8795A3)
val DfiAccent = Color(0xFF0E9F8E)
val DfiAccentDark = Color(0xFF0B7D70)
val DfiAccentSoft = Color(0xFFDDF2EE)
val DfiAccentWash = Color(0xFFF2FAF8)
val DfiSuccess = Color(0xFF1E9E6A)
val DfiCaution = Color(0xFFC98A1B)
val DfiDanger = Color(0xFFC8483F)
val DfiDangerTint = Color(0xFFFBEDEC)

@Immutable
data class DfiColors(
    val background: Color = DfiBackground,
    val surface: Color = DfiSurface,
    val surfaceMuted: Color = DfiSurfaceMuted,
    val surfaceTint: Color = DfiSurfaceTint,
    val outline: Color = DfiOutline,
    val ink: Color = DfiInk,
    val inkSecondary: Color = DfiInkSecondary,
    val inkTertiary: Color = DfiInkTertiary,
    val accent: Color = DfiAccent,
    val accentDark: Color = DfiAccentDark,
    val accentSoft: Color = DfiAccentSoft,
    val accentWash: Color = DfiAccentWash,
    val success: Color = DfiSuccess,
    val caution: Color = DfiCaution,
    val danger: Color = DfiDanger,
    val dangerTint: Color = DfiDangerTint
)

val LocalDfiColors = staticCompositionLocalOf { DfiColors() }

