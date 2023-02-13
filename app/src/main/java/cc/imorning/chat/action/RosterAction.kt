package cc.imorning.chat.action

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config
import cc.imorning.common.exception.OfflineException
import cc.imorning.common.utils.NetworkUtils
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterGroup
import org.jivesoftware.smackx.search.UserSearchManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jivesoftware.smackx.xdata.form.Form
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.impl.JidCreate


private const val TAG = "RosterAction"

object RosterAction {

    private val connection = App.getTCPConnection()

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
    fun getRosterList(): List<RosterEntry>? {
        if (NetworkUtils.isNetworkNotConnected(CommonApp.getContext())) {
            return null
        }
        if (!connection.isConnected) {
            ConnectionManager.connect(connection)
        }
        if (!connection.isAuthenticated) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "connection is not authenticated")
            }
            return null
        }
        val roster: Roster = Roster.getInstanceFor(connection)
        roster.subscriptionMode = Roster.SubscriptionMode.manual
        // 刷新列表
        if (!roster.isLoaded) {
            roster.reloadAndWait()
        }
        val rosterGroups = roster.groups
        val members: MutableList<RosterEntry> = ArrayList()
        // 遍历群组
        for (group in rosterGroups) {
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
     *
     * @param jidString a user jid without resource
     */
    fun getContactVCard(jidString: String?): VCard? {
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            if (jidString == null) {
                val vCardManager = VCardManager.getInstanceFor(connection)
                return vCardManager.loadVCard()
            }
            if (jidString.isNotEmpty()) {
                val user = JidCreate.entityBareFrom(jidString)
                return VCardManager.getInstanceFor(connection).loadVCard(user)
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
    fun addRoster(
        jid: String,
        nickName: String,
        groups: Array<String>? = arrayOf(Config.DEFAULT_GROUP)
    ): Boolean {
        if (!ConnectionManager.isConnectionAuthenticated(connection)) {
            return false
        }
        val roster = Roster.getInstanceFor(connection)
        try {
            val bareJid = JidCreate.bareFrom(jid)
            roster.createItemAndRequestSubscription(bareJid, nickName, groups)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "add roster failed", e)
        }
        return false
    }

    /**
     * delete a contact
     */
    fun removeRoster(roster: Roster, jid: BareJid): Boolean {
        try {
            val entry = roster.getEntry(jid)
            roster.removeEntry(entry)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "remove user failed", e)
        }
        return false
    }

    /**
     * get contact's nick name by jid like im@test.com
     */
    fun getNickName(jidString: String? = null): String {
        if (jidString == null) {
            val vCardManager = VCardManager.getInstanceFor(connection)
            val vCard = vCardManager.loadVCard()
            return vCard.nickName
        }
        if (jidString.isEmpty()) {
            return ""
        }
        val vCard = getContactVCard(jidString)
        if (vCard != null && !vCard.nickName.isNullOrEmpty()) {
            return vCard.nickName.orEmpty()
        }
        val rosterEntry = Roster.getInstanceFor(connection)
        if (rosterEntry != null) {
            val roster = rosterEntry.getEntry(JidCreate.bareFrom(jidString))
            if (roster != null) {
                return roster.name
            }
        }
        return JidCreate.bareFrom(jidString).localpartOrNull.toString()

    }

    /**
     * Search user by key
     */
    fun search(key: String?): MutableList<SearchResult>? {
        if (key == null) {
            return null
        }
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            val userSearchManager = UserSearchManager(connection)
            for (service in userSearchManager.searchServices) {
                try {
                    val searchDataForm = userSearchManager.getSearchForm(service)
                    val searchForm = Form(searchDataForm)
                    val requestForm = searchForm.fillableForm
                    if (BuildConfig.DEBUG) {
                        val supportName = StringBuilder()
                        for (supportFieldName in searchDataForm.fields) {
                            supportName.append("${supportFieldName.fieldName} ")
                        }
                    }
                    requestForm.setAnswer("search", key)
                    requestForm.setAnswer("Username", true)
                    requestForm.setAnswer("Name", true)
                    requestForm.setAnswer("Email", true)
                    val reportedData =
                        userSearchManager.getSearchResults(requestForm.dataFormToSubmit, service)
                    if (reportedData != null) {
                        val results: MutableList<SearchResult> = mutableListOf()
                        for (row in reportedData.rows) {
                            results.add(
                                SearchResult(
                                    jid = row.getValues("jid")[0].toString(),
                                    username = row.getValues("Username")[0].toString(),
                                    name = row.getValues("Name")[0].toString(),
                                    email = row.getValues("Email")[0].toString()
                                )
                            )
                        }
                        return results
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "search user failed cause ${e.localizedMessage}")
                    }
                    continue
                }
            }
        }
        return null
    }

    /**
     * return if fromId is user's friend
     */
    fun isFriend(fromId: BareJid): Boolean {
        if (fromId.isEmpty()) {
            return false
        }
        val roster = Roster.getInstanceFor(connection)
        return roster.isSubscribedToMyPresence(fromId)
    }

    fun isFriend(fromId: String): Boolean {
        if (fromId.isEmpty()) {
            return false
        }
        return isFriend(JidCreate.bareFrom(fromId))
    }

    suspend fun accept(jidString: String, rosterNickName: String) {
        if (jidString.isEmpty()) {
            return
        }
        if (connection.isConnected && connection.isAuthenticated) {
            val subscribed = connection.stanzaFactory.buildPresenceStanza()
                .to(jidString)
                .ofType(Presence.Type.subscribed)
                .build()
            connection.sendStanza(subscribed)
            addRoster(jidString, rosterNickName)
        }
    }

    suspend fun reject(jidString: String) {
        if (connection.isConnected && connection.isAuthenticated) {
            val subscribed = connection.stanzaFactory.buildPresenceStanza()
                .to(jidString)
                .ofType(Presence.Type.unsubscribe)
                .build()
            connection.sendStanza(subscribed)
        }
    }

    fun getRosterStatus(jidString: String?): Mode {
        if (jidString.isNullOrEmpty()) {
            return Mode.xa
        }
        val roster = Roster.getInstanceFor(connection)
        val presence: Presence = roster.getPresence(JidCreate.bareFrom(jidString))
        return presence.mode

    }

    /**
     * update current user mode
     */
    fun updateMode(mode: Mode) {
        val presence = PresenceBuilder
            .buildPresence()
            .setMode(mode)
            .build()
        connection.sendStanza(presence)
    }

    /**
     * update current user nick name with [nickName]
     *
     * @param nickName new nick name
     */
    fun updateNickName(nickName: String) {
        val vCardManager = VCardManager.getInstanceFor(connection)
        val me = vCardManager.loadVCard()
        me.nickName = nickName
        vCardManager.saveVCard(me)
    }

    /**
     * update current user nick name with [phoneNumber]
     *
     * @param phoneNumber new phone number
     */
    fun updatePhoneNumber(phoneNumber: String) {
        val vCardManager = VCardManager.getInstanceFor(connection)
        val me = vCardManager.loadVCard()
        me.setPhoneWork(Config.PHONE, phoneNumber)
        vCardManager.saveVCard(me)
    }

}

/**
 * This class used to save search result from server
 *
 * @see <a href="https://xmpp.org/extensions/xep-0055.html#registrar-formtypes">Field Standardization</a>
 *
 */
data class SearchResult(
    val jid: String,
    val username: String,
    val name: String?,
    val email: String?
)
