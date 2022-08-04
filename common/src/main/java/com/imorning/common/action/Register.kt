package com.imorning.common.action

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.BuildConfig
import com.imorning.common.constant.StatusCode.ERROR
import com.imorning.common.constant.StatusCode.OK
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.parts.Localpart

private const val TAG = "Register"

object Register {

    fun run(
        userName: String,
        password: String
    ): Int {
        try {
            val accountManager = AccountManager.getInstance(App.getConnection())
            accountManager.sensitiveOperationOverInsecureConnection(true)
            accountManager.createAccount(Localpart.from(userName), password)
            return OK
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "register new user failed", e)
            }
            return ERROR
        }
    }

}