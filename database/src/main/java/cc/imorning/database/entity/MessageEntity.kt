package cc.imorning.database.entity

import com.google.gson.annotations.SerializedName
import org.jivesoftware.smack.packet.Message.Type
import org.joda.time.Instant

/**
 * This is an entity for message will be sent or received.
 * It should be decoded by Base64(Maybe)
 *
 * @param sender who sent the message
 * @param receiver the user who will receive the message
 * @param sendTime a joda DateTime object
 * @param messageBody the message body
 * @param isShow the message is delete by user?
 * @param isRecall the message is recall by sender?
 *
 */
data class MessageEntity(

    @SerializedName("sender")
    val sender: String,

    @SerializedName("receiver")
    val receiver: String,

    @SerializedName("message_type")
    val messageType: Type = Type.chat,

    @SerializedName("body")
    val messageBody: MessageBody,

    @SerializedName("send_time")
    val sendTime: Long = Instant.now().millis,

    @SerializedName("is_show")
    val isShow: Boolean = true,

    @SerializedName("is_recall")
    val isRecall: Boolean = false
) {

    override fun toString(): String {
        return "[$sender]>[$receiver]:${messageBody.text}"
    }
}

/**
 * This data class will include message body
 *
 * @param text message's text, for most time,will not be encode or decode
 * @param image you should convert a picture to String with base64
 * @param audio process with encode by base64
 * @param video process with encode by base64
 * @param action process with encode by base64
 *
 */
data class MessageBody(

    @SerializedName("text")
    val text: String = "",

    @SerializedName("pic")
    val image: String? = "",

    @SerializedName("audio")
    val audio: String? = "",

    @SerializedName("video")
    val video: String? = "",

    @SerializedName("file")
    val file: String? = "",

    @SerializedName("action")
    val action: String? = ""
)
