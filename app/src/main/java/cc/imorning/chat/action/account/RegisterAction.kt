package cc.imorning.chat.action.account

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import cc.imorning.common.constant.ResultCode
import cc.imorning.common.constant.ServerConfig
import cc.imorning.common.utils.NetworkUtils
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.parts.Localpart

private const val TAG = "Register"

object RegisterAction {

    private val connection = App.getTCPConnection()

    fun run(account: String, password: String): ResultCode {
        if (NetworkUtils.isNetworkNotConnected()) {
            return ResultCode.ERROR_NETWORK
        }
        val accountManager = AccountManager.getInstance(connection)
        accountManager.sensitiveOperationOverInsecureConnection(true)
        try {
            connection.connect()
            if (!accountManager.supportsAccountCreation()) {
                return ResultCode.ERROR_NOT_SUPPORT_OPERATION
            }
            val attributes = mutableMapOf<String, String>()
            attributes["name"] = "User"
            attributes["email"] = account.plus("@").plus(ServerConfig.HOST_NAME)
            accountManager.createAccount(
                Localpart.from(account),
                password,
                attributes
            )
            return ResultCode.OK
        } catch (e: XMPPException.XMPPErrorException) {
            return ResultCode.ERROR_NOT_SUPPORT_OPERATION
        } catch (e: SmackException.NoResponseException) {
            return ResultCode.ERROR_NO_RESPONSE
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "register new user failed: ${e.localizedMessage}", e)
            }
            return ResultCode.ERROR
        }
    }

}