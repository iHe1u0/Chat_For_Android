package cc.imorning.chat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.action.message.MessageHelper
import cc.imorning.chat.action.message.MessageManager
import cc.imorning.chat.monitor.ChatStanzaListener
import cc.imorning.chat.monitor.IncomingMessageListener
import cc.imorning.chat.monitor.RosterListener
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.database.AppDatabase
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.MessageTypeFilter
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import kotlin.system.exitProcess

class MessageMonitorService : Service() {

    private val messageServiceBinder = MessageServiceBinder()
    private val connection = App.getTCPConnection()

    private var isRunning: Boolean = false

    private lateinit var chatManager: ChatManager
    private lateinit var incomingMessageListener: IncomingChatMessageListener
    private lateinit var chatStanzaListener: ChatStanzaListener

    // database dao for operating database
    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

    private val chatNotificationManager = ChatNotificationManager.manager

    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            chatNotificationManager.setUpAppRunningNotificationChannels()
            startForeground(
                ChatNotificationManager.CHANNEL_APP_RUNNING_ID,
                chatNotificationManager.showAppRunningNotification()
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!connection.isAuthenticated) {
            exitProcess(0)
        }
        processOfflineMessage()
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            addMessageListener()
            addRosterPresenceChangeListener()
            isRunning = true
        } else {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * roster status listener
     */
    private fun addRosterPresenceChangeListener() {
        val filter = AndFilter(
            StanzaTypeFilter(
                Presence::class.java
            )
        )
        connection.addStanzaListener(RosterListener.rosterListener, filter)
    }

    /**
     * message listener
     */
    private fun addMessageListener() {
        chatManager = ChatManager.getInstanceFor(connection)
        incomingMessageListener = IncomingMessageListener.get()
        chatStanzaListener = ChatStanzaListener.get()
        connection.addAsyncStanzaListener(
            chatStanzaListener,
            MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE
        )
        chatManager.addIncomingListener(incomingMessageListener)
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
            }
        }
    }

    override fun onDestroy() {
        if (isRunning) {
            chatManager.removeIncomingListener(incomingMessageListener)
            connection.removeStanzaListener(chatStanzaListener)
        }
        chatNotificationManager.cancelNotification(ChatNotificationManager.CHANNEL_APP_RUNNING_ID)
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
