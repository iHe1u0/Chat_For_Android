package cc.imorning.chat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.R
import cc.imorning.chat.compontens.BottomSheetListItem
import cc.imorning.chat.compontens.RegisterDialog
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.chat.viewmodel.LoginViewModel

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
    val isSaveChecked = viewModel.shouldSaveState().observeAsState()
    val shouldShowWaitingDialog = viewModel.shouldShowWaitingDialog().observeAsState()
    val shouldShowErrorDialog = viewModel.shouldShowErrorDialog().observeAsState()
    val message = viewModel.getErrorMessage().observeAsState()
    val needStartActivity = viewModel.needStartActivity().observeAsState()
    if (shouldShowWaitingDialog.value == true) {
        ComposeDialogUtils.ShowWaitingDialog(title = "登录中")
    }
    if (shouldShowErrorDialog.value == true) {
        ComposeDialogUtils.InfoAlertDialog(message = message.value!!) {
            viewModel.closeDialog()
        }
    }
    if (needStartActivity.value == true) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var showToken by remember { mutableStateOf(false) }
        val visualTransformation =
            if (showToken) VisualTransformation.None else PasswordVisualTransformation()
        Column {
            OutlinedTextField(
                value = account.value.toString(),
                onValueChange = { viewModel.setAccount(it.trim()) },
                label = { Text(text = "账号") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "账户"
                    )
                },
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .background(Color.Red)
            )
            OutlinedTextField(
                value = token.value.toString(),
                onValueChange = { viewModel.setToken(it.trim()) },
                label = { Text(text = "密码") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = "密码"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = visualTransformation,
                trailingIcon = {
                    IconButton(onClick = { showToken = !showToken }) {
                        if (showToken) {
                            Icon(imageVector = Icons.Filled.Visibility, contentDescription = "")
                        } else {
                            Icon(imageVector = Icons.Filled.VisibilityOff, contentDescription = "")
                        }
                    }
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Checkbox(checked = isSaveChecked.value!!,
                    onCheckedChange = { checked ->
                        viewModel.setSaveState(checked)
                    }
                )
                Text(text = "记住密码")
            }
            Button(
                onClick = {
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
    var showRegisterDialog by remember { mutableStateOf(false) }
    if (showRegisterDialog) {
        RegisterDialog(
            onDismiss = { showRegisterDialog = false }
        )
    }
    FloatingActionButton(
        onClick = {
            showRegisterDialog = true
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
