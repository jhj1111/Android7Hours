package com.sesac.auth.utils

import android.content.Context
import android.util.Log
import com.kakao.sdk.user.UserApiClient

fun loginWithKakaoAccount(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    UserApiClient.Companion.instance.loginWithKakaoAccount(context) { token, error ->
        if (error != null) {
            Log.e("KakaoLogin", "카카오계정으로 로그인 실패", error)
            onFailure()
        } else if (token != null) {
            Log.i("KakaoLogin", "카카오계정으로 로그인 성공 ${token.accessToken}")
            onSuccess(token.accessToken)
        }
    }
}