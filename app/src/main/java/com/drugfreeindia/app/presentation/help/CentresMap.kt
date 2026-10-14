package com.drugfreeindia.app.presentation.help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import com.drugfreeindia.app.BuildConfig
import com.drugfreeindia.app.domain.model.SupportCentre
import com.drugfreeindia.app.presentation.onboarding.cityCentres
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker as OsmMarker

/**
 * A real map. Google Maps Compose when MAPS_API_KEY is configured, otherwise OpenStreetMap via osmdroid.
 * Only centres with stored coordinates get pins — we never place pins at guessed locations.
 */
@Composable
fun CentresMap(centres: List<SupportCentre>, citySlug: String?, onSelect: (SupportCentre) -> Unit) {
    val pinned = centres.filter { it.lat != null && it.lng != null }
    val centre = pinned.firstOrNull()?.let { it.lat!! to it.lng!! } ?: citySlug?.let { cityCentres[it] } ?: (20.5937 to 78.9629)
    val zoom = if (pinned.isNotEmpty() || citySlug != null) 11.0 else 4.5
    if (BuildConfig.MAPS_API_KEY.isNotBlank()) {
        val camera = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(LatLng(centre.first, centre.second), zoom.toFloat()) }
        GoogleMap(Modifier.fillMaxSize(), cameraPositionState = camera, uiSettings = MapUiSettings(mapToolbarEnabled = false)) {
            pinned.forEach { c ->
                Marker(state = remember(c.id) { MarkerState(LatLng(c.lat!!, c.lng!!)) }, title = c.name, snippet = c.cityName,
                    onClick = { onSelect(c); false })
            }
        }
    } else {
        val context = LocalContext.current
        val map = remember {
            Configuration.getInstance().userAgentValue = context.packageName
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoom)
                controller.setCenter(GeoPoint(centre.first, centre.second))
            }
        }
        DisposableEffect(map) {
            map.onResume()
            onDispose { map.onPause(); map.onDetach() }
        }
        AndroidView({ map }, Modifier.fillMaxSize(), update = { mv ->
            mv.overlays.clear()
            pinned.forEach { c ->
                mv.overlays.add(OsmMarker(mv).apply {
                    position = GeoPoint(c.lat!!, c.lng!!)
                    title = c.name
                    setAnchor(OsmMarker.ANCHOR_CENTER, OsmMarker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ -> onSelect(c); true }
                })
            }
            mv.invalidate()
        })
    }
}
