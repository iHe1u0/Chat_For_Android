package cc.imorning.common

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import cc.imorning.common.constant.ServerConfig
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.android.AndroidSmackInitializer
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties
import java.util.*
import kotlin.system.exitProcess

open class CommonApp : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        Throwable().stackTrace[0].apply { Log.i("Fkt", "${className}@${lineNumber}") }
        AndroidUsingLinkProperties.setup(this)
        AndroidSmackInitializer.initialize(this)

        // Security.addProvider(BouncyCastleProvider())
        // KeyStore.getInstance("BKS")

        AppCenter.start(
            application, "28fb209f-c852-48b1-b691-9fa2b06c1762",
            Crashes::class.java
        )
        AppCenter.setLogLevel(Log.ERROR)
        AppCenter.setEnabled(!BuildConfig.DEBUG)

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
        configurationBuilder.setDnssecMode(ConnectionConfiguration.DnssecMode.disabled)
        configurationBuilder.setSendPresence(false)
        configurationBuilder.setKeyManager(null)
        configurationBuilder.setConnectTimeout(10 * 1000)
        xmppTcpConnection = XMPPTCPConnection(configurationBuilder.build())
        ReconnectionManager.getInstanceFor(xmppTcpConnection).enableAutomaticReconnection()
        ReconnectionManager.getInstanceFor(xmppTcpConnection)
            .setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY)
    }

    companion object {
        private const val TAG = "CommonApp"

        private var application: Application? = null
        lateinit var xmppTcpConnection: XMPPTCPConnection

        var vCard: VCard? = null

        fun getContext(): Context {
            return application!!
        }

        fun exitApp(status: Int = 0) {
            if (status != 0) {
                Toast.makeText(getContext(), "Error:{$status}", Toast.LENGTH_LONG).show()
            }
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