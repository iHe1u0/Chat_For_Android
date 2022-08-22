package cc.imorning.chat.monitor

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cc.imorning.common.ActivityCollector

class ActivityMonitor private constructor() : Application.ActivityLifecycleCallbacks {

    private var activityCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        ActivityCollector.addActivity(activity)
        Runtime.getRuntime().gc()
        activityCount++
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        ActivityCollector.removeActivity(activity = activity)
    }

    fun isForeground(): Boolean {
        return activityCount != 0
    }

    companion object {

        private const val TAG = "ActivityMonitor"

        val monitor: ActivityMonitor by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityMonitor()
        }

    }
}