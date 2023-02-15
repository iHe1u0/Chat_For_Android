package cc.imorning.chat.activity.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.R
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.ui.view.ToastUtils
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jivesoftware.smackx.vcardtemp.VCardManager
import java.io.File

class ProfileViewModel : ViewModel() {

    private var connection = App.getTCPConnection()

    private val _avatarPath = MutableStateFlow("")
    private val _nickName = MutableStateFlow("null")
    private val _phoneNumber = MutableStateFlow("")
    private val _jidString = MutableStateFlow("")
    private val _status = MutableStateFlow("")


    val avatarPath: StateFlow<String> = _avatarPath
    val nickname: StateFlow<String> = _nickName
    val phoneNumber: StateFlow<String> = _phoneNumber
    val jidString: StateFlow<String> = _jidString
    val status: StateFlow<String> = _status

    suspend fun updateUserConfigure() {
        if (!connection.isConnected || !connection.isAuthenticated) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "get user info failed cause connection in error status")
            }
            return
        }
        val vCard = VCardManager.getInstanceFor(connection)
        val jid = connection.user.asBareJid()
        val currentUser = vCard.loadVCard()
        val name = RosterAction.getNickName()
        if (name.isEmpty()) {
            _nickName.value = jid.toString()
        } else {
            _nickName.value = name
        }
        if (currentUser.avatar != null) {
            AvatarUtils.instance.saveAvatar()
            _avatarPath.value =
                AvatarUtils.instance.getAvatarPath()
        } else {
            _avatarPath.value = AvatarUtils.instance.getOnlineAvatar(jid.toString())
        }
        // jid
        _jidString.value = jid.toString()
        // status
        _status.value = StatusHelper(RosterAction.getRosterStatus(jid.toString())).toString()
        // phone number
        _phoneNumber.value = currentUser.getPhoneWork(Config.PHONE).orEmpty()
    }

    /**
     * update user's avatar
     *
     * @param path picture file path
     */
    fun updateAvatar(context: Context, path: String) {
        val file = File(path)
        if (file.exists() && connection.isAuthenticated) {
            val avatarFile: File = FileUtils.instance.compressImage(file)
            if (avatarFile.length() / 1024 > 1024) {
                ToastUtils.showMessage(context, context.getString(R.string.file_too_large))
                return
            }
            viewModelScope.launch(Dispatchers.IO) {
                val vCardManager = VCardManager.getInstanceFor(connection)
                val vCard = vCardManager.loadVCard()
                vCard.avatar = FileUtils.instance.getFileBytes(avatarFile)
                try {
                    vCardManager.saveVCard(vCard)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "updateAvatar: ${e.message}", e)
                    }
                }
                updateUserConfigure()
            }
        }
    }

    fun updateNickName(newName: String) {
        RosterAction.updateNickName(newName)
        viewModelScope.launch(Dispatchers.IO) {
            updateUserConfigure()
        }
    }

    fun updatePhoneNumber(newPhoneNum: String) {
        RosterAction.updatePhoneNumber(newPhoneNum)
        viewModelScope.launch(Dispatchers.IO) {
            updateUserConfigure()
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
