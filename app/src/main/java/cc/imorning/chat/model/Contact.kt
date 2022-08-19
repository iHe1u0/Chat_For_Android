package cc.imorning.chat.model

data class Contact(
    val jid: String,
    val nickName: String,
    val status: Int? = 0
)