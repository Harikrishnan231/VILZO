package com.drugfreeindia.app.presentation.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.R
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun EmergencyGuideScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DfiTheme.colors.surfaceMuted)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = DfiTheme.colors.ink,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Medical Emergency Protocol",
                        style = DfiTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "Life-Threatening Crisis Response",
                        style = DfiTheme.typography.label,
                        color = DfiTheme.colors.danger
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Emergency Hero Card with Call 112
                item {
                    DfiCard(
                        backgroundColor = Color(0xFFFFF4F2),
                        borderColor = Color(0xFFFFD9D6),
                        padding = 20.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Rounded.MedicalServices,
                                contentDescription = null,
                                tint = DfiTheme.colors.danger,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Is Someone Unconscious or in Distress?",
                                style = DfiTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Emergency dispatchers can send an ambulance with trained paramedics immediately.",
                                style = DfiTheme.typography.bodyMedium,
                                color = DfiTheme.colors.inkSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DfiButton(
                                text = "Call 112 (National Emergency)",
                                onClick = { dial112(context) },
                                variant = DfiButtonVariant.Danger,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Call,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Step-by-Step Actions
                item {
                    Text(
                        text = "Immediate First-Response Steps",
                        style = DfiTheme.typography.titleSmall,
                        color = DfiTheme.colors.ink
                    )
                }

                item {
                    EmergencyStepCard(
                        step = "1",
                        title = "Check Responsiveness & Breathing",
                        detail = "Gently shake their shoulders and call their name loudly. Check if their chest is rising normally. If breathing is shallow or absent, dial 112 right away."
                    )
                }

                item {
                    EmergencyStepCard(
                        step = "2",
                        title = "Turn into Recovery Position",
                        detail = "If they are breathing but semi-conscious or vomiting, turn them gently onto their side with their head tilted slightly backward. This keeps the airway clear and prevents choking."
                    )
                }

                item {
                    EmergencyStepCard(
                        step = "3",
                        title = "Never Leave Them Alone",
                        detail = "Stay right next to the person until medical professionals arrive. Keep them warm with a blanket or jacket. Do not give them food, milk, or induce vomiting."
                    )
                }

                item {
                    EmergencyStepCard(
                        step = "4",
                        title = "What to Tell the Operator",
                        detail = "State your exact location, landmarks, whether the person is breathing, and if known, what substance was ingested. Doctors need facts to administer antidotes like Naloxone."
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DfiTheme.colors.surfaceMuted,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DfiTheme.colors.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = DfiTheme.colors.inkSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.medical_disclaimer),
                                style = DfiTheme.typography.label.copy(color = DfiTheme.colors.inkSecondary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyStepCard(
    step: String,
    title: String,
    detail: String
) {
    DfiCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(DfiTheme.colors.surfaceMuted, CircleShape)
                    .border(1.dp, DfiTheme.colors.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step,
                    style = DfiTheme.typography.label.copy(
                        fontWeight = FontWeight.Bold,
                        color = DfiTheme.colors.ink
                    )
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = DfiTheme.typography.titleSmall,
                    color = DfiTheme.colors.ink
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = DfiTheme.typography.bodyMedium,
                    color = DfiTheme.colors.inkSecondary
                )
            }
        }
    }
}

private fun dial112(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:112")
    }
    context.startActivity(intent)
}
