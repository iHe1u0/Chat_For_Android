package cc.imorning.chat.monitor

import cc.imorning.chat.App
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza

class ChatStanzaListener private constructor() : StanzaListener {

    private val connection = App.getTCPConnection()

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
        // if this is a message,then return,we process Message in IncomingMessageListener.kt
        if (packet is Message) {
            return
        }
        val stanza = packet
        // MessageHelper.processMessage(
        //     from = stanza.from.asEntityBareJidString(),
        //     message = stanza
        // )
        // ChatNotificationManager.manager.showNotification(
        //     message = OnlineMessage(
        //         from = stanza.from.asEntityBareJidString(),
        //         receiver = connection.user.asEntityBareJidString(),
        //         message = stanza.body
        //     ),
        //     from = stanza.from.asEntityBareJidString(),
        // )
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