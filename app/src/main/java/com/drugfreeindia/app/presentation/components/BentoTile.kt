package com.drugfreeindia.app.presentation.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme

enum class BentoSpan {
    Span1x1, // Half width, square-ish
    Span2x1, // Full width, horizontal
    Span1x2, // Half width, tall
    Span2x2  // Full width, tall
}

@Composable
fun BentoTile(
    span: BentoSpan,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DfiTheme.colors.surface,
    borderColor: Color? = null,
    padding: Dp? = null,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val minHeight = when (span) {
        BentoSpan.Span1x1 -> 146.dp
        BentoSpan.Span2x1 -> 136.dp
        BentoSpan.Span1x2 -> 304.dp
        BentoSpan.Span2x2 -> 304.dp
    }

    val cornerRadius = when (span) {
        BentoSpan.Span1x1 -> 20.dp
        else -> 24.dp
    }

    val finalPadding = padding ?: when (span) {
        BentoSpan.Span1x1 -> 16.dp
        else -> 20.dp
    }

    val finalBorderColor = borderColor ?: if (span == BentoSpan.Span1x1) {
        DfiTheme.colors.outline.copy(alpha = 0.6f)
    } else null

    DfiCard(
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        backgroundColor = backgroundColor,
        borderColor = finalBorderColor,
        cornerRadius = cornerRadius,
        padding = finalPadding,
        elevation = elevation,
        onClick = onClick,
        content = content
    )
}

