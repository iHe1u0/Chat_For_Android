package cc.imorning.chat.ui.state

import androidx.compose.runtime.Immutable
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.utils.AvatarUtils


@Immutable
data class DetailsScreenUiState(val jid: String) {
    fun isMe() = jid == App.user

    fun avatar() = AvatarUtils.instance.getAvatarPath(jid)

    fun nickName() = RosterAction.getNickName(jid)

    fun status() = RosterAction.getRosterStatus(jid)

    fun phone() = RosterAction.getPhone(jid)

    fun email() = RosterAction.getEmail(jid)
}