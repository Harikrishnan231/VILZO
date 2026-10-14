package com.drugfreeindia.app.presentation.help

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.PrivacyNote
import com.drugfreeindia.app.presentation.theme.DfiTheme

@Composable
fun HelpFriendGuideScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val guideSections = remember { loadGuideSections(context) }

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
                        text = "How to Support a Friend",
                        style = DfiTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "Compassionate, non-judgmental guidance",
                        style = DfiTheme.typography.label,
                        color = DfiTheme.colors.accentDark
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                items(guideSections.size) { index ->
                    val (title, body) = guideSections[index]
                    DfiCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        padding = 16.dp
                    ) {
                        Column {
                            Text(
                                text = title,
                                style = DfiTheme.typography.titleSmall,
                                color = DfiTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = body,
                                style = DfiTheme.typography.bodyMedium,
                                color = DfiTheme.colors.inkSecondary
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    PrivacyNote(
                        text = "Remember: You are a loyal companion, not their therapist. Guiding them towards professional helplines ensures their long-term health."
                    )
                }
            }
        }
    }
}

private fun loadGuideSections(context: Context): List<Pair<String, String>> {
    return listOf(
        "1. Listen Actively Without Interruption" to "Create a calm, private space. Speak when neither of you is in a rush, angry, or exhausted. Let them speak freely about what is going on without judging or lecturing them. Reflect back what you hear: 'It sounds like you have been carrying an enormous amount of stress lately.'",
        "2. Never Judge, Shame, or Threaten" to "Shaming makes people withdraw and hide risky behaviours. Separate the person from the action: they are still your friend, even if they made an unwise choice under social pressure. Avoid ultimatum phrases like 'If you do this again, I won't talk to you.' Instead say: 'I care about your safety too much to watch you hurt yourself.'",
        "3. Help Them Build an Exit Plan" to "Agree on a safe code word or silent text that means 'Call me right now with an excuse to pull me away.' Offer to accompany them to neutral places: coffee shops, libraries, sports grounds, or art circles where there is zero temptation.",
        "4. Connect Them with Confidential Help" to "You do not have to carry this alone. Gently remind them of free, confidential resources like MANAS National Toll-Free Anti-Drug Line (1933), Tele-MANAS (14416), and MSJE De-addiction (14446). Offer to sit beside them while they call if they feel anxious.",
        "5. In an Acute Crisis or Medical Emergency" to "If your friend is unresponsive, breathing irregularly, vomiting while semi-conscious, or experiencing seizures, dial 112 immediately. Turn them onto their side into the Recovery Position to keep their airway open. Never leave them alone."
    )
}
