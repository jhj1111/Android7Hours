package com.sesac.trail.nav_graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.domain.model.Path
import com.sesac.common.ui_state.AuthUiState
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.trail_create_screen.TrailCreateScreen
import com.sesac.trail.presentation.trail_main_screen.TrailMainScreen


fun NavGraphBuilder.trailRoute(
    trailViewModel: TrailViewModel,
    navController: NavController,
    uiState: AuthUiState,
    onStartFollowing: (Path) -> Unit,
    commonMapLifecycle : CommonMapLifecycle,
) {
    composable<TrailNavigationRoute.TrailMainTab> {
        TrailMainScreen(
            viewModel = trailViewModel,
            navController = navController,
            uiState = uiState,
            commonMapLifecycle = commonMapLifecycle,
            onStartFollowing = onStartFollowing,
        )
    }
    composable<TrailNavigationRoute.TrailCreateTab> {
        TrailCreateScreen(
            viewModel = trailViewModel,
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