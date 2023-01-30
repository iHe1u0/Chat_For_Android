package cc.imorning.chat.compontens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.imorning.chat.model.Contact

@Composable
fun NewUserCard(
    contact: Contact,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(avatarPath = contact.jid)
            Text(
                text = contact.nickName,
                modifier = Modifier.align(Alignment.CenterVertically),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row {
                Button(onClick = { onAccept() }) {
                    Text(text = "同意")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(onClick = { onReject() }) {
                    Text(text = "忽略")
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
