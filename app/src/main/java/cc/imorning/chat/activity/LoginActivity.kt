package cc.imorning.chat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.R
import cc.imorning.chat.compontens.BottomSheetListItem
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
            content = { padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
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

    var showLoginDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    if (showLoginDialog) {
        Dialog(
            onDismissRequest = { showLoginDialog = false },
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column {
                    CircularProgressIndicator()
                    Text(
                        text = "登陆中",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
    if (showDialog) {
        ComposeDialogUtils.InfoAlertDialog(message = message) {
            viewModel.setStatus()
            showDialog = false
        }
    }
    when (viewModel.getLoginStatus().observeAsState().value!!) {
        StatusCode.INIT -> {}
        StatusCode.OK -> {
            showLoginDialog = false
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
            (context as Activity).finish()
        }
        StatusCode.LoginCode.ACCOUNT_OR_TOKEN_IS_NULL -> {
            message = "账号或密码不能为空"
            showDialog = true
            showLoginDialog = false
        }
        StatusCode.LoginCode.LOGIN_AUTH_FAILED -> {
            message = "账号或密码错误"
            showDialog = true
            showLoginDialog = false
        }
        else -> {
            message = "登录失败"
            showDialog = true
            showLoginDialog = false
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
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .background(Color.Red)
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
                    onCheckedChange = { checked ->
                        viewModel.setChecked(checked)
                    }
                )
                Text(text = "记住密码")
            }
            Button(
                onClick = {
                    showLoginDialog = true
                    viewModel.login()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "登录")
            }
        }
    }
}

@Preview
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
            painter = painterResource(id = R.drawable.ic_register),
            contentDescription = "注册"
        )
    }
}

@Composable
fun BottomSheetContent() {
    val context = LocalContext.current
    Column {
        BottomSheetListItem(
            imageVector = Icons.Filled.Add,
            title = "注册",
            onItemClick = { title ->
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
            }
        )
        BottomSheetListItem(
            imageVector = Icons.Filled.FindInPage,
            title = "忘记密码",
            onItemClick = { title ->
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun BottomSheetContentPreview() {
    BottomSheetContent()
}
