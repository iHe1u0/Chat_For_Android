package cc.imorning.chat.monitor

import android.util.Log
import cc.imorning.chat.model.OnlineMessage
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.MessageHelper
import com.orhanobut.logger.Logger
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza

class ChatStanzaListener private constructor() : StanzaListener {

    private val connection = CommonApp.getTCPConnection()

    /**
     * Process the next stanza sent to this stanza listener.
     *
     * If this listener is synchronous, then a single thread is responsible for invoking all listeners, so
     * it's very important that implementations of this method not block
     * for any extended period of time.
     *
     */
    override fun processStanza(packet: Stanza?) {
        if (packet == null) {
            return
        }
        val message = packet as Message
        MessageHelper.processMessage(
            from = message.from.asUnescapedString(),
            message = message
        )
        ChatNotificationManager.manager.showNotification(
            message = OnlineMessage(
                from = message.from.asUnescapedString(),
                receiver = connection.user.asUnescapedString(),
                message = message.body
            ),
            from = message.from.asUnescapedString(),
        )
    }

    companion object {
        private const val TAG = "ChatStanzaListener"
        private var listener: ChatStanzaListener? = null
            get() {
                if (field == null) {
                    field = ChatStanzaListener()
                }
                return field
            }
        fun get(): ChatStanzaListener {
            return listener!!
        }
    }
}