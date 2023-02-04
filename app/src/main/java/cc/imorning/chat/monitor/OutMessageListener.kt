package cc.imorning.chat.monitor

import cc.imorning.chat.App
import cc.imorning.chat.action.UserAction
import cc.imorning.chat.action.message.MessageHelper
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.MessageBuilder
import org.joda.time.DateTime
import org.jxmpp.jid.EntityBareJid

class OutMessageListener private constructor() : OutgoingChatMessageListener {

    private val connection = App.getTCPConnection()

    override fun newOutgoingMessage(
        to: EntityBareJid?,
        messageBuilder: MessageBuilder?,
        chat: Chat?
    ) {
        val fromString = to!!.asUnescapedString()
        val nickName = UserAction.getNickName(fromString)
        val dateTime: DateTime = DateTime.now()
        val message = messageBuilder!!.build()
        MessageHelper.insertRecentMessage(fromString, nickName, message, dateTime)
    }

    companion object {
        private var listener: OutMessageListener? = null
            get() {
                if (field == null) {
                    field = OutMessageListener()
                }
                return field
            }

        fun get(): OutMessageListener {
            return listener!!
        }
    }
}