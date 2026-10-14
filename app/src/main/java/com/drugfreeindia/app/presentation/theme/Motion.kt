package com.drugfreeindia.app.presentation.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp

object DfiMotion {
    val defaultSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.8f,
        stiffness = 380f
    )

    val dpSpring: SpringSpec<Dp> = spring(
        dampingRatio = 0.8f,
        stiffness = 380f
    )

    val snappySpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    const val pressedScale = 0.97f
    const val bentoStaggerDelayMs = 40
}
