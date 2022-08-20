package cc.imorning.chat.model

import org.joda.time.DateTime

data class OnlineMessage(
    val from: String,
    val receiver: String,
    val message: String? = "",
    val time: DateTime = DateTime.now(),
)