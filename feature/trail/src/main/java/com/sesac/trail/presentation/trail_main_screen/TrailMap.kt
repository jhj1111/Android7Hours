package com.sesac.trail.presentation.trail_main_screen

import android.util.Log
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
    selectedCoordSetter: (coord: LatLng?) -> Unit,
    showMemoDialogSetter: (Boolean) -> Unit,
    memoTextSetter: (String) -> Unit,
    onLocationChanged: (Coord) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    AndroidView(
        modifier = modifier,
        factory = { context ->
            Log.d(SCREEN_TAG, "🏗️ AndroidView factory called")

            // ✅ 공통 MapView 가져오기
            CommonMapView.getMapView(context, SCREEN_TAG).apply {
                getMapAsync { naverMap ->
                    Log.d(SCREEN_TAG, "✅ NaverMap 준비 완료")

                    // ✅ 지도 기본 설정
                    naverMap.locationSource = locationSource
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    naverMap.uiSettings.isLocationButtonEnabled = true

                    // ✅ 롱클릭 리스너 (메모 추가)
                    naverMap.setOnMapLongClickListener { _, coord ->
                        if (isRecording) {
                            selectedCoordSetter(coord)
                            showMemoDialogSetter(true)
                            memoTextSetter("")
                            Log.d(SCREEN_TAG, "📍 메모 추가 위치: $coord")
                        }
                    }

                    // ✅ 위치 변경 리스너
                    naverMap.addOnLocationChangeListener { location ->
                        onLocationChanged(Coord(location.latitude, location.longitude))
                    }

                    // ✅ 지도 준비 완료 콜백
                    onMapReady(naverMap)
                }
            }
        },
        update = { mapView ->
            Log.d(SCREEN_TAG, "🔄 AndroidView update called - requestLayout")
            // 🔥 화면 크기 변경 시 레이아웃 강제 갱신
            mapView.requestLayout()
        }
    )

    /* -----------------------------------
     * 화면 이탈 시 정리
     * ----------------------------------- */
    DisposableEffect(Unit) {
        Log.d(SCREEN_TAG, "🎬 DisposableEffect registered")
        onDispose {
            Log.d(SCREEN_TAG, "🧹 DisposableEffect executing onDispose")

            // 🔥 MapView detach
            CommonMapView.detachMapView(SCREEN_TAG)

            Log.d(SCREEN_TAG, "🧹 TrailMap disposed complete")
        }
    }
}