package com.imorning.common.action.message

import android.util.Log
import com.imorning.common.BuildConfig
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smackx.offline.OfflineMessageManager

private const val TAG = "MessageManager"

/**
 * 消息管理类
 *
 * @since 1.0.0
 *
 */
object MessageManager {

    /**
     * 获取用户的离线消息
     */
    fun getOfflineMessage(connection: AbstractXMPPConnection): List<Message> {

        //将用户状态设为离线
        val presence = Presence(Presence.Type.unavailable)
        // val offlinePresence =
        //     PresenceBuilder.buildPresence(Presence.Type.unavailable.toString())
        connection.sendStanza(presence)

        val offlineMessageManager = OfflineMessageManager.getInstanceFor(connection)
        // 获取离线消息
        val messages: List<Message> = offlineMessageManager.messages
        offlineMessageManager.headers

        val counts = offlineMessageManager.messageCount
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getOfflineMessage: $counts messages")
        }
        // 获取后删除离线消息记录
        offlineMessageManager.deleteMessages()

        // 将用户状态设置为在线
        val onlinePresence = Presence(Presence.Type.available)
        connection.sendStanza(onlinePresence)

        return messages

    }
}