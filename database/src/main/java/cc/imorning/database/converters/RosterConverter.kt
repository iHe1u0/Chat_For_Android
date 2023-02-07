package cc.imorning.database.converters

import androidx.room.TypeConverter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence.Mode

class RosterConverter {
    @TypeConverter
    fun rosterTypeToString(type: String): Message.Type {
        return Message.Type.fromString(type)
    }

    @TypeConverter
    fun rosterStringToType(type: Message.Type): String {
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

    @TypeConverter
    fun rosterModeToString(modeString: String): Mode {
        return Mode.fromString(modeString)
    }

    @TypeConverter
    fun rosterModeToStatus(mode: Mode): String {
        return mode.name
    }

}