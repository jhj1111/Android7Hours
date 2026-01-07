package com.sesac.trail.presentation.trail_main_screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.domain.model.Coord
import com.sesac.trail.presentation.TrailCreateViewModel
import com.sesac.trail.presentation.TrailMainViewModel


@Composable
fun TrailMap(
    modifier: Modifier = Modifier,
    commonMapLifecycle: CommonMapLifecycle,
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).also { mapView ->
                commonMapLifecycle.setMapView(mapView)
                mapView.onCreate(null)
                Log.d("TrailMap", "✅ MapView 생성")
            }
        },
        update = { mapView ->
            mapView.getMapAsync { naverMap ->
                Log.d("TrailMap", "✅ NaverMap 준비 완료")

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
                        Log.d("TrailMap", "📍 메모 추가 위치: $coord")
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
    )
}