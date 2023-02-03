package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message.Type
import org.joda.time.DateTime

@Entity(tableName = DatabaseHelper.TABLE_RECENT_MESSAGE)
data class RecentMessageEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "sender_jid")
    val sender: String,

    @ColumnInfo(name = "nick_name")
    val nickName: String? = "",

    @ColumnInfo(name = "type")
    val type: Type = Type.normal,

    @ColumnInfo(name = "message")
    val message: String? = "",

    @ColumnInfo(name = "time")
    val time: DateTime,

    @ColumnInfo(name = "isShow")
    val isShow: Boolean
)