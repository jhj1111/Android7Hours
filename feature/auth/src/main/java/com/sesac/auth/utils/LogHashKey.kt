package com.sesac.auth.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

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