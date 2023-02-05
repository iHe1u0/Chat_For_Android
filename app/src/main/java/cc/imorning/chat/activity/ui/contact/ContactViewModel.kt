package cc.imorning.chat.activity.ui.contact

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config
import cc.imorning.database.dao.DataDatabaseDao
import cc.imorning.database.entity.RosterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.packet.RosterPacket
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val databaseDao: DataDatabaseDao
) : ViewModel() {

    companion object {
        private const val TAG = "ContactViewModel"
    }

    private val connection = CommonApp.xmppTcpConnection

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    // list of all contacts
    private val _friends = MutableStateFlow<List<RosterEntity>>(emptyList())
    val rosters: StateFlow<List<RosterEntity>>
        get() = _friends.asStateFlow()

    // list of all new contacts
    private val _strangers = MutableStateFlow<List<RosterEntity>>(emptyList())
    val newRosters: StateFlow<List<RosterEntity>>
        get() = _strangers.asStateFlow()

    init {
        viewModelScope.launch {
            getRostersFromServer()
            updateRosterView()
        }
    }

    fun updateRosterView() {
        viewModelScope.launch(Dispatchers.IO) {
            val rosterList = databaseDao.queryRosters()
            withContext(Dispatchers.Main) {
                if (rosterList.isNotEmpty()) {
                    // _rosters.value = rosterList
                    val friends = mutableListOf<RosterEntity>()
                    val strangers = mutableListOf<RosterEntity>()
                    for (roster in rosterList) {
                        // if user and roster is both friend
                        if (roster.is_friend) {
                            friends.add(roster)
                        } else {
                            strangers.add(roster)
                        }
                    }
                    _friends.value = friends
                    _strangers.value = strangers
                }
            }
        }
    }

    @Synchronized
    fun getRostersFromServer() {
        // return if isRefreshing
        if (_isRefreshing.value) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.emit(true)
            val rosterList = RosterAction.getRosterList()
            if (rosterList != null && rosterList.isNotEmpty()) {
                for (roster in rosterList) {
                    withContext(Dispatchers.IO) {
                        val jidString = roster.jid.asBareJid().toString()
                        // insert roster into database
                        databaseDao.insertRoster(
                            RosterEntity(
                                jid = jidString,
                                nick = roster.name,
                                group = roster.groups[0].name,
                                type = Message.Type.chat,
                                item_type = roster.type,
                                is_friend = roster.isSubscriptionPending
                            )
                        )
                        // save avatar
                        AvatarUtils.instance.saveAvatar(jidString)
                    }
                }
            }
            delay(500)
            _isRefreshing.emit(false)
        }
    }

    fun acceptSubscribe(jidString: String) {
        if ((connection != null) && connection.isConnected && connection.isAuthenticated) {
            val subscribed = connection.stanzaFactory.buildPresenceStanza()
                .to(jidString)
                .ofType(Presence.Type.subscribed)
                .build()
            connection.sendStanza(subscribed)
            MainScope().launch(Dispatchers.IO) {
                databaseDao.updateRoster(
                    RosterEntity(
                        jid = jidString,
                        nick = RosterAction.getNickName(jidString),
                        type = Message.Type.chat,
                        group = Config.DEFAULT_GROUP,
                        item_type = RosterPacket.ItemType.to,
                        is_friend = true
                    )
                )
                withContext(Dispatchers.Main) {
                    updateRosterView()
                }
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "acceptSubscribe: $jidString")
            }
        }
    }

    fun rejectSubscribe(jidString: String) {
    }
}


class ContactViewModelFactory(
    private val databaseDao: DataDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ContactViewModel(databaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}