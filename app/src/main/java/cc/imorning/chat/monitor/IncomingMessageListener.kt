package cc.imorning.chat.monitor

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.action.message.MessageHelper
import cc.imorning.chat.model.OnlineMessage
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.database.entity.MessageEntity
import com.google.gson.Gson
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid

class IncomingMessageListener private constructor() : IncomingChatMessageListener {

    private val connection = App.getTCPConnection()

    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        Log.d(
            TAG, "from: $from \n" +
                    "message: ${message?.body} \n" +
                    "chat: ${chat?.xmppAddressOfChatPartner}"
        )
        val fromJidString = from?.asEntityBareJidString()
        if (message != null) {
            val messageEntity =
                Gson().fromJson(message.body, MessageEntity::class.java)
            MessageHelper.processMessage(
                messageEntity = messageEntity,
                chat = chat
            )
        }
        // then let's make a notification
        // val icon = AvatarUtils.instance.getAvatarPath(jid = from.asEntityBareJidString())
        val onlineMessage = OnlineMessage(
            from = fromJidString,
            receiver = connection.user.asEntityBareJidString(),
            message = message?.body
        )
        ChatNotificationManager.manager.showNotification(
            message = onlineMessage,
            from = fromJidString,
        )
    }

    companion object {
        private const val TAG = "IncomingMessageListener"

        private var listener: IncomingMessageListener? = null
            get() {
                if (field == null) {
                    field = IncomingMessageListener()
                }
                return field
            }

        fun get(): IncomingMessageListener {
            return listener!!
        }
    }
}