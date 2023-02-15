package cc.imorning.chat.network

import cc.imorning.chat.App
import cc.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection

private const val TAG = "ConnectionManager"

private var count = 0

object ConnectionManager {

    fun isConnectionAvailable(connection: XMPPConnection? = App.getTCPConnection()): Boolean {
        if (connection == null) {
            return false
        }
        return connection.isAuthenticated && NetworkUtils.isNetworkConnected()
    }

    @Synchronized
    fun disconnect(connection: XMPPTCPConnection? = App.getTCPConnection()) {
        if (connection == null) {
            return
        }
        if (isConnectionAvailable(connection)) {
            MainScope().launch(Dispatchers.IO) {
                connection.disconnect()
            }
        }
    }
}