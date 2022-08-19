package cc.imorning.chat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.R
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config
import cc.imorning.common.manager.ConnectionManager


private const val TAG = "ChatActivity"

class ChatActivity : BaseActivity() {

    private var chatJid: String? = null
    private val connection = CommonApp.getTCPConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent()
        setContent {
            ChatTheme {
                ChatScreen()
            }
        }
    }

    private fun processIntent() {
        if ((null != intent) && (null != intent.action)) {
            when (val action = intent.action) {
                Intent.ACTION_VIEW -> {
                    if (!ConnectionManager.isConnectionAuthenticated(this.connection)) {
                        Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                    chatJid = intent.data?.getQueryParameter(Config.Intent.Key.START_CHAT_JID)
                }
                Config.Intent.Action.START_CHAT_FROM_APP -> {
                    chatJid = intent.getStringExtra(Config.Intent.Key.START_CHAT_JID).toString()
                }
                else -> {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "unknown action: $action")
                        return
                    }
                    finish()
                }
            }
        }
        if (chatJid == null) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "chat user name is null or empty")
                return
            }
            Toast.makeText(this, "发起消息失败，目标用户: $chatJid", Toast.LENGTH_LONG).show()
            this.finish()
            return
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChatScreen() {
    // Just for dev
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    val contactName = "iMorning"
    val member = 2022
    Scaffold(
        topBar = {
            ChatAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Channel name
                        Text(
                            text = contactName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Number of members
                        Text(
                            text = stringResource(id = R.string.members, member),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Search icon
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = { showBuildingDialog = true })
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(24.dp),
                        contentDescription = stringResource(id = android.R.string.search_go)
                    )
                    // Info icon
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = { showBuildingDialog = true })
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(24.dp),
                        contentDescription = "信息"
                    )
                }
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                ChatContentScreen()
            }

        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val activity = LocalContext.current as Activity
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )
    Box(
        modifier = Modifier.background(
            color = backgroundColors.containerColor(
                colorTransitionFraction = 0.01f
            ).value
        )
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = actions,
            title = title,
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable(onClick = {
                            activity.finish()
                        }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowLeft,
                        contentDescription = "返回上个界面",
                        modifier = Modifier
                        // .size(32.dp)
                    )
                    if (true) {
                        Text(
                            text = "999+",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }
}


@Composable
fun ChatContentScreen() {

}