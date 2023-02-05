package cc.imorning.chat.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.R
import cc.imorning.common.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import java.io.IOException

class AvatarUtils private constructor() {

    private val connection: XMPPConnection = App.getTCPConnection()

    /**
     * did jid has avatar cache
     *
     * @return true if cached or false for any other case.
     */
    fun hasAvatarCache(jid: String): Boolean {
        return FileUtils.instance.isFileExist(FileUtils.instance.getAvatarCachePath(jid).absolutePath)
    }

    /**
     * try to get and save avatar for user @param jid
     */
    fun saveAvatar(jidString: String) {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "connection is not authenticated")
            }
            return
        }
        val vCard = RosterAction.getContactVCard(jidString)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                saveContactAvatar(jidString = jidString, avatarByte = avatarByte)
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "avatar is null for $jidString")
            }
        }
    }

    fun getAvatarPath(jidString: String): String {
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            if (!hasAvatarCache(jidString)) {
                saveAvatar(jidString)
            }
            return FileUtils.instance.getAvatarCachePath(jidString).absolutePath
        }
        return getOnlineAvatar(jidString)
    }

    private fun saveContactAvatar(jidString: String, avatarByte: ByteArray) {
        val localCache = FileUtils.instance.getAvatarCachePath(jidString)
        MainScope().launch(Dispatchers.IO) {
            if (localCache.exists()) {
                localCache.delete()
            }
            try {
                localCache.writeBytes(avatarByte)
            } catch (e: IOException) {
                Log.e(TAG, "saveContactAvatar failed", e)
            }
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

    fun getUserBitmap(jidString: String): Bitmap {
        if (connection.isConnected) {
            val vCard = VCardManager.getInstanceFor(connection)
                .loadVCard(JidCreate.entityBareFrom(jidString))
            val byteArray = vCard.avatar
            if (byteArray != null) {
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }
        val drawable = ContextCompat.getDrawable(CommonApp.getContext(), R.drawable.ic_avatar)
        return drawable!!.toBitmap()
        // return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_avatar)
    }

    companion object {
        private const val TAG = "AvatarUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AvatarUtils()
        }

    }
}
