package com.imorning.common.manager

import com.imorning.chat.App
import org.jivesoftware.smack.XMPPConnection

object ConnectionManager {

    fun isConnectionAuthenticated(connection: XMPPConnection? = App.getTCPConnection()): Boolean {

        if (connection == null || !connection.isConnected || !connection.isAuthenticated) {
            return false
        }
        return true
    }

}