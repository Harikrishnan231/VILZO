package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow
import kotlin.math.roundToInt

@Composable
fun SegmentedTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackShape = RoundedCornerShape(22.dp)
    val thumbShape = RoundedCornerShape(18.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(trackShape)
            .background(DfiTheme.colors.surfaceMuted)
            .padding(4.dp)
    ) {
        val segmentCount = tabs.size.coerceAtLeast(1)
        val segmentWidth = maxWidth / segmentCount
        val animatedIndex by animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
            animationSpec = DfiTheme.motion.defaultSpring,
            label = "tabThumbAnim"
        )

        // Sliding white thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(x = (animatedIndex * segmentWidth.toPx()).roundToInt(), y = 0) }
                .width(segmentWidth)
                .fillMaxHeight()
                .softShadow(shape = thumbShape, elevation = 3.dp, ambientAlpha = 0.04f, spotAlpha = 0.08f)
                .background(DfiTheme.colors.surface, thumbShape)
        )

        // Tab items
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(thumbShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = DfiTheme.colors.accentDark.copy(alpha = 0.1f)),
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = DfiTheme.typography.labelL.copy(
                            color = if (isSelected) DfiTheme.colors.ink else DfiTheme.colors.inkSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
