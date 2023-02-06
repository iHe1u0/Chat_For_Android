package cc.imorning.chat.compontens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.constant.ChatType
import cc.imorning.common.constant.Config
import cc.imorning.database.entity.RosterEntity

private const val TAG = "ContactItem"

/**
 * 联系人列表条目
 */
@Composable
fun RosterItem(
    roster: RosterEntity
) {
    val context = LocalContext.current
    val jidString = roster.jid
    val nickname = roster.nick
    var avatarPath = AvatarUtils.instance.getAvatarPath(jidString)
    if (!AvatarUtils.instance.hasAvatarCache(jidString)) {
        avatarPath = AvatarUtils.instance.getOnlineAvatar(jidString)
    }
    TextButton(
        onClick = {
            val chatActivity = Intent(context, ChatActivity::class.java)
            chatActivity.action = Config.Intent.Action.START_CHAT_FROM_APP
            chatActivity.putExtra(Config.Intent.Key.START_CHAT_JID, jidString)
            chatActivity.putExtra(Config.Intent.Key.START_CHAT_TYPE, ChatType.Type.Single)
            context.startActivity(chatActivity)
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Avatar(avatarPath)
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                text = jidString,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Canvas(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawCircle(
                    color = Color.Red,
                    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    radius = size.minDimension / 4
                )
            }
        }
    }
    Divider(
        modifier = Modifier.padding(horizontal = 0.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
}
