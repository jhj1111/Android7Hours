package com.sesac.common.component

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.naver.maps.map.MapView

object CommonMapView {
    private var mapView: MapView? = null
    private var currentAttachedScreenTag: String? = null

    fun getMapView(context: Context, screenTag: String = "Unknown"): MapView {
        Log.d("CommonMapView", "📍 getMapView from: $screenTag (currently attached: $currentAttachedScreenTag)")

        if (mapView == null) {
            Log.d("CommonMapView", "🆕 Creating new MapView")
            mapView = MapView(context)
        }

        val view = mapView!!

        // 🔥 핵심: 다른 화면이 이미 붙어있으면 제거
        if (currentAttachedScreenTag != null && currentAttachedScreenTag != screenTag) {
            Log.d("CommonMapView", "🧹 Detaching from previous screen: $currentAttachedScreenTag")
            val parent = view.parent
            if (parent is ViewGroup) {
                parent.removeView(view)
            }
        }

        currentAttachedScreenTag = screenTag
        Log.d("CommonMapView", "✅ MapView attached to: $screenTag")
        return view
    }

    fun detachMapView(screenTag: String) {
        if (currentAttachedScreenTag == screenTag) {
            Log.d("CommonMapView", "🧹 Detaching from screen: $screenTag")
            val parent = mapView?.parent
            if (parent is ViewGroup) {
                parent.removeView(mapView)
            }
            currentAttachedScreenTag = null
            Log.d("CommonMapView", "✅ MapView detached from: $screenTag")
        } else {
            Log.d("CommonMapView", "⚠️ Detach called from $screenTag but attached to $currentAttachedScreenTag (ignoring)")
        }
    }

    fun clear() {
        Log.d("CommonMapView", "🧹 Clearing MapView completely")
        mapView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            it.onDestroy()
        }
        mapView = null
        currentAttachedScreenTag = null
    }
}