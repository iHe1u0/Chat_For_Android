package com.imorning.chat

import android.app.Application
import android.content.Context
import com.imorning.common.BuildConfig
import com.imorning.common.constant.Server
import com.imorning.common.utils.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
            MainScope().launch(Dispatchers.IO) {
                connectionInstance!!.connect()
                Log.d(TAG, "server connected")
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "server connect failed", e)
            }
        }

    }

}