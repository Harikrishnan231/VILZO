package com.drugfreeindia.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun DrugFreeIndiaTheme(
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = remember { DfiColors() }
    val typography = remember { DfiTypography() }
    val context = LocalContext.current
    val reduce = reduceMotion || remember { systemAnimationsDisabled(context) }

    val scheme = lightColorScheme(
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        primaryContainer = colors.primarySoft,
        onPrimaryContainer = colors.primaryDark,
        secondary = colors.primaryDark,
        background = colors.background,
        onBackground = colors.onSurface,
        surface = colors.surface,
        onSurface = colors.onSurface,
        surfaceVariant = colors.surfaceMuted,
        onSurfaceVariant = colors.onSurfaceVariant,
        surfaceContainer = colors.surface,
        surfaceContainerLow = colors.surface,
        surfaceContainerHigh = colors.surface,
        surfaceContainerHighest = colors.surfaceMuted,
        outline = colors.outline,
        outlineVariant = colors.outline,
        error = colors.error,
        onError = colors.onPrimary
    )

    CompositionLocalProvider(
        LocalDfiColors provides colors,
        LocalDfiTypography provides typography,
        LocalDfiShapes provides DfiShapes(),
        LocalDfiSpacing provides DfiSpacing(),
        LocalReduceMotion provides reduce
    ) {
        MaterialTheme(colorScheme = scheme, typography = materialTypography(typography), content = content)
    }
}

object DfiTheme {
    val colors: DfiColors
        @Composable @ReadOnlyComposable get() = LocalDfiColors.current
    val typography: DfiTypography
        @Composable @ReadOnlyComposable get() = LocalDfiTypography.current
    val shapes: DfiShapes
        @Composable @ReadOnlyComposable get() = LocalDfiShapes.current
    val spacing: DfiSpacing
        @Composable @ReadOnlyComposable get() = LocalDfiSpacing.current
}
