package cc.imorning.chat.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.activity.MainActivity
import cc.imorning.chat.monitor.ActivityMonitor
import cc.imorning.chat.receiver.ReplyReceiver
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config
import cc.imorning.database.entity.MessageEntity

class ChatNotificationManager private constructor(val context: Context) {

    companion object {

        const val CHANNEL_NEW_MESSAGES_ID = 1124
        private const val CHANNEL_NEW_MESSAGES = "$CHANNEL_NEW_MESSAGES_ID"

        const val CHANNEL_APP_RUNNING_ID = 1809
        private const val CHANNEL_APP_RUNNING = "$CHANNEL_APP_RUNNING_ID"

        private const val REQUEST_CONTENT = 1
        private const val PENDING_INTENT_REQUEST_CODE = 2

        val manager: ChatNotificationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ChatNotificationManager(CommonApp.getContext())
        }
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalArgumentException()

    @TargetApi(Build.VERSION_CODES.O)
    fun setUpNewMessageNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) != null) {
            return
        }
        val notificationChannel = NotificationChannel(
            CHANNEL_NEW_MESSAGES,
            "消息通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "新消息通知"
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpAppRunningNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_APP_RUNNING) != null) {
            return
        }
        val notificationChannel = NotificationChannel(
            CHANNEL_APP_RUNNING,
            context.getString(R.string.app_is_running),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = context.getString(R.string.app_is_running)
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAllowBubbles(false)
            }
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    /**
     * show a notification, if exist then update
     *
     * @param update if exist
     */
    @WorkerThread
    fun showNotification(
        message: MessageEntity,
        from: String? = "unknown sender",
        update: Boolean = false
    ) {
        val user =
            Person.Builder()
                .setName(App.getTCPConnection().user.asEntityBareJid().toString())
                .build()
        val icon = IconCompat.createWithResource(context, R.drawable.ic_default_avatar)
        val person = Person.Builder().setName(from).setIcon(icon).build()
        val contentUrl = "imorningchat://app/chat?chatJid=$from".toUri()
        val pendingIntent = PendingIntent.getActivity(
            context, PENDING_INTENT_REQUEST_CODE,
            Intent(context, ChatActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUrl),
            getNotificationFlag(mutable = true)
        )

        val messageStyle = NotificationCompat.MessagingStyle(user)

        val m = NotificationCompat.MessagingStyle.Message(
            message.messageBody.text,
            System.currentTimeMillis(),
            if (message.sender.isNotBlank()) person else null
        ).apply {

        }
        messageStyle.addMessage(m)

        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGES)
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(pendingIntent, icon)
                    .setDesiredHeight(400)
                    .apply {

                    }
                    .build()
            )
            .setContentTitle(from)
            .setSmallIcon(R.drawable.ic_message)
            .setContentText(message.messageBody.text)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .addPerson(person)
            .setShowWhen(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CONTENT,
                    Intent(context, ChatActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUrl), getNotificationFlag(mutable = true)
                )
            )
            // for reply
            .addAction(
                NotificationCompat.Action
                    .Builder(
                        IconCompat.createWithResource(context, R.drawable.ic_send),
                        "回复",
                        PendingIntent.getBroadcast(
                            context,
                            REQUEST_CONTENT,
                            Intent(
                                context,
                                ReplyReceiver::class.java
                            ).putExtra(Config.Intent.Action.QUICK_REPLY_TO, from),
                            getNotificationFlag(mutable = true)
                        )
                    )
                    .addRemoteInput(
                        RemoteInput.Builder(Config.Intent.Action.QUICK_REPLY).run {
                            setLabel("快速回复")
                            build()
                        }
                    )
                    .setAllowGeneratedReplies(true)
                    .build()
            )
        if (update) {
            builder.setOnlyAlertOnce(true)
        }
        if (!ActivityMonitor.monitor.isForeground()) {
            notificationManager.notify(CHANNEL_NEW_MESSAGES_ID, builder.build())
        }
    }

    /**
     * @return notification flag by sdk version
     */
    private fun getNotificationFlag(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_CANCEL_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    /**
     * build an app running notification for Android O+
     */
    fun buildAppRunningNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_APP_RUNNING)
            .setContentTitle(
                context.getString(R.string.app_name)
                    .plus(context.getString(R.string.app_is_running))
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setShowWhen(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, REQUEST_CONTENT, Intent(context, MainActivity::class.java),
                    getNotificationFlag(mutable = true)
                )
            ).build()
    }

    /**
     * cancel notification by id
     */
    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    /**
     * cancel all notification
     */
    fun cancelAll() {
        notificationManager.cancelAll()
    }
}