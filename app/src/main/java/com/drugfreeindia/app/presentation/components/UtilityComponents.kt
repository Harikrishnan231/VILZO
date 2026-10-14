package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow

@Composable
fun IconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = DfiTheme.colors.accent,
    backgroundColor: Color = DfiTheme.colors.accentSoft,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    onClick: (() -> Unit)? = null
) {
    val ringGradient = Brush.sweepGradient(
        listOf(DfiTheme.colors.accent, DfiTheme.colors.accentSoft, DfiTheme.colors.accent)
    )

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = false, radius = size / 2f),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(clickModifier)
            .background(ringGradient, CircleShape)
            .padding(2.5.dp)
            .background(DfiTheme.colors.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size - 8.dp)
                .background(DfiTheme.colors.accentSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials.take(2).uppercase(),
                style = DfiTheme.typography.labelL.copy(
                    color = DfiTheme.colors.accentDark,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val finalLabel = actionLabel ?: actionText
    val finalAction = onAction ?: onActionClick
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = DfiTheme.typography.titleM,
            color = DfiTheme.colors.ink
        )
        if (finalLabel != null && finalAction != null) {
            Text(
                text = finalLabel,
                style = DfiTheme.typography.labelL.copy(
                    color = DfiTheme.colors.accentDark,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { finalAction() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun QuickExitChip(
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = DfiTheme.colors.ink.copy(alpha = 0.1f)),
                onClick = onQuickExit
            ),
        color = DfiTheme.colors.surfaceMuted,
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(1.dp, DfiTheme.colors.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.quick_exit_btn),
                tint = DfiTheme.colors.ink,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.quick_exit_btn),
                style = DfiTheme.typography.labelM.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DfiTheme.colors.ink
                )
            )
        }
    }
}

@Composable
fun QuickExitButton(
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    QuickExitChip(onQuickExit = onQuickExit, modifier = modifier)
}

@Composable
fun ListRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTile: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.1f)),
            onClick = onClick
        )
    } else Modifier

    val leadingContent = leading ?: iconTile

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .then(clickModifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.width(14.dp))
        } else if (icon != null) {
            IconTile(icon = icon)
            Spacer(modifier = Modifier.width(14.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = DfiTheme.typography.titleM,
                color = DfiTheme.colors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = DfiTheme.typography.bodyM,
                    color = DfiTheme.colors.inkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        trailing?.let {
            Spacer(modifier = Modifier.width(8.dp))
            it()
        } ?: run {
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = DfiTheme.colors.inkTertiary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
fun GroupedList(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .softShadow(shape = shape, elevation = 6.dp),
        shape = shape,
        color = DfiTheme.colors.surface,
        border = BorderStroke(1.dp, DfiTheme.colors.outline.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun GroupedListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp),
        thickness = 1.dp,
        color = DfiTheme.colors.outline
    )
}

@Composable
fun DfiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = DfiTheme.colors.surface,
            checkedTrackColor = DfiTheme.colors.accent,
            checkedBorderColor = DfiTheme.colors.accent,
            uncheckedThumbColor = DfiTheme.colors.surface,
            uncheckedTrackColor = DfiTheme.colors.outline,
            uncheckedBorderColor = DfiTheme.colors.outline
        )
    )
}

@Composable
fun DfiChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val bgColor = if (isSelected) DfiTheme.colors.accentSoft else DfiTheme.colors.surfaceMuted
    val textColor = if (isSelected) DfiTheme.colors.accentDark else DfiTheme.colors.inkSecondary
    val shape = RoundedCornerShape(10.dp)

    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = bgColor,
        border = null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Box(modifier = Modifier.padding(end = 6.dp)) { it() }
            }
            Text(
                text = text,
                style = DfiTheme.typography.labelM.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = textColor
                ),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun DfiChipRow(
    items: List<String>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onSelect: ((Int) -> Unit)? = null,
    selectedItem: String? = null,
    onItemSelected: ((String) -> Unit)? = null
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = if (selectedItem != null) item == selectedItem else index == selectedIndex
            DfiChip(
                text = item,
                isSelected = isSelected,
                onClick = {
                    onItemSelected?.invoke(item)
                    onSelect?.invoke(index)
                }
            )
        }
    }
}

@Composable
fun ActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = DfiTheme.colors.accent,
    iconBg: Color = DfiTheme.colors.accentSoft,
    badgeText: String? = null
) {
    ListRow(
        title = title,
        subtitle = subtitle,
        iconTile = {
            IconTile(
                icon = icon,
                tint = iconTint,
                backgroundColor = iconBg
            )
        },
        trailing = badgeText?.let {
            {
                Text(
                    text = it,
                    style = DfiTheme.typography.labelM.copy(
                        color = DfiTheme.colors.accentDark,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .background(DfiTheme.colors.surfaceTint, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun StreakFlame(
    streakDays: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = DfiTheme.colors.accent
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = "Streak",
            tint = activeColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$streakDays days",
            style = DfiTheme.typography.statL.copy(color = DfiTheme.colors.ink)
        )
    }
}

@Composable
fun PrivacyNote(
    text: String = stringResource(R.string.privacy_banner_text),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DfiTheme.colors.surfaceMuted, shape = RoundedCornerShape(14.dp))
            .border(1.dp, DfiTheme.colors.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Shield,
            contentDescription = null,
            tint = DfiTheme.colors.accentDark,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = DfiTheme.typography.bodyM.copy(color = DfiTheme.colors.inkSecondary)
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Info,
    actionButton: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(DfiTheme.colors.surfaceMuted, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DfiTheme.colors.inkTertiary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = DfiTheme.typography.titleM,
            color = DfiTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = DfiTheme.typography.bodyM,
            color = DfiTheme.colors.inkSecondary
        )
        actionButton?.let {
            Spacer(modifier = Modifier.height(16.dp))
            it()
        }
    }
}

@Composable
fun Shimmer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        DfiTheme.colors.surfaceMuted,
        Color.White,
        DfiTheme.colors.surfaceMuted
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}
