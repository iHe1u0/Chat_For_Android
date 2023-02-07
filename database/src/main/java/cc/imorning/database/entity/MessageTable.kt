package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message

@Entity(tableName = DatabaseHelper.TABLE_MESSAGE)
data class MessageTable(

    @ColumnInfo(name = "sender")
    val sender: String,

    @ColumnInfo(name = "receiver")
    val receiver: String,

    @ColumnInfo(name = "message_type")
    val message_type: Message.Type = Message.Type.normal,

    @ColumnInfo(name = "send_time")
    val send_time: String,

    @ColumnInfo(name = "is_show")
    val is_show: Boolean = true,

    @ColumnInfo(name = "is_recall")
    val is_recall: Boolean,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "pic")
    val image: String? = "",

    @ColumnInfo(name = "audio")
    val audio: String? = "",

    @ColumnInfo(name = "video")
    val video: String? = "",

    @ColumnInfo(name = "file")
    val file: String? = "",

    @ColumnInfo(name = "action")
    val action: String? = ""

)
