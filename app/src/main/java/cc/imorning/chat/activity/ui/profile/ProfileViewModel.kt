package cc.imorning.chat.activity.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.imorning.chat.App
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.AvatarUtils

class ProfileViewModel : ViewModel() {

    private var connection = App.getTCPConnection()
    private var vCard = App.vCard

    private val _avatarPath = MutableLiveData<String>().apply {
        value = if (ConnectionManager.isConnectionAuthenticated(connection)) {
            AvatarUtils.instance.getAvatarPath(null)
        } else {
            null
        }
    }
    val avatarPath: LiveData<String> = _avatarPath

    private val _nickName = MutableLiveData<String>().apply {
        if (vCard != null) {
            value = vCard!!.nickName
        }
    }
    val nickname: LiveData<String> = _nickName

    private val _phoneNumber = MutableLiveData<String>().apply {
        val phoneType = "TEL"
        if (vCard != null &&
            (!vCard!!.getPhoneHome(phoneType).isNullOrEmpty())
        ) {
            value = vCard!!.getPhoneWork(phoneType)
        } else {
            value = "暂未填写手机号"
        }
    }
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _userName = MutableLiveData<String>().apply {
        if (connection.isConnected && connection.isAuthenticated) {
            value = connection.user.asEntityBareJidString()
        } else if (vCard != null) {
            value = vCard!!.jabberId
        }
    }
    val userName: LiveData<String> = _userName

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}