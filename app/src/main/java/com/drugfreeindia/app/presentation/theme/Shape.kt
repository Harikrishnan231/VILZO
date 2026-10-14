package com.drugfreeindia.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Radii from §4.3 — deliberately not one radius for everything. */
@Immutable
data class DfiShapes(
    val hero: RoundedCornerShape = RoundedCornerShape(24.dp),
    val card: RoundedCornerShape = RoundedCornerShape(18.dp),
    val tile: RoundedCornerShape = RoundedCornerShape(12.dp),
    val input: RoundedCornerShape = RoundedCornerShape(14.dp),
    val button: RoundedCornerShape = RoundedCornerShape(14.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(percent = 50),
    val chip: RoundedCornerShape = RoundedCornerShape(10.dp),
    val sheet: RoundedCornerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
)

/** 4dp grid. */
@Immutable
data class DfiSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val s: Dp = 12.dp,
    val m: Dp = 16.dp,
    val l: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val section: Dp = 28.dp,
    val screen: Dp = 20.dp,
    val cardGap: Dp = 12.dp
)

val LocalDfiShapes = staticCompositionLocalOf { DfiShapes() }
val LocalDfiSpacing = staticCompositionLocalOf { DfiSpacing() }
