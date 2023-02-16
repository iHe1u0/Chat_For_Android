package cc.imorning.chat.compontens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import cc.imorning.chat.action.account.RegisterAction
import cc.imorning.chat.ui.view.ToastUtils
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.ResultCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDialog(onDismiss: () -> Unit) {

    val context = LocalContext.current
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = "注册新用户")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = account,
                    onValueChange = { account = it },
                    label = { Text(text = "账号") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "账号"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "密码") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "密码"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                )
                OutlinedTextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
                    label = { Text(text = "确认密码") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "确认密码"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (account.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "账号或密码不能为空", Toast.LENGTH_LONG).show()
                    return@TextButton
                }
                if (account.contains("@")) {
                    Toast.makeText(context, "账号不能含有@", Toast.LENGTH_LONG).show()
                    return@TextButton
                }
                if (password != rePassword) {
                    Toast.makeText(context, "两次输入密码不一致", Toast.LENGTH_LONG).show()
                    return@TextButton
                }
                MainScope().launch(Dispatchers.IO) {
                    doRegistration(account, password)
                }
            }) {
                Text(text = "注册")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        })
}

fun doRegistration(account: String, password: String) {

    val context = CommonApp.getContext()
    when (RegisterAction.run(account, password)) {
        ResultCode.OK -> {
            ToastUtils.showMessage(context, "账号[$account]注册成功，请返回登录")
        }
        ResultCode.ERROR_NOT_SUPPORT_OPERATION -> {
            ToastUtils.showMessage(context, "当前禁止新用户注册")
        }
        ResultCode.ERROR_NETWORK -> {
            ToastUtils.showMessage(context, "网络连接失败，请检查网络")
        }
        ResultCode.ERROR_NO_RESPONSE -> {
            ToastUtils.showMessage(context, "服务器未响应，请重启App后尝试")
        }
        ResultCode.ERROR -> {
            ToastUtils.showMessage(context, "未知错误")
        }
        else -> {}
    }

}

@Preview
@Composable
fun PreviewDialog() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        RegisterDialog { showDialog = false }
    }
    RegisterDialog {
        showDialog = false
    }
}