package com.imorning.chat.activity

import android.os.Bundle
import android.util.Log
import com.imorning.chat.BuildConfig
import com.imorning.chat.R
import com.imorning.common.constant.Config

private const val TAG = "ChatActivity"

class ChatActivity : BaseActivity() {

    private lateinit var chatUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatUserName = intent.getStringExtra(Config.Action.START_CHAT_JID).toString()
        if (chatUserName.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "chat user name is null or empty")
            }
            this@ChatActivity.finish()
        }
        Log.d(TAG, "start chat with $chatUserName")
    }

}