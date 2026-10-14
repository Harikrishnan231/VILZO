package com.drugfreeindia.app.presentation.theme

import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext

/** True when the user enabled in-app "Reduce motion" OR system "Remove animations". */
val LocalReduceMotion = compositionLocalOf { false }

fun systemAnimationsDisabled(context: android.content.Context): Boolean = runCatching {
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
}.getOrDefault(false)

object DfiMotion {
    const val SHORT = 150
    const val MEDIUM = 220
    const val pressedScale = 0.97f

    @Composable
    @ReadOnlyComposable
    fun <T> tweenOrSnap(durationMs: Int = MEDIUM): androidx.compose.animation.core.FiniteAnimationSpec<T> =
        if (LocalReduceMotion.current) snap() else tween(durationMs, easing = FastOutSlowInEasing)

    @Composable
    @ReadOnlyComposable
    fun <T> springOrSnap(): androidx.compose.animation.core.FiniteAnimationSpec<T> =
        if (LocalReduceMotion.current) snap() else spring(dampingRatio = 0.8f, stiffness = 380f)

    fun <T> spec(durationMs: Int = MEDIUM, reduce: Boolean = false): androidx.compose.animation.core.FiniteAnimationSpec<T> =
        if (reduce) snap() else tween(durationMs, easing = FastOutSlowInEasing)

    @Composable
    fun reduce(): Boolean = LocalReduceMotion.current || systemAnimationsDisabled(LocalContext.current)
}
