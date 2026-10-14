package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun ProgressRing(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    size: Dp = 104.dp,
    strokeWidth: Dp = 12.dp,
    color: Color = DfiTheme.colors.accent,
    trackColor: Color = DfiTheme.colors.accentSoft,
    contentDescription: String? = null,
    centerContent: (@Composable () -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = DfiTheme.motion.defaultSpring,
        label = "ringProgress"
    )

    val accessibilityModifier = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(accessibilityModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val arcSize = Size(canvasWidth - strokePx, canvasHeight - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Draw background track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Draw active progress arc
            val sweep = animatedProgress * 360f
            if (sweep > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Small end-dot highlight
                val angleRad = Math.toRadians((-90f + sweep).toDouble())
                val radius = (canvasWidth - strokePx) / 2f
                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f
                val dotX = (centerX + radius * Math.cos(angleRad)).toFloat()
                val dotY = (centerY + radius * Math.sin(angleRad)).toFloat()

                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.28f,
                    center = Offset(dotX, dotY)
                )
            }
        }
        centerContent?.invoke()
    }
}
