package cc.imorning.chat.activity.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.model.RecentMessage
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.utils.NetworkUtils
import cc.imorning.database.dao.RecentDatabaseDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster

class MessageViewModel(
    private val databaseDao: RecentDatabaseDao
) : ViewModel() {

    private val connection = App.getTCPConnection()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _avatarPath = MutableLiveData("")
    val avatarPath: MutableLiveData<String> = _avatarPath

    private val _nickName = MutableStateFlow("USER")
    val nickName: StateFlow<String> = _nickName

    private val _status = MutableStateFlow("ONLINE")
    val status: StateFlow<String> = _status

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
            if (ConnectionManager.isConnectionAvailable(connection = connection) &&
                NetworkUtils.isNetworkConnected()
            ) {
                MainScope().launch(Dispatchers.IO) {
                    AvatarUtils.instance.saveAvatar(connection.user.asEntityBareJidString())
                }
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
    fun updateView(isFromUser: Boolean = false) {
        // return if isRefreshing
        if (_isRefreshing.value) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            // if this update from user,then show the dialog
            if (isFromUser) {
                _isRefreshing.emit(true)
            }
            val messages = databaseDao.queryRecentMessage()
            val list = mutableListOf<RecentMessage>()
            if (messages.isNotEmpty()) {
                for (message in messages) {
                    list.add(
                        RecentMessage(
                            nickName = "${message.nickName}",
                            user = message.sender,
                            message = "${message.message}",
                            time = message.time
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
        if (ConnectionManager.isConnectionAvailable(connection) &&
            chatManager == null
        ) {
            chatManager = ChatManager.getInstanceFor(connection)
            incomingChatMessageListener = IncomingChatMessageListener { _, _, _ ->
                updateView(false)
            }
            outgoingChatMessageListener = OutgoingChatMessageListener { _, _, _ ->
                updateView(false)
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

    fun updateUser() {
        if (connection.isAuthenticated) {
            val jid = connection.user.asEntityBareJidString()
            _nickName.value = RosterAction.getNickName(jidString = jid)
            _status.value = StatusHelper(RosterAction.getRosterStatus(jidString = jid)).toString()
        }
    }

    companion object {
        private const val TAG = "MessageViewModel"
    }
}

class MessageViewModelFactory(
    private val recentDatabaseDao: RecentDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(recentDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}