package com.imorning.common.action

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.BuildConfig
import com.imorning.common.constant.Server
import com.imorning.common.constant.StatusCode.ERROR
import com.imorning.common.constant.StatusCode.LOGIN_FAILED_CAUSE_ONLINE
import com.imorning.common.constant.StatusCode.OK
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

object Login {

    private const val TAG = "Login"

    fun run(
        account: String,
        password: String
    ): Int {
        val connection: AbstractXMPPConnection = App.getConnection()
        try {
            return if (!connection.isAuthenticated) {
                connection.login(account, password)
                // 接受离线消息
                val presence = Presence(Presence.Type.unavailable)
                connection.sendStanza(presence)
                OK
            } else {
                LOGIN_FAILED_CAUSE_ONLINE
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "login failed cause ${e.message}")
            }
            return ERROR
        }
    }

}