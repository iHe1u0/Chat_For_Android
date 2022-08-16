package cc.imorning.chat

import android.app.Application
import android.content.Context
import cc.imorning.common.BuildConfig
import cc.imorning.common.constant.ServerConfig
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.NetworkUtils
import com.bumptech.glide.Glide
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.coroutines.*
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import kotlin.system.exitProcess


class App : Application() {

    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodCount(10)
            .tag(TAG)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        val configurationBuilder = XMPPTCPConnectionConfiguration.builder()
        configurationBuilder.setHost(ServerConfig.HOST_NAME)
        configurationBuilder.setXmppDomain(ServerConfig.DOMAIN)
        configurationBuilder.setPort(ServerConfig.LOGIN_PORT)
        configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        configurationBuilder.setSendPresence(false)
        xmppTcpConnection = XMPPTCPConnection(configurationBuilder.build())
        if (!ConnectionManager.isConnectionAuthenticated(getTCPConnection())) {
            MainScope().launch(Dispatchers.IO) { getTCPConnection() }
        }
    }

    companion object {
        private const val TAG = "ChatApp_LOG"

        private var application: Application? = null
        private var xmppTcpConnection: XMPPTCPConnection? = null

        var vCard: VCard? = null

        fun getContext(): Context {
            return application!!
        }

        @Synchronized
        fun getTCPConnection(): XMPPTCPConnection {
            xmppTcpConnection!!.apply {
                if (!this.isConnected && NetworkUtils.isNetworkConnected(getContext())) {
                    runBlocking {
                        supervisorScope {
                            val connectJob = async(Dispatchers.IO) {
                                xmppTcpConnection!!.connect()
                            }
                            try {
                                connectJob.await()
                            } catch (throwable: Throwable) {
                                Logger.e("get TCP Connection failed", throwable)
                            }
                        }
                    }
                }
                return this
            }
        }

        fun exitApp() {
            if (ConnectionManager.isConnectionAuthenticated(connection = xmppTcpConnection)) {
                xmppTcpConnection?.disconnect()
            }
            Glide.get(App.getContext()).clearMemory()
            MainScope().launch(Dispatchers.IO) {
                App().appDatabase.userInfoDao().deleteAllContact()
                exitProcess(0)
            }
        }

    }

}