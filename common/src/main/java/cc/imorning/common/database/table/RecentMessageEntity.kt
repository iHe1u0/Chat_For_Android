package cc.imorning.common.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.common.constant.DatabaseConstant
import org.joda.time.DateTime

@Entity(tableName = DatabaseConstant.TABLE_RECENT_MESSAGE)
data class RecentMessageEntity(

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