package cc.imorning.common.manager

import cc.imorning.chat.App
import org.jivesoftware.smack.XMPPConnection

object ConnectionManager {

    fun isConnectionAuthenticated(connection: XMPPConnection? = App.getTCPConnection()): Boolean {

        if (connection == null || !connection.isConnected || !connection.isAuthenticated) {
            return false
        }
        return true
    }

}