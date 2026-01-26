package com.sesac.android7hours

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sesac.android7hours.common.AppTopBarData
import com.sesac.android7hours.common.topBarAsRouteName
import com.sesac.android7hours.nav_graph.AppBottomBarItem
import com.sesac.android7hours.nav_graph.AppNavHost
import com.sesac.auth.nav_graph.AuthNavigationRoute
import com.sesac.common.CommonViewModel
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.component.CommonMapView
import com.sesac.common.service.CurrentLocationService
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.community.nav_graph.CommunityNavigationRoute
import com.sesac.community.presentation.CommunityViewModel
import com.sesac.domain.model.Coord
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.home.nav_graph.EntryPointScreen
import com.sesac.home.nav_graph.HomeNavigationRoute
import com.sesac.home.nav_graph.TopBarAction
import com.sesac.mypage.nav_graph.MypageNavigationRoute
import com.sesac.mypage.presentation.MypageViewModel
import com.sesac.trail.nav_graph.NestedNavigationRoute
import com.sesac.trail.presentation.PlaceViewModel
import com.sesac.trail.presentation.TrailCreateViewModel
import com.sesac.trail.presentation.TrailDetailViewModel
import com.sesac.trail.presentation.TrailFollowViewModel
import com.sesac.trail.presentation.TrailMainViewModel
//import com.sesac.trail.presentation.TrailViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.sesac.common.R as cR


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val commonViewModel: CommonViewModel by viewModels()

    // ✅ [추가] Activity 생명주기에 귀속되는 MapLifecycle
    private lateinit var commonMapLifecycle: CommonMapLifecycle

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // 권한 획득 시 초기 위치 1회 로드
//            commonViewModel.fetchInitialLocation()
        } else {
            showPermissionDeniedDialog()
        }
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val deniedPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isEmpty()) {
            // 모든 권한이 이미 있으면 초기 위치 1회 로드
//            commonViewModel.fetchInitialLocation()
        } else {
            permissionLauncher.launch(deniedPermissions.toTypedArray())
        }
    }

    private fun startLocationService() {
        try {
            val intent = Intent(this, CurrentLocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            commonViewModel.updateLocationServiceState(true)
            Log.d("TAG-MainActivity", "Location service started.")
        } catch (e: SecurityException) {
            Log.e("TAG-MainActivity", "SecurityException: ${e.message}")
            showPermissionDeniedDialog()
        } catch (e: Exception) {
            Log.e("TAG-MainActivity", "Failed to start service: ${e.message}")
        }
    }

    private fun stopLocationService() {
        stopService(Intent(this, CurrentLocationService::class.java))
        commonViewModel.updateLocationServiceState(false)
        Log.d("TAG-MainActivity", "Location service stopped.")
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("앱의 전체 기능을 사용하려면 위치 권한과 알림 권한이 필요합니다.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val lifecycle = lifecycleOwner.lifecycle
            val uiState by commonViewModel.uiState.collectAsStateWithLifecycle()
            val isLocationServiceRunning by commonViewModel.isLocationServiceRunning.collectAsStateWithLifecycle()

            // 🔥 더 이상 필요 없음! 각 화면에서 독립적으로 생성함

            val trailMainViewModel = hiltViewModel<TrailMainViewModel>()
            val trailCreateViewModel = hiltViewModel<TrailCreateViewModel>()
            val trailDetailViewModel = hiltViewModel<TrailDetailViewModel>()
            val followViewModel = hiltViewModel<TrailFollowViewModel>()
            val placeViewModel = hiltViewModel<PlaceViewModel>()
            val communityViewModel = hiltViewModel<CommunityViewModel>()
            val mypageViewModel = hiltViewModel<MypageViewModel>()
            val navController = rememberNavController()
            val startDestination = HomeNavigationRoute.HomeTab
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            // 서비스 실행 로직 중앙화
            LaunchedEffect(uiState, isLocationServiceRunning) {
                val shouldBeRunning = uiState.isLoggedIn && uiState.user?.isPet == true

                if (shouldBeRunning && !isLocationServiceRunning) {
                    startLocationService()
                } else if (!shouldBeRunning && isLocationServiceRunning) {
                    stopLocationService()
                }
            }

            // 초기 위치가 확보되면 추천 경로를 미리 로드
            val initialLocationState by commonViewModel.initialLocation.collectAsStateWithLifecycle()
            LaunchedEffect(initialLocationState) {
                if (initialLocationState is ResponseUiState.Success) {
                    val coord = (initialLocationState as ResponseUiState.Success<Coord?>).result
                    if (coord != null) {
                        trailMainViewModel.loadInitialPaths(coord)
                    }
                }
            }

            val topBarActions = if (uiState.isLoggedIn) {
                listOf(
                    TopBarAction.TextAction(text = uiState.user?.nickname ?: "User"),
                    TopBarAction.IconAction(
                        icon = uiState.user?.profileImageUrl ?: Icons.Default.AccountCircle,
                        contentDescription = "Mypage",
                        onClick = { navController.navigate(MypageNavigationRoute.MainTab) }
                    )
                )
            } else {
                listOf(
                    TopBarAction.IconAction(
                        icon = Icons.Default.AccountCircle,
                        contentDescription = "Login",
                        onClick = { navController.navigate(AuthNavigationRoute.LoginTab) }
                    )
                )
            }

            val loginRequiredScreen = listOf(
                stringResource(cR.string.mypage_main),
                stringResource(cR.string.mypage_myinfo),
                stringResource(cR.string.mypage_management),
                stringResource(cR.string.mypage_setting),
                stringResource(cR.string.mypage_favorite),
                stringResource(cR.string.mypage_register_pet),
            )

            val currentTopBarData = navBackStackEntry?.topBarAsRouteName ?: AppTopBarData()
            val finalTopBarData = if (currentTopBarData is AppTopBarData) {
                currentTopBarData.copy(actions = topBarActions)
            } else {
                currentTopBarData
            }

            val appBottomBarItem = remember { AppBottomBarItem().fetch() }
            val isSearchOpen = remember { mutableStateOf(false) }
            val permissionStates = remember { mutableStateMapOf<String, Boolean>() }
            val isRecording by trailMainViewModel.isRecording.collectAsStateWithLifecycle()

            Android7HoursTheme {
                LaunchedEffect(uiState) {
                    if (!uiState.isLoggedIn && loginRequiredScreen.contains(finalTopBarData.title)){
                        navController.navigate(AuthNavigationRoute.LoginTab)
                    }
                }

                EntryPointScreen(
                    isRecording = isRecording,
                    navController = navController,
                    startDestination = startDestination,
                    scaffoldActionCases = listOf(
                        stringResource(cR.string.community)
                    ),
                    navBackOptions = listOf(
                        stringResource(cR.string.mypage_management),
                        stringResource(cR.string.mypage_favorite),
                        stringResource(cR.string.mypage_setting),
                        stringResource(cR.string.trail_create_page),
                        stringResource(cR.string.trail_detail_page),
                        stringResource(cR.string.mypage_myinfo),
                        stringResource(cR.string.mypage_register_pet),
                        stringResource(cR.string.trail_info_detail_page),
                    ),
                    appTopBarData = finalTopBarData,
                    appBottomBarItem = appBottomBarItem,
                    isSearchOpen = isSearchOpen,
                    screensWithCustomTopBar = listOf(stringResource(cR.string.community)),
                    navHost = { paddingValues ->
                        AppNavHost(
                            trailMainViewModel = trailMainViewModel,
                            placeViewModel = placeViewModel,
                            trailCreateViewModel = trailCreateViewModel,
                            trailDetailViewModel = trailDetailViewModel,
                            followViewModel = followViewModel,
                            communityViewModel = communityViewModel,
                            mypageViewModel = mypageViewModel,
                            paddingValues = paddingValues,
                            navController = navController,
                            nav2Home = { navController.navigate(HomeNavigationRoute.HomeTab) },
                            nav2LoginScreen = { navController.navigate(AuthNavigationRoute.LoginTab) },
                            onNavigateToPathDetail = { path ->
                                path?.let {
                                    navController.navigate(NestedNavigationRoute.TrailDetail(it))
                                }
                            },
                            onNavigateToCommunity = { navController.navigate(CommunityNavigationRoute.MainTab) },
                            startDestination = startDestination,
                            uiState = uiState,
                            onStartFollowing = { path ->
                                followViewModel.startFollowing(path)
                                trailMainViewModel.updateIsSheetOpen(false)
                                Log.d("Tag-MainActivity", "Following path: ${path.pathName}")
                            },
                            permissionState = permissionStates,
                        )
                    }
                )
            }
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val cF = currentFocus
            if (cF is EditText) {
                val outRect = Rect()
                cF.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    cF.clearFocus()
                    val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(cF.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}