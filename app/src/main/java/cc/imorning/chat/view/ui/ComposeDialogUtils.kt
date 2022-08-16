package cc.imorning.chat.view.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cc.imorning.chat.R

object ComposeDialogUtils {

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
            text = {
                Column {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.fillMaxWidth(),
                        alignment = Alignment.Center
                    )
                    Text(
                        text = "Designed by iMorning",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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