package com.sesac.auth.nav_graph


import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sesac.auth.presentation.AuthViewModel
import com.sesac.auth.presentation.join_screen.AuthJoinScreen
import com.sesac.auth.presentation.login_screen.AuthLoginScreen
import com.sesac.auth.presentation.FindAccountViewModel
import com.sesac.auth.presentation.ui.FindAccountScreen

fun NavGraphBuilder.authRoute(
    navController: NavController,
    nav2Home: () -> Unit,
) {
    composable<AuthNavigationRoute.JoinTab> {
        val authViewModel: AuthViewModel = hiltViewModel()
        AuthJoinScreen(
            nav2Home = nav2Home,
            viewModel = authViewModel,
        )
    }
    composable<AuthNavigationRoute.LoginTab> {
        val authViewModel: AuthViewModel = hiltViewModel()
        AuthLoginScreen(
            viewModel = authViewModel,
            navController = navController,
//            onLoginSuccess = { navController.popBackStack() },
            onLoginSuccess = nav2Home,
            onNavigateToFindAccount = {
                navController.navigate(AuthNavigationRoute.FindAccountTab)
            }
        )
    }
    composable<AuthNavigationRoute.FindAccountTab> {
        val findAccountViewModel: FindAccountViewModel = hiltViewModel()

        FindAccountScreen(
            viewModel = findAccountViewModel,
            onNavigateBack = {
                navController.popBackStack() // 뒤로가기 시 로그인 화면으로 복귀
            }
        )
    }
}