package cc.imorning.chat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.chat.viewmodel.ChatViewModel
import cc.imorning.chat.viewmodel.ChatViewModelFactory
import cc.imorning.common.CommonApp
import cc.imorning.common.action.UserAction
import cc.imorning.common.constant.ChatType
import cc.imorning.common.constant.Config
import cc.imorning.common.manager.ConnectionManager


private const val TAG = "ChatActivity"

class ChatActivity : BaseActivity() {

    private var chatUserJid: String = ""
    private var chatType: ChatType.Type = ChatType.Type.Unknown
    private val connection = CommonApp.getTCPConnection()

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(CommonApp().appDatabase.appDatabaseDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            ChatTheme {
                ChatScreen(viewModel, chatUserJid, chatType)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (null != intent.action) {
            when (val action = intent.action) {
                Intent.ACTION_VIEW -> {
                    if (!ConnectionManager.isConnectionAuthenticated(this.connection)) {
                        Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show()
                        val loginActivity = Intent(this, LoginActivity::class.java)
                        startActivity(loginActivity)
                        this.finish()
                    }
                    chatUserJid =
                        intent.data?.getQueryParameter(Config.Intent.Key.START_CHAT_JID).toString()
                    chatType = ChatType.from(
                        intent.data?.getQueryParameter(Config.Intent.Key.START_CHAT_TYPE).toString()
                    )
                }
                Config.Intent.Action.START_CHAT_FROM_APP -> {
                    chatUserJid = intent.getStringExtra(Config.Intent.Key.START_CHAT_JID).toString()
                    chatType = ChatType.from(
                        intent.getStringExtra(Config.Intent.Key.START_CHAT_JID).toString()
                    )
                }
                else -> {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "unknown action: $action")
                    }
                    finish()
                }
            }
        }
        if (chatUserJid.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "chat user name is null or empty")
                return
            }
            Toast.makeText(this, "发起会话失败", Toast.LENGTH_LONG).show()
            this.finish()
            return
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "start with jid: $chatUserJid")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel, chatUserJid: String, chatType: ChatType.Type) {
    // Just for dev
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    val user = viewModel.userOrGroupName.observeAsState()
    val status = viewModel.userOrGroupStatus.observeAsState()

    // set user jid and chat type
    viewModel.setChatType(chatType)
    viewModel.setChatUserId(chatUserJid)

    Scaffold(
        topBar = {
            ChatAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${UserAction.getNickName(user.value.orEmpty())}",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${status.value}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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