package cc.imorning.chat.compontens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.model.RecentMessage
import cc.imorning.common.constant.ChatType
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.AvatarUtils

private const val TAG = "RecentMessageItem"

@Composable
fun RecentMessageItem(message: RecentMessage) {
    val context = LocalContext.current
    val avatarPath = AvatarUtils.instance.getAvatarPath(message.sender)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val chatActivity = Intent(context, ChatActivity::class.java)
                chatActivity.action = Config.Intent.Action.START_CHAT_FROM_APP
                chatActivity.putExtra(Config.Intent.Key.START_CHAT_JID, message.sender)
                chatActivity.putExtra(Config.Intent.Key.START_CHAT_TYPE, ChatType.Type.Single)
                context.startActivity(chatActivity)
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(avatarPath = avatarPath)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = message.nickName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = message.message,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}