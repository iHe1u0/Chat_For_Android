package cc.imorning.chat.ui.state

import androidx.compose.runtime.Immutable
import cc.imorning.database.entity.MessageEntity
import org.jivesoftware.smack.packet.Presence

@Immutable
data class ChatUiState(
    val nickName: String,
    val mode: Presence.Mode,
    val avatarPath: String,
    val chatJid: String,
    val messageEntity: List<MessageEntity>,
)