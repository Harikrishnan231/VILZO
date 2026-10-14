package com.drugfreeindia.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val MaterialLightColorScheme = lightColorScheme(
    primary = DfiAccent,
    onPrimary = DfiSurface,
    primaryContainer = DfiSurfaceTint,
    onPrimaryContainer = DfiAccentDark,
    background = DfiBackground,
    onBackground = DfiInk,
    surface = DfiSurface,
    onSurface = DfiInk,
    surfaceVariant = DfiSurfaceMuted,
    onSurfaceVariant = DfiInkSecondary,
    outline = DfiOutline,
    outlineVariant = DfiOutline,
    error = DfiDanger,
    onError = DfiSurface
)

@Composable
fun DrugFreeIndiaTheme(
    content: @Composable () -> Unit
) {
    val colors = DfiColors()
    val typography = DfiTypography()
    val shapes = DfiShapes()
    val spacing = DfiSpacing()

    CompositionLocalProvider(
        LocalDfiColors provides colors,
        LocalDfiTypography provides typography,
        LocalDfiShapes provides shapes,
        LocalDfiSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = MaterialLightColorScheme,
            typography = createMaterialTypography(typography),
            content = content
        )
    }
}

object DfiTheme {
    val colors: DfiColors
        @Composable
        @ReadOnlyComposable
        get() = LocalDfiColors.current

    val typography: DfiTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalDfiTypography.current

    val shapes: DfiShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalDfiShapes.current

    val spacing: DfiSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalDfiSpacing.current

    val motion: DfiMotion
        get() = DfiMotion
}
