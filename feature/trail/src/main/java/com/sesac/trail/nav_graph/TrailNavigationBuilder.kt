package com.sesac.trail.nav_graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.domain.model.Path
import com.sesac.common.ui_state.AuthUiState
import com.sesac.trail.presentation.PlaceViewModel
import com.sesac.trail.presentation.TrailCreateViewModel
import com.sesac.trail.presentation.TrailFollowViewModel
import com.sesac.trail.presentation.TrailMainViewModel
import com.sesac.trail.presentation.trail_create_screen.TrailCreateScreen
import com.sesac.trail.presentation.trail_main_screen.TrailMainScreen


fun NavGraphBuilder.trailRoute(
    mainViewModel: TrailMainViewModel,
    createViewModel: TrailCreateViewModel,
    followViewModel: TrailFollowViewModel,
    placeViewModel: PlaceViewModel,
    navController: NavController,
    uiState: AuthUiState,
    onStartFollowing: (Path) -> Unit,
    commonMapLifecycle : CommonMapLifecycle,
) {
    composable<TrailNavigationRoute.TrailMainTab> {
        TrailMainScreen(
            mainViewModel = mainViewModel,
            createViewModel = createViewModel,
            followViewModel = followViewModel,
            placeViewModel = placeViewModel,
            navController = navController,
            uiState = uiState,
            commonMapLifecycle = commonMapLifecycle,
            onStartFollowing = onStartFollowing,
        )
    }
    composable<TrailNavigationRoute.TrailCreateTab> {
        TrailCreateScreen(
            createViewModel = createViewModel,
            mainViewModel = mainViewModel,
            navController = navController,
//            uiState = uiState
        )
    }
//    composable<TrailNavigationRoute.TrailDetailTab> {
//        TrailDetailScreen(
//            viewModel = trailViewModel,
//            uiState = uiState,
//            navController = navController,
//            onStartFollowing = onStartFollowing,
//        )
//    }
}