package cc.imorning.chat.activity.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.CommonApp
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.AvatarUtils
import com.orhanobut.logger.Logger
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smackx.vcardtemp.VCardManager

class ProfileViewModel : ViewModel() {

    private var connection = CommonApp.getTCPConnection()

    private val _avatarPath = MutableLiveData("")
    private val _nickName = MutableLiveData("")
    private val _phoneNumber = MutableLiveData("")
    private val _jidString = MutableLiveData("")
    private val _status = MutableLiveData("")


    val avatarPath: LiveData<String> = _avatarPath
    val nickname: LiveData<String> = _nickName
    val phoneNumber: LiveData<String> = _phoneNumber
    val jidString: LiveData<String> = _jidString
    val status: LiveData<String> = _status

    fun getUserInfo() {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "get user info failed cause connection in error status")
            }
            return
        }
        val vCard = VCardManager.getInstanceFor(connection)
        val jid = connection.user.asEntityBareJidString()
        val currentUser = vCard.loadVCard(connection.user.asEntityBareJid())
        val roster = Roster.getInstanceFor(connection)
        // for avatar
        if (currentUser.avatar != null) {
            AvatarUtils.instance.saveAvatar(jid)
            _avatarPath.value =
                AvatarUtils.instance.getAvatarPath(jid)
        } else {
            val name = currentUser.firstName
            if (name != null && name.isNotEmpty()) {
                _avatarPath.value = AvatarUtils.instance.getOnlineAvatar(name)
            } else {
                _avatarPath.value = AvatarUtils.instance.getOnlineAvatar(jid)
            }
        }
        // nick name
        _nickName.value = if (currentUser.nickName == null) jid else currentUser.nickName
        // phone number?
        _phoneNumber.value = currentUser.getPhoneHome("VOICE")
        // jid
        _jidString.value = jid
        // status
        val availability = roster.getPresence(connection.user.asBareJid())
        _status.value = StatusHelper(availability.mode).toString()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
