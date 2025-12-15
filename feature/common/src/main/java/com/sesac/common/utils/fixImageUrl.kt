package com.sesac.common.utils

import android.util.Log
import com.sesac.common.BuildConfig

fun fixImageUrl(url: String?): String? {
    if (url.isNullOrEmpty()) return null

    var serverIp = BuildConfig.SERVER_URL
    serverIp = serverIp.replace("http://", "")
    serverIp = serverIp.replace(":8000/", "")
    Log.d("TAG-fixImageUrl", "serverIp : $serverIp")
    Log.d("TAG-fixImageUrl", "url : $url")

    return url.replace(Regex("(http://|https://)(127\\.0\\.0\\.1|localhost|minio)"), "$1$serverIp")
}