package cc.imorning.chat.monitor

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.action.message.MessageHelper
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
        val fromJidString = from?.asEntityBareJidString()
        if (message != null) {
            if ((message.type == Message.Type.chat) || (message.type == Message.Type.groupchat)) {
                val messageEntity =
                    Gson().fromJson(message.body, MessageEntity::class.java)
                MessageHelper.processMessage(
                    messageEntity = messageEntity,
                    chat = chat
                )
                // then let's make a notification
                // val icon = AvatarUtils.instance.getAvatarPath(jid = from.asEntityBareJidString())
                ChatNotificationManager.manager.showNotification(
                    message = messageEntity,
                    from = fromJidString,
                )
            } else {
                Log.w(TAG, "didn't know how to handle message:${message.toXML()}")
            }
        }
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