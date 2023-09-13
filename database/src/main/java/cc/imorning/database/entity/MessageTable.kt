package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper
import org.jivesoftware.smack.packet.Message
import org.joda.time.Instant

@Entity(tableName = DatabaseHelper.TABLE_MESSAGE)
data class MessageTable(

    @PrimaryKey
    @ColumnInfo(name = "send_time")
    val send_time: Long = Instant.now().millis,

    @ColumnInfo(name = "sender")
    val sender: String,

    @ColumnInfo(name = "receiver")
    val receiver: String,

    @ColumnInfo(name = "message_type")
    val messageType: Message.Type = Message.Type.normal,

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
