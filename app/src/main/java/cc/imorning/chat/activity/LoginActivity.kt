package cc.imorning.chat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.chat.viewmodel.LoginViewModel
import cc.imorning.common.constant.StatusCode

private const val TAG = "LoginActivity"

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
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
                    ContentScreen(viewModel)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val account = viewModel.getAccount().observeAsState()
    val token = viewModel.getToken().observeAsState()
    val isSaveChecked = viewModel.getChecked().observeAsState()


    var showDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    if (showDialog) {
        ComposeDialogUtils.InfoAlertDialog(message = message) {
            viewModel.setStatus()
            showDialog = false
        }
    }
    when (viewModel.getLoginStatus().observeAsState().value!!) {
        StatusCode.INIT -> {}
        StatusCode.OK -> {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
            (context as Activity).finish()
        }
        StatusCode.LoginCode.ACCOUNT_OR_TOKEN_IS_NULL -> {
            message = "账号或密码不能为空"
            showDialog = true
        }
        StatusCode.LoginCode.LOGIN_AUTH_FAILED -> {
            message = "账号或密码错误"
            showDialog = true
        }
        else -> {
            message = "登录失败"
            showDialog = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            OutlinedTextField(
                value = account.value.toString(),
                onValueChange = { viewModel.setAccount(it) },
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
                value = token.value.toString(),
                onValueChange = { viewModel.setToken(it) },
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Checkbox(checked = isSaveChecked.value!!,
                    onCheckedChange = {
                        viewModel.setChecked(it)
                    }
                )
                Text(text = "记住密码")
            }
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
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