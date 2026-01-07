package com.sesac.home.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.common.model.PathParceler
import com.sesac.home.presentation.home_main.HomeScreen

fun NavGraphBuilder.homeRoute(
    onNavigateToPathDetail: (PathParceler?) -> Unit,
    onNavigateToCommunity: () -> Unit,
    ) {
    composable<HomeNavigationRoute.HomeTab> {
        HomeScreen(
            onNavigateToPathDetail = onNavigateToPathDetail,
            onNavigateToCommunity =onNavigateToCommunity,
            )
    }
}