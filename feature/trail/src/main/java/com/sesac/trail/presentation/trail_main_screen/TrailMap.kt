package com.sesac.trail.presentation.trail_main_screen

import android.os.Trace
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.sesac.common.component.CommonMapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.domain.model.Coord
import com.sesac.trail.presentation.TrailCreateViewModel
import com.sesac.trail.presentation.TrailMainViewModel

private const val SCREEN_TAG = "TrailMap"
@Composable
fun TrailMap(
    modifier: Modifier = Modifier,
    locationSource: FusedLocationSource,
    isRecording: Boolean,
    onMapReady: (NaverMap) -> Unit,
    viewModel: TrailMainViewModel,
    createViewModel: TrailCreateViewModel,
    commonMapLifecycle: CommonMapLifecycle,
    selectedCoordSetter: (coord: LatLng?) -> Unit,
    showMemoDialogSetter: (Boolean) -> Unit,
    memoTextSetter: (String) -> Unit,
    onLocationChanged: (Coord) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                Log.d(SCREEN_TAG, "🏗️ AndroidView factory called")
                Trace.beginSection("TrailMap.AndroidView.factory")

                // 1. 싱글턴 MapView 가져오기
                val mapView = CommonMapView.getMapView(context, SCREEN_TAG)

                // 2. MapView와 Lifecycle 연결
                commonMapLifecycle.onStart(mapView, lifecycle)

                mapView.apply {
                    getMapAsync { naverMap ->
                        try {
                            Log.d(SCREEN_TAG, "✅ NaverMap 준비 완료")
                            naverMap.locationSource = locationSource
                            naverMap.locationTrackingMode = LocationTrackingMode.Follow
                            naverMap.uiSettings.isLocationButtonEnabled = true
                            naverMap.setOnMapLongClickListener { _, coord ->
                                if (isRecording) {
                                    selectedCoordSetter(coord)
                                    showMemoDialogSetter(true)
                                    memoTextSetter("")
                                    Log.d(SCREEN_TAG, "📍 메모 추가 위치: $coord")
                                }
                            }
                            naverMap.addOnLocationChangeListener { location ->
                                onLocationChanged(Coord(location.latitude, location.longitude))
                            }
                            onMapReady(naverMap)
                            Trace.endSection()
                        } catch (e: Exception) {
                            Log.e(SCREEN_TAG, "❌ NaverMap 초기화 실패: ${e.message}", e)
                            Trace.endSection()
                        }
                    }
                }
            },
            update = { mapView ->
                Log.d(SCREEN_TAG, "🔄 AndroidView update called - requestLayout")
                Trace.beginSection("TrailMap.AndroidView.update")
                mapView.requestLayout()
                Trace.endSection()
            }
        )
    }

    /* -----------------------------------
     * 화면 이탈 시 정리
     * ----------------------------------- */
    DisposableEffect(Unit) {
        onDispose {
            Log.d(SCREEN_TAG, "🧹 TrailMap onDispose executing")
            Trace.beginSection("TrailMap.onDispose")
            try {
                // 1. Lifecycle Observer 정리
                commonMapLifecycle.onDispose()
                // 2. MapView 부모 View에서 분리
                CommonMapView.detachMapView(SCREEN_TAG)
                Log.d(SCREEN_TAG, "🧹 TrailMap disposed complete")
                Trace.endSection()
            } catch (e: Exception) {
                Log.e(SCREEN_TAG, "❌ Dispose 실패: ${e.message}", e)
                Trace.endSection()
            }
        }
    }
}