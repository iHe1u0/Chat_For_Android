package cc.imorning.chat.activity.ui.message

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.model.RecentMessage
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.CommonApp
import cc.imorning.common.database.dao.AppDatabaseDao
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.AvatarUtils
import cc.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import javax.inject.Inject
import kotlin.math.log

class MessageViewModel @Inject constructor(
    private val databaseDao: AppDatabaseDao
) : ViewModel() {

    private val connection = CommonApp.getTCPConnection()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _avatarPath = MutableLiveData("")
    val avatarPath: MutableLiveData<String> = _avatarPath

    private val _status = MutableLiveData("在线")
    val status: MutableLiveData<String> = _status

    private val _messages = MutableLiveData<MutableList<RecentMessage>>()
    val messages: MutableLiveData<MutableList<RecentMessage>> = _messages

    private var chatManager: ChatManager? = null
    private var incomingChatMessageListener: IncomingChatMessageListener? = null
    private var outgoingChatMessageListener: OutgoingChatMessageListener? = null

    init {
        updateStatus()
        addListener()
    }

    @Synchronized
    fun updateStatus() {
        MainScope().launch {
            if (ConnectionManager.isConnectionAuthenticated(connection = connection) &&
                NetworkUtils.isNetworkConnected()
            ) {
                AvatarUtils.instance.saveAvatar(connection.user.asEntityBareJidString())
                val roster = Roster.getInstanceFor(connection)
                val availability = roster.getPresence(connection.user.asBareJid())
                _avatarPath.value =
                    AvatarUtils.instance.getAvatarPath(connection.user.asEntityBareJidString())
                _status.value = StatusHelper(availability.mode).toString()
            } else {
                _status.value = StatusHelper(Presence.Mode.xa).toString()
            }
        }
    }

    @Synchronized
    fun refresh(isFromUser: Boolean = false) {
        // return if isRefreshing
        if (_isRefreshing.value) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (isFromUser) {
                _isRefreshing.emit(true)
            }
            val recentMessageEntities = databaseDao.getAllRecentMessages()
            val list = mutableListOf<RecentMessage>()
            if (recentMessageEntities.isNotEmpty()) {
                for (recentMessageEntity in recentMessageEntities) {
                    list.add(
                        RecentMessage(
                            nickName = "${recentMessageEntity.nickName}",
                            sender = recentMessageEntity.sender,
                            message = "${recentMessageEntity.lastMessage}",
                            time = recentMessageEntity.lastMessageTime
                        )
                    )
                }
                withContext(Dispatchers.Main) { _messages.value = list }
            }
            delay(1000)
            _isRefreshing.emit(false)
        }
    }

    @Synchronized
    fun addListener() {
        if (ConnectionManager.isConnectionAuthenticated(connection) &&
            chatManager == null
        ) {
            chatManager = ChatManager.getInstanceFor(connection)
            incomingChatMessageListener = IncomingChatMessageListener { from, message, chat ->
                refresh(false)
            }
            outgoingChatMessageListener = OutgoingChatMessageListener { to, messageBuilder, chat ->
                refresh(false)
            }
        }
        if (chatManager != null) {
            chatManager?.apply {
                addIncomingListener(incomingChatMessageListener)
                addOutgoingListener(outgoingChatMessageListener)
            }
        }
    }

    @Synchronized
    fun removeListener() {
        chatManager?.apply {
            removeIncomingListener(incomingChatMessageListener)
            removeOutgoingListener(outgoingChatMessageListener)
            chatManager = null
        }
    }

    companion object {
        private const val TAG = "MessageViewModel"
    }
}

class MessageViewModelFactory(
    private val appDatabaseDao: AppDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(appDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}