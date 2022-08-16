package cc.imorning.common.utils

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import cc.imorning.common.R
import cc.imorning.common.action.ContactAction
import cc.imorning.common.manager.ConnectionManager
import org.jivesoftware.smack.XMPPConnection
import java.io.IOException

class AvatarUtils {

    private val connection: XMPPConnection = App.getTCPConnection()

    fun cacheAvatar(jid: String): String? {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG){
                Log.d(TAG, "connection is not authenticated")
            }
            return null
        }
        val vCard = ContactAction.getContactVCard(jid)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                return saveContactAvatar(jid = jid, avatarByte = avatarByte)
            }
        }
        if (BuildConfig.DEBUG){
            Log.d(TAG, "$jid didn't have avatar")
        }
        return null
    }

    fun cacheUserAvatar(avatarByte: ByteArray) {
        val localCache =
            FileUtils.instance.getAvatarCachePath(connection.user.asEntityBareJidString())
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
                return FileUtils.instance.getAvatarCachePath(connection.user.asEntityBareJidString()).absolutePath
            }
            return null
        }
        return FileUtils.instance.getAvatarCachePath(jid).absolutePath
    }

    private fun saveContactAvatar(jid: String, avatarByte: ByteArray): String? {
        val localCache = FileUtils.instance.getAvatarCachePath(jid)
        if (localCache.exists()) {
            localCache.delete()
        }
        try {
            localCache.writeBytes(avatarByte)
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "cacheAvatar: $jid success")
            }
            return localCache.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "saveContactAvatar failed", e)
        }
        return null
    }

    fun getDefaultAvatar(): Drawable? {
        return ResourcesCompat.getDrawable(
            App.getContext().resources, R.drawable.ic_default_avatar, null
        )
    }


    companion object {
        private const val TAG = "AvatarUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AvatarUtils()
        }

    }
}