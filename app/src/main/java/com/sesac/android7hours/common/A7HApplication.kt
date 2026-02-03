package com.sesac.android7hours.common

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kakao.sdk.common.KakaoSdk
import com.sesac.android7hours.R // app 모듈의 R 클래스
import com.sesac.android7hours.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import com.sesac.common.FirebaseAnalyticsHelper // Import the helper

//Default Memory Size = 0.15 ~ 0.2
const val COIL_MEMORY_CACHE_SIZE_PERCENT = 0.3

//Coil Disk Cache Size Setting
//30~100
const val COIL_DISK_CACHE_DIR_NAME = "coil_file_cache"
const val COIL_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 100

@HiltAndroidApp
class A7HApplication(): Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        val appStartTime = System.currentTimeMillis()

        // ✅ ThreeTenABP 초기화 (LocalDate 등 사용 가능하게 함)
        AndroidThreeTen.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        // Initialize Firebase Analytics
        FirebaseAnalyticsHelper.initialize(Firebase.analytics)

        // ✅ 기존 전역 참조 유지
        a7HApp = this
    }
    companion object{
        private lateinit var a7HApp: A7HApplication
        fun getA7HApplication() = a7HApp
    }

    // Coil 세팅
    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        // 메모리 세팅
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, COIL_MEMORY_CACHE_SIZE_PERCENT)
                .build()
        }
        // 가상메모리 세팅
        .diskCache {
            DiskCache.Builder()
                .directory(filesDir.resolve(COIL_DISK_CACHE_DIR_NAME))
                .maximumMaxSizeBytes(COIL_DISK_CACHE_MAX_SIZE.toLong())
                .build()
        }.build()

}