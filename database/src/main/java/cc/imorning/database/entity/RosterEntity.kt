package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.common.constant.Config
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.roster.packet.RosterPacket

/**
 * roster entity
 */
@Entity(tableName = DatabaseHelper.TABLE_ROSTER)
data class RosterEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "jid", typeAffinity = ColumnInfo.TEXT)
    val jid: String,

    @ColumnInfo(name = "nick", typeAffinity = ColumnInfo.TEXT)
    val nick: String = "",

    @ColumnInfo(name = "mode")
    val mode: Mode = Mode.chat,

    @ColumnInfo(name = "type")
    val type: Message.Type = Message.Type.normal,

    @ColumnInfo(name = "group", typeAffinity = ColumnInfo.TEXT)
    val group: String = Config.DEFAULT_GROUP,

    /**
     * both: both user and sender is friend
     *
     *
     * from: user receive request
     *
     *
     * to: user send request
     *
     *
     * none: not friend
     */
    @ColumnInfo(name = "item_type")
    val item_type: RosterPacket.ItemType,

    @ColumnInfo(name = "is_friend")
    val is_friend: Boolean = false
)
