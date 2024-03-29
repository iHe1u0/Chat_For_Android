package cc.imorning.chat.model

import android.util.Log
import cc.imorning.chat.App.Companion.getTCPConnection
import cc.imorning.chat.network.ConnectionManager.isConnectionAvailable
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.common.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

class User(private val jidString: String) {

    lateinit var jid: Jid
    var nickName: String? = null
    var aliasName: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var name: String? = null

    /**
     * Phone number of home's VOICE
     *
     * @see VCard.getPhoneHome
     */
    var phoneNumber: String? = null

    /**
     * Email from home
     *
     * @see VCard.getEmailHome
     */
    var email: String? = ""

    var homeAddress: String? = ""

    var workAddress: String? = ""

    var isFriend: Boolean = false

    /**
     * @see Roster.getPresence
     */
    var status: Presence.Mode? = null

    init {
        val jid = JidCreate.entityBareFrom(jidString)
        val connection = getTCPConnection()
        if (isConnectionAvailable(connection)) {
            try {
                val vCard = VCardManager.getInstanceFor(connection).loadVCard(jid)
                MainScope().launch(Dispatchers.IO) { AvatarUtils.saveAvatar(user = jidString) }
                nickName = vCard.nickName
                email = vCard.emailHome
                phoneNumber = vCard.getPhoneHome("VOICE")
                firstName = vCard.firstName
                lastName = vCard.lastName
                homeAddress = vCard.getAddressFieldHome("")
                workAddress = vCard.getAddressFieldWork("")
                name = vCard.lastName.orEmpty() + vCard.firstName.orEmpty()
                val roster = Roster.getInstanceFor(connection)
                val rosterEntry = roster.getEntry(jid)
                if (rosterEntry != null) {
                    aliasName = rosterEntry.name
                    status = roster.getPresence(jid).mode
                    isFriend = true
                } else {
                    isFriend = false
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "construct User failed: ${e.localizedMessage}", e)
                }
            }
        }
    }

    companion object {
        private const val TAG = "User"
    }
}