package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper

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
    val type: org.jivesoftware.smack.packet.Message.Type = org.jivesoftware.smack.packet.Message.Type.normal,

    @ColumnInfo(name = "group", typeAffinity = ColumnInfo.TEXT)
    val group: String,

    @ColumnInfo(name = "status")
    val status: org.jivesoftware.smack.packet.Presence.Type = org.jivesoftware.smack.packet.Presence.Type.unsubscribe
)
