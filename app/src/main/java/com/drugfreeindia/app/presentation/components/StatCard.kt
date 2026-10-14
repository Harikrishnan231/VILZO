package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    delta: String? = null,
    trailing: String? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    DfiCard(
        modifier = modifier,
        cornerRadius = 20.dp,
        padding = 16.dp,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = DfiTheme.typography.labelM,
                    color = DfiTheme.colors.inkSecondary
                )
                if (trailingContent != null) {
                    trailingContent()
                } else if (trailing != null) {
                    Text(
                        text = trailing,
                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = DfiTheme.typography.statL,
                    color = DfiTheme.colors.ink
                )
                delta?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DfiTheme.colors.accentSoft)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NorthEast,
                            contentDescription = null,
                            tint = DfiTheme.colors.accentDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = it,
                            style = DfiTheme.typography.labelM.copy(
                                color = DfiTheme.colors.accentDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    deltaText: String? = null,
    isDeltaPositive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    StatTile(
        label = label,
        value = value,
        modifier = modifier,
        delta = deltaText
    )
}

@Composable
fun Sparkline(
    points: List<Float>,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
    lineColor: Color = DfiTheme.colors.accent,
    fillColor: Color = DfiTheme.colors.accent.copy(alpha = 0.18f),
    strokeWidth: Dp = 2.5.dp
) {
    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (points.isEmpty()) return@Canvas
            val width = size.width
            val height = size.height

            val minVal = (points.minOrNull() ?: 0f) - 4f
            val maxVal = (points.maxOrNull() ?: 100f) + 4f
            val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

            val stepX = width / (points.size - 1).coerceAtLeast(1)

            val coords = points.mapIndexed { index, value ->
                val x = index * stepX
                val normalizedY = 1f - ((value - minVal) / range)
                val y = (normalizedY * (height - 12.dp.toPx())) + 6.dp.toPx()
                Offset(x, y)
            }

            val path = Path()
            val fillPath = Path()

            coords.forEachIndexed { i, pt ->
                if (i == 0) {
                    path.moveTo(pt.x, pt.y)
                    fillPath.moveTo(pt.x, height)
                    fillPath.lineTo(pt.x, pt.y)
                } else {
                    val prev = coords[i - 1]
                    val cx = (prev.x + pt.x) / 2f
                    path.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                    fillPath.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Vertical gradient fill: Accent @ 18% -> 0%
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColor, Color.Transparent)
                )
            )

            // Cubic line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // End dot with 6dp halo
            coords.lastOrNull()?.let { last ->
                drawCircle(
                    color = lineColor.copy(alpha = 0.25f),
                    radius = 8.dp.toPx(),
                    center = last
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = last
                )
                drawCircle(
                    color = lineColor,
                    radius = 2.5.dp.toPx(),
                    center = last
                )
            }
        }

        if (labels != null && labels.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        style = DfiTheme.typography.caption.copy(fontSize = 10.sp),
                        color = DfiTheme.colors.inkTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonBar(
    label: String,
    before: Int,
    after: Int,
    modifier: Modifier = Modifier
) {
    val deltaPercent = if (before > 0) "+${((after - before) * 100 / before)}%" else "+${after}%"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = DfiTheme.typography.titleS,
                color = DfiTheme.colors.ink
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$before → $after",
                    style = DfiTheme.typography.bodyM.copy(fontWeight = FontWeight.SemiBold),
                    color = DfiTheme.colors.inkSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DfiTheme.colors.accentSoft)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = deltaPercent,
                        style = DfiTheme.typography.labelM.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Single 10dp track with faint before segment and solid after segment
        val barShape = RoundedCornerShape(5.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(barShape)
                .background(DfiTheme.colors.surfaceMuted)
        ) {
            // After portion (solid)
            val afterFraction = (after / 100f).coerceIn(0.05f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(afterFraction)
                    .fillMaxHeight()
                    .background(DfiTheme.colors.accent, barShape)
            )

            // Before portion (faint segment on top)
            val beforeFraction = (before / 100f).coerceIn(0.05f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(beforeFraction)
                    .fillMaxHeight()
                    .background(DfiTheme.colors.accentSoft.copy(alpha = 0.85f), barShape)
            )
        }
    }
}

// Backward compatibility alias for BarMini
@Composable
fun BarMini(
    label: String,
    beforeVal: Int,
    afterVal: Int,
    delta: String,
    modifier: Modifier = Modifier
) {
    ComparisonBar(label = label, before = beforeVal, after = afterVal, modifier = modifier)
}
