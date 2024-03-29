package cc.imorning.chat.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.activity.DetailsActivity
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.chat.ui.state.ChatUiState
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.constant.ChatType
import cc.imorning.database.dao.MessageDatabaseDao
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import cc.imorning.database.utils.MessageDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

class ChatViewModel : ViewModel() {

    private val connection = App.getTCPConnection()

    private lateinit var roster: Roster
    private lateinit var presence: Presence
    private lateinit var messageDatabaseDao: MessageDatabaseDao

    private lateinit var chatManager: ChatManager
    private var incomingChatMessageListener: IncomingChatMessageListener? = null
    private var outgoingChatMessageListener: OutgoingChatMessageListener? = null

    private val _chatType = MutableLiveData(ChatType.Type.Unknown)
    val chatType: MutableLiveData<ChatType.Type>
        get() = _chatType

    private val _unreadMsgCount = MutableStateFlow(0)
    val unreadMsgCount: MutableStateFlow<Int>
        get() = _unreadMsgCount

    private val _chatUserId = MutableStateFlow("")
    val chatUserId: MutableStateFlow<String>
        get() = _chatUserId

    private val _userOrGroupName = MutableStateFlow("")
    val userOrGroupName: MutableStateFlow<String>
        get() = _userOrGroupName

    private val _status = MutableStateFlow(Mode.xa)
    val status: StateFlow<Mode>
        get() = _status.asStateFlow()

    private val _historyMessages = MutableStateFlow(emptyList<MessageEntity>())
    val historyMessages: StateFlow<List<MessageEntity>>
        get() = _historyMessages.asStateFlow()

    private val _uiState = MutableStateFlow(
        ChatUiState(
            nickName = "",
            mode = Mode.xa,
            avatarPath = "",
            chatJid = chatUserId.value,
            messageEntity = emptyList()
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun init(context: Context) {
        if (chatUserId.value.isNotEmpty()) {
            messageDatabaseDao = MessageDatabaseHelper.instance.getMessageDB(
                context,
                chatUserId.value,
                App.user
            )!!.databaseDao()
            if (ConnectionManager.isConnectionAvailable()) {
                AvatarUtils.update(context, App.user)
                AvatarUtils.update(context, chatUserId.value)
                viewModelScope.launch {
                    _userOrGroupName.value = RosterAction.getNickName(jidString = chatUserId.value)
                    _status.value = RosterAction.getRosterStatus(jidString = chatUserId.value)
                }
            }
        }
    }

    suspend fun getHistoryMessages() {

        viewModelScope.launch(Dispatchers.IO) {

            val historyTables = messageDatabaseDao.queryMessage()
            val historyMsg = mutableListOf<MessageEntity>()

            for (msg in historyTables) {
                with(msg) {
                    historyMsg.add(
                        MessageEntity(
                            sender = sender,
                            receiver = receiver,
                            messageType = messageType,
                            messageBody = MessageBody(text, image, audio, video, file, action),
                            sendTime = send_time,
                            isShow = is_show,
                            isRecall = is_recall
                        )
                    )
                }
            }
            withContext(Dispatchers.Main) {
                _historyMessages.emit(historyMsg)
            }
        }
    }

    /**
     * Add roster status listener for current friend
     */
    fun initStatusListener() {
        val roster = Roster.getInstanceFor(connection)
        presence = roster.getPresence(JidCreate.bareFrom(_chatUserId.value))
        roster.addRosterListener(object : RosterListener {
            override fun entriesAdded(addresses: MutableCollection<Jid>?) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "entriesAdded: $addresses")
                }
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "entriesUpdated: $addresses")
                }
            }

            override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "entriesDeleted: $addresses")
                }
            }

            override fun presenceChanged(presence: Presence?) {
                presence?.apply {
                    val from = this.from
                    val bestPresence = roster.getPresence(JidCreate.bareFrom(from))
                    _status.value = bestPresence.mode
                }
            }
        })
    }

    fun initMessageListener() {
        chatManager = ChatManager.getInstanceFor(connection)
        incomingChatMessageListener = IncomingChatMessageListener { from, _, _ ->
            if (from.toString() == chatUserId.value) {
                viewModelScope.launch(Dispatchers.IO) { getHistoryMessages() }
            }
        }
        chatManager.addIncomingListener(incomingChatMessageListener)
    }

    fun clearMessageListener() {
        chatManager.removeIncomingListener(incomingChatMessageListener)
    }

    /**
     * start
     */
    fun startDetailsActivity(context: Context, uid: String) {
        if (uid.isEmpty()) {
            return
        }
        val detailsActivity = Intent(context, DetailsActivity::class.java)
        detailsActivity.putExtra(DetailsActivity.KEY_UID, uid)
        detailsActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(detailsActivity)
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}

class ChatViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}