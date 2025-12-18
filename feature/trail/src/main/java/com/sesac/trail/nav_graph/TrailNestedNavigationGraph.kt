package com.sesac.trail.nav_graph
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sesac.common.model.PathParceler
import com.sesac.common.model.PlaceParceler
import com.sesac.common.model.PlaceParcelerNavType // Add this import
import com.sesac.common.model.parcelableType
import com.sesac.common.model.toPlace
import com.sesac.domain.model.Path
import com.sesac.domain.result.AuthUiState
import com.sesac.trail.presentation.TrailViewModel
import com.sesac.trail.presentation.place_info_detail_screen.PlaceInfoDetailScreen
import com.sesac.trail.presentation.trail_detail_screen.TrailDetailScreen
import kotlin.reflect.typeOf

// ... other imports ...

fun NavGraphBuilder.trailNestedNavGraph(
    uiState: AuthUiState,
    trailViewModel: TrailViewModel,
    navController: NavController,
    onStartFollowing: (Path) -> Unit,
) {
    composable<NestedNavigationRoute.TrailDetail>(
        typeMap = mapOf(typeOf<PathParceler>() to parcelableType<PathParceler>())
    ) { navBackStackEntry ->
        val selectedDetailPath = navBackStackEntry.toRoute<NestedNavigationRoute.TrailDetail>().pathParceler.toPath()
        TrailDetailScreen(
            uiState = uiState,
            viewModel = trailViewModel,
            navController = navController,
            selectedDetailPath = selectedDetailPath,
            onStartFollowing = onStartFollowing,
            onEditClick = { path ->
                navController.navigate(TrailNavigationRoute.TrailCreateTab)
            },
            onDeleteClick = { path ->
                trailViewModel.deletePath(path.id)
                navController.popBackStack()
            }
        )
    }
    // ===== 2. Place Detail =====
    composable<NestedNavigationRoute.PlaceDetail>(
        typeMap = mapOf(
            typeOf<PlaceParceler>() to PlaceParcelerNavType
        )
    ) { backStackEntry ->
        val args = backStackEntry.toRoute<NestedNavigationRoute.PlaceDetail>()
        val loadedPlace = args.placeParceler.toPlace()
        val viewModel: TrailViewModel = hiltViewModel()

        PlaceInfoDetailScreen(
            place = loadedPlace,
            onBackClick = { navController.popBackStack() },
            viewModel = viewModel
        )
    }
}