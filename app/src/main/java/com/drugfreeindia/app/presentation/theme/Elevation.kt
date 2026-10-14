package com.drugfreeindia.app.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF0F1B2D)

/** Very soft shadow (≤8% navy) per §4.3. Use sparingly; prefer 1dp outline borders. */
fun Modifier.softShadow(shape: Shape, elevation: Dp = 8.dp): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = Navy.copy(alpha = 0.04f),
    spotColor = Navy.copy(alpha = 0.08f)
)
