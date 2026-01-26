package com.sesac.common.component

import android.content.Context
import android.os.Trace
import android.view.ViewGroup
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapOptions

object CommonMapView {

    private var mapView: MapView? = null

    fun getMapView(context: Context): MapView {
        Trace.beginSection("CommonMapView.getMapView")
        try {
            if (mapView == null) {
                Trace.beginSection("MapView.create(TextureView)")
                val options = NaverMapOptions()
                    .useTextureView(true)
                mapView = MapView(context.applicationContext, options)
                Trace.endSection()
            }

            // 이미 다른 부모에 붙어있으면 떼어내기 (중요!!)
            val parent = mapView!!.parent
            if (parent is ViewGroup) {
                Trace.beginSection("MapView.removeView")
                parent.removeView(mapView)
                Trace.endSection()
            }

            return mapView!!
        } finally {
            Trace.endSection()
        }
    }

    fun clear() {
        mapView = null
    }
}