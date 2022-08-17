package cc.imorning.chat.model

data class Contact(
    val jid: String,
    val avatarPath: String? = null,
    val nickName: String,
    val status: Int? = 0
)