package cc.imorning.chat.activity.ui.contact

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.imorning.chat.R
import cc.imorning.chat.model.Contact
import cc.imorning.chat.view.ui.ComposeDialogUtils

@Composable
fun ContactItem(
    contact: Contact
) {
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    TextButton(
        onClick = {
            showBuildingDialog = true
        },
        modifier = Modifier.fillMaxWidth(),
        //horizontalArrangement = Arrangement.Center,
        //verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_default_avatar),
            contentDescription = "头像",
            modifier = Modifier
                .fillMaxHeight()
                .size(24.dp),
            alignment = Alignment.Center
        )
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Text(
                text = contact.nickName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = contact.jid,
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