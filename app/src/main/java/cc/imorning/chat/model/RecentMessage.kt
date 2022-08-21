package cc.imorning.chat.model

import org.joda.time.DateTime

data class RecentMessage(
    val nickName: String,
    val sender: String,
    val message: String,
    val time: DateTime
)