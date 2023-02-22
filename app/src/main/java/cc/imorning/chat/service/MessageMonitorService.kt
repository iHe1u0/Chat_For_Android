package cc.imorning.chat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.R
import cc.imorning.chat.action.message.MessageHelper
import cc.imorning.chat.action.message.MessageManager
import cc.imorning.chat.file.ChatFileTransferListener
import cc.imorning.chat.monitor.ChatStanzaListener
import cc.imorning.chat.monitor.IncomingMessageListener
import cc.imorning.chat.monitor.OutMessageListener
import cc.imorning.chat.monitor.RosterListener
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.MessageTypeFilter
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.filetransfer.FileTransferManager

class MessageMonitorService : Service() {

    private val messageServiceBinder = MessageServiceBinder()
    private val connection = App.getTCPConnection()

    private var isRunning: Boolean = false

    private lateinit var chatManager: ChatManager
    private lateinit var incomingMessageListener: IncomingChatMessageListener
    private lateinit var outMessageListener: OutMessageListener
    private lateinit var chatStanzaListener: ChatStanzaListener

    private val chatNotificationManager = ChatNotificationManager.manager

    private lateinit var fileTransferListener: ChatFileTransferListener
    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            chatNotificationManager.setUpAppRunningNotificationChannels()
            startForeground(
                ChatNotificationManager.CHANNEL_APP_RUNNING_ID,
                chatNotificationManager.buildAppRunningNotification()
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processOfflineMessage()
        if (ConnectionManager.isConnectionAvailable(connection)) {
            addMessageListener()
            addRosterPresenceChangeListener()
            addFileListener()
            isRunning = true
        } else {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun addFileListener() {
        fileTransferListener = ChatFileTransferListener()
        FileTransferManager.getInstanceFor(connection)
            .addFileTransferListener(fileTransferListener)
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
        outMessageListener = OutMessageListener.get()
        chatStanzaListener = ChatStanzaListener.get()
        connection.addAsyncStanzaListener(
            chatStanzaListener,
            MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE
        )
        chatManager.addIncomingListener(incomingMessageListener)
        chatManager.addOutgoingListener(outMessageListener)
    }

    /**
     * process offline message
     */
    private fun processOfflineMessage() {
        if (ConnectionManager.isConnectionAvailable(connection = connection)) {
            val offlineMessages: List<Message> = MessageManager.getOfflineMessage()
            val gson = Gson()
            for (message in offlineMessages) {
                if (message.type == Message.Type.chat) {
                    var messageEntity = gson.fromJson(message.body, MessageEntity::class.java)
                    if (messageEntity == null) {
                        messageEntity = MessageEntity(
                            sender = message.from.toString(),
                            receiver = connection.user.asEntityBareJidString(),
                            messageBody = MessageBody(text = message.body)
                        )
                    }
                    MessageHelper.processMessage(messageEntity = messageEntity)
                } else {
                    if (BuildConfig.DEBUG) {
                        Logger.xml(message.toXML().toString())
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (isRunning) {
            chatManager.removeIncomingListener(incomingMessageListener)
            chatManager.removeOutgoingListener(outMessageListener)
            connection.removeStanzaListener(chatStanzaListener)
            FileTransferManager.getInstanceFor(connection)
                .removeFileTransferListener(fileTransferListener)
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
