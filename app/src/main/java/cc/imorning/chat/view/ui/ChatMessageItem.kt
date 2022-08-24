package cc.imorning.chat.view.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TAG = "ChatMessageItem"

@Composable
fun ChatMessageItem() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = Icons.Filled.FamilyRestroom, contentDescription = "")
    }
}

//@Composable
//fun ChatItemBubble(
//    message: Message,
//    isUserMe: Boolean,
//    authorClicked: (String) -> Unit
//) {
//
//    val backgroundBubbleColor = if (isUserMe) {
//        MaterialTheme.colorScheme.primary
//    } else {
//        MaterialTheme.colorScheme.surfaceVariant
//    }
//
//    Column {
//        Surface(
//            color = backgroundBubbleColor,
//            shape = ChatBubbleShape
//        ) {
//            ClickableMessage(
//                message = message,
//                isUserMe = isUserMe,
//                authorClicked = authorClicked
//            )
//        }
//
//        message.image?.let {
//            Spacer(modifier = Modifier.height(4.dp))
//            Surface(
//                color = backgroundBubbleColor,
//                shape = ChatBubbleShape
//            ) {
//                Image(
//                    painter = painterResource(it),
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier.size(160.dp),
//                    contentDescription = stringResource(id = R.string.attached_image)
//                )
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun ChatMessageItemPreview() {
    ChatMessageItem()
}