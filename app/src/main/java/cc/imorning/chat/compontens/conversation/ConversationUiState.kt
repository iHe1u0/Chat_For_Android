package cc.imorning.chat.compontens.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import cc.imorning.chat.R
import cc.imorning.common.entity.MessageEntity
import org.jivesoftware.smack.packet.Presence.Mode

class ConversationUiState(
    val nickName: String,
    val friendStatus: Mode,
    initialMessages: List<MessageEntity>
) {
    private val _messages: MutableList<MessageEntity> = initialMessages.toMutableStateList()
    val messages: List<MessageEntity> = _messages

    fun addMessage(msg: MessageEntity) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ic_default_avatar else R.drawable.ic_nick
)
