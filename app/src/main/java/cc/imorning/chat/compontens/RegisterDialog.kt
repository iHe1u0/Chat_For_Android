package cc.imorning.chat.compontens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import cc.imorning.chat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDialog(onDismiss: () -> Unit) {

    var nickName by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "注册"
            )
        },
        text = {
            Column {
                TextField(
                    value = nickName,
                    onValueChange = { nickName = it },
                    label = { Text(text = "昵称") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = "昵称"
                        )
                    },
                    singleLine = true,
                )
                TextField(
                    value = account,
                    onValueChange = { account = it },
                    label = { Text(text = "账号") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "账号"
                        )
                    },
                    singleLine = true
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "密码") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "密码"
                        )
                    },
                    singleLine = true
                )
                TextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
                    label = { Text(text = "确认密码") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "确认密码"
                        )
                    },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "注册")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        })
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