package cc.imorning.common.action.account

import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import cc.imorning.common.constant.StatusCode.ERROR
import cc.imorning.common.constant.StatusCode.OK
import com.orhanobut.logger.Logger
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.parts.Localpart

private const val TAG = "Register"

object Register {

    fun run(
        userName: String,
        password: String
    ): Int {
        try {
            val accountManager = AccountManager.getInstance(App.getTCPConnection())
            accountManager.sensitiveOperationOverInsecureConnection(true)
            accountManager.createAccount(Localpart.from(userName), password)
            return OK
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Logger.e(TAG, "register new user failed", e)
            }
            return ERROR
        }
    }

}