package cc.imorning.chat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.R
import cc.imorning.chat.monitor.ChatStanzaListener
import cc.imorning.chat.monitor.IncomingMessageListener
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.action.message.MessageManager
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.MessageHelper
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.filter.MessageTypeFilter
import org.jivesoftware.smack.packet.Message

class MessageMonitorService : Service() {

    private val messageServiceBinder = MessageServiceBinder()
    private val connection = CommonApp.getTCPConnection()

    private var isRunning: Boolean = false

    private lateinit var chatManager: ChatManager
    private lateinit var incomingMessageListener: IncomingChatMessageListener
    private lateinit var chatStanzaListener: ChatStanzaListener

    // database dao for operating database
    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processOfflineMessage()
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            chatManager = ChatManager.getInstanceFor(connection)
            incomingMessageListener = IncomingMessageListener.get()
            chatStanzaListener = ChatStanzaListener.get()
            connection.addAsyncStanzaListener(chatStanzaListener, MessageTypeFilter.HEADLINE)
            chatManager.addIncomingListener(incomingMessageListener)
            isRunning = true
        } else {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * process offline message
     */
    private fun processOfflineMessage() {
        val offlineMessages: List<Message>
        if (ConnectionManager.isConnectionAuthenticated(connection = connection)) {
            offlineMessages = MessageManager.getOfflineMessage()
            for (message in offlineMessages) {
                MessageHelper.processMessage(message = message)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "offline message >>> ${message.body}")
                }
            }
        } else {
            Log.w(TAG, "not connect or authenticated")
        }
    }

    override fun onDestroy() {
        if (isRunning) {
            chatManager.removeIncomingListener(incomingMessageListener)
            connection.removeStanzaListener(chatStanzaListener)
        }
        super.onDestroy()
    }

    companion object {

        private const val TAG = "MessageMonitorService"
        private const val Notification_New_Message = R.string.app_name
    }

    inner class MessageServiceBinder : Binder() {
        fun getService(): MessageMonitorService = this@MessageMonitorService
    }

}
