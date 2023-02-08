package cc.imorning.chat.model

import cc.imorning.chat.App
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.constant.MessageType
import cc.imorning.common.utils.NetworkUtils
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
        if (ConnectionManager.isConnectionAuthenticated(App.getTCPConnection())
            && NetworkUtils.isNetworkConnected()
        ) {
            MainScope().launch(Dispatchers.IO) {
                AvatarUtils.instance.saveAvatar(user)
            }
        }
    }

    override fun toString(): String {
        return "[$user]:$message"
    }
}