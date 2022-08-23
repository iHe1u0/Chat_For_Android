package cc.imorning.chat

import android.util.Log
import cc.imorning.chat.monitor.ActivityMonitor
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : CommonApp(), ImageLoaderFactory {

    private var connectionListener: ChatConnectionListener? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        ChatNotificationManager.manager.setUpNewMessageNotificationChannels()
        if (connectionListener == null) {
            connectionListener = ChatConnectionListener()
            getTCPConnection().addConnectionListener(connectionListener)
            Log.d(TAG, "getTCPConnection().addConnectionListener")
        }
        registerActivityLifecycleCallbacks(ActivityMonitor.monitor)
    }

    companion object {
        private const val TAG = "App"
        fun exitApp(status: Int = 0) {
            CommonApp.exitApp(status)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }
}