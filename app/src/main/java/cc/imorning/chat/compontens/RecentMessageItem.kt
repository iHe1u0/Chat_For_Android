package cc.imorning.chat.compontens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.imorning.chat.R
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.common.constant.Config

@Composable
fun RecentMessageItem(message: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val chatActivity = Intent(context, ChatActivity::class.java)
                chatActivity.action = Config.Intent.Action.START_CHAT_FROM_APP
                chatActivity.putExtra(Config.Intent.Key.START_CHAT_JID, message)
                context.startActivity(chatActivity)
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_default_avatar),
            contentDescription = message,
            alignment = Alignment.Center
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}