package cc.imorning.chat

import cc.imorning.chat.monitor.ActivityMonitor
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import coil.ImageLoader
import coil.ImageLoaderFactory

class App : CommonApp(), ImageLoaderFactory {

    private var connectionListener: ChatConnectionListener? = null

    override fun onCreate() {
        super.onCreate()

        // Setup notification
        ChatNotificationManager.manager.setUpNewMessageNotificationChannels()

        if (connectionListener == null) {
            connectionListener = ChatConnectionListener()
            getTCPConnection().addConnectionListener(connectionListener)
        }

        // Register activity monitor
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
            .build()
    }
}