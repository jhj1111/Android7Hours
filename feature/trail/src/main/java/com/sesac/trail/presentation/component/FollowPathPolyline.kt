package com.sesac.trail.presentation.component

import android.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import com.sesac.domain.model.Path
import com.sesac.trail.utils.toLatLng

@Composable
fun FollowPathPolyline(
    naverMap: NaverMap?,
    isFollowingPath: Boolean,
    path: Path?
    ) {
        DisposableEffect(naverMap, isFollowingPath, path) {
        val map = naverMap
        var followPolyline: PolylineOverlay? = null
        var startMarker: Marker? = null
        var endMarker: Marker? = null

        if (map != null && isFollowingPath && path != null) {
            val coords = path.coord?.map { it.toLatLng() } ?: emptyList()
            if (coords.size >= 2) {
                followPolyline = PolylineOverlay().apply {
                    this.coords = coords
                    color = 0xFF6200EE.toInt()
                    width = 12
                    capType = PolylineOverlay.LineCap.Round
                    joinType = PolylineOverlay.LineJoin.Round
                    this.map = map
                }
                startMarker = Marker().apply {
                    position = coords.first()
                    icon = OverlayImage.fromResource(R.drawable.ic_input_add)
                    captionText = "출발"
                    captionColor = Color.Green.toArgb()
                    this.map = map
                }
                endMarker = Marker().apply {
                    position = coords.last()
                    icon = OverlayImage.fromResource(R.drawable.ic_menu_close_clear_cancel)
                    captionText = "도착"
                    captionColor = Color.Red.toArgb()
                    this.map = map
                }
                val cameraUpdate = CameraUpdate.scrollTo(coords.first())
                map.moveCamera(cameraUpdate)
            }
        }
        onDispose {
            followPolyline?.map = null
            startMarker?.map = null
            endMarker?.map = null
        }
    }
}