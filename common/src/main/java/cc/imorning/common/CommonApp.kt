package cc.imorning.common

import android.app.Activity
import android.app.Application
import android.content.Context
import cc.imorning.common.constant.ServerConfig
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.NetworkUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import java.util.*


open class CommonApp : Application() {

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
        configurationBuilder.setResource(ServerConfig.RESOURCE)
        configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        configurationBuilder.setSendPresence(false)
        // if (BuildConfig.DEBUG){
        //     configurationBuilder.enableDefaultDebugger()
        // }
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
//                    runBlocking(Dispatchers.IO) {
//                        supervisorScope {
//                            val connectJob = async(Dispatchers.IO) {
//                                xmppTcpConnection!!.connect()
//                            }
//                            try {
//                                connectJob.await()
//                            } catch (throwable: Throwable) {
//                                Logger.e("get TCP Connection failed", throwable)
//                            }
//                        }
//                    }
                    ConnectionManager.connect(xmppTcpConnection!!)
                }
                return this
            }
        }

        fun exitApp() {
            if (ConnectionManager.isConnectionAuthenticated(connection = xmppTcpConnection)) {
                xmppTcpConnection?.disconnect()
            }
            MainScope().launch(Dispatchers.IO) {
                CommonApp().appDatabase.appDatabaseDao().deleteAllContact()
                withContext(Dispatchers.Main) {
                    ActivityCollector.finishAll()
                }
            }
        }
    }
}

object ActivityCollector {

    lateinit var currentActivity: String

    var activities = LinkedList<Activity>()
    fun addActivity(activity: Activity) {
        activities.add(activity)
        currentActivity = activity.localClassName
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }
}