package cc.imorning.chat.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import cc.imorning.chat.R
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.ui.view.ToastUtils
import cc.imorning.common.constant.ChatType
import cc.imorning.common.constant.Config

object IntentUtils {

    /**
     * open web browser
     *
     * @param context Context for start Activity
     *
     * @param uri Web uri
     */
    fun openBrowser(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            ToastUtils.showMessage(context, context.getString(R.string.browser_not_found))
        }
    }

    /**
     * start [ChatActivity]
     *
     * @param context Context for start Activity
     *
     * @param user the other user for chatting
     */
    fun startChatActivity(context: Context, user: String) {
        val chatActivity = Intent(context, ChatActivity::class.java)
        chatActivity.action = Config.Intent.Action.START_CHAT_FROM_APP
        chatActivity.putExtra(Config.Intent.Key.START_CHAT_JID, user)
        chatActivity.putExtra(Config.Intent.Key.START_CHAT_TYPE, ChatType.Type.Single)
        context.startActivity(chatActivity)
    }
}