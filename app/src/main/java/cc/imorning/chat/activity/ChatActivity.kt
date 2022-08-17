package cc.imorning.chat.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.common.constant.Config
import cc.imorning.common.manager.ConnectionManager


private const val TAG = "ChatActivity"

class ChatActivity : BaseActivity() {

    private var chatJid: String? = null
    private val connection = App.getTCPConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (null != intent && null != intent.action) {
            val action = intent.action
            if (action == Intent.ACTION_VIEW) {
                if (!ConnectionManager.isConnectionAuthenticated(this.connection)) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
                chatJid = intent.data?.getQueryParameter(Config.Intent.Key.START_CHAT_JID)
            } else if (action == Config.Intent.Action.START_CHAT_FROM_APP) {
                chatJid = intent.getStringExtra(Config.Intent.Key.START_CHAT_JID).toString()
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "unknown action: $action")
                }
                finish()
            }
        }
        if (chatJid == null) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "chat user name is null or empty")
            }
            Toast.makeText(this, "发起消息失败，目标用户: $chatJid", Toast.LENGTH_LONG).show()
            this.finish()
            return
        }
    }

}