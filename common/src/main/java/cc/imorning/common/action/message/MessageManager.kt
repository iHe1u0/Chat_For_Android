package cc.imorning.common.action.message

import android.util.Log
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jxmpp.jid.impl.JidCreate

private const val TAG = "MessageManager"

/**
 * 消息管理类
 *
 * @since 1.0.0
 *
 */
object MessageManager {

    private val connection = CommonApp.getTCPConnection()

    private val chatManager = ChatManager.getInstanceFor(connection)

    /**
     * get offline message
     */
    fun getOfflineMessage(): List<Message> {

        //将用户状态设为离线
        val offlinePresence =
            PresenceBuilder.buildPresence().ofType(Presence.Type.unavailable).build()
        connection.sendStanza(offlinePresence)

        val offlineMessageManager = OfflineMessageManager.getInstanceFor(connection)
        // 获取离线消息
        val messages: List<Message> = offlineMessageManager.messages
        // offlineMessageManager.headers

        // val counts = offlineMessageManager.messageCount
        // 获取后删除离线消息记录
        offlineMessageManager.deleteMessages()

        // 将用户状态设置为在线
        val onlinePresence =
            PresenceBuilder.buildPresence().ofType(Presence.Type.available).build()
        connection.sendStanza(onlinePresence)

        return messages
    }

    /**
     * send message to contact
     *
     * @param jidString jid like 'admin@chat.catcompany.cn'
     * @param message message text
     *
     * @return true if send success
     */
    fun sendMessage(
        jidString: String,
        message: String
    ): Boolean {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return false
        }
        val newChat = chatManager.chatWith(JidCreate.entityBareFrom(jidString))
        try {
            newChat.send(message)
            Log.i(TAG, "sendMessage success.")
            return true
        } catch (exception: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "send message failed", exception)
            }
        }
        return false
    }
}