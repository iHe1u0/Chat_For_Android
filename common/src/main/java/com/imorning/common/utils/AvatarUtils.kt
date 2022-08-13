package com.imorning.common.utils

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.action.ContactAction
import com.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.XMPPConnection
import java.io.IOException

class AvatarUtils {

    private val connection: XMPPConnection = App.getTCPConnection()

    fun cacheAvatar(jid: String): String? {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return null
        }
        val vCard = ContactAction.getContactVCard(jid)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                return saveContactAvatar(jid = jid, avatarByte)
            }
        }
        return null
    }

    fun cacheUserAvatar(avatarByte: ByteArray) {
        val localCache = FileUtils.instance.getAvatarCache(connection.user.asEntityBareJidString())
        if (localCache.exists()) {
            localCache.delete()
        }
        try {
            localCache.writeBytes(avatarByte)
        } catch (e: IOException) {
            Log.e(TAG, "save user avatar failed", e)
        }
    }

    fun getAvatarPath(jid: String?): String? {
        if (jid == null) {
            if (ConnectionManager.isConnectionAuthenticated(connection)) {
                return FileUtils.instance.getAvatarCache(connection.user.asEntityBareJidString()).absolutePath
            }
            return null
        }
        return FileUtils.instance.getAvatarCache(jid).absolutePath
    }

    private fun saveContactAvatar(jid: String, avatarByte: ByteArray): String? {
        val localCache = FileUtils.instance.getAvatarCache(jid)
        if (localCache.exists()) {
            localCache.delete()
        }
        try {
            localCache.writeBytes(avatarByte)
            return localCache.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "saveContactAvatar failed", e)
        }
        return null
    }


    companion object {
        private const val TAG = "AvatarUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AvatarUtils()
        }

    }
}