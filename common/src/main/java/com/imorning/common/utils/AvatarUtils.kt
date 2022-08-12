package com.imorning.common.utils

import com.google.android.material.imageview.ShapeableImageView
import com.imorning.chat.App
import com.imorning.common.action.ContactAction
import com.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.XMPPConnection

class AvatarUtils {

    private val connection: XMPPConnection = App.getTCPConnection()


    fun setAvatar(jid: String, imageView: ShapeableImageView) {

    }

     fun cacheAvatar(jid: String) {
         if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return
        }
        val vCard = ContactAction.getContactVCard(jid)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                saveContactAvatar(jid = jid, avatarByte)
            }
        }
    }

    private fun saveContactAvatar(jid: String, avatarByte: ByteArray) {
        val localCache = FileUtils.instance.getAvatarCache(jid)
        if (localCache.exists()) {
            localCache.delete()
        }
        localCache.writeBytes(avatarByte)
    }


    companion object {
        private const val TAG = "AvatarUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AvatarUtils()
        }

    }
}