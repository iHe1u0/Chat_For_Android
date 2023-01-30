package cc.imorning.chat.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.compontens.conversation.ConversationContent
import cc.imorning.chat.compontens.conversation.ConversationUiState
import cc.imorning.chat.compontens.conversation.LocalBackPressedDispatcher
import cc.imorning.chat.data.initialMessages
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.viewmodel.ChatViewModel
import cc.imorning.chat.viewmodel.ChatViewModelFactory
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.ChatType
import cc.imorning.common.constant.Config
import cc.imorning.common.manager.ConnectionManager

private const val TAG = "ChatActivity"

class ChatActivity : ComponentActivity() {

    private var chatUserJid: String = ""
    private var chatType: ChatType.Type = ChatType.Type.Unknown
    private val connection = CommonApp.getTCPConnection()

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(CommonApp().appDatabase.appDatabaseDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, true)
        handleIntent(intent)
        setContent {
            CompositionLocalProvider(
                LocalBackPressedDispatcher provides this@ChatActivity.onBackPressedDispatcher
            ) {
                ChatTheme {
                    val uiState = ConversationUiState(
                        initialMessages = initialMessages,
                        channelName = viewModel.getUserOrGroupName(),
                        channelMembers = 2
                    )
                    ConversationContent(
                        chatUid = chatUserJid,
                        uiState = uiState,
                        navigateToProfile = { /*Action when click user avatar */ },
                        onNavIconPressed = { this@ChatActivity.finish() },
                        // Add padding so that we are inset from any navigation bars
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets
                                .navigationBars
                                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        )
                    )
                }
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
        viewModel.setChatUserId(chatUserJid)
    }

}