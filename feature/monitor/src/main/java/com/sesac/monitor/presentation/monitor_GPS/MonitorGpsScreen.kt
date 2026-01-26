package com.sesac.monitor.presentation.monitor_GPS

import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.component.CommonMapView
import com.sesac.common.utils.EffectPauseStop
import com.sesac.monitor.presentation.MonitorViewModel
import com.naver.maps.geometry.LatLng
import com.sesac.common.ui_state.ResponseUiState // NEW IMPORT
import kotlinx.coroutines.delay

private const val SCREEN_TAG = "MonitorGpsScreen"

@Composable
fun MonitorGpsScreen (
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = hiltViewModel(),
    petId: Int,
) {
    Log.d(SCREEN_TAG, "🔴 MonitorGpsScreen composing...")

    val monitoredPetState by viewModel.monitoredPet.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    Log.d(SCREEN_TAG, "📋 lifecycle owner: ${lifecycleOwner.javaClass.simpleName}")

    var currentNaverMap by remember { mutableStateOf<NaverMap?>(null) }
    var petMarker by remember { mutableStateOf<Marker?>(null) }
    var isMapReady by remember { mutableStateOf(false) }

    LaunchedEffect(petId, isMapReady) {
        if (isMapReady) {
            delay(300)
            Log.d(SCREEN_TAG, "🚀 Map ready → start monitoring")
            viewModel.startMonitoringPetLocation(petId)
        }
    }

    LaunchedEffect(monitoredPetState) {
        val naverMap = currentNaverMap ?: return@LaunchedEffect

        when (val state = monitoredPetState) {
            is ResponseUiState.Success -> {
                state.result.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    petMarker?.map = null

                    petMarker = Marker().apply {
                        position = latLng
                        captionText = state.result.name
                        map = naverMap
                    }

                    Log.d(SCREEN_TAG, "📍 camera move → $latLng")
                    delay(100)
                    naverMap.moveCamera(CameraUpdate.scrollTo(latLng))
                }
            }

            is ResponseUiState.Error -> {
                Log.e(SCREEN_TAG, "❌ Monitoring error: ${state.message}")
            }

            is ResponseUiState.Idle,
            is ResponseUiState.Loading -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        key(SCREEN_TAG) {
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { context ->
                    Log.d(SCREEN_TAG, "🏗️ AndroidView factory START")

                    CommonMapView.getMapView(context, SCREEN_TAG).apply {
                        getMapAsync { naverMap ->
                            Log.d(SCREEN_TAG, "✅ Map ready")
                            currentNaverMap = naverMap

                            naverMap.uiSettings.apply {
                                isLocationButtonEnabled = true
                                isZoomControlEnabled = false
                            }

                            isMapReady = true
                        }
                    }
                },
                update = {
                    Log.d(SCREEN_TAG, "🔄 AndroidView update - requestLayout")
                    // 🔥 화면 크기 변경 시 레이아웃 강제 갱신
                    it.requestLayout()
                }
            )
        }
    }

    DisposableEffect(Unit) {
        Log.d(SCREEN_TAG, "🎬 DisposableEffect registered")
        onDispose {
            Log.d(SCREEN_TAG, "🧹 DisposableEffect executing onDispose")
            petMarker?.map = null
            petMarker = null
            isMapReady = false
            currentNaverMap = null

            CommonMapView.detachMapView(SCREEN_TAG)

            Log.d(SCREEN_TAG, "🧹 MonitorGpsScreen disposed complete")
        }
    }
}