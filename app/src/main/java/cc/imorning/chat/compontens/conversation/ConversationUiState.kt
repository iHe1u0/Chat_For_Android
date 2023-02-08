package cc.imorning.chat.compontens.conversation

import androidx.compose.runtime.toMutableStateList
import cc.imorning.database.entity.MessageEntity
import org.jivesoftware.smack.packet.Presence.Mode

class ConversationUiState(
    val nickName: String,
    val friendStatus: Mode,
    initialMessages: List<MessageEntity>
) {
    private val _messages: MutableList<MessageEntity> = initialMessages.toMutableStateList()
    val messages: List<MessageEntity> = _messages

    fun addMessageUI(msg: MessageEntity) {
        // Add to the beginning of the list
        _messages.add(0, msg)
    }
}