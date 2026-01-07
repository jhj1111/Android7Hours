package com.sesac.trail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.sesac.common.model.UiEvent
import com.sesac.domain.model.Path
import com.sesac.domain.result.LocationFlowResult
import com.sesac.domain.usecase.location.LocationUseCase
import com.sesac.trail.utils.toLatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailFollowViewModel @Inject constructor(
    private val locationUseCase: LocationUseCase,
) : ViewModel() {

    private val _invalidToken = Channel<UiEvent>()
    val invalidToken = _invalidToken.receiveAsFlow()

    // =================================================================
    // 📌 1. 따라가기 상태 관리
    // =================================================================

    private val _selectedPath = MutableStateFlow<Path?>(null)
    val selectedPath get() = _selectedPath.asStateFlow()

    private val _memoMarkers = MutableStateFlow<List<com.sesac.domain.model.MemoMarker>>(emptyList())
    val memoMarkers = _memoMarkers.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing = _isFollowing.asStateFlow()

    private val _offRoute = MutableStateFlow(false)
    val offRoute = _offRoute.asStateFlow()

    private val _isRouteCompleted = MutableStateFlow(false)
    val isRouteCompleted = _isRouteCompleted.asStateFlow()

    private val _remainingDistance = MutableStateFlow(0f)
    val remainingDistance = _remainingDistance.asStateFlow()

    fun startFollowing(path: Path) {
        val coords = path.coord
        if (coords == null || coords.size < 2) {
            Log.e("TrailFollowViewModel", "❌ 따라가기 실패: 좌표가 부족합니다 (${coords?.size ?: 0}개)")
            viewModelScope.launch {
                _invalidToken.send(UiEvent.ToastEvent("경로 데이터가 올바르지 않습니다"))
            }
            return
        }

        Log.d("TrailFollowViewModel", "Starting to follow path: ${path.pathName}. Markers in path: ${path.markers?.size ?: 0}")
        Log.d("TrailFollowViewModel", "✅ 따라가기 시작: ${path.pathName}, 좌표 ${coords.size}개")

        _selectedPath.value = path
        _memoMarkers.value = path.markers ?: emptyList()
        _isFollowing.value = true
        _isRouteCompleted.value = false
        _offRoute.value = false

        // 전체 거리 계산
        var totalDist = 0.0
        for (i in 0 until coords.size - 1) {
            totalDist += coords[i].toLatLng().distanceTo(coords[i + 1].toLatLng())
        }
        _remainingDistance.value = totalDist.toFloat()

        // 위치 업데이트 시작
        startLocationUpdatesForFollowing()
    }

    fun stopFollowing() {
        _isFollowing.value = false
        _isRouteCompleted.value = false
    }

    // =================================================================
    // 📌 2. 위치 업데이트 & 경로 추적
    // =================================================================

    private fun startLocationUpdatesForFollowing() {
        viewModelScope.launch {
            locationUseCase.getRealtimeLocationUseCase().collect { result ->
                when (result) {
                    is LocationFlowResult.Success -> {
                        val newLocation = result.coord
                        val newPoint = newLocation.toLatLng()

                        if (_isFollowing.value) {
                            updateUserLocation(newPoint)
                            updateUserLocationMarker(newPoint)
                        }
                    }
                    is LocationFlowResult.Error -> {
                        Log.e("TrailFollowViewModel", "Location error: ${result.exception.message}")
                    }
                }
            }
        }
    }

    fun updateUserLocation(current: LatLng) {
        if (!_isFollowing.value) return

        val path = _selectedPath.value ?: return
        val coords = path.coord ?: emptyList()

        // ✅ 1. 도착 지점 근처인지 확인 (완료 조건)
        val destination = coords.last().toLatLng()
        val distanceToDestination = current.distanceTo(destination)

        if (distanceToDestination < 20.0) {
            if (!_isRouteCompleted.value) {
                _isRouteCompleted.value = true
                _remainingDistance.value = 0f
                _offRoute.value = false
                viewModelScope.launch {
                    _invalidToken.send(UiEvent.ToastEvent("🎉 경로 완료! 수고하셨습니다!"))
                }
                Log.d("TrailFollowViewModel", "🎉 경로 완료!")
            }
            return
        }

        // ✅ 2. 경로에서 가장 가까운 지점 찾기
        var minDistance = Double.MAX_VALUE
        var closestIndex = 0

        for (i in coords.indices) {
            val dist = current.distanceTo(coords[i].toLatLng())
            if (dist < minDistance) {
                minDistance = dist
                closestIndex = i
            }
        }

        // ✅ 3. 남은 거리 계산
        var remaining = 0.0
        for (i in closestIndex until coords.size - 1) {
            remaining += coords[i].toLatLng().distanceTo(coords[i + 1].toLatLng())
        }
        _remainingDistance.value = remaining.toFloat()

        // ✅ 4. 이탈 감지
        _offRoute.value = minDistance > 30.0

        Log.d("TrailFollowViewModel", "📍 현재: 도착까지 ${remaining.toInt()}m, 경로까지 ${minDistance.toInt()}m")
    }

    // =================================================================
    // 📌 3. 사용자 위치 마커
    // =================================================================

    private val _userLocationMarker = MutableStateFlow<LatLng?>(null)
    val userLocationMarker = _userLocationMarker.asStateFlow()

    private fun updateUserLocationMarker(location: LatLng) {
        _userLocationMarker.value = location
    }

    fun clearUserLocationMarker() {
        _userLocationMarker.value = null
    }
}