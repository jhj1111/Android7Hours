package com.sesac.auth.presentation.login_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Black
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.Yellow100
import com.sesac.common.ui.theme.iconSizeLarge
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium

@Composable
fun LoginButtonsView(
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = paddingLarge))
        } else {
            // 일반 로그인 버튼
            Button(
                onClick = onLoginClick,
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
                onClick = onKakaoLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(iconSizeLarge),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow100)
            ) {
                Text(
                    text = "카카오 로그인",
                    color = Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginButtonsViewPreview() {
    MaterialTheme {
        LoginButtonsView(
            isLoading = false,
            onLoginClick = {},
            onKakaoLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginButtonsViewLodingPreview() {
    MaterialTheme {
        // 로딩 상태 프리뷰
        LoginButtonsView(
            isLoading = true,
            onLoginClick = {},
            onKakaoLoginClick = {}
        )
    }
}
