package com.sesac.auth.presentation.LoginScreen

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.sesac.auth.nav_graph.AuthNavigationRoute
import com.sesac.auth.presentation.AuthViewModel
import com.sesac.common.R
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.ui.theme.paddingExtraLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
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
        Icon(
            painterResource(R.drawable.image7hours),
            contentDescription = "App Logo",
            modifier = Modifier.size(iconSizeLarge),
//            tint = MaterialTheme.colorScheme.primary,
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.height(paddingMedium))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(paddingExtraLarge))

        // 2. 입력 필드
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.loginEmail.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.auth_join_email_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(paddingSmall))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.loginPassword.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.auth_join_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(paddingLarge))

        // 3. 로딩 인디케이터 또는 버튼 영역
        if (uiState is JoinUiState.Loading || isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = paddingLarge))
        } else {
            // 로그인 버튼
            Button(
                onClick = { viewModel.onLoginClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(iconSizeLarge)
            ) {
                Text(
                    text = stringResource(R.string.auth_login_button),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = White,
                )
            }
            Spacer(modifier = Modifier.height(paddingMedium))

            // 카카오 로그인 버튼
            Button(
                onClick = {
                    handleKakaoLogin(context, { accessToken ->
                        viewModel.onKakaoLoginSuccess(accessToken)
                    }) {
                        isLoading = false
                    }
                    isLoading = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(iconSizeLarge),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500))
            ) {
                Text("카카오 로그인", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
            }
        }

        // 4. 에러 메시지
        if (uiState is JoinUiState.Error) {
            Toast.makeText(context, (uiState as JoinUiState.Error).message, Toast.LENGTH_SHORT)
                .show()
        }


        // 5. 회원가입 및 계정 찾기

        Row(
            modifier = Modifier.padding(top = paddingSmall),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.navigate(AuthNavigationRoute.JoinTab) }) {
                Text(stringResource(id = R.string.auth_signup_button))
            }
            Text("|", color = TextSecondary)
            TextButton(onClick = onNavigateToFindAccount) {
                Text(stringResource(R.string.auth_find_id_and_password))
            }
        }
    }
}
/**
 * Kakao Login 로직을 처리하는 Helper 함수
 */
private fun handleKakaoLogin(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "카카오톡으로 로그인 실패", error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onFailure()
                    return@loginWithKakaoTalk
                }
                // 카톡 로그인 실패 시 계정 로그인으로 fallback
                loginWithKakaoAccount(context, onSuccess, onFailure)
            } else if (token != null) {
                Log.i("KakaoLogin", "카카오톡으로 로그인 성공 ${token.accessToken}")
                onSuccess(token.accessToken)
            }
        }
    } else {
        loginWithKakaoAccount(context, onSuccess, onFailure)
    }
}

private fun loginWithKakaoAccount(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
        if (error != null) {
            Log.e("KakaoLogin", "카카오계정으로 로그인 실패", error)
            onFailure()
        } else if (token != null) {
            Log.i("KakaoLogin", "카카오계정으로 로그인 성공 ${token.accessToken}")
            onSuccess(token.accessToken)
        }
    }
}

/**
 * Kakao SDK에 등록할 해시키를 Logcat에 출력합니다.
 */
private fun logHashKey(context: Context) {
    try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNATURES.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
        }
//        val signature: Signature = packageInfo.signatures?.first() ?: Signature()
//        val md = MessageDigest.getInstance("SHA")
//        md.update(signature.toByteArray())
//        Log.d("HashKey", "KeyHash: ${Base64.encodeToString(md.digest(), Base64.DEFAULT)}")
    } catch (e: Exception) {
        Log.e("HashKey", "Hash key retrieval failed", e)
    }
}