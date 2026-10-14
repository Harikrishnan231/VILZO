package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow
import kotlin.math.roundToInt

val LocalBottomBarPadding = compositionLocalOf<Dp> { 108.dp }

data class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun DfiScreen(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    tabs: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = DfiTheme.spacing.screenHorizontal,
                        end = DfiTheme.spacing.screenHorizontal,
                        top = 12.dp,
                        bottom = 12.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navigationIcon?.let {
                            Box(modifier = Modifier.padding(end = 12.dp)) { it() }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = DfiTheme.typography.screenTitle,
                                color = DfiTheme.colors.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!subtitle.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = subtitle,
                                    style = DfiTheme.typography.bodyM,
                                    color = DfiTheme.colors.inkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    actions?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            it()
                        }
                    }
                }

                if (tabs != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    tabs()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DfiTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = DfiTheme.colors.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                navigationIcon?.let {
                    Box(modifier = Modifier.padding(end = 12.dp)) { it() }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = DfiTheme.typography.titleM,
                        color = DfiTheme.colors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    subtitle?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            actions?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    it()
                }
            }
        }
    }
}

@Composable
fun DfiBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(28.dp)
    val pillShape = RoundedCornerShape(15.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 40dp vertical gradient scrim behind bar (Background @ 0% -> Background @ 100%)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DfiTheme.colors.background.copy(alpha = 0f),
                            DfiTheme.colors.background.copy(alpha = 0.95f),
                            DfiTheme.colors.background
                        )
                    )
                )
        )

        // Floating Bar
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(68.dp)
                .softShadow(shape = barShape, elevation = 12.dp, ambientAlpha = 0.06f, spotAlpha = 0.12f)
                .background(DfiTheme.colors.surface, barShape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val tintColor = if (isSelected) DfiTheme.colors.accent else DfiTheme.colors.inkTertiary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(pillShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = DfiTheme.colors.accent.copy(alpha = 0.15f)),
                                onClick = { onItemClick(item.route) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(30.dp)
                                    .background(
                                        color = if (isSelected) DfiTheme.colors.accentSoft else Color.Transparent,
                                        shape = pillShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(id = item.labelResId),
                                    tint = tintColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(id = item.labelResId),
                                style = DfiTheme.typography.caption.copy(
                                    color = tintColor,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}
