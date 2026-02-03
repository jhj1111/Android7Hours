package com.sesac.mypage.presentation.mypage_main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sesac.common.R
import com.sesac.common.component.CommonMenuItem
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui_state.AuthUiState
import com.sesac.common.ui_state.ResponseUiState
import com.sesac.common.config.defaultProfileImageUrl
import com.sesac.domain.model.MypageMenuItem
import com.sesac.mypage.nav_graph.MypageNavigationRoute
import com.sesac.mypage.presentation.MypageViewModel

val menuItems = listOf(
    MypageMenuItem(key = "MANAGE", icon = Icons.Default.CalendarToday, labels = listOf("일정관리")),
    MypageMenuItem(key = "FAVORITE", icon = Icons.Default.Star, labels = listOf("즐겨찾기")),
    MypageMenuItem(key = "SETTING", icon = Icons.Default.Settings, labels = listOf("설정")),
    MypageMenuItem(key = "HELP", icon = Icons.AutoMirrored.Filled.Help, labels = listOf("도움말")),
    MypageMenuItem(key = "POLICY", icon = Icons.Default.Shield, labels = listOf("개인정보 처리방침"))
)

@Composable
fun MypageMainScreen(
    navController: NavController,
    nav2LoginScreen: () -> Unit,
    viewModel: MypageViewModel = hiltViewModel(),
    uiState: AuthUiState,
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState.isLoggedIn){
            viewModel.getStats(uiState)
        } else {
            nav2LoginScreen()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = paddingLarge)
    ) {
        item {
            ProfileHeaderView(
                name = uiState.user?.fullName ?: "",
                email = uiState.user?.email ?: "",
                imageUrl = uiState.user?.profileImageUrl ?: defaultProfileImageUrl,
                onNavigateToProfile = { navController.navigate(MypageNavigationRoute.DetailScreen) }
            )
        }

        item {
            when (val s = stats) {
                is ResponseUiState.Loading -> CircularProgressIndicator()
                is ResponseUiState.Success -> StatsSectionView(stats = s.result)
                is ResponseUiState.Error -> Text(s.message)
                else -> {}
            }
        }

        item {
            Text(
                text = stringResource(R.string.common_menu),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(
                    start = paddingLarge,
                    bottom = paddingSmall
                )
            )
        }
        items(menuItems) { item ->
            CommonMenuItem(
                item = item,
                onClick = {
                    when (item.key) {
                        "MANAGE" -> navController.navigate(MypageNavigationRoute.ManageTab)
                        "FAVORITE" -> navController.navigate(MypageNavigationRoute.FavoriteTab)
                        "SETTING" -> navController.navigate(MypageNavigationRoute.SettingTab)
                        // Handle other keys or do nothing
                    }
                }
            )
        }

        item {
            MypageButtonView(
                onClick = { viewModel.logout() },
                modifier = Modifier.padding(paddingLarge),
                text = stringResource(R.string.auth_logout_button),
                icon = Icons.AutoMirrored.Filled.Logout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(borderMicro, MaterialTheme.colorScheme.onError),
            )
        }

        item {
            MypageButtonView(
                onClick = {
                    viewModel.signOut(uiState.user?.id ?: -1)
                    uiState.reset()
                    viewModel.logout()
                    nav2LoginScreen()
                },
                modifier = Modifier.padding(horizontal = paddingLarge),
                text = stringResource(R.string.auth_signout_button),
                icon = Icons.Default.Delete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(borderMicro, MaterialTheme.colorScheme.onError),
            )
        }

    }

}