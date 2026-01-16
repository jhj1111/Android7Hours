package com.sesac.mypage.nav_graph

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sesac.common.model.PathParceler
import com.sesac.common.ui_state.AuthUiState
import com.sesac.mypage.presentation.MypageViewModel
import com.sesac.mypage.presentation.mypage_add_pet.AddPetScreen
import com.sesac.mypage.presentation.mypage_detail.MypageDetailScreen
import com.sesac.mypage.presentation.mypage_bookmark.MypageBookmarkScreen
import com.sesac.mypage.presentation.mypage_main.MypageMainScreen
import com.sesac.mypage.presentation.mypage_manage.MypageManageScreen
import com.sesac.mypage.presentation.mypage_setting.MypageSettingScreen

fun NavGraphBuilder.mypageRoute(
    mypageViewModel: MypageViewModel,
    navController: NavController,
    nav2LoginScreen: () -> Unit,
    onNavigateToPathDetail: (PathParceler) -> Unit,
    uiState: AuthUiState,
    permissionState: SnapshotStateMap<String, Boolean>,
    ) {
    composable<MypageNavigationRoute.MainTab> {
        MypageMainScreen(
            navController = navController,
            nav2LoginScreen = nav2LoginScreen,
            uiState = uiState,
        )
    }
    composable<MypageNavigationRoute.ManageTab> {
        MypageManageScreen(
            uiState = uiState,
            viewModel = mypageViewModel,
        )
    }
    composable<MypageNavigationRoute.FavoriteTab> {
        MypageBookmarkScreen(uiStatus = uiState, onNavigateToPathDetail = onNavigateToPathDetail)
    }
    composable<MypageNavigationRoute.SettingTab> {
        MypageSettingScreen(permissionStates = permissionState)
    }
    composable<MypageNavigationRoute.DetailScreen> {
        MypageDetailScreen(
            viewModel = mypageViewModel,
            navController = navController,
            uiState = uiState,
        )
    }
    composable<MypageNavigationRoute.AddPetScreen> { backStackEntry ->
        val petInfo = backStackEntry.toRoute<MypageNavigationRoute.AddPetScreen>()
        AddPetScreen(
            petId = petInfo.petId,
            viewModel = mypageViewModel,
            navController = navController,
            uiState = uiState,
        )
    }
}