package com.imorning.chat

import android.app.Application
import android.content.Context
import android.util.Log
import com.imorning.common.BuildConfig
import com.imorning.common.constant.Server
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

class App : Application() {

    companion object {
        private const val TAG = "App"

        var application: Application? = null
        var connectionInstance: XMPPTCPConnection? = null

        fun getContext(): Context {
            return application!!
        }

        fun getConnection(): XMPPTCPConnection {
            return connectionInstance!!
        }

    }

    override fun onCreate() {
        super.onCreate()
        application = this

        val configurationBuilder = XMPPTCPConnectionConfiguration.builder()
        try {
            configurationBuilder.setHost(Server.HOST_NAME)
            configurationBuilder.setXmppDomain(Server.DOMAIN)
            configurationBuilder.setPort(Server.LOGIN_PORT)
            configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            configurationBuilder.setSendPresence(false)
            connectionInstance = XMPPTCPConnection(configurationBuilder.build())
            connectionInstance!!.connect()
            Log.i(TAG, "server connect successfully")
        } catch (e: Exception) {
            Log.e(TAG, "server connect failed", e)
        }
        Log.i(TAG, "onCreate: ...........................")
    }

}