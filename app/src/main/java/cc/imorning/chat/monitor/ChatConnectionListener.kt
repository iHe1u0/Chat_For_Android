package cc.imorning.chat.monitor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.*
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.connection.ReconnectionWorker
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smackx.vcardtemp.VCardManager

class ChatConnectionListener : ConnectionListener {

    private val context: Context = CommonApp.getContext()
    private var messageMonitor: Intent? = null

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
        super.authenticated(connection, resumed)
    }

    override fun connectionClosed() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "connection closed")
        }
        context.stopService(messageMonitor)
        super.connectionClosed()
    }

    override fun connectionClosedOnError(e: Exception?) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "connection closed with error: ${e?.localizedMessage}")
        }
        context.stopService(messageMonitor)
        messageMonitor = null
        reconnect(CommonApp.getContext())
        super.connectionClosedOnError(e)
    }

    private fun reconnect(context: Context) {
        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(true)
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