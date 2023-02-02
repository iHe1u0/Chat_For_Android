package cc.imorning.chat.model

import cc.imorning.chat.App
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.constant.MessageType
import cc.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime

data class RecentMessage(
    val nickName: String = "",
    val sender: String = "",
    val message: String = "",
    val time: DateTime = DateTime.now(),
    val messageType: MessageType = MessageType.TEXT
) {
    init {
        if (ConnectionManager.isConnectionAuthenticated(App.getTCPConnection())
            && NetworkUtils.isNetworkConnected()
        ) {
            MainScope().launch(Dispatchers.IO) {
                AvatarUtils.instance.saveAvatar(sender)
            }
        }
    }
}