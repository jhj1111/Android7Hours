package com.sesac.android7hours.nav_graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sesac.auth.nav_graph.authRoute
import com.sesac.common.component.CommonMapView
import com.sesac.common.model.PathParceler
import com.sesac.community.nav_graph.communityRoute
import com.sesac.community.presentation.CommunityViewModel
import com.sesac.domain.model.Path
import com.sesac.common.ui_state.AuthUiState
import com.sesac.home.nav_graph.homeRoute
import com.sesac.monitor.nav_graph.monitorRoute
import com.sesac.mypage.nav_graph.mypageRoute
import com.sesac.mypage.presentation.MypageViewModel
import com.sesac.trail.nav_graph.trailNestedNavGraph
import com.sesac.trail.nav_graph.trailRoute
import com.sesac.trail.presentation.PlaceViewModel
import com.sesac.trail.presentation.TrailCreateViewModel
import com.sesac.trail.presentation.TrailDetailViewModel
import com.sesac.trail.presentation.TrailFollowViewModel
import com.sesac.trail.presentation.TrailMainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    trailMainViewModel: TrailMainViewModel,
    trailCreateViewModel: TrailCreateViewModel,
    trailDetailViewModel: TrailDetailViewModel,
    placeViewModel: PlaceViewModel,
    followViewModel: TrailFollowViewModel,
    communityViewModel: CommunityViewModel,
    mypageViewModel: MypageViewModel,
    navController: NavHostController,
    nav2Home: () -> Unit,
    nav2LoginScreen: () -> Unit,
    onNavigateToPathDetail: (PathParceler?) -> Unit,
    onNavigateToCommunity: () -> Unit,
    startDestination: Any,
    uiState: AuthUiState,
    onStartFollowing: (Path) -> Unit,
    permissionState: SnapshotStateMap<String, Boolean>,
) {
    // 앱 종료 시 MapView 리소스를 완전히 해제하기 위한 Effect
    DisposableEffect(Unit) {
        onDispose {
            CommonMapView.clear()
        }
    }

    NavHost(
        modifier = Modifier.padding(paddingValues = paddingValues),
        navController = navController,
        startDestination = startDestination,
    ) {
        homeRoute(
            onNavigateToPathDetail = onNavigateToPathDetail,
            onNavigateToCommunity = onNavigateToCommunity,
        )
        trailRoute(
            mainViewModel = trailMainViewModel,
            createViewModel = trailCreateViewModel,
            placeViewModel = placeViewModel,
            followViewModel = followViewModel,
            navController = navController,
            uiState = uiState,
            onStartFollowing = onStartFollowing,
        )
        trailNestedNavGraph(
            uiState = uiState,
            detailViewModel = trailDetailViewModel,
            placeViewModel = placeViewModel,
            navController = navController,
            onStartFollowing = onStartFollowing,
        )
        communityRoute(
            uiState = uiState,
            nav2LoginScreen = nav2LoginScreen,
            viewModel = communityViewModel)

        monitorRoute(
            authorUiState = uiState,
        )
        mypageRoute(
            mypageViewModel = mypageViewModel,
            navController = navController,
            nav2LoginScreen = nav2LoginScreen,
            onNavigateToPathDetail = onNavigateToPathDetail,
            permissionState = permissionState,
            uiState = uiState,
        )
        authRoute(
            navController = navController,
            nav2Home = nav2Home,
        )
    }
}