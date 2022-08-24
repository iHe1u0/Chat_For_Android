package cc.imorning.common.entity

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * This is an entity for message will be sent or received.
 * It should be decoded by Base64
 *
 * @param sender who sent the message
 * @param receiver the user who will receive the message
 * @param sendTime a joda DateTime object
 * @param content the message body
 *
 */
data class MessageEntity(

    @SerializedName("sender")
    val sender: String,

    @SerializedName("receiver")
    val receiver: String,

    @SerializedName("content")
    val content: MessageContent,

    @SerializedName("sendTime")
    val sendTime: String = DateTime.now().toString(),
)
/**
 * This data class will include message body
 *
 * @param text message's text, for most time,will not be encode or decode
 * @param pic you should convert a picture to String with base64
 * @param audio process with encode by base64
 * @param video process with encode by base64
 * @param action process with encode by base64
 *
 */
data class MessageContent(

    @SerializedName("text")
    val text: String = "",

    @SerializedName("pic")
    val pic: String? = "",

    @SerializedName("audio")
    val audio: String? = "",

    @SerializedName("video")
    val video: String? = "",

    @SerializedName("file")
    val file: String? = "",

    @SerializedName("action")
    val action: String? = ""
)
