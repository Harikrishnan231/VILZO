package com.drugfreeindia.app.presentation.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.theme.DfiTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(300)
        onTimeout()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale.value)
            ) {
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(DfiTheme.colors.surfaceTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(76.dp)) {
                        drawCircle(
                            color = Color(0xFF0E9F8E).copy(alpha = 0.15f),
                            radius = size.minDimension / 2f
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Spa,
                        contentDescription = "Drug-Free India",
                        tint = DfiTheme.colors.accent,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    style = DfiTheme.typography.display.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DfiTheme.colors.ink
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.app_tagline),
                    style = DfiTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = DfiTheme.colors.accentDark
                    )
                )
            }
        }
    }
}
