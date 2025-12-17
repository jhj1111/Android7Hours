package com.sesac.trail.presentation.ui
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.component.CommonMapView
import com.sesac.common.ui.theme.paddingLarge
import kotlinx.coroutines.delay
import com.sesac.domain.model.Coord
import com.sesac.common.utils.EffectPauseStop
import com.sesac.domain.model.Path
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.trail.nav_graph.TrailNavigationRoute
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.component.BottomSheetContent
import com.sesac.trail.presentation.component.MemoDialog
import com.sesac.trail.presentation.component.RecordingControls
import com.sesac.trail.presentation.component.ReopenSheetButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.OverlayImage
import com.sesac.common.model.toPathParceler
import com.sesac.domain.model.Place
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.presentation.component.FollowGuide
import com.sesac.trail.utils.toLatLng
import com.sesac.common.model.toParceler
import com.sesac.common.ui.theme.ColorBlue
import com.sesac.common.ui.theme.ColorPink
import com.sesac.common.ui.theme.SheetMinHeight
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
    onMapReady: ((NaverMap) -> Unit)? = null,
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

    // 마커 관리 리스트/맵
    val currentMarkers = viewModel.currentMarkers
    val infoWindowStates = remember { mutableStateMapOf<Marker, Boolean>() }

    // Place 마커 관리 (ViewModel 외부)
    val placeMarkers = remember { mutableListOf<Marker>() }

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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = SheetMinHeight,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContent = {
            val activeState = if (activeTab == WalkPathTab.RECOMMENDED) recommendedPaths else myPaths

            when(activeState) {
                is ResponseUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ResponseUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        Text(text = activeState.message)
                    }
                }
                else -> { // Success or Idle
                    BottomSheetContent(
                        uiState = uiState,
                        activeTab = activeTab,
                        recommendedPaths = (recommendedPaths as? ResponseUiState.Success)?.result ?: emptyList(),
                        myPaths = (myPaths as? ResponseUiState.Success)?.result ?: emptyList(),
                        currentUser = userInfo,
                        onSheetOpenToggle = {
                            scope.launch {
                                if (sheetState.currentValue == SheetValue.PartiallyExpanded) {
                                    sheetState.expand()
                                } else {
                                    sheetState.partialExpand()
                                }
                            }
                        },
                        onStartRecording = {
                            viewModel.startRecording()
                        },
                        onTabChange = { viewModel.updateActiveTab(it) },
                        onPathClick = {
                            viewModel.updateSelectedPath(it)
                            navController.navigate(NestedNavigationRoute.TrailDetail(it.toPathParceler()))
                        },
                        onFollowClick = onStartFollowing,
                        onModifyClick = { path ->
                            viewModel.updateSelectedPath(path)
                            navController.navigate(TrailNavigationRoute.TrailCreateTab)
                        },
                        onDeleteClick = { pathId -> viewModel.deletePath(pathId) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 지도 영역
            key(lifecycleState) {
                if (lifecycleState.isAtLeast(Lifecycle.State.CREATED)) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            val mapView = commonMapLifecycle.mapView ?: CommonMapView.getMapView(context).also {
                                commonMapLifecycle.setMapView(it)
                            }
                            (mapView.parent as? ViewGroup)?.removeView(mapView)
                            mapView.onStart()
                            mapView.onResume()
                            mapView.getMapAsync { naverMap ->
                                currentNaverMap = naverMap
                                naverMap.locationSource = locationSource
                                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                                naverMap.uiSettings.isLocationButtonEnabled = true
                                naverMap.uiSettings.isZoomControlEnabled = false
                                onMapReady?.invoke(naverMap)

                                val newPolyline = PolylineOverlay().apply {
                                    color = 0xFF0000FF.toInt()
                                    width = 10
                                    capType = PolylineOverlay.LineCap.Round
                                    joinType = PolylineOverlay.LineJoin.Round
                                }
                                viewModel.setPolylineInstance(newPolyline)

                                naverMap.setOnMapLongClickListener { _, coord ->
                                    if (isRecording) {
                                        selectedCoord = coord
                                        memoText = ""
                                        showMemoDialog = true
                                    }
                                }
                            }
                            mapView
                        },
                        update = {
                            it.requestLayout()
                        }
                    )
                }
            }
            // Place 마커 표시
            LaunchedEffect(placesState, currentNaverMap, isRecording, isFollowingPath) {
                val map = currentNaverMap ?: return@LaunchedEffect
                if (isRecording || isFollowingPath) {
                    placeMarkers.forEach { it.map = null }
                    placeMarkers.clear()
                    return@LaunchedEffect
                }
                placeMarkers.forEach { it.map = null }
                placeMarkers.clear()

                if (placesState is ResponseUiState.Success) {
                    val places = (placesState as ResponseUiState.Success<List<Place>>).result
                    places.forEach { place ->
                        val marker = Marker().apply {
                            position = place.toLatLng()
                            icon = Marker.DEFAULT_ICON
                            captionText = place.title
                            captionColor = ColorBlue.toArgb()
                            setOnClickListener {
                                navController.navigate(NestedNavigationRoute.PlaceDetail(place.toParceler()))
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

            // 추천 경로 마커 표시
            val recommendedPathMarkers = remember { mutableListOf<Marker>() }
            LaunchedEffect(recommendedPaths, currentNaverMap, isRecording, isFollowingPath) {
                val map = currentNaverMap ?: return@LaunchedEffect
                if (isRecording || isFollowingPath) {
                    recommendedPathMarkers.forEach { it.map = null }
                    recommendedPathMarkers.clear()
                    return@LaunchedEffect
                }
                recommendedPathMarkers.forEach { it.map = null }
                recommendedPathMarkers.clear()

                if (recommendedPaths is ResponseUiState.Success) {
                    val paths = (recommendedPaths as ResponseUiState.Success<List<Path>>).result
                    paths.forEach { path ->
                        path.coord?.firstOrNull()?.let { startCoord ->
                            val marker = Marker().apply {
                                position = startCoord.toLatLng()
                                icon = Marker.DEFAULT_ICON
                                iconTintColor = 0xFF6200EE.toInt()
                                captionText = path.pathName
                                captionColor = ColorPink.toArgb()
                                setOnClickListener {
                                    navController.navigate(NestedNavigationRoute.TrailDetail(path.toPathParceler()))
                                    true
                                }
                                this.map = map
                            }
                            recommendedPathMarkers.add(marker)
                        }
                    }
                }
            }

            // 추천 경로 마커 정리
            DisposableEffect(Unit) {
                onDispose {
                    recommendedPathMarkers.forEach { it.map = null }
                    recommendedPathMarkers.clear()
                }
            }

            // 선택된 경로의 폴리라인 표시
            DisposableEffect(isFollowingPath, selectedPath, currentNaverMap) {
                val map = currentNaverMap
                val path = selectedPath
                val followPolyline: PolylineOverlay?
                val startMarker: Marker?
                val endMarker: Marker?

                if (map != null && isFollowingPath && path != null) {
                    val coords = path.coord?.map { it.toLatLng() } ?: emptyList()
                    if (coords.size >= 2) {
                        followPolyline = PolylineOverlay().apply {
                            this.coords = coords
                            color = 0xFF6200EE.toInt()
                            width = 12
                            capType = PolylineOverlay.LineCap.Round
                            joinType = PolylineOverlay.LineJoin.Round
                            this.map = map
                        }
                        startMarker = Marker().apply {
                            position = coords.first()
                            icon = OverlayImage.fromResource(android.R.drawable.ic_input_add)
                            captionText = "출발"
                            captionColor = Color.Green.toArgb()
                            this.map = map
                        }
                        endMarker = Marker().apply {
                            position = coords.last()
                            icon = OverlayImage.fromResource(android.R.drawable.ic_menu_close_clear_cancel)
                            captionText = "도착"
                            captionColor = Color.Red.toArgb()
                            this.map = map
                        }
                        val cameraUpdate = CameraUpdate.scrollTo(coords.first())
                        map.moveCamera(cameraUpdate)
                    } else {
                        followPolyline = null
                        startMarker = null
                        endMarker = null
                    }
                } else {
                    followPolyline = null
                    startMarker = null
                    endMarker = null
                }

                onDispose {
                    followPolyline?.map = null
                    startMarker?.map = null
                    endMarker?.map = null
                }
            }

            // 사용자 현재 위치 마커
            val userLocation by viewModel.userLocationMarker.collectAsStateWithLifecycle()
            var userMarker by remember { mutableStateOf<Marker?>(null) }
            LaunchedEffect(userLocation, currentNaverMap, isFollowingPath) {
                val map = currentNaverMap ?: return@LaunchedEffect
                val location = userLocation
                if (isFollowingPath && location != null) {
                    try {
                        if (userMarker == null) {
                            userMarker = Marker().apply {
                                this.position = location
                                this.icon = OverlayImage.fromResource(android.R.drawable.ic_menu_mylocation)
                                this.width = 60
                                this.height = 60
                                this.map = map
                            }
                        } else {
                            userMarker?.position = location
                        }
                    } catch (e: Exception) {
                        Log.e("TrailMainScreen", "❌ 마커 생성/업데이트 실패: ${e.message}", e)
                    }
                } else {
                    userMarker?.map = null
                    userMarker = null
                }
            }

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
                FollowGuide(viewModel = viewModel,
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
                    selectedCoord?.let {
                        viewModel.addMemoMarker(it.latitude, it.longitude, memoText)
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

