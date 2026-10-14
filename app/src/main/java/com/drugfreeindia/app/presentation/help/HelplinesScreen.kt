package com.drugfreeindia.app.presentation.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.PhoneInTalk
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.Helpline
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.PrivacyNote
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun HelplinesScreen(
    onBack: () -> Unit,
    viewModel: HelpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 12.dp),
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
                        text = "National Helplines",
                        style = DfiTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "Confidential · 24/7 Toll-Free",
                        style = DfiTheme.typography.label,
                        color = DfiTheme.colors.accentDark
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.helplines) { helpline ->
                    HelplineCard(
                        helpline = helpline,
                        onCall = { dial(context, helpline.number) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    PrivacyNote(
                        text = "All calls to 1933, 14446, and Tele-MANAS (14416) are free of charge from any Indian mobile network or landline."
                    )
                }
            }
        }
    }
}

@Composable
fun HelplineCard(
    helpline: Helpline,
    onCall: () -> Unit
) {
    DfiCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 18.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = helpline.name,
                        style = DfiTheme.typography.titleMedium,
                        color = DfiTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = helpline.category,
                        style = DfiTheme.typography.label.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(DfiTheme.colors.surfaceTint, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = helpline.hours,
                        style = DfiTheme.typography.label.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = helpline.description,
                style = DfiTheme.typography.bodyMedium,
                color = DfiTheme.colors.inkSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = helpline.number,
                        style = DfiTheme.typography.stat.copy(color = DfiTheme.colors.ink)
                    )
                    Text(
                        text = "Verified: ${helpline.lastVerified}",
                        style = DfiTheme.typography.label.copy(color = DfiTheme.colors.inkTertiary)
                    )
                }

                DfiButton(
                    text = "Dial ${helpline.number}",
                    onClick = onCall,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Call,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

private fun dial(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$number")
    }
    context.startActivity(intent)
}
