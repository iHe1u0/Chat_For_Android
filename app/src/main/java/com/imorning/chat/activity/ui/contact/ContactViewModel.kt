package com.imorning.chat.activity.ui.contact

import android.util.Log
import androidx.lifecycle.*
import com.imorning.chat.BuildConfig
import com.imorning.common.action.ContactAction
import com.imorning.common.database.UserDatabase
import com.imorning.common.database.dao.UserInfoDao
import com.imorning.common.database.table.UserInfoEntity
import com.imorning.common.exception.OfflineException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.roster.RosterEntry
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val userInfoDao: UserInfoDao) : ViewModel() {

    companion object {
        private const val TAG = "ContactViewModel"
    }

    private val database = UserDatabase.getInstance()
    internal val allContacts: LiveData<List<UserInfoEntity>> =
        database.userInfoDao().getAllContact()

    init {
        viewModelScope.launch {
            try {
                val members = ContactAction.getContactList()
                if (members != null && members.isNotEmpty()) {
                    for (member in members) {
                        withContext(Dispatchers.IO) {
                            userInfoDao.insertContact(
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
    private val userInfoDao: UserInfoDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(userInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}