package cc.imorning.chat.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.compontens.VCardDialog
import cc.imorning.chat.compontens.conversation.ClickableMessage
import cc.imorning.common.BuildConfig
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.FileUtils
import cc.imorning.common.utils.QrUtils
import cc.imorning.common.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ComposeDialogUtils"

object ComposeDialogUtils {

    @Composable
    fun ShowWaitingDialog(title: String) {
        Dialog(
            onDismissRequest = {},
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = title,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

    }

    @Composable
    fun InfoAlertDialog(
        title: String = stringResource(id = R.string.information),
        message: String = "",
        confirmTitle: String = "",
        dismissTitle: String = "",
        onDismiss: () -> Unit = {},
        onConfirm: () -> Unit = {},
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
                TextButton(onClick = onConfirm)
                {
                    Text(text = confirmTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissTitle)
                }
            }
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
    fun AboutDialog(onDismiss: () -> Unit) {
        val context = LocalContext.current
        val content = FileUtils.readStringFromAssets(context, "about.txt")
        val bitmap: Bitmap = BitmapFactory.decodeStream(context.assets.open("logo.webp"))
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "logo",
                    modifier = Modifier.size(128.dp),
                    alignment = Alignment.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.Start) {
                    ClickableMessage(
                        message = content,
                        isUserMe = true,
                        authorClicked = {},
                    )
                    Text(
                        text = "Build time: ${TimeUtils.getFormatDateTime(BuildConfig.build_time)}",
                        maxLines = 1
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditorDialog(
        title: String = "Tips",
        hint: String = "Please input...",
        positiveButton: String = "OK",
        negativeButton: String = "Cancel",
        onConfirm: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        var text by remember { mutableStateOf(hint) }
        AlertDialog(
            onDismissRequest = { onConfirm(text) },
            title = {
                Text(text = title)
            },
            text = {
                Column {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                    )
                }
            },
            confirmButton = {
                Row {
                    Button(
                        onClick = {
                            onConfirm(text)
                        }
                    ) {
                        Text(text = positiveButton)
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = onCancel
                ) {
                    Text(text = negativeButton)
                }
            }
        )
    }

    @Composable
    fun ShowQrCode(data: String, onDismiss: () -> Unit) {
        val bitmap = QrUtils.createQRImage(content = data, widthPix = 144, heightPix = 144)
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "扫一扫添加好友")
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = data,
                            modifier = Modifier.size(144.dp)
                        )
                    } else {
                        Image(imageVector = Icons.Filled.ErrorOutline, contentDescription = "error")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowVCard(jidString: String, onDismiss: () -> Unit) {

        var sendRequest by remember { mutableStateOf(false) }
        var showQrCode by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        if (sendRequest) {
            ShowWaitingDialog(title = "正在发送请求")
            var name by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { sendRequest = false },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "添加[${RosterAction.getNickName(jidString)}]",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        OutlinedTextField(value = name,
                            onValueChange = { name = it.trim() },
                            label = { Text(text = "添加备注") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (name.trim().isBlank() || name.trim().isEmpty()) {
                            name = jidString.split("@")[0]
                        }
                        scope.launch(Dispatchers.IO) {
                            val result = RosterAction.addRoster(
                                jidString,
                                name,
                                arrayOf(Config.DEFAULT_GROUP)
                            )
                            if (Looper.myLooper() == null) {
                                Looper.prepare()
                            }
                            if (result) {
                                Toast.makeText(context, "请求已发送", Toast.LENGTH_LONG).show()
                                sendRequest = false
                            } else {
                                Toast.makeText(context, "请求发送失败", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text(text = "确定")
                    }
                },
                dismissButton = {
                    Button(onClick = { sendRequest = false }) {
                        Text(text = "取消")
                    }
                }
            )

        }
        if (showQrCode) {
            ShowQrCode(data = jidString) {
                showQrCode = false
            }
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                VCardDialog(jidString = jidString)
            },
            confirmButton = {
                if (jidString == App.getTCPConnection().user.asBareJid().toString()) {
                    Button(onClick = {
                        showQrCode = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "分享我的名片"
                        )
                        Text(text = "分享")
                    }
                } else {
                    Button(onClick = { sendRequest = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "添加到好友")
                        Text(text = "添加到好友")
                    }
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "关闭")
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }
}

@Composable
fun PreviewDialog() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ComposeDialogUtils.AboutDialog { showDialog = false }
    }
    ComposeDialogUtils.AboutDialog {
        showDialog = false
    }
}