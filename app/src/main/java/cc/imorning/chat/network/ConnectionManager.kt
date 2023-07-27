package cc.imorning.chat.network

import cc.imorning.chat.App
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection

private const val TAG = "ConnectionManager"

object ConnectionManager {

    private var connection = App.getTCPConnection()

    /**
     * Try to connect to server, the connection should be connected
     * before use [XMPPTCPConnection.connect] method
     *
     * @param connection The connection will be connected to server, use global connection if it's null
     *
     */
    @Synchronized
    fun connect(connection: AbstractXMPPConnection = getConnection()) {
        if (NetworkUtils.isNetworkConnected(CommonApp.getContext())) {
            if (!connection.isConnected) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        connection.connect()
                    } catch (_: SmackException) {}
                }
            }
        }
    }

    @Synchronized
    fun disconnect(connection: XMPPTCPConnection? = getConnection()) {
        if (connection == null) {
            return
        }
        if (isConnectionAvailable(connection)) {
            GlobalScope.launch(Dispatchers.IO) {
                connection.disconnect()
            }
        }
    }

    /**
     * Return global connection
     */
    fun getConnection(): XMPPTCPConnection {
        return connection
    }

    /**
     * Set connection and update status
     */
    fun setConnection(connection: XMPPTCPConnection) {
        this.connection = connection
    }

    /**
     * Judge the connection is available
     */
    fun isConnectionAvailable(connection: XMPPTCPConnection? = getConnection()): Boolean {
        if (connection == null) {
            return false
        }
        return connection.isAuthenticated && NetworkUtils.isNetworkConnected()
    }
}