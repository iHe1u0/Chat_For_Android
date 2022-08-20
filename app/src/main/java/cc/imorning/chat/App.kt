package cc.imorning.chat

import android.util.Log
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : CommonApp() {

    private var connectionListener: ChatConnectionListener? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        ChatNotificationManager.manager.setUpNotificationChannels()
        if (connectionListener == null) {
            connectionListener = ChatConnectionListener()
            getTCPConnection().addConnectionListener(connectionListener)
            Log.d(TAG, "getTCPConnection().addConnectionListener")
        }
    }

    companion object {
        private const val TAG = "App"
        fun exitApp(status: Int = 0) {
            CommonApp.exitApp(status)
        }
    }
}