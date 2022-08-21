package cc.imorning.chat.view.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cc.imorning.chat.R

private const val TAG = "ComposeDialogUtils"

object ComposeDialogUtils {

    @Composable
    fun LoginAlertDialog(
        onDismiss: () -> Unit
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column {
                    CircularProgressIndicator()
                    Text(
                        text = "登录中",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

    }

    @Composable
    fun InfoAlertDialog(
        title: String = "提示",
        message: String,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = onDismiss)
                {
                    Text(text = "确定")
                }
            },
        )
    }

    @Composable
    fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Text(
                    text = "功能正在开发中 \uD83D\uDE48",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }

    @Composable
    fun ShowAbout(onDismiss: () -> Unit) {
        val context = LocalContext.current
        val bitmap: Bitmap = BitmapFactory.decodeStream(context.assets.open("logo.png"))
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.size(128.dp),
                    alignment = Alignment.Center
                )
            },
            text = {
                Text(
                    text = "Designed by iMorning",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewDialog() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ComposeDialogUtils.ShowAbout { showDialog = false }
    }
    ComposeDialogUtils.ShowAbout {
        showDialog = false
    }
}