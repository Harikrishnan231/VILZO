package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow

@Composable
fun DfiCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DfiTheme.colors.surface,
    borderColor: Color? = null,
    cornerRadius: Dp = 24.dp,
    padding: Dp = 20.dp,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = DfiTheme.motion.defaultSpring,
        label = "cardScale"
    )

    val shape = RoundedCornerShape(cornerRadius)
    val borderStroke = borderColor?.let { BorderStroke(1.dp, it) }

    Surface(
        modifier = modifier
            .scale(scale)
            .softShadow(shape = shape, elevation = elevation),
        shape = shape,
        color = backgroundColor,
        border = borderStroke
    ) {
        val clickModifier = if (onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.12f)),
                onClick = onClick
            )
        } else Modifier

        Box(
            modifier = Modifier
                .then(clickModifier)
                .padding(padding)
        ) {
            content()
        }
    }
}

@Composable
fun HeroCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    padding: Dp = 20.dp,
    elevation: Dp = 10.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val gradient = Brush.verticalGradient(
        colors = listOf(DfiTheme.colors.accentWash, DfiTheme.colors.surface)
    )

    Surface(
        modifier = modifier.softShadow(shape = shape, elevation = elevation),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.12f)),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClick
                        )
                    } else Modifier
                )
                .padding(padding)
        ) {
            content()
        }
    }
}

@Composable
fun TintCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    padding: Dp = 20.dp,
    backgroundColor: Color = DfiTheme.colors.surfaceTint,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        border = null
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.12f)),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClick
                        )
                    } else Modifier
                )
                .padding(padding)
        ) {
            content()
        }
    }
}

