package cc.imorning.common.constant

object Config {

    object Intent {
        object Action {
            const val START_CHAT_FROM_APP: String = "app"
            const val START_CHAT_FROM_WEB: String = android.content.Intent.ACTION_VIEW
        }

        object Key {
            const val START_CHAT_JID: String = "chatJid"
        }
    }

    const val DEFAULT_CONFIG = "default_info"
    const val LOGIN_INFO = "login_info"

}