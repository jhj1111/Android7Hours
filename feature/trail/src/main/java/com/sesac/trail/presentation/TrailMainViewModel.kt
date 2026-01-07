package com.sesac.trail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.sesac.common.model.UiEvent
import com.sesac.domain.model.Coord
import com.sesac.domain.model.Path
import com.sesac.domain.model.User
import com.sesac.domain.result.AuthResult
import com.sesac.domain.result.LocationFlowResult
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.domain.usecase.location.LocationUseCase
import com.sesac.domain.usecase.path.PathUseCase
import com.sesac.domain.usecase.session.SessionUseCase
import com.sesac.trail.presentation.trail_main_screen.WalkPathTab
import com.sesac.trail.utils.toLatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TrailMainViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val pathUseCase: PathUseCase,
    private val locationUseCase: LocationUseCase,
) : ViewModel() {

    private val _invalidToken = Channel<UiEvent>()
    val invalidToken = _invalidToken.receiveAsFlow()

    private var lastRecommendedPathFetchLocation: LatLng? = null
    private var areInitialPathsLoaded = false

    // =================================================================
    // 📌 1. 위치 & 녹화 관련
    // =================================================================

    private val _currentLocation = MutableStateFlow<ResponseUiState<Coord?>>(ResponseUiState.Idle)

    private val _tempPathCoords = MutableStateFlow<List<LatLng>>(emptyList())
    val tempPathCoords = _tempPathCoords.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _recordingTime = MutableStateFlow(0L)
    val recordingTime = _recordingTime.asStateFlow()

    fun startLocationUpdates() {
        Log.d("TrailMainViewModel", "startLocationUpdates() called")
        viewModelScope.launch {
            locationUseCase.getRealtimeLocationUseCase().collect { result ->
                when (result) {
                    is LocationFlowResult.Success -> {
                        val newLocation = result.coord
                        val newPoint = newLocation.toLatLng()

                        // 녹화 중일 때 좌표 추가
                        if (_isRecording.value) {
                            val lastPoint = _tempPathCoords.value.lastOrNull()
                            if (lastPoint != null) {
                                val diff = lastPoint.distanceTo(newPoint)
                                if (diff < 5) { // 5m 미만 이동은 무시
                                    return@collect
                                }
                            }
                            addTempPoint(newPoint)
                        }

                        // 스마트 데이터 로딩 (1km 이상 이동 시)
                        val distance = lastRecommendedPathFetchLocation?.distanceTo(newLocation.toLatLng()) ?: Double.MAX_VALUE
                        if (distance > 1000) {
                            Log.d("TrailMainViewModel", "Fetching new recommended paths. Moved ${distance}m")
                            getRecommendedPaths(newLocation, 5000f)
                            lastRecommendedPathFetchLocation = newLocation.toLatLng()
                        }
                    }
                    is LocationFlowResult.Error -> {
                        Log.e("TrailMainViewModel", "Location error: ${result.exception.message}")
                    }
                }
            }
        }
    }

    private fun addTempPoint(point: LatLng) {
        _tempPathCoords.value = _tempPathCoords.value + point
        Log.d("TrailMainViewModel", "📍 좌표 추가: 총 ${_tempPathCoords.value.size}개")
    }

    fun clearTempPath() {
        _tempPathCoords.value = emptyList()
        Log.d("TrailMainViewModel", "🧹 임시 경로 초기화")
    }

    fun startRecording() {
        _isRecording.value = true
        _recordingTime.value = 0L
        clearTempPath()
        Log.d("TrailMainViewModel", "🎬 녹화 시작")
    }

    fun stopRecording() {
        _isRecording.value = false
        Log.d("TrailMainViewModel", "⏹️ 녹화 중지")
    }

    fun updateRecordingTime(changeRate: Long?) {
        _recordingTime.value += changeRate ?: -_recordingTime.value
    }

    // =================================================================
    // 📌 2. 지도 오버레이 관리
    // =================================================================

    private val _polylineOverlay = MutableStateFlow<PolylineOverlay?>(null)
    val polylineOverlay = _polylineOverlay.asStateFlow()

    val currentMarkers: MutableList<Marker> = mutableListOf()

    fun setPolylineInstance(polyline: PolylineOverlay) {
        _polylineOverlay.value = polyline
        Log.d("TrailMainViewModel", "✅ Polyline 인스턴스 설정")
    }

    fun clearAllMapObjects(naverMap: NaverMap?) {
        if (naverMap == null) return

        _polylineOverlay.value?.map = null
        _polylineOverlay.value = null

        currentMarkers.forEach { marker ->
            marker.map = null
        }
        currentMarkers.clear()

        Log.d("TrailMainViewModel", "🧹 지도 객체 초기화 완료")
    }

    // =================================================================
    // 📌 3. 경로 목록 관리
    // =================================================================

    private val _recommendedPaths = MutableStateFlow<ResponseUiState<List<Path>>>(ResponseUiState.Idle)
    val recommendedPaths = _recommendedPaths.asStateFlow()

    private val _myPaths = MutableStateFlow<ResponseUiState<List<Path>>>(ResponseUiState.Idle)
    val myPaths = _myPaths.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun loadInitialPaths(coord: Coord) {
        if (areInitialPathsLoaded) return
        getRecommendedPaths(coord)
        areInitialPathsLoaded = true
    }

    fun getCurrentUserInfo() {
        viewModelScope.launch {
            _userInfo.value = sessionUseCase.getUserInfo().first()
        }
    }

    fun getRecommendedPaths(coord: Coord, radius: Float = 5000f) {
        viewModelScope.launch {
            _recommendedPaths.value = ResponseUiState.Loading
            pathUseCase.getAllRecommendedPathsUseCase(coord, radius)
                .catch { e ->
                    _recommendedPaths.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { pathsResult ->
                    when (pathsResult) {
                        is AuthResult.Success -> {
                            Log.d("TrailMainViewModel", "현재 위치 : $coord")
                            _recommendedPaths.value = ResponseUiState.Success("추천 경로를 불러왔습니다.", pathsResult.resultData)
                        }
                        is AuthResult.NetworkError -> {
                            _recommendedPaths.value = ResponseUiState.Error(pathsResult.exception.message ?: "unknown")
                        }
                        else -> Unit
                    }
                }
        }
    }

    fun getMyPaths() {
        viewModelScope.launch {
            _myPaths.value = ResponseUiState.Loading
            val token = sessionUseCase.getAccessToken().first()
            if (token == null) {
                _myPaths.value = ResponseUiState.Error("로그인이 필요합니다.")
                return@launch
            }
            pathUseCase.getMyPaths(token)
                .catch { e ->
                    _myPaths.value = ResponseUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
                .collectLatest { pathsResult ->
                    when (pathsResult) {
                        is AuthResult.Success -> {
                            _myPaths.value = ResponseUiState.Success("내 경로를 불러왔습니다.", pathsResult.resultData)
                        }
                        is AuthResult.NetworkError -> {
                            _myPaths.value = ResponseUiState.Error(pathsResult.exception.message ?: "unknown")
                        }
                        else -> Unit
                    }
                }
        }
    }

    // =================================================================
    // 📌 4. UI 상태 관리
    // =================================================================

    private val _isSheetOpen = MutableStateFlow(false)
    val isSheetOpen get() = _isSheetOpen.asStateFlow()

    private val _activeTab = MutableStateFlow(WalkPathTab.RECOMMENDED)
    val activeTab get() = _activeTab.asStateFlow()

    fun updateIsSheetOpen(newState: Boolean?) {
        viewModelScope.launch {
            _isSheetOpen.value = newState ?: !_isSheetOpen.value
        }
    }

    fun updateActiveTab(walkPathTab: WalkPathTab) {
        viewModelScope.launch {
            _activeTab.value = walkPathTab
        }
    }
}



