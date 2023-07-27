package cc.imorning.chat.utils

import android.content.Context
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
    fun hasAvatarCache(context: Context = CommonApp.getContext(), user: String): Boolean {
        return FileUtils.isFileExist(FileUtils.getAvatarCachePath(context, user).absolutePath)
    }

    /**
     * try to get and save avatar for user
     *
     * @param context Context for file operation
     * @param user jid
     *
     */
    @Synchronized
    fun saveAvatar(context: Context = CommonApp.getContext(), user: String? = null) {
        if (!ConnectionManager.isConnectionAvailable()) {
            return
        }
        val vCard = RosterAction.getVCard(user)
        if (vCard != null) {
            val avatarByte = vCard.avatar
            if (avatarByte != null) {
                if (user != null) {
                    saveRosterAvatar(context, user, avatarByte)
                    return
                }
                saveRosterAvatar(context, App.user, avatarByte)
            }
        }
    }

    fun getAvatarPath(context: Context = CommonApp.getContext(), user: String? = null): String {
        if (ConnectionManager.isConnectionAvailable()) {
            if (user == null) {
                return FileUtils.getAvatarCachePath(context, App.user).absolutePath
            }
            if (!hasAvatarCache(context, user)) {
                saveAvatar(context, user)
            }
            return FileUtils.getAvatarCachePath(context, user).absolutePath
        } else {
            if (!user.isNullOrEmpty() && hasAvatarCache(context, user)) {
                return FileUtils.getAvatarCachePath(context, user).absolutePath
            }
        }
        return getOnlineAvatar(user.orEmpty())
    }

    /**
     * Generates an online avatar image URL using the ui-avatars.com API based on the given name.
     *
     * @param name The name for which the avatar will be generated.
     * @return The URL of the generated avatar image.
     */
    fun getOnlineAvatar(name: String): String {
        return "https://ui-avatars.com/api/?background=87cefa&color=0000ff&length=2&name=$name"
    }

    fun getUserBitmap(jidString: String): Bitmap {
        if (ConnectionManager.isConnectionAvailable()) {
            val vCard = VCardManager.getInstanceFor(connection)
                .loadVCard(JidCreate.entityBareFrom(jidString))
            val byteArray = vCard.avatar
            if (byteArray != null) {
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }
        val drawable = ContextCompat.getDrawable(CommonApp.getContext(), R.drawable.ic_avatar)
        return drawable!!.toBitmap()
    }

    fun update(context: Context, user: String? = null) {
        saveAvatar(context, user)
    }

    @Throws(IOException::class)
    private fun saveRosterAvatar(
        context: Context,
        user: String,
        avatarByte: ByteArray,
    ) {
        val localCache = FileUtils.getAvatarCachePath(context, user)
        MainScope().launch(Dispatchers.IO) {
            if (localCache.exists()) {
                localCache.delete()
            }
            localCache.writeBytes(avatarByte)
        }
    }
}
