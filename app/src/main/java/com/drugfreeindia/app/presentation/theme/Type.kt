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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.drugfreeindia.app.R

/** Bundled Plus Jakarta Sans (static TTFs in res/font). */
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

private val LineStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)

@Suppress("DEPRECATION")
private val NoFontPadding = PlatformTextStyle(includeFontPadding = false)

private fun style(
    size: TextUnit,
    line: TextUnit,
    weight: FontWeight,
    tracking: TextUnit = 0.sp,
    features: String? = null
) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontSize = size,
    lineHeight = line,
    fontWeight = weight,
    letterSpacing = tracking,
    lineHeightStyle = LineStyle,
    platformStyle = NoFontPadding,
    fontFeatureSettings = features
)

/** Type scale from master prompt §4.2. */
@Immutable
data class DfiTypography(
    val screenTitle: TextStyle = style(28.sp, 34.sp, FontWeight.Bold, (-0.4).sp),
    val sectionTitle: TextStyle = style(18.sp, 24.sp, FontWeight.SemiBold),
    val cardTitle: TextStyle = style(16.sp, 22.sp, FontWeight.SemiBold),
    val body: TextStyle = style(15.sp, 22.sp, FontWeight.Normal),
    val bodySmall: TextStyle = style(14.sp, 20.sp, FontWeight.Normal),
    val caption: TextStyle = style(12.sp, 16.sp, FontWeight.Medium),
    val captionLarge: TextStyle = style(13.sp, 18.sp, FontWeight.Medium),
    val label: TextStyle = style(15.sp, 20.sp, FontWeight.SemiBold),
    val labelSmall: TextStyle = style(14.sp, 20.sp, FontWeight.SemiBold),
    val dialogue: TextStyle = style(17.sp, 24.sp, FontWeight.Medium),
    val statLarge: TextStyle = style(44.sp, 48.sp, FontWeight.ExtraBold, (-1).sp, "tnum"),
    val stat: TextStyle = style(28.sp, 32.sp, FontWeight.Bold, (-0.4).sp, "tnum"),
    val heroTitle: TextStyle = style(24.sp, 30.sp, FontWeight.Bold, (-0.3).sp)
)

val LocalDfiTypography = staticCompositionLocalOf { DfiTypography() }

fun materialTypography(t: DfiTypography) = Typography(
    displayLarge = t.statLarge,
    displayMedium = t.stat,
    displaySmall = t.screenTitle,
    headlineLarge = t.screenTitle,
    headlineMedium = t.heroTitle,
    headlineSmall = t.sectionTitle,
    titleLarge = t.sectionTitle,
    titleMedium = t.cardTitle,
    titleSmall = t.labelSmall,
    bodyLarge = t.body,
    bodyMedium = t.bodySmall,
    bodySmall = t.caption,
    labelLarge = t.label,
    labelMedium = t.captionLarge,
    labelSmall = t.caption
)
