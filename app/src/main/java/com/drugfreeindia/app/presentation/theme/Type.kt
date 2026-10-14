package com.drugfreeindia.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.drugfreeindia.app.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

val DfiFontFamily = PlusJakartaSans

private val DefaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

@Suppress("DEPRECATION")
private val DefaultPlatformTextStyle = PlatformTextStyle(
    includeFontPadding = false
)

private fun dfiStyle(
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp,
    fontFeatureSettings: String? = null
): TextStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontSize = fontSize,
    lineHeight = lineHeight,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
    color = DfiInk,
    lineHeightStyle = DefaultLineHeightStyle,
    platformStyle = DefaultPlatformTextStyle,
    fontFeatureSettings = fontFeatureSettings
)

@Immutable
data class DfiTypography(
    // Section 2.2 exact scale
    val screenTitle: TextStyle = dfiStyle(28.sp, 34.sp, FontWeight.Bold, (-0.4).sp),
    val display: TextStyle = dfiStyle(26.sp, 32.sp, FontWeight.Bold, (-0.3).sp),
    val titleL: TextStyle = dfiStyle(20.sp, 26.sp, FontWeight.SemiBold, (-0.2).sp),
    val titleM: TextStyle = dfiStyle(17.sp, 24.sp, FontWeight.SemiBold, 0.sp),
    val titleS: TextStyle = dfiStyle(15.sp, 20.sp, FontWeight.SemiBold, 0.sp),
    val bodyL: TextStyle = dfiStyle(15.sp, 22.sp, FontWeight.Normal, 0.sp),
    val bodyDialogue: TextStyle = dfiStyle(17.sp, 26.sp, FontWeight.Medium, 0.sp),
    val bodyM: TextStyle = dfiStyle(14.sp, 20.sp, FontWeight.Normal, 0.sp),
    val labelL: TextStyle = dfiStyle(14.sp, 18.sp, FontWeight.SemiBold, 0.1.sp),
    val labelM: TextStyle = dfiStyle(12.sp, 16.sp, FontWeight.Medium, 0.2.sp),
    val caption: TextStyle = dfiStyle(11.sp, 14.sp, FontWeight.Medium, 0.3.sp),
    val statXL: TextStyle = dfiStyle(44.sp, 48.sp, FontWeight.ExtraBold, (-1.0).sp, fontFeatureSettings = "tnum"),
    val statL: TextStyle = dfiStyle(30.sp, 34.sp, FontWeight.Bold, (-0.5).sp, fontFeatureSettings = "tnum"),

    // Compatibility aliases
    val titleLarge: TextStyle = dfiStyle(24.sp, 32.sp, FontWeight.SemiBold, (-0.3).sp),
    val titleMedium: TextStyle = dfiStyle(20.sp, 28.sp, FontWeight.SemiBold, (-0.2).sp),
    val titleSmall: TextStyle = dfiStyle(16.sp, 24.sp, FontWeight.SemiBold, 0.sp),
    val bodyLarge: TextStyle = dfiStyle(16.sp, 24.sp, FontWeight.Normal, 0.sp),
    val bodyMedium: TextStyle = dfiStyle(14.sp, 20.sp, FontWeight.Normal, 0.sp),
    val label: TextStyle = dfiStyle(12.sp, 16.sp, FontWeight.Medium, 0.2.sp),
    val stat: TextStyle = dfiStyle(36.sp, 40.sp, FontWeight.Bold, (-0.5).sp, fontFeatureSettings = "tnum")
)

val LocalDfiTypography = staticCompositionLocalOf { DfiTypography() }

fun createMaterialTypography(dfi: DfiTypography = DfiTypography()): Typography = Typography(
    displayLarge = dfi.display,
    displayMedium = dfi.display,
    displaySmall = dfi.screenTitle,
    headlineLarge = dfi.screenTitle,
    headlineMedium = dfi.titleLarge,
    headlineSmall = dfi.titleL,
    titleLarge = dfi.titleL,
    titleMedium = dfi.titleM,
    titleSmall = dfi.titleS,
    bodyLarge = dfi.bodyL,
    bodyMedium = dfi.bodyM,
    bodySmall = dfi.caption,
    labelLarge = dfi.labelL,
    labelMedium = dfi.labelM,
    labelSmall = dfi.caption
)

