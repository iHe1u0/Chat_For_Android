package cc.imorning.chat.activity.ui.profile

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.imorning.chat.App
import cc.imorning.chat.R
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


    private val _nickName = MutableLiveData<String>().apply {
        if (vCard != null) {
            vCard!!.let { vcard ->
                if (vcard.nickName.isNullOrBlank()) {
                    value = connection.user.asEntityBareJidString().split("@")[0]
                } else {
                    value = vcard.nickName
                }
            }
        }
    }
    private val _phoneNumber = MutableLiveData<String>().apply {
        val phoneType = "TEL"
        if (vCard != null &&
            (!vCard!!.getPhoneHome(phoneType).isNullOrEmpty())
        ) {
            value = vCard!!.getPhoneWork(phoneType)
        } else {
            value = "暂未设置手机号"
        }
    }


    private val _userName = MutableLiveData<String>().apply {
        if (connection.isConnected && connection.isAuthenticated) {
            value = connection.user.asEntityBareJidString()
        } else if (vCard != null) {
            value = vCard!!.jabberId
        }
    }

    private val _status = MutableLiveData<String>().apply {
        if (connection.isConnected && connection.isAuthenticated) {
            val presenceBuilder = connection.stanzaFactory.buildPresenceStanza()
            value = presenceBuilder.build().status
            Log.i(TAG, presenceBuilder.toString())
        }
    }


    val avatarPath: LiveData<String> = _avatarPath
    val nickname: LiveData<String> = _nickName
    val phoneNumber: LiveData<String> = _phoneNumber
    val userName: LiveData<String> = _userName
    val status: LiveData<String> = _status

    private var userId: String = ""

    fun getUserInfo() {
        // Workaround for simplicity
        // _userData.value =  meProfile
        _phoneNumber.value = "2022"
    }

    private val _userData = MutableLiveData<ProfileScreenState>()
    val userData: LiveData<ProfileScreenState> = _userData

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}

@Immutable
data class ProfileScreenState(
    val userId: String,
    @DrawableRes val photo: Int?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?, // Null if me
    val commonChannels: String? // Null if me
) {
    fun isMe() = userId == meProfile.userId
}

val meProfile = ProfileScreenState(
    userId = "me",
    photo = R.drawable.ic_default_avatar,
    name = "Ali Conors",
    status = "Online",
    displayName = "aliconors",
    position = "Senior Android Dev at Yearin\nGoogle Developer Expert",
    twitter = "twitter.com/aliconors",
    timeZone = "In your timezone",
    commonChannels = null
)
