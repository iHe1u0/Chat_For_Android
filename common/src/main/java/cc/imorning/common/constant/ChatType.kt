package cc.imorning.common.constant

import java.util.*

class ChatType {

    enum class Type {
        Single, Group, Unknown;
    }

    companion object {
        fun from(type: String): Type {
            return when (type.lowercase(Locale.ROOT)) {
                "single" -> {
                    Type.Single
                }
                "group" -> {
                    Type.Group
                }
                else -> {
                    Type.Unknown
                }
            }
        }
    }
}