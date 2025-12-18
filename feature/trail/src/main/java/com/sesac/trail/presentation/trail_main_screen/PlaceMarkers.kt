package com.sesac.trail.presentation.trail_main_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavController
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.sesac.common.model.toParceler
import com.sesac.common.ui.theme.ColorBlue
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Place
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.utils.toLatLng

@Composable
fun PlaceMarkers(
    naverMap: NaverMap?,
    placesState: ResponseUiState<List<Place>>,
    isRecording: Boolean,
    isFollowingPath: Boolean,
    navController: NavController,
) {
    val placeMarkers = remember { mutableListOf<Marker>() }
    // Place 마커 표시
    LaunchedEffect(placesState, naverMap, isRecording, isFollowingPath) {
        val map = naverMap ?: return@LaunchedEffect

        // 기록 중이거나 경로 따라가는 중이면 마커 제거
        if (isRecording || isFollowingPath) {
            placeMarkers.forEach { it.map = null }
            placeMarkers.clear()
            return@LaunchedEffect
        }

        // 기존 마커 정리
        placeMarkers.forEach { it.map = null }
        placeMarkers.clear()

        if (placesState is ResponseUiState.Success) {
            placesState.result.forEach { place ->
                val marker = Marker().apply {
                    position = place.toLatLng()
                    icon = Marker.DEFAULT_ICON
                    captionText = place.title
                    captionColor = ColorBlue.toArgb()
                    setOnClickListener {
                        navController.navigate(
                            NestedNavigationRoute.PlaceDetail(place.toParceler())
                        )
                        true
                    }
                    this.map = map
                }
                placeMarkers.add(marker)
            }
        }
    }

    // Place 마커 정리
    DisposableEffect(Unit) {
        onDispose {
            placeMarkers.forEach { it.map = null }
            placeMarkers.clear()
        }
    }
}

private fun MutableList<Marker>.clearMarkers() {
    forEach { it.map = null }
    clear()
}