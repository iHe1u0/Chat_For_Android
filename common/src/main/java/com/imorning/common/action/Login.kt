package com.imorning.common.action

import android.util.Log
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
        val connection: AbstractXMPPConnection? = getConnection()
        try {
            if (connection != null) {
                return if (!connection.isAuthenticated) {
                    connection.login(account, password)
                    // 接受离线消息
                    val presence = Presence(Presence.Type.unavailable)
                    connection.sendStanza(presence)
                    OK
                } else {
                    LOGIN_FAILED_CAUSE_ONLINE
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "login failed cause ${e.message}")
            }
            return ERROR
        }
        return ERROR
    }

    private fun getConnection(): AbstractXMPPConnection? {
        val configurationBuilder = XMPPTCPConnectionConfiguration.builder()
        try {
            configurationBuilder.setHost(Server.HOST_NAME)
            configurationBuilder.setXmppDomain(Server.DOMAIN)
            configurationBuilder.setPort(Server.LOGIN_PORT)
            configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            configurationBuilder.setSendPresence(false)
            val connectionInstance = XMPPTCPConnection(configurationBuilder.build())
            connectionInstance.connect()
            Log.i(TAG, "server connect successfully")
            return connectionInstance
        } catch (e: Exception) {
            Log.e(TAG, "server connect failed", e)
        }
        return null
    }

}