package cc.imorning.chat

import android.content.Intent
import cc.imorning.chat.monitor.ActivityMonitor
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.FileUtils
import cc.imorning.common.utils.NetworkUtils
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

        if (!ConnectionManager.isConnectionAuthenticated(getTCPConnection())) {
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
         * try to connect server
         */
        @Synchronized
        fun getTCPConnection(): XMPPTCPConnection {
            xmppTcpConnection!!.apply {
                if (!this.isConnected && NetworkUtils.isNetworkConnected(getContext())) {
                    ConnectionManager.connect(xmppTcpConnection!!)
                }
                return this
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .build()
    }

}