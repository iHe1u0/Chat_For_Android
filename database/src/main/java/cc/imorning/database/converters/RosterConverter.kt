package cc.imorning.database.converters

import androidx.room.TypeConverter

class RosterConverter {
    @TypeConverter
    fun rosterTypeToString(type: String): org.jivesoftware.smack.packet.Message.Type {
        return org.jivesoftware.smack.packet.Message.Type.fromString(type)
    }

    @TypeConverter
    fun rosterStringToType(type: org.jivesoftware.smack.packet.Message.Type): String {
        return type.name
    }

    @TypeConverter
    fun rosterStatusToString(type: String): org.jivesoftware.smack.packet.Presence.Type {
        return org.jivesoftware.smack.packet.Presence.Type.fromString(type)
    }

    @TypeConverter
    fun rosterStringToStatus(type: org.jivesoftware.smack.packet.Presence.Type): String {
        return type.name
    }

}