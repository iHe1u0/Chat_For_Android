package cc.imorning.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper
import org.joda.time.DateTime

@Entity(tableName = DatabaseHelper.TABLE_RECENT_MESSAGE)
data class RecentMessageTable(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "nick_name")
    val nickName: String? = "",

    @ColumnInfo(name = "sender_jid")
    val sender: String,

    @ColumnInfo(name = "receiver_jid")
    val receiver: String,

    @ColumnInfo(name = "last_message")
    val lastMessage: String? = "",

    @ColumnInfo(name = "last_message_time")
    val lastMessageTime: DateTime,

    )