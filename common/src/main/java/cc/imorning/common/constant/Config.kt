package cc.imorning.common.constant

import java.util.*

object Config {

    object Intent {
        object Action {
            const val START_CHAT_FROM_WEB: String = android.content.Intent.ACTION_VIEW
            const val START_CHAT_FROM_APP: String = "app"

            const val QUICK_REPLY: String = "quick_reply"
            const val QUICK_REPLY_TO: String = "quick_reply_to"
        }

        object Key {
            const val START_CHAT_JID: String = "chatJid"
            const val START_CHAT_TYPE: String = "chatType"
        }
    }

    const val DEFAULT_CONFIG = "default_info"
    const val LOGIN_INFO = "login_info"

}