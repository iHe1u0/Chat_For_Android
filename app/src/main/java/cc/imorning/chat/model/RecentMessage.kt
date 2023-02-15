package cc.imorning.chat.model

import cc.imorning.chat.App
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.constant.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

data class RecentMessage(
    val nickName: String = "",
    val user: String = "",
    val message: String = "",
    val time: Long,
    val messageType: MessageType = MessageType.TEXT
) {
    init {
        if (ConnectionManager.isConnectionAvailable(App.getTCPConnection())) {
            MainScope().launch(Dispatchers.IO) {
                AvatarUtils.instance.saveAvatar(user)
            }
        }
    }

    override fun toString(): String {
        return "[$user]:$message"
    }
}