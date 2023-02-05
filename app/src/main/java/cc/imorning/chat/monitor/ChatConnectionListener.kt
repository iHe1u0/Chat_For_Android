package cc.imorning.chat.monitor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.*
import cc.imorning.chat.activity.LoginActivity
import cc.imorning.chat.network.ReconnectionWorker
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import org.jivesoftware.smack.*
import org.jivesoftware.smackx.vcardtemp.VCardManager

class ChatConnectionListener : ConnectionListener {

    private val context: Context = CommonApp.getContext()
    private var messageMonitor: Intent? = null

    private lateinit var reconnectionManager: ReconnectionManager

    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        if (messageMonitor == null) {
            messageMonitor = Intent(CommonApp.getContext(), MessageMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(messageMonitor)
            } else {
                context.startService(messageMonitor)
            }
        }
        CommonApp.vCard = VCardManager.getInstanceFor(connection).loadVCard()
        reconnectionManager= ReconnectionManager.getInstanceFor(connection as AbstractXMPPConnection);
        reconnectionManager.enableAutomaticReconnection();
    }

    override fun connectionClosed() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "connection closed")
        }
        reconnectionManager.abortPossiblyRunningReconnection();
        context.stopService(messageMonitor)
        super.connectionClosed()
    }

    /**
     * java.net.SocketException: Software caused connection abort >>> Close cause network error
     * org.jivesoftware.smack.XMPPException$StreamErrorException  >>> Close cause sign in elsewhere
     */
    override fun connectionClosedOnError(e: Exception?) {
        context.stopService(messageMonitor)
        messageMonitor = null
        if (e is XMPPException.StreamErrorException) {
            val loginActivity = Intent(context, LoginActivity::class.java)
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(loginActivity)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "登录过期，请重新登录", Toast.LENGTH_LONG).show()
            }
            reconnectionManager.abortPossiblyRunningReconnection();
        } else {
            reconnect(CommonApp.getContext())
        }
        super.connectionClosedOnError(e)
    }

    private fun reconnect(context: Context) {
        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val reconnectionWorkerRequest: WorkRequest =
            OneTimeWorkRequestBuilder<ReconnectionWorker>()
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context).enqueue(reconnectionWorkerRequest)
    }

    companion object {
        private const val TAG = "ChatConnectionListener"
    }
}