package com.imorning.common.action

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.BuildConfig
import com.imorning.common.database.UserDatabase
import com.imorning.common.database.table.UserInfoEntity
import com.imorning.common.exception.OfflineException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry


private const val TAG = "Contact"

object Contact {

    private val connection = App.getTCPConnection()
    private val databaseDao = UserDatabase.getInstance().userInfoDao()

    /**
     * 获取好友列表
     *
     * @return 好友列表（带分组）
     */
    @Synchronized
    fun getContactListWithGroup(): Map<String, List<Any>> {
        if (!connection.isConnected || !connection.isAuthenticated) {
            throw OfflineException(msg = "登录过期")
        }
        val roster: Roster = Roster.getInstanceFor(connection)
        // 刷新列表
        if (!roster.isLoaded) {
            roster.reloadAndWait()
        }
        val entriesGroup = roster.groups
        val contactList: MutableMap<String, List<Any>> = HashMap()
        val groupList: MutableList<String> = ArrayList()
        val listGroupMember: MutableList<Any> = ArrayList()
        // 遍历群组
        for (group in entriesGroup) {
            val entries: Collection<RosterEntry> = group.entries
            groupList.add(group.name)
            val groupMember: MutableList<RosterEntry> = ArrayList()
            // 遍历群组里面的联系人
            for (rosterEntry in entries) {
                groupMember.add(rosterEntry)
                if (BuildConfig.DEBUG) {
                    Log.d(
                        TAG,
                        "${rosterEntry.name} ${rosterEntry.jid} ${rosterEntry.groups} ${rosterEntry.isApproved}"
                    )
                }
            }
            listGroupMember.add(groupMember)
        }
        contactList["groupName"] = groupList
        contactList["groupMember"] = listGroupMember
        return contactList
    }

    /**
     * 获取好友列表
     *
     * @return 好友列表
     */
    @Synchronized
    fun getContactList(): List<RosterEntry> {
        if (!connection.isConnected || !connection.isAuthenticated) {
            throw OfflineException(msg = "登录过期")
        }
        val roster: Roster = Roster.getInstanceFor(connection)
        // 刷新列表
        if (!roster.isLoaded) {
            roster.reloadAndWait()
        }
        val entriesGroup = roster.groups
        val members: MutableList<RosterEntry> = ArrayList()
        // 遍历群组
        for (group in entriesGroup) {
            val entries: Collection<RosterEntry> = group.entries
            // 遍历群组里面的联系人
            for (rosterEntry in entries) {
                members.add(rosterEntry)
                if (BuildConfig.DEBUG) {
                    Log.d(
                        TAG,
                        "用户名：${rosterEntry.name} JID：${rosterEntry.jid} 状态：${rosterEntry.isApproved}"
                    )
                }
            }
        }
        return members
    }


}