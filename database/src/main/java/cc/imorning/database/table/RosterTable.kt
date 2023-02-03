package cc.imorning.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message.Type

/**
 * roster table
 */
@Entity(tableName = DatabaseHelper.TABLE_ROSTER)
data class RosterTable(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "jid", typeAffinity = ColumnInfo.TEXT)
    val jid: String,

    @ColumnInfo(name = "nick", typeAffinity = ColumnInfo.TEXT)
    val nick: String,

    @ColumnInfo(name = "type")
    val type: Type = Type.normal,

    @ColumnInfo(name = "group", typeAffinity = ColumnInfo.TEXT)
    val group: String,

    )
