package cc.imorning.chat.monitor

import android.util.Log
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction.getNickName
import cc.imorning.common.CommonApp.Companion.getContext
import cc.imorning.common.CommonApp.Companion.xmppTcpConnection
import cc.imorning.common.constant.Config
import cc.imorning.database.db.DataDB.Companion.getInstance
import cc.imorning.database.entity.RosterEntity
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.packet.RosterPacket

object RosterListener {
    private const val TAG = "RosterListener"
    private val databaseDao = getInstance(
        getContext(),
        xmppTcpConnection!!.user.asEntityBareJidString()
    ).databaseDao()

    /**
     * listener for roster change,for more information:
     *
     *
     * [xmpp之添加好友请求报文](https://www.yashinu.com/shili/show-269932.html)
     */
    val rosterListener = StanzaListener { packet: Stanza? ->
        if (packet is Presence) {
            val fromId = packet.from.asBareJid()
            when (packet.type) {
                Presence.Type.subscribe -> {
                    // Write this request into database
                    val roster = RosterEntity(
                        fromId.toString(),
                        getNickName(fromId.toString()),
                        Message.Type.normal,
                        Config.DEFAULT_GROUP,
                        RosterPacket.ItemType.from,
                        false
                    )
                    databaseDao.insertRoster(roster)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 请求加为好友")
                    }
                }
                Presence.Type.subscribed -> {
                    val roster = RosterEntity(
                        fromId.toString(),
                        getNickName(fromId.toString()),
                        Message.Type.normal,
                        Config.DEFAULT_GROUP,
                        RosterPacket.ItemType.from,
                        true
                    )
                    databaseDao.insertRoster(roster)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 同意了好友请求")
                    }
                }
                Presence.Type.unsubscribed -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 拒绝了好友请求")
                    }
                }
                Presence.Type.unsubscribe -> {
                    val roster = RosterEntity(
                        fromId.toString(),
                        getNickName(fromId.toString()),
                        Message.Type.normal,
                        Config.DEFAULT_GROUP,
                        RosterPacket.ItemType.from,
                        false
                    )
                    databaseDao.deleteRoster(roster)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 删除了好友")
                    }
                }
                Presence.Type.unavailable -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 下线")
                    }
                }
                Presence.Type.available -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$fromId 上线")
                    }
                }
                else -> {
                    Log.d(TAG, "unknown type:${packet.type}")
                }
            }
        }
    }
}