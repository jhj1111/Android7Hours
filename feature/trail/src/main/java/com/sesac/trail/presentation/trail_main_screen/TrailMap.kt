package com.sesac.trail.presentation.trail_main_screen

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.component.CommonMapView
import com.sesac.trail.presentation.TrailViewModel

@Composable
fun TrailMap(
    modifier: Modifier = Modifier,
    commonMapLifecycle: CommonMapLifecycle,
    locationSource: FusedLocationSource,
    isRecording: Boolean,
    onMapReady: (NaverMap) -> Unit,
    viewModel: TrailViewModel,
    selectedCoordSetter: (coord: LatLng?) -> Unit,
    showMemoDialogSetter: (Boolean) -> Unit,
    memoTextSetter: (String) -> Unit
) {
    val isRecordingState = rememberUpdatedState(isRecording)

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val mapView = commonMapLifecycle.mapView ?: CommonMapView.getMapView(context).also {
                commonMapLifecycle.setMapView(it)
            }
            (mapView.parent as? ViewGroup)?.removeView(mapView)
            mapView.onStart()
            mapView.onResume()
            mapView.getMapAsync { naverMap ->
                onMapReady(naverMap)
                naverMap.locationSource = locationSource
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                naverMap.uiSettings.isLocationButtonEnabled = true
                naverMap.uiSettings.isZoomControlEnabled = false

                val newPolyline = PolylineOverlay().apply {
                    color = 0xFF0000FF.toInt()
                    width = 10
                    capType = PolylineOverlay.LineCap.Round
                    joinType = PolylineOverlay.LineJoin.Round
                }
                viewModel.setPolylineInstance(newPolyline)

                naverMap.setOnMapLongClickListener { _, coord ->
                    if (isRecordingState.value) {
                        selectedCoordSetter(coord)
                        memoTextSetter("")
                        showMemoDialogSetter(true)
                    }
                }
            }
            mapView
        },
        update = { it.requestLayout() }
    )
}