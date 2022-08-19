package cc.imorning.common.utils

import cc.imorning.common.CommonApp
import cc.imorning.common.database.AppDatabase
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message

private const val TAG = "MessageHelper"

object MessageHelper {

    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

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
                processChatMessage(message)
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

    private fun processChatMessage(message: Message) {

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