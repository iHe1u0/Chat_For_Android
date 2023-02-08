package cc.imorning.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.ChatType
import cc.imorning.database.dao.MessageDatabaseDao
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import cc.imorning.database.utils.MessageDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import javax.inject.Inject

class ChatViewModel @Inject constructor() : ViewModel() {

    private val connection = CommonApp.xmppTcpConnection

    private lateinit var roster: Roster
    private lateinit var presence: Presence
    private lateinit var messageDatabaseDao: MessageDatabaseDao
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
    val historyMessages: MutableStateFlow<List<MessageEntity>>
        get() = _historyMessages

    fun init() {
        if (chatUserId.value.isEmpty() || connection == null) {
            Log.d(TAG, "init failed")
            return
        }
        _userOrGroupName.value = RosterAction.getNickName(jidString = chatUserId.value)
        _status.value = RosterAction.getRosterStatus(jidString = chatUserId.value)
        // messageDatabaseDao = MessageDB.getInstance(
        //     context = CommonApp.getContext(),
        //     user = chatUserId.value,
        //     me = connection.user.asEntityBareJidString()
        // ).databaseDao()
        messageDatabaseDao = MessageDatabaseHelper.instance!!.getMessageDB(
            CommonApp.getContext(),
            chatUserId.value,
            connection.user.asEntityBareJidString()
        )!!.databaseDao()
        incomingChatMessageListener = IncomingChatMessageListener { from, message, chat ->
            if (from.asEntityBareJidString() != chatUserId.value) {
                return@IncomingChatMessageListener
            }
            getHistoryMessages()
            // val messageEntity = Gson().fromJson(message.body, MessageEntity::class.java)
            // getHistoryMessages()
        }
    }

    fun getHistoryMessages() {
        MainScope().launch(Dispatchers.IO) {

            val historyTables = messageDatabaseDao.queryMessage()
            val historyMsg = mutableListOf<MessageEntity>()

            for (msg in historyTables) {
                with(msg) {
                    historyMsg.add(
                        MessageEntity(
                            sender = sender,
                            receiver = receiver,
                            messageType = message_type,
                            messageBody = MessageBody(text, image, audio, video, file, action),
                            sendTime = send_time,
                            isShow = is_show,
                            isRecall = is_recall
                        )
                    )
                }
            }
            with(Dispatchers.Main) {
                _historyMessages.value = historyMsg
            }
        }
    }

    /**
     * Add roster status listener for current friend
     */
    fun addStatusListener() {
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

    fun initIncomeListener() {

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