package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme

enum class DfiButtonVariant {
    Primary,
    Secondary,
    Ghost,
    Danger
}

@Composable
fun DfiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DfiButtonVariant = DfiButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isPill: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = DfiTheme.motion.defaultSpring,
        label = "btnScale"
    )

    val (bgColor, textColor) = when (variant) {
        DfiButtonVariant.Primary -> Pair(
            DfiTheme.colors.accent,
            DfiTheme.colors.surface
        )
        DfiButtonVariant.Secondary -> Pair(
            DfiTheme.colors.surfaceMuted,
            DfiTheme.colors.ink
        )
        DfiButtonVariant.Ghost -> Pair(
            Color.Transparent,
            DfiTheme.colors.accent
        )
        DfiButtonVariant.Danger -> Pair(
            DfiTheme.colors.danger,
            DfiTheme.colors.surface
        )
    }

    val shape = if (isPill) DfiTheme.shapes.pill else DfiTheme.shapes.button

    Surface(
        modifier = modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.4f)
            .heightIn(min = 52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                enabled = enabled,
                onClick = onClick
            ),
        shape = shape,
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leadingIcon?.let {
                Box(modifier = Modifier.padding(end = 8.dp)) { it() }
            }
            Text(
                text = text,
                style = DfiTheme.typography.labelL.copy(
                    color = textColor
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            trailingIcon?.let {
                Box(modifier = Modifier.padding(start = 8.dp)) { it() }
            }
        }
    }
}
