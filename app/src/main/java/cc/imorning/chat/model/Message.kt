package cc.imorning.chat.model

import org.joda.time.field.OffsetDateTimeField

data class Message(
    val from: String,
    val receiver: String,
    val message: String? = "",
    val time: OffsetDateTimeField,
)