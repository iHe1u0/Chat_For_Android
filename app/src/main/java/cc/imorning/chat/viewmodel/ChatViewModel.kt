package cc.imorning.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.imorning.common.action.UserAction
import cc.imorning.common.constant.ChatType
import cc.imorning.common.database.dao.AppDatabaseDao
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val appDatabaseDao: AppDatabaseDao
) : ViewModel() {

    private val _chatType = MutableLiveData(ChatType.Type.Unknown)
    val chatType: MutableLiveData<ChatType.Type>
        get() = _chatType

    private val _unreadMsgCount = MutableLiveData(0)
    val unreadMsgCount: MutableLiveData<Int>
        get() = _unreadMsgCount

    private val _chatUserId = MutableLiveData("")
    val chatUserId: MutableLiveData<String>
        get() = _chatUserId

    private val _userOrGroupName = MutableLiveData("")
    val userOrGroupName: MutableLiveData<String>
        get() = _chatUserId

    private val _userOrGroupStatus = MutableLiveData("")
    val userOrGroupStatus: MutableLiveData<String>
        get() = _userOrGroupStatus

    fun setChatType(chatType: ChatType.Type) {
        _chatType.value = chatType
        when (chatType) {
            ChatType.Type.Unknown -> {
                _userOrGroupStatus.value = "未知"
            }
            ChatType.Type.Single -> {
                _userOrGroupStatus.value = chatType.name
            }
            ChatType.Type.Group -> {
                _userOrGroupName.value = "${chatType.ordinal} 个成员"
            }
        }
    }

    fun setUnreadCount(count: Int) {
        _unreadMsgCount.value = count
    }

    fun setChatUserId(userJidString: String) {
        _chatUserId.value = userJidString
    }

    fun getUserOrGroupName(): String? {
        if (_chatUserId.value == null || _chatUserId.value.isNullOrEmpty()) {
            return ""
        }
        return UserAction.getNickName(_chatUserId.value!!)
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}

class ChatViewModelFactory(
    private val appDatabaseDao: AppDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(appDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}