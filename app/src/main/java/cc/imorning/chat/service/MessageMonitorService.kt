package cc.imorning.chat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.R
import cc.imorning.chat.model.OnlineMessage
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.action.message.MessageManager
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.MessageHelper
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.filter.MessageTypeFilter
import org.jivesoftware.smack.packet.Message

class MessageMonitorService : Service() {

    private val messageServiceBinder = MessageServiceBinder()
    private val connection = CommonApp.getTCPConnection()

    private lateinit var chatManager: ChatManager

    private var isRunning: Boolean = false

    // database dao for operating database
    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStartCommand: $this")
        }
        intent?.also {
            val action = it.getStringExtra(ACTION_KEY)
            if (null != action && action == STOP) {
                stopSelf()
            }
        }
        if (ConnectionManager.isConnectionAuthenticated(connection = connection)) {
            chatManager = ChatManager.getInstanceFor(connection)
            if (!isRunning) {
                processOfflineMessage()
                addMessageListener()
                isRunning = false
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * listen online message
     */
    private fun addMessageListener() {
        Log.d(TAG, "addMessageListener: ")
        chatManager.addIncomingListener { from, message, chat ->
            val fromJidString = from.asUnescapedString()
            MessageHelper.processMessage(
                from = fromJidString,
                message = message,
                chat = chat
            )
            // then let's make a notification
            // val icon = AvatarUtils.instance.getAvatarPath(jid = from.asUnescapedString())
            val onlineMessage = OnlineMessage(
                from = fromJidString,
                receiver = connection.user.asUnescapedString(),
                message = message.body
            )
            ChatNotificationManager.manager.showNotification(
                message = onlineMessage,
                from = fromJidString,
            )
        }
        connection.addAsyncStanzaListener({ packet ->
            val message = packet as Message
            MessageHelper.processMessage(
                from = message.from.asUnescapedString(),
                message = message
            )
            ChatNotificationManager.manager.showNotification(
                message = OnlineMessage(
                    from = message.from.asUnescapedString(),
                    receiver = connection.user.asUnescapedString(),
                    message = message.body
                ),
                from = message.from.asUnescapedString(),
            )
        }, MessageTypeFilter.HEADLINE)
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
                if (BuildConfig.DEBUG){
                    Log.d(TAG, "offline message >>> ${message.body}")
                }
            }
        } else {
            Log.w(TAG, "not connect or authenticated")
        }
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    companion object {

        const val ACTION_KEY = "key"
        const val START = "start"
        const val STOP = "stop"

        private const val TAG = "MessageMonitorService"
        private const val Notification_New_Message = R.string.app_name
    }

    inner class MessageServiceBinder : Binder() {
        fun getService(): MessageMonitorService = this@MessageMonitorService
    }

}
