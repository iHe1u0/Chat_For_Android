package cc.imorning.chat.activity.ui.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.model.Contact
import cc.imorning.common.action.UserAction
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.database.dao.AppDatabaseDao
import cc.imorning.common.database.table.UserInfoEntity
import cc.imorning.common.utils.AvatarUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val appDatabaseDao: AppDatabaseDao
) : ViewModel() {

    companion object {
        private const val TAG = "ContactViewModel"
    }

    private val database = AppDatabase.getInstance()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    // list of all contacts
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>>
        get() = _contacts.asStateFlow()
    internal val allContacts: LiveData<List<UserInfoEntity>> =
        database.appDatabaseDao().getAllContact()

    // list of all new contacts
    private val _newContacts = MutableStateFlow<List<Contact>>(emptyList())
    val newContacts: StateFlow<List<Contact>>
        get() = _newContacts.asStateFlow()


    init {
        refresh()
    }

    @Synchronized
    fun refresh() {
        // return if isRefreshing
        if (_isRefreshing.value) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.emit(true)
            val members = UserAction.getContactList()
            val contactList = ArrayList<Contact>()
            if ((members != null) && members.isNotEmpty()) {
                for (member in members) {
                    withContext(Dispatchers.IO) {
                        val jidString = member.jid.asUnescapedString()
                        val nickName = member.name
                        // insert contact into database
                        appDatabaseDao.insertContact(
                            UserInfoEntity(
                                jid = jidString,
                                username = nickName
                            )
                        )
                        // save contact's avatar
                        AvatarUtils.instance.saveAvatar(jidString)
                        // show for ui
                        contactList.add(
                            Contact(
                                nickName = nickName,
                                jid = jidString,
                                status = 0
                            )
                        )
                    }
                }
            }
            _contacts.emit(contactList)
            delay(1000)
            _isRefreshing.emit(false)
        }
    }

}


class ContactViewModelFactory(
    private val appDatabaseDao: AppDatabaseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(appDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}