package cc.imorning.chat.activity.ui.contact

import android.util.Log
import androidx.lifecycle.*
import cc.imorning.chat.BuildConfig
import cc.imorning.common.action.ContactAction
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.database.dao.AppDatabaseDao
import cc.imorning.common.database.table.UserInfoEntity
import cc.imorning.common.exception.OfflineException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val appDatabaseDao: AppDatabaseDao) : ViewModel() {

    companion object {
        private const val TAG = "ContactViewModel"
    }

    private val database = AppDatabase.getInstance()
    internal val allContacts: LiveData<List<UserInfoEntity>> =
        database.userInfoDao().getAllContact()

    init {
        viewModelScope.launch {
            try {
                val members = ContactAction.getContactList()
                if (members != null && members.isNotEmpty()) {
                    for (member in members) {
                        withContext(Dispatchers.IO) {
                            appDatabaseDao.insertContact(
                                UserInfoEntity(
                                    jid = member.jid.asUnescapedString(),
                                    username = member.name
                                )
                            )
                        }
                    }
                }
            } catch (e: OfflineException) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "user offline")
                }
            }
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