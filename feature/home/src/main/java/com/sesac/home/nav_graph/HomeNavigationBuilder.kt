package com.sesac.home.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.common.model.PathParceler
import com.sesac.common.ui_state.AuthUiState
import com.sesac.home.presentation.ui.HomeScreen

fun NavGraphBuilder.homeRoute(
    uiState: AuthUiState,
    onNavigateToPathDetail: (PathParceler?) -> Unit,
    onNavigateToCommunity: () -> Unit,
    ) {
    composable<HomeNavigationRoute.HomeTab> {
        HomeScreen(
            uiState = uiState,
            onNavigateToPathDetail = onNavigateToPathDetail,
            onNavigateToCommunity =onNavigateToCommunity,
            )
    }
}