package com.imorning.chat.activity.ui.contact

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imorning.chat.BuildConfig
import com.imorning.common.action.ContactAction
import com.imorning.common.database.dao.UserInfoDao
import com.imorning.common.database.table.UserInfoEntity
import kotlinx.coroutines.flow.Flow
import org.jivesoftware.smack.roster.RosterEntry

class ContactViewModel(private val userInfoDao: UserInfoDao) : ViewModel() {

    companion object {
        private const val TAG = "ContactViewModel"
    }

    private val _members = MutableLiveData<List<RosterEntry>>().apply {
        value = ContactAction.getContactList()
    }
    val text: LiveData<List<RosterEntry>> = _members

    fun queryAll(): Flow<List<UserInfoEntity>> {
        return userInfoDao.getAllContact()
    }

    suspend fun insert(members: List<RosterEntry>) {
        if (members.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "insert size is null or zero")
            }
            return
        }
        for (member in members) {
            userInfoDao.insertContact(
                UserInfoEntity(
                    jid = member.jid.asUnescapedString(),
                    username = member.name
                )
            )
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