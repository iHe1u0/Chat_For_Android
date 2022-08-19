package cc.imorning.chat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.utils.RingUtils
import cc.imorning.common.CommonApp
import cc.imorning.common.action.message.MessageManager
import cc.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.packet.Message

class MessageMonitorService : Service() {

    private val messageServiceBinder = MessageServiceBinder()
    private val connection = CommonApp.getTCPConnection()

    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onCreate() {
        super.onCreate()
        val offlineMessages: List<Message>
        if (ConnectionManager.isConnectionAuthenticated(connection = connection)) {
            RingUtils.playNewMessage(CommonApp.getContext())
            offlineMessages = MessageManager.getOfflineMessage(connection)
            Log.i(TAG, "message count: ${offlineMessages.size}")
            for (message in offlineMessages) {
                Log.i(TAG, message.body)
            }
        } else {
            Log.w(TAG, "not connect or authenticated")
        }
    }

    companion object {
        private const val TAG = "MessageMonitorService"
    }

    inner class MessageServiceBinder : Binder() {
        fun getService(): MessageMonitorService = this@MessageMonitorService
    }
}