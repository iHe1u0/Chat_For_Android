package cc.imorning.chat.activity.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.constant.Config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jivesoftware.smackx.vcardtemp.VCardManager

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

    fun getUserInfo() {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "get user info failed cause connection in error status")
            }
            return
        }
        val vCard = VCardManager.getInstanceFor(connection)
        val jid = connection.user.asBareJid()
        val currentUser = vCard.loadVCard(jid.asEntityBareJidIfPossible())
        // for avatar
        if (currentUser.avatar != null) {
            AvatarUtils.instance.saveAvatar(jid.toString())
            _avatarPath.value =
                AvatarUtils.instance.getAvatarPath(jid.toString())
        }
        val name = RosterAction.getNickName(jidString = jid.toString())
        if (name.isEmpty()) {
            _nickName.value = jid.toString()
            if (currentUser.avatar == null) {
                _avatarPath.value = AvatarUtils.instance.getOnlineAvatar(jid.toString())
            }
        } else {
            _nickName.value = RosterAction.getNickName(_jidString.value)
            if (currentUser.avatar == null) {
                _avatarPath.value = AvatarUtils.instance.getOnlineAvatar(name)
            }
        }
        // jid
        _jidString.value = jid.toString()
        // status
        _status.value = StatusHelper(RosterAction.getRosterStatus(jid.toString())).toString()
        // phone number
        _phoneNumber.value = currentUser.getPhoneWork(Config.PHONE).orEmpty()
    }

    fun updateAvatar(uri: Uri?) {
        Log.d(TAG, "updateAvatar: $uri")
        if (uri == null) {
            return
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
