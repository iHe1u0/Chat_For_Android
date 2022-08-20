package cc.imorning.common.action

import android.util.Log
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.database.AppDatabase
import cc.imorning.common.exception.OfflineException
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.NetworkUtils
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterGroup
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.impl.JidCreate


private const val TAG = "ContactAction"

object ContactAction {

    private val connection = CommonApp.getTCPConnection()
    private val databaseDao = AppDatabase.getInstance().appDatabaseDao()

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
    fun getContactList(): List<RosterEntry>? {
        if (NetworkUtils.isNetworkNotConnected(CommonApp.getContext())) {
            return null
        }
        if (!connection.isConnected) {
            ConnectionManager.connect(connection)
        }
        if (!connection.isAuthenticated) {
            if (BuildConfig.DEBUG){
                Log.d(TAG, "connection is not authenticated")
            }
            return null
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
            }
        }
        return members
    }

    /**
     * get all groups
     */
    fun getGroups(roster: Roster): MutableList<RosterGroup> {
        val groupList: MutableList<RosterGroup> = ArrayList()
        val rosterGroup = roster.groups
        val i: Iterator<RosterGroup> = rosterGroup.iterator()
        while (i.hasNext()) {
            groupList.add(i.next())
        }
        return groupList
    }

    /**
     * Add a group
     */
    fun addGroup(roster: Roster, groupName: String): Boolean {
        return try {
            roster.createGroup(groupName)
            true
        } catch (e: Exception) {
            Log.e(TAG, "add group failed", e)
            false
        }
    }

    /**
     * Get contacts in group
     */
    fun getEntriesByGroup(
        roster: Roster,
        groupName: String
    ): List<RosterEntry> {
        val contacts: MutableList<RosterEntry> = ArrayList()
        val rosterGroup = roster.getGroup(groupName)
        val rosterEntry: Collection<RosterEntry> = rosterGroup.entries
        val item = rosterEntry.iterator()
        while (item.hasNext()) {
            contacts.add(item.next())
        }
        return contacts
    }

    /**
     * Get VCard information
     */
    fun getContactVCard(jid: String): VCard? {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return null
        }
        try {
            val user = JidCreate.entityBareFrom(jid)
            return VCardManager.getInstanceFor(connection).loadVCard(user)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "get user vCard failed", e)
            }
        }
        return null
    }

    /**
     * Add a contact
     *
     * @param jid the jid like admin@hostname
     * @param nickName you will set a alias name for he/she
     * @param groups Groups
     *
     * @return true if add success
     */
    fun addContact(jid: String, nickName: String, groups: List<String>?): Boolean {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return false
        }
        val roster = Roster.getInstanceFor(connection)
        try {
            if (groups != null) {
                roster.createItemAndRequestSubscription(
                    JidCreate.entityBareFrom(jid),
                    nickName,
                    groups.toTypedArray()
                )
            } else {
                roster.createItem(
                    JidCreate.entityBareFrom(jid),
                    nickName,
                    null
                )
            }
            return true
        } catch (e: Exception) {
            Log.e(TAG, "add user failed", e)
        }
        return false
    }

    /**
     * delete a contact
     */
    fun removeContact(roster: Roster, jid: String): Boolean {
        try {
            val entry = roster.getEntry(JidCreate.entityBareFrom(jid))
            roster.removeEntry(entry)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "remove user failed", e)
        }
        return false
    }
}