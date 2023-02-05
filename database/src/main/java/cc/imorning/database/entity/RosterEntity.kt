package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message
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
    val nick: String,

    @ColumnInfo(name = "type")
    val type: Message.Type = Message.Type.normal,

    @ColumnInfo(name = "group", typeAffinity = ColumnInfo.TEXT)
    val group: String,

    @ColumnInfo(name = "item_type")
    val item_type: RosterPacket.ItemType = RosterPacket.ItemType.none,

    @ColumnInfo(name = "is_friend")
    val is_friend: Boolean = false
)
