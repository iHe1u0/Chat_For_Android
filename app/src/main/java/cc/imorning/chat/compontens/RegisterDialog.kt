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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import cc.imorning.chat.R
import cc.imorning.chat.action.account.RegisterAction
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.common.constant.ResultCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDialog(onDismiss: () -> Unit) {

    val context = LocalContext.current
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }

    var regMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ComposeDialogUtils.InfoAlertDialog(
            title = stringResource(id = R.string.information),
            message = regMessage,
            confirmTitle = stringResource(id = R.string.ok),
            onConfirm = { showDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = stringResource(id = R.string.register))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = account,
                    onValueChange = { account = it },
                    label = { Text(text = stringResource(id = R.string.account)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(id = R.string.account)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = stringResource(id = R.string.password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = stringResource(id = R.string.password)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                )
                OutlinedTextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
                    label = { Text(text = stringResource(R.string.confirm_password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = stringResource(id = R.string.confirm_password)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
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
                    doRegistration(account, password) { result ->
                        regMessage = result
                        showDialog = true
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.register))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        })
}

fun doRegistration(
    account: String,
    password: String,
    onResult: (String) -> Unit
) {
    when (RegisterAction.run(account, password)) {
        ResultCode.OK -> {
            onResult("账号[${account.lowercase(Locale.getDefault())}]注册成功，请返回登录")
        }
        ResultCode.ERROR_NOT_SUPPORT_OPERATION -> {
            onResult("当前禁止新用户注册")
        }
        ResultCode.ERROR_NETWORK -> {
            onResult("网络连接失败，请检查网络")
        }
        ResultCode.ERROR_NO_RESPONSE -> {
            onResult("服务器未响应，请重启App后尝试")
        }
        ResultCode.ERROR -> {
            onResult("未知错误")
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