package cc.imorning.chat.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.network.ConnectionManager
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

private const val TAG = "AvatarUtils"

private val connection = App.getTCPConnection()

object AvatarUtils {

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
    @Synchronized
    fun saveAvatar(user: String? = null) {
        if (!ConnectionManager.isConnectionAvailable(connection)) {
            return
        }
        val vCard = RosterAction.getContactVCard(user)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                if (user != null) {
                    saveRosterAvatar(user = user, avatarByte = avatarByte)
                    return
                }
                saveRosterAvatar(App.user, avatarByte)
            }
        }
    }

    fun getAvatarPath(jidString: String? = null): String {
        if (ConnectionManager.isConnectionAvailable(connection)) {
            if (jidString == null) {
                return FileUtils.instance.getAvatarCachePath(App.user).absolutePath
            }
            if (!hasAvatarCache(jidString)) {
                saveAvatar(jidString)
            }
            return FileUtils.instance.getAvatarCachePath(jidString).absolutePath
        }
        return getOnlineAvatar(jidString.orEmpty())
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
        if (ConnectionManager.isConnectionAvailable(connection)) {
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

    fun update(jid: String? = null) {
        saveAvatar(user = jid)
    }

    @Throws(IOException::class)
    private fun saveRosterAvatar(user: String, avatarByte: ByteArray, test: Long = 0L) {
        val localCache = FileUtils.instance.getAvatarCachePath(user)
        MainScope().launch(Dispatchers.IO) {
            if (localCache.exists()) {
                localCache.delete()
            }
            localCache.writeBytes(avatarByte)
        }
    }
}
