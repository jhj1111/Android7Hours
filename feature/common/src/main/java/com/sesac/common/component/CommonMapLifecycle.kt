package com.sesac.common.component

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.naver.maps.map.MapView

/**
 * MapView의 생명주기(onResume, onPause 등)를 관리하는 LifecycleObserver.
 * 이 클래스는 MapView 인스턴스를 소유하거나 파괴하지 않으며, 오직 생명주기 이벤트만 전달합니다.
 * 싱글턴으로 관리되는 CommonMapView와 함께 사용하는 것을 전제로 합니다.
 */
class CommonMapLifecycle(
    private val screenTag: String
) : DefaultLifecycleObserver {

    private var _mapView: MapView? = null
    private var currentLifecycle: Lifecycle? = null

    /**
     * MapView와 Lifecycle을 이 Observer에 연결합니다.
     */
    fun onStart(mapView: MapView, lifecycle: Lifecycle) {
        Log.d("[$screenTag-Lifecycle]", "onStart: linking MapView and Lifecycle.")
        this._mapView = mapView

        // 다른 Lifecycle에 등록되어 있었다면 제거
        if (currentLifecycle != lifecycle) {
            currentLifecycle?.removeObserver(this)
        }

        // 새 Lifecycle에 등록
        currentLifecycle = lifecycle
        lifecycle.addObserver(this)
    }

    /**
     * Composable이 dispose될 때 호출됩니다.
     * Lifecycle observer를 제거하여 메모리 누수를 방지합니다.
     */
    fun onDispose() {
        Log.d("[$screenTag-Lifecycle]", "onDispose: unregistering lifecycle observer.")
        currentLifecycle?.removeObserver(this)
        currentLifecycle = null
        _mapView = null
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d("[$screenTag-Lifecycle]", "🟢 onResume")
        _mapView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d("[$screenTag-Lifecycle]", "🟡 onPause")
        _mapView?.onPause()
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d("[$screenTag-Lifecycle]", "▶️ onStart")
        _mapView?.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("[$screenTag-Lifecycle]", "🛑 onStop")
        _mapView?.onStop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("[$screenTag-Lifecycle]", "💀 onDestroy: unregistering observer.")
        // MapView 자체를 파괴하지 않고, observer 등록만 해제합니다.
        owner.lifecycle.removeObserver(this)
        if (currentLifecycle == owner.lifecycle) {
            currentLifecycle = null
        }
    }
}