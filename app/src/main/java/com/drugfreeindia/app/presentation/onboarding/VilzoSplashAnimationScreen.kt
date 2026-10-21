package com.drugfreeindia.app.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drugfreeindia.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VilzoSplashAnimationScreen(
    onAnimationComplete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F171A) else Color(0xFFFDFCF7)
    val textPrimary = if (isDark) Color(0xFFE2EBE8) else Color(0xFF0A4D4E)
    val saffron = Color(0xFFFF7A00)
    val emerald = Color(0xFF168B4B)

    // Animation drivers
    val arcProgress = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val badgeScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val lineExpansion = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Draw dynamic circular arcs
        launch {
            arcProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }

        // Step 2: VZ Monogram & Figure scales up with spring bounce
        launch {
            logoAlpha.animateTo(1f, tween(300))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Step 3: Anti-drug badge stamps in
        delay(400)
        launch {
            badgeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }

        // Step 4: Reveal VILZO title and tagline
        delay(300)
        launch {
            textAlpha.animateTo(1f, tween(400))
        }
        launch {
            lineExpansion.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }

        // Hold briefly, then proceed
        delay(900)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allow instant tap to skip
                onAnimationComplete()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Animated Logo Container
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Canvas drawing the circular Saffron & Emerald dynamic arcs
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 5.dp.toPx()
                    val diameter = size.minDimension - strokeWidth * 2
                    val arcSize = Size(diameter, diameter)
                    val arcOffset = Offset(strokeWidth, strokeWidth)
                    val progress = arcProgress.value

                    // Top Saffron Arc (sweeping clockwise)
                    if (progress > 0f) {
                        drawArc(
                            color = saffron,
                            startAngle = 210f,
                            sweepAngle = 135f * progress,
                            useCenter = false,
                            topLeft = arcOffset,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Bottom Emerald Arc (sweeping counter-clockwise)
                    if (progress > 0.15f) {
                        val greenProgress = ((progress - 0.15f) / 0.85f).coerceIn(0f, 1f)
                        drawArc(
                            color = emerald,
                            startAngle = 30f,
                            sweepAngle = 135f * greenProgress,
                            useCenter = false,
                            topLeft = arcOffset,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // VILZO Logo Mark Image (Centered and Spring-Scaled)
                Image(
                    painter = painterResource(id = R.drawable.vilzo_logo_mark),
                    contentDescription = "VILZO Logo",
                    modifier = Modifier
                        .size(190.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(Modifier.height(28.dp))

            // Brand Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(textAlpha.value)
            ) {
                Text(
                    text = "VILZO",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    letterSpacing = 6.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // Tagline with expanding saffron & emerald accent lines
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width((32 * lineExpansion.value).dp)
                            .height(2.dp)
                            .background(saffron)
                    )

                    Text(
                        text = "VIBRANT INDIA  ZERO OVERDOSE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFB0C4DE) else Color(0xFF2C3E50),
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .width((32 * lineExpansion.value).dp)
                            .height(2.dp)
                            .background(emerald)
                    )
                }
            }
        }
    }
}
