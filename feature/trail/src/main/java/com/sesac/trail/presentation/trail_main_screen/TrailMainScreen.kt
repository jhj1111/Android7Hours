package com.sesac.trail.presentation.trail_main_screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavController
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.ui.theme.paddingLarge
import kotlinx.coroutines.delay
import com.sesac.domain.model.Coord
import com.sesac.common.utils.EffectPauseStop
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthUiState
import com.sesac.trail.nav_graph.TrailNavigationRoute
import com.sesac.trail.presentation.TrailViewModel
import androidx.compose.runtime.DisposableEffect
import com.sesac.common.model.toPathParceler
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.presentation.component.FollowGuide
import com.sesac.trail.presentation.component.FollowPathPolyline
import kotlinx.coroutines.launch

enum class WalkPathTab { RECOMMENDED, MY_RECORDS }

// --- Main Page Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailMainScreen(
    viewModel: TrailViewModel = hiltViewModel(),
    navController: NavController,
    uiState: AuthUiState,
    commonMapLifecycle : CommonMapLifecycle,
    onStartFollowing: (Path) -> Unit,
) {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleState by lifecycle.currentStateAsState()
    // ViewModel State 수집
    val recommendedPaths by viewModel.recommendedPaths.collectAsStateWithLifecycle()
    val myPaths by viewModel.myPaths.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val isFollowingPath by viewModel.isFollowingPath.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingTime by viewModel.recordingTime.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val tempPathCoords by viewModel.tempPathCoords.collectAsStateWithLifecycle()
    val polylineFromVM by viewModel.polylineOverlay.collectAsStateWithLifecycle()
    val placesState by viewModel.placesState.collectAsStateWithLifecycle()

    // 네이버 지도 위치 소스
    val locationSource = remember {
        activity?.let { FusedLocationSource(it, 1000) }
            ?: throw IllegalStateException("Activity not found for FusedLocationSource")
    }
    // 위치 권한 상태 추적
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 위치 권한 요청
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // 메모 입력용 상태
    var showMemoDialog by remember { mutableStateOf(false) }
    var selectedCoord by remember { mutableStateOf<LatLng?>(null) }
    var memoText by remember { mutableStateOf("") }

    // NaverMap 저장 위한 변수
    var currentNaverMap by remember { mutableStateOf<NaverMap?>(null) }


    // 폴리라인 좌표 업데이트
    LaunchedEffect(tempPathCoords.size, isRecording) {
        val currentPolyline = polylineFromVM

        if (isRecording && tempPathCoords.size >= 2) {
            currentPolyline?.coords = tempPathCoords.toList()
            currentPolyline?.map = currentNaverMap
        } else {
            currentPolyline?.map = null
        }
    }
    // Draft, 경로 목록, 사용자 정보 초기화
    LaunchedEffect(Unit, hasLocationPermission, uiState) {
        if (hasLocationPermission) {
            viewModel.startLocationUpdates()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        viewModel.loadDrafts()
        viewModel.getMyPaths()
        viewModel.getCurrentUserInfo() // 현재 사용자 정보 요청
    }

    // --- 타이머 로직 (녹화 중일 때 시간 증가) ---
    LaunchedEffect(lifecycleState, isRecording) {
        while (isRecording && lifecycleState == Lifecycle.State.RESUMED) {
            delay(1000)
            viewModel.updateRecordingTime(1)
        }
    }
    // effectPauseStop 적용
    lifecycle.EffectPauseStop {
        commonMapLifecycle.mapView?.onPause()
        commonMapLifecycle.mapView?.onStop()
    }

    DisposableEffect(Unit) {
        onDispose {
            currentNaverMap?.locationSource = null
            locationSource.deactivate()
        }
    }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    // 녹화 또는 따라가기 시작 시 시트 숨기기
    LaunchedEffect(isRecording, isFollowingPath) {
        if (isRecording || isFollowingPath) {
            scope.launch { sheetState.hide() }
        }
    }

    // 하단 바텀 시트
    TrailBottomSheet(
        scaffoldState = scaffoldState,
        activeTab = activeTab,
        recommendedPathsState = recommendedPaths,
        myPathsState = myPaths,
        uiState = uiState,
        currentUser = userInfo,
        onSheetOpenToggle = { },
        onStartRecording = { viewModel.startRecording() },
        onTabChange = { tab -> viewModel.updateActiveTab(tab) },
        onPathClick = { path ->
            viewModel.updateSelectedPath(path)
            navController.navigate(
                NestedNavigationRoute.TrailDetail(path.toPathParceler())
            )
        },
        onFollowClick = onStartFollowing,
        onModifyClick = { path ->
            viewModel.updateSelectedPath(path)
            navController.navigate(TrailNavigationRoute.TrailCreateTab)
        },
        onDeleteClick = { pathId: Int ->
            viewModel.deletePath(pathId)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 지도 영역
            key(lifecycleState) {
                if (lifecycleState.isAtLeast(Lifecycle.State.CREATED)) {
                    TrailMap(
                        modifier = Modifier.fillMaxSize(),
                        commonMapLifecycle = commonMapLifecycle,
                        locationSource = locationSource,
                        isRecording = isRecording,
                        onMapReady = { currentNaverMap = it },
                        viewModel = viewModel,
                        selectedCoordSetter = { selectedCoord = it },
                        showMemoDialogSetter = { showMemoDialog = it },
                        memoTextSetter = { memoText = it }
                    )
                }
            }

            // Place 마커 표시
            PlaceMarkers(
                naverMap = currentNaverMap,
                placesState = placesState,
                isRecording = isRecording,
                isFollowingPath = isFollowingPath,
                navController = navController
            )

            // 추천 경로 마커 표시
            RecommendedPathMarkers(
                naverMap = currentNaverMap,
                pathsState = recommendedPaths,
                isVisible = !isRecording && !isFollowingPath,
                onPathClick = {
                    navController.navigate(
                        NestedNavigationRoute.TrailDetail(it.toPathParceler())
                    )
                }
            )

            // 선택된 경로의 폴리라인 표시
            FollowPathPolyline(
                naverMap = currentNaverMap,
                isFollowingPath = isFollowingPath,
                path = selectedPath
            )

            // 시트 다시 열기 버튼
            AnimatedVisibility(
                visible = sheetState.currentValue == SheetValue.Hidden && !isRecording && !isFollowingPath,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = paddingLarge * 2)
            ) {
                ReopenSheetButton(onClick = { scope.launch { sheetState.partialExpand() } })
            }

            // 따라가기 안내 UI
            AnimatedVisibility(
                visible = isFollowingPath,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp)
            ) {
                FollowGuide(
                    viewModel = viewModel,
                    onStopFollowing = {
                        viewModel.stopFollowing()
                        viewModel.updateIsFollowingPath(false)
                        viewModel.clearUserLocationMarker()
                    }
                )
            }

            // 녹화 중 UI
            AnimatedVisibility(
                visible = isRecording,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 128.dp)
            ) {
                RecordingControls(
                    recordingTime = recordingTime,
                    onStopRecording = {
                        val recordedCoords = tempPathCoords.map { latLng -> Coord(latLng.latitude, latLng.longitude) }
                        val currentMemoMarkers = viewModel.memoMarkers.value
                        val newPath = Path.EMPTY.copy(coord = recordedCoords, markers = currentMemoMarkers)
                        viewModel.updateSelectedPath(newPath)
                        viewModel.stopRecording()
                        viewModel.clearAllMapObjects(currentNaverMap)
                        currentNaverMap?.locationTrackingMode = LocationTrackingMode.Follow
                        navController.navigate(TrailNavigationRoute.TrailCreateTab)
                    }
                )
            }

            MemoDialog(
                show = showMemoDialog,
                memoText = memoText,
                onTextChange = { memoText = it },
                onCancel = { showMemoDialog = false },
                onConfirm = {
                    val coord = selectedCoord
                    val map = currentNaverMap

                    if (coord != null && map != null) {

                        // 실제 지도에 마커 추가
                        val marker = Marker().apply {
                            position = coord
                            this.map = map
                        }

                        // ViewModel에는 "데이터"만 저장
                        viewModel.addMemoMarker(
                            coord.latitude,
                            coord.longitude,
                            memoText
                        )
                    }
                    viewModel.selectedPath.value?.let { currentPath ->
                        val currentDescription = currentPath.pathComment ?: ""
                        val newDescription = if (currentDescription.isEmpty()) memoText else "$currentDescription\n\n$memoText"
                        viewModel.updateSelectedPath(currentPath.copy(pathComment = newDescription))
                    }
                    showMemoDialog = false
                }
            )
        }
    }
}

