package com.sesac.common.component

import android.os.Trace
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.naver.maps.map.MapView

class CommonMapLifecycle(
    lifecycle: Lifecycle
) : DefaultLifecycleObserver {
    private var _mapView: MapView? = null
    val mapView: MapView?
        get() = _mapView

    init {
        lifecycle.addObserver(this)
    }
    fun setMapView(view: MapView) {
        _mapView = view
    }
    override fun onCreate(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onCreate")
        _mapView?.onCreate(null)
        Trace.endSection()
    }

    override fun onStart(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onStart")
        _mapView?.onStart()
        Trace.endSection()
    }

    override fun onResume(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onResume")
        _mapView?.onResume()
        Trace.endSection()
    }

    override fun onPause(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onPause")
        _mapView?.onPause()
        Trace.endSection()
    }

    override fun onStop(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onStop")
        _mapView?.onStop()
        Trace.endSection()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Trace.beginSection("MapLifecycle.onDestroy")
        _mapView?.onDestroy()
        CommonMapView.clear()
        Trace.endSection()
    }
}
