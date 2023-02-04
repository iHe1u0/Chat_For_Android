package cc.imorning.chat.activity.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.CommonApp
import cc.imorning.database.dao.DataDatabaseDao
import cc.imorning.database.entity.RosterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.packet.Message
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
    private val _rosters = MutableStateFlow<List<RosterEntity>>(emptyList())
    val contacts: StateFlow<List<RosterEntity>>
        get() = _rosters.asStateFlow()

    // list of all new contacts
    private val _newRosters = MutableStateFlow<List<RosterEntity>>(emptyList())
    val newContacts: StateFlow<List<RosterEntity>>
        get() = _newRosters.asStateFlow()

    init {
        viewModelScope.launch {
            getRostersFromServer()
        }
    }

    private fun updateView() {
        viewModelScope.launch(Dispatchers.IO) {
            val rosterList = databaseDao.queryRosters()
            withContext(Dispatchers.Main) {
                if (rosterList.isNotEmpty()) {
                    _rosters.value = rosterList
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
                            )
                        )
                        // save avatar
                        AvatarUtils.instance.saveAvatar(jidString)
                    }
                }
                updateView()
            }
            delay(1000)
            _isRefreshing.emit(false)
        }
    }

}


class ContactViewModelFactory(
    private val databaseDao: DataDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(databaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}