package com.sesac.monitor.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.common.component.CommonMapLifecycle
import com.sesac.common.ui_state.AuthUiState
import com.sesac.monitor.presentation.monitor_main.MonitorMainScreen


fun NavGraphBuilder.monitorRoute(
    authorUiState: AuthUiState,
) {
    composable<MonitorNavigationRoute.MainTab> {
        MonitorMainScreen(
            authorUiState = authorUiState,
        )
    }
}