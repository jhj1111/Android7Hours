package com.sesac.auth.presentation.login_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import com.sesac.auth.nav_graph.AuthNavigationRoute
import com.sesac.auth.presentation.AuthViewModel
import com.sesac.auth.utils.handleKakaoLogin
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.domain.result.JoinUiState

@Composable
fun AuthLoginScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onNavigateToFindAccount: () -> Unit
) {
    val email by remember { viewModel.loginEmail }
    val password by remember { viewModel.loginPassword }
    val uiState by viewModel.joinUiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 카카오 SDK 해시키 로깅 (개발용)
    if (!LocalInspectionMode.current) {
        LaunchedEffect(Unit) {
//            logHashKey(context)
        }
    }

    DisposableEffect(Unit) {
        viewModel.resetUiState()
        onDispose { viewModel.resetUiState() }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is JoinUiState.Success -> onLoginSuccess()
            is JoinUiState.Error -> isLoading = false
            is JoinUiState.Loading -> isLoading = true
            else -> {}
        }
    }

    // 에러 메시지 처리
    LaunchedEffect(uiState) {
        if (uiState is JoinUiState.Error) {
            Toast.makeText(context, (uiState as JoinUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 1. 스크롤 가능하도록 변경
            .imePadding() // 2. 키보드가 올라올 때 화면이 가려지지 않도록 패딩 추가
            .padding(horizontal = paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // 키보드가 없을 때 콘텐츠를 화면 중앙에 배치
    ) {
        // 1. 로고 및 앱 이름
        AuthLoginHeaderView()

        // 2. 입력 필드
        LoginInputFieldsView(
            email = email,
            password = password,
            onEmailChange = { viewModel.loginEmail.value = it },
            onPasswordChange = { viewModel.loginPassword.value = it }
        )

        Spacer(modifier = Modifier.height(paddingLarge))

        // 3. 로그인 버튼 영역
        LoginButtonsView(
            isLoading = uiState is JoinUiState.Loading || isLoading,
            onLoginClick = { viewModel.onLoginClick() },
            onKakaoLoginClick = {
                handleKakaoLogin(context, { accessToken ->
                    viewModel.onKakaoLoginSuccess(accessToken)
                }) {
                    isLoading = false
                }
                isLoading = true
            }
        )

        // 4. 회원가입 및 계정 찾기
        LoginFooterLinksView(
            onSignUpClick = { navController.navigate(AuthNavigationRoute.JoinTab) },
            onFindAccountClick = onNavigateToFindAccount
        )
    }
}