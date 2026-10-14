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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.TrustedContact
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.EmptyState
import com.drugfreeindia.app.presentation.components.PrivacyNote
import com.drugfreeindia.app.presentation.theme.DfiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(
    onBack: () -> Unit,
    viewModel: HelpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                        text = "Trusted Contacts",
                        style = DfiTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "Encrypted on your device only",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.trustedContacts.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No trusted contacts added yet",
                            description = "Add up to 5 close friends or family members who you can call or text when facing pressure.",
                            icon = Icons.Rounded.Security,
                            actionButton = {
                                DfiButton(
                                    text = "Add First Contact",
                                    onClick = { showAddDialog = true }
                                )
                            }
                        )
                    }
                } else {
                    items(state.trustedContacts) { contact ->
                        ContactCard(
                            contact = contact,
                            onCall = { dialPhone(context, contact.phone) },
                            onWhatsApp = { sendGentleWhatsApp(context, contact.phone) },
                            onDelete = { viewModel.deleteContact(contact.id) }
                        )
                    }

                    if (state.trustedContacts.size < 5) {
                        item {
                            DfiButton(
                                text = "Add Another Contact (${state.trustedContacts.size}/5)",
                                onClick = { showAddDialog = true },
                                variant = DfiButtonVariant.Secondary,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = DfiTheme.colors.accentDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    PrivacyNote(
                        text = "Contacts are stored using AES-256 hardware encryption. They are never uploaded or synced to external servers."
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, relation ->
                viewModel.addContact(name, phone, relation)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ContactCard(
    contact: TrustedContact,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onDelete: () -> Unit
) {
    DfiCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(DfiTheme.colors.surfaceTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = DfiTheme.colors.accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = contact.name,
                            style = DfiTheme.typography.titleSmall,
                            color = DfiTheme.colors.ink
                        )
                        Text(
                            text = "${contact.relationship} · ${contact.phone}",
                            style = DfiTheme.typography.bodyMedium,
                            color = DfiTheme.colors.inkSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = DfiTheme.colors.inkTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DfiButton(
                    text = "Call",
                    onClick = onCall,
                    variant = DfiButtonVariant.Primary,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Call,
                            contentDescription = null,
                            tint = DfiTheme.colors.surface,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                DfiButton(
                    text = "WhatsApp",
                    onClick = onWhatsApp,
                    variant = DfiButtonVariant.Secondary,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Chat,
                            contentDescription = null,
                            tint = DfiTheme.colors.accentDark,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Friend") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            DfiButton(
                text = "Save Contact",
                enabled = name.isNotBlank() && phone.isNotBlank(),
                onClick = { onAdd(name.trim(), phone.trim(), relation.trim()) }
            )
        },
        dismissButton = {
            DfiButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = DfiButtonVariant.Ghost
            )
        },
        title = {
            Text(
                text = "Add Trusted Person",
                style = DfiTheme.typography.titleMedium,
                color = DfiTheme.colors.ink
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name or Nickname") },
                    shape = DfiTheme.shapes.input,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    shape = DfiTheme.shapes.input,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text("Relationship (e.g. Friend, Sister, Mentor)") },
                    shape = DfiTheme.shapes.input,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = DfiTheme.shapes.card,
        containerColor = DfiTheme.colors.surface
    )
}

private fun dialPhone(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    context.startActivity(intent)
}

private fun sendGentleWhatsApp(context: Context, phone: String) {
    val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
    val gentleText = "Hey, can we talk? I need some support."
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(gentleText)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard SMS
        val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$cleanPhone")).apply {
            putExtra("sms_body", gentleText)
        }
        context.startActivity(smsIntent)
    }
}
