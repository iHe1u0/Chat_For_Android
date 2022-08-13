package com.imorning.common.action.account

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.BuildConfig
import com.imorning.common.constant.StatusCode.ERROR
import com.imorning.common.constant.StatusCode.OK
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