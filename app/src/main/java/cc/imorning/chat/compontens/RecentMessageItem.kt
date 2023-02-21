package cc.imorning.chat.compontens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.imorning.chat.model.RecentMessage
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.utils.TimeUtils

private const val TAG = "RecentMessageItem"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentMessageItem(
    message: RecentMessage,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit
) {

    val avatarPath = AvatarUtils.getAvatarPath(message.user)

    Box(modifier = Modifier
        .fillMaxWidth()
        .combinedClickable(
            onClick = {
                onItemClick(message.user)
            },
            onLongClick = {
                onItemLongClick(message.user)
            }
        )) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            val time = message.time
            val timeFormat = if (TimeUtils.isToday(time)) {
                TimeUtils.DEFAULT_TIME_FORMAT
            } else {
                TimeUtils.DEFAULT_DATE_FORMAT
            }
            Text(
                text = TimeUtils.millisToDateTime(time).toLocalDateTime().toString(timeFormat),
                maxLines = 1,
                modifier = Modifier.padding(end = 2.dp)
            )
        }
    }
}