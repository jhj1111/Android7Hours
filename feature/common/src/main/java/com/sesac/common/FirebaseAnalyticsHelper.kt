package com.sesac.common

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

object FirebaseAnalyticsHelper {

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize(analytics: FirebaseAnalytics) {
        firebaseAnalytics = analytics
    }

    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics?.logEvent(eventName, params)
    }

    fun logScreenView(screenName: String, screenClass: String? = null) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    // You can add more specific logging methods here, e.g., for performance
    fun logPerformanceEvent(featureName: String, metricName: String, value: Long, unit: String) {
        val params = Bundle().apply {
            putString("feature_name", featureName)
            putString("metric_name", metricName)
            putLong("value", value)
            putString("unit", unit)
        }
        firebaseAnalytics?.logEvent("performance_metric", params)
    }
}
