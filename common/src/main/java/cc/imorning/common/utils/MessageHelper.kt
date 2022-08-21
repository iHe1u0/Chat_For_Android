package cc.imorning.common.utils

import cc.imorning.common.CommonApp
import cc.imorning.common.action.ContactAction
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.database.table.RecentMessageEntity
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.joda.time.DateTime

private const val TAG = "MessageHelper"

object MessageHelper {

    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()
    private val connection = CommonApp.getTCPConnection()

    fun processMessage(
        message: Message,
        from: String? = null,
        chat: Chat? = null
    ) {
        RingUtils.playNewMessage(
            context = CommonApp.getContext(),
            type = message.type
        )
        when (message.type) {
            Message.Type.chat -> {
                processChatMessage(message, from, chat)
            }
            Message.Type.groupchat -> {

            }
            Message.Type.headline -> {

            }
            Message.Type.normal -> {

            }
            Message.Type.error -> {

            }
            else -> {}
        }
    }

    private fun processChatMessage(message: Message, from: String?, chat: Chat?) {
        val fromString = from ?: message.from.asUnescapedString()
        val receiver = connection.user.asUnescapedString()
        val nickName = ContactAction.getNickName(fromString)
        val dateTime: DateTime = DateTime.now()
        val id = MD5Utils.digest(receiver.plus(from))!!
        val recentMessage = RecentMessageEntity(
            id = id,
            nickName = nickName,
            sender = fromString,
            receiver = receiver,
            lastMessage = message.body,
            lastMessageTime = dateTime,
        )
        databaseDao.insertRecentMessage(recentMessage)
    }

    private fun processGroupChatMessage(message: Message) {

    }

    private fun processHeadlineMessage(message: Message) {

    }

    private fun processNormalMessage(message: Message) {

    }

    private fun processErrorMessage(message: Message) {

    }

}