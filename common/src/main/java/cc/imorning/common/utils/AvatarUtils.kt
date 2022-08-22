package cc.imorning.common.utils

import android.util.Log
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.action.ContactAction
import cc.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.XMPPConnection
import java.io.IOException

class AvatarUtils private constructor() {

    private val connection: XMPPConnection = CommonApp.getTCPConnection()


    fun hasAvatarCache(jid: String): Boolean {
        return FileUtils.instance.isFileExist(FileUtils.instance.getAvatarCachePath(jid).absolutePath)
    }

    fun saveAvatar(jid: String) {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "connection is not authenticated")
            }
            return
        }
        val vCard = ContactAction.getContactVCard(jid)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                saveContactAvatar(jid = jid, avatarByte = avatarByte)
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "avatar is null for $jid")
            }
        }
    }

    fun getAvatarPath(jidString: String): String {
        if (BuildConfig.DEBUG){
            Log.d(TAG, "get avatar for [$jidString]")
        }
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            return FileUtils.instance.getAvatarCachePath(jidString).absolutePath
        }
        return getOnlineAvatar(jidString)
    }

    private fun saveContactAvatar(jid: String, avatarByte: ByteArray) {
        val localCache = FileUtils.instance.getAvatarCachePath(jid)
        if (localCache.exists()) {
            localCache.delete()
        }
        try {
            localCache.writeBytes(avatarByte)
        } catch (e: IOException) {
            Log.e(TAG, "saveContactAvatar failed", e)
        }
    }

    /**
     * get a online avatar
     *
     * @return address
     */
    fun getOnlineAvatar(name: String): String {
        return "https://ui-avatars.com/api/?name=$name"
    }

    companion object {
        private const val TAG = "AvatarUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AvatarUtils()
        }

    }
}
