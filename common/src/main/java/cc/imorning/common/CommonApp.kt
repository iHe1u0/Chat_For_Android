package cc.imorning.common

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import cc.imorning.common.constant.ServerConfig
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
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
import kotlin.system.exitProcess


open class CommonApp : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this

        AppCenter.start(
            application,
            "28fb209f-c852-48b1-b691-9fa2b06c1762",
            Analytics::class.java,
            Crashes::class.java
        )
        AppCenter.setLogLevel(Log.WARN)
        AppCenter.setEnabled(!Build.FINGERPRINT.contains(other = "generic", ignoreCase = true))

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodCount(10)
            .tag(TAG)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG &&
                        Build.FINGERPRINT.contains(other = "generic", ignoreCase = true)
            }
        })

        val configurationBuilder = XMPPTCPConnectionConfiguration.builder()
        configurationBuilder.setHost(ServerConfig.HOST_NAME)
        configurationBuilder.setXmppDomain(ServerConfig.DOMAIN)
        configurationBuilder.setPort(ServerConfig.LOGIN_PORT)
        configurationBuilder.setResource(ServerConfig.RESOURCE)
        configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        configurationBuilder.setDnssecMode(ConnectionConfiguration.DnssecMode.disabled)
        configurationBuilder.setSendPresence(false)
        configurationBuilder.setKeyManager(null)
        xmppTcpConnection = XMPPTCPConnection(configurationBuilder.build())

    }

    companion object {
        private const val TAG = "CommonApp"

        private var application: Application? = null
        var xmppTcpConnection: XMPPTCPConnection? = null

        var vCard: VCard? = null

        fun getContext(): Context {
            return application!!
        }

        fun exitApp(status: Int = 0) {
            val notificationManager =
                getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            MainScope().launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    ActivityCollector.finishAll()
                }
                exitProcess(status)
            }
        }
    }
}

object ActivityCollector {

    var currentActivity: String = ""

    private var activities = LinkedList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
        currentActivity = activity.localClassName
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
        currentActivity = activity.localClassName
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }
}