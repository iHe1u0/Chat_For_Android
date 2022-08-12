package com.imorning.chat

import android.app.Application
import android.content.Context
import com.imorning.common.constant.ServerConfig
import com.imorning.common.database.UserDatabase
import com.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jivesoftware.smack.ConnectionConfiguration
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
        configurationBuilder.setHost(ServerConfig.HOST_NAME)
        configurationBuilder.setXmppDomain(ServerConfig.DOMAIN)
        configurationBuilder.setPort(ServerConfig.LOGIN_PORT)
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
            xmppTcpConnection!!.apply {
                if (!this.isConnected && NetworkUtils.isNetworkConnected(getContext())) {
                    runBlocking(Dispatchers.IO) {
                        this@apply.connect()
                    }
                }
                return this
            }
        }

    }

}