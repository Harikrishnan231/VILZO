package com.drugfreeindia.app.presentation.play.story

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun SceneIllustration(
    sceneKey: String,
    modifier: Modifier = Modifier,
    speakerName: String = ""
) {
    val skyGradient = Brush.verticalGradient(
        listOf(
            DfiTheme.colors.accentWash,
            DfiTheme.colors.accentSoft
        )
    )

    val skylineColor = DfiTheme.colors.inkTertiary.copy(alpha = 0.25f)
    val accentColor = DfiTheme.colors.accent
    val inkColor = DfiTheme.colors.ink
    val secondaryColor = DfiTheme.colors.inkSecondary
    val groundColor = DfiTheme.colors.accentSoft.copy(alpha = 0.7f)
    val shadowColor = Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(skyGradient)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Soft background skyline / architectural silhouettes
            when (sceneKey) {
                "sports_ground" -> {
                    // Bleachers and trees
                    val hillPath = Path().apply {
                        moveTo(0f, h * 0.72f)
                        quadraticTo(w * 0.35f, h * 0.60f, w * 0.7f, h * 0.68f)
                        quadraticTo(w * 0.85f, h * 0.72f, w, h * 0.65f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(hillPath, color = skylineColor)
                }
                "hostel" -> {
                    // Corridor doorways and pillars
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.1f, h * 0.3f), size = Size(w * 0.2f, h * 0.7f))
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.45f, h * 0.25f), size = Size(w * 0.22f, h * 0.75f))
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.78f, h * 0.35f), size = Size(w * 0.18f, h * 0.65f))
                }
                else -> {
                    // Terrace party / Campus skyline
                    val buildingWidth = w * 0.18f
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.05f, h * 0.40f), size = Size(buildingWidth, h * 0.6f))
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.26f, h * 0.32f), size = Size(buildingWidth * 1.1f, h * 0.68f))
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.50f, h * 0.45f), size = Size(buildingWidth * 0.9f, h * 0.55f))
                    drawRect(color = skylineColor, topLeft = Offset(w * 0.72f, h * 0.36f), size = Size(buildingWidth * 1.2f, h * 0.64f))
                }
            }

            // 2. String lights on a soft curve (warm festive/terrace ambiance)
            val lightWire = Path().apply {
                moveTo(0f, h * 0.22f)
                quadraticTo(w * 0.5f, h * 0.38f, w, h * 0.20f)
            }
            drawPath(
                lightWire,
                color = accentColor.copy(alpha = 0.4f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Warm dot lights along the curve
            val numDots = 8
            for (i in 1..numDots) {
                val t = i / (numDots + 1f)
                // Quadratic bezier formula: (1-t)^2 * p0 + 2(1-t)t * p1 + t^2 * p2
                val px = (1 - t) * (1 - t) * 0f + 2 * (1 - t) * t * (w * 0.5f) + t * t * w
                val py = (1 - t) * (1 - t) * (h * 0.22f) + 2 * (1 - t) * t * (h * 0.38f) + t * t * (h * 0.20f)

                // Glow ring
                drawCircle(
                    color = accentColor.copy(alpha = 0.3f),
                    radius = 4.5.dp.toPx(),
                    center = Offset(px, py)
                )
                // Bright center
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(px, py)
                )
            }

            // 3. Ground base
            drawRect(
                color = groundColor,
                topLeft = Offset(0f, h * 0.82f),
                size = Size(w, h * 0.18f)
            )

            // 4. Flat character silhouettes (Circle head + Rounded-rect body + ground shadow)
            // Left character (Friend)
            val c1X = w * 0.26f
            val c1GroundY = h * 0.84f
            drawOval(
                color = shadowColor,
                topLeft = Offset(c1X - 22.dp.toPx(), c1GroundY - 4.dp.toPx()),
                size = Size(44.dp.toPx(), 8.dp.toPx())
            )
            drawRoundRect(
                color = secondaryColor,
                topLeft = Offset(c1X - 16.dp.toPx(), c1GroundY - 50.dp.toPx()),
                size = Size(32.dp.toPx(), 48.dp.toPx()),
                cornerRadius = CornerRadius(14.dp.toPx())
            )
            drawCircle(
                color = secondaryColor,
                radius = 11.dp.toPx(),
                center = Offset(c1X, c1GroundY - 64.dp.toPx())
            )

            // Center character (Speaker / Active participant - slightly larger & highlighted)
            val c2X = w * 0.50f
            val c2GroundY = h * 0.86f
            drawOval(
                color = shadowColor,
                topLeft = Offset(c2X - 26.dp.toPx(), c2GroundY - 5.dp.toPx()),
                size = Size(52.dp.toPx(), 10.dp.toPx())
            )
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(c2X - 20.dp.toPx(), c2GroundY - 62.dp.toPx()),
                size = Size(40.dp.toPx(), 60.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx())
            )
            drawCircle(
                color = accentColor,
                radius = 13.dp.toPx(),
                center = Offset(c2X, c2GroundY - 78.dp.toPx())
            )

            // Right character (Arjun / Listener)
            val c3X = w * 0.74f
            val c3GroundY = h * 0.84f
            drawOval(
                color = shadowColor,
                topLeft = Offset(c3X - 22.dp.toPx(), c3GroundY - 4.dp.toPx()),
                size = Size(44.dp.toPx(), 8.dp.toPx())
            )
            drawRoundRect(
                color = inkColor,
                topLeft = Offset(c3X - 16.dp.toPx(), c3GroundY - 52.dp.toPx()),
                size = Size(32.dp.toPx(), 50.dp.toPx()),
                cornerRadius = CornerRadius(14.dp.toPx())
            )
            drawCircle(
                color = inkColor,
                radius = 11.dp.toPx(),
                center = Offset(c3X, c3GroundY - 66.dp.toPx())
            )
        }
    }
}
