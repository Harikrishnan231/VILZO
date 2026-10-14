package com.drugfreeindia.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class DfiShapes(
    val heroCard: RoundedCornerShape = RoundedCornerShape(28.dp),
    val card: RoundedCornerShape = RoundedCornerShape(24.dp),
    val tile: RoundedCornerShape = RoundedCornerShape(20.dp),
    val iconTile: RoundedCornerShape = RoundedCornerShape(14.dp),
    val chip: RoundedCornerShape = RoundedCornerShape(10.dp),
    val input: RoundedCornerShape = RoundedCornerShape(16.dp),
    val button: RoundedCornerShape = RoundedCornerShape(100.dp), // pill
    val pill: RoundedCornerShape = RoundedCornerShape(100.dp),
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
)

@Immutable
data class DfiSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val s: Dp = 12.dp,
    val m: Dp = 16.dp,
    val l: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 28.dp,
    val sectionGap: Dp = 28.dp,
    val sectionTitleToContent: Dp = 12.dp,
    val cardGap: Dp = 12.dp,
    val cardInnerPadding: Dp = 20.dp,
    val smallTilePadding: Dp = 16.dp,
    val screenHorizontal: Dp = 20.dp,
    val bentoGap: Dp = 12.dp
)

val LocalDfiShapes = staticCompositionLocalOf { DfiShapes() }
val LocalDfiSpacing = staticCompositionLocalOf { DfiSpacing() }

