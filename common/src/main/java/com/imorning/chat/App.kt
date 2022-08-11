package com.imorning.chat

import android.app.Application
import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.imorning.common.constant.Server
import com.imorning.common.database.UserDatabase
import com.imorning.common.utils.Log
import kotlinx.coroutines.*
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration


class App : Application() {

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        val configurationBuilder = XMPPTCPConnectionConfiguration.builder()
        configurationBuilder.setHost(Server.HOST_NAME)
        configurationBuilder.setXmppDomain(Server.DOMAIN)
        configurationBuilder.setPort(Server.LOGIN_PORT)
        configurationBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        configurationBuilder.setSendPresence(false)
        xmppTcpConnection = XMPPTCPConnection(configurationBuilder.build())
    }

    companion object {
        private const val TAG = "App"

        private var application: Application? = null
        private var xmppTcpConnection: XMPPTCPConnection? = null

        fun getContext(): Context {
            return application!!
        }

        @Synchronized
        fun getTCPConnection(): XMPPTCPConnection {
            return xmppTcpConnection!!
        }

    }

}