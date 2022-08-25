package cc.imorning.chat.compontens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.imorning.chat.model.User
import cc.imorning.common.utils.AvatarUtils

@Composable
fun VCardDialog(jidString: String) {

    val user = User(jidString)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(avatarPath = AvatarUtils.instance.getAvatarPath(jidString))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            VCardItem(key = "用户ID", value = jidString)
            VCardItem(key = "昵   称", value = user.nickName)
            VCardItem(key = "姓   名", value = user.name)
            VCardItem(key = "电   话", value = user.phoneNumber)
            VCardItem(key = "邮   箱", value = user.email)
            VCardItem(key = "住   址", value = user.homeAddress)
            VCardItem(key = "工作地址", value = user.workAddress)
        }
    }

}

@Composable
fun VCardItem(key: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = key,
            maxLines = 1,
            textAlign = TextAlign.Justify
        )
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .background(Color.Red)
        )
        SelectionContainer {
            Text(text = value.orEmpty())
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun VCardPreview() {
    val jidString = "admin@chat.catcompany.cn"
    VCardDialog(jidString = jidString)
}