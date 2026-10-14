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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drugfreeindia.app.domain.model.Centre
import com.drugfreeindia.app.presentation.components.DfiButton
import com.drugfreeindia.app.presentation.components.DfiButtonVariant
import com.drugfreeindia.app.presentation.components.DfiCard
import com.drugfreeindia.app.presentation.components.DfiChipRow
import com.drugfreeindia.app.presentation.components.IconTile
import com.drugfreeindia.app.presentation.components.LocalBottomBarPadding
import com.drugfreeindia.app.presentation.theme.DfiTheme
import com.drugfreeindia.app.presentation.theme.softShadow
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentresMapScreen(
    onBack: () -> Unit,
    viewModel: HelpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedCentre by remember { mutableStateOf<Centre?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomPadding = LocalBottomBarPadding.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DfiTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Centres near you",
                        style = DfiTheme.typography.screenTitle.copy(fontSize = 22.sp),
                        color = DfiTheme.colors.ink
                    )
                    Text(
                        text = "21 sample listings",
                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkSecondary)
                    )
                }
            }

            // Search Field (52dp, SurfaceMuted, radius 16dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = {
                        Text(
                            text = "Search city, pincode, or hospital…",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.inkTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = DfiTheme.colors.inkTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear search",
                                    tint = DfiTheme.colors.inkSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DfiTheme.colors.surfaceMuted,
                        unfocusedContainerColor = DfiTheme.colors.surfaceMuted,
                        focusedBorderColor = DfiTheme.colors.accent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }

            // Filter Chips in scrolling DfiChipRow
            val filters = listOf("All", "Government", "Counselling", "De-addiction")
            DfiChipRow(
                items = filters,
                selectedItem = state.selectedFilter,
                onItemSelected = { viewModel.onFilterSelected(it) },
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Real Map View (Height 220dp, radius 24dp, custom accent pins)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = DfiTheme.spacing.screenHorizontal, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                val indiaCenter = LatLng(20.5937, 78.9629)
                val cameraState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(indiaCenter, 4.2f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = false
                    )
                ) {
                    state.centres.forEach { centre ->
                        Marker(
                            state = MarkerState(position = LatLng(centre.latitude, centre.longitude)),
                            title = centre.name,
                            snippet = "${centre.type} · ${centre.city}",
                            onClick = {
                                selectedCentre = centre
                                true
                            }
                        )
                    }
                }

                // "Search this area" chip overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DfiTheme.colors.surface.copy(alpha = 0.94f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = DfiTheme.colors.accentDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Search this area",
                        style = DfiTheme.typography.labelM.copy(
                            color = DfiTheme.colors.accentDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // "My location" FAB (40dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DfiTheme.colors.surface)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = "My location",
                        tint = DfiTheme.colors.accentDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Centres List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = DfiTheme.spacing.screenHorizontal,
                    end = DfiTheme.spacing.screenHorizontal,
                    top = 8.dp,
                    bottom = bottomPadding + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.centres) { centre ->
                    DfiCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedCentre = centre },
                        padding = 16.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // IconTile by type
                            IconTile(
                                icon = Icons.Rounded.LocalHospital,
                                size = 44.dp,
                                iconSize = 22.dp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = centre.name,
                                        style = DfiTheme.typography.titleM,
                                        color = DfiTheme.colors.ink,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DfiTheme.colors.surfaceMuted)
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = centre.type,
                                            style = DfiTheme.typography.caption.copy(
                                                color = DfiTheme.colors.inkSecondary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // One line with location icon + city
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint = DfiTheme.colors.inkTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${centre.city}, ${centre.state}",
                                        style = DfiTheme.typography.bodyM.copy(color = DfiTheme.colors.inkSecondary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // One line with clock icon + hours
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Schedule,
                                        contentDescription = null,
                                        tint = DfiTheme.colors.inkTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = centre.hours,
                                        style = DfiTheme.typography.caption.copy(color = DfiTheme.colors.inkTertiary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Trailing 40dp round chevron button (NOT text, no wrapping)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DfiTheme.colors.surfaceMuted),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = "View details",
                                    tint = DfiTheme.colors.inkSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Centre Detail Bottom Sheet
        selectedCentre?.let { centre ->
            ModalBottomSheet(
                onDismissRequest = { selectedCentre = null },
                sheetState = sheetState,
                shape = DfiTheme.shapes.bottomSheet,
                containerColor = DfiTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = centre.name,
                            style = DfiTheme.typography.titleL,
                            color = DfiTheme.colors.ink,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DfiTheme.colors.accentSoft)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sample listing",
                                style = DfiTheme.typography.caption.copy(
                                    color = DfiTheme.colors.accentDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Three icon rows: Address, Hours, Phone
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Rounded.LocationOn, size = 36.dp, iconSize = 18.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${centre.address}, ${centre.city}, ${centre.state} - ${centre.pincode}",
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.ink
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Rounded.Schedule, size = 36.dp, iconSize = 18.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = centre.hours,
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.ink
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Rounded.Call, size = 36.dp, iconSize = 18.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = centre.phone,
                            style = DfiTheme.typography.bodyM,
                            color = DfiTheme.colors.ink
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons: Primary Call, Secondary Directions, Ghost Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DfiButton(
                            text = "Call",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${centre.phone}"))
                                context.startActivity(intent)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Call,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DfiButton(
                            text = "Directions",
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:${centre.latitude},${centre.longitude}?q=${Uri.encode(centre.name)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            },
                            variant = DfiButtonVariant.Secondary,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Directions,
                                    contentDescription = null,
                                    tint = DfiTheme.colors.ink,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DfiButton(
                        text = "Share details",
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${centre.name}\n${centre.address}\nPhone: ${centre.phone}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Centre Details"))
                        },
                        variant = DfiButtonVariant.Ghost,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null,
                                tint = DfiTheme.colors.accent,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
