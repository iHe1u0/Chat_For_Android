package cc.imorning.chat.monitor

import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.action.message.MessageHelper
import cc.imorning.database.entity.MessageEntity
import com.google.gson.Gson
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.MessageBuilder
import org.jxmpp.jid.EntityBareJid

class OutMessageListener private constructor() : OutgoingChatMessageListener {

    private val connection = App.getTCPConnection()

    override fun newOutgoingMessage(
        to: EntityBareJid?,
        messageBuilder: MessageBuilder?,
        chat: Chat?
    ) {
        messageBuilder?.build().apply {
            if (this == null) {
                return
            }
            val gson = Gson()
            val message = gson.fromJson(this.body, MessageEntity::class.java)
            val nickName = RosterAction.getNickName(message.receiver)
            with(message) {
                MessageHelper.insertRecentMessage(
                    user = receiver,
                    nickName = nickName,
                    messageBody = messageBody.text,
                    messageType = messageType,
                    dateTime = sendTime
                )
                MessageHelper.insertMessage(this)
            }
        }
    }

    companion object {
        private const val TAG = "OutMessageListener"
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