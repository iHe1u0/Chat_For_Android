package cc.imorning.chat.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import cc.imorning.chat.R
import cc.imorning.chat.receiver.ReplyReceiver
import cc.imorning.chat.service.NotificationHelper.ID.NEW_MESSAGE
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.action.message.MessageManager
import cc.imorning.common.constant.Config
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

    // database dao for operating database
    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

    override fun onBind(intent: Intent?): IBinder {
        return messageServiceBinder
    }

    override fun onCreate() {
        super.onCreate()
        if (ConnectionManager.isConnectionAuthenticated(connection = connection)) {
            chatManager = ChatManager.getInstanceFor(connection)
            processOfflineMessage()
            addMessageListener()
        }
    }

    /**
     * listen online message
     */
    private fun addMessageListener() {
        chatManager.addIncomingListener { from, message, chat ->

            val jidString = from.asUnescapedString()

            MessageHelper.processMessage(
                from = jidString,
                message = message,
                chat = chat
            )
            // then let's make a notification
            // val icon = AvatarUtils.instance.getAvatarPath(jid = from.asUnescapedString())
            NotificationHelper.notifyNewMessage(
                context = CommonApp.getContext(),
                smallIcon = R.mipmap.ic_launcher,
                id = getString(R.string.new_message),
                title = jidString.split("@")[0],
                content = message.body
            )

        }
        connection.addAsyncStanzaListener({ packet ->
            val message = packet as Message
            MessageHelper.processMessage(
                from = message.from.asUnescapedString(),
                message = message
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
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "message count: ${offlineMessages.size}")
                for (message in offlineMessages) {
                    MessageHelper.processMessage(message = message)
                    // Logger.xml(message.toXML().toString())
                    Log.i(TAG, "message: ${message.body}")
                }
            }
        } else {
            Log.w(TAG, "not connect or authenticated")
        }
    }

    companion object {

        private const val TAG = "MessageMonitorService"

        private const val Notification_New_Message = R.string.app_name

    }

    inner class MessageServiceBinder : Binder() {
        fun getService(): MessageMonitorService = this@MessageMonitorService
    }
}


object NotificationHelper {

    object ID {
        const val NEW_MESSAGE = 1124
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun notifyNewMessage(
        context: Context,
        @DrawableRes smallIcon: Int,
        id: String,
        title: String,
        content: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ): Int {
        createNotificationChannel(
            context = context,
            id = id,
            name = id,
            descriptionText = id
        )
        val remoteInput: RemoteInput = RemoteInput.Builder(Config.Intent.Action.QUICK_REPLY).run {
            setLabel("快速回复")
            build()
        }
        val intent = Intent(context, ReplyReceiver::class.java)
        intent.putExtra(Config.Intent.Action.QUICK_REPLY_TO, title)
        // Build a PendingIntent for the reply action to trigger.
        val replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        // Create the reply action and add the remote input.
        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(smallIcon, "回复", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()
        val builder = NotificationCompat.Builder(context, id)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setPriority(priority)
            .addAction(action)
            .setAutoCancel(false)
        with(NotificationManagerCompat.from(context)) {
            notify(NEW_MESSAGE, builder.build())
        }
        return NEW_MESSAGE
    }

    private fun createNotificationChannel(
        context: Context,
        id: String,
        name: String,
        descriptionText: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}