package cc.imorning.database.converters

import androidx.room.TypeConverter
import org.jivesoftware.smack.packet.Message.Type

class RosterTypeConverter {

    @TypeConverter
    fun revertObject(type: String): Type {
        return Type.fromString(type)
    }

    @TypeConverter
    fun converterObject(type: Type): String {
        return type.name
    }

}