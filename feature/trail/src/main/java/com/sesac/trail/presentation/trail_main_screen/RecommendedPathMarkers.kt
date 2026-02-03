package com.sesac.trail.presentation.trail_main_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.sesac.common.ui.theme.ColorPink
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.model.Path
import com.sesac.trail.utils.toLatLng

@Composable
fun RecommendedPathMarkers(
    naverMap: NaverMap?,
    pathsState: ResponseUiState<List<Path>>,
    isVisible: Boolean,
    onPathClick: (Path) -> Unit
) {
    val markers = remember { mutableListOf<Marker>() }

    LaunchedEffect(pathsState, naverMap, isVisible) {
        val map = naverMap ?: return@LaunchedEffect

        // 항상 초기화
        markers.forEach { it.map = null }
        markers.clear()

        if (!isVisible) return@LaunchedEffect
        if (pathsState !is ResponseUiState.Success) return@LaunchedEffect

        pathsState.result.forEach { path ->
            path.coord?.firstOrNull()?.let { startCoord ->
                val marker = Marker().apply {
                    position = startCoord.toLatLng()
                    iconTintColor = 0xFF6200EE.toInt()
                    captionText = path.pathName
                    captionColor = ColorPink.toArgb()
                    setOnClickListener {
                        onPathClick(path)
                        true
                    }
                    this.map = map
                }
                markers.add(marker)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            markers.forEach { it.map = null }
            markers.clear()
        }
    }
}

