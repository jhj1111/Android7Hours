package com.sesac.common.component

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.naver.maps.map.MapView

class CommonMapLifecycle(
    private val screenTag: String = "MapLifecycle"
) : DefaultLifecycleObserver {

    private var _mapView: MapView? = null
    val mapView: MapView?
        get() = _mapView

    // 🔥 이미 등록된 lifecycle을 추적하여 중복 등록 방지
    private var registeredLifecycle: Lifecycle? = null

    fun setMapView(view: MapView, lifecycle: Lifecycle) {
        _mapView = view

        // 🔥 핵심: 같은 lifecycle에 이미 등록되었으면 무시
        if (registeredLifecycle == lifecycle) {
            Log.d(screenTag, "✅ Already registered with this lifecycle, skipping")
            return
        }

        // 이전 lifecycle에서 제거
        registeredLifecycle?.removeObserver(this)
        Log.d(screenTag, "🧹 Removed observer from previous lifecycle")

        // 새 lifecycle에 등록
        registeredLifecycle = lifecycle
        lifecycle.addObserver(this)
        Log.d(screenTag, "✅ MapView set and registered to lifecycle")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(screenTag, "🟢 onResume - mapView?.onResume()")
        _mapView?.onResume()
        _mapView?.postInvalidate()
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(screenTag, "🟡 onPause - mapView?.onPause()")
        _mapView?.onPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(screenTag, "💀 onDestroy - cleanup")
        owner.lifecycle.removeObserver(this)
        registeredLifecycle = null
        // MapView 자체는 유지 (재사용)
        _mapView = null
    }

    // 화면 이동 시 cleanup용 함수
    fun detachMapView() {
        Log.d(screenTag, "🧹 detachMapView called")
        registeredLifecycle?.removeObserver(this)
        registeredLifecycle = null
        _mapView = null
    }
}