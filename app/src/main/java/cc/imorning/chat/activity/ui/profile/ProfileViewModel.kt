package cc.imorning.chat.activity.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.imorning.chat.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.manager.ConnectionManager
import com.orhanobut.logger.Logger
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
        val currentUser = vCard.loadVCard()
        Logger.xml(currentUser.toXML().toString())
        _phoneNumber.value = "2022"
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
