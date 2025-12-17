package com.sesac.community.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.community.presentation.CommunityViewModel
import com.sesac.community.presentation.post_main.CommunityMainScreen
import com.sesac.common.ui_state.AuthUiState

fun NavGraphBuilder.communityRoute(
    nav2LoginScreen: () -> Unit,
    uiState: AuthUiState,
    viewModel: CommunityViewModel,
    ) {
    composable<CommunityNavigationRoute.MainTab>() {
        CommunityMainScreen(
            nav2LoginScreen = nav2LoginScreen,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}