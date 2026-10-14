package com.drugfreeindia.app.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.softShadow(
    shape: Shape,
    elevation: Dp = 10.dp,
    ambientAlpha: Float = 0.05f,
    spotAlpha: Float = 0.09f
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = DfiInk.copy(alpha = ambientAlpha),
    spotColor = DfiInk.copy(alpha = spotAlpha)
)
