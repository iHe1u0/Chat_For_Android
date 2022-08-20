package cc.imorning.chat.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import cc.imorning.chat.R
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.model.OnlineMessage
import cc.imorning.chat.receiver.ReplyReceiver
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config

class ChatNotificationManager private constructor(val context: Context) {

    companion object {

        const val CHANNEL_NEW_MESSAGES_ID = 1124
        private const val CHANNEL_NEW_MESSAGES = "new_message"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2

        val manager: ChatNotificationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ChatNotificationManager(CommonApp.getContext())
        }
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalArgumentException()

    fun setUpNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) != null) {
            return
        }
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_NEW_MESSAGES,
                "消息通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "新消息通知"
            }
        )
    }

    /**
     * show a notification, if exist then update
     *
     * @param update if exist
     */
    @WorkerThread
    fun showNotification(
        message: OnlineMessage,
        from: String,
        update: Boolean = false
    ) {
        val user =
            Person.Builder().setName(CommonApp.getTCPConnection().user.asEntityBareJid().toString())
                .build()
        val icon = IconCompat.createWithResource(context, R.drawable.ic_default_avatar)
        val person = Person.Builder().setName(from).setIcon(icon).build()
        val contentUrl = "imorningchat://app/chat/$from".toUri()

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_BUBBLE,
            Intent(context, ChatActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUrl), getNotificationFlag(mutable = true)
        )

        val messageStyle = NotificationCompat.MessagingStyle(user)

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
            .setContentText("${message.message}")
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
        notificationManager.notify(0, builder.build())
    }

    private fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
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
}