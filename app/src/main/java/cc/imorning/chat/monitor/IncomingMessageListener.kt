package cc.imorning.chat.monitor

import cc.imorning.chat.App
import cc.imorning.chat.action.message.MessageHelper
import cc.imorning.chat.model.OnlineMessage
import cc.imorning.chat.utils.ChatNotificationManager
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid

class IncomingMessageListener private constructor() : IncomingChatMessageListener {

    private val connection = App.getTCPConnection()

    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val fromJidString = from?.asUnescapedString()
        if (message != null) {
            MessageHelper.processMessage(
                from = fromJidString,
                message = message,
                chat = chat
            )
        }
        // then let's make a notification
        // val icon = AvatarUtils.instance.getAvatarPath(jid = from.asUnescapedString())
        val onlineMessage = OnlineMessage(
            from = fromJidString,
            receiver = connection.user.asUnescapedString(),
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