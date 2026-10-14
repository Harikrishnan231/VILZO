package com.drugfreeindia.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic colour tokens (master prompt §4.1). Screens must use these, never raw hex. */
@Immutable
data class DfiColors(
    val background: Color = Color(0xFFF6F8FA),
    val surface: Color = Color(0xFFFFFFFF),
    val onSurface: Color = Color(0xFF0F1B2D),
    val onSurfaceVariant: Color = Color(0xFF617184),
    val primary: Color = Color(0xFF0E9F8E),
    val primaryDark: Color = Color(0xFF087F70),
    val primarySoft: Color = Color(0xFFDDF2EE),
    val outline: Color = Color(0xFFE3E9EE),
    val surfaceMuted: Color = Color(0xFFEEF2F5),
    val warning: Color = Color(0xFFB7791F),
    val error: Color = Color(0xFFC0392B),
    val success: Color = Color(0xFF1E8E5A),
    val onPrimary: Color = Color(0xFFFFFFFF),
    /** Very soft navy used only for shadows (≤8%). */
    val shadow: Color = Color(0x140F1B2D),
    /** Neutral grey for switch off-state / disabled. */
    val disabled: Color = Color(0xFFB6C1CC)
)

val LocalDfiColors = staticCompositionLocalOf { DfiColors() }
