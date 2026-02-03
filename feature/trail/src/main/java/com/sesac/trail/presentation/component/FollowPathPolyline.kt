package com.sesac.trail.presentation.component

import android.R
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.naver.maps.geometry.LatLngBounds
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
    var polyline by remember { mutableStateOf<PolylineOverlay?>(null) }
    var startMarker by remember { mutableStateOf<Marker?>(null) }
    var endMarker by remember { mutableStateOf<Marker?>(null) }

    LaunchedEffect(naverMap, isFollowingPath, path) {
        if (naverMap == null) return@LaunchedEffect

        // 기존 오버레이 제거
        polyline?.map = null
        startMarker?.map = null
        endMarker?.map = null

        if (isFollowingPath && path != null) {
            val coords = path.coord
            if (coords != null && coords.size >= 2) {
                val latLngCoords = coords.map { it.toLatLng() }

                // ✅ 폴리라인 생성
                val newPolyline = PolylineOverlay().apply {
                    this.coords = latLngCoords
                    color = 0xFF6200EE.toInt() // Purple
                    width = 12
                    capType = PolylineOverlay.LineCap.Round
                    joinType = PolylineOverlay.LineJoin.Round
                    map = naverMap
                }
                polyline = newPolyline

                // ✅ 출발 마커
                val newStartMarker = Marker().apply {
                    position = latLngCoords.first()
                    icon = OverlayImage.fromResource(R.drawable.ic_input_add)
                    captionText = "출발"
                    captionColor = Color.Green.toArgb()
                    map = naverMap
                }
                startMarker = newStartMarker

                // ✅ 도착 마커
                val newEndMarker = Marker().apply {
                    position = latLngCoords.last()
                    icon = OverlayImage.fromResource(R.drawable.ic_menu_close_clear_cancel)
                    captionText = "도착"
                    captionColor = Color.Red.toArgb()
                    map = naverMap
                }
                endMarker = newEndMarker

                // ✅ 카메라를 경로 전체가 보이도록 이동
                try {
                    val bounds = LatLngBounds.Builder().apply {
                        latLngCoords.forEach { coord ->
                            include(coord)
                        }
                    }.build()

                    val cameraUpdate = CameraUpdate.fitBounds(bounds, 100)
                    naverMap.moveCamera(cameraUpdate)

                    Log.d("FollowPathPolyline", "✅ 카메라 이동 완료: ${coords.size}개 좌표")
                } catch (e: Exception) {
                    Log.e("FollowPathPolyline", "❌ 카메라 이동 실패: ${e.message}")
                    // Fallback: 출발점으로 이동
                    val cameraUpdate = CameraUpdate.scrollTo(latLngCoords.first())
                    naverMap.moveCamera(cameraUpdate)
                }
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            polyline?.map = null
            startMarker?.map = null
            endMarker?.map = null
        }
    }
}