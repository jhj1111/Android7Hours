package com.sesac.auth.utils

import android.content.Context
import android.util.Log
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

/**
 * Kakao Login 로직을 처리하는 Helper 함수
 */
fun handleKakaoLogin(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.Companion.instance.loginWithKakaoTalk(context) { token, error ->
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