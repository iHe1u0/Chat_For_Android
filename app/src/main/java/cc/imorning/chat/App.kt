package cc.imorning.chat

import android.content.Intent
import cc.imorning.chat.monitor.ActivityMonitor
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.FileUtils
import coil.ImageLoader
import coil.ImageLoaderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class App : CommonApp(), ImageLoaderFactory {

    private var connectionListener: ChatConnectionListener? = null

    override fun onCreate() {
        super.onCreate()
        // Setup notification
        ChatNotificationManager.manager.setUpNewMessageNotificationChannels()

        if (!ConnectionManager.isConnectionAvailable(getTCPConnection())) {
            MainScope().launch(Dispatchers.IO) { getTCPConnection() }
        }

        if (connectionListener == null) {
            connectionListener = ChatConnectionListener()
            getTCPConnection().addConnectionListener(connectionListener)
        }

        // Register activity monitor
        registerActivityLifecycleCallbacks(ActivityMonitor.monitor)
    }

    companion object {
        private const val TAG = "App"
        var user: String = ""
        fun exitApp(status: Int = 0) {
            FileUtils.instance.cleanCache()
            getContext().stopService(
                Intent(
                    getContext(),
                    MessageMonitorService::class.java
                )
            )
            ConnectionManager.disconnect()
            CommonApp.exitApp(status)
        }

        /**
         * return tcp connection
         */
        fun getTCPConnection(): XMPPTCPConnection {
            xmppTcpConnection.replyTimeout = 10 * 1000
            return xmppTcpConnection
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .error(R.drawable.ic_default_avatar)
            .bitmapFactoryMaxParallelism(6)
            .build()
    }

}