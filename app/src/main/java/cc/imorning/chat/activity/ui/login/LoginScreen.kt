package cc.imorning.chat.activity.ui.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen() {
    ChatTheme {
        Scaffold(
            topBar = {},
            floatingActionButton = { FloatingActionButton() },
            content = {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {

    var account by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = account,
                onValueChange = { account = it },
                label = { Text(text = "账号") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "账户"
                    )
                }
            )
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text(text = "密码") },
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = "密码"
                    )
                },
                visualTransformation = PasswordVisualTransformation()
            )
            // Row(
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .padding(start = 32.dp),
            //     verticalAlignment = Alignment.CenterVertically,
            //     horizontalArrangement = Arrangement.Start
            // ) {
            //     Checkbox(checked = true, onCheckedChange = {})
            //     Text(text = "记住密码")
            // }
            Button(onClick = { }) {
                Text(text = "登录")
            }
        }
    }
}

@Composable
fun FloatingActionButton() {
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    FloatingActionButton(
        onClick = {
            showBuildingDialog = true
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.HelpOutline,
            contentDescription = "帮助"
        )
    }
}

@Composable
fun BottomSheetListItem(
    icon: ImageVector,
    title: String,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(title) })
            .height(55.dp)
            .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title)
    }
}

@Composable
fun BottomSheetContent() {
    val context = LocalContext.current
    Column {
        BottomSheetListItem(
            icon = Icons.Filled.NoAccounts,
            title = "找回密码",
            onItemClick = { title ->
                Toast.makeText(
                    context,
                    title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        BottomSheetListItem(
            icon = Icons.Filled.Add,
            title = "添加用户",
            onItemClick = { title ->
                Toast.makeText(
                    context,
                    title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        BottomSheetListItem(
            icon = Icons.Filled.Copyright,
            title = "关于",
            onItemClick = { title ->
                Toast.makeText(
                    context,
                    title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}