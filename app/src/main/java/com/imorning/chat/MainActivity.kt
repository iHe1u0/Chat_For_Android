package com.imorning.chat

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imorning.chat.ui.theme.ChatForAndroidTheme
import com.imorning.common.action.Login
import com.imorning.common.constant.StatusCode.OK

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContent {
            ChatForAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showSystemUi = true)
fun LoginView() {

    var account by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("@Morning2021") }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = account,
                    onValueChange = {
                        account = it
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .width(300.dp)
                        .border(
                            1.dp,
                            Color(111, 111, 111, 66),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AccountBox,
                            contentDescription = ""
                        )
                    },
                    singleLine = true
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .width(300.dp)
                        .border(
                            1.dp,
                            Color(111, 111, 111, 66),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Password,
                            contentDescription = ""
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                Button(onClick = {
                    if (Login.run(account, password) == OK) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "login success")
                        }
                    }
                }) {
                    Text("登录")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row {
                Text(text = "注册")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "忘记密码")
            }
        }
    }
}


